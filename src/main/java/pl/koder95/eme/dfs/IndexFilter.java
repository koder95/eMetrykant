package pl.koder95.eme.dfs;

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
