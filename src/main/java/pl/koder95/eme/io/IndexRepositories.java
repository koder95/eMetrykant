package pl.koder95.eme.io;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import pl.koder95.eme.Files;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.io.csv.CsvIndexRepository;

import java.nio.file.Path;

/**
 * Fabryka domyślnego repozytorium indeksów.
 */
@Log
@UtilityClass
public class IndexRepositories {

    /**
     * Wybiera źródło danych: gdy nie ma {@code indices.xml}, a w katalogu danych
     * są pliki CSV ksiąg, używa {@link CsvIndexRepository}; w przeciwnym razie
     * {@link InMemoryIndexRepository} (XML).
     */
    public IndexRepository createDefault() {
        if (!java.nio.file.Files.exists(Files.INDICES_XML) && anyBookCsvExists(Files.DATA_DIR)) {
            log.info(() -> "Brak indices.xml – używam danych CSV z: " + Files.DATA_DIR);
            return new CsvIndexRepository();
        }
        return new InMemoryIndexRepository();
    }

    private boolean anyBookCsvExists(Path dataDir) {
        for (BookType type : BookType.values()) {
            if (java.nio.file.Files.exists(dataDir.resolve(type.getBookName() + ".csv"))) {
                return true;
            }
        }
        return false;
    }
}
