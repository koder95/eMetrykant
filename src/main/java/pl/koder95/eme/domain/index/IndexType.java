package pl.koder95.eme.domain.index;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Typ księgi indeksowej.
 */
public enum IndexType {
    BAPTISMS("Księga ochrzczonych", new LinkedList<>(Arrays.asList("name", "surname", "an"))),
    CONFIRMATIONS("Księga bierzmowanych", new LinkedList<>(Arrays.asList("name", "surname", "an"))),
    MARRIAGES("Księga zaślubionych", new LinkedList<>(Arrays.asList("husband-surname", "husband-name", "wife-surname", "wife-name", "an"))),
    DECEASES("Księga zmarłych", new LinkedList<>(Arrays.asList("name", "surname", "an")));

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
