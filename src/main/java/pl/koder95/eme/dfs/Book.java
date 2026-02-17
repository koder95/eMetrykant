/*
 * Copyright (C) 2018 Kamil Jan Mularski [@koder95]
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;

/**
 * Klasa reprezentuje księgę zawierającą indeksy z danymi osobowymi.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.1, 2026-02-13
 * @since 0.1.10
 */
public class Book {

    final ObservableList<Index> indices;
    private final String name;

    /**
     * Tworzy księgę o podanej nazwie.
     * @param name nazwa księgi
     */
    public Book(String name) {
        this.name = name;
        this.indices = FXCollections.observableArrayList();
    }

    /**
     * @return nazwa księgi
     */
    public String getName() {
        return name;
    }

    public ObservableList<Index> getIndices() {
        return indices;
    }

    public void addIndex(Index index) {
        if (index != null) {
            indices.add(index);
        }
    }

    public void addIndices(Collection<Index> records) {
        if (records != null) {
            for (Index record : records) {
                addIndex(record);
            }
        }
    }
}
