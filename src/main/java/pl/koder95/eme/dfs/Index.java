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

import org.w3c.dom.Node;
import pl.koder95.eme.Visited;
import pl.koder95.eme.dfs.impl.IndexNodeInterpreterImpl;

import java.util.Map;
import java.util.Set;

/**
 * Klasa reprezentuje indeks, czyli zbiór danych osobowych. Może również
 * posiadać numer aktu, który sprawia, że można odszukać dany indeks w księgach
 * przechowywanych na parafii.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.5.0, 2025-12-11
 * @since 0.1.11
 */
public class Index implements Visited {

    private static final IndexNodeInterpreter NODE_INTERPRETER = new IndexNodeInterpreterImpl();
    private final Map<String, String> data; // dane indeksu
    private ActNumber an;
    private final Book owner;

    private Index(Book owner, Map<String, String> data, ActNumber an) {
        this.owner = owner;
        this.an = an;
        this.data = Map.copyOf(data);
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
        if (index == null || !index.hasAttributes()) return new Index(owner, Map.of(), null);
        if (!index.getNodeName().equalsIgnoreCase("index")) return null;
        Map<String, String> data = NODE_INTERPRETER.interpret(index);
        return validate(new Index(owner, data, ActNumber.parseActNumber(data.get("an"))));
    }

    private static Index validate(Index index) {
        if (index == null || !index.getDataNames().contains("an") || index.getData("an").isEmpty()) {
            return null;
        }
        return index;
    }

    /**
     * Tworzy nowy indeks na podstawie mapy danych.
     *
     * @param owner księga, do której indeks należy
     * @param data mapa danych
     * @return nowy indeks lub {@code null}, jeśli mapa to {@code null} lub jeśli nie znaleziono numeru aktu
     */
    public static Index create(Book owner, Map<String, String> data) {
        if (data == null || !data.containsKey("an")) return null;
        return new Index(owner, data, ActNumber.parseActNumber(data.get("an")));
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
