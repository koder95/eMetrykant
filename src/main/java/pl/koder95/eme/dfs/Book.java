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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.koder95.eme.xml.XMLLoader;

/**
 * Klasa reprezentuje księgę zawierającą indeksy z danymi osobowymi.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.1.10
 */
public class Book {

    final ObservableList<Index> indices;
    private final String name;

    /**
     * Tworzy księgę o podanej nazwie.
     * @param name nazwa księgi
     */
    public Book(String name) {
        this.name = name;
        this.indices = FXCollections.observableArrayList();
    }

    /**
     * @return nazwa księgi
     */
    public String getName() {
        return name;
    }
    
    /**
     * Wczytuje indeksy z listy węzłów i dodaje je do księgi.
     * 
     * @param indices lista węzłów
     */
    public void load(NodeList indices) {
        if (indices == null) return;
        LinkedList<Index> linked = new LinkedList<>();
        for (int i = 0; i < indices.getLength(); i++) {
            Index ix = Index.create(indices.item(i));
            if (ix != null) linked.add(ix);
        }
        this.indices.addAll(linked);
        linked.clear();
    }
    
    /**
     * Wczytuje księgę z węzła XML.
     * 
     * @param book węzeł dokumentu XML
     * @return księga z indeksami
     */
    public static Book load(Node book) {
        if (book == null) return null;
        if (book.getNodeName().equalsIgnoreCase("book")) {
            if (book.hasAttributes()) {
                Book b = new Book(XMLLoader.getAttrV(book, "name"));
                b.load(book.getChildNodes());
                return b;
            } else return new Book("");
        } else return null;
    }
    
    /**
     * Wczytuje księgi z pliku XML.
     * 
     * @param xml plik XML
     * @return lista ksiąg z indeksami
     * @throws IOException problemy z odczytaniem pliku
     * @throws SAXException błąd SAX
     * @throws ParserConfigurationException problemy z konfiguracją parsera XML
     */
    public static List<Book> load(File xml) throws IOException,
            ParserConfigurationException, SAXException {
        LinkedList<Book> books = new LinkedList<>();
        Document doc = XMLLoader.loadDOM(xml);
        Element indices = doc.getDocumentElement();
        if (indices.getNodeName().equalsIgnoreCase("indices")) {
            NodeList bookNodes = indices.getElementsByTagName("book");
            for (int i = 0; i < bookNodes.getLength(); i++) {
                Book b = load(bookNodes.item(i));
                if (b != null) books.add(b);
            }
        }
        ArrayList<Book> array = new ArrayList<>(books);
        books.clear();
        return array;
    }
}
