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

import pl.koder95.eme.idf.Index;

/**
 * Klasa reprezentuje filter szukający.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
public interface SearchFilter {

    /**
     * Metoda zwraca {@code true} wtedy, i tylko wtedy, gdy indeks ma zostać
     * włączony w wynik wyszukiwania.
     *
     * @param i indeks sprawdzany
     * @return {@code true} - jeśli ma zostać włączony do wyników wyszukiwania,
     * {@code false} - w innych przypadkach
     */
    public boolean accept(Index i);
}
