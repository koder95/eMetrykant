/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.gui;

import java.awt.Font;
import javax.swing.JLabel;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.147
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
