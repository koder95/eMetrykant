package pl.koder95.eme.factory;

import pl.koder95.eme.domain.index.Index;

import java.util.Map;

/**
 * Creates domain {@link Index} instances from field maps.
 */
public interface IndexFactory {

    Index create(Map<String, String> data);
}
