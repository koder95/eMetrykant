package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.DataSource;
import pl.koder95.eme.dfs.ActNumber;
import pl.koder95.eme.dfs.IndexList;

import java.util.*;

/**
 * Źródło danych zawierające cztery pod-źródła, czyli instancje {@link IndexContainerDataSource źródeł},
 * które pobierają informacje z każdej instancji {@link IndexList listy indeksów}.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public class IndexListDataSource implements DataSource {
    private final IndexContainerDataSource baptisms;
    private final IndexContainerDataSource confirmations;
    private final IndexContainerDataSource marriages;
    private final IndexContainerDataSource deceases;
    private Map<String, Set<String>> personalData;

    /**
     * Tworzy nową instancję wczytując do niej dane z czterech {@link IndexList list indeksów}.
     */
    public IndexListDataSource() {
        baptisms = new IndexContainerDataSource(IndexList.LIBER_BAPTISMORUM);
        confirmations = new IndexContainerDataSource(IndexList.LIBER_CONFIRMATORUM);
        marriages = new IndexContainerDataSource(IndexList.LIBER_MATRIMONIORUM);
        deceases = new IndexContainerDataSource(IndexList.LIBER_DEFUNCTORUM);
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
        if (personalData == null) {
            Map<String, Set<String>> merge0 = merge(baptisms.getPersonalData(), confirmations.getPersonalData());
            Map<String, Set<String>> merge1 = merge(marriages.getPersonalData(), deceases.getPersonalData());
            Map<String, Set<String>> finalMerge = merge(merge0, merge1);
            merge0.clear();
            merge1.clear();
            personalData = finalMerge;
        }
        return personalData;
    }

    private static Map<String, Set<String>> merge(Map<String, Set<String>> pdata0,
                                                  Map<String, Set<String>> pdata1) {
        Map<String, Set<String>> merged = new TreeMap<>(pdata0);
        pdata1.forEach((surname, names) -> {
            if (!merged.containsKey(surname)) {
                merged.put(surname, new HashSet<>());
            }
            merged.get(surname).addAll(names);
        });
        return merged;
    }
}
