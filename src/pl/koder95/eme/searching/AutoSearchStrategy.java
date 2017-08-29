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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import pl.koder95.eme.idf.Index;

/**
 * Klasa reprezentuje strategię automatyczną wyszukiwania, czyli taką, która
 * dobiera sobie sposób wyszukiwania zależnie od wprowadzonych danych. W miarę
 * możliwości należy unikać zastosowania tej strategii, gdyż jest mało wydajna.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
class AutoSearchStrategy extends SearchStrategy {
    
    private final ANSearchStrategy an;
    private final DataSearchStrategy data;
    private final IDSearchStrategy id;
    private final YearSearchStrategy year;

    public AutoSearchStrategy(AbstractSearchQuery query) {
        super(query);
        an = new ANSearchStrategy(query);
        data = new DataSearchStrategy(query);
        id = new IDSearchStrategy(query);
        year = new YearSearchStrategy(query);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null) return new LinkedList<>();
        setQuery(query);
        
        if (query.getID() > 0) return this.id.searchFor(list);
        if (query.getActNumber() != null) return this.an.searchFor(list);
        
        String[] data = query.getData();
        int year = query.getYear();
        System.out.println("data=" + Arrays.toString(data));
        System.out.println("year=" + year);
        if (data != null && data.length != 0) {
            LinkedList<Index> listL;
            
            if (year > 0) listL = this.year.searchFor(list);
            else return this.data.searchFor(list);
            
            return this.data.searchFor(listL);
        }
        if (year > 0) return this.year.searchFor(list);
        return new LinkedList<>();
    }

    @Override
    public void setQuery(AbstractSearchQuery query) {
        this.query = query;
        an.query = query;
        data.query = query;
        id.query = query;
        year.query = query;
    }
}