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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static pl.koder95.eme.idf.BookTemplate.Section;
import org.xml.sax.SAXException;

/**
 * Pozwala wczytać szablon księgi lub ksiąg.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.4, 2017-09-06
 * @since 0.1.4
 */
public class BookTemplateLoader {
    
    /**
     * Czyta plik XML i pobiera z niego szablony dla ksiąg, tworząc mapę, której
     * kluczami są nazwy ksiąg a zawartościami szablony.
     * 
     * @param xml plik XML zawierający szablony dla ksiąg
     * @return mapa, gdzie kluczem jest nazwa księgi a szablon wartością
     * @throws IOException problemy z odczytaniem pliku
     * @throws SAXException błąd SAX
     * @throws ParserConfigurationException problemy z konfiguracją parsera XML
     */
    public static HashMap<String, BookTemplate> load(File xml)
            throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = fact.newDocumentBuilder();
        Document doc = docBuilder.parse(xml);
        HashMap<String, BookTemplate> tmpls = new HashMap<>();
        NodeList bts = doc.getElementsByTagName("bt");
        for (int i = 0; i < bts.getLength(); i++) {
            BookTemplate tmpl = load(bts.item(i));
            tmpls.put(tmpl.name, tmpl);
        }
        return tmpls;
    }

    static BookTemplate load(File xml, String bookName)
            throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = fact.newDocumentBuilder();
        Document doc = docBuilder.parse(xml);
        NodeList bts = doc.getElementsByTagName("bt");
        for (int i = 0; i < bts.getLength(); i++) {
            Node btTag = bts.item(i);
            if (btTag.getAttributes().getNamedItem("name").getTextContent()
                    .equalsIgnoreCase(bookName)) {
                return load(btTag);
            }
        }
        return null;
    }
    
    private static BookTemplate load(Node btTag) {
        if (!btTag.getNodeName().equalsIgnoreCase("bt")) return null;
        
        BookTemplate tmpl = new BookTemplate(btTag.getAttributes()
                .getNamedItem("name").getTextContent());
        if (btTag.hasChildNodes()) {
            NodeList sections = btTag.getChildNodes();
            for (int i = 0; i < sections.getLength(); i++) {
                Node s = sections.item(i);
                if (!s.getNodeName().equalsIgnoreCase("section")) continue;
                Section section = new Section(s.getAttributes()
                        .getNamedItem("header").getTextContent());
                NodeList fields = s.getChildNodes();
                for (int n = 0; n < fields.getLength(); n++) {
                    Node f = fields.item(n);
                    if (!f.getNodeName().equalsIgnoreCase("field")) continue;
                    NamedNodeMap attribs = f.getAttributes();
                    String name = attribs.getNamedItem("name")
                            .getTextContent();
                    String label = attribs.getNamedItem("label")
                            .getTextContent();
                    int index = Integer.parseInt(attribs.getNamedItem("index")
                            .getTextContent());
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
