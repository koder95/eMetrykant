/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import pl.koder95.ip.Main;
import pl.koder95.ip.u161114.DialogLink;
import pl.koder95.ip.u161114.Index;
import pl.koder95.ip.u161114.models.OnePersonIndexModel;

/**
 *
 * @author Kamil
 */
public class OnePersonIndexView extends IndexView {

    private final JLabel lastName = new JLabel(), lastNameLabel = new JLabel("Nazwisko:");
    private final JLabel name = new JLabel(), nameLabel = new JLabel("Imię:");

    public OnePersonIndexView(Index index) {
        super(index);
    }

    public OnePersonIndexView() {
    }

    @Override
    protected void updateFields() {
        super.updateFields();
        OnePersonIndexModel model = (OnePersonIndexModel) getModel();
        lastName.setText(model.getLastName());
        name.setText(model.getName());
    }

    @Override
    protected void addComponents() {
        setValueFont(lastName);
        setValueFont(name);
        add(lastNameLabel);
        add(lastName);
        add(nameLabel);
        add(name);
        super.addComponents();
    }

    @Override
    protected LayoutManager2 buildLayout(Component... c) {
        GroupLayout l = new GroupLayout(this);
        GroupLayout.ParallelGroup horiz = l.createParallelGroup().addGroup(
                l.createSequentialGroup().addComponent(c[0]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[1])
        ).addGroup(
                l.createSequentialGroup().addComponent(c[2]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[3])
        ).addGroup(
                l.createSequentialGroup().addComponent(c[4]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[5])
        ).addGroup(
                l.createSequentialGroup().addComponent(c[6]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[7])
        );
        GroupLayout.SequentialGroup verti = l.createSequentialGroup().addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[0]).addComponent(c[1])
        ).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[2]).addComponent(c[3])
        ).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[4]).addComponent(c[5])
        ).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[6]).addComponent(c[7])
        );
        l.setHorizontalGroup(l.createSequentialGroup().addGap(10).addGroup(horiz).addGap(10));
        l.setVerticalGroup(l.createSequentialGroup().addGap(10).addGroup(verti).addGap(10));
        return l;
    }
    
    public static void main(String[] args) {
        try {
            JFrame f = new JFrame("Test");
            f.setMinimumSize(new Dimension(400, 300));
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            Index[] loaded = DialogLink.MATRIMONIORUM.loadIndices(Main.CSV_DEFAULT_CHARSET);
            OnePersonIndexView iv = new OnePersonIndexView(loaded[0]);
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
