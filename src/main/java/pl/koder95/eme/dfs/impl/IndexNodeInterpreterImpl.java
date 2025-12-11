package pl.koder95.eme.dfs.impl;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import pl.koder95.eme.dfs.IndexNodeInterpreter;

public class IndexNodeInterpreterImpl implements IndexNodeInterpreter {
    @Override
    public Map<String, String> interpret(Node node) {
        if (node == null || node.getNodeName().equalsIgnoreCase("index")) {
            throw new IllegalArgumentException("Node is null or node is not an index");
        }
        Map<String, String> data = new HashMap<>();
        NamedNodeMap attrs = node.getAttributes(); // pobiera listę atrybutów
        while (attrs.getLength() > 0) {
            Node attr = attrs.item(0); // pobiera pierwszy atrybut
            String key = attr.getNodeName(); // - nazwa atrybutu
            String value = attr.getTextContent(); // - wartość atrybutu
            data.put(key, value); // dodanie nazwy i wartości atrybutu do danych
            attrs.removeNamedItem(key); // usuwa odczytany atrybut
        }
        return data;
    }
}
