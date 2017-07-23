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
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pl.koder95.ip.Main;
import pl.koder95.ip.u161114.DialogLink;
import pl.koder95.ip.u161114.Index;
import pl.koder95.ip.u161114.Sex;
import pl.koder95.ip.u161114.models.SexIndexModel;

/**
 *
 * @author Kamil
 */
public class SexIndexView extends IndexView {

    private final JPanel male = new JPanel(), female = new JPanel();
    private final JLabel maleLastName = new JLabel(), maleLastNameLabel = new JLabel("Nazwisko:");
    private final JLabel maleName = new JLabel(), maleNameLabel = new JLabel("Imię:");
    private final JLabel femaleLastName = new JLabel(), femaleLastNameLabel = new JLabel("Nazwisko:");
    private final JLabel femaleName = new JLabel(), femaleNameLabel = new JLabel("Imię:");

    public SexIndexView(Index index) {
        super(index);
    }

    public SexIndexView() {
    }

    @Override
    protected void updateFields() {
        super.updateFields();
        SexIndexModel model = (SexIndexModel) getModel();
        maleLastName.setText(model.getLastName(Sex.MALE));
        maleName.setText(model.getName(Sex.MALE));
        femaleLastName.setText(model.getLastName(Sex.FEMALE));
        femaleName.setText(model.getName(Sex.FEMALE));
    }

    @Override
    protected void addComponents() {
        male.setBorder(BorderFactory.createTitledBorder("Mężczyzna"));
        female.setBorder(BorderFactory.createTitledBorder("Kobieta"));
        setValueFont(maleLastName);
        setValueFont(maleName);
        setValueFont(femaleLastName);
        setValueFont(femaleName);
        male.setLayout(buildSexPanelLayout(male, maleLastNameLabel, maleLastName, maleNameLabel, maleName));
        female.setLayout(buildSexPanelLayout(female, femaleLastNameLabel, femaleLastName, femaleNameLabel, femaleName));
        add(male);
        add(female);
        super.addComponents();
    }

    @Override
    protected LayoutManager2 buildLayout(Component... c) {
        GroupLayout l = new GroupLayout(this);
        int min = (c[0].getMinimumSize().width + c[1].getMinimumSize().width)/2,
                pref = (c[0].getPreferredSize().width + c[1].getPreferredSize().width)/2,
                max = (c[0].getMaximumSize().width + c[1].getMaximumSize().width)/2;
        GroupLayout.ParallelGroup horiz = l.createParallelGroup().addGroup(
                l.createSequentialGroup().addComponent(c[0], min, pref, max).addGap(10).addComponent(c[1], min, pref, max)
        ).addGroup(
                l.createSequentialGroup().addComponent(c[2]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[3])
        ).addGroup(
                l.createSequentialGroup().addComponent(c[4]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[5])
        );
        GroupLayout.SequentialGroup verti = l.createSequentialGroup().addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[0]).addComponent(c[1])
        ).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[2]).addComponent(c[3])
        ).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[4]).addComponent(c[5])
        );
        l.setHorizontalGroup(l.createSequentialGroup().addGap(10).addGroup(horiz).addGap(10));
        l.setVerticalGroup(l.createSequentialGroup().addGap(10).addGroup(verti).addGap(10));
        return l;
    }
    
    public LayoutManager2 buildSexPanelLayout(JPanel sexPanel, Component... c) {
        GroupLayout l = new GroupLayout(sexPanel);
        GroupLayout.ParallelGroup horiz = l.createParallelGroup().addGroup(
                l.createSequentialGroup().addComponent(c[0]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[1])
        ).addGroup(
                l.createSequentialGroup().addComponent(c[2]).addGap(10, 10, Short.MAX_VALUE).addComponent(c[3])
        );
        GroupLayout.SequentialGroup verti = l.createSequentialGroup().addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[0]).addComponent(c[1])
        ).addGap(10).addGroup(
                l.createParallelGroup().addComponent(c[2]).addComponent(c[3])
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
            SexIndexView iv = new SexIndexView(loaded[0]);
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
