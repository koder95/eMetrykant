/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import java.awt.Component;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Kamil
 */
public class IndexSearchingPanel extends JPanel {

    public IndexSearchingPanel(IndexBrowserMediator mediator) {
        actCombo.setEnabled(false);
        searchButton.setEnabled(false);
        yearCombo.setEnabled(false);
        searchField.setPreferredSize(new java.awt.Dimension(250, 20));
        searchLabel.setText("Wyszukaj:");
        searchButton.setText("Szukaj");
        
        actSearching.addActionListener((e) -> mediator.switchSearching());
        searchButton.addActionListener((e) -> mediator.search());
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mediator.searchKeyPressed(evt.getKeyCode());
            }
        });
        yearCombo.addActionListener((e) -> mediator.loadActComboBoxModel());
        
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

    public JButton getSearchButton() {
        return searchButton;
    }

    public JCheckBox getActSearching() {
        return actSearching;
    }

    public JComboBox<String> getActCombo() {
        return actCombo;
    }

    public JComboBox<Integer> getYearCombo() {
        return yearCombo;
    }

    public JLabel getSearchLabel() {
        return searchLabel;
    }

    public JLabel getSlash() {
        return slash;
    }

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
