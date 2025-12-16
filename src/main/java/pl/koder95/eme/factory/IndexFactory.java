package pl.koder95.eme.factory;

import pl.koder95.eme.dfs.BookType;
import pl.koder95.eme.dfs.Index;

import java.util.Map;

public interface IndexFactory {

    Index create(Map<String, String> data);
}
