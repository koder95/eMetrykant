/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114;

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import pl.koder95.ip.u161114.views.IndexView;

/**
 *
 * @author Kamil
 */
public class MainFrame extends JFrame {
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
    
    private static final Font DEFAULT_BUTTON_FONT = new Font("Parchment", 1, 75);

    public MainFrame(String title) {
        super(title);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.getContentPane().setLayout(new GridLayout(2, 2));
        for (DialogLink dl: DialogLink.values()) {
            JButton b = new JButton(dl.getButtonLabel());
            b.setFont(DEFAULT_BUTTON_FONT);
            b.addActionListener((e) -> openDialogLink(dl));
            // TODO: napisać akcje do przycisku
            super.getContentPane().add(b);
        }
        super.pack();
        super.setLocationRelativeTo(null);
        javax.swing.JButton b = new javax.swing.JButton();
        
    }

    public MainFrame() {
        this("Przeglądarka indeksów parafialnych");
    }

    private void openDialogLink(DialogLink dl) {
        IndexView iv = dl.createIndexView();
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
