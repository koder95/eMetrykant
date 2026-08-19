package pl.koder95.eme.model.impl;

import lombok.Getter;
import pl.koder95.eme.model.ElementIndex;
import pl.koder95.eme.model.ElementRepository;
import pl.koder95.eme.model.RepositoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * In-memory implementacja {@link ElementRepository}.
 */
public class HashMapElementRepository implements ElementRepository {

    private static final Function<String, ElementIndex> DEFAULT_INDEX_FACTORY =
            id -> new ElementIndex(id, new ArrayList<>());

    private final Map<String, ElementIndex> indexMap = new HashMap<>();
    @Getter
    private Function<String, ElementIndex> indexFactory = DEFAULT_INDEX_FACTORY;

    public void setIndexFactory(Function<String, ElementIndex> indexFactory) {
        this.indexFactory = indexFactory == null ? DEFAULT_INDEX_FACTORY : indexFactory;
    }

    @Override
    public void createIndex(String id) {
        if (indexMap.containsKey(id)) {
            throw new RepositoryException(
                    "Cannot create an index with id: " + id,
                    new IllegalArgumentException("Index already exists: " + id)
            );
        }
        ElementIndex index = indexFactory.apply(id);
        if (index == null) {
            throw new RepositoryException(
                    "Cannot create an index with id: " + id,
                    new IllegalArgumentException("Index factory returned null for id: " + id)
            );
        }
        indexMap.put(id, index);
    }

    @Override
    public ElementIndex getIndex(String id) {
        return indexMap.get(id);
    }

    @Override
    public void removeIndex(String id) {
        ElementIndex removed = indexMap.remove(id);
        if (removed != null) {
            boolean stillReferenced = indexMap.values().stream().anyMatch(idx -> idx == removed);
            if (!stillReferenced) {
                removed.elementList().clear();
            }
        }
    }

    @Override
    public List<ElementIndex> getIndices() {
        return indexMap.values().stream()
                .map(idx -> new ElementIndex(idx.id(), List.copyOf(idx.elementList())))
                .toList();
    }
}
