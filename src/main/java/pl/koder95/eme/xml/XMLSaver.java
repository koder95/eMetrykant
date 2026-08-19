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

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

/**
 * Ułatwia zapisywanie plików XML. Klasa jest uzupełnieniem {@link XMLLoader}:
 * przyjmuje obiektowy model dokumentu (OMD, ang. DOM) i utrwala go w strumieniu.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @since 0.5.0
 */
public class XMLSaver {

    private XMLSaver() {}

    /**
     * @return nowy, pusty obiektowy model dokumentu
     * @throws ParserConfigurationException nieodpowiednia konfiguracja parsera
     */
    public static Document createDOM() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    /**
     * Zapisuje obiektowy model dokumentu do strumienia w formie sformatowanego XML-a.
     *
     * @param document obiektowy model dokumentu do zapisania
     * @param output strumień, do którego zapisywany jest dokument
     * @throws TransformerException błąd zapisu dokumentu
     */
    public static void saveDOM(Document document, OutputStream output) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(document), new StreamResult(output));
    }
}
