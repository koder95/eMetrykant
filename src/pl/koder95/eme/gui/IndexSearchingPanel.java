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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Panel zawiera komponenty umożliwiające wyszukiwanie indeksów.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.0.201
 */
public class IndexSearchingPanel extends JPanel {

    private static final long serialVersionUID = 1853788901959580314L;

    /**
     * Domyślny konstruktor.
     */
    public IndexSearchingPanel() {
        actCombo.setEnabled(false);
        searchButton.setEnabled(false);
        yearCombo.setEnabled(false);
        searchField.setPreferredSize(new java.awt.Dimension(250, 20));
        PlainDocument doc = new PlainDocument() {
            private static final long serialVersionUID = 5542303891178286627L;
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                super.insertString(offs, str.toUpperCase(), a);
            }
        };
        searchField.setDocument(doc);
        searchLabel.setText("Wyszukaj:");
        searchButton.setText("Szukaj");
        
        GroupLayout l = new GroupLayout(this);
        super.setLayout(l);
        l.setHorizontalGroup(l.createSequentialGroup()
                .addComponent(searchLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 29, Short.MAX_VALUE)
                .addComponent(actSearching)
                .addGap(4, 4, 4)
                .addComponent(actCombo, 0, 69, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slash)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yearCombo, 0, 70, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton)
        );
        l.setVerticalGroup(l.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(actSearching, javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(l.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(slash)
                    .addComponent(searchButton)
                    .addComponent(actCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }

    /**
     * @return przycisk "Szukaj"
     */
    public JButton getSearchButton() {
        return searchButton;
    }

    /**
     * @return pole aktywujące wyszukiwanie po numerach aktu
     */
    public JCheckBox getActSearching() {
        return actSearching;
    }

    /**
     * @return lista rozwijana, zawierająca numery aktów dla danego roku
     */
    public JComboBox<String> getActCombo() {
        return actCombo;
    }

    /**
     * @return lista rozwijana, zawierająca dostępne lata, dla których można
     * wyszukać jakiś akt
     */
    public JComboBox<Integer> getYearCombo() {
        return yearCombo;
    }

    /**
     * @return etykieta przed polem wyszukiwania
     */
    public JLabel getSearchLabel() {
        return searchLabel;
    }

    /**
     * @return pole wyszukiwania
     */
    public JTextField getSearchField() {
        return searchField;
    }
    
    // Variables declaration - do not modify
    private final JButton searchButton = new JButton();
    private final JComboBox<String> actCombo = new JComboBox<>();
    private final JComboBox<Integer> yearCombo = new JComboBox<>();
    private final JCheckBox actSearching = new JCheckBox();
    private final JLabel searchLabel = new JLabel();
    private final JLabel slash = new JLabel("/");
    private final JTextField searchField = new JTextField();
    // End of variables declaration
}
