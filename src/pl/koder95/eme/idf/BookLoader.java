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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Umożliwia wczytanie ksiąg zapisanych w plikach XML.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.4, 2017-09-06
 * @since 0.1.4
 */
public class BookLoader {

    private final Map<String, IndicesLoader> loaders;
    private Map<String, BookTemplate> templates;
    
    private BookLoader(Map<String, IndicesLoader> loaders) {
        this.loaders = loaders;
    }
    
    List<RealIndex> load(File xml, String bookName)
            throws ParserConfigurationException,SAXException, IOException {
        DocumentBuilderFactory fdoc = DocumentBuilderFactory.newInstance();
        DocumentBuilder bdoc = fdoc.newDocumentBuilder();
        Document doc = bdoc.parse(xml);
        Element root = doc.getDocumentElement();
        NodeList books = root.getElementsByTagName("book");
        for (int i = 0; i < books.getLength(); i++) {
            Node book = books.item(i);
            if (book.hasAttributes()) {
                NamedNodeMap attrs = book.getAttributes();
                String name = attrs.getNamedItem("name").getTextContent();
                if (name.equalsIgnoreCase(bookName))
                    return loaders.get(attrs.getNamedItem("name")
                           .getTextContent()).load(attrs, book.getChildNodes());
            }
        }
        return null;
    }
    
    public void loadBookTemplates(File xml) throws IOException, SAXException,
            ParserConfigurationException {
        this.templates = BookTemplateLoader.load(xml);
    }
    
    public void loadBookTemplate(File xml, String bookName) throws IOException,
            SAXException, ParserConfigurationException {
        if (this.templates == null) this.templates = new HashMap<>();
        this.templates.put(bookName, BookTemplateLoader.load(xml, bookName));
    }

    public IndicesLoader getLoader(String bookName) {
        return loaders.get(bookName);
    }
    
    public void createIndicesLoader(String bookName, IndexTemplate tmpl) {
        loaders.put(bookName, new IndicesLoader(bookName, tmpl));
    }
    
    public void createIndicesLoader(String bookName) {
        BookTemplate btmpl = templates.get(bookName);
        createIndicesLoader(bookName, btmpl.createIndexTemplate());
    }
    
    private static final BookLoader INSTANCE = new BookLoader(new HashMap<>());
    
    public static BookLoader get() {
        return INSTANCE;
    }
}
