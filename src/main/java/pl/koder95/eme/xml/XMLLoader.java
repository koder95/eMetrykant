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

package pl.koder95.eme.xml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static pl.koder95.eme.Main.BUNDLE;

/**
 * Ułatwia wczytywanie plików XML. Klasa za pomocą metod przetwarza informacje
 * w pliku XML i w postaci obiektowego modelu dokumentu (OMD, ang. DOM)
 * udostępnia za pomocą interfejsu {@link Document}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.6, 2018-01-30
 * @since 0.1.6
 */
public class XMLLoader {

    private XMLLoader() {}

    /**
     * Wczytuje dokument z pliku XML w formie obiektowego modelu dokumentu.
     * 
     * @param xml plik XML, który ma zostać odczytany
     * @return obektowy model dokumentu wczytanego
     * @throws ParserConfigurationException nieodpowiednia konfiguracja parsera
     * @throws SAXException podstawowy błąd lub uwaga SAX
     * @throws IOException błąd wczytywania
     */
    public static Document loadDOM(File xml)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(xml);
    }
    
    /**
     * @param tag węzeł drzewa dokumentu XML, która jest znacznikiem i posiada
     * swoją listę atrybutów
     * @param attr nazwa atrybutu
     * @return węzeł drzewa dokumentu XML, atrybut o podanej nazwie
     */
    public static Node getAttribute(Node tag, String attr)
            throws IllegalArgumentException {
        if (tag.getAttributes() == null)
            throw new IllegalArgumentException(BUNDLE
                    .getString("THR_ARG_TAG_IS_WRONG"));
        else return tag.getAttributes().getNamedItem(attr);
    }
    
    /**
     * @param tag znacznik
     * @param attr nazwa atrybutu
     * @return wartość atrybutu
     */
    public static String getAttrV(Node tag, String attr) {
        return getAttribute(tag, attr).getTextContent();
    }
}
