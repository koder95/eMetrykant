package pl.koder95.eme.core;

import static org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.koder95.eme.core.spi.FilingCabinet;
import pl.koder95.eme.core.spi.PersonalDataModel;

import java.util.*;

/**
 * <b>Dostawca sugestii</b> daje możliwość przeszukiwania {@link FilingCabinet szafy}
 * zwracając sugestie co do nazwisk i imion. Udostępnia również konwerter, który
 * zamienia {@link pl.koder95.eme.core.spi.Briefcase aktówkę} na {@link PersonalDataModel model danych osobowych}.
 * Sugestie sortowane są za pomocą komparatora, który w domyślnej instancji porównuje
 * najpierw nazwiska, później imiona.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public class SuggestionProvider implements Callback<ISuggestionRequest, Collection<PersonalDataModel>> {

    private static final Comparator<PersonalDataModel> DEFAULT_COMPARATOR = Comparator
            .comparing(PersonalDataModel::getSurname).thenComparing(PersonalDataModel::getName);

    private final FilingCabinet cabinet;
    private final PersonalDataModelConverter defaultConverter;
    private StringConverter<PersonalDataModel> converter;
    private Comparator<PersonalDataModel> comparator;

    /**
     * Tworzy obiekt ustawiając wszystkie składowe elementy. Jeżeli {@code converter == null} wtedy tworzony
     * jest nowy konwerter typu {@link PersonalDataModelConverter}, który używa {@code cabinet}. Jeżeli
     * natomiast {@code comparator == null} wtedy ustawiany jest domyślny komparator porównujący
     * najpierw nazwiska, później imiona.
     *
     * @param cabinet szafa aktowa
     * @param converter przekształca ciągi znaków na modele danych, może być {@code null}
     * @param comparator porównuje modele danych osobowych ze sobą
     */
    public SuggestionProvider(FilingCabinet cabinet, StringConverter<PersonalDataModel> converter,
                              Comparator<PersonalDataModel> comparator) {
        this.cabinet = cabinet;
        this.defaultConverter = new PersonalDataModelConverter(cabinet);
        setConverter(converter);
        setComparator(comparator);
    }

    /**
     * Tworzy obiekt korzystając z domyślnego komparatora.
     *
     * @param cabinet szafa aktowa
     * @param converter przekształca ciągi znaków na modele danych, może być {@code null}
     * @see #SuggestionProvider(FilingCabinet, StringConverter, Comparator)
     */
    public SuggestionProvider(FilingCabinet cabinet, StringConverter<PersonalDataModel> converter) {
        this(cabinet, converter, null);
    }

    /**
     * Tworzy obiekt korzystając z domyślnego konwertera i komparatora.
     *
     * @param cabinet szafa aktowa
     */
    public SuggestionProvider(FilingCabinet cabinet) {
        this(cabinet, null);
    }

    @Override
    public Collection<PersonalDataModel> call(ISuggestionRequest param) {
        if (param.getUserText().isEmpty()) return Collections.emptyList();

        List<PersonalDataModel> suggestions = new LinkedList<>();
        String string = param.getUserText().trim();

        int spaceIndex = string.indexOf(' ');
        String beforeSpace = spaceIndex > -1? string.substring(0, spaceIndex) : string;
        String afterSpace = spaceIndex > -1? string.substring(spaceIndex + 1) : "";

        for (Map.Entry<String, Set<String>> entry : cabinet.getPersonalData().entrySet()) {
            String surname = entry.getKey();
            Set<String> names = entry.getValue();
            for (String name : names) {
                if (accept(
                        beforeSpace.toUpperCase(), afterSpace.toUpperCase(),
                        surname.toUpperCase(), name.toUpperCase()
                )) {
                    addTo(suggestions, surname, name);
                }
            }
        }

        suggestions.sort(comparator);
        List<PersonalDataModel> tmp = new ArrayList<>(suggestions);
        suggestions.clear();
        return tmp;
    }

    private void addTo(List<PersonalDataModel> suggestions, String surname, String name) {
        suggestions.add(converter.fromString(surname + " " + name));
    }

    /**
     * Metoda sprawdzająca, czy wpisane dane w wyszukiwarce mogą być zaakceptowane w kontekście podanych
     * danych osobowych. Podane ciągi powinny być złożone z WIELKICH LITER (patrz:
     * {@link String#toUpperCase()}).
     *
     * @param beforeSpace ciąg znaków znajdujących się przed pierwszą spacją
     * @param afterSpace ciąg znaków znajdujących się za pierwszą spacją
     * @param surname nazwisko
     * @param name imię
     * @return jeśli {@code true}, dane osobowe zostaną dodane do sugestii
     */
    public boolean accept(String beforeSpace, String afterSpace, String surname, String name) {
        return afterSpace.isEmpty()?
                surname.startsWith(beforeSpace) || name.startsWith(beforeSpace) :
                surname.equals(beforeSpace)?
                        name.startsWith(afterSpace) :
                        name.equals(beforeSpace) && surname.startsWith(afterSpace);
    }

    /**
     * @return szafa aktowa używana do pozyskiwania danych do sugestii
     */
    public FilingCabinet getCabinet() {
        return cabinet;
    }

    /**
     * @return konwerter zamieniający ciągi znaków na modele danych osobowych
     */
    public StringConverter<PersonalDataModel> getConverter() {
        return converter;
    }

    /**
     * Jeżeli {@code converter == null} wtedy używany jest domyślny konwerter typu
     * {@link PersonalDataModelConverter}.
     *
     * @param converter przekształca ciągi znaków na modele danych, może być {@code null}
     */
    public final void setConverter(StringConverter<PersonalDataModel> converter) {
        this.converter = converter != null? converter : defaultConverter;
    }

    /**
     * @return komparator sortujący sugestie
     */
    public Comparator<PersonalDataModel> getComparator() {
        return comparator;
    }

    /**
     * Jeżeli {@code comparator == null} wtedy ustawiany jest domyślny komparator
     * porównujący najpierw nazwiska, później imiona.
     *
     * @param comparator porównuje modele danych osobowych ze sobą
     */
    public final void setComparator(Comparator<PersonalDataModel> comparator) {
        this.comparator = comparator != null? comparator : DEFAULT_COMPARATOR;
    }
}
