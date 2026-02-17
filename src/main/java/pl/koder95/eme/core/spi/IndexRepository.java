package pl.koder95.eme.core.spi;

import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.index.BookType;

import java.util.List;

/**
 * Repozytorium indeksów z możliwością przeładowania danych.
 */
public interface IndexRepository {

    List<Index> getIndices(BookType type);

    void reloadAll();
}
