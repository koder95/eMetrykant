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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import pl.koder95.eme.dfs.Index;

/**
 * Kolejka wyszukiwania to klasa zarządzająca kolejnością przeszukiwania danych
 * każdego indeksu.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.1.11
 */
public class SearchQueue extends SearchStrategy {

    private final Queue<Collection<String>> groups = new LinkedList<>();

    public SearchQueue(AbstractSearchQuery query) {
        super(query);
    }
    
    /**
     * Dodaje grupę nazw danych.
     * 
     * @param names nazwy systemowe
     * @return {@code true} (as specified by {@link LinkedList#add})
     */
    public boolean addNameGroup(String... names) {
        return groups.add(new LinkedList<>(Arrays.asList(names)));
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        Queue<Collection<String>> groups = new LinkedList<>(this.groups);
        LinkedList<Index> found = new LinkedList<>(list);
        
        String[] words = query.getEnteredText().split(" ");
        for (String word : words) {
            found = searchFor(found, groups.poll(), word);
        }
        
        return found;
    }
    
    private LinkedList<Index> searchFor(List<Index> list,
            Collection<String> names, String text) {
        LinkedList<Index> found = new LinkedList<>();
        names.stream().forEach((name) -> {
            searchFor(list, name, text).stream()
                    .filter((index) -> (!found.contains(index)))
                    .forEach((index) -> {
                found.offer(index);
            });
        });
        System.out.println("found(" + found.size() + ")=" + found);
        return found;
    }
    
    private LinkedList<Index> searchFor(List<Index> list, String name,
            String text) {
        LinkedList<Index> found = new LinkedList<>();
        LinkedList<Index> tmp = new LinkedList<>(list);
        tmp.removeIf((i)->{
            return !i.getDataNames().contains(name);
        });
        
        tmp.stream().filter((index) -> !(found.contains(index)))
                .forEach((index) -> {
            String data = index.getData(name);
            if (data.toUpperCase().startsWith(text.toUpperCase())) {
                found.offer(index);
            }
        });
        System.out.println(name + " found(" + found.size() + ")=" + found);
        return found;
    }
}
