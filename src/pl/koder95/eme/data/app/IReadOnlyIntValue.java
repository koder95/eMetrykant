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

import java.util.Scanner;
import pl.koder95.eme.data.IReadOnlyValue;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.1, 2018-10-15
 * @since 0.2.1
 */
public interface IReadOnlyIntValue extends IReadOnlyValue {

    public default int starts() {
        String value = getValue();
        if (value == null || value.isEmpty()) return -1;
        
        try (Scanner scanner = new Scanner(value)) {
            if (scanner.hasNextInt()) return scanner.nextInt();
        }
        return -1;
    }
    
    public default String tail() {
        String value = getValue();
        if (value == null || value.isEmpty()) return "";
        return value.substring(("" + starts()).length());
    }
    
    public default int toInt() {
        String tail = tail();
        if (tail == null || tail.isEmpty()) return starts();
        return -1;
    }
    
    public default boolean corrupted() {
        return !getValue().startsWith("-1") && starts() == -1;
    }

    @Override
    public default int compareTo(IReadOnlyValue rov) {
        if (rov == null) return 1;
        return rov instanceof IReadOnlyIntValue?
                compareTo((IReadOnlyIntValue) rov)
                : IReadOnlyValue.super.compareTo(rov);
    }
    
    public default int compareTo(IReadOnlyIntValue roiv) {
        if (roiv == null) return 1;
        
        if (corrupted() && roiv.corrupted())
            return tail().compareTo(roiv.tail());
        if (corrupted()) return -1;
        if (roiv.corrupted()) return 1;
        
        int i0ToInt = toInt(), i1ToInt = roiv.toInt();
        int i0Starts = starts(), i1Starts = roiv.starts();
        boolean i0digitV = i0ToInt == i0Starts;
        boolean i1digitV = i1ToInt == i1Starts;
        if (i0digitV && i1digitV) {
            // porównywanie wartości, które są liczbami int:
            return Integer.compare(i0ToInt, i1ToInt);
        }
        if (i0digitV) {
            // tylko pierwsza wartość jest liczbą, dlatego:
            return 1;
        }
        if (i1digitV) {
            // tylko druga liczba jest liczbą, dlatego:
            return -1;
        }
        // porównywanie wartości, które nie są liczbami, ale rozpoczynają się
        // od nich:
        return Integer.compare(i0Starts, i1Starts);
    }
}
