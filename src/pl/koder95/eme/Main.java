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

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Klasa uruchamiająca i inicjalizująca podstawowe elementy aplikacji.
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.0.201
 */
public class Main implements LaunchMethod {
    /**
     * Domyślny pakiet językowy.
     */
    public static final ResourceBundle BUNDLE
            = ResourceBundle.getBundle("pl/koder95/eme/strings");
    /**
     * Używana stała dla Polski.
     */
    public static final Locale POLISH = Locale.forLanguageTag("PL-pl"); //NOI18N
    /**
     * Folder, gdzie znajdują się pliki programu.
     */
    public static final File WORKDIR = Files.WORKDIR;
    /**
     * Folder, gdzie znajdują się pliki zawierające dane do wczytania
     * przez program.
     */
    public static final File DATA_DIR = Files.DATA_DIR;
    /**
     * Domyślne kodowanie dla pliku CSV.
     */
    public static final Charset CSV_DEFAULT_CHARSET = Charset.forName("UTF-8");
    /**
     * Domyślny sposób porównywania stringów (polski).
     */
    public static final Collator DEFAULT_COLLATOR
            = Collator.getInstance(POLISH); //NOI18N
    private static final String FAV_PATH_START = "pl/koder95/eme/favicon";
    /**
     * Ikona dla okienek.
     */
    public static final Image FAVICON = Toolkit.getDefaultToolkit()
           .createImage(ClassLoader.getSystemResource(FAV_PATH_START + ".png"));
    /**
     * Ikona dla zasobnika systemowego w rozmiarze 16x16.
     */
    public static final Image FAVICON16 = Toolkit.getDefaultToolkit()
         .createImage(ClassLoader.getSystemResource(FAV_PATH_START + "16.png"));
    /**
     * Ikona dla zasobnika systemowego w rozmiarze 24x24.
     */
    public static final Image FAVICON24 = Toolkit.getDefaultToolkit()
         .createImage(ClassLoader.getSystemResource(FAV_PATH_START + "24.png"));
    /**
     * Komunikat błędu braku danych, które powinny zostać wczytane.
     */
    public static final Object READ_DATA_ERR_MESSAGE
            = BUNDLE.getString("ERR_IMPORTANT_FILE_NOT_FOUND");
    /**
     * Tytuł komunikatu błędu braku danych, które powinny zostać wczytane.
     */
    public static final String READ_DATA_ERR_TITLE
            = BUNDLE.getString("ERR_IMPORTANT_FILE_NOT_FOUND_TITLE");
    /**
     * Wzór identyfikujący liczby w stringu.
     */
    public static final Pattern DIGITS_STRING_PATTERN
            = Pattern.compile("([0-9]*)");

    /**
     * Uruchamia program.
     * 
     * @param args jeśli tablica jest pusta, uruchamia program standardowo;
     * jeśli tablica zawiera {@code "-c"}, zostanie uruchomiony konwerter plików
     * CSV na XML
     */
    public static void main(String[] args) {
        LinkedList<String> arg = new LinkedList<>(Arrays.asList(args));
        Main instance = new Main(Files.CSV_DIR, Files.XML_DIR, "indices.xml");
        if (args == null || args.length == 0) {
            if (Files.XML_DIR != null || Files.XML_DIR.list() != null)
                instance.setNextLaunchMethod(new AbstractDefaultLaunch() {
                    @Override
                    public void launch(List<String> args) {
                        pl.koder95.eme.fx.Main.main(args);
                    }
                });
        }
        instance.launch(arg);
    }
    
