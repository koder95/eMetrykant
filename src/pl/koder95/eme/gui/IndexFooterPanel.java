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
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Stopka wyświetlająca ogólne informacje.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
public class IndexFooterPanel extends JPanel {
    
    private static final Font BOLD = new Font("Tahoma", 1, 11);
    private static final long serialVersionUID = -6481415779900749806L;

    /**
     * Domyślny konstruktor.
     */
    public IndexFooterPanel() {
        min.setFont(BOLD);
        max.setFont(BOLD);
        
        GroupLayout l = new GroupLayout(this);
        super.setLayout(l);
        l.setHorizontalGroup(l.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 0, 0)
                .addComponent(prefix)
                .addGap(0, 0, 0)
                .addComponent(min)
                .addGap(0, 0, 0)
                .addComponent(inter)
                .addGap(0, 0, 0)
                .addComponent(max)
                .addGap(0, 0, Short.MAX_VALUE)
        );
        l.setVerticalGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(prefix)
                .addComponent(min)
                .addComponent(inter)
                .addComponent(max)
                .addComponent(title)
        );
    }

    /**
     * @return etykieta, wyświetlająca największy numer indeksu, jaki został
     * wczytany
     */
    public JLabel getMax() {
        return max;
    }

    /**
     * @return etykieta, wyświetlająca najmniejszy numer indeksu, jaki został
     * wczytany
     */
    public JLabel getMin() {
        return min;
    }

    /**
     * @return etykieta, wyświetlająca tytuł zbioru indeksów
     */
    public JLabel getTitle() {
        return title;
    }
    
    // Variables declaration - do not modify
    private final JLabel inter = new JLabel(" do ");
    private final JLabel max = new JLabel("-/-");
    private final JLabel min = new JLabel("-/-");
    private final JLabel prefix = new JLabel(" zawiera dane od ");
    private final JLabel title = new JLabel("Program");
    // End of variables declaration
}
