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

package pl.koder95.eme.data.app;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import pl.koder95.eme.data.INamed;
import pl.koder95.eme.data.IUnmodifiableList;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public interface IBook extends INamed, IUnmodifiableList<IAct> {
    
    public IBookRepository getOwner();

    public default IBookTemplate getTemplate() {
        IBookRepository repo = getOwner();
        if (repo == null) return null;
        return repo.getBookTemplate(getName());
    }
    
    public default IAct getAct(IActNumber an) {
        return stream().reduce(null, (r, c) -> c.getNumber().equals(an)? c : r);
    }
    
    public default boolean containsAct(IActNumber an) {
        return stream().anyMatch(a -> a.getNumber().equals(an));
    }
    
    public default List<IAct> getActs(IYear y) {
        if (!containsYear(y)) return Collections.emptyList();
        return stream().filter(a -> a.getNumber().getYear()
                .equals(y.getValue())).collect(Collectors.toList());
    }
    
    public default boolean containsYear(String year) {
        if (year == null) return false;
        return stream().anyMatch(a -> a.getNumber().getYear().equals(year));
    }
    
    public default boolean containsYear(IYear y) {
        return containsYear(y.getValue());
    }
    
    public default IYear getYear(String year) {
        if (year == null) return null;
        return stream().reduce(null, (r, c) -> c.getNumber().getYear()
                .equals(year)? c : r).getYear();
    }
    
    public default IYear getPreviousYear(IYear year) {
        if (year == null) return null;
        int index = indexOf(year.getFirstAct());
        IAct last = null;
        while (last == null) {
            if (index <= 0) return null;
            last = get(index--);
        }
        return last.getYear();
    }
    
    public default IYear getNextYear(IYear year) {
        if (year == null) return null;
        int index = indexOf(year.getLastAct());
        IAct first = null;
        while (first == null) {
            if (index < 0 || index >= size()-1) return null;
            first = get(index++);
        }
        return first.getYear();
    }
}
