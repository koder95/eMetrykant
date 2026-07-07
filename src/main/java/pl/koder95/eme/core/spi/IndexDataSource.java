package pl.koder95.eme.core.spi;

import org.w3c.dom.Document;

import java.io.IOException;

/**
 * XML-owe źródło dokumentu indeksów.
 *
 * <p>Kontrakt jest celowo specyficzny dla XML i zwraca {@link Document}.</p>
 */
public interface IndexDataSource {

    Document loadDocument() throws IOException;
}
