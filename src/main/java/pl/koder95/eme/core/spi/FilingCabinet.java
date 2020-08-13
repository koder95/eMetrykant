package pl.koder95.eme.core.spi;

import java.util.Map;
import java.util.Set;

/**
 * Interfejs <i>szafy aktowej</i> dostarcza metody zarządzające {@link Briefcase aktówkami}.
 * Przyporządkowuje je nazwisku i imieniu (imionom) osób, dzięki czemu można pozyskiwać
 * dane o numerach aktów konkretnych osób. Szafą zarządza {@link CabinetWorker pracownik szafy},
 * który korzystając z metod tego interfejsu może zarządzać danymi.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-13
 * @since 0.4.0
 */
public interface FilingCabinet {

    /**
     * Dodaje (albo zastępuje) do szafy aktówkę pod określone nazwisko i imię osoby.
     *
     * @param surname nazwisko
     * @param name imię (imiona)
     * @param b aktówka
     */
    void add(String surname, String name, Briefcase b);

    /**
     * Usuwa dane dot. osoby.
     *
     * @param surname nazwisko
     * @param name imię (imiona)
     */
    void remove(String surname, String name);

    /**
     * Usuwa wszystkie dane, które zawiera szafa.
     */
    void clear();

    /**
     * Pobiera wszystkie aktówki, które są umieszczone pod podanym nazwiskiem.
     *
     * @param surname nazwisko
     * @return mapa aktówek, której kluczem jest imię (imiona) osoby
     */
    Map<String, Briefcase> get(String surname);

    /**
     * @return dane osobowe w formie mapy, która każdemu nazwisku przyporządkowuje zbiór imion
     */
    Map<String, Set<String>> getPersonalData();

    /**
     * @param surname nazwisko
     * @param name imię
     * @return aktówka, która przechowywana jest pod podanym nazwiskiem i imieniem (imionami)
     */
    default Briefcase get(String surname, String name) {
        return get(surname).get(name);
    }
}
