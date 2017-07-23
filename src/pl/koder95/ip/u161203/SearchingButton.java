/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161203;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author Kamil
 */
public final class SearchingButton extends JButton implements ActionListener {
    
    private final Mediator mediator;

    public SearchingButton(Mediator mediator, String text) {
        super(text);
        this.mediator = mediator;
        mediator.registerSearchingButton(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mediator.search();
    }
    
    
}
