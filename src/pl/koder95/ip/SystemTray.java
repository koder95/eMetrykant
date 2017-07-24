/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import pl.koder95.ip.idf.Indices;

/**
 * Klasa zarządza ikoną zasobnika systemu.
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
public class SystemTray {
    
    private Image image;
    private String title;
    private PopupMenu popup;
    private TrayIcon trayIcon;
    
    /**
     * Inicjuje i dodaje ikonę do zasobnika systemu. Ikona umożliwia rozwinięcie
     * pięciu pozycji menu: cztery odnośniki do indeksów parafialnych oraz
     * zamykanie.
     */
    public void start() {
        image = Main.FAVICON;
        title = "Indeksy parafialne";
        popup = new PopupMenu("Indeksy parafialne");
        
        createMenuItemForAllIndices();
        popup.addSeparator();
        MenuItem action = new MenuItem("Zamknij");
        action.addActionListener((e)-> System.exit(0));
        popup.add(action);
        
        trayIcon = new TrayIcon(image, title, popup);
        show();
    }
    
    /**
     * Pokazuje w zasobniku ikonę. Ta metoda służy do przywracania wcześniej
     * schowanej ikony, dlatego nie powinna być wywoływana zaraz po metodzie
     * {@link SystemTray#start() start()} (gdy po niej nie było wywołania
     * {@link SystemTray#stop() stop()}) albo bez wcześniejszego wywołania
     * metody {@link SystemTray#start() start()}.
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
    public void stop() {
        java.awt.SystemTray.getSystemTray().remove(trayIcon);
    }
    
    private void createMenuItemForAllIndices() {
        for (Indices indices: Indices.values()) {
            MenuItem action = new MenuItem(indices.getName());
            action.addActionListener((e)-> service(indices));
            popup.add(action);
        }
    }
    
    private void service(Indices indices) {
        new IndexBrowserFrame(indices).setVisible(true);
    }
}
