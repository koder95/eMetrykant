package pl.koder95.eme.core.spi;

import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.index.UniqueActNumber;

import java.util.List;
import java.util.Optional;

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

    /**
     * Wyszukuje indeks po unikalnym numerze aktu, niezależnie od typu księgi.
     *
     * @param uan unikalny numer aktu
     * @return indeks albo {@link Optional#empty()} gdy numer nie istnieje,
     * jest nieznany albo skonfliktowany (przypisany do wielu indeksów)
     */
    Optional<Index> findByActNumber(UniqueActNumber uan);

    void reloadAll();
}
