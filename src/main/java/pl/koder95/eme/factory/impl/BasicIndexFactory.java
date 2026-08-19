package pl.koder95.eme.factory.impl;

import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.factory.IndexFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Creates an {@link Index} by matching field names to a {@link BookType} and owner {@link Book}.
 */
public class BasicIndexFactory implements IndexFactory {

    private final Function<Set<String>, BookType> bookTypeMatcher;
    private final Map<BookType, Book> bookMap;

    public BasicIndexFactory(Function<Set<String>, BookType> bookTypeMatcher, Map<BookType, Book> bookMap) {
        this.bookTypeMatcher = bookTypeMatcher;
        this.bookMap = bookMap;
    }

    @Override
    public Index create(Map<String, String> data) {
        if (data == null) {
            return null;
        }
        BookType bookType = bookTypeMatcher.apply(data.keySet());
        if (bookType == null) {
            return null;
        }
        return Index.create(bookMap.get(bookType), data);
    }
}
