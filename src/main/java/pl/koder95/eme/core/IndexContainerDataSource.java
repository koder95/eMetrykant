package pl.koder95.eme.core;

import pl.koder95.eme.Visitor;
import pl.koder95.eme.core.spi.DataSource;
import pl.koder95.eme.dfs.*;

import java.util.*;

/**
 * Źródło danych, które pobiera informacje z {@link IndexContainer kontenera indeksów}.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.1, 2021-11-07
 * @since 0.4.0
 */
public class IndexContainerDataSource implements DataSource, Visitor<Index> {

    private final Map<String, Map<String, Set<ActNumber>>> baptisms = new TreeMap<>();
    private final Map<String, Map<String, Set<ActNumber>>> confirmations = new TreeMap<>();
    private final Map<String, Map<String, Set<ActNumber>>> marriages = new TreeMap<>();
    private final Map<String, Map<String, Set<ActNumber>>> deceases = new TreeMap<>();
    private final Map<String, Set<String>> personalData = new TreeMap<>();

    /**
     * Tworzy źródło danych na podstawie wczytanych indeksów do {@link IndexContainer kontenera}.
     * @param container kontener, którego indeksy zostaną przejrzane, aby pozyskać informacje
     */
    public IndexContainerDataSource(IndexContainer container) {
        if (container != null) container.getLoaded().forEach(this::visit);
    }

    @Override
    public ActNumber[] getBaptism(String surname, String name) {
        return get(baptisms, surname, name);
    }

    @Override
    public ActNumber[] getConfirmation(String surname, String name) {
        return get(confirmations, surname, name);
    }

    @Override
    public ActNumber[] getMarriage(String surname, String name) {
        return get(marriages, surname, name);
    }

    @Override
    public ActNumber[] getDecease(String surname, String name) {
        return get(deceases, surname, name);
    }

    @Override
    public Map<String, Set<String>> getPersonalData() {
        return personalData;
    }

    private static ActNumber[] get(Map<String, Map<String, Set<ActNumber>>> map, String surname, String name) {
        for (Map.Entry<String, Map<String, Set<ActNumber>>> entry : map.entrySet()) {
            if (entry.getKey().equals(surname)) {
                for (Map.Entry<String, Set<ActNumber>> e : entry.getValue().entrySet()) {
                    if (e.getKey().equals(name)) {
                        if (e.getValue() == null) return new ActNumber[0];
                        return e.getValue().toArray(new ActNumber[0]);
                    }
                }
            }
        }
        return null;
    }

    private void set(Map<String, Map<String, Set<ActNumber>>> map, String surname, String name,
                            ActNumber number) {
        surname = removeUTF8BOM(surname.trim());
        name = removeUTF8BOM(name.trim());
        if (!map.containsKey(surname)) {
            map.put(surname, new TreeMap<>());
        }
        Map<String, Set<ActNumber>> namesMap = map.get(surname);
        if (!namesMap.containsKey(name)) {
            namesMap.put(name, new LinkedHashSet<>());
        }
        Set<ActNumber> numbers = namesMap.get(name);
        numbers.add(number);

        if (!personalData.containsKey(surname)) {
            personalData.put(surname, new TreeSet<>());
        }
        Set<String> names = personalData.get(surname);
        names.add(name);
    }

    private void setBaptism(Index i) {
        set(baptisms, i.getData("surname"), i.getData("name"), i.getActNumber());
    }

    private void setConfirmation(Index i) {
        set(confirmations, i.getData("surname"), i.getData("name"), i.getActNumber());
    }

    private void setMarriage(Index i) {
        set(marriages, i.getData("husband-surname"), i.getData("husband-name"), i.getActNumber());
        set(marriages, i.getData("wife-surname"), i.getData("wife-name"), i.getActNumber());
    }

    private void setDecease(Index i) {
        set(deceases, i.getData("surname"), i.getData("name"), i.getActNumber());
    }

    @Override
    public void visit(Index i) {
        String bookName = i.getOwner().getName();
        if (bookName.equalsIgnoreCase("Księga ochrzczonych")) {
            setBaptism(i);
        } else if (bookName.equalsIgnoreCase("Księga bierzmowanych")) {
            setConfirmation(i);
        } else if (bookName.equalsIgnoreCase("Księga zaślubionych")) {
            setMarriage(i);
        } else if (bookName.equalsIgnoreCase("Księga zmarłych")) {
            setDecease(i);
        }
    }

    private static final String UTF8_BOM = "\uFEFF";

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
}
