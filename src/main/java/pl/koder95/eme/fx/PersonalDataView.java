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
import pl.koder95.eme.application.PersonalDataPresentation;
import pl.koder95.eme.application.PersonalDataQueryService;
import pl.koder95.eme.core.spi.PersonalDataModel;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Kontroler dla widoku modelu danych osobowych.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.2, 2020-11-18
 * @since 0.1.11
 */
public class PersonalDataView implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(PersonalDataView.class.getName());

    private final PersonalDataQueryService personalDataQueryService;
    private final IndexReloadService indexReloadService;
    private final AppCloseService appCloseService;
    private final DataManagementViewFactory dataManagementViewFactory;
    private final FxDialogs dialogs;
    private final ResourceBundle bundle;

    public PersonalDataView(PersonalDataQueryService personalDataQueryService,
                            IndexReloadService indexReloadService,
                            AppCloseService appCloseService,
                            DataManagementViewFactory dataManagementViewFactory,
                            FxDialogs dialogs,
                            ResourceBundle bundle) {
        this.personalDataQueryService = Objects.requireNonNull(personalDataQueryService, "personalDataQueryService must not be null");
        this.indexReloadService = Objects.requireNonNull(indexReloadService, "indexReloadService must not be null");
        this.appCloseService = Objects.requireNonNull(appCloseService, "appCloseService must not be null");
        this.dataManagementViewFactory = Objects.requireNonNull(dataManagementViewFactory, "dataManagementViewFactory must not be null");
        this.dialogs = Objects.requireNonNull(dialogs, "dialogs must not be null");
        this.bundle = Objects.requireNonNull(bundle, "bundle must not be null");
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
    private TextField searching;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        installCabinetAnalyzer();
    }

    private void installCabinetAnalyzer() {
        setupAutoCompletion();
        setupInputActions();
        numberOfActs.setText(String.valueOf(personalDataQueryService.getNumberOfActs()));
    }

    private void setupAutoCompletion() {
        AutoCompletionBinding<PersonalDataModel> binding = TextFields.bindAutoCompletion(
                searching,
                personalDataQueryService.getSuggestionProvider(),
                personalDataQueryService.getPersonalDataConverter()
        );
        binding.setOnAutoCompleted(event -> setPersonalDataModel(event.getCompletion()));
    }

    private void setupInputActions() {
        searching.setOnAction(event -> setPersonalDataModel(
                personalDataQueryService.getPersonalDataConverter().fromString(searching.getText())
        ));
        searching.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (oldValue.length() < newValue.length()) {
                        searching.setText(newValue.toUpperCase());
                    }
                }
        );
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
     * Otwiera okno zarządzania danymi, a po jego zamknięciu odświeża widok,
     * jeżeli dane zostały zmienione.
     */
    public void manage(ActionEvent actionEvent) {
        Scene scene = main.getScene();
        try {
            if (dataManagementViewFactory.showAndWait(scene == null ? null : scene.getWindow())) {
                refreshAfterDataChange();
            }
        } catch (IOException | RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Błąd podczas zarządzania danymi", ex);
            dialogs.createErrorAlert(
                    scene,
                    bundle.getString("ALERT_MANAGE_ERROR_TITLE"),
                    bundle.getString("ALERT_MANAGE_ERROR_HEADER"),
                    ex.getMessage() == null ? ex.toString() : ex.getMessage()
            ).showAndWait();
        }
    }

    private void refreshAfterDataChange() {
        personalDataQueryService.reloadAnalyzer();
        numberOfActs.setText(String.valueOf(personalDataQueryService.getNumberOfActs()));
        if (searching instanceof TextField field) {
            setPersonalDataModel(personalDataQueryService.fromText(field.getText()));
        }
    }

    /**
     * Ponownie wczytuje dane indeksów i odświeża licznik aktów.
     */
    public void reload() {
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
                    LOGGER.log(Level.SEVERE, "Błąd podczas przeładowania indeksów", ex);
                } finally {
                    Exception finalReloadException = reloadException;
                    Platform.runLater(() -> {
                        if (finalReloadException != null) {
                            dialogs.createErrorAlert(
                                    scene,
                                    bundle.getString("ALERT_RELOAD_ERROR_TITLE"),
                                    bundle.getString("ALERT_RELOAD_ERROR_HEADER"),
                                    finalReloadException.getMessage() != null
                                            ? finalReloadException.getMessage()
                                            : finalReloadException.toString()
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
            dialog.setOnShown(event -> thread.start());
            dialog.show();
        }
    }
}
