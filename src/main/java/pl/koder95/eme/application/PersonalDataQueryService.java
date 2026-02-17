package pl.koder95.eme.application;

import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.koder95.eme.core.AbstractCabinetWorker;
import pl.koder95.eme.core.IndexListDataSource;
import pl.koder95.eme.core.spi.CabinetAnalyzer;
import pl.koder95.eme.core.spi.PersonalDataModel;

import java.util.Collection;

import static org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;

/**
 * Serwis aplikacyjny odpowiedzialny za wyszukiwanie i mapowanie danych osobowych.
 */
public class PersonalDataQueryService {

    private static final PersonalDataPresentation EMPTY_DATA = new PersonalDataPresentation(
            "-", "", "-", "-", "-", "-"
    );

    private final CabinetAnalyzer analyzer;

    public PersonalDataQueryService(CabinetAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Callback<ISuggestionRequest, Collection<PersonalDataModel>> getSuggestionProvider() {
        return analyzer.getSuggestionProvider();
    }

    public StringConverter<PersonalDataModel> getPersonalDataConverter() {
        return analyzer.getPersonalDataConverter();
    }

    public void reloadAnalyzer() {
        if (analyzer instanceof AbstractCabinetWorker worker) {
            worker.setDataSource(new IndexListDataSource());
        }
        analyzer.load();
    }

    public int getNumberOfActs() {
        return analyzer.getNumberOfActs();
    }

    public PersonalDataModel fromText(String text) {
        return analyzer.getPersonalDataConverter().fromString(text);
    }

    public PersonalDataPresentation toPresentation(PersonalDataModel model) {
        if (model == null) {
            return EMPTY_DATA;
        }
        return new PersonalDataPresentation(
                model.getSurname(),
                model.getName(),
                model.getBaptismAN(),
                model.getConfirmationAN(),
                model.getMarriageAN(),
                model.getDeceaseAN()
        );
    }
}
