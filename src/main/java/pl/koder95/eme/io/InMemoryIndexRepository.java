package pl.koder95.eme.io;

import lombok.extern.java.Log;
import pl.koder95.eme.Files;
import pl.koder95.eme.MemoryUtils;
import pl.koder95.eme.core.spi.IndexFilter;
import pl.koder95.eme.core.spi.MutableIndexRepository;
import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.index.UniqueActNumber;
import pl.koder95.eme.domain.index.UniqueActNumberRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Repozytorium indeksów utrzymujące cache w pamięci i odświeżanie z XML.
 */
@Log
public class InMemoryIndexRepository implements MutableIndexRepository {

    private final IndexLoader loader;
    private final IndexWriter writer;
    private final Map<BookType, List<Index>> loaded = new EnumMap<>(BookType.class);
    private final Map<BookType, Book> booksForNewIndices = new EnumMap<>(BookType.class);
    private volatile UniqueActNumberRegistry actNumberRegistry = new UniqueActNumberRegistry();
    private volatile boolean loadedOnce;

    public InMemoryIndexRepository() {
        this(
                new IndexLoader(new FileXmlIndexDataSource(Files.INDICES_XML), IndexFilter.acceptAll()),
                new IndexWriter(new FileXmlIndexDataTarget(Files.INDICES_XML))
        );
    }

    public InMemoryIndexRepository(IndexLoader loader) {
        this(loader, new IndexWriter(new FileXmlIndexDataTarget(Files.INDICES_XML)));
    }

    public InMemoryIndexRepository(IndexLoader loader, IndexWriter writer) {
        this.loader = Objects.requireNonNull(loader, "loader must not be null");
        this.writer = Objects.requireNonNull(writer, "writer must not be null");
        for (BookType type : BookType.values()) {
            loaded.put(type, new ArrayList<>());
        }
        this.loadedOnce = false;
    }

    @Override
    public synchronized List<Index> getIndices(BookType type) {
        Objects.requireNonNull(type, "type must not be null");
        ensureLoaded();
        return Collections.unmodifiableList(loaded.computeIfAbsent(type, ignored -> new ArrayList<>()));
    }

    @Override
    public synchronized Optional<Index> findByActNumber(UniqueActNumber uan) {
        ensureLoaded();
        return actNumberRegistry.find(uan);
    }

    /**
     * @return rejestr unikalnych numerów aktów zbudowany przy ostatnim przeładowaniu
     */
    public synchronized UniqueActNumberRegistry getActNumberRegistry() {
        ensureLoaded();
        return actNumberRegistry;
    }

    @Override
    public synchronized void reloadAll() {
        MemoryUtils.memory();
        try {
            List<Book> books = loader.loadBooks();
            for (BookType type : BookType.values()) {
                List<Index> selected = new ArrayList<>();
                books.stream()
                        .filter(book -> book.getName().equalsIgnoreCase(type.getBookName()))
                        .forEach(book -> {
                            MemoryUtils.memory();
                            selected.addAll(book.getIndices());
                            MemoryUtils.memory();
                        });
                List<Index> existing = loaded.computeIfAbsent(type, ignored -> new ArrayList<>());
                existing.clear();
                existing.addAll(selected);
            }
            booksForNewIndices.clear();
            UniqueActNumberRegistry registry = new UniqueActNumberRegistry();
            loaded.values().forEach(indices -> indices.forEach(registry::register));
            if (!registry.getConflicts().isEmpty()) {
                registry.getConflicts().forEach((uan, indices) ->
                        log.warning(() -> "Konflikt unikalnego numeru aktu " + uan
                                + " – liczba indeksów: " + indices.size()));
            }
            actNumberRegistry = registry;
            loadedOnce = true;
        } catch (IOException ex) {
            for (BookType type : BookType.values()) {
                loaded.computeIfAbsent(type, ignored -> new ArrayList<>()).clear();
            }
            loadedOnce = false;
            log.log(Level.SEVERE, "Failed to reload indices", ex);
            throw new IllegalStateException("Failed to reload indices", ex);
        }
    }

    @Override
    public synchronized Index add(BookType type, Map<String, String> data) {
        Objects.requireNonNull(type, "type must not be null");
        ensureLoaded();
        Index created = Index.create(bookFor(type), data);
        if (created == null) {
            throw new IllegalArgumentException("Nie można utworzyć indeksu z podanych danych: " + data);
        }
        Book owner = created.getOwner();
        if (owner != null) {
            owner.addIndex(created);
        }
        loaded.computeIfAbsent(type, ignored -> new ArrayList<>()).add(created);
        actNumberRegistry.register(created);
        return created;
    }

    @Override
    public synchronized Index replace(BookType type, Index index, Map<String, String> data) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(index, "index must not be null");
        ensureLoaded();
        List<Index> indices = loaded.computeIfAbsent(type, ignored -> new ArrayList<>());
        int position = indexOf(indices, index);
        if (position < 0) {
            throw new IllegalStateException("Indeks nie należy do księgi: " + type.getBookName());
        }
        Book owner = index.getOwner() == null ? bookFor(type) : index.getOwner();
        Index created = Index.create(owner, data);
        if (created == null) {
            throw new IllegalArgumentException("Nie można utworzyć indeksu z podanych danych: " + data);
        }
        owner.removeIndex(index);
        owner.addIndex(created);
        indices.set(position, created);
        rebuildActNumberRegistry();
        return created;
    }

    @Override
    public synchronized boolean remove(BookType type, Index index) {
        Objects.requireNonNull(type, "type must not be null");
        if (index == null) {
            return false;
        }
        ensureLoaded();
        List<Index> indices = loaded.computeIfAbsent(type, ignored -> new ArrayList<>());
        int position = indexOf(indices, index);
        if (position < 0) {
            return false;
        }
        indices.remove(position);
        Book owner = index.getOwner();
        if (owner != null) {
            owner.removeIndex(index);
        }
        rebuildActNumberRegistry();
        return true;
    }

    @Override
    public synchronized void saveAll() {
        ensureLoaded();
        try {
            writer.saveBooks(loaded);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to save indices", ex);
            throw new IllegalStateException("Failed to save indices", ex);
        }
    }

    private Book bookFor(BookType type) {
        return booksForNewIndices.computeIfAbsent(type, key -> new Book(key.getBookName()));
    }

    private void rebuildActNumberRegistry() {
        UniqueActNumberRegistry registry = new UniqueActNumberRegistry();
        loaded.values().forEach(indices -> indices.forEach(registry::register));
        actNumberRegistry = registry;
    }

    /**
     * Indeksy nie mają tożsamości opartej o wartość, dlatego poszukiwane są po referencji.
     */
    private static int indexOf(List<Index> indices, Index index) {
        for (int i = 0; i < indices.size(); i++) {
            if (indices.get(i) == index) {
                return i;
            }
        }
        return -1;
    }

    private void ensureLoaded() {
        if (!loadedOnce) {
            reloadAll();
        }
    }
}
