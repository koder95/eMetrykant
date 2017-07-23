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
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import pl.koder95.ip.Main;
import pl.koder95.ip.u161114.DialogLink;
import pl.koder95.ip.u161114.Index;
import pl.koder95.ip.u161114.models.PersonIndexModel;

/**
 *
 * @author Kamil
 */
public class PersonIndexView extends IndexView {
    
    private final JList<String> peopleList = new JList<>();

    public PersonIndexView(Index index) {
        super(index);
        peopleList.setFocusable(false);
        peopleList.setModel(new DefaultListModel<>());
    }

    public PersonIndexView() {}

    @Override
    protected void updateFields() {
        super.updateFields();
        DefaultListModel<String> list = (DefaultListModel<String>) peopleList.getModel();
        list.removeAllElements();
        PersonIndexModel pim = (PersonIndexModel) getModel();
        for (int i = 1; i <= pim.getNumberOfPeaple(); i++) {
            list.addElement(createPersonLabel(pim.getLastName(i), pim.getName(i)));
        }
    }

    @Override
    protected void addComponents() {
        add(new JScrollPane(peopleList));
        super.addComponents();
    }

    @Override
    protected LayoutManager2 buildLayout(Component... c) {
        GroupLayout l = new GroupLayout(this);
        GroupLayout.ParallelGroup horiz = l.createParallelGroup().addComponent(c[0]).addGroup(
                l.createSequentialGroup().addComponent(c[1]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[2])
        ).addGroup(
                l.createSequentialGroup().addComponent(c[3]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[4])
        );
        GroupLayout.SequentialGroup verti = l.createSequentialGroup().addComponent(c[0]).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[1]).addComponent(c[2])
        ).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[3]).addComponent(c[4])
        );
        l.setHorizontalGroup(l.createSequentialGroup().addGap(10).addGroup(horiz).addGap(10));
        l.setVerticalGroup(l.createSequentialGroup().addGap(10).addGroup(verti).addGap(10));
        return l;
    }
    
    public static String createPersonLabel(String lastName, String name) {
        return lastName + " " + name;
    }
    
    public static void main(String[] args) {
        try {
            JFrame f = new JFrame("Test");
            f.setMinimumSize(new Dimension(400, 300));
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            Index[] loaded = DialogLink.MATRIMONIORUM.loadIndices(Main.CSV_DEFAULT_CHARSET);
            PersonIndexView iv = new PersonIndexView(loaded[0]);
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
