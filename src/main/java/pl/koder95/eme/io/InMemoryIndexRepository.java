package pl.koder95.eme.io;

import pl.koder95.eme.Files;
import pl.koder95.eme.MemoryUtils;
import pl.koder95.eme.core.spi.IndexFilter;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.index.BookType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Repozytorium indeksów utrzymujące cache w pamięci i odświeżanie z XML.
 */
public class InMemoryIndexRepository implements IndexRepository {

    private final IndexLoader loader;
    private final Map<BookType, List<Index>> loaded = new EnumMap<>(BookType.class);

    public InMemoryIndexRepository() {
        this(new IndexLoader(new FileXmlIndexDataSource(Files.INDICES_XML), IndexFilter.acceptAll()));
    }

    public InMemoryIndexRepository(IndexLoader loader) {
        this.loader = loader;
        for (BookType type : BookType.values()) {
            loaded.put(type, new ArrayList<>());
        }
    }

    @Override
    public synchronized List<Index> getIndices(BookType type) {
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
                            book.getIndices().forEach(index -> index.getDataNames().stream()
                                    .filter(type.getFieldSchema()::contains)
                                    .forEachOrdered(type.getFieldSchema()::add));
                            MemoryUtils.memory();
                        });
                loaded.put(type, new ArrayList<>(selected));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            for (BookType type : BookType.values()) {
                loaded.put(type, new ArrayList<>());
            }
        }
    }

    private void ensureLoaded() {
        boolean empty = loaded.values().stream().allMatch(List::isEmpty);
        if (empty) {
            reloadAll();
        }
    }
}
