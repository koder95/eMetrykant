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

import java.util.function.BiFunction;

/**
 * Wyliczenie służące do określana, jaką metodą mają być porównywane ciągi
 * znaków podczas wyszukiwania i określania podobieństwa.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2020-05-20
 * @since 0.3.0
 */
public enum SearchMethod {
    
    EQUALS_IGNORE_CASE  (Acception.EVERY),
    STARTS_WITH         (Acception.BEGIN),
    CONTAINS            (Acception.SOME);

    private final Acception acception;

    SearchMethod(Acception acception) {
        this.acception = acception;
    }

    /**
     * @param s ciąg znaków, który ma być sprawdzany względem kwerendy
     * @param query kwerenda, tzn. wyraz lub inny dowolny ciąg znaków
     * @return podobieństwo, przedział: 0-1
     */
    public double similarity(String s, String query) {
        return acception.getSimilarity(s, query);
    }
    
    private enum Acception {
        EVERY((s, q) -> s.equalsIgnoreCase(q)? 1d: 0d),
        BEGIN((s, q) -> {
            int begin = 0;
            while (begin < s.length() && begin < q.length()) {
                if (s.charAt(begin) != q.charAt(begin)) break;
                begin++;
            }
            return (double) begin / s.length();
        }),
        SOME((s, q) -> {
            if (!s.contains(q)) return 0d;
            return (double) q.length() / s.length();
        }),
        NONE((s, q) -> {
            if (!s.contains(q)) return 1d;
            return 1d - ((double) q.length() / s.length());
        });
        
        private final BiFunction<String, String, Double> similarityFactor;

        private Acception(BiFunction<String, String, Double> similarityFactor) {
            this.similarityFactor = similarityFactor;
        }

        public double getSimilarity(String s, String query) {
            return similarityFactor.apply(s, query);
        }
    }
}
