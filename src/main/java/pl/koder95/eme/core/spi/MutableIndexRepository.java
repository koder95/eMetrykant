package pl.koder95.eme.core.spi;

import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;

import java.util.Map;

/**
 * Repozytorium indeksów pozwalające na modyfikowanie i utrwalanie danych.
 */
public interface MutableIndexRepository extends IndexRepository {

    /**
     * Dodaje nowy indeks do wskazanej księgi.
     *
     * @param type typ księgi
     * @param data atrybuty indeksu, wymagany jest niepusty atrybut {@code an}
     * @return dodany indeks
     * @throws IllegalArgumentException gdy danych nie da się zamienić na indeks
     */
    Index add(BookType type, Map<String, String> data);

    /**
     * Zastępuje istniejący indeks nowym, zbudowanym z podanych atrybutów.
     *
     * @param type typ księgi
     * @param index indeks do zastąpienia
     * @param data nowe atrybuty indeksu
     * @return nowy indeks, który zajął miejsce poprzedniego
     * @throws IllegalArgumentException gdy danych nie da się zamienić na indeks
     * @throws IllegalStateException gdy wskazany indeks nie należy do księgi
     */
    Index replace(BookType type, Index index, Map<String, String> data);

    /**
     * Usuwa indeks ze wskazanej księgi.
     *
     * @param type typ księgi
     * @param index indeks do usunięcia
     * @return {@code true}, gdy indeks został usunięty
     */
    boolean remove(BookType type, Index index);

    /**
     * Utrwala aktualny stan wszystkich ksiąg.
     */
    void saveAll();
}
