package pl.koder95.eme.core.spi;

import pl.koder95.eme.dfs.ActNumber;

import java.util.Set;

/**
 * Interfejs <i>aktówka</i> dostarcza paczkę danych, która zawiera listę numerów aktów.
 * Za pomocą interfejsu numery akt są rozpoznawane i kategoryzowane.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-13
 * @since 0.4.0
 */
public interface Briefcase {

    /**
     * @return numer aktu chrztu, może być {@code null}
     */
    ActNumber getBaptism();
    /**
     * @return numer aktu bierzmowania, może być {@code null}
     */
    ActNumber getConfirmation();
    /**
     * @return numer aktu małżeństwa, może być {@code null}
     */
    ActNumber getMarriage();
    /**
     * @return numer aktu pogrzebu, może być {@code null}
     */
    ActNumber getDecease();
}
