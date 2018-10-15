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
import java.util.ListIterator;
import pl.koder95.eme.data.IReadOnlyValue;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public interface IYear extends IReadOnlyValue {
    
    public IBook getOwner();
    
    public default List<IAct> getActs() {
        if (getOwner() == null) return Collections.emptyList();
        return getOwner().getActs(this);
    }
    
    public default IAct getFirstAct() {
        return getActs().get(0);
    }
    
    public default IAct getLastAct() {
        return getActs().get(getActs().size()-1);
    }
    
    public default IAct getAct(String id) {
        if (id == null) return null;
        return getActs().stream().reduce(null, (r, c) -> c.getNumber().getSign()
                .equals(id)? c : r);
    }
    
    public default ListIterator<IAct> getActListIterator(String id) {
        IAct a = getAct(id);
        if (a == null) return null;
        return getActs().listIterator(getActs().indexOf(a));
    }
    
    public default IYear previous() {
        IBook owner = getOwner();
        return owner == null? null : getOwner().getPreviousYear(this);
    }
    
    public default IYear next() {
        IBook owner = getOwner();
        return owner == null? null : getOwner().getNextYear(this);
    }
}
