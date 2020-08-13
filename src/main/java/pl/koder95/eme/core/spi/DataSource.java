package pl.koder95.eme.core.spi;

import pl.koder95.eme.dfs.ActNumber;

import java.util.Map;
import java.util.Set;

/**
 * Interfejs pozwala na pozyskanie wymaganych danych bez określania formatu
 * i sposobu odczytu.
 */
public interface DataSource {

    /**
     * @param surname nazwisko
     * @param name imię
     * @return akt chrztu osoby albo {@code null} jeżeli nie posiada aktu lub jest nieznany
     */
    ActNumber getBaptism(String surname, String name);

    /**
     * @param surname nazwisko
     * @param name imię
     * @return akt bierzmowania osoby albo {@code null} jeżeli nie posiada aktu lub jest nieznany
     */
    ActNumber getConfirmation(String surname, String name);

    /**
     * @param surname nazwisko
     * @param name imię
     * @return akt małżeństwa osoby albo {@code null} jeżeli nie posiada aktu lub jest nieznany
     */
    ActNumber getMarriage(String surname, String name);

    /**
     * @param surname nazwisko
     * @param name imię
     * @return akt pogrzebu osoby albo {@code null} jeżeli nie posiada aktu lub jest nieznany
     */
    ActNumber getDecease(String surname, String name);

    /**
     * @return dane osobowe w formie mapy, która każdemu nazwisku przyporządkowuje zbiór imion
     */
    Map<String, Set<String>> getPersonalData();
}
