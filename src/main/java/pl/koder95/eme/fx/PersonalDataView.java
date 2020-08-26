package pl.koder95.eme.fx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import pl.koder95.eme.core.CabinetWorkers;
import pl.koder95.eme.core.spi.CabinetAnalyzer;
import pl.koder95.eme.core.spi.PersonalDataModel;

import java.net.URL;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;

/**
 * Kontroler dla widoku modelu danych osobowych.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.1.11
 */
public class PersonalDataView implements Initializable {

    public static final String ANALYZER_KEY = "analyzer";

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
        if (resources == null) {
            resources = new ListResourceBundle() {
                @Override
                protected Object[][] getContents() {
                    return new Object[][] {
                            { ANALYZER_KEY, CabinetWorkers.get(CabinetAnalyzer.class) }
                    };
                }
            };
        }
        if (resources.containsKey(ANALYZER_KEY)) {
            Object obj = resources.getObject(ANALYZER_KEY);
            if (obj instanceof CabinetAnalyzer) {
                CabinetAnalyzer analyzer = (CabinetAnalyzer) obj;
                if (searching instanceof TextField) {
                    TextField field = (TextField) searching;
                    AutoCompletionBinding<PersonalDataModel> autoCompletionBinding = TextFields.bindAutoCompletion(
                            field,
                            analyzer.getSuggestionProvider(),
                            analyzer.getPersonalDataConverter()
                    );
                    autoCompletionBinding.setOnAutoCompleted(event -> setPersonalDataModel(event.getCompletion()));
                    field.setOnAction(event -> setPersonalDataModel(
                            analyzer.getPersonalDataConverter().fromString(field.getText())
                    ));
                    field.textProperty().addListener(
                            (observable, oldValue, newValue) -> {
                                if (oldValue.length() < newValue.length()) {
                                    field.setText(newValue.toUpperCase());
                                }
                            }
                    );
                }
                numberOfActs.setText(analyzer.getNumberOfActs() + "");
            }
        }
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
