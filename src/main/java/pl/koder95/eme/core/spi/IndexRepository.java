package pl.koder95.eme.core.spi;

import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;

import java.util.List;

/**
 * Repozytorium indeksów z możliwością przeładowania danych.
 */
public interface IndexRepository {

    /**
     * Zwraca indeksy dla wskazanego typu księgi.
     *
     * @param type typ księgi; wartość {@code null} nie jest dozwolona
     * @return zawsze nie-{@code null} lista indeksów; gdy brak danych zwracana jest pusta lista
     * @throws NullPointerException gdy {@code type == null}
     */
    List<Index> getIndices(BookType type);

    void reloadAll();
}
