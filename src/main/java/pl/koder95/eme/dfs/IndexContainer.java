/*
 * Copyright (C) 2017 Kamil Jan Mularski [@koder95]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.koder95.eme.dfs;

import java.util.List;

/**
 * Zawiera metody dla klasy, która przechowywać będzie indeksy.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.1, 2020-05-20
 * @since 0.1.11
 */
public interface IndexContainer {

    /**
     * @param id identyfikator {@literal >} 0
     * @return indeks
     * @throws IndexOutOfBoundsException identyfikator nie jest większy od 0
     */
    default Index get(int id) {
        if (id > 0)
            return getLoaded().get(id - 1);
        else
            throw new IndexOutOfBoundsException("ID have to be greater than 0");
    }

    /**
     * @return lista wczytanych indeksów
     */
    List<Index> getLoaded();

    /**
     * Usuwa wczytane dane i zwalnia pamięć dla potencjalnie nowych danych.
     */
    default void clear() {
        getLoaded().clear();
    }
    
    /**
     * @return liczba indeksów wczytanych oraz ostatni utworzony identyfikator
     */
    default int size() {
        return getLoaded().size();
    }
    
    /**
     * @return pierwszy indeks
     */
    default Index getFirst() {
        return get(1);
    }
    
    /**
     * @return ostatni indeks
     */
    default Index getLast() {
        return get(size());
    }

    /**
     * @param i indeks
     * @return kolejny indeks
     */
    default Index getNext(Index i) {
        List<Index> loaded = getLoaded();
        int index = loaded.indexOf(i);
        return index < 0? null : loaded.get(index-1);
    }

    /**
     * @param i indeks
     * @return poprzedni indeks
     */
    default Index getPrev(Index i) {
        List<Index> loaded = getLoaded();
        int index = loaded.indexOf(i);
        return index < loaded.size()? loaded.get(index+1) : null;
    }
}
