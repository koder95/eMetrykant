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
import java.util.Map;
import pl.koder95.eme.dfs.Index;

/**
 * Klasa reprezentuje strategię automatyczną wyszukiwania, czyli taką, która
 * dobiera sobie sposób wyszukiwania zależnie od wprowadzonych danych. W miarę
 * możliwości należy unikać zastosowania tej strategii, gdyż jest mało wydajna.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.12-alt, 2018-08-04
 * @since 0.0.201
 */
class AutoSearchStrategy extends SearchStrategy {
    
    private final YearSearchStrategy year;
    private final SearchQueue queue; // kolejka wyszukiwania - strategia

    public AutoSearchStrategy(AbstractSearchQuery query) {
        super(query);
        year = new YearSearchStrategy(query);
        queue = new SearchQueue(query);
        // określa kolejność przeszukiwania danych w każdym indeksie:
        queue.addNameGroup("surname", "husband-surname", "wife-surname", "an");
        queue.addNameGroup("wife-surname", "name", "husband-name", "wife-name", "an");
        queue.addNameGroup("husband-name", "wife-name", "an");
        queue.addNameGroup("wife-name", "an");
        queue.addNameGroup("an");
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null) return new LinkedList<>();
        setQuery(query);
        
        Map<String, String> data = query.getData();
        int year = query.getYear();
        if (data != null && !data.isEmpty()) {
            if (list == null) System.err.println("list == null");
            return this.queue.searchFor(list);
        }
        if (year > 0) return this.year.searchFor(list);
        return new LinkedList<>();
    }

    @Override
    public void setQuery(AbstractSearchQuery query) {
        this.query = query;
        year.query = query;
        queue.query = query;
    }
}