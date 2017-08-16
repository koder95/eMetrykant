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
 * Klasa reprezentuje wyszukiwanie po danych. Każdy z indeksów sprawdzany jest,
 * czy zawiera dane, które zaczynają się jak odpowiadające im dane w kwerendzie.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
class DataSearchStrategy extends SearchStrategy {

    DataSearchStrategy(AbstractSearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null || query.getData().length == 0)
            return new LinkedList<>();
        
        LinkedList<Index> result = new LinkedList<>();
        String[] qData = query.getData();
        for (Index element: list) {
            boolean add = false;
            for (int i = 0; i < qData.length; i++) {
                for (int d = i; d < element.getData().length; d+=2) {
                    String dataE = element.getData(d);
                    String dataQ = qData[i];
                    if (dataE.startsWith(dataQ)) add = true;
                }
            }
            if (add) result.add(element);
        }
        return result;
    }

}
