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
package pl.koder95.eme.fx;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Klasa Main, która rozszerza klasę Application.
 *
 * <p><b>Cykl życia Application</b></p>
 * <p>
 * Punktem wejścia dla aplikacji JavaFX jest klasa Application, którą właśnie
 * rozszerza klasa Main. Środowisko wykonawcze JavaFX wykonuje następujące
 * czynności po uruchomieniu aplikacji:
 * </p>
 * <ol>
 * <li>Tworzy instancję określonej klasy Application</li>
 * <li>Wywołuje metodę {@link #init}</li>
 * <li>Wywołuje metodę {@link #start}</li>
 * <li>Czeka na zakończenie aplikacji, które zdarza się gdy występuje jedno
 * z poniższych:
 * <ul>
 * <li>aplikacja wywołuje {@link Platform#exit}</li>
 * <li>ostatnie okno zostało zamknięte i atrybut {@code implicitExit}
 * w {@code Platform} jest prawdziwy</li>
 * </ul></li>
 * <li>Wywołuje metodę {@link #stop}</li>
 * </ol>
 * <p>Należy zauważyć, że metoda {@code start} jest abstrakcyjna i musi być
 * nadpisana.
 * Metody {@code init} i {@code stop} mają konkretne implementacje, które nic
 * nie robią.</p>
 *
 * <p>Wywoływanie {@link Platform#exit} jest preferowanym sposobem jawnego
 * zakończenia aplikacji JavaFX. Bezpośrednie wywoływanie {@link System#exit}
 * jest akceptowaną alternatywą, ale nie pozwala metodzie {@link #stop}
 * klasy Application zostać wywołaną.
 * </p>
 *
 * <p>Aplikacja JavaFX nie powinna próbować używać póżniej JavaFX po zakończeniu
 * zestawu narzędzi FX lub z ShutdownHook, co jest, po zwróceniu metody
 * {@link #stop} lub {@link System#exit}, która jest wywołana.
 * </p>
 *
 * <p><b>Parametry Application</b></p>
 * <p>
 * Parametry aplikacji są dostępne przez wywołanie metody {@link #getParameters}
 * od czasu wywołania metody {@link #init}.
 * </p>
 *
 * <p><b>Wątkowość Application</b></p>
 * <p>
 * JavaFX tworzy wątek aplikacji dla uruchomienia metody startu aplikacji,
 * przetwarzania zdarzeń wejścia, i uruchamiania linii czasu animacji. Tworzenie
 * {@link Scene} and {@link Stage} objektów JavaFX jak również modyfikowanie
 * operacji na scenie dla <em>żywych</em> obiektów (te obiekty już dołączone do
 * sceny) musi być zrobione w wątku aplikacji JavaFX.
 * </p>
 *
 * <p>
 * Program uruchamiający Java ładuje i inicjalizuje określoną klasę Application
 * do JavaFX Application Thread. Jeśli tam nie ma metody main w klasie
 * Application, lub jeśli metoda main wywołuje Application.launch(), wtedy
 * instancja Application jest tworzona w JavaFX Application Thread.
 * </p>
 *
 * <p>
 * Metoda {@code init} jest wywoływana w wątku uruchamiania programu,
 * nie w JavaFX Application Thread.
 * To znaczy, że aplikacji nie musi tworzyć w metodzie {@code init} obiektu
 * {@link Scene} lub {@link Stage}.
 * Aplikacja może stworzyć inne obiekty JavaFX w metodzie {@code init}.
 * </p>
 *
 * <p>
 * Wszystkie nieobsłużone wyjątki w wątku aplikacji JavaFX, które powodują 
 * All the unhandled exceptions on the JavaFX application thread that wystąpią
 * podczas wysyłania zdarzeń, uruchamiania linii czasu animacji, lub innego
 * kodu, są przekazywane do wątku
 * {@link java.lang.Thread.UncaughtExceptionHandler obsługi nieprzechwyconych
 * wątków}.
 * </p>
 *
 * <p><b>Charakterystyka Main</b></p>
 * <p>Klasa Main dostarcza implementacje metodzie
 * {@link Application#start(javafx.stage.Stage) start}. Najpierw dodawana jest
 * ikona do okienka, następnie ustawiany jest tytuł okna
 * i jego {@link Scene scena}. Na końcu następuje wywołanie metody
 * {@link Stage#show()}.
 * </p>
 * 
 * <p>Klasa Main nadpisuje również metodę {@link Application#init() init},
 * w której wczytywany jest interfejs aplikacji z pliku FXML.</p>
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.12-alt, 2018-08-04
 * @since 0.1.12-alt
 */
public class Main extends Application {

    private Parent root = null;
    
    @Override
    public void init() throws Exception {
        try {
            root = FXMLLoader.load(FXMLLoader.getDefaultClassLoader()
                    .getResource("pl/koder95/eme/fx/Main.fxml"));
        } catch (IOException ex) {
            System.err.println("ERROR 46 + " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(pl.koder95.eme.Main.FAVICON_PATH));
        primaryStage.setTitle("eMetrykant pre-alpha");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
