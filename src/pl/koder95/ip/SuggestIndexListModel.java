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

package pl.koder95.ip;

import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;
import pl.koder95.ip.idf.Index;
import pl.koder95.ip.idf.Indices;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.147
 */
public class SuggestIndexListModel extends AbstractListModel<String> {

    private static final long serialVersionUID = 8380603467716631460L;
    private final Indices indices;
    private final List<Index> suggest = new LinkedList<>();

    public SuggestIndexListModel(Indices indices) {
        this.indices = indices;
    }

    @Override
    public int getSize() {
        return suggest.size();
    }

    @Override
    public String getElementAt(int index) {
        Index i = suggest.get(index);
        String sug = i.getActNumber().toString();
        return sug + " " + String.join(" ", i.getData());
    }
    
    public void init() {
        suggest.addAll(indices.getLoaded());
    }
    
}
