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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import pl.koder95.eme.dfs.ActNumber;

/**
 * Klasa reprezentuje kwerendę wyszukującą.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.0.202
 */
class ConstantSearchQuery extends AbstractSearchQuery {
    
    private final int year;
    private final ActNumber an;
    private final Map<String, String> data;

    /**
     * Podstawowy konstruktor.
     * 
     * @param names zbiór nazw danych do przeszukania
     * @param value wartość do znalezienia
     */
    public ConstantSearchQuery(Set<String> names, String value) {
        super(value);
        SearchPhrase phrase = SearchPhrase.createDefault(value);
        this.year = getYear(phrase.getWords(), phrase.getANWordIndex());
        this.an = getActNumber(phrase.getWords(), phrase.getANWordIndex());
        this.data = getData(names, phrase.getWords(), phrase.getIDWordIndex(),
                year);
    }

    /**
     * Tworzy nową kwerendę szukającą numer aktu.
     * 
     * @param an numer aktu do znalezienia
     */
    public static ConstantSearchQuery create(ActNumber an) {
        HashSet<String> names = new HashSet<>();
        names.add("an");
        return new ConstantSearchQuery(names, an.toString());
    }

    @Override
    int getYear() {
        return year;
    }

    @Override
    ActNumber getActNumber() {
        return an;
    }

    @Override
    String getData(String name) {
        return data.get(name);
    }

    @Override
    Map<String, String> getData() {
        return data;
    }
    
    private static int getYear(String[] words, int anIndex) {
        int year = -1;
        try (Scanner scan = new Scanner(String.join(" ", words))) {
            while (scan.hasNext()) {
                if (scan.hasNextInt()) {
                    year = scan.useDelimiter(" ").nextInt(); break;
                } else scan.next();
            }
        } catch (InputMismatchException ex) {
            ActNumber an = getActNumber(words, anIndex);
            if (an == null) return -1;
            return an.getYear();
        }
        return year;
    }
    
    private static ActNumber getActNumber(String[] words, int anIndex) {
        if (anIndex < 0) return null;
        
        ActNumber an = ActNumber.parseActNumber(words[anIndex]);
        if (an!= null) return an;
        
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("/")) {
                if (words[i].equals("/") && i-1 >= 0 && i+1 < words.length) {
                    return ActNumber
                            .parseActNumber(words[i-1] + "/" + words[i+1]);
                }
                int slash = words[i].indexOf("/");
                if (slash != 0 && slash != words[i].length()-1)
                    return ActNumber.parseActNumber(words[i]);
            }
        }
        return null;
    }
    
    private static Map<String, String> getData(Set<String> names, String[] words,
            int anIndex, int year) {
        HashMap<String, String> map = new HashMap<>();
        LinkedList<String> data = new LinkedList<>();
        data.addAll(Arrays.asList(words));
        
        if (anIndex >= 0) data.remove(anIndex);
        if (year > 0) data.remove(year + "");
        
        names.forEach((name) -> {
            map.put(name, data.remove());
        });
        return map;
    }
    
    static ConstantSearchQuery toConstant(AbstractSearchQuery q) {
        if (q == null) return null;
        return new ConstantSearchQuery(q.getData().keySet(), q.getEnteredText());
    }
}
