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

package pl.koder95.eme.dfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

/**
 * Szablon dla indeksów, który pozwala utworzyć indeks według określonych reguł
 * układania danych.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.2.0
 */
public class IndexTemplate {

    private final Map<String, Integer> attr_data;
    private final Map<String, String> attr_label;
    private int data_length;

    private IndexTemplate(Map<String, Integer> attr_data,
            Map<String, String> label_attr) {
        this.attr_data = attr_data;
        this.attr_label = label_attr;
        int max = -1;
        for (Integer i: attr_data.values()) {
            if (i > max) max = i;
        }
        this.data_length = max+1;
    }
    
    /**
     * Tworzy obiekt z mapami typu {@link HashMap}.
     */
    public IndexTemplate() {
        this(new HashMap<>(), new HashMap<>());
    }
    
    /**
     * Pobiera określoną informacje z indeksu.
     * 
     * @param data tablica danych, z której pobrana ma być informacja
     * @param attr atrybut, odnosi się do konkretnego miejsca w tablicy z danymi
     * @return informacja, wartość atrybutu
     * @since 0.1.5
     */
    public String getData(String[] data, String attr) {
        return data[attr_data.get(attr)];
    }
    
    /**
     * Pozwala pobrać nazwę atrybutu, który przypisany jest do podanej etykiety.
     * 
     * @param label etykieta, wykorzystywana w różnych formularzach
     * @return nazwa atrybutu przypisanego do etykiety
     */
    public String getAttr(String label) {
        for (String key: attr_label.keySet()) {
            if (attr_label.get(key).equalsIgnoreCase(label)) return key;
        }
        return null;
    }
    
    /**
     * Pobiera pierwszą występującą etykietę, która do której przypisano podany
     * atrybut.
     * 
     * @param attr nazwa atrybutu
     * @return etykieta wyświetlana w formularzach
     */
    public String getLabel(String attr) {
        return attr_label.get(attr);
    }
    
    /**
     * Tworzy i dodaje nowy atrybut, który odnosi się do konkretnej informacji,
     * przypisuje go do nowej etykiety.
     * 
     * @param attr atrybuy
     * @param data_index indeks do informacji w {@link Index indeksie}
     * @param label etykieta
     */
    public void createAttr(String attr, int data_index, String label) {
        attr_label.put(attr, label);
        attr_data.put(attr, data_index);
        if (data_index+1 > data_length) data_length = data_index+1;
    }

    /**
     * Tworzy tablicę pustych atrybutów.
     * 
     * @param doc dokument XML
     * @return lista atrybutów, wartości nie ustawione
     * @since 0.1.5
     */
    public List<Attr> createAttrXMLArray(Document doc) {
        List<Attr> attrs = new ArrayList<>();
        attr_data.keySet().forEach((a) -> attrs.add(doc.createAttribute(a)));
        return attrs;
    }

    /**
     * @return ilość informacji, jakie zawiera każdy indeks według tego szablonu
     */
    public int getDataLength() {
        return data_length;
    }

    @Override
    public String toString() {
        return "IndexTemplate{" + "attr_data=" + attr_data + ", label_attr="
                + attr_label + ", data_length=" + data_length + '}';
    }
}
