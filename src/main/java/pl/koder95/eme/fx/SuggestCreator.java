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

package pl.koder95.eme.fx;

import java.util.LinkedList;
import java.util.List;
import pl.koder95.eme.Visitor;
import pl.koder95.eme.dfs.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.12-alt, 2018-08-04
 * @since 0.1.12-alt
 */
public class SuggestCreator implements Visitor<Index> {

    @Override
    public void visit(Index i) {
        suggests.add(creating.createSuggestion(i));
    }

    private final List<String> suggests;
    private SuggestCreatingMethod creating;
    
    public static String prepareData(String data) {
        return data.toUpperCase().replaceAll(" ", "_");
    }
    
    public static final SuggestCreatingMethod DEFAULT_METHOD = (i)-> {
        StringBuilder builder = new StringBuilder();
        builder.append(prepareData(i.getData("surname")));
        builder.append(" ").append(prepareData(i.getData("name")));
        builder.append(" ").append(prepareData(i.getData("an")));
        return builder.toString();
    };

    public SuggestCreator(SuggestCreatingMethod creating) {
        this.suggests = new LinkedList<>();
        this.creating = creating;
    }

    public SuggestCreatingMethod getCreatingMethod() {
        return creating;
    }

    public void setCreatingMethod(SuggestCreatingMethod creating) {
        this.creating = creating;
    }
}
