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
package pl.koder95.eme.gui;

import java.util.List;
import pl.koder95.eme.idf.ActNumber;
import pl.koder95.eme.idf.Index;
import pl.koder95.eme.idf.Indices;
import pl.koder95.eme.searching.AbstractSearchQuery;
import pl.koder95.eme.searching.SearchContext;
import pl.koder95.eme.searching.SearchQuery;

/**
 * Klasa pomagająca wyszukiwanie indeksów.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
public class IndexSearcher {
    
    private final SearchContext context = new SearchContext();
    private final Indices indices;

    /**
     * Podstawowy konstruktor.
     * 
     * @param indices
     */
    public IndexSearcher(Indices indices) {
        this.indices = indices;
        this.indices.load();
    }

    /**
     * @return zbiór indeksów
     */
    public Indices getIndices() {
        return indices;
    }

    /**
     * @return lista indeksów ze zbioru
     */
    public List<Index> getLoaded() {
      return indices.getLoaded();
    }
    
    /**
     * @param id identyfikator
     * @return indeks o konkretnym identyfikatorze
     */
    public Index get(int id) {
        return indices.get(id);
    }
    
    /**
     * @param year rok
     * @return sprawdza, czy zbiór indeksów zawiera podany rok
     */
    public boolean hasYear(int year) {
        return indices.getLoaded().stream().anyMatch((i)
                -> (i.getActNumber().getYear() == year));
    }
    
    /**
     * @return pierwszy załadowany indeks
     */
    public Index getFirst() {
        return indices.getFirst();
    }
    
    /**
     * @return ostatni załadowany indeks
     */
    public Index getLast() {
        return indices.getLast();
    }

    /**
     * @param query kwerenda szukająca
     * @return tablica indeksów pasujących do kwerendy
     */
    public Index[] find(AbstractSearchQuery query) {
        context.setQuery(query);
        Index[] result = context.search(getLoaded());
        return result.length == 0? null : result;
    }

    /**
     * @param label tekst do znalezienia
     * @return tablica indeksów pasujących do tekstu
     */
    public Index[] find(String label) {
        String[] words = label.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].endsWith(":")) {
                words[i] = "#" + words[i].substring(0, words[i].lastIndexOf(':'));
            }
        }
        context.setAutoSearch();
        return find(new SearchQuery(String.join(" ", words)));
    }

    /**
     * @param data dane, które mają zostać wyszukane
     * @return tablica indeksów pasujących do wyszukiwanych danych
     */
    public Index[] find(String... data) {
        context.setDataSearch();
        return find(new SearchQuery(String.join(" ", data)));
    }

    /**
     * @param year rok
     * @param act numer aktu
     * @return tablica indeksów pasujących do podanego roku i numeru aktu
     */
    public Index[] find(int year, String act) {
        context.setActNumberSearch();
        return find(new SearchQuery(new ActNumber(act, year)));
    }

    /**
     * @param year rok
     * @return tablica indeksów pasujących do podanego roku
     */
    public Index[] find(int year) {
        System.out.println("find year " + year);
        context.setYearSearch();
        return find(new SearchQuery("" + year));
    }

    /**
     * @param result wynik wyszukiwania
     * @return jeden, najbardziej odpowiedni indeks
     */
    Index selectOne(Index[] result) {
        return context.select(result);
    }

}
