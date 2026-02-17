package pl.koder95.eme.io;

import pl.koder95.eme.Files;
import pl.koder95.eme.MemoryUtils;
import pl.koder95.eme.core.spi.IndexFilter;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Repozytorium indeksów utrzymujące cache w pamięci i odświeżanie z XML.
 */
public class InMemoryIndexRepository implements IndexRepository {

    private final IndexLoader loader;
    private final Map<BookType, List<Index>> loaded = new EnumMap<>(BookType.class);
    private volatile boolean loadedOnce;

    public InMemoryIndexRepository() {
        this(new IndexLoader(new FileXmlIndexDataSource(Files.INDICES_XML), IndexFilter.acceptAll()));
    }

    public InMemoryIndexRepository(IndexLoader loader) {
        this.loader = Objects.requireNonNull(loader, "loader must not be null");
        for (BookType type : BookType.values()) {
            loaded.put(type, new ArrayList<>());
        }
        this.loadedOnce = false;
    }

    @Override
    public synchronized List<Index> getIndices(BookType type) {
        Objects.requireNonNull(type, "type must not be null");
        ensureLoaded();
        return Collections.unmodifiableList(loaded.get(type));
    }

    @Override
    public synchronized void reloadAll() {
        MemoryUtils.memory();
        try {
            List<Book> books = loader.loadBooks();
            for (BookType type : BookType.values()) {
                List<Index> selected = new LinkedList<>();
                books.stream()
                        .filter(book -> book.getName().equalsIgnoreCase(type.getBookName()))
                        .forEach(book -> {
                            MemoryUtils.memory();
                            selected.addAll(book.getIndices());
                            MemoryUtils.memory();
                        });
                List<Index> existing = loaded.get(type);
                existing.clear();
                existing.addAll(selected);
            }
            loadedOnce = true;
        } catch (IOException ex) {
            for (BookType type : BookType.values()) {
                loaded.get(type).clear();
            }
            loadedOnce = true;
            throw new IllegalStateException("Failed to reload indices", ex);
        }
    }

    private void ensureLoaded() {
        if (!loadedOnce) {
            reloadAll();
        }
    }
}
