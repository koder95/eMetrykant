package pl.koder95.eme.io;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Default interpreter: reads attribute name/value pairs without mutating the node.
 */
public class IndexNodeInterpreterImpl implements IndexNodeInterpreter {

    @Override
    public Map<String, String> interpret(Node node) {
        if (node == null || !node.getNodeName().equalsIgnoreCase("index")) {
            throw new IllegalArgumentException("Node is null or node is not an index");
        }
        Map<String, String> data = new HashMap<>();
        NamedNodeMap attrs = node.getAttributes();
        if (attrs == null) {
            return data;
        }
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            if (attr != null) {
                data.put(attr.getNodeName(), attr.getTextContent());
            }
        }
        return data;
    }
}
