package pl.koder95.eme.io;

import org.w3c.dom.Node;

import java.util.Map;

/**
 * Interprets an XML index node into a field map.
 */
public interface IndexNodeInterpreter {

    Map<String, String> interpret(Node node);
}
