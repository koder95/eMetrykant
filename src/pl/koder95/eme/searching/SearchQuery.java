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
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import pl.koder95.eme.idf.ActNumber;

/**
 * Klasa reprezentuje kwerendę wyszukującą.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
public class SearchQuery extends AbstractSearchQuery {
    
    private final SearchPhrase phrase;

    /**
     * Podstawowy konstruktor.
     * 
     * @param phrase fraza wyszukiwania
     */
    public SearchQuery(SearchPhrase phrase) {
        this.phrase = phrase;
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param value wartość do znalezienia
     */
    public SearchQuery(String value) {
        this(SearchPhrase.createDefault(value));
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param an numer aktu do znalezienia
     */
    public SearchQuery(ActNumber an) {
        this(an.toString());
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param id identyfikator do znalezienia
     */
    public SearchQuery(int id) {
        this("#" + id);
    }

    @Override
    int getID() {
        if (phrase.getIDWordIndex() < 0) return -1;
        String idWord = phrase.getIDWord();
        try {
            return Integer.parseInt(idWord.substring(1));
        } catch (NumberFormatException ex) { return -1; }
    }

    @Override
    int getYear() {
        int year;
        try (Scanner scan = new Scanner(String.join(" ", phrase.getWords()))) {
            scan.useDelimiter(" ");
            year = scan.nextInt();
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
    String[] getData() {
        LinkedList<String> data = new LinkedList<>();
        data.addAll(Arrays.asList(phrase.getWords()));
        int idIndex = phrase.getIDWordIndex();
        int anIndex = phrase.getANWordIndex();
        
        if (idIndex >= 0) {
            if (anIndex >= 0) {
                if (idIndex == anIndex) data.remove(idIndex);
                else if (idIndex > anIndex) {
                    data.remove(idIndex);
                    data.remove(anIndex);
                }
                else {
                    data.remove(anIndex);
                    data.remove(idIndex);
                }
            }
            else data.remove(idIndex);
        }
        else if (anIndex >= 0) data.remove(anIndex);
        if (getYear() > 0) data.remove(getYear() + "");
        
        return data.toArray(new String[data.size()]);
    }

    @Override
    String getData(int i) {
        return getData()[i];
    }

    @Override
    public SearchFilter getIDFilter() {
        return (i)-> {
            return getID() == i.ID;
        };
    }

    @Override
    public SearchFilter getActNumberFilter() {
        return (i)-> {
            return getActNumber().compareTo(i.getActNumber()) == 0;
        };
    }

    @Override
    public SearchFilter getYearFilter() {
        return (i)-> {
            return getYear() == i.getActNumber().getYear();
        };
    }

    @Override
    public SearchFilter getDataFilter() {
        return (i)-> {
            String[] data = getData();
            boolean accept = true;
            for (int ii = 0; ii < data.length; ii++) {
                accept = accept && i.getData(ii).startsWith(data[ii]);
            }
            return accept;
        };
    }
}
