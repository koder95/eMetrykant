package pl.koder95.eme.factory.impl;

import pl.koder95.eme.dfs.Book;
import pl.koder95.eme.dfs.BookType;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

public class StandardIndexFactory extends BasicIndexFactory {

    public StandardIndexFactory(BiPredicate<Set<String>, BookType> bookTypePattern) {
        super(strings -> Arrays.stream(BookType.values())
                .filter(bookType -> bookTypePattern.test(strings, bookType))
                .findFirst().orElseThrow(),
                Map.of(
                        BookType.LIBER_BAPTISMORUM, new Book("Księga ochrzczonych"),
                        BookType.LIBER_CONFIRMATORUM, new Book("Księga bierzmowanych"),
                        BookType.LIBER_MATRIMONIORUM, new Book("Księga zaślubionych"),
                        BookType.LIBER_DEFUNCTORUM, new Book("Księga zmarłych")
                )
        );
    }

    public StandardIndexFactory() {
        this((strings, bookType) -> switch (bookType) {
            case null -> false;
            case LIBER_CONFIRMATORUM -> strings.contains("confirmation-name");
            case LIBER_DEFUNCTORUM -> strings.contains("death-date-time");
            case LIBER_MATRIMONIORUM -> strings.contains("husband-surname");
            default -> strings.contains("surname");
        });
    }
}
