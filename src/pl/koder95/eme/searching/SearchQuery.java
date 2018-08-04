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
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import pl.koder95.eme.dfs.ActNumber;

/**
 * Klasa reprezentuje kwerendę wyszukującą.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.13-alt, 2018-08-04
 * @since 0.0.201
 */
public class SearchQuery extends AbstractSearchQuery {
    
    private final List<String> names; // lista nazw danych do przeszukania
    private final SearchPhrase phrase;

    /**
     * Podstawowy konstruktor.
     * 
     * @param enteredText wprowadzony tekst
     * @param names lista nazw danych do przeszukania
     * @param phrase fraza wyszukiwania
     */
    public SearchQuery(String enteredText, List<String> names, SearchPhrase phrase) {
        super(enteredText);
        this.names = names;
        this.phrase = phrase;
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param names lista nazw danych do przeszukania
     * @param value wartość do znalezienia
     */
    public SearchQuery(List<String> names, String value) {
        this(value, names, SearchPhrase.createDefault(value));
    }

    /**
     * Tworzy nową kwerendę szukającą numer aktu.
     * 
     * @param an numer aktu do znalezienia
     */
    public static SearchQuery create(ActNumber an) {
        List<String> names = new LinkedList<>();
        names.add("an");
        return new SearchQuery(names, an.toString());
    }

    @Override
    int getYear() {
        int year = -1;
        try (Scanner scan = new Scanner(String.join(" ", phrase.getWords()))) {
            while (scan.hasNext()) {
                if (scan.hasNextInt()) {
                    year = scan.useDelimiter(" ").nextInt(); break;
                } else scan.next();
            }
        } catch (InputMismatchException ex) {
            ActNumber an = getActNumber();
            if (an == null) return -1;
            return an.getYear();
        }
        return year;
    }

    @Override
    ActNumber getActNumber() {
        if (phrase.getANWordIndex() < 0) return null;
        
        ActNumber an = ActNumber.parseActNumber(phrase.getANWord());
        if (an!= null) return an;
        
        String[] words = phrase.getWords();
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

    @Override
    Map<String, String> getData() {
        HashMap<String, String> map = new HashMap<>();
        LinkedList<String> data = new LinkedList<>();
        data.addAll(Arrays.asList(phrase.getWords()));
        
        if (getYear() > 0) data.remove(getYear() + "");
        
        names.forEach((name) -> {
            if (!data.isEmpty()) {
                String info = data.remove();
                if (!info.isEmpty()) {
                    System.out.println(String.format("PUT %s TO KEY [%s]", info, name));
                    map.put(name, info);
                }
            }
        });
        System.out.println("map="+map);
        return map;
    }

    @Override
    String getData(String name) {
        return getData().get(name);
    }
}
