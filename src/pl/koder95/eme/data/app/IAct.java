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

import java.util.Comparator;
import pl.koder95.eme.data.IUnmodifiableCollection;


/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public interface IAct extends IUnmodifiableCollection<IDataSection>,
        Comparable<IAct>, Comparator<IActNumber> {

    public IBook getOwner();
    
    public default IYear getYear() {
        return new IYear() {
            @Override
            public IBook getOwner() {
                return IAct.this.getOwner();
            }

            @Override
            public String getValue() {
                return IAct.this.getNumber().getYear();
            }
        };
    }
    
    public default String getSectionHeader(String dataName) {
        if (dataName == null) return null;
        return stream().reduce(null,
                (r, c) -> c.containsData(dataName)? c : r).getHeader();
    }
    
    public default boolean containsSection(String header) {
        if (header == null) return false;
        return stream().anyMatch(s -> s.getHeader().equals(header));
    }
    
    public default IDataSection getSection(String header) {
        if (header == null) return null;
        return stream().reduce(null,
                (r, c) -> c.getHeader().equals(header)? c : r);
    }
    
    public default IData getData(String name) {
        if (name == null) return null;
        String label = getSectionHeader(name);
        return getSection(label).getData(name);
    }
    
    public default boolean containsData(String name) {
        if (name == null) return false;
        return getSectionHeader(name) != null;
    }
    
    public default IActNumber getNumber() {
        if (!containsData(IActNumber.DATA_NAME))
            return IActNumber.unknown(this);
        IData d = getData(IActNumber.DATA_NAME);
        return d instanceof IActNumber? (IActNumber) d
                : IActNumber.unknown(this);
    }

    @Override
    public default int compareTo(IAct a) {
        if (a == null) return 1;
        return compare(getNumber(), a.getNumber());
    }

    @Override
    public default int compare(IActNumber o1, IActNumber o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        
        return o1.compareTo(o2);
    }
}
