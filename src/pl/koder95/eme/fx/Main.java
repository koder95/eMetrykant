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
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.koder95.eme.LaunchMethod;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(FXMLLoader.getDefaultClassLoader()
                    .getResource("pl/koder95/eme/fx/Main.fxml"));
        } catch (IOException ex) {
            System.err.println("ERROR 46 + " + ex.getLocalizedMessage());
        }

        primaryStage.setTitle("eMetrykant pre-alpha");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    public static void main(List<String> args) {
        Main.launch(args.toArray(new String[args.size()]));
    }
}
