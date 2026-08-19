package pl.koder95.eme.domain.index;

import lombok.Getter;
import lombok.extern.java.Log;
import org.w3c.dom.Node;
import pl.koder95.eme.Visited;
import pl.koder95.eme.io.IndexNodeInterpreter;
import pl.koder95.eme.io.IndexNodeInterpreterImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Pojedynczy rekord indeksu danych osobowych.
 */
@Log
public class Index implements Visited {

    private static final IndexNodeInterpreter NODE_INTERPRETER = new IndexNodeInterpreterImpl();

    private final Map<String, String> data = new HashMap<>();
    private volatile ActNumber an;
    private volatile UniqueActNumber uan;
    @Getter
    private final Book owner;

    private Index(Book owner, Map<String, String> data) {
        this.owner = owner;
        if (data != null) {
            data.forEach((key, value) -> {
                if (key != null && !key.isBlank()) {
                    this.data.put(key, value == null ? "" : value);
                }
            });
        }
    }

    public static Index create(Node index) {
        return create(null, index);
    }

    public static Index create(Book owner, Node index) {
        if (index == null) {
            return null;
        }
        if (!index.getNodeName().equalsIgnoreCase("index")) {
            return null;
        }
        Map<String, String> interpreted = index.hasAttributes()
                ? NODE_INTERPRETER.interpret(index)
                : Map.of();
        return create(owner, interpreted);
    }

    /**
     * Tworzy indeks z mapy pól.
     *
     * @param owner księga właściciela
     * @param data  mapa atrybutów; wymaga niepustego {@code an}
     * @return nowy indeks albo {@code null}
     */
    public static Index create(Book owner, Map<String, String> data) {
        if (data == null) {
            return null;
        }
        Index created = new Index(owner, data);
        if (created.getData("an").isBlank()) {
            String ownerName = owner == null ? "<null>" : owner.getName();
            log.warning(() -> "Pominięto indeks bez atrybutu 'an'. owner=" + ownerName);
            return null;
        }
        return created;
    }

    public String getData(String name) {
        return data.getOrDefault(name, "");
    }

    public Set<String> getDataNames() {
        return Collections.unmodifiableSet(data.keySet());
    }

    /**
     * @return wszystkie atrybuty indeksu w formie niemodyfikowalnej mapy
     */
    public Map<String, String> getAllData() {
        return Collections.unmodifiableMap(new HashMap<>(data));
    }

    public ActNumber getActNumber() {
        if (an == null) {
            an = ActNumber.parseActNumber(getData("an"));
        }
        return an;
    }

    /**
     * Unikalny numer aktu (typ księgi + rok + sygnatura).
     */
    public UniqueActNumber getUniqueActNumber() {
        if (uan == null) {
            String bookName = owner == null ? null : owner.getName();
            UniqueActNumber converted = UniqueActNumber.from(bookName, getActNumber());
            uan = converted == null ? UniqueActNumber.UNKNOWN : converted;
        }
        return uan;
    }

    @Override
    public String toString() {
        return getActNumber() + " " + data;
    }
}
