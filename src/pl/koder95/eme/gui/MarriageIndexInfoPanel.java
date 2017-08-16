/*
 * Copyright (C) 2017 Kamil Jan Mularski [@koder95]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.koder95.eme.gui;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import pl.koder95.eme.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
public class MarriageIndexInfoPanel extends IndexInfoPanel {

    private static final long serialVersionUID = -5428026457693053552L;

    /**
     * Domyślny konstruktor.
     */
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
                        .addComponent(super.getLastNameInfo().getName())
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(super.getLastNameInfo().getValue())
                    )
                    .addGroup(lay0.createSequentialGroup()
                        .addComponent(super.getNameInfo().getName())
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(super.getNameInfo().getValue())
                    )
                )
                .addContainerGap()
        );
        lay0.setVerticalGroup(lay0.createSequentialGroup()
                .addContainerGap()
                .addGroup(lay0.createParallelGroup()
                        .addComponent(super.getLastNameInfo().getName())
                        .addComponent(super.getLastNameInfo().getValue())
                )
                .addGap(20)
                .addGroup(lay0.createParallelGroup()
                        .addComponent(super.getNameInfo().getName())
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
                        .addComponent(lastNameInfo.getName())
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lastNameInfo.getValue())
                    )
                    .addGroup(lay1.createSequentialGroup()
                        .addComponent(nameInfo.getName())
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(nameInfo.getValue())
                    )
                )
                .addContainerGap()
        );
        lay1.setVerticalGroup(lay1.createSequentialGroup()
                .addContainerGap()
                .addGroup(lay1.createParallelGroup()
                        .addComponent(lastNameInfo.getName())
                        .addComponent(lastNameInfo.getValue())
                )
                .addGap(20)
                .addGroup(lay1.createParallelGroup()
                        .addComponent(nameInfo.getName())
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
                .addComponent(i.getName())
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(i.getValue());
    }
    
    private GroupLayout.ParallelGroup createVG(GroupLayout l, IndexInfo i) {
        return l.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(i.getName(), GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
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
