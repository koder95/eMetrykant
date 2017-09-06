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

package pl.koder95.eme.idf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Wczytuje indeksy.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.4, 2017-09-06
 * @since 0.1.4
 */
public class IndicesLoader {

    private final String bookName;
    private final IndexTemplate tmpl;

    /**
     * Tworzy obiekt wczytujący księgę, do której stosowany jest odpowiedni
     * szablon indeksu.
     * 
     * @param bookName nazwa księgi
     * @param tmpl szablon, za pomocą którego tworzone będą indeksy
     */
    public IndicesLoader(String bookName, IndexTemplate tmpl) {
        this.bookName = bookName;
        this.tmpl = tmpl;
    }
    
    List<RealIndex> load(NamedNodeMap bookAttrs, NodeList childNodes) {
        List<RealIndex> reals = new LinkedList<>();
        if (bookAttrs.getNamedItem("name").getTextContent()
                .equalsIgnoreCase(bookName)) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeName().equalsIgnoreCase("index")) {
                    RealIndex r = tmpl.create(reals.size()+1, child);
                    if (r != null) reals.add(r);
                }
            }
        }
        List<RealIndex> array = new ArrayList<>(reals);
        reals.clear();
        return array;
    }
}
