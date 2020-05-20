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
import pl.koder95.eme.dfs.ActNumber;
import pl.koder95.eme.dfs.Index;

/**
 * Strategia wyszukiwania indeksów na podstawie roku w numerze aktu.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.0.201
 */
class YearSearchStrategy extends SearchStrategy {

    YearSearchStrategy(AbstractSearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null || query.getYear() < 0) {
            System.out.println("query.getYear()=" + query.getYear());
            return new LinkedList<>();
        }
        
            System.out.println("query.getYear()=" + query.getYear());
        LinkedList<Index> result = new LinkedList<>();
        list.stream().forEach((i) -> {
            ActNumber an = i.getActNumber();
            if (an != null && an.getYear() == query.getYear()) {
                result.add(i);
            }
        });
        return result;
    }

}
