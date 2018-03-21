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

import java.util.Map;
import pl.koder95.eme.dfs.ActNumber;

/**
 * Klasa reprezentująca kwerendę szukającą.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.0.201
 */
public abstract class AbstractSearchQuery {

    private final String enteredText;
    
    AbstractSearchQuery(String entered) {
        this.enteredText = entered;
    }

    abstract int getYear();
    abstract ActNumber getActNumber();
    abstract Map<String, String> getData();

    String getData(String name) {
        return getData().get(name);
    }

    String getEnteredText() {
        return enteredText;
    }
}
