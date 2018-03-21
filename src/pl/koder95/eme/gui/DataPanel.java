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

import java.util.Queue;
import javax.swing.JPanel;
import pl.koder95.eme.dfs.Index;

/**
 * Panel, który ma wyświetlać dane pobrane z indeksu.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.1.11
 */
public abstract class DataPanel extends JPanel {

    private static final long serialVersionUID = -2952779491434639106L;

    /**
     * Kieruje żądanie wyświetlenia danych indeksu w kolejności ustalonej przez
     * kolejkę.
     * 
     * @param i indeks, którego dane mają zostać wyświetlone
     * @param info kolejka nazw danych
     */
    public abstract void showData(Index i, Queue<String> info);
    
    /**
     * Kieruje żądanie wyświetlenia początkowego stanu panela.
     */
    public abstract void reset();
}
