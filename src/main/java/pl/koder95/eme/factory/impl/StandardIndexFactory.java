package pl.koder95.eme.factory.impl;

import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookType;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Standard book-type inference from characteristic field names.
 */
public class StandardIndexFactory extends BasicIndexFactory {

    public StandardIndexFactory(BiPredicate<Set<String>, BookType> bookTypePattern) {
        super(strings -> Arrays.stream(BookType.values())
                        .filter(bookType -> bookTypePattern.test(strings, bookType))
                        .findFirst().orElseThrow(),
                Map.of(
                        BookType.LIBER_BAPTISMORUM, new Book(BookType.LIBER_BAPTISMORUM.getBookName()),
                        BookType.LIBER_CONFIRMATORUM, new Book(BookType.LIBER_CONFIRMATORUM.getBookName()),
                        BookType.LIBER_MATRIMONIORUM, new Book(BookType.LIBER_MATRIMONIORUM.getBookName()),
                        BookType.LIBER_DEFUNCTORUM, new Book(BookType.LIBER_DEFUNCTORUM.getBookName())
                )
        );
    }

    public StandardIndexFactory() {
        this((strings, bookType) -> switch (bookType) {
            case LIBER_CONFIRMATORUM -> strings.contains("confirmation-name");
            case LIBER_DEFUNCTORUM -> strings.contains("death-date-time");
            case LIBER_MATRIMONIORUM -> strings.contains("husband-surname");
            case LIBER_BAPTISMORUM -> strings.contains("surname");
        });
    }
}
