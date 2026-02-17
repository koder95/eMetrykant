package pl.koder95.eme.domain.index;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import pl.koder95.eme.Visited;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Pojedynczy rekord indeksu danych osobowych.
 */
public class Index implements Visited {

    private final Map<String, String> data = new HashMap<>();
    private ActNumber an;
    private final Book owner;

    private Index(Book owner, Node index) {
        this.owner = owner;
        NamedNodeMap attrs = index.getAttributes();
        while (attrs.getLength() > 0) {
            Node attr = attrs.item(0);
            String key = attr.getNodeName();
            String value = attr.getTextContent();
            data.put(key, value);
            attrs.removeNamedItem(key);
        }
    }

    private Index(Book owner) {
        this.owner = owner;
    }

    public static Index create(Node index) {
        return create(null, index);
    }

    public static Index create(Book owner, Node index) {
        if (index == null) {
            return new Index(owner);
        }
        if (!index.getNodeName().equalsIgnoreCase("index")) {
            return null;
        }
        Index i = index.hasAttributes() ? new Index(owner, index) : new Index(owner);
        return i.getDataNames().contains("an") && !i.getData("an").isEmpty() ? i : null;
    }

    public String getData(String name) {
        return data.getOrDefault(name, "");
    }

    public Set<String> getDataNames() {
        return data.keySet();
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
