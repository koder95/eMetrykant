package pl.koder95.eme.fx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import pl.koder95.eme.core.CabinetWorkers;
import pl.koder95.eme.core.spi.CabinetAnalyzer;
import pl.koder95.eme.core.spi.PersonalDataModel;
import pl.koder95.eme.dfs.IndexList;

import java.net.URL;
import java.util.ResourceBundle;

import static pl.koder95.eme.Main.BUNDLE;

/**
 * Kontroler dla widoku modelu danych osobowych.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.2, 2020-11-18
 * @since 0.1.11
 */
public class PersonalDataView implements Initializable {

    @FXML
    private BorderPane main;
    @FXML
    private Label personalData;

    @FXML
    private Label baptism;
    @FXML
    private Label confirmation;
    @FXML
    private Label marriage;
    @FXML
    private Label decease;

    @FXML
    private Label numberOfActs;

    @FXML
    private TextField searching;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        installCabinetAnalyzer(CabinetWorkers.get(CabinetAnalyzer.class));
    }

    private void installCabinetAnalyzer(CabinetAnalyzer analyzer) {
        setupAutoCompletion(analyzer);
        setupInputActions(analyzer);
        numberOfActs.setText(analyzer.getNumberOfActs() + "");
    }

    private void setupInputActions(CabinetAnalyzer analyzer) {
        searching.setOnAction(event -> setPersonalDataModel(
                analyzer.getPersonalDataConverter().fromString(searching.getText())
        ));
        searching.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (oldValue.length() < newValue.length()) {
                        searching.setText(newValue.toUpperCase());
                    }
                }
        );
    }

    private void setupAutoCompletion(CabinetAnalyzer analyzer) {
        AutoCompletionBinding<PersonalDataModel> binding = TextFields.bindAutoCompletion(
                searching,
                analyzer.getSuggestionProvider(),
                analyzer.getPersonalDataConverter()
        );
        binding.setOnAutoCompleted(event -> setPersonalDataModel(event.getCompletion()));
    }

    private void setPersonalDataModel(PersonalDataModel model) {
        if (model != null) {
            String personalData = model.getSurname().toUpperCase();
            if (!model.getName().isEmpty()) {
                personalData += " " + model.getName();
            }
            this.personalData.setText(personalData);
            this.baptism.setText(model.getBaptismAN());
            this.confirmation.setText(model.getConfirmationAN());
            this.marriage.setText(model.getMarriageAN());
            this.decease.setText(model.getDeceaseAN());
        } else setPersonalDataModel(EMPTY_DATA);
    }

    public void close(ActionEvent actionEvent) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        Scene scene = main.getScene();
        if (scene != null) {
            a.initOwner(scene.getWindow());
        }
        a.setTitle(BUNDLE.getString("ALERT_CONFIRM_CLOSE_TITLE"));
        a.setHeaderText(BUNDLE.getString("ALERT_CONFIRM_CLOSE_HEADER"));
        a.setContentText(BUNDLE.getString("ALERT_CONFIRM_CLOSE_CONTENT"));
        a.showAndWait();
        if (a.getResult() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public void reload() {
        Scene scene = main.getScene();
        if (scene != null) {
            ProgressDialog.start();
        }
    }

    private static final PersonalDataModel EMPTY_DATA = new PersonalDataModel() {
        @Override
        public String getSurname() {
            return "-";
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getBaptismAN() {
            return "-";
        }

        @Override
        public String getConfirmationAN() {
            return "-";
        }

        @Override
        public String getMarriageAN() {
            return "-";
        }

        @Override
        public String getDeceaseAN() {
            return "-";
        }
    };
}
