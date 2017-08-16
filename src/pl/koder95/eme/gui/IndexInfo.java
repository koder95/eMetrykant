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
package pl.koder95.eme.gui;

import java.awt.Font;
import javax.swing.JLabel;

/**
 * Klasa zawiera dwie etykiety pozwalające wyświetlić nazwę pola i jego wartość.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
public class IndexInfo {
    
    private static final Font LABEL_FONT = new Font("Tahoma", 0, 14);
    private static final Font VALUE_FONT = LABEL_FONT.deriveFont(1);
    private final JLabel name, value;

    /**
     * Podstawowy konstruktor.
     * 
     * @param name nazwa pola
     */
    public IndexInfo(String name) {
        this.name = new JLabel(name.concat(":"));
        this.name.setFont(LABEL_FONT);
        value = new JLabel("-");
        value.setFont(VALUE_FONT);
    }

    /**
     * @return etykieta wyświetlająca nazwę pola
     */
    public JLabel getName() {
        return name;
    }

    /**
     * @return etykieta wyświetlająca wartość pola
     */
    public JLabel getValue() {
        return value;
    }
}
