/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.gui;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.147
 */
public class MarriageIndexInfoPanel extends IndexInfoPanel {

    private static final long serialVersionUID = -5428026457693053552L;

    public MarriageIndexInfoPanel() {
        super.setBorder(BorderFactory.createTitledBorder(""));
        
        JPanel man, woman, people;
        man = new JPanel();
        GroupLayout lay0 = new GroupLayout(man);
        man.setLayout(lay0);
        lay0.setHorizontalGroup(lay0.createSequentialGroup()
                .addContainerGap()
                .addGroup(lay0.createParallelGroup()
                    .addGroup(lay0.createSequentialGroup()
                        .addComponent(super.getLastNameInfo().getLabel())
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(super.getLastNameInfo().getValue())
                    )
                    .addGroup(lay0.createSequentialGroup()
                        .addComponent(super.getNameInfo().getLabel())
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(super.getNameInfo().getValue())
                    )
                )
                .addContainerGap()
        );
        lay0.setVerticalGroup(lay0.createSequentialGroup()
                .addContainerGap()
                .addGroup(lay0.createParallelGroup()
                        .addComponent(super.getLastNameInfo().getLabel())
                        .addComponent(super.getLastNameInfo().getValue())
                )
                .addGap(20)
                .addGroup(lay0.createParallelGroup()
                        .addComponent(super.getNameInfo().getLabel())
                        .addComponent(super.getNameInfo().getValue())
                )
                .addContainerGap()
        );
        
        woman = new JPanel();
        GroupLayout lay1 = new GroupLayout(woman);
        woman.setLayout(lay1);
        lay1.setHorizontalGroup(lay1.createSequentialGroup()
                .addContainerGap()
                .addGroup(lay1.createParallelGroup()
                    .addGroup(lay1.createSequentialGroup()
                        .addComponent(lastNameInfo.getLabel())
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lastNameInfo.getValue())
                    )
                    .addGroup(lay1.createSequentialGroup()
                        .addComponent(nameInfo.getLabel())
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(nameInfo.getValue())
                    )
                )
                .addContainerGap()
        );
        lay1.setVerticalGroup(lay1.createSequentialGroup()
                .addContainerGap()
                .addGroup(lay1.createParallelGroup()
                        .addComponent(lastNameInfo.getLabel())
                        .addComponent(lastNameInfo.getValue())
                )
                .addGap(20)
                .addGroup(lay1.createParallelGroup()
                        .addComponent(nameInfo.getLabel())
                        .addComponent(nameInfo.getValue())
                )
                .addContainerGap()
        );
        
        people = new JPanel(new GridLayout(1, 2));
        people.add(man);
        people.add(woman);
        people.setBorder(BorderFactory.createTitledBorder("Mąż i żona"));
        
        GroupLayout l = new GroupLayout(this);
        super.setLayout(l);
        l.setHorizontalGroup(
            l.createSequentialGroup()
                .addContainerGap()
                .addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(people)
                    .addGroup(createHG(l, super.getActNumberInfo()))
                    .addGroup(createHG(l, super.getYearInfo())))
                .addContainerGap()
        );
        l.setVerticalGroup(
            l.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(people)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(createVG(l, super.getActNumberInfo()))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(createVG(l, super.getYearInfo()))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        linkSize(l, super.getYearInfo());
        linkSize(l, super.getActNumberInfo());
        linkSize(lay0, super.getNameInfo());
        linkSize(lay0, super.getLastNameInfo());
        linkSize(lay1, nameInfo);
        linkSize(lay1, lastNameInfo);
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

    private void setIndex(int id, String maleLastName, String maleName,
            String femaleLastName, String femaleName, String act, String year) {
        if (id > 0) super.setBorder(BorderFactory.createTitledBorder("#" + id));
        else super.setBorder(BorderFactory.createTitledBorder(""));
        getActNumberInfo().getValue().setText(act);
        getLastNameInfo().getValue().setText(maleLastName);
        getNameInfo().getValue().setText(maleName);
        lastNameInfo.getValue().setText(femaleLastName);
        nameInfo.getValue().setText(femaleName);
        getYearInfo().getValue().setText(year);
    }
    
    @Override
    public void setIndex(Index i) {
        if (i != null) {
            setIndex(i.ID, i.getData()[0], i.getData()[1], i.getData()[2],
                    i.getData()[3], i.getActNumber().getSign(),
                    "" + i.getActNumber().getYear());
            return;
        }
        resetIndex();
    }

    @Override
    public void resetIndex() {
        setIndex(0, "-", "-", "-", "-", "-", "-");
    }

    // Variables declaration - do not modify
    private final IndexInfo lastNameInfo = new IndexInfo("Nazwisko");
    private final IndexInfo nameInfo = new IndexInfo("Imię");
    // End of variables declaration
}
