package pl.koder95.eme.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pl.koder95.eme.core.spi.IndexDataTarget;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.xml.XMLSaver;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Zapisujący odpowiedzialny za utrwalanie rekordów indeksów.
 *
 * <p>Klasa jest odwrotnością {@link IndexLoader}: buduje dokument XML na podstawie
 * indeksów pogrupowanych według {@link BookType typu księgi} i przekazuje go
 * do {@link IndexDataTarget celu}.</p>
 */
public class IndexWriter {

    private final IndexDataTarget dataTarget;

    public IndexWriter(IndexDataTarget dataTarget) {
        this.dataTarget = Objects.requireNonNull(dataTarget, "dataTarget must not be null");
    }

    public void saveBooks(Map<BookType, List<Index>> indicesByType) throws IOException {
        Objects.requireNonNull(indicesByType, "indicesByType must not be null");
        Document document;
        try {
            document = XMLSaver.createDOM();
        } catch (ParserConfigurationException ex) {
            throw new IOException("Nie udało się utworzyć dokumentu XML", ex);
        }
        Element indices = document.createElement("indices");
        document.appendChild(indices);
        for (BookType type : BookType.values()) {
            Element book = document.createElement("book");
            book.setAttribute("name", type.getBookName());
            appendIndices(document, book, type, indicesByType.get(type));
            indices.appendChild(book);
        }
        dataTarget.saveDocument(document);
    }

    private void appendIndices(Document document, Element book, BookType type, Collection<Index> indices) {
        if (indices == null) {
            return;
        }
        for (Index index : indices) {
            if (index == null) {
                continue;
            }
            Element element = document.createElement("index");
            for (String name : orderedDataNames(type, index)) {
                element.setAttribute(name, index.getData(name));
            }
            book.appendChild(element);
        }
    }

    /**
     * Atrybuty ze schematu księgi zapisywane są w ustalonej kolejności, a pozostałe
     * alfabetycznie, aby zapisany plik był powtarzalny.
     */
    private List<String> orderedDataNames(BookType type, Index index) {
        List<String> ordered = new ArrayList<>();
        for (String name : type.getFieldSchema()) {
            if (index.getDataNames().contains(name)) {
                ordered.add(name);
            }
        }
        ordered.addAll(new TreeSet<>(index.getDataNames()).stream().filter(name -> !ordered.contains(name)).toList());
        return ordered;
    }
}
