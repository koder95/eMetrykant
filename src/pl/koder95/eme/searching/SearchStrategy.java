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
package pl.koder95.eme.searching;

import java.util.LinkedList;
import java.util.List;
import pl.koder95.eme.idf.Index;

/**
 * Klasa reprezentuje strategię wyszukiwania indeksów.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
abstract class SearchStrategy {
    
    AbstractSearchQuery query;

    /**
     * Podstawowy konstruktor.
     * 
     * @param query kwerenda szukająca
     */
    public SearchStrategy(AbstractSearchQuery query) {
        this.query = query;
    }

    /**
     * Przeszukuje listę indeksów i zwraca wynik wyszukiwania w postaci listy
     * łączonej.
     * 
     * @param list lista indeksów
     * @return wynik wyszukiwania
     */
    public abstract LinkedList<Index> searchFor(List<Index> list);

    /**
     * Ustawia kwerendę szukającą.
     * 
     * @param query kwerenda szukająca
     */
    public void setQuery(AbstractSearchQuery query) {
        this.query = query;
    }
}
