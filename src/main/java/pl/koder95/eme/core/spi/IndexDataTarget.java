package pl.koder95.eme.core.spi;

import org.w3c.dom.Document;

import java.io.IOException;

/**
 * XML-owy cel zapisu dokumentu indeksów.
 *
 * <p>Kontrakt jest odwrotnością {@link IndexDataSource} i również celowo
 * operuje na {@link Document}.</p>
 */
public interface IndexDataTarget {

    void saveDocument(Document document) throws IOException;
}
