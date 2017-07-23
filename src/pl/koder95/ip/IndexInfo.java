/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import java.awt.Font;
import javax.swing.GroupLayout;
import javax.swing.JLabel;

/**
 *
 * @author Kamil
 */
public class IndexInfo {
    
    private static final Font LABEL_FONT = new Font("Tahoma", 0, 14);
    private static final Font VALUE_FONT = LABEL_FONT.deriveFont(1);
    private final JLabel label, value;

    public IndexInfo(String name) {
        label = new JLabel(name.concat(":"));
        label.setFont(LABEL_FONT);
        value = new JLabel("-");
        value.setFont(VALUE_FONT);
    }

    public JLabel getLabel() {
        return label;
    }

    public JLabel getValue() {
        return value;
    }
}
