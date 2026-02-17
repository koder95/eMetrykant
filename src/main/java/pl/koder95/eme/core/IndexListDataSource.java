package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.DataSource;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.domain.index.ActNumber;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.io.InMemoryIndexRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Źródło danych budowane z repozytorium indeksów.
 */
public class IndexListDataSource implements DataSource {
    private final IndexContainerDataSource baptisms;
    private final IndexContainerDataSource confirmations;
    private final IndexContainerDataSource marriages;
    private final IndexContainerDataSource deceases;
    private final Map<String, Set<String>> personalData;

    public IndexListDataSource() {
        this(new InMemoryIndexRepository());
    }

    public IndexListDataSource(IndexRepository indexRepository) {
        baptisms = new IndexContainerDataSource(indexRepository.getIndices(BookType.LIBER_BAPTISMORUM));
        confirmations = new IndexContainerDataSource(indexRepository.getIndices(BookType.LIBER_CONFIRMATORUM));
        marriages = new IndexContainerDataSource(indexRepository.getIndices(BookType.LIBER_MATRIMONIORUM));
        deceases = new IndexContainerDataSource(indexRepository.getIndices(BookType.LIBER_DEFUNCTORUM));

        Map<String, Set<String>> merged = new HashMap<>();
        putAll(merged, baptisms.getPersonalData());
        putAll(merged, confirmations.getPersonalData());
        putAll(merged, marriages.getPersonalData());
        putAll(merged, deceases.getPersonalData());
        this.personalData = Collections.unmodifiableMap(merged);
    }

    @Override
    public ActNumber[] getBaptism(String surname, String name) {
        return baptisms.getBaptism(surname, name);
    }

    @Override
    public ActNumber[] getConfirmation(String surname, String name) {
        return confirmations.getConfirmation(surname, name);
    }

    @Override
    public ActNumber[] getMarriage(String surname, String name) {
        return marriages.getMarriage(surname, name);
    }

    @Override
    public ActNumber[] getDecease(String surname, String name) {
        return deceases.getDecease(surname, name);
    }

    @Override
    public Map<String, Set<String>> getPersonalData() {
        return personalData;
    }

    private static void putAll(Map<String, Set<String>> merged, Map<String, Set<String>> source) {
        source.forEach((surname, names) -> merged.computeIfAbsent(surname, ignored -> new HashSet<>()).addAll(names));
    }
}
