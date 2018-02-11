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
 * @version 0.1.9, 2018-02-11
 * @since 0.0.201
 */
public class IndexInfoPanel extends JPanel {

    private static final long serialVersionUID = 3889281569274752554L;
    
    private static GroupLayout.ParallelGroup buildHorizontalGroup(GroupLayout l,
            IndexInfo[] infos) {
        GroupLayout.ParallelGroup g = l.createParallelGroup(GroupLayout
                .Alignment.LEADING);
        for (IndexInfo info: infos) {
            g = g.addGroup(l.createSequentialGroup()
                    .addComponent(info.getName())
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(info.getValue())
            );
        }
        return g;
    }
    
    private static GroupLayout.SequentialGroup buildVerticalGroup(GroupLayout l,
            IndexInfo[] infos) {
        GroupLayout.SequentialGroup g = l.createSequentialGroup();
        g = g.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        int i = 0;
        for (IndexInfo info: infos) {
            if (i++ != 0)
                g = g.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
            
            g = g.addGroup(l
                    .createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(info.getName(),
                            GroupLayout.PREFERRED_SIZE,
                            15,
                            GroupLayout.PREFERRED_SIZE
                    )
                    .addComponent(info.getValue())
            );
        }
        g = g.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        return g;
    }
    
    static void linkSize(GroupLayout l, IndexInfo[] infos) {
        for (IndexInfo info: infos) l.linkSize(SwingConstants.VERTICAL,
                new Component[] {info.getValue(), info.getName()});
    }

    /**
     * Domyślny konstruktor.
     */
    public IndexInfoPanel() {
        this(new IndexInfo[]{
            new IndexInfo("Nazwisko"), new IndexInfo("Imię"),
            new IndexInfo("Nr aktu"), new IndexInfo("Rok")
        });
    }
    
    private IndexInfoPanel(IndexInfo[] infos) {
        this.infos = infos;
        super.setBorder(BorderFactory.createTitledBorder(""));
        GroupLayout l = new GroupLayout(this);
        super.setLayout(l);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(l.createSequentialGroup()
                .addContainerGap()
                .addGroup(buildHorizontalGroup(l, infos))
                .addContainerGap())
        );
        l.setVerticalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(buildVerticalGroup(l, infos))
        );

        linkSize(l, infos);
    }

    /**
     * @return pole z informacjami o nazwisku
     */
    public IndexInfo getLastNameInfo() {
        return infos[0];
    }

    /**
     * @return pole z informacjami o imieniu/imionach
     */
    public IndexInfo getNameInfo() {
        return infos[1];
    }

    /**
     * @return pole z informacjami o numerze aktu
     */
    public IndexInfo getActNumberInfo() {
        return infos[2];
    }

    /**
     * @return pole z informacjami o roku
     */
    public IndexInfo getYearInfo() {
        return infos[3];
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
        getActNumberInfo().getValue().setText(act);
        getLastNameInfo().getValue().setText(lastName);
        getNameInfo().getValue().setText(name);
        getYearInfo().getValue().setText(year);
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
    private final IndexInfo[] infos;
    // End of variables declaration
}
