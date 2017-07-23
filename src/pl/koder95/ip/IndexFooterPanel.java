/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import java.awt.Font;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Kamil
 */
public class IndexFooterPanel extends JPanel {
    
    private static final Font BOLD = new Font("Tahoma", 1, 11);

    public IndexFooterPanel() {
        min.setFont(BOLD);
        max.setFont(BOLD);
        
        GroupLayout l = new GroupLayout(this);
        super.setLayout(l);
        l.setHorizontalGroup(l.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 0, 0)
                .addComponent(prefix)
                .addGap(0, 0, 0)
                .addComponent(min)
                .addGap(0, 0, 0)
                .addComponent(inter)
                .addGap(0, 0, 0)
                .addComponent(max)
                .addGap(0, 0, Short.MAX_VALUE)
        );
        l.setVerticalGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(prefix)
                .addComponent(min)
                .addComponent(inter)
                .addComponent(max)
                .addComponent(title)
        );
    }

    public JLabel getMax() {
        return max;
    }

    public JLabel getMin() {
        return min;
    }

    public JLabel getTitle() {
        return title;
    }
    
    // Variables declaration - do not modify
    private final JLabel inter = new JLabel(" do ");
    private final JLabel max = new JLabel("-/-");
    private final JLabel min = new JLabel("-/-");
    private final JLabel prefix = new JLabel(" zawiera dane od ");
    private final JLabel title = new JLabel("Program");
    // End of variables declaration
}
