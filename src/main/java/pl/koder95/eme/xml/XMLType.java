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

/**
 * Typ wyliczeniowy reprezentujący wewnętrzny typ dokumentu XML. 
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.6, 2018-01-30
 * @since 0.1.6
 */
public enum XMLType {

    /**
     * Reprezentuje typ wewnętrzny dokumentu XML,
     * który jest szablonem i zawiera podstawowe
     * znaczniki o nazwie {@code templates}.
     */
    TEMPLATES,
    /**
     * Reprezentuje typ wewnętrzny dokumentu XML,
     * który przechowuje dane o indeksach i zawiera podstawowe
     * znaczniki o nazwie {@code indices}.
     */
    INDICES;
    
    /**
     * Sprawdza dokument i zwraca jego typ wewnętrzny dokumentu XML.
     * 
     * @param doc obiektowy model dokumentu (OMD, ang. DOM)
     * @return typ wewnętrzny dokumentu XML
     */
    static XMLType analize(Document doc) {
        return valueOf(doc.getDocumentElement().getTagName().toUpperCase());
    }
}
