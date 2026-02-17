package pl.koder95.eme.domain.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Księga zawierająca indeksy.
 */
public class Book {

    private final List<Index> indices;
    private final String name;

    public Book(String name) {
        this.name = name;
        this.indices = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Index> getIndices() {
        return Collections.unmodifiableList(indices);
    }

    public void addIndex(Index index) {
        if (index != null) {
            indices.add(index);
        }
    }

    public void addIndices(Collection<Index> records) {
        if (records != null) {
            for (Index record : records) {
                addIndex(record);
            }
        }
    }
}
