package pl.koder95.eme.domain.index;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import pl.koder95.eme.Visited;

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

    private final Map<String, String> data = new HashMap<>();
    private volatile ActNumber an;
    private final Book owner;

    private Index(Book owner, Node index) {
        this.owner = owner;
        NamedNodeMap attrs = index.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            if (attr != null) {
                String key = attr.getNodeName();
                String value = attr.getTextContent();
                data.put(key, value);
            }
        }
    }

    private Index(Book owner) {
        this.owner = owner;
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
        Index i = index.hasAttributes() ? new Index(owner, index) : new Index(owner);
        if (i.getDataNames().contains("an") && !i.getData("an").isEmpty()) {
            return i;
        }

        String ownerName = owner == null ? "<null>" : owner.getName();
        LOGGER.warning(() -> "Pominięto indeks bez atrybutu 'an'. owner=" + ownerName
                + ", nodeName=" + index.getNodeName());
        return null;
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
