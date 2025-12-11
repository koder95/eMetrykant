package pl.koder95.eme.dfs;

import java.util.Map;
import org.w3c.dom.Node;

public interface IndexNodeInterpreter {
    Map<String, String> interpret(Node node);
}
