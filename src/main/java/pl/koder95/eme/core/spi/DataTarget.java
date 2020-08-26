package pl.koder95.eme.core.spi;

import pl.koder95.eme.dfs.ActNumber;

import java.util.Collection;
import java.util.Set;

/**
 * Interfejs dostarcza metod, które pozwalają przechować dane w sposób
 * zależny od implementacji.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-13
 * @since 0.4.0
 */
public interface DataTarget {

    /**
     * Ustala dane do zapisu, aby można było je później odczytać ze {@link DataSource źródła}.
     *
     * @param surname nazwisko
     * @param name imię
     * @param an numer aktu chrztu
     */
    void setBaptism(String surname, String name, ActNumber an);

    /**
     * Ustala dane do zapisu, aby można było je później odczytać ze {@link DataSource źródła}.
     *
     * @param surname nazwisko
     * @param name imię
     * @param an numer aktu bierzmowania
     */
    void setConfirmation(String surname, String name, ActNumber an);

    /**
     * Ustala dane do zapisu, aby można było je później odczytać ze {@link DataSource źródła}.
     *
     * @param surname nazwisko
     * @param name imię
     * @param an numer aktu małżeństwa
     */
    void setMarriage(String surname, String name, ActNumber an);

    /**
     * Ustala dane do zapisu, aby można było je później odczytać ze {@link DataSource źródła}.
     *
     * @param surname nazwisko
     * @param name imię
     * @param an numer aktu pogrzebu
     */
    void setDecease(String surname, String name, ActNumber an);
}