    /**
     * Odtwarza sygnał dźwiękowy błędu i wyświetla o nim komunikat, generując
     * status.
     * 
     * @param parentComponent komponent-rodzic
     * @param message komunikat
     * @param title tytuł komunikatu
     * @return status (patrz: {@link System#exit(int) exit status})
     */
    public static int showErrorMessage(java.awt.Component parentComponent,
            Object message, String title) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        javax.swing.JOptionPane.showMessageDialog(parentComponent, message,
                title, javax.swing.JOptionPane.ERROR_MESSAGE);
        return -1;
    }
    
    /**
     * Pokazuje wiadomość o błędzie w formie okienka dialogowego. Wywoływana
     * jest metoda {@link #showErrorMessage(java.awt.Component,
     * java.lang.Object, java.lang.String)} i jeśli {@code exit == true} to
     * zamykana jest aplikacja ze statusem zwróconym przez wywołaną metodę.
     * 
     * @param parentComponent komponent-rodzic
     * @param message komunikat
     * @param title tytuł komunikatu
     * @param exit jeśli {@code true} to po zamknięciu komunikatu, zamknie się
     * również aplikacja
     */
    public static void showErrorMessage(java.awt.Component parentComponent,
            Object message, String title, boolean exit) {
        int status = showErrorMessage(parentComponent, message, title);
        if (exit) System.exit(status);
    }
    
    /**
     * Uproszczona wersja metody {@link #showErrorMessage(java.awt.Component,
     * java.lang.Object, java.lang.String)}.
     * Metoda wywołuje {@link #showErrorMessage(java.awt.Component,
     * java.lang.Object, java.lang.String)} z pierwszym argumentem równym
     * {@code null}.
     * 
     * @param message komunikat
     * @param title tytuł komunikatu
     * @return status (patrz: {@link System#exit(int) exit status})
     */
    public static int showErrorMessage(Object message, String title) {
        return showErrorMessage(null, message, title);
    }
    
    /**
     * Pokazuje wiadomość o błędzie w formie okienka dialogowego. Wywoływana
     * jest metoda {@link #showErrorMessage(java.lang.Object, java.lang.String)}
     * i jeśli {@code exit == true} to zamykana jest aplikacja ze statusem
     * zwróconym przez wywołaną metodę.
     * 
     * @param message komunikat
     * @param title tytuł komunikatu
     * @param exit jeśli {@code true} to po zamknięciu komunikatu, zamknie się
     * również aplikacja
     */
    public static void showErrorMessage(Object message, String title,
            boolean exit) {
        int status = showErrorMessage(message, title);
        if (exit) System.exit(status);
    }
    
    /**
     * Ustawia systemowy wygląd interfejsu użytkownika.
     * 
     * @throws ClassNotFoundException jeśli nie znaleziono wyglądu
     * @throws InstantiationException jeśli nowa instancja wyglądu nie może
     * zostać utworzona
     * @throws IllegalAccessException jeśli odmówiono dostępu
     * @throws UnsupportedLookAndFeelException jeśli system nie wspiera
     * możliwości zmiany wyglądu
     * @see UIManager#setLookAndFeel(java.lang.String) 
     */
    public static void setSystemLookAndFeel() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    
    /**
     * @since 0.0.203
     * @see MemoryUtils#releaseMemory()
     */
    public static void releaseMemory() {
        MemoryUtils.releaseMemory();
    }
    
    private LaunchMethod next;

    public Main(File csvDir, File xmlDir, String xmlFileName) {
        this.next = ConverterCSV.create(csvDir, xmlDir, xmlFileName);
    }

    @Override
    public void launch(List<String> args) {
        LinkedList<String> argL = new LinkedList<>();
        argL.addAll(Files.createNotExistDirs(args));
        try {
            argL.addAll(Files.createNotExistFiles(args));
        } catch (IOException ex) {
            showErrorMessage(BUNDLE.getString("ERR_CANNOT_CREATE_NEW_FILE"),
                    BUNDLE.getString("ERR_CANNOT_CREATE_NEW_FILE_TITLE"));
        }
        try {
            setSystemLookAndFeel();
        } catch (ClassNotFoundException | InstantiationException 
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            showErrorMessage(ex.getLocalizedMessage(),
                    BUNDLE.getString("ERR_GUI_TITLE"), true);
        } finally {
            nextMethod().launch(argL);
        }
    }

    @Override
    public void setNextLaunchMethod(LaunchMethod next) {
        this.next = next;
    }

    @Override
    public LaunchMethod nextMethod() {
        return next;
    }
}
