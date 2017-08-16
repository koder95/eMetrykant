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

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import pl.koder95.eme.idf.Index;

/**
 * Panel wyświetla zestaw informacji na temat indeksu.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
 */
public class IndexInfoPanel extends JPanel {

    private static final long serialVersionUID = 3889281569274752554L;

    /**
     * Domyślny konstruktor.
     */
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

        linkSize(l, yearInfo);
        linkSize(l,actNumberInfo);
        linkSize(l, nameInfo);
        linkSize(l, lastNameInfo);
    }
    
    final void linkSize(GroupLayout l, IndexInfo i) {
        l.linkSize(SwingConstants.VERTICAL,
                new Component[] {i.getValue(), i.getName()});
    }
    
    private GroupLayout.SequentialGroup createHG(GroupLayout l, IndexInfo i) {
        return l.createSequentialGroup()
                .addComponent(i.getName())
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(i.getValue());
    }
    
    private GroupLayout.ParallelGroup createVG(GroupLayout l, IndexInfo i) {
        return l.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(i.getName(), GroupLayout.PREFERRED_SIZE, 15,
                            GroupLayout.PREFERRED_SIZE)
                    .addComponent(i.getValue());
    }

    /**
     * @return pole z informacjami o nazwisku
     */
    public IndexInfo getLastNameInfo() {
        return lastNameInfo;
    }

    /**
     * @return pole z informacjami o imieniu/imionach
     */
    public IndexInfo getNameInfo() {
        return nameInfo;
    }

    /**
     * @return pole z informacjami o numerze aktu
     */
    public IndexInfo getActNumberInfo() {
        return actNumberInfo;
    }

    /**
     * @return pole z informacjami o roku
     */
    public IndexInfo getYearInfo() {
        return yearInfo;
    }
    
    private void setIndexID(int id) {
        if (id > 0) super.setBorder(BorderFactory.createTitledBorder("#" + id));
        else super.setBorder(BorderFactory.createTitledBorder(""));
    }
    
    /**
     * @return identyfikator indeksu, który aktualnie jest wyświetlany
     */
    public int getIndexID() {
        String idS = ((TitledBorder) super.getBorder()).getTitle();
        if (idS.isEmpty()) return -1;
        try {
            return Integer.parseInt(idS.substring(1));
        } catch (NumberFormatException ex) { return -1; }
    }
    
    private void setIndex(int id, String lastName, String name,
            String act, String year) {
        setIndexID(id);
        actNumberInfo.getValue().setText(act);
        lastNameInfo.getValue().setText(lastName);
        nameInfo.getValue().setText(name);
        yearInfo.getValue().setText(year);
    }
    
    /**
     * Ustawia pola, aby wyświetlały informacje o podanym indeksie.
     *
     * @param i indeks, którego dane zostaną wyświetlone w panelu informacyjnym
     */
    public void setIndex(Index i) {
        if (i != null) {
            setIndex(i.ID, i.getData()[0], i.getData()[1],
                    i.getActNumber().getSign(), ""+i.getActNumber().getYear());
            return;
        }
        resetIndex();
    }

    /**
     * Ustawia wartości początkowe dla pól.
     */
    public void resetIndex() {
        setIndex(0, "-", "-", "-", "-");
    }

    // Variables declaration - do not modify
    private final IndexInfo lastNameInfo = new IndexInfo("Nazwisko");
    private final IndexInfo nameInfo = new IndexInfo("Imię");
    private final IndexInfo actNumberInfo = new IndexInfo("Nr aktu");
    private final IndexInfo yearInfo = new IndexInfo("Rok");
    // End of variables declaration
}
