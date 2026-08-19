package pl.koder95.eme.io.csv;

import org.junit.jupiter.api.Test;
import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookTemplate;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.model.RepositoryException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvIndexCodecTest {

    private static final CsvIndexCodec CODEC = new CsvIndexCodec();
    private static final BookTemplate BAPTISM_TEMPLATE = BookTemplate.builder()
            .bookType(BookType.LIBER_BAPTISMORUM)
            .fieldNames(List.of("surname", "name", "birth-date", "birth-place"))
            .build();
    private static final Book BOOK = new Book(BookType.LIBER_BAPTISMORUM.getBookName());

    @Test
    void decodesLegacyShortRow() {
        Index index = CODEC.decode("Indruch;Romualda;1000;1998", BOOK, BAPTISM_TEMPLATE);

        assertNotNull(index);
        assertEquals("Indruch", index.getData("surname"));
        assertEquals("Romualda", index.getData("name"));
        assertEquals("1000/1998", index.getData("an"));
        assertEquals("", index.getData("birth-date"));
    }

    @Test
    void decodesFullRowAndStripsBom() {
        Index index = CODEC.decode("\uFEFFNowak;Jan;1900-01-01;Kraków;12a;1900", BOOK, BAPTISM_TEMPLATE);

        assertNotNull(index);
        assertEquals("Kraków", index.getData("birth-place"));
        assertEquals("12a", index.getActNumber().getSign());
        assertEquals(1900, index.getActNumber().getYear());
    }

    @Test
    void rejectsRowsWithoutValidActNumber() {
        assertNull(CODEC.decode(null, BOOK, BAPTISM_TEMPLATE));
        assertNull(CODEC.decode("", BOOK, BAPTISM_TEMPLATE));
        assertNull(CODEC.decode("Nowak;Jan;12;rok", BOOK, BAPTISM_TEMPLATE), "year must be a number");
        assertNull(CODEC.decode("Nowak;Jan;;1900", BOOK, BAPTISM_TEMPLATE), "sign must not be blank");
    }

    @Test
    void encodeTrimsTrailingEmptyColumns() {
        Index index = Index.create(BOOK, Map.of("surname", "Indruch", "name", "Romualda", "an", "1000/1998"));

        assertEquals("Indruch;Romualda;1000;1998", CODEC.encode(index, BAPTISM_TEMPLATE));
    }

    @Test
    void encodeKeepsInnerEmptyColumns() {
        Index index = Index.create(BOOK, Map.of(
                "surname", "Nowak", "birth-place", "Kraków", "an", "12a/1900"));

        assertEquals("Nowak;;;Kraków;12a;1900", CODEC.encode(index, BAPTISM_TEMPLATE));
    }

    @Test
    void roundTripPreservesData() {
        String line = "Nowak;Jan;1900-01-01;Kraków;12a;1900";
        Index decoded = CODEC.decode(line, BOOK, BAPTISM_TEMPLATE);

        assertEquals(line, CODEC.encode(decoded, BAPTISM_TEMPLATE));
    }

    @Test
    void encodeRejectsSeparatorInValue() {
        Index index = Index.create(BOOK, Map.of("surname", "No;wak", "an", "1/1900"));

        assertThrows(RepositoryException.class, () -> CODEC.encode(index, BAPTISM_TEMPLATE));
    }
}
