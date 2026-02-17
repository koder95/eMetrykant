package pl.koder95.eme.fx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import pl.koder95.eme.application.AppCloseService;
import pl.koder95.eme.application.IndexReloadService;
import pl.koder95.eme.application.PersonalDataQueryService;
import pl.koder95.eme.application.PersonalDataPresentation;
import pl.koder95.eme.core.spi.PersonalDataModel;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Kontroler dla widoku modelu danych osobowych.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.2, 2020-11-18
 * @since 0.1.11
 */
public class PersonalDataView implements Initializable {

    private final PersonalDataQueryService personalDataQueryService;
    private final IndexReloadService indexReloadService;
    private final AppCloseService appCloseService;
    private final FxDialogs dialogs;
    private final ResourceBundle bundle;

    public PersonalDataView(PersonalDataQueryService personalDataQueryService,
                            IndexReloadService indexReloadService,
                            AppCloseService appCloseService,
                            FxDialogs dialogs,
                            ResourceBundle bundle) {
        this.personalDataQueryService = personalDataQueryService;
        this.indexReloadService = indexReloadService;
        this.appCloseService = appCloseService;
        this.dialogs = dialogs;
        this.bundle = bundle;
    }

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
    private Object searching;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (searching instanceof TextField) {
            TextField field = (TextField) searching;
            AutoCompletionBinding<PersonalDataModel> autoCompletionBinding = TextFields.bindAutoCompletion(
                    field,
                    personalDataQueryService.getSuggestionProvider(),
                    personalDataQueryService.getPersonalDataConverter()
            );
            autoCompletionBinding.setOnAutoCompleted(event -> setPersonalDataModel(event.getCompletion()));
            field.setOnAction(event -> setPersonalDataModel(
                    personalDataQueryService.fromText(field.getText())
            ));
            field.textProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (oldValue.length() < newValue.length()) {
                            field.setText(newValue.toUpperCase());
                        }
                    }
            );
        }
        numberOfActs.setText(String.valueOf(personalDataQueryService.getNumberOfActs()));
    }

    private void setPersonalDataModel(PersonalDataModel model) {
        PersonalDataPresentation viewData = personalDataQueryService.toPresentation(model);
        this.personalData.setText(viewData.fullName());
        this.baptism.setText(viewData.getBaptismAN());
        this.confirmation.setText(viewData.getConfirmationAN());
        this.marriage.setText(viewData.getMarriageAN());
        this.decease.setText(viewData.getDeceaseAN());
    }

    /**
     * Obsługuje próbę zamknięcia aplikacji z potwierdzeniem.
     */
    public void close(ActionEvent actionEvent) {
        appCloseService.closeWithConfirmation(main.getScene());
    }

    /**
     * Ponownie wczytuje dane indeksów i odświeża licznik aktów.
     */
    public void reload(ActionEvent actionEvent) {
        Scene scene = main.getScene();
        if (scene != null) {
            Dialog<Boolean> dialog = dialogs.createProgressDialog(scene, bundle.getString("FX_RELOAD_PROGRESS_MESSAGE"));
            Thread thread = new Thread(() -> {
                Exception reloadException = null;
                try {
                    indexReloadService.reloadAll();
                    personalDataQueryService.reloadAnalyzer();
                } catch (Exception ex) {
                    reloadException = ex;
                    ex.printStackTrace();
                } finally {
                    Exception finalReloadException = reloadException;
                    Platform.runLater(() -> {
                        if (finalReloadException != null) {
                            dialogs.createErrorAlert(
                                    scene,
                                    bundle.getString("ALERT_RELOAD_ERROR_TITLE"),
                                    bundle.getString("ALERT_RELOAD_ERROR_HEADER"),
                                    finalReloadException.getMessage()
                            ).showAndWait();
                        } else {
                            numberOfActs.setText(String.valueOf(personalDataQueryService.getNumberOfActs()));
                        }
                        dialog.setResult(finalReloadException == null);
                        dialog.close();
                    });
                }
            });
            thread.setDaemon(true);
            dialog.setOnShown(event -> Platform.runLater(thread::start));
            dialog.show();
        }
    }
}
