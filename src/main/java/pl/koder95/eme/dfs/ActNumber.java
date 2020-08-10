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
package pl.koder95.eme.dfs;

import java.io.Serializable;
import java.util.regex.Matcher;

import javafx.beans.property.*;
import pl.koder95.eme.Main;
import static pl.koder95.eme.Main.DIGITS_STRING_PATTERN;

/**
 * Klasa reprezentuje numer aktu. Zawiera sygnaturę, czyli niepowtarzalny dla
 * danego roku ciąg znaków (zwykle liczbę naturalną), oraz rok.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.1, 2020-05-21
 * @since 0.1.11
 */
public class ActNumber implements Comparable<ActNumber>, Serializable {

    private final ReadOnlyStringWrapper sign;
    private final ReadOnlyIntegerWrapper year;

    /**
     * Podstawowy konstruktor.
     * 
     * @param sign sygnatura
     * @param year rok
     */
    public ActNumber(String sign, int year) {
        this.sign = new ReadOnlyStringWrapper(sign);
        this.year = new ReadOnlyIntegerWrapper(year);
    }

    /**
     * @return sygnatura
     */
    public String getSign() {
        return signProperty().get();
    }

    /**
     * @return rok
     */
    public int getYear() {
        return yearProperty().get();
    }

    @Override
    public String toString() {
        return getSign() + "/" + getYear();
    }

    @Override
    public int compareTo(ActNumber o) {
        if (getYear() > o.getYear()) return 1;
        else if (getYear() < o.getYear()) return -1;
        else {
            Matcher digit1 = DIGITS_STRING_PATTERN.matcher(getSign());
            Matcher digit2 = DIGITS_STRING_PATTERN.matcher(o.getSign());
            if (digit1.find() && digit2.find()) {
                int act1 = Integer.parseInt(digit1.group(1));
                int act2 = Integer.parseInt(digit2.group(1));
                return act1 > act2? 1: act1 == act2? 0: -1;
            }
            String remainder1 = getSign().substring(digit1.end(1)+1);
            String remainder2 = getSign().substring(digit2.end(1)+1);
            return Main.DEFAULT_COLLATOR.compare(remainder1, remainder2);
        }
    }

    /**
     * @return właściwość: sygnatura
     * @since 0.3.1
     */
    public ReadOnlyStringProperty signProperty() {
        return sign.getReadOnlyProperty();
    }

    /**
     * @return właściwość: rok
     * @since 0.3.1
     */
    public ReadOnlyIntegerProperty yearProperty() {
        return year.getReadOnlyProperty();
    }
    
    /**
     * @param s ciąg znaków
     * @return numer aktu, chyba że ciąg jest nie możliwy do sparsowania, wtedy
     * {@code null}
     */
    public static ActNumber parseActNumber(String s) {
        if (!s.contains("/")) return null;
        
        int slash = s.indexOf("/");
        if (slash == 0 || slash == s.length()-1) return null;
        
        String sign = s.substring(0, slash);
        String yearS = s.substring(slash+1);
        try {
            int year = Integer.parseInt(yearS);
            return new ActNumber(sign, year);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
