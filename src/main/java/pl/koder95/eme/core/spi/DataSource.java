package pl.koder95.eme.core.spi;

import pl.koder95.eme.dfs.ActNumber;

import java.util.Map;
import java.util.Set;

/**
 * Interfejs pozwala na pozyskanie wymaganych danych bez określania formatu
 * i sposobu odczytu.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public interface DataSource {

    /**
     * @param surname nazwisko
     * @param name imię
     * @return numery aktów chrztu osoby, tablica może być pusta
     */
    ActNumber[] getBaptism(String surname, String name);

    /**
     * @param surname nazwisko
     * @param name imię
     * @return numery aktów bierzmowania osoby, tablica może być pusta
     */
    ActNumber[] getConfirmation(String surname, String name);

    /**
     * @param surname nazwisko
     * @param name imię
     * @return numery aktów małżeństwa osoby, tablica może być pusta
     */
    ActNumber[] getMarriage(String surname, String name);

    /**
     * @param surname nazwisko
     * @param name imię
     * @return numery aktów pogrzebu osoby, tablica może być pusta
     */
    ActNumber[] getDecease(String surname, String name);

    /**
     * Zwraca mapę, która każdemu nazwisku przyporządkowuje zbiór imion. Dane te to możliwe argumenty
     * dla metod {@link #getBaptism(String, String)}, {@link #getConfirmation(String, String)},
     * {@link #getMarriage(String, String)} i {@link #getDecease(String, String)}.
     *
     * @return dane osobowe w formie mapy, która każdemu nazwisku przyporządkowuje zbiór imion,
     * {@code null} - jeżeli źródło danych nie wspiera możliwości pobrania danych osobowych.
     */
    Map<String, Set<String>> getPersonalData();
}
