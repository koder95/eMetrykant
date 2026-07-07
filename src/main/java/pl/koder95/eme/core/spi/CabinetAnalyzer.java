package pl.koder95.eme.core.spi;

import static org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;

import java.util.Collection;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Interfejs, który poszerza zakres obowiązków {@link CabinetWorker pracownika szafy}.
 * <i>Analizator szafy</i> musi określić ilość aktów oraz dostarczyć interfejsu
 * potrzebne do wyszukiwania i zbierania danych.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-13
 * @since 0.4.0
 */
public interface CabinetAnalyzer extends CabinetWorker {

    /**
     * @return liczba aktów wczytanych i przechowywanych w {@link FilingCabinet szafie aktowej}
     */
    int getNumberOfActs();

    /**
     * @return interfejs dostarczający listę {@link PersonalDataModel modeli danych osobowych}, które mają
     * być zasugerowane podczas wyszukiwania
     */
    Callback<ISuggestionRequest, Collection<PersonalDataModel>> getSuggestionProvider();

    /**
     * @return interfejs zmieniający tekst na dane osobowe i dane osobowe na tekst, który wykorzystany
     * jest do prezentacji sugestii wyszukiwania
     */
    StringConverter<PersonalDataModel> getPersonalDataConverter();

    /**
     * Ustawia źródło danych wykorzystywane przy ponownym ładowaniu analizatora.
     *
     * @param source nowe źródło danych
     */
    void setDataSource(DataSource source);
}
