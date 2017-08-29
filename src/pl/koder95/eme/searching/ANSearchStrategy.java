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
import pl.koder95.eme.idf.ActNumber;
import pl.koder95.eme.idf.Index;

/**
 * Klasa reprezentuje wyszukiwanie po numerze aktu (tzw.&nbsp;AN).
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
class ANSearchStrategy extends SearchStrategy {

    ANSearchStrategy(AbstractSearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null || query.getActNumber() == null)
            return new LinkedList<>();
        
        LinkedList<Index> result = new LinkedList<>();
        ActNumber an = query.getActNumber();
        list.stream().filter((i) -> (i.getActNumber().compareTo(an) == 0))
                .forEach((i) -> result.add(i));
        return result;
    }

}
