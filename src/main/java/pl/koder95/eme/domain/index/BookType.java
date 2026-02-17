package pl.koder95.eme.domain.index;

import java.util.List;

/**
 * Typ księgi indeksowej.
 */
public enum BookType {
    LIBER_BAPTISMORUM("Księga ochrzczonych", List.of("name", "surname", "an")),
    LIBER_CONFIRMATORUM("Księga bierzmowanych", List.of("name", "surname", "an")),
    LIBER_MATRIMONIORUM("Księga zaślubionych", List.of("husband-surname", "husband-name", "wife-surname", "wife-name", "an")),
    LIBER_DEFUNCTORUM("Księga zmarłych", List.of("name", "surname", "an"));

    private final String bookName;
    private final List<String> fieldSchema;

    BookType(String bookName, List<String> fieldSchema) {
        this.bookName = bookName;
        this.fieldSchema = fieldSchema;
    }

    public String getBookName() {
        return bookName;
    }

    public List<String> getFieldSchema() {
        return fieldSchema;
    }
}
