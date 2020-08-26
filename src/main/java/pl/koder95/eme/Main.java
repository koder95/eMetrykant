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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pl.koder95.eme.core.*;
import pl.koder95.eme.core.spi.CabinetAnalyzer;
import pl.koder95.eme.core.spi.FilingCabinet;
import pl.koder95.eme.dfs.IndexList;

import java.awt.*;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Klasa uruchamiająca i inicjalizująca podstawowe elementy aplikacji.
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.0, 2020-08-26
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
     * Wzór identyfikujący liczby w stringu.
     */
    public static final Pattern DIGITS_STRING_PATTERN
            = Pattern.compile("([0-9]*)");

    public static void main(String[] args) {
        Main.launch(args);
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
        Arrays.stream(IndexList.values()).forEach(IndexList::load);

        FilingCabinet cabinet = new TreeFilingCabinet();
        IndexListDataSource source = new IndexListDataSource();

        SuggestionProvider suggestionProvider = new SuggestionProvider(cabinet);
        AbstractCabinetAnalyzer worker = new SimpleCabinetAnalyzer(cabinet, source, null, suggestionProvider);
        CabinetWorkers.register(CabinetAnalyzer.class, worker);
        worker.load();

        root = FXMLLoader.load(ClassLoader.getSystemResource("pl/koder95/eme/fx/PersonalDataView.fxml"));
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(FAVICON_PATH));
        primaryStage.setTitle("eMetrykant " + Version.get());
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}
