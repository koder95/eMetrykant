package pl.koder95.eme.core.spi;

import pl.koder95.eme.domain.index.Index;

/**
 * Punkt rozszerzeń dla filtrowania rekordów indeksów.
 */
@FunctionalInterface
public interface IndexFilter {

    boolean accept(Index index);

    static IndexFilter acceptAll() {
        return index -> true;
    }
}
