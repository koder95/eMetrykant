package pl.koder95.eme.domain.person;

import org.junit.jupiter.api.Test;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.UniqueActNumber;
import pl.koder95.eme.model.RepositoryException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PersonRegistryTest {

    private static PersonAppearance appearance(BookType type, int year, int number, Role role) {
        return new PersonAppearance(new UniqueActNumber(type, year, number, ""), role);
    }

    @Test
    void appearanceBelongsToAtMostOnePerson() {
        PersonRegistry registry = new PersonRegistry();
        UniquePerson a = registry.register(new UniquePerson("Nowak", "Jan"));
        UniquePerson b = registry.register(new UniquePerson("Nowak", "Jan"));
        PersonAppearance shared = appearance(BookType.LIBER_BAPTISMORUM, 1900, 1, Role.BAPTIZED);

        registry.addAppearance(a.uuid(), shared);
        assertEquals(a.uuid(), registry.findOwner(shared).orElseThrow());
        assertThrows(RepositoryException.class, () -> registry.addAppearance(b.uuid(), shared));
    }

    @Test
    void uniqueRoleCannotRepeatWithinPerson() {
        PersonRegistry registry = new PersonRegistry();
        UniquePerson person = registry.register(new UniquePerson("Nowak", "Jan"));
        registry.addAppearance(person.uuid(), appearance(BookType.LIBER_BAPTISMORUM, 1900, 1, Role.BAPTIZED));

        assertThrows(RepositoryException.class, () -> registry.addAppearance(
                person.uuid(), appearance(BookType.LIBER_BAPTISMORUM, 1901, 2, Role.BAPTIZED)));
        // role małżeńskie mogą się powtarzać
        registry.addAppearance(person.uuid(), appearance(BookType.LIBER_MATRIMONIORUM, 1920, 1, Role.HUSBAND));
        registry.addAppearance(person.uuid(), appearance(BookType.LIBER_MATRIMONIORUM, 1930, 2, Role.HUSBAND));
    }

    @Test
    void mergeMovesAppearancesAndRemovesSource() {
        PersonRegistry registry = new PersonRegistry();
        UniquePerson target = registry.register(new UniquePerson("Nowak", "Jan"));
        UniquePerson source = registry.register(new UniquePerson("Nowak", "Jan"));
        PersonAppearance baptism = appearance(BookType.LIBER_BAPTISMORUM, 1900, 1, Role.BAPTIZED);
        PersonAppearance decease = appearance(BookType.LIBER_DEFUNCTORUM, 1960, 5, Role.DECEASED);
        registry.addAppearance(target.uuid(), baptism);
        registry.addAppearance(source.uuid(), decease);

        registry.merge(target.uuid(), source.uuid());

        assertTrue(registry.find(source.uuid()).isEmpty());
        assertEquals(Set.of(baptism, decease), registry.getAppearances(target.uuid()));
        assertEquals(target.uuid(), registry.findOwner(decease).orElseThrow());
    }

    @Test
    void mergeRejectsConflictingUniqueRoles() {
        PersonRegistry registry = new PersonRegistry();
        UniquePerson a = registry.register(new UniquePerson("Nowak", "Jan"));
        UniquePerson b = registry.register(new UniquePerson("Nowak", "Jan"));
        registry.addAppearance(a.uuid(), appearance(BookType.LIBER_BAPTISMORUM, 1900, 1, Role.BAPTIZED));
        registry.addAppearance(b.uuid(), appearance(BookType.LIBER_BAPTISMORUM, 1901, 2, Role.BAPTIZED));

        assertThrows(RepositoryException.class, () -> registry.merge(a.uuid(), b.uuid()));
        assertTrue(registry.find(b.uuid()).isPresent(), "failed merge must not remove source");
    }

    @Test
    void splitExtractsAppearancesToNewPerson() {
        PersonRegistry registry = new PersonRegistry();
        UniquePerson person = registry.register(new UniquePerson("Nowak", "Jan"));
        PersonAppearance baptism = appearance(BookType.LIBER_BAPTISMORUM, 1900, 1, Role.BAPTIZED);
        PersonAppearance decease = appearance(BookType.LIBER_DEFUNCTORUM, 1960, 5, Role.DECEASED);
        registry.addAppearance(person.uuid(), baptism);
        registry.addAppearance(person.uuid(), decease);

        UniquePerson created = registry.split(person.uuid(), Set.of(decease));

        assertNotEquals(person.uuid(), created.uuid());
        assertEquals(person.surname(), created.surname());
        assertEquals(Set.of(baptism), registry.getAppearances(person.uuid()));
        assertEquals(Set.of(decease), registry.getAppearances(created.uuid()));
        assertThrows(RepositoryException.class,
                () -> registry.split(created.uuid(), Set.of(decease)), "cannot extract all appearances");
    }
}
