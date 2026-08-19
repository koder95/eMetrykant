package pl.koder95.eme.domain.index;

import org.w3c.dom.Node;
import pl.koder95.eme.Visited;
import pl.koder95.eme.io.IndexNodeInterpreter;
import pl.koder95.eme.io.IndexNodeInterpreterImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Pojedynczy rekord indeksu danych osobowych.
 */
public class Index implements Visited {

    private static final Logger LOGGER = Logger.getLogger(Index.class.getName());
    private static final IndexNodeInterpreter NODE_INTERPRETER = new IndexNodeInterpreterImpl();

    private final Map<String, String> data;
    private volatile ActNumber an;
    private final Book owner;

    private Index(Book owner, Map<String, String> data) {
        this.owner = owner;
        this.data = new HashMap<>(data == null ? Map.of() : data);
    }

    private Index(Book owner, Map<String, String> data) {
        this.owner = owner;
        data.forEach((key, value) -> {
            if (key != null && !key.isBlank()) {
                this.data.put(key, value == null ? "" : value);
            }
        });
    }

    public static Index create(Node index) {
        return create(null, index);
    }

    /**
     * Tworzy indeks na podstawie mapy atrybutów, np. danych wprowadzonych przez użytkownika.
     *
     * @param owner księga, do której należy indeks
     * @param data atrybuty indeksu, wymagany jest niepusty atrybut {@code an}
     * @return nowy indeks, albo {@code null} gdy brakuje numeru aktu
     */
    public static Index create(Book owner, Map<String, String> data) {
        if (data == null) {
            return null;
        }
        Index i = new Index(owner, data);
        if (i.getData("an").isBlank()) {
            String ownerName = owner == null ? "<null>" : owner.getName();
            LOGGER.warning(() -> "Pominięto indeks bez atrybutu 'an'. owner=" + ownerName);
            return null;
        }
        return i;
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
        String anValue = data.get("an");
        if (anValue == null || anValue.isEmpty()) {
            String ownerName = owner == null ? "<null>" : owner.getName();
            LOGGER.warning(() -> "Pominięto indeks bez atrybutu 'an'. owner=" + ownerName);
            return null;
        }
        return new Index(owner, data);
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

    public Book getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return getActNumber() + " " + data;
    }
}
