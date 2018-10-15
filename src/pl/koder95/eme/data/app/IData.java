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

import pl.koder95.eme.data.IReadOnlyField;
import pl.koder95.eme.data.IReadOnlyValue;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public interface IData extends IReadOnlyField {

    public IAct getOwner();
    
    public default IDataSection getSection() {
        if (getOwner() == null) return null;
        return getOwner().getSection(getOwner().getSectionHeader(getName()));
    }

    @Override
    public default int compareTo(IReadOnlyValue rov) {
        if (rov == null) return 1;
        return rov instanceof IData? compareTo((IData) rov)
                : IReadOnlyField.super.compareTo(rov);
    }
    
    public default int compareTo(IData d) {
        if (d == null) return 1;
        
        if (getOwner() == null && getOwner() == null)
            return IReadOnlyField.super.compareTo(d);
        if (getOwner() == null) return -1;
        if (d.getOwner() == null) return 1;
        
        int owners = getOwner().compareTo(d.getOwner());
        return owners == 0? IReadOnlyField.super.compareTo(d) : owners;
    }
}
