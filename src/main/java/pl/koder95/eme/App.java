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

import static pl.koder95.eme.Main.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.koder95.eme.au.SelfUpdateTask;
import pl.koder95.eme.bootstrap.AppConfig;
import pl.koder95.eme.bootstrap.ApplicationContext;
import pl.koder95.eme.application.AppCloseService;
import pl.koder95.eme.application.IndexReloadService;
import pl.koder95.eme.application.PersonalDataQueryService;
import pl.koder95.eme.dfs.IndexList;
import pl.koder95.eme.fx.FxDialogs;
import pl.koder95.eme.fx.PersonalDataView;
import pl.koder95.eme.git.RepositoryInfo;

import java.net.URL;
import java.util.Arrays;

public class App extends Application {

    private Parent root = null;
    private SelfUpdateTask task = null;
    private ApplicationContext applicationContext = null;
    
    @Override
    public void init() throws Exception {
        super.init();
        task = new SelfUpdateTask();
        if (getParameters().getUnnamed().isEmpty()) {
            Arrays.stream(IndexList.values()).forEach(IndexList::load);
        }

        applicationContext = new ApplicationContext();
        applicationContext.initialize();

        PersonalDataQueryService personalDataQueryService = applicationContext.getPersonalDataQueryService();
        IndexReloadService indexReloadService = applicationContext.getIndexReloadService();
        AppCloseService appCloseService = applicationContext.getAppCloseService();
        FxDialogs dialogs = applicationContext.getDialogs();
        AppConfig appConfig = applicationContext.getAppConfig();

        String resource = "PersonalDataView.fxml";
        URL url = PersonalDataView.class.getResource(resource);
        FXMLLoader loader = new FXMLLoader(url, appConfig.bundle());
        loader.setControllerFactory(type -> {
            if (type == PersonalDataView.class) {
                return new PersonalDataView(personalDataQueryService, indexReloadService, appCloseService, dialogs);
            }
            throw new IllegalArgumentException("Nieobsługiwany kontroler FXML: " + type.getName());
        });
        root = loader.load();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(FAVICON_PATH));
        primaryStage.setTitle("eMetrykant " + Version.get());
        Version latestRelease = RepositoryInfo.get().getLatestReleaseVersion();
        if (latestRelease != null &&
                (latestRelease.compareTo(Version.get()) > 0 || !getParameters().getUnnamed().isEmpty())) {
            ProgressBar updating = new ProgressBar();
            Label title = new Label();
            Label message = new Label();
            VBox root = new VBox(5d, title, updating, message);

            updating.setMinSize(400d, 35d);
            updating.progressProperty().bind(task.progressProperty());
            title.textProperty().bind(task.titleProperty());
            message.textProperty().bind(task.messageProperty());

            root.setAlignment(Pos.CENTER);

            primaryStage.initStyle(StageStyle.UTILITY);
            primaryStage.setScene(new Scene(root));
            primaryStage.setAlwaysOnTop(true);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(100);
            primaryStage.show();
            new Thread(task).start();
        }
        else {
            primaryStage.setScene(new Scene(root));
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            primaryStage.show();
        }
    }
}
