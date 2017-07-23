/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161203;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JRadioButton;

/**
 *
 * @author Kamil
 */
public class SearchingModeButton extends JRadioButton implements ActionListener {
    
    private final Mediator mediator;
    private final int searchingMode;

    public SearchingModeButton(Mediator mediator, int searchingMode, boolean selected) {
        super("", selected);
        this.mediator = mediator;
        this.searchingMode = searchingMode;
    }

    public SearchingModeButton(Mediator mediator, int searchingMode) {
        super("");
        this.mediator = mediator;
        this.searchingMode = searchingMode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mediator.setSearchingMode(searchingMode);
    }
    
}
