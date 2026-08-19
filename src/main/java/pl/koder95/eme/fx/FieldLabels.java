package pl.koder95.eme.fx;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Tłumaczy nazwy atrybutów rekordów na etykiety widoczne dla użytkownika.
 *
 * <p>Etykiety opisane są kluczami w postaci {@code FX_FIELD_<NAZWA>},
 * np. atrybut {@code husband-name} ma klucz {@code FX_FIELD_HUSBAND_NAME}.</p>
 */
final class FieldLabels {

    private FieldLabels() {}

    /**
     * @param bundle pakiet językowy
     * @param field nazwa atrybutu rekordu
     * @return etykieta pola, albo nazwa atrybutu gdy brakuje tłumaczenia
     */
    static String get(ResourceBundle bundle, String field) {
        try {
            return bundle.getString("FX_FIELD_" + field.toUpperCase().replace('-', '_'));
        } catch (MissingResourceException ex) {
            return field;
        }
    }
}
