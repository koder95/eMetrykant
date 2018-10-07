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
package pl.koder95.eme;

import com.sun.javafx.application.LauncherImpl;
import java.awt.Toolkit;
import java.io.File;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pl.koder95.eme.fx.Preloader;

/**
 * Klasa uruchamiająca i inicjalizująca podstawowe elementy aplikacji.
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.0, 2018-10-07
 * @since 0.0.201
 */
public class Main extends Application {
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
     * Ścieżka ikony dla okienek.
     */
    public static final String FAVICON_PATH = FAV_PATH_START + ".png";
    /**
     * Ikona dla okienek.
     */
    public static final java.awt.Image FAVICON = Toolkit.getDefaultToolkit()
           .createImage(ClassLoader.getSystemResource(FAV_PATH_START + ".png"));
    /**
     * Ikona dla zasobnika systemowego w rozmiarze 16x16.
     */
    public static final java.awt.Image FAVICON16 = Toolkit.getDefaultToolkit()
         .createImage(ClassLoader.getSystemResource(FAV_PATH_START + "16.png"));
    /**
     * Ikona dla zasobnika systemowego w rozmiarze 24x24.
     */
    public static final java.awt.Image FAVICON24 = Toolkit.getDefaultToolkit()
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

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, Preloader.class, args);
    }
    
    /**
     * @since 0.0.203
     * @see MemoryUtils#releaseMemory()
     */
    public static void releaseMemory() {
        MemoryUtils.releaseMemory();
    }

    private Parent root = null;
    
    @Override
    public void init() throws Exception {
        super.init();
        notifyPreloader(new Preloader.ProgressNotification(0));
        root = FXMLLoader.load(FXMLLoader.getDefaultClassLoader()
                .getResource("pl/koder95/eme/fx/Main.fxml"));
        System.out.println(System.currentTimeMillis());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(FAVICON_PATH));
        primaryStage.setTitle("eMetrykant " + Version.get());
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.show();
    }
}
