/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import pl.koder95.ip.Main;
import pl.koder95.ip.u161114.views.IndexView;

/**
 *
 * @author Kamil
 */
public class IndexBrowser extends JDialog {

    private final JPanel[] layers = { new JPanel(), new JPanel() };
    private final Mediator mediator;
    private final IndexView indexPane;

    public IndexBrowser(DialogLink link, Frame owner)
            throws IOException {
        super(owner, link.getDialogTitle());
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.mediator = new Mediator(link.loadIndices());
        this.indexPane = link.createIndexView();
    }

    public IndexBrowser(DialogLink link, Charset c, Frame owner)
            throws IOException {
        super(owner, link.getDialogTitle());
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.mediator = new Mediator(link.loadIndices(c));
        this.indexPane = link.createIndexView();
    }
    
    public static void main(String[] args) {
        try {
            IndexBrowser browser = new IndexBrowser(DialogLink.CONFIRMATORUM, Main.CSV_DEFAULT_CHARSET, null);
            browser.initComponents();
            browser.setVisible(true);
        } catch (IOException ex) {
            System.exit(-1);
        }
    }

    private void initComponents() {
        JSeparator hl = new JSeparator();
        int margin = 10;
        
        GroupLayout l = new GroupLayout(layers[0]);
        layers[0].setLayout(l);
        l.setHorizontalGroup(
            l.createParallelGroup()
                .addGroup(l.createSequentialGroup()
                    .addGap(margin)
                    .addGroup(l.createParallelGroup()
                        .addComponent(mediator.getSearchingField())
                        .addComponent(indexPane))
                    .addGap(margin))
                .addComponent(hl, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addGroup(l.createSequentialGroup()
                    .addComponent(mediator.getMinLabel())
                    .addComponent(mediator.getMaxLabel()))
        );
        
        l.setVerticalGroup(
            l.createSequentialGroup()
                .addGap(margin)
                .addComponent(mediator.getSearchingField())
                .addGap(100)
                .addComponent(indexPane)
                .addGap(margin)
                .addComponent(hl)
                .addGap(margin)
                .addGroup(l.createParallelGroup()
                    .addComponent(mediator.getMinLabel())
                    .addComponent(mediator.getMaxLabel()))
        );
        
        layers[1].setOpaque(true);
        //layers[1].setBackground(Color.red);
        layers[1].setLayout(null);
        layers[1].add(mediator.getSuggestionPane());
        
        JLayeredPane layered = new JLayeredPane();
        layered.setLayer(layers[0], JLayeredPane.DEFAULT_LAYER);
        layered.setLayer(layers[1], JLayeredPane.POPUP_LAYER);
        layered.add(layers[0]);
        layered.add(layers[1]);
        
        setMinimumSize(new Dimension(400, 300));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(layered, BorderLayout.CENTER);
        pack();
        mediator.calculateCellHeight();
        setLocationRelativeTo(null);
    }
}
