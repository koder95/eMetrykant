package pl.koder95.eme.io;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import pl.koder95.eme.core.spi.IndexDataSource;
import pl.koder95.eme.xml.XMLLoader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Domyślne źródło danych indeksów z pliku XML.
 */
public class FileXmlIndexDataSource implements IndexDataSource {

    private final Path xmlPath;

    public FileXmlIndexDataSource(Path xmlPath) {
        this.xmlPath = xmlPath;
    }

    @Override
    public Document loadDocument() throws IOException {
        ensureFileExists();
        try {
            return XMLLoader.loadDOM(xmlPath.toFile());
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException(e);
        }
    }

    private void ensureFileExists() throws IOException {
        if (Files.exists(xmlPath)) {
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(xmlPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            writer.write(
                    "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
                            "<indices>\n" +
                            "    <book name=\"Księga zaślubionych\"></book>\n" +
                            "    <book name=\"Księga zmarłych\"></book>\n" +
                            "    <book name=\"Księga ochrzczonych\"></book>\n" +
                            "    <book name=\"Księga bierzmowanych\"></book>\n" +
                            "</indices>"
            );
        } catch (FileAlreadyExistsException ignored) {
            // wyścig między sprawdzeniem Files.exists(...) a CREATE_NEW - można bezpiecznie zignorować
        }
    }
}
