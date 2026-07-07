package pl.koder95.eme.domain.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Księga zawierająca indeksy.
 */
public class Book {

    private final List<Index> indices;
    private final String name;

    public Book(String name) {
        String normalizedName = Objects.requireNonNull(name, "name must not be null").trim();
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = normalizedName;
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
