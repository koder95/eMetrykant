package pl.koder95.eme.core;

import javafx.util.Callback;
import static org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;

import javafx.util.StringConverter;
import pl.koder95.eme.core.spi.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * <b>Ogólny analizator szafy</b> definiuje interfejs dostawcy sugestii oraz konwerter ({@link String} <=> {@link PersonalDataModel}).
 * Zlicza również ile zostało wczytanych aktów ze źródła.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public abstract class AbstractCabinetAnalyzer extends AbstractCabinetWorker implements CabinetAnalyzer {

    private final Callback<ISuggestionRequest, Collection<PersonalDataModel>> suggestionProvider;
    private final StringConverter<PersonalDataModel> converter;
    private float numberOfActs;

    /**
     * Tworzy obiekt określając wszystkie jego elementy.
     *
     * @param cabinet szafa aktowa
     * @param source źródło danych
     * @param target cel danych
     * @param suggestionProvider interfejs dostawcy sugestii
     * @param converter konwerter {@link String} <=> {@link PersonalDataModel}
     */
    public AbstractCabinetAnalyzer(FilingCabinet cabinet, DataSource source, DataTarget target,
                                   Callback<ISuggestionRequest, Collection<PersonalDataModel>> suggestionProvider,
                                   StringConverter<PersonalDataModel> converter) {
        super(cabinet, source, target);
        this.suggestionProvider = suggestionProvider;
        this.converter = converter;
    }

    /**
     * Tworzy obiekt określając konieczne jego elementy.
     *
     * @param cabinet szafa aktowa
     * @param suggestionProvider interfejs dostawcy sugestii
     * @param converter konwerter {@link String} <=> {@link PersonalDataModel}
     */
    public AbstractCabinetAnalyzer(FilingCabinet cabinet,
                                   Callback<ISuggestionRequest, Collection<PersonalDataModel>> suggestionProvider,
                                   StringConverter<PersonalDataModel> converter) {
        super(cabinet);
        this.suggestionProvider = suggestionProvider;
        this.converter = converter;
    }

    @Override
    public Callback<ISuggestionRequest, Collection<PersonalDataModel>> getSuggestionProvider() {
        return suggestionProvider;
    }

    @Override
    public StringConverter<PersonalDataModel> getPersonalDataConverter() {
        return converter;
    }

    /**
     * Liczba aktów jest liczona podczas ładowania ich ze źródła do szafy metodą {@link #load()}.
     * Każda tablica zwrócona przez metody {@link DataSource#getBaptism(String, String)},
     * {@link DataSource#getConfirmation(String, String)} i {@link DataSource#getDecease(String, String)}
     * jest używana przez zwrócenie liczby elementów tablicy. Te wartości dodawane do liczby numerów aktów.
     * Podobnie jest z metodą {@link DataSource#getMarriage(String, String)}, z tą różnicą, że liczba
     * elementów dzielona jest przez {@code 2f}, ponieważ te akta zawierają informację o dwóch osobach.
     * Zatem, wywołując {@link DataSource#getMarriage(String, String)} najpierw z danymi męża, później
     * z danymi żony, wśród elementów powinniśmy otrzymać ten sam numer aktu za pierwszym i za drugim razem.
     * Wartość liczby zmiennoprzecinkowej podczas zwracania jest
     * zaokrąglana za pomocą metody {@link Math#round(float)}.
     *
     * @return liczba aktów wczytanych i przechowywanych w {@link FilingCabinet szafie aktowej}
     */
    @Override
    public int getNumberOfActs() {
        return Math.round(numberOfActs);
    }

    @Override
    public void load() {
        Map<String, Set<String>> personalData = getDataSource().getPersonalData();
        personalData.forEach((surname, names) -> names.forEach((name) -> {
            DataSource dataSource = getDataSource();
            Briefcase briefcase = createBriefcase(
                    dataSource.getBaptism(surname, name),
                    dataSource.getConfirmation(surname, name),
                    dataSource.getMarriage(surname, name),
                    dataSource.getDecease(surname, name)
            );
            numberOfActs += briefcase.getBaptism().length;
            numberOfActs += briefcase.getConfirmation().length;
            numberOfActs += briefcase.getMarriage().length/2f;
            numberOfActs += briefcase.getDecease().length;
            getCabinet().add(surname, name, briefcase);
        }));
    }
}
