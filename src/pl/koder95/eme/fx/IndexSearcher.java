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
package pl.koder95.eme.fx;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import pl.koder95.eme.dfs.IndexList;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.searching.AbstractSearchQuery;
import pl.koder95.eme.searching.SearchContext;
import pl.koder95.eme.searching.SearchQuery;

/**
 * Klasa pomagająca wyszukiwanie indeksów.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.13-alt, 2018-08-04
 * @since 0.0.201
 */
public class IndexSearcher {
    
    private final SearchContext context = new SearchContext();
    private final IndexList indices;
    private final List<String> names = new LinkedList<>();

    /**
     * Podstawowy konstruktor.
     * 
     * @param indices 
     */
    public IndexSearcher(IndexList indices) {
        this.indices = indices;
        this.indices.load();
        // dodawanie nazw, które mają być przeszukiwane:
        addName("surname");
        addName("name");
        addName("husband-surname");
        addName("husband-name");
        addName("wife-surname");
        addName("wife-name");
        addName("an");
        System.out.println(names);
    }
    
    private void addName(String name) {
        if (indices.queueNames().contains(name)) names.add(name);
        else System.err.println("The name (" + name + ") was not adding,"
                + " because queue names is not contain this name!");
    }

    /**
     * @return zbiór indeksów
     */
    public IndexList getIndices() {
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
        System.out.println("find=" + label);
        context.setAutoSearch();
        return find(new SearchQuery(names, String.join(" ", words)));
    }

    /**
     * @param data dane, które mają zostać wyszukane
     * @return tablica indeksów pasujących do wyszukiwanych danych
     */
    public Index[] find(String... data) {
        context.setSearchQueueStrategy();
        return find(new SearchQuery(names, String.join(" ", data)));
    }

    /**
     * @param year rok
     * @return tablica indeksów pasujących do podanego roku
     */
    public Index[] find(int year) {
        System.out.println("find year " + year);
        context.setYearSearch();
        return find(new SearchQuery(names, "" + year));
    }

    /**
     * @param result wynik wyszukiwania
     * @return jeden, najbardziej odpowiedni indeks
     */
    public Index selectOne(Index[] result) {
        System.out.println("result=" + Arrays.toString(result));
        return context.select(result);
    }

}
