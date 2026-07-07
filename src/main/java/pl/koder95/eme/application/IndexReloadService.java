package pl.koder95.eme.application;

import pl.koder95.eme.core.spi.IndexRepository;

import java.util.Objects;

/**
 * Serwis aplikacyjny odpowiedzialny za przeładowanie indeksów.
 */
public class IndexReloadService {

    private final IndexRepository indexRepository;

    public IndexReloadService(IndexRepository indexRepository) {
        this.indexRepository = Objects.requireNonNull(indexRepository, "indexRepository must not be null");
    }

    public void reloadAll() {
        indexRepository.reloadAll();
    }
}
