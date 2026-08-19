package pl.koder95.eme.io;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.koder95.eme.Files;
import pl.koder95.eme.domain.index.BookTemplate;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.xml.XMLLoader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Loader szablonów ksiąg z pliku XML ({@code templates.xml}).
 *
 * <p>Oczekiwana struktura dokumentu:</p>
 * <pre>{@code
 * <templates>
 *     <bt name="Księga ochrzczonych">
 *         <section header="Ochrzczony">
 *             <field name="name" index="1" label="Imię"/>
 *             ...
 *         </section>
 *     </bt>
 * </templates>
 * }</pre>
 *
 * <p>Atrybut {@code index} pola wyznacza pozycję nazwy pola na liście
 * {@link BookTemplate#fieldNames()}, a zakres indeksów pól sekcji wyznacza
 * {@link BookTemplate.Section#startIndex()} i {@link BookTemplate.Section#endIndex()}.</p>
 */
@Log
@RequiredArgsConstructor
public class BookTemplateLoader {

    @NonNull
    private final Path xmlPath;

    public BookTemplateLoader() {
        this(Files.TEMPLATES_XML);
    }

    /**
     * Wczytuje wszystkie poprawne szablony ksiąg.
     *
     * @return lista szablonów; szablony z nierozpoznaną nazwą księgi są pomijane
     * @throws IOException problemy z odczytem albo parsowaniem pliku
     */
    public List<BookTemplate> loadTemplates() throws IOException {
        Document doc;
        try {
            doc = XMLLoader.loadDOM(xmlPath.toFile());
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to load templates: " + xmlPath, e);
        }
        List<BookTemplate> templates = new ArrayList<>();
        Element root = doc.getDocumentElement();
        if (root == null || !root.getNodeName().equalsIgnoreCase("templates")) {
            log.warning(() -> "Brak elementu głównego <templates> w pliku: " + xmlPath);
            return templates;
        }
        NodeList btNodes = root.getElementsByTagName("bt");
        for (int i = 0; i < btNodes.getLength(); i++) {
            BookTemplate template = parseTemplate(btNodes.item(i));
            if (template != null) {
                templates.add(template);
            }
        }
        return templates;
    }

    /**
     * Wczytuje szablony zmapowane po typie księgi.
     *
     * @return mapa typ księgi → szablon
     * @throws IOException problemy z odczytem albo parsowaniem pliku
     */
    public Map<BookType, BookTemplate> loadTemplateMap() throws IOException {
        Map<BookType, BookTemplate> map = new EnumMap<>(BookType.class);
        for (BookTemplate template : loadTemplates()) {
            map.put(template.bookType(), template);
        }
        return map;
    }

    private BookTemplate parseTemplate(Node btNode) {
        if (btNode == null || btNode.getNodeType() != Node.ELEMENT_NODE) {
            return null;
        }
        String bookName = attribute(btNode, "name");
        Optional<BookType> bookType = BookType.ofBookName(bookName);
        if (bookType.isEmpty()) {
            log.warning(() -> "Pominięto szablon o nierozpoznanej nazwie księgi: " + bookName);
            return null;
        }

        List<IndexedField> fields = new ArrayList<>();
        List<BookTemplate.Section> sections = new ArrayList<>();
        NodeList children = btNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE || !child.getNodeName().equalsIgnoreCase("section")) {
                continue;
            }
            BookTemplate.Section section = parseSection(child, sections.size(), fields);
            if (section != null) {
                sections.add(section);
            }
        }

        List<String> fieldNames = fields.stream()
                .sorted(Comparator.comparingInt(IndexedField::index))
                .map(IndexedField::name)
                .toList();

        return BookTemplate.builder()
                .bookType(bookType.get())
                .fieldNames(fieldNames)
                .sections(sections)
                .build();
    }

    private BookTemplate.Section parseSection(Node sectionNode, int ordinal, List<IndexedField> fields) {
        String header = attribute(sectionNode, "header");
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        NodeList children = sectionNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE || !child.getNodeName().equalsIgnoreCase("field")) {
                continue;
            }
            String name = attribute(child, "name");
            Integer index = parseIndex(attribute(child, "index"));
            if (name == null || name.isBlank() || index == null || index < 0) {
                log.warning(() -> "Pominięto pole bez poprawnych atrybutów 'name'/'index' w sekcji: " + header);
                continue;
            }
            fields.add(new IndexedField(name, index));
            start = Math.min(start, index);
            end = Math.max(end, index);
        }
        if (start > end) {
            log.warning(() -> "Pominięto sekcję bez poprawnych pól: " + header);
            return null;
        }
        return BookTemplate.Section.builder()
                .id("section-" + ordinal)
                .startIndex(start)
                .endIndex(end)
                .title(header)
                .build();
    }

    private static String attribute(Node node, String name) {
        if (!node.hasAttributes()) {
            return null;
        }
        Node attr = node.getAttributes().getNamedItem(name);
        return attr == null ? null : attr.getTextContent();
    }

    private static Integer parseIndex(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private record IndexedField(String name, int index) {
    }
}
