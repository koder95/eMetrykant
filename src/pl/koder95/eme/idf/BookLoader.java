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
 * @version 0.1.5, 2017-09-08
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
    
    /**
     * Tworzy nowe szablony ksiąg wczytując je z pliku XML.
     * 
     * @param xml plik XML zawierający szablony ksiąg
     * @throws IOException problemy z odczytaniem pliku
     * @throws SAXException błąd SAX
     * @throws ParserConfigurationException problemy z konfiguracją parsera XML
     */
    public void loadBookTemplates(File xml) throws IOException, SAXException,
            ParserConfigurationException {
        this.templates = BookTemplateLoader.load(xml);
    }
    
    /**
     * Tworzy nowy szablon księgi o podanej nazwie wczytując go z pliku XML.
     * 
     * @param xml plik XML zawierający szablon księgi
     * @param bookName nazwa księgi
     * @throws IOException problemy z odczytaniem pliku
     * @throws SAXException błąd SAX
     * @throws ParserConfigurationException problemy z konfiguracją parsera XML
     */
    public void loadBookTemplate(File xml, String bookName) throws IOException,
            SAXException, ParserConfigurationException {
        if (this.templates == null) this.templates = new HashMap<>();
        this.templates.put(bookName, BookTemplateLoader.load(xml, bookName));
    }

    /**
     * @param bookName nazwa księgi
     * @return obiekt wczytujący indeksy dla księgi
     */
    public IndicesLoader getLoader(String bookName) {
        return loaders.get(bookName);
    }
    
    /**
     * Tworzy nowy obiekt wczytujący indeksy dla księgi o podanej nazwie. Każdy
     * nowowczytany indeks tworzony będzie według podanego schematu.
     * 
     * @param bookName nazwa księgi
     * @param tmpl szablon indeksu
     */
    public void createIndicesLoader(String bookName, IndexTemplate tmpl) {
        loaders.put(bookName, new IndicesLoader(bookName, tmpl));
    }
    
    /**
     * Tworzy nowy obiekt wczytujący indeksy dla księgi o podanej nazwie. Każdy
     * nowowczytany indeks tworzony będzie według schematu, który utworzony
     * zostanie z szablonu księgi.
     * 
     * @param bookName nazwa księgi
     * @throws IllegalStateException jeśli przed wywołaniem tej funkcji nie
     * został utworzony szablon dla księgi o podanej nazwie (należy wywołać
     * metodę {@link #loadBookTemplate(java.io.File, java.lang.String)
     * loadBookTemplate} lub {@link #loadBookTemplates(java.io.File)
     * loadBookTemplates})
     */
    public void createIndicesLoader(String bookName)
            throws IllegalStateException{
        BookTemplate btmpl = templates.get(bookName);
        if (btmpl == null) throw new IllegalStateException("nie znaleziono "
                + "szablonu dla księgi o nazwie: \"" + bookName + "\"");
        createIndicesLoader(bookName, btmpl.createIndexTemplate());
    }
    
    private static final BookLoader INSTANCE = new BookLoader(new HashMap<>());
    
    /**
     * @return instancja {@code BookLoader}a
     */
    public static BookLoader get() {
        return INSTANCE;
    }
    
    /**
     * Wczytuje szablon indeksu.
     * 
     * @param xml plik z zapisanymi szablonami indeksów
     * @param bookName nazwa księgi
     * @return szablon indeksu
     * @throws IOException problemy z odczytaniem pliku
     * @throws SAXException błąd SAX
     * @throws ParserConfigurationException problemy z konfiguracją parsera XML
     * @since 0.1.5 
     */
    public static IndexTemplate loadIndexTemplate(File xml, String bookName)
            throws IOException, SAXException, ParserConfigurationException {
        return BookTemplateLoader.load(xml, bookName).createIndexTemplate();
    }
}
