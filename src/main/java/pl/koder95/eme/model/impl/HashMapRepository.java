package pl.koder95.eme.model.impl;

import pl.koder95.eme.factory.IndexFactory;
import pl.koder95.eme.model.Index;
import pl.koder95.eme.model.Repository;
import pl.koder95.eme.model.RepositoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapRepository implements Repository {
    private static final IndexFactory DEFAULT_INDEX_FACTORY
            = id -> new Index(id, new ArrayList<>());

    private final Map<String, Index> indexMap = new HashMap<>();
    private IndexFactory indexFactory = DEFAULT_INDEX_FACTORY;

    public IndexFactory getIndexFactory() {
        return indexFactory;
    }

    public void setIndexFactory(IndexFactory indexFactory) {
        this.indexFactory = indexFactory;
    }

    @Override
    public void createIndex(String id) {
        if (indexMap.containsKey(id)) {
            throw new RepositoryException(
                    "Cannot create an index with id: " + id,
                    new IllegalArgumentException("Index already exists: " + id)
            );
        }
        indexMap.put(id, indexFactory.create(id));
    }

    @Override
    public Index getIndex(String id) {
        return indexMap.get(id);
    }

    @Override
    public void removeIndex(String id) {
        indexMap.remove(id).elementList().clear();
    }

    @Override
    public List<Index> getIndices() {
        return List.copyOf(indexMap.values());
    }
}
