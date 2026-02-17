package pl.koder95.eme.domain.index;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Typ księgi indeksowej.
 */
public enum IndexType {
    LIBER_BAPTISMORUM("Księga ochrzczonych", new LinkedList<>(Arrays.asList("name", "surname", "an"))),
    LIBER_CONFIRMATORUM("Księga bierzmowanych", new LinkedList<>(Arrays.asList("name", "surname", "an"))),
    LIBER_MATRIMONIORUM("Księga zaślubionych", new LinkedList<>(Arrays.asList("husband-surname", "husband-name", "wife-surname", "wife-name", "an"))),
    LIBER_DEFUNCTORUM("Księga zmarłych", new LinkedList<>(Arrays.asList("name", "surname", "an")));

    private final String bookName;
    private final Queue<String> fieldSchema;

    IndexType(String bookName, Queue<String> fieldSchema) {
        this.bookName = bookName;
        this.fieldSchema = fieldSchema;
    }

    public String getBookName() {
        return bookName;
    }

    public Queue<String> getFieldSchema() {
        return fieldSchema;
    }
}
