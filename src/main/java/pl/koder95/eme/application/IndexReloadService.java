package pl.koder95.eme.application;

import pl.koder95.eme.core.spi.IndexRepository;

/**
 * Serwis aplikacyjny odpowiedzialny za przeładowanie indeksów.
 */
public class IndexReloadService {

    private final IndexRepository indexRepository;

    public IndexReloadService(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    public void reloadAll() {
        try {
            indexRepository.reloadAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
