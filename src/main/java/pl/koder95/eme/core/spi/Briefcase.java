package pl.koder95.eme.core.spi;

import pl.koder95.eme.domain.index.ActNumber;

import java.util.Set;

/**
 * Interfejs <i>aktówka</i> dostarcza paczkę danych, która zawiera listę numerów aktów.
 * Za pomocą interfejsu numery akt są rozpoznawane i kategoryzowane.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public interface Briefcase {

    /**
     * @return numery aktów chrztu, tablica może być pusta
     */
    ActNumber[] getBaptism();
    /**
     * @return numery aktów bierzmowania, tablica może być pusta
     */
    ActNumber[] getConfirmation();
    /**
     * @return numery aktów małżeństwa, tablica może być pusta
     */
    ActNumber[] getMarriage();
    /**
     * @return numery aktów pogrzebu, tablica może być pusta
     */
    ActNumber[] getDecease();
}
