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

package pl.koder95.eme.dfs;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import pl.koder95.eme.Visited;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Klasa reprezentuje indeks, czyli zbiór danych osobowych. Może również
 * posiadać numer aktu, który sprawia, że można odszukać dany indeks w księgach
 * przechowywanych na parafii.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.1.11
 */
public class Index implements Visited {

    private final Map<String, String> data = new HashMap<>(); // dane indeksu
    private ActNumber an;
    private final Book owner;
    
    private Index(Book owner, Node index) {
        this.owner = owner;
        NamedNodeMap attrs = index.getAttributes(); // pobiera listę atrybutów
        while (attrs.getLength() > 0) {
            Node attr = attrs.item(0); // pobiera pierwszy atrybut
            String key = attr.getNodeName(); // - nazwa atrybutu
            String value = attr.getTextContent(); // - wartość atrybutu
            data.put(key, value); // dodanie nazwy i wartości atrybutu do danych
            attrs.removeNamedItem(key); // usuwa odczytany atrybut
        }
    }
    
    private Index(Book owner) {
        this.owner = owner;
    }

    /**
     * Tworzy nowy indeks na podstawie węzła obiektowego modelu dokumentu XML.
     *
     * @param index węzeł XML
     * @return nowy indeks lub {@code null} jeśli węzeł {@code index} nazywa się
     * inaczej niż "index"
     */
    public static Index create(Node index) {
        return create(null, index);
    }

    /**
     * Tworzy nowy indeks na podstawie węzła obiektowego modelu dokumentu XML.
     *
     * @param owner księga, do której indeks należy
     * @param index węzeł XML
     * @return nowy indeks lub {@code null} jeśli węzeł {@code index} nazywa się
     * inaczej niż "index"
     */
    public static Index create(Book owner, Node index) {
        if (index == null) return new Index(owner);
        if (!index.getNodeName().equalsIgnoreCase("index")) return null;
        Index i = index.hasAttributes()? new Index(owner, index) : new Index(owner);
        return i.getDataNames().contains("an") && !i.getData("an").isEmpty()?
                i : null;
    }
    
    /**
     * @param name nazwa unikatowa określająca informacje, którą indeks
     * przechowuje
     * @return informacja o indeksie
     */
    public String getData(String name) {
        return data.getOrDefault(name, "");
    }
    
    /**
     * @return zestaw nazw informacji o indeksie
     */
    public Set<String> getDataNames() {
        return data.keySet();
    }

    /**
     * @return numer aktu
     */
    public ActNumber getActNumber() {
        if (an == null) an = ActNumber.parseActNumber(getData("an"));
        return an;
    }

    /**
     * @return księga, do której należy akt
     */
    public Book getOwner() {
        return owner;
    }
    
    @Override
    public String toString() {
       return getActNumber() + " " + data.toString();
    }
}
