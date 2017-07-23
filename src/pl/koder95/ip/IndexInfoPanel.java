/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
public class IndexInfoPanel extends JPanel {

    private static final long serialVersionUID = 3889281569274752554L;

    public IndexInfoPanel() {
        super.setBorder(BorderFactory.createTitledBorder(""));
        GroupLayout l = new GroupLayout(this);
        super.setLayout(l);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(l.createSequentialGroup()
                .addContainerGap()
                .addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(createHG(l, lastNameInfo))
                    .addGroup(createHG(l, nameInfo) )
                    .addGroup(createHG(l, actNumberInfo))
                    .addGroup(createHG(l, yearInfo)))
                .addContainerGap())
        );
        l.setVerticalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(l.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(createVG(l, lastNameInfo))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(createVG(l, nameInfo))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(createVG(l, actNumberInfo))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(createVG(l, yearInfo))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        l.linkSize(SwingConstants.VERTICAL, new Component[] {yearInfo.getValue(), yearInfo.getLabel()});
        l.linkSize(SwingConstants.VERTICAL, new Component[] {actNumberInfo.getValue(), actNumberInfo.getLabel()});
        l.linkSize(SwingConstants.VERTICAL, new Component[] {nameInfo.getValue(), nameInfo.getLabel()});
        l.linkSize(SwingConstants.VERTICAL, new Component[] {lastNameInfo.getValue(), lastNameInfo.getLabel()});
    }
    
    private GroupLayout.SequentialGroup createHG(GroupLayout l, IndexInfo i) {
        return l.createSequentialGroup()
                .addComponent(i.getLabel())
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(i.getValue());
    }
    
    private GroupLayout.ParallelGroup createVG(GroupLayout l, IndexInfo i) {
        return l.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(i.getLabel(), GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                    .addComponent(i.getValue());
    }

    public IndexInfo getLastNameInfo() {
        return lastNameInfo;
    }

    public IndexInfo getNameInfo() {
        return nameInfo;
    }

    public IndexInfo getActNumberInfo() {
        return actNumberInfo;
    }

    public IndexInfo getYearInfo() {
        return yearInfo;
    }
    
    private void setIndex(String lastName, String name,
            String act, String year) {
        actNumberInfo.getValue().setText(act);
        lastNameInfo.getValue().setText(lastName);
        nameInfo.getValue().setText(name);
        yearInfo.getValue().setText(year);
    }
    
    public void setIndex(Index i) {
        if (i != null) {
            setIndex(i.getData()[0], i.getData()[1], i.getActNumber().getSign(),
                    ""+i.getActNumber().getYear());
            return;
        }
        resetIndex();
    }

    public void resetIndex() {
        setIndex("-", "-", "-", "-");
    }

    // Variables declaration - do not modify
    private final IndexInfo lastNameInfo = new IndexInfo("Nazwisko");
    private final IndexInfo nameInfo = new IndexInfo("Imię");
    private final IndexInfo actNumberInfo = new IndexInfo("Nr aktu");
    private final IndexInfo yearInfo = new IndexInfo("Rok");
    // End of variables declaration
}
