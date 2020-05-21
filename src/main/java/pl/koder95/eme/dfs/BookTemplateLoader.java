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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static pl.koder95.eme.dfs.BookTemplate.Section;
import org.xml.sax.SAXException;
import pl.koder95.eme.xml.XMLLoader;

/**
 * Pozwala wczytać szablon księgi lub ksiąg.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.1, 2020-05-21
 * @since 0.2.0
 */
public class BookTemplateLoader {
    
    /**
     * Czyta plik XML i pobiera z niego szablony dla ksiąg, tworząc mapę, której
     * kluczami są nazwy ksiąg a zawartościami - szablony.
     * 
     * @param xml plik XML zawierający szablony dla ksiąg
     * @return mapa, gdzie kluczem jest nazwa księgi a szablon wartością
     * @throws IOException problemy z odczytaniem pliku
     * @throws SAXException błąd SAX
     * @throws ParserConfigurationException problemy z konfiguracją parsera XML
     */
    public static HashMap<String, BookTemplate> load(File xml)
            throws IOException, SAXException, ParserConfigurationException {
        Document doc = XMLLoader.loadDOM(xml);
        HashMap<String, BookTemplate> tmpls = new HashMap<>();
        NodeList bts = doc.getElementsByTagName("bt");
        for (int i = 0; i < bts.getLength(); i++) {
            BookTemplate tmpl = load(bts.item(i));
            tmpls.put(tmpl.name, tmpl);
        }
        return tmpls;
    }

    /**
     * Przetwarza plik XML i zwraca jako szablon księgi. Jeżeli szablon nie
     * zawiera szablonu księgi o podanej nazwie, to zostaje zwrócona wartość
     * {@code null}.
     * 
     * @param xml plik XML
     * @param bookName nazwa księgi
     * @return szablon księgi wczytany z pliku XML
     * @throws IOException problemy z odczytaniem pliku
     * @throws SAXException błąd SAX
     * @throws ParserConfigurationException problemy z konfiguracją parsera XML
     */
    static BookTemplate load(File xml, String bookName)
            throws IOException, SAXException, ParserConfigurationException {
        return load(XMLLoader.getTagNode(XMLLoader.loadDOM(xml), "bt",
                "name", bookName));
    }
    
    private static BookTemplate load(Node btTag) {
        if (btTag == null) return null;
        if (!btTag.getNodeName().equalsIgnoreCase("bt")) return null;
        System.out.println("Book template loading...");
        
        BookTemplate tmpl = new BookTemplate(XMLLoader.getAttrV(btTag, "name"));
        if (btTag.hasChildNodes()) {
            NodeList sections = btTag.getChildNodes();
            for (int i = 0; i < sections.getLength(); i++) {
                Node s = sections.item(i);
                if (!s.getNodeName().equalsIgnoreCase("section")) continue;
                Section section = new Section(XMLLoader.getAttrV(s, "header"));
                NodeList fields = s.getChildNodes();
                for (int n = 0; n < fields.getLength(); n++) {
                    Node f = fields.item(n);
                    if (!f.getNodeName().equalsIgnoreCase("field")) continue;
                    
                    String name = XMLLoader.getAttrV(f, "name");
                    String label = XMLLoader.getAttrV(f, "label");
                    int index = Integer.parseInt(XMLLoader
                            .getAttrV(f, "index"));
                    section.fields.add(new BookTemplate
                            .Field(name, index, label));
                }
                tmpl.sections.add(section);
            }
        }
        return tmpl;
    }
    
    private BookTemplateLoader() {}
}
