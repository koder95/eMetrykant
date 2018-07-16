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
package pl.koder95.eme.idf;

import pl.koder95.eme.dfs.ActNumber;
/**
 * Klasa umożliwia stworzenie obiektu, który zawiera jedynie identyfikator,
 * a nie wszystkie informacje o obiekcie, czyli indeks wirtualny. Informacje
 * brakujące pobierane są ze zbioru {@link RealIndex indeksów rzeczywistych}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.0.201
 */
class VirtualIndex extends Index {
    private final Indices indices;

    VirtualIndex(int id, Indices indices) {
        super(id);
        this.indices = indices;
    }

    @Override
    public ActNumber getActNumber() {
        return getRealIndex().AN;
    }

    @Override
    public String[] getData() {
        return getRealIndex().getData();
    }

    @Override
    public String getData(int index) {
        return getRealIndex().getData(index);
    }
    
    /**
     * @return obiekt przechowujący każde informacje o indeksie, nie tylko
     * jego identyfikator, tzw. {@link RealIndex indeks rzeczywisty}
     */
    public RealIndex getRealIndex() {
        return indices.getReal(ID);
    }
}
