package pl.koder95.eme.factory.impl;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import pl.koder95.eme.dfs.Book;
import pl.koder95.eme.dfs.BookType;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.dfs.IndexNodeInterpreter;
import pl.koder95.eme.factory.IndexFactory;

public class BasicIndexFactory implements IndexFactory {

    private final Function<Set<String>, BookType> bookTypeMatcher;
    private final Map<BookType, Book> bookMap;

    public BasicIndexFactory(Function<Set<String>, BookType> bookTypeMatcher, Map<BookType, Book> bookMap) {
        this.bookTypeMatcher = bookTypeMatcher;
        this.bookMap = bookMap;
    }

    @Override
    public Index create(Map<String, String> data) {
        if (data == null) return null;
        BookType bookType = bookTypeMatcher.apply(data.keySet());
        if (bookType == null) return null;
        return Index.create(bookMap.get(bookType), data);
    }
}
