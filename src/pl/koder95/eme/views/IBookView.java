/*
 * Copyright (C) 2018 Kamil Mularski
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
package pl.koder95.eme.views;

import pl.koder95.eme.dfs.IndexList;

/**
 * Interfejs umożliwia wyświetlenie widoku księgi.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.3.0
 */
public interface IBookView {
    
    /**
     * Wyświetla listę indeksów. Jeśli {@code list = null}, wtedy metoda
     * przywraca początkowy stan widoku.
     * 
     * @param list lista indeksów
     */
    void displayIndexList(IndexList list);
    
    /**
     * Pozwala zresetować widok, czyli przywrócić stan początkowy.
     */
    default void reset() {
        displayIndexList(null);
    }
}
