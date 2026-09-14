package pl.koder95.eme.io.csv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.index.UniqueActNumber;
import pl.koder95.eme.io.BookTemplateLoader;
import pl.koder95.eme.model.RepositoryException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvIndexRepositoryTest {

    @TempDir
    Path dataDir;

    private CsvIndexRepository repository;

    private static BookTemplateLoader realTemplates() {
        // templates.xml z korzenia repozytorium — realny układ kolumn
        return new BookTemplateLoader(Path.of("templates.xml"));
    }

    private void write(BookType type, String... lines) throws IOException {
        Files.writeString(dataDir.resolve(type.getBookName() + ".csv"),
                String.join("\r\n", lines) + "\r\n", StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp() throws IOException {
        write(BookType.LIBER_BAPTISMORUM,
                "Indruch;Romualda;1000;1998",
                "Ałkowski;Flora;999;1998");
        write(BookType.LIBER_MATRIMONIORUM,
                "Galantowski;Kazimierz;Kuszeman;Sara;1000;2005");
        repository = new CsvIndexRepository(dataDir, realTemplates());
    }

    @Test
    void loadsBooksFromCsvFiles() {
        assertEquals(2, repository.getIndices(BookType.LIBER_BAPTISMORUM).size());
        assertEquals(1, repository.getIndices(BookType.LIBER_MATRIMONIORUM).size());
        assertTrue(repository.getIndices(BookType.LIBER_DEFUNCTORUM).isEmpty(), "missing file means empty book");

        Index marriage = repository.getIndices(BookType.LIBER_MATRIMONIORUM).get(0);
        assertEquals("Kuszeman", marriage.getData("wife-surname"));
        assertEquals("1000/2005", marriage.getData("an"));
    }

    @Test
    void findsByUniqueActNumber() {
        UniqueActNumber uan = UniqueActNumber.parse("eme.uan:LIBER_BAPTISMORUM/1998/1000");
        assertEquals("Indruch", repository.findByActNumber(uan).orElseThrow().getData("surname"));
    }

    @Test
    void detectsActNumberConflicts() throws IOException {
        write(BookType.LIBER_DEFUNCTORUM,
                "Pieczman;Kira;7;2003",
                "Zizulewski;Eugenia;7;2003");
        repository.reloadAll();

        UniqueActNumber conflicted = UniqueActNumber.parse("eme.uan:LIBER_DEFUNCTORUM/2003/7");
        assertTrue(repository.findByActNumber(conflicted).isEmpty());
        assertEquals(1, repository.getActNumberRegistry().getConflicts().size());
    }

    @Test
    void addRejectsDuplicateActNumber() {
        assertThrows(RepositoryException.class, () -> repository.add(BookType.LIBER_BAPTISMORUM,
                Map.of("surname", "Duplikat", "name", "Test", "an", "1000/1998")));
    }

    @Test
    void addRemoveSaveRoundTrip() {
        repository.add(BookType.LIBER_BAPTISMORUM,
                Map.of("surname", "Nowak", "name", "Jan", "an", "5a/1999"));
        repository.remove(UniqueActNumber.parse("eme.uan:LIBER_BAPTISMORUM/1998/999"));
        repository.saveAll();

        List<String> lines;
        try {
            lines = Files.readAllLines(
                    dataDir.resolve(BookType.LIBER_BAPTISMORUM.getBookName() + ".csv"), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            fail(ex);
            return;
        }
        assertEquals(List.of("Indruch;Romualda;1000;1998", "Nowak;Jan;5a;1999"), lines);

        CsvIndexRepository reloaded = new CsvIndexRepository(dataDir, realTemplates());
        assertEquals(2, reloaded.getIndices(BookType.LIBER_BAPTISMORUM).size());
        assertEquals("Nowak", reloaded
                .findByActNumber(UniqueActNumber.parse("eme.uan:LIBER_BAPTISMORUM/1999/5a"))
                .orElseThrow().getData("surname"));
        // księgi bez danych też zostały zapisane (zarządzamy kompletem plików)
        assertTrue(Files.exists(dataDir.resolve(BookType.LIBER_DEFUNCTORUM.getBookName() + ".csv")));
    }

    @Test
    void replaceUpdatesActInPlace() {
        Index original = repository.findByActNumber(
                UniqueActNumber.parse("eme.uan:LIBER_BAPTISMORUM/1998/1000")).orElseThrow();
        Index updated = repository.replace(BookType.LIBER_BAPTISMORUM, original,
                Map.of("surname", "Indruchowa", "name", "Romualda", "an", "1000/1998"));
        assertEquals("Indruchowa", updated.getData("surname"));
        assertEquals("Indruchowa", repository.findByActNumber(
                UniqueActNumber.parse("eme.uan:LIBER_BAPTISMORUM/1998/1000")).orElseThrow().getData("surname"));
    }

    @Test
    void removeRequiresUnambiguousActNumber() {
        assertThrows(RepositoryException.class,
                () -> repository.remove(UniqueActNumber.parse("eme.uan:LIBER_BAPTISMORUM/1998/12345")));
    }
}
