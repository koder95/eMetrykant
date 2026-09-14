package pl.koder95.eme.application;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.koder95.eme.core.spi.IndexRepository;

/**
 * Serwis aplikacyjny odpowiedzialny za przeładowanie indeksów.
 */
@RequiredArgsConstructor
public class IndexReloadService {

    @NonNull
    private final IndexRepository indexRepository;

    public void reloadAll() {
        indexRepository.reloadAll();
    }
}
