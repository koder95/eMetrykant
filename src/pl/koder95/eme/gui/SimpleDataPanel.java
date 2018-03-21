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

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import pl.koder95.eme.dfs.Index;

/**
 * Panel wyświetlający dane indeksu.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.1.11
 */
public class SimpleDataPanel extends DataPanel {

    private static final long serialVersionUID = 1578360541569667467L;
    
    private Map<String, String> names = new HashMap<>();
    /**
     * Interfejs potrzebny do wyświetlania danych.
     */
    protected DataShowing showing;

    /**
     * Tworzy panel, który przy pomocy interfejsu {@link DataShowing} wyświetli
     * wybrane dane. Mapa nazw służy zamianie nazw systemowych
     * na nazwy wyświetlane.
     * 
     * @param showing interfejs wyświetlający dane
     * @param names mapa nazw, którym przyporządkowane są etykiety
     */
    public SimpleDataPanel(DataShowing showing, Map<String, String> names) {
        this.showing = showing;
        this.names = names;
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.CENTER, (Component) showing);
    }

    @Override
    public void showData(Index i, Queue<String> info) {
        System.out.println("showData");
        if (i == null) reset();
        else if (info != null && !info.isEmpty()) {
            while(!info.isEmpty()) {
                String is = info.poll();
                if (i.getDataNames().contains(is)) {
                    String label = names.getOrDefault(is, is);
                    showing.addData(label, i.getData(is));
                }
            }
            ((Component) showing).revalidate();
            validate();
        }
    }

    @Override
    public void reset() {
        names.keySet().iterator().forEachRemaining((name)->showing.reset(name));
    }
}
