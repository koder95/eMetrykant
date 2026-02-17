package pl.koder95.eme.core.spi;

import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Źródło dokumentu indeksów niezależne od formatu i nośnika.
 */
public interface IndexDataSource {

    Document loadDocument() throws IOException;
}
