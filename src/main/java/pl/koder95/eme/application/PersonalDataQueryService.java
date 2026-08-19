package pl.koder95.eme.application;

import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.koder95.eme.core.IndexListDataSource;
import pl.koder95.eme.core.spi.CabinetAnalyzer;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.core.spi.PersonalDataModel;

import java.util.Collection;

import static org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;

/**
 * Serwis aplikacyjny odpowiedzialny za wyszukiwanie i mapowanie danych osobowych.
 */
@RequiredArgsConstructor
public class PersonalDataQueryService {

    private static final PersonalDataPresentation EMPTY_DATA = new PersonalDataPresentation(
            null, null, null, null, null, null
    );

    @NonNull
    private final CabinetAnalyzer analyzer;
    @NonNull
    private final IndexRepository indexRepository;

    public Callback<ISuggestionRequest, Collection<PersonalDataModel>> getSuggestionProvider() {
        return analyzer.getSuggestionProvider();
    }

    public StringConverter<PersonalDataModel> getPersonalDataConverter() {
        return analyzer.getPersonalDataConverter();
    }

    /**
     * Ponownie ładuje analizator na podstawie aktualnego źródła danych.
     *
     * <p>Przed wywołaniem tej metody należy wykonać
     * {@link IndexReloadService#reloadAll()}, aby repozytorium indeksów zostało
     * odświeżone. Metoda podmienia źródło analizatora na
     * {@link IndexListDataSource} i wywołuje {@link CabinetAnalyzer#load()}.</p>
     */
    public void reloadAnalyzer() {
        analyzer.setDataSource(new IndexListDataSource(indexRepository));
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
