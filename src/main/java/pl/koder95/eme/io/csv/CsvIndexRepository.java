package pl.koder95.eme.io.csv;

import lombok.NonNull;
import lombok.extern.java.Log;
import pl.koder95.eme.Files;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookTemplate;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.index.UniqueActNumber;
import pl.koder95.eme.domain.index.UniqueActNumberRegistry;
import pl.koder95.eme.io.BookTemplateLoader;
import pl.koder95.eme.model.RepositoryException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Repozytorium indeksów zarządzające danymi w plikach CSV
 * (jeden plik na księgę: {@code <nazwa księgi>.csv}), których układ kolumn
 * wyznacza {@code templates.xml}.
 *
 * <p>Odczyt buduje {@link UniqueActNumberRegistry rejestr unikalnych numerów};
 * modyfikacje ({@link #add(BookType, Map)}, {@link #remove(UniqueActNumber)})
 * działają w pamięci do czasu jawnego {@link #saveAll() zapisu}, który jest
 * atomowy per plik (UTF-8 bez BOM, CRLF).</p>
 */
@Log
public class CsvIndexRepository implements IndexRepository {

    private static final String FILE_EXTENSION = ".csv";
    private static final String LINE_SEPARATOR = "\r\n";

    private final Path dataDir;
    private final BookTemplateLoader templateLoader;
    private final CsvIndexCodec codec = new CsvIndexCodec();
    private final Map<BookType, List<Index>> loaded = new EnumMap<>(BookType.class);
    private final Map<BookType, Book> books = new EnumMap<>(BookType.class);
    private Map<BookType, BookTemplate> templates = new EnumMap<>(BookType.class);
    private volatile UniqueActNumberRegistry actNumberRegistry = new UniqueActNumberRegistry();
    private volatile boolean loadedOnce;

    public CsvIndexRepository(@NonNull Path dataDir, @NonNull BookTemplateLoader templateLoader) {
        this.dataDir = dataDir;
        this.templateLoader = templateLoader;
        for (BookType type : BookType.values()) {
            loaded.put(type, new ArrayList<>());
            books.put(type, new Book(type.getBookName()));
        }
        this.loadedOnce = false;
    }

    public CsvIndexRepository() {
        this(Files.DATA_DIR, new BookTemplateLoader());
    }

    @Override
    public synchronized List<Index> getIndices(BookType type) {
        Objects.requireNonNull(type, "type must not be null");
        ensureLoaded();
        return Collections.unmodifiableList(loaded.get(type));
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
        try {
            templates = templateLoader.loadTemplateMap();
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to load book templates", ex);
            throw new IllegalStateException("Failed to load book templates", ex);
        }
        try {
            for (BookType type : BookType.values()) {
                List<Index> target = loaded.get(type);
                target.clear();
                BookTemplate template = templates.get(type);
                if (template == null) {
                    log.warning(() -> "Brak szablonu dla księgi: " + type.getBookName());
                    continue;
                }
                Path file = fileOf(type);
                if (!java.nio.file.Files.exists(file)) {
                    log.info(() -> "Brak pliku CSV, księga pusta: " + file);
                    continue;
                }
                Book book = books.get(type);
                for (String line : java.nio.file.Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    Index index = codec.decode(line, book, template);
                    if (index != null) {
                        target.add(index);
                    }
                }
            }
        } catch (IOException ex) {
            loaded.values().forEach(List::clear);
            loadedOnce = false;
            log.log(Level.SEVERE, "Failed to reload CSV indices", ex);
            throw new IllegalStateException("Failed to reload CSV indices", ex);
        }
        rebuildRegistry();
        loadedOnce = true;
    }

    /**
     * Dodaje nowy indeks do księgi (tylko w pamięci — patrz {@link #saveAll()}).
     *
     * @return utworzony indeks
     * @throws RepositoryException gdy dane są niepoprawne, brak szablonu księgi
     * albo numer aktu już istnieje
     */
    public synchronized Index add(BookType type, Map<String, String> data) {
        Objects.requireNonNull(type, "type must not be null");
        ensureLoaded();
        if (templates.get(type) == null) {
            throw new RepositoryException("No template for book: " + type.getBookName());
        }
        Index index = Index.create(books.get(type), data);
        if (index == null) {
            throw new RepositoryException("Invalid index data (missing 'an'?): " + data);
        }
        UniqueActNumber uan = index.getUniqueActNumber();
        if (!UniqueActNumber.UNKNOWN.equals(uan)
                && (actNumberRegistry.find(uan).isPresent()
                    || actNumberRegistry.getConflicts().containsKey(uan))) {
            throw new RepositoryException("Act number already exists: " + uan);
        }
        loaded.get(type).add(index);
        actNumberRegistry.register(index);
        return index;
    }

    /**
     * Usuwa akt jednoznacznie wskazany numerem (tylko w pamięci — patrz {@link #saveAll()}).
     *
     * @return usunięty indeks
     * @throws RepositoryException gdy numer nie wskazuje jednoznacznie aktu
     */
    public synchronized Index remove(UniqueActNumber uan) {
        ensureLoaded();
        Index index = actNumberRegistry.find(uan)
                .orElseThrow(() -> new RepositoryException(
                        "Act number does not identify a single index: " + uan));
        loaded.values().forEach(indices -> indices.remove(index));
        rebuildRegistry();
        return index;
    }

    /**
     * Zapisuje wszystkie księgi do plików CSV (atomowo, UTF-8 bez BOM, CRLF).
     *
     * @throws IOException problemy z zapisem
     */
    public synchronized void saveAll() throws IOException {
        ensureLoaded();
        for (BookType type : BookType.values()) {
            BookTemplate template = templates.get(type);
            if (template == null) {
                continue;
            }
            StringBuilder content = new StringBuilder();
            for (Index index : loaded.get(type)) {
                content.append(codec.encode(index, template)).append(LINE_SEPARATOR);
            }
            Path file = fileOf(type);
            Path temp = java.nio.file.Files.createTempFile(dataDir, file.getFileName().toString(), ".tmp");
            java.nio.file.Files.writeString(temp, content, StandardCharsets.UTF_8);
            try {
                java.nio.file.Files.move(temp, file,
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                java.nio.file.Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void rebuildRegistry() {
        UniqueActNumberRegistry registry = new UniqueActNumberRegistry();
        loaded.values().forEach(indices -> indices.forEach(registry::register));
        registry.getConflicts().forEach((uan, indices) ->
                log.warning(() -> "Konflikt unikalnego numeru aktu " + uan
                        + " – liczba indeksów: " + indices.size()));
        actNumberRegistry = registry;
    }

    private Path fileOf(BookType type) {
        return dataDir.resolve(type.getBookName() + FILE_EXTENSION);
    }

    private void ensureLoaded() {
        if (!loadedOnce) {
            reloadAll();
        }
    }
}
