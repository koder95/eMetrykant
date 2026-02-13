package pl.koder95.eme.dfs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loader odpowiedzialny za wczytywanie i filtrowanie rekordów indeksów.
 */
public class IndexLoader {

    private final IndexDataSource dataSource;
    private final IndexFilter filter;

    public IndexLoader(IndexDataSource dataSource, IndexFilter filter) {
        this.dataSource = dataSource;
        this.filter = filter;
    }

    public List<Book> loadBooks() throws IOException {
        Document doc = dataSource.loadDocument();
        Element indices = doc.getDocumentElement();
        List<Book> books = new ArrayList<>();
        if (indices != null && indices.getNodeName().equalsIgnoreCase("indices")) {
            NodeList bookNodes = indices.getElementsByTagName("book");
            for (int i = 0; i < bookNodes.getLength(); i++) {
                Book b = parseBook(bookNodes.item(i));
                if (b != null) {
                    books.add(b);
                }
            }
        }
        return books;
    }

    private Book parseBook(Node bookNode) {
        if (bookNode == null || !bookNode.getNodeName().equalsIgnoreCase("book")) {
            return null;
        }
        if (!bookNode.hasAttributes()) {
            return new Book("");
        }
        String bookName = Optional
                .ofNullable(bookNode.getAttributes().getNamedItem("name"))
                .map(Node::getTextContent)
                .orElseThrow(() -> new IllegalArgumentException("Brak atrybutu 'name' w węźle <book>."));
        Book book = new Book(bookName);
        NodeList indices = bookNode.getChildNodes();
        for (int i = 0; i < indices.getLength(); i++) {
            Index index = Index.create(book, indices.item(i));
            if (index != null && filter.accept(index)) {
                book.addIndex(index);
            }
        }
        return book;
    }
}
