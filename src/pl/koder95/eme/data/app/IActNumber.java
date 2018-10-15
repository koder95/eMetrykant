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

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public interface IActNumber extends IData {
    
    public static final String DATA_NAME = "an";
    
    public default int getSeparatorPosition() {
        return getValue().indexOf('/');
    }

    public default String getSign() {
        int sp = getSeparatorPosition();
        return sp > 0? getValue().substring(0, sp) : "";
    }
    
    public default String getYear() {
        int sp = getSeparatorPosition();
        if (sp < 0 || sp + 1 >= getValue().length()) return "";
        return getValue().substring(sp + 1);
    }

    @Override
    public default String getName() {
        return DATA_NAME;
    }

    @Override
    public default int compareTo(IData d) {
        if (d == null) return 1;
        if (d instanceof IActNumber) return compareTo((IActNumber) d);
        return IData.super.compareTo(d);
    }
    
    public default int compareTo(IActNumber an) {
        if (an == null) return 1;
        
        IReadOnlyIntValue year1 = () -> getYear();
        IReadOnlyIntValue year2 = () -> an.getYear();
        
        int years = year1.compareTo(year2);
        if (years != 0) return years;
        
        IReadOnlyIntValue id1 = () -> getSign();
        IReadOnlyIntValue id2 = () -> an.getSign();
        return id1.compareTo(id2);
    }
    
    public static IActNumber unknown(IAct owner) {
        return new IActNumber() {
            @Override
            public IAct getOwner() {
                return owner;
            }

            @Override
            public String getValue() {
                return "-/-";
            }
        };
    }
}
