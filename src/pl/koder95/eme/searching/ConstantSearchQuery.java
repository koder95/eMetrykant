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
 * @version 0.0.202, 2017-08-23
 * @since 0.0.202
 */
class ConstantSearchQuery extends AbstractSearchQuery {
    
    private final int id;
    private final int year;
    private final ActNumber an;
    private final String[] data;

    /**
     * Podstawowy konstruktor.
     * 
     * @param id identyfikator
     * @param year rok
     * @param an numer aktu
     * @param data dane
     */
    public ConstantSearchQuery(int id, int year, ActNumber an, String... data) {
        this.id = id;
        this.year = year;
        this.an = an;
        this.data = data;
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param words słowa
     * @param idIndex indeks słowa będącego identyfikatorem
     * @param anIndex indeks słowa będącego numerem aktu
     */
    public ConstantSearchQuery(String[] words, int idIndex, int anIndex) {
        this.id = getID(words, idIndex);
        this.year = getYear(words, anIndex);
        this.an = getActNumber(words, anIndex);
        this.data = getData(words, idIndex, anIndex, year);
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param phrase fraza wyszukiwania
     */
    public ConstantSearchQuery(SearchPhrase phrase) {
      this(phrase.getWords(), phrase.getIDWordIndex(), phrase.getANWordIndex());
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param value wartość do znalezienia
     */
    public ConstantSearchQuery(String value) {
        this(SearchPhrase.createDefault(value));
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param an numer aktu do znalezienia
     */
    public ConstantSearchQuery(ActNumber an) {
        this(an.toString());
    }

    /**
     * Alternatywny konstruktor.
     * 
     * @param id identyfikator do znalezienia
     */
    public ConstantSearchQuery(int id) {
        this("#" + id);
    }

    @Override
    int getID() {
        return id;
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
    String[] getData() {
        return data;
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
    
    private static int getID(String[] words, int idIndex) {
        if (idIndex < 0) return -1;
        String idWord = words[idIndex];
        try {
            return Integer.parseInt(idWord.substring(1));
        } catch (NumberFormatException ex) { return -1; }
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
    
    private static String[] getData(String[] words, int idIndex, int anIndex,
            int year) {
        LinkedList<String> data = new LinkedList<>();
        data.addAll(Arrays.asList(words));
        
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
        if (year > 0) data.remove(year + "");
        
        return data.toArray(new String[data.size()]);
    }
    
    static ConstantSearchQuery toConstant(AbstractSearchQuery q) {
        return new ConstantSearchQuery(q.getID(), q.getYear(), q.getActNumber(),
                q.getData());
    }
}
