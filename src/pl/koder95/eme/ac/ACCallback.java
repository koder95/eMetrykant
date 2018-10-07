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

import java.util.Collection;
import java.util.LinkedList;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import pl.koder95.eme.dfs.Index;

/**
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.0, 2018-10-07
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
                if (!i.toString().toUpperCase().contains(word)) remove = true;
            }
            return remove;
        });
        list.sort((o1, o2) -> {
            double[][] similarity = new double[2][words.length];
            int w = 0;
            for (String word : words) {
                String s1 = o1.toString().toUpperCase();
                String s2 = o2.toString().toUpperCase();
                similarity[0][w] = s1.contains(word)? 1 : 0; // - ile wspólnych
                similarity[1][w] = s2.contains(word)? 1 : 0; // - ile wspólnych
                w++;
            }
            double[] sim = new double[2];
            for (int i = 0; i < sim.length; i++) {
                for (w = 0; w < similarity[i].length; w++) {
                    sim[i] += similarity[i][w];
                }
            }
            return sim[0] > sim[1]? 1 : sim[1] > sim[0]? -1 : 0;
        });
        return list;
    }
    
}
