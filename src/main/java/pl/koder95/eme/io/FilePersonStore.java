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
import pl.koder95.eme.domain.index.UniqueActNumber;
import pl.koder95.eme.domain.person.PersonAppearance;
import pl.koder95.eme.domain.person.PersonRegistry;
import pl.koder95.eme.domain.person.Role;
import pl.koder95.eme.domain.person.UniquePerson;
import pl.koder95.eme.xml.XMLLoader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Trwały magazyn tożsamości osób ({@code people.xml}).
 *
 * <p>Utrwala UUID-y osób oraz ich wystąpienia w aktach (jako tekstowe
 * {@link UniqueActNumber unikalne numery aktów} + role), dzięki czemu decyzje
 * o scaleniu/podziale osób przeżywają restart aplikacji.</p>
 */
@Log
@RequiredArgsConstructor
public class FilePersonStore {

    @NonNull
    private final Path xmlPath;

    public FilePersonStore() {
        this(Files.PEOPLE_XML);
    }

    /**
     * Wczytuje osoby do rejestru. Osoby już zarejestrowane (po UUID) są pomijane,
     * dzięki czemu wielokrotne wywołanie jest bezpieczne.
     *
     * @throws IOException problemy z odczytem albo parsowaniem pliku
     */
    public void load(PersonRegistry registry) throws IOException {
        if (!java.nio.file.Files.exists(xmlPath)) {
            return;
        }
        Document doc;
        try {
            doc = XMLLoader.loadDOM(xmlPath.toFile());
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to load people: " + xmlPath, e);
        }
        Element root = doc.getDocumentElement();
        if (root == null || !root.getNodeName().equalsIgnoreCase("people")) {
            log.warning(() -> "Brak elementu głównego <people> w pliku: " + xmlPath);
            return;
        }
        NodeList personNodes = root.getElementsByTagName("person");
        for (int i = 0; i < personNodes.getLength(); i++) {
            loadPerson(personNodes.item(i), registry);
        }
    }

    private void loadPerson(Node personNode, PersonRegistry registry) {
        String uuidValue = attribute(personNode, "uuid");
        String surname = attribute(personNode, "surname");
        String name = attribute(personNode, "name");
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidValue == null ? "" : uuidValue);
        } catch (IllegalArgumentException ex) {
            log.warning(() -> "Pominięto osobę z niepoprawnym UUID: " + uuidValue);
            return;
        }
        if (registry.find(uuid).isPresent()) {
            return;
        }
        UniquePerson person = new UniquePerson(uuid, surname, name);
        if (UniquePerson.UNKNOWN.equals(person)) {
            log.warning(() -> "Pominięto osobę z niekompletnymi danymi: " + uuidValue);
            return;
        }
        registry.register(person);
        NodeList children = personNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE || !child.getNodeName().equalsIgnoreCase("appearance")) {
                continue;
            }
            String act = attribute(child, "act");
            String roleValue = attribute(child, "role");
            UniqueActNumber uan = UniqueActNumber.parse(act);
            Role role = parseRole(roleValue);
            if (uan == null || UniqueActNumber.UNKNOWN.equals(uan) || role == null) {
                log.warning(() -> "Pominięto niepoprawne wystąpienie osoby " + uuid
                        + ": act=" + act + ", role=" + roleValue);
                continue;
            }
            registry.addAppearance(uuid, new PersonAppearance(uan, role));
        }
    }

    /**
     * Zapisuje pełny stan rejestru osób.
     *
     * @throws IOException problemy z zapisem pliku
     */
    public void save(PersonRegistry registry) throws IOException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new IOException("Failed to create XML document", e);
        }
        Element root = doc.createElement("people");
        doc.appendChild(root);
        for (UniquePerson person : registry.getPeople()) {
            Element personElement = doc.createElement("person");
            personElement.setAttribute("uuid", person.uuid().toString());
            personElement.setAttribute("surname", person.surname());
            personElement.setAttribute("name", person.name());
            for (PersonAppearance appearance : registry.getAppearances(person.uuid())) {
                Element appearanceElement = doc.createElement("appearance");
                appearanceElement.setAttribute("act", appearance.act().toString());
                appearanceElement.setAttribute("role", appearance.role().name());
                personElement.appendChild(appearanceElement);
            }
            root.appendChild(personElement);
        }
        try (OutputStream out = java.nio.file.Files.newOutputStream(xmlPath)) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(out));
        } catch (TransformerException e) {
            throw new IOException("Failed to save people: " + xmlPath, e);
        }
    }

    private static Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Role.valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String attribute(Node node, String name) {
        if (!node.hasAttributes()) {
            return null;
        }
        Node attr = node.getAttributes().getNamedItem(name);
        return attr == null ? null : attr.getTextContent();
    }
}
