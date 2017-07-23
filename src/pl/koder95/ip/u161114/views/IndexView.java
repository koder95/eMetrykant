/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pl.koder95.ip.Main;
import pl.koder95.ip.u161114.DialogLink;
import pl.koder95.ip.u161114.Index;
import pl.koder95.ip.u161114.models.IndexModel;

/**
 *
 * @author Kamil
 */
public class IndexView extends JPanel {
    
    private static final Font VALUE_FONT = new Font("Tahoma", Font.BOLD, 14);

    private final JLabel actNumber = new JLabel(), actNumberLabel = new JLabel("Nr aktu:");
    private final JLabel year = new JLabel(), yearLabel = new JLabel("Rok:");
    
    private final IndexModel model;

    public IndexView(Index index) {
        this.model = new IndexModel(index);
        setValueFont(actNumber);
        setValueFont(year);
    }
    
    public IndexView() {
        this(null);
    }

    public IndexModel getModel() {
        return model;
    }
    
    public final void setValueFont(JComponent comp) {
        comp.setFont(VALUE_FONT);
    }
    
    protected void addComponents() {
        add(actNumberLabel);
        add(actNumber);
        add(yearLabel);
        add(year);
    }

    public final void buildLayout() {
        addComponents();
        setLayout(buildLayout(getComponents()));
    }
    
    protected boolean requestUpdateFields() {
        if (getModel() == null) return false;
        return !(actNumber.getText().equals(getModel().getActNumber()) && year.getText().equals(getModel().getYear()));
    }

    protected void updateFields() {
        if (getModel() == null) return;
        System.out.println("UPDATE");
        actNumber.setText(getModel().getActNumber());
        year.setText(getModel().getYear());
    }

    protected LayoutManager2 buildLayout(Component... c) {
        GroupLayout l = new GroupLayout(this);
        GroupLayout.ParallelGroup horiz = l.createParallelGroup().addGroup(
                l.createSequentialGroup().addComponent(c[0]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[1])
        ).addGroup(
                l.createSequentialGroup().addComponent(c[2]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[3])
        );
        GroupLayout.SequentialGroup verti = l.createSequentialGroup().addGroup(
                l.createParallelGroup().addComponent(c[0]).addComponent(c[1])
        ).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[2]).addComponent(c[3])
        );
        l.setHorizontalGroup(l.createSequentialGroup().addGap(10).addGroup(horiz).addGap(10));
        l.setVerticalGroup(l.createSequentialGroup().addGap(10).addGroup(verti).addGap(10));
        return l;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (requestUpdateFields()) {
            updateFields();
        }
    }
    
    public static void main(String[] args) {
        try {
            JFrame f = new JFrame("Test");
            f.setMinimumSize(new Dimension(400, 300));
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            Index[] loaded = DialogLink.DEFUNCTORUM.loadIndices(Main.CSV_DEFAULT_CHARSET);
            IndexView iv = new IndexView(loaded[0]);
            iv.buildLayout();
            
            f.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {}

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        int i = iv.getModel().getIndex().ID;
                        iv.getModel().setIndex(loaded[i]);
                        System.out.println(iv.getModel().getIndex());
                        iv.repaint();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {}
            });
            
            f.setContentPane(iv);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
