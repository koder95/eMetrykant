package pl.koder95.eme.core.spi;

import pl.koder95.eme.dfs.ActNumber;

import java.util.Map;
import java.util.Set;

/**
 * Interfejs dostarcza metod, które pozwalają zarządzać danymi wprowadzonymi ze {@link DataSource źródła}
 * do {@link FilingCabinet szafy aktowej} oraz zapisanymi do {@link DataTarget celu}. <i>Pracownik szafy</i>
 * ma zadanie utworzenia nowych aktówek oraz przyporządkowania ich do konkretnych osób. Robi to podczas
 * ładowania danych, które wykonywane jest na żądanie klienta. Klient może również zarządać zapisania
 * danych, więc <i>pracownik</i> przekazuje je do celu.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-13
 * @since 0.4.0
 */
public interface CabinetWorker {

    /**
     * @return {@link FilingCabinet szafa aktowa}, którą zarządza pracownik
     */
    FilingCabinet getCabinet();
    /**
     * @return {@link DataSource źródło}, z którego pracownik pozyskuje dane podczas ładowania
     */
    DataSource getDataSource();
    /**
     * @return {@link DataTarget cel}, do którego wysyła pracownik dane do zapisu
     */
    DataTarget getDataTarget();

    /**
     * Metoda wytwórcza aktówek z numerami akt: chrztu, bierzmowania, małżeństwa i pogrzebu.
     *
     * @param baptism numer aktu chrztu
     * @param confirmation numer aktu bierzmowania
     * @param marriage numer aktu małżeństwa
     * @param decease numer aktu pogrzebu
     * @return {@link Briefcase aktówka}, która zawiera wprowadzone dane
     */
    Briefcase createBriefcase(ActNumber baptism, ActNumber confirmation, ActNumber marriage, ActNumber decease);

    /**
     * Pracownik ładuje dane ze {@link DataSource źródła} do {@link FilingCabinet szafy aktowej}.
     */
    default void load() {
        Map<String, Set<String>> personalData = getDataSource().getPersonalData();
        personalData.forEach((surname, names) -> names.forEach((name) -> {
            getCabinet().add(surname, name, createBriefcase(
                    getDataSource().getBaptism(surname, name),
                    getDataSource().getConfirmation(surname, name),
                    getDataSource().getMarriage(surname, name),
                    getDataSource().getDecease(surname, name))
            );
        }));
    }

    /**
     * Pracownik wysyła dane do {@link DataTarget celu}, aby zostały zapisane.
     */
    default void save() {
        Map<String, Set<String>> personalData = getCabinet().getPersonalData();
        personalData.forEach((surname, names) -> names.forEach((name) -> {
            Briefcase briefcase = getCabinet().get(surname, name);
            getDataTarget().setBaptism(surname, name, briefcase.getBaptism());
            getDataTarget().setConfirmation(surname, name, briefcase.getConfirmation());
            getDataTarget().setMarriage(surname, name, briefcase.getMarriage());
            getDataTarget().setDecease(surname, name, briefcase.getDecease());
        }));
    }
}
