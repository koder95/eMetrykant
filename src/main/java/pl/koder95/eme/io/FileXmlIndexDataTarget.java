package pl.koder95.eme.io;

import org.w3c.dom.Document;
import pl.koder95.eme.core.spi.IndexDataTarget;
import pl.koder95.eme.xml.XMLSaver;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Domyślny cel zapisu indeksów do pliku XML.
 */
public class FileXmlIndexDataTarget implements IndexDataTarget {

    private final Path xmlPath;

    public FileXmlIndexDataTarget(Path xmlPath) {
        this.xmlPath = xmlPath;
    }

    /**
     * Dokument zapisywany jest najpierw do pliku tymczasowego, a dopiero potem podmieniany,
     * aby przerwany zapis nie zniszczył dotychczasowych danych.
     */
    @Override
    public void saveDocument(Document document) throws IOException {
        Path directory = xmlPath.toAbsolutePath().getParent();
        if (directory != null) {
            Files.createDirectories(directory);
        }
        Path temporary = Files.createTempFile(directory, "indices", ".xml.tmp");
        try {
            try (OutputStream output = Files.newOutputStream(temporary)) {
                XMLSaver.saveDOM(document, output);
            } catch (TransformerException ex) {
                throw new IOException("Nie udało się zapisać dokumentu XML", ex);
            }
            move(temporary);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private void move(Path temporary) throws IOException {
        try {
            Files.move(temporary, xmlPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporary, xmlPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
