package pl.koder95.eme.application;

import pl.koder95.eme.dfs.IndexList;

/**
 * Serwis aplikacyjny odpowiedzialny za przeładowanie indeksów.
 */
public class IndexReloadService {

    public void reloadAll() {
        for (IndexList value : IndexList.values()) {
            try {
                value.clear();
                value.load();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }
}
