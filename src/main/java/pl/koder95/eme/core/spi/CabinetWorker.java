package pl.koder95.eme.core.spi;

import pl.koder95.eme.dfs.ActNumber;

import java.util.Arrays;
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
 * @version 0.4.0, 2020-08-26
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
     * @param baptism numery aktów chrztu
     * @param confirmation numery aktów bierzmowania
     * @param marriage numery aktów małżeństwa
     * @param decease numery aktów pogrzebu
     * @return {@link Briefcase aktówka}, która zawiera wprowadzone dane
     */
    Briefcase createBriefcase(ActNumber[] baptism, ActNumber[] confirmation, ActNumber[] marriage, ActNumber[] decease);

    /**
     * Pracownik ładuje dane ze {@link DataSource źródła} do {@link FilingCabinet szafy aktowej}.
     */
    default void load() {
        DataSource dataSource = getDataSource();
        Map<String, Set<String>> personalData = dataSource.getPersonalData();
        FilingCabinet cabinet = getCabinet();
        personalData.forEach((surname, names) -> names.forEach((name) -> cabinet.add(surname, name, createBriefcase(
                dataSource.getBaptism(surname, name),
                dataSource.getConfirmation(surname, name),
                dataSource.getMarriage(surname, name),
                dataSource.getDecease(surname, name))
        )));
    }

    /**
     * Pracownik wysyła dane do {@link DataTarget celu}, aby zostały zapisane.
     */
    default void save() {
        FilingCabinet cabinet = getCabinet();
        Map<String, Set<String>> personalData = cabinet.getPersonalData();
        DataTarget dataTarget = getDataTarget();
        personalData.forEach((surname, names) -> names.forEach((name) -> {
            Briefcase briefcase = cabinet.get(surname, name);
            Arrays.stream(briefcase.getBaptism()).forEach(a -> dataTarget.setBaptism(surname, name, a));
            Arrays.stream(briefcase.getConfirmation()).forEach(a -> dataTarget.setBaptism(surname, name, a));
            Arrays.stream(briefcase.getMarriage()).forEach(a -> dataTarget.setBaptism(surname, name, a));
            Arrays.stream(briefcase.getDecease()).forEach(a -> dataTarget.setBaptism(surname, name, a));
        }));
    }
}
