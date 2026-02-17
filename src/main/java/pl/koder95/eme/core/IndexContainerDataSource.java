package pl.koder95.eme.core;

import pl.koder95.eme.Visitor;
import pl.koder95.eme.core.spi.DataSource;
import pl.koder95.eme.domain.index.ActNumber;
import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Źródło danych, które pobiera informacje z kolekcji indeksów.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.1, 2021-11-07
 * @since 0.4.0
 */
public class IndexContainerDataSource implements DataSource, Visitor<Index> {

    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(IndexContainerDataSource.class.getName());

    private final Map<String, Map<String, Set<ActNumber>>> baptisms = new TreeMap<>();
    private final Map<String, Map<String, Set<ActNumber>>> confirmations = new TreeMap<>();
    private final Map<String, Map<String, Set<ActNumber>>> marriages = new TreeMap<>();
    private final Map<String, Map<String, Set<ActNumber>>> deceases = new TreeMap<>();
    private final Map<String, Set<String>> personalData = new TreeMap<>();

    /**
     * Tworzy źródło danych na podstawie kolekcji indeksów.
     * @param indexes indeksy do analizy
     */
    public IndexContainerDataSource(Collection<Index> indexes) {
        if (indexes != null) indexes.forEach(this::visit);
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
        return personalData.entrySet().stream()
                .map(entry ->
                        Map.entry(entry.getKey(), Collections.unmodifiableSet(entry.getValue()))
                ).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static ActNumber[] get(Map<String, Map<String, Set<ActNumber>>> map, String surname, String name) {
        if (surname == null || name == null) {
            return new ActNumber[0];
        }
        Map<String, Set<ActNumber>> namesBySurname = map.get(surname);
        if (namesBySurname == null) {
            return new ActNumber[0];
        }
        Set<ActNumber> numbers = namesBySurname.get(name);
        if (numbers == null) {
            return new ActNumber[0];
        }
        return numbers.toArray(new ActNumber[0]);
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
        if (i == null) {
            return;
        }
        Book owner = i.getOwner();
        if (owner == null) {
            return;
        }
        String bookName = owner.getName();
        if (bookName.equalsIgnoreCase(BookType.LIBER_BAPTISMORUM.getBookName())) {
            setBaptism(i);
        } else if (bookName.equalsIgnoreCase(BookType.LIBER_CONFIRMATORUM.getBookName())) {
            setConfirmation(i);
        } else if (bookName.equalsIgnoreCase(BookType.LIBER_MATRIMONIORUM.getBookName())) {
            setMarriage(i);
        } else if (bookName.equalsIgnoreCase(BookType.LIBER_DEFUNCTORUM.getBookName())) {
            setDecease(i);
        } else {
            LOGGER.warning(() -> "Unrecognized book name, index skipped: " + bookName);
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
