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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pl.koder95.eme.Main;
import pl.koder95.eme.dfs.Index;

/**
 * Klasa określa kontekst dla wyszukiwań i umożliwia wybranie odpowiedniej
 * strategii wyszukiwania.
 * 
 * Aby wyszukiwanie było efektywne należy dobierać strategie zgodnie z ich
 * przeznaczeniem. Jest kilka dostępnych podstawowych strategii i jedna
 * zaawansowana.
 * 
 * Podstawowe:
 * <ol>
 * <li>wyszukiwanie {@link #setYearSearch() po roku}</li>
 * <li>wyszukiwanie {@link #setSearchQueueStrategy() po danych}</li>
 * <li>wyszukiwanie {@link #setAutoSearch() automatyczne}</li>
 * </ol>
 * Zaawansowana:
 * <ol>
 * <li>wyszukiwanie
 * {@link #setFilterSearch(pl.koder95.eme.searching.SearchFilter) filtrem}</li>
 * </ol>
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.0, 2018-10-07
 * @since 0.0.201
 */
public class SearchContext {
    
    private SearchStrategy strategy = new AutoSearchStrategy(null);

    /**
     * @param query kwerenda wyszukująca, na podstawie której dokona się
     * wyszukiwanie
     */
    public void setQuery(AbstractSearchQuery query) {
        strategy.query = query;
    }
    
    private void constantQuery() {
        setQuery(ConstantSearchQuery.toConstant(strategy.query));
    }

    /**
     * @param loaded lista indeksów, które mają zostać przeszukane
     * @return wynik wyszukiwania
     */
    public Index[] search(List<Index> loaded) {
        constantQuery();
        if (strategy == null) {
            System.err.println("strategy == null");
            return new Index[0];
        }
        loaded = new LinkedList<>(loaded);
        loaded.removeIf(i -> {
            boolean remove = false;
            for (String word : strategy.query.getEnteredText().split(" ")) {
                if (!i.toString().toUpperCase().contains(word)) remove = true;
            }
            return remove;
        });
        LinkedList<Index> search = strategy.searchFor(loaded);
        Index[] array = search.toArray(new Index[search.size()]);
        for (Index i: array) {
            if (i == null) search.remove(i);
        }
        array = search.toArray(new Index[search.size()]);
        search.clear();
        return array;
    }
    
    /**
     * @param searchResult wynik wyszukiwania
     * @return jeden, najbardziej odpowiedni indeks
     */
    public Index select(LinkedList<Index> searchResult) {
        if (searchResult == null || searchResult.isEmpty()) return null;
        if (searchResult.size() == 1) return searchResult.remove();
        Index theBest = searchResult.pollFirst();
        while (!searchResult.isEmpty())
            theBest = better(theBest, searchResult.pollFirst());
        Main.releaseMemory();
        return theBest;
    }
    
    /**
     * @param searchResultArray wynik wyszukiwania
     * @return jeden, najbardziej odpowiedni indeks
     */
    public Index select(Index[] searchResultArray) {
        if (searchResultArray == null || searchResultArray.length == 0)
            return null;
        
        LinkedList<Index> searchResult = new LinkedList<>();
        searchResult.addAll(Arrays.asList(searchResultArray));
        return select(searchResult);
    }
    
    private Index better(Index i0, Index i1) {
        double s0 = similarity(i0), s1 = similarity(i1);
        return s0 > s1? i0 : i1;
    }
    
    private double similarity(Index i) {
        if (strategy.query.getActNumber()!= null) {
            if (strategy.query.getActNumber().compareTo(i.getActNumber()) == 0)
                return 1d;
        }
        
        double similarity = 1d;
        Map<String, String> strData = strategy.query.getData();
        for (String name : strData.keySet()) {
            if (strData.get(name) != null && i.getData(name) != null) {
                similarity*= similarity(strData.get(name).toUpperCase(),
                        i.getData(name).toUpperCase());
                System.out.print("name: " + name);
                System.out.println(" similarity: " + similarity);
                if (similarity == 0) return 0;
            }
        }
        return similarity;
    }
    
    private double similarity(String s0, String s1) {
        if (s0.equals(s1)) return 1f;
        
        int prefixLength = 0;
        int sufixLength = 0;
        int begin = 0, end0 = s0.length()-1, end1 = s1.length()-1;
        while (begin >= end0 || begin >= end1) {
            if (prefixLength == 0) {
                if (begin < Math.min(s0.length(), s1.length()))
                    if (s0.charAt(begin) != s1.charAt(begin))
                        prefixLength = begin;
            }
            if (sufixLength == 0) {
                if (Math.min(end0, end1) >= 0)
                    if (s0.charAt(end0) != s1.charAt(end1))
                        prefixLength = s0.length() - end0;
            }
            begin++;
            end0--;
            end1--;
        }
        String s0d = s0.substring(prefixLength, s0.length()-sufixLength);
        String s1d = s1.substring(prefixLength, s1.length()-sufixLength);
        System.out.println("s0d = " + s0d);
        System.out.println("s1d = " + s1d);
        double s = (prefixLength + sufixLength) / (Math.max(s0.length(), s1.length()));
        return s;
    }

    /**
     * Ustawia wyszukiwanie automatyczne.
     */
    public void setAutoSearch() {
        strategy = new AutoSearchStrategy(strategy.query);
    }

    /**
     * Ustawia wyszukiwanie po roku.
     */
    public void setYearSearch() {
        strategy = new YearSearchStrategy(strategy.query);
    }

    /**
     * Ustawia wyszukiwanie według określonej kolejki wyszukiwania.
     */
    public void setSearchQueueStrategy() {
        strategy = new SearchQueue(strategy.query);
    }
}
