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

package pl.koder95.eme.ac;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import pl.koder95.eme.dfs.ActNumber;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.searching.SearchMethod;

/**
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.2.0
 */
public class ACCallback implements Callback
        <ISuggestionRequest, Collection<Index>> {
    
    private final Collection<Index> indices;

    public ACCallback(Collection<Index> indices) {
        this.indices = indices;
    }

    @Override
    public Collection<Index> call(ISuggestionRequest param) {
        LinkedList<Index> list = new LinkedList<>(indices);
        String[] words = param.getUserText().split(" ");
        list.removeIf(i -> {
            boolean remove = false;
            for (String word : words) {
                if (!i.toString().toUpperCase().contains("=" + word))
                    remove = true;
            }
            return remove;
        });
        list.sort((i1, i2)-> {
            String n1 = i1.getDataNames().stream()
                    .reduce(null, (r, c) -> c.contains("surname")? c : r);
            String n2 = i2.getDataNames().stream()
                    .reduce(null, (r, c) -> c.contains("surname")? c : r);
            int c1 = i1.getData(n1).compareTo(i2.getData(n1));
            int c2 = i1.getData(n2).compareTo(i2.getData(n2));
            if (c1 == c2) {
                int compare = c1;
                if (compare != 0) return compare;
                
                n1 = i1.getDataNames().stream()
                        .reduce(null, (r, c) -> c.contains("name")? c : r);
                n2 = i2.getDataNames().stream()
                        .reduce(null, (r, c) -> c.contains("name")? c : r);
                c1 = i1.getData(n1).compareTo(i2.getData(n1));
                c2 = i1.getData(n2).compareTo(i2.getData(n2));
                if (c1 == c2) return c1;
                if (c1 == 0) return c2;
                if (c2 == 0) return c1;
                int c3 = n1.compareTo(n2);
                return c3 > 0? c1 : c3 < 0? c2 : c1;
            }
            int compare = i1.getData(n1).compareTo(i2.getData(n2));
            if (compare != 0) return compare;
            n1 = i1.getDataNames().stream()
                    .reduce(null, (r, c) -> c.contains("name")? c : r);
            n2 = i2.getDataNames().stream()
                    .reduce(null, (r, c) -> c.contains("name")? c : r);
            c1 = i1.getData(n1).compareTo(i2.getData(n1));
            c2 = i1.getData(n2).compareTo(i2.getData(n2));
            if (c1 == c2) return c1;
            if (c1 == 0) return c2;
            if (c2 == 0) return c1;
            int c3 = n1.compareTo(n2);
            return c3 > 0? c1 : c3 < 0? c2 : c1;
        });
        list.sort(comparator(words));
        return list;
    }
    
    private Comparator<Index> comparator(String[] words) {
        ListIterator<String> iterator
                = new LinkedList<>(Arrays.asList("surname", "name"))
                        .listIterator();
        return (i1, i2) -> {
            for (String word : words) {
                ActNumber an = ActNumber.parseActNumber(word);
                if (an != null) {
                    int c1 = i1.getActNumber().compareTo(an);
                    int c2 = i2.getActNumber().compareTo(an);
                    if (c1 == 0) return 1;
                    if (c2 == 0) return -1;
                    return 0;
                }
                else {
                    String next;
                    if (iterator.hasNext()) next = iterator.next();
                    else {
                        while(iterator.hasPrevious()) iterator.previous();
                        next = iterator.next();
                    }
                    final String namePart = next;
                    String name1 = i1.getDataNames().stream().reduce(null,
                            (r, c) -> c.contains(namePart) || r == null?
                                    c : r.equalsIgnoreCase(namePart)? r : c);
                    String name2 = i2.getDataNames().stream().reduce(null,
                            (r, c) -> c.contains(namePart) || r == null?
                                    c : r.equalsIgnoreCase(namePart)? r : c);
                    String value1 = i1.getData(name1);
                    String value2 = i2.getData(name2);
                    SearchMethod method = SearchMethod.STARTS_WITH;
                    if (!value1.equalsIgnoreCase(value2)) {
                        double sim1 = method.similarity(value1, word);
                        double sim2 = method.similarity(value2, word);
                        if (sim1 > sim2) return 1;
                        else if (sim2 > sim1) return -1;
                    }
                }
            }
            return 0;
        };
    }
    
}
