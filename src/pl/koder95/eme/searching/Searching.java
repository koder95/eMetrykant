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

package pl.koder95.eme.searching;

import java.util.Collection;
import java.util.Iterator;
import pl.koder95.eme.ac.ACCallback;
import pl.koder95.eme.dfs.Index;

/**
 * Klasa reprezentująca wyszukiwanie umożliwia wykorzystanie wywołania zwrotnego
 * podpowiedzi, aby znaleźć indeks.
 * 
 * <p>Aby wyszukać indeks należy utworzyć obiekt wykorzystujący ten sam obiekt
 * wywołania zwrotnego, co system podpowiedzi.</p>
 * <p>
 * {@code ACCallback callback = ...;}<br/>
 * {@code TextField field = ...;}<br/>
 * {@code ...}<br/>
 * {@code Searching searching = new Searching(callback);}<br/>
 * {@code ...}<br/>
 * {@code TextFields.bindAutoCompletion(field, searching.getSuggestionCallback(), ...);}
 * </p>
 * 
 * <p>Dzięki temu można łatwo, korzystając z interfejsu {@link ISearchingRequest}
 * wyszukać indeks wykorzystując pole tekstowe i system podpowiedzi.</p>
 * <p>
 * {@code Index result = searching.search(field.getText());}
 * </p>
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.3.0
 */
public class Searching {

    private final ACCallback suggestionCallback;

    /**
     * Tworzy nowe wyszukiwanie przypisując wywołanie zwrotne dla podpowiedzi.
     * 
     * @param suggestionCallback wywołanie zwrotne, które wyszukiwanie
     * wykorzystuje, aby wyszukać indeks
     */
    public Searching(ACCallback suggestionCallback) {
        this.suggestionCallback = suggestionCallback;
    }

    /**
     * @param query kwerenda poszukująca
     * @return znaleziony indeks
     */
    public Index search(String query) {
        ISearchingRequest request = ()-> query;
        Collection<Index> indices = suggestionCallback.call(request);
        
        Iterator<Index> i = indices.iterator();
        if (!i.hasNext()) return null;
        return i.next();
    }

    /**
     * @return wywołanie zwrotne podpowiedzi
     */
    public ACCallback getSuggestionCallback() {
        return suggestionCallback;
    }
}
