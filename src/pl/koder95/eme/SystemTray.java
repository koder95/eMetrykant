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
package pl.koder95.eme;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import pl.koder95.eme.gui.GUIMediator;
import pl.koder95.eme.idf.Indices;

/**
 * Klasa zarządza ikoną zasobnika systemu.
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.10, 2018-03-18
 * @since 0.0.201
 */
public class SystemTray extends AbstractDefaultLaunch {
    
    private Image image;
    private String title;
    private PopupMenu popup;
    private TrayIcon trayIcon;
    
    /**
     * Inicjuje ikonę. Ikona umożliwia rozwinięcie pięciu pozycji menu:
     * cztery odnośniki do indeksów parafialnych oraz zamykanie.
     */
    public void init() {
        image = Main.FAVICON16;
        title = "eMetrykant";
        popup = new PopupMenu("eMetrykant");
        
        createMenuItemForAllIndices();
        popup.addSeparator();
        MenuItem action = new MenuItem("Zamknij");
        action.addActionListener((e)-> System.exit(0));
        popup.add(action);
        
        trayIcon = new TrayIcon(image, title, popup);
    }
    
    /**
     * Pokazuje w zasobniku ikonę. Ta metoda służy do przywracania wcześniej
     * schowanej ikony, dlatego nie powinna być wywoływana zaraz po metodzie
     * {@link SystemTray#init() start()} (gdy po niej nie było wywołania
     * {@link SystemTray#hide() hide()}) albo bez wcześniejszego wywołania
     * metody {@link SystemTray#hide() hide()}.
     */
    public void show() {
        try {
            java.awt.SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException ex) {
            System.err.println(ex);
        }
    }
    
    /**
     * Usuwa ikonę z zasobnika.
     */
    public void hide() {
        java.awt.SystemTray.getSystemTray().remove(trayIcon);
    }
    
    /**
     * Generuje pozycje menu dla każdego zbioru indeksów i dodaje je do listy
     * rozwijanej.
     */
    private void createMenuItemForAllIndices() {
        for (Indices indices: Indices.values()) {
            MenuItem action = new MenuItem(indices.getName());
            action.addActionListener((e)-> service(indices));
            popup.add(action);
        }
    }
    
    /**
     * Obsługuje {@link Indices zbiór indeksów} przez pokazanie
     * odpowiedniej ramki.
     * @param indices zbiór indeksów
     */
    private void service(Indices indices) {
        new GUIMediator(indices).showFrame();
    }

    @Override
    public void launch(String[] args) {
        this.init();
        this.show();
    }
}
