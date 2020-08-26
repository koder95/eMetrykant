package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.FilingCabinet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Szafa aktowa oparta o {@link TreeMap mapę typu drzewo} oraz {@link TreeSet zbiór typu drzewo}.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public class TreeFilingCabinet extends AbstractFilingCabinet implements FilingCabinet {

    /**
     * Tworzy nowy obiekt z pustą mapą i fabryką map tworzącą puste mapy.
     */
    public TreeFilingCabinet() {
        super(new TreeMap<>(), TreeMap::new);
    }
}
