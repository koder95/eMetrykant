package pl.koder95.eme.domain.index;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UniqueActNumberTest {

    @Test
    void fromSplitsSignIntoNumberAndSuffix() {
        UniqueActNumber uan = UniqueActNumber.from(BookType.LIBER_BAPTISMORUM, new ActNumber("12a", 1900));
        assertEquals(BookType.LIBER_BAPTISMORUM, uan.bookType());
        assertEquals(1900, uan.year());
        assertEquals(12, uan.signNumber());
        assertEquals("a", uan.signSuffix());
    }

    @Test
    void fromWithoutBookTypeIsUnknown() {
        assertEquals(UniqueActNumber.UNKNOWN, UniqueActNumber.from((BookType) null, new ActNumber("1", 1900)));
    }

    @Test
    void fromResolvesBookTypeByDisplayName() {
        UniqueActNumber uan = UniqueActNumber.from("Księga zmarłych", new ActNumber("7", 1950));
        assertEquals(BookType.LIBER_DEFUNCTORUM, uan.bookType());
    }

    @Test
    void parseRoundTripsToString() {
        UniqueActNumber original = new UniqueActNumber(BookType.LIBER_MATRIMONIORUM, 1925, 3, "b");
        assertEquals(original, UniqueActNumber.parse(original.toString()));
        assertEquals(UniqueActNumber.UNKNOWN, UniqueActNumber.parse(UniqueActNumber.UNKNOWN.toString()));
    }

    @Test
    void parseRejectsMalformedInput() {
        assertNull(UniqueActNumber.parse(null));
        assertNull(UniqueActNumber.parse("12/1900"));
        assertNull(UniqueActNumber.parse("eme.uan:NOT_A_BOOK/1900/12"));
        assertNull(UniqueActNumber.parse("eme.uan:LIBER_BAPTISMORUM/1900/abc"));
    }

    @Test
    void invalidComponentsCollapseToUnknown() {
        assertEquals(UniqueActNumber.UNKNOWN, new UniqueActNumber(BookType.LIBER_BAPTISMORUM, 1500, 1, ""));
        assertEquals(UniqueActNumber.UNKNOWN, new UniqueActNumber(BookType.LIBER_BAPTISMORUM, 1900, 0, ""));
    }

    @Test
    void registryDetectsCollisionsAndUnknowns() {
        Book book = new Book(BookType.LIBER_BAPTISMORUM.getBookName());
        Index first = Index.create(book, Map.of("an", "12/1900", "surname", "Nowak", "name", "Jan"));
        Index duplicate = Index.create(book, Map.of("an", "12/1900", "surname", "Kowalski", "name", "Piotr"));
        Index distinct = Index.create(book, Map.of("an", "13/1900", "surname", "Wiśniewski", "name", "Adam"));
        Index unknown = Index.create(new Book("Nieznana księga"), Map.of("an", "1/1900", "surname", "X", "name", "Y"));

        UniqueActNumberRegistry registry = UniqueActNumberRegistry.of(List.of(first, duplicate, distinct, unknown));

        assertEquals(1, registry.size());
        assertTrue(registry.find(distinct.getUniqueActNumber()).isPresent());
        assertTrue(registry.find(first.getUniqueActNumber()).isEmpty(), "conflicted UAN must not resolve");
        assertEquals(1, registry.getConflicts().size());
        assertEquals(2, registry.getConflicts().values().iterator().next().size());
        assertEquals(1, registry.getUnknown().size());
    }
}
