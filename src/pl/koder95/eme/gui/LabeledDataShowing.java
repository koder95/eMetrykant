/*
 * Copyright (C) 2018 Kamil Jan Mularski [@koder95]
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

/**
 * Panel, który zdolny jest wyświetlać dane indeksu w sposób etykietowy
 * ({@code etykieta: wartość}).
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.1.11
 */
public class LabeledDataShowing extends JPanel implements DataShowing {

    private static final long serialVersionUID = 7560527702098730891L;
    private final List<IndexInfo> infos = new ArrayList<>();

    /**
     * Tworzy nowy panel dodając do niego informacje.
     * 
     * @param infos kolekcja informacji
     */
    public LabeledDataShowing(Collection<IndexInfo> infos) {
        infos.addAll(infos);
    }

    /**
     * Tworzy nowy panel dodając do niego informacje.
     * 
     * @param infos tablica informacji
     */
    public LabeledDataShowing(IndexInfo[] infos) {
        this(Arrays.asList(infos));
    }

    /**
     * Tworzy nowy pusty panel.
     */
    public LabeledDataShowing() {
    }

    @Override
    public void addData(String name, String value) {
        IndexInfo info = getInfo(name);
        if (info == null) {
            info = new IndexInfo(name);
            infos.add(info);
        }
        info.getValue().setText(value);
    }

    @Override
    public void removeData(String name) {
        infos.removeIf((IndexInfo info)->{
            return info.getName().getText().equalsIgnoreCase(name);
        });
    }
    
    /**
     * @param name nazwa wyświetlana pola
     * @return informacje wyświetlane w postacji {@code etykieta: wartość}
     */
    public IndexInfo getInfo(String name) {
        for (IndexInfo info: infos)
            if (info.getName().getText().equalsIgnoreCase(name.concat(":")))
                return info;
        return null;
    }

    @Override
    public void doLayout() {
        super.doLayout();
        System.out.println("doLayout");
        layoutData();
    }

    private void layoutData() {
        System.out.println("layoutData");
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

    @Override
    public void reset(String name) {
        IndexInfo info = getInfo(name);
        if (info != null) info.getValue().setText("-");
    }
    
    private static void linkSize(GroupLayout l, List<IndexInfo> infos) {
        infos.stream().forEach((info) -> {
            l.linkSize(SwingConstants.VERTICAL, new Component[] {
                info.getName(), info.getValue()
            });
        });
    }
    
    private static GroupLayout.ParallelGroup buildHorizontalGroup(GroupLayout l,
            List<IndexInfo> infos) {
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
            List<IndexInfo> infos) {
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
}
