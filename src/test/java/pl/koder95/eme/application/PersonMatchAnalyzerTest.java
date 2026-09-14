package pl.koder95.eme.application;

import org.junit.jupiter.api.Test;
import pl.koder95.eme.application.PersonMatchAnalyzer.MatchSuggestion;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.domain.index.Book;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.index.UniqueActNumber;
import pl.koder95.eme.domain.index.UniqueActNumberRegistry;
import pl.koder95.eme.domain.person.PersonAppearanceExtractor;
import pl.koder95.eme.domain.person.PersonAppearanceExtractor.ExtractedPerson;
import pl.koder95.eme.domain.person.PersonRegistry;
import pl.koder95.eme.domain.person.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PersonMatchAnalyzerTest {

    private static final PersonAppearanceExtractor EXTRACTOR = new PersonAppearanceExtractor();

    private record StubRepository(List<Index> indices, UniqueActNumberRegistry registry) implements IndexRepository {

        static StubRepository of(Index... indices) {
            List<Index> list = List.of(indices);
            return new StubRepository(list, UniqueActNumberRegistry.of(list));
        }

        @Override
        public List<Index> getIndices(BookType type) {
            return indices.stream()
                    .filter(i -> i.getUniqueActNumber().bookType() == type)
                    .toList();
        }

        @Override
        public Optional<Index> findByActNumber(UniqueActNumber uan) {
            return registry.find(uan);
        }

        @Override
        public void reloadAll() {
        }
    }

    private static Index index(BookType type, Map<String, String> data) {
        return Index.create(new Book(type.getBookName()), data);
    }

    private static List<ExtractedPerson> extractAll(StubRepository repository) {
        List<ExtractedPerson> extractions = new ArrayList<>();
        repository.indices().forEach(i -> extractions.addAll(EXTRACTOR.extract(i)));
        return extractions;
    }

    @Test
    void unambiguousGroupBecomesSinglePerson() {
        StubRepository repository = StubRepository.of(
                index(BookType.LIBER_BAPTISMORUM, Map.of("an", "1/1900", "surname", "Nowak", "name", "Jan")),
                index(BookType.LIBER_DEFUNCTORUM, Map.of("an", "5/1960", "surname", "Nowak", "name", "Jan"))
        );
        PersonRegistry registry = new PersonRegistry();

        List<MatchSuggestion> suggestions = new PersonMatchAnalyzer(repository)
                .assign(extractAll(repository), registry);

        assertTrue(suggestions.isEmpty());
        assertEquals(1, registry.getPeople().size());
        assertEquals(2, registry.getAppearances(registry.getPeople().iterator().next().uuid()).size());
    }

    @Test
    void conflictingGroupSplitsIntoPersonsWithSuggestions() {
        StubRepository repository = StubRepository.of(
                index(BookType.LIBER_BAPTISMORUM, Map.of(
                        "an", "1/1900", "surname", "Nowak", "name", "Jan",
                        "father-name", "Piotr", "mother-name", "Anna")),
                index(BookType.LIBER_BAPTISMORUM, Map.of(
                        "an", "2/1902", "surname", "Nowak", "name", "Jan",
                        "father-name", "Tomasz", "mother-name", "Maria")),
                index(BookType.LIBER_DEFUNCTORUM, Map.of(
                        "an", "9/1970", "surname", "Nowak", "name", "Jan",
                        "father-name", "Piotr", "mother-name", "Anna"))
        );
        PersonRegistry registry = new PersonRegistry();

        List<MatchSuggestion> suggestions = new PersonMatchAnalyzer(repository)
                .assign(extractAll(repository), registry);

        assertEquals(3, registry.getPeople().size(), "ambiguous data must not be auto-merged");
        // sugestie tylko dla par, których scalenie nie łamie unikalności ról (chrzest+zgon, nie chrzest+chrzest)
        assertEquals(2, suggestions.size());
        assertTrue(suggestions.stream().anyMatch(s -> s.confidence() > 0.5
                        && s.reasons().stream().anyMatch(r -> r.contains("rodziców"))),
                "matching parents should raise confidence");
        assertTrue(suggestions.stream().anyMatch(s -> s.confidence() == 0.5),
                "pair with different parents keeps base confidence");
    }

    @Test
    void marriageProducesTwoPersons() {
        StubRepository repository = StubRepository.of(
                index(BookType.LIBER_MATRIMONIORUM, Map.of(
                        "an", "3/1925",
                        "husband-surname", "Nowak", "husband-name", "Jan",
                        "wife-surname", "Kowalska", "wife-name", "Maria"))
        );
        PersonRegistry registry = new PersonRegistry();

        new PersonMatchAnalyzer(repository).assign(extractAll(repository), registry);

        assertEquals(2, registry.getPeople().size());
        assertTrue(registry.getPeople().stream().anyMatch(p -> p.surname().equals("Kowalska")));
        registry.getPeople().forEach(p -> {
            Role role = registry.getAppearances(p.uuid()).iterator().next().role();
            assertEquals(p.surname().equals("Nowak") ? Role.HUSBAND : Role.WIFE, role);
        });
    }

    @Test
    void conflictedActNumbersAreSkipped() {
        // dwa indeksy z tym samym numerem aktu — UAN skonfliktowany, wystąpienia pomijane
        StubRepository repository = StubRepository.of(
                index(BookType.LIBER_BAPTISMORUM, Map.of("an", "9/1910", "surname", "Duplikat", "name", "Test")),
                index(BookType.LIBER_BAPTISMORUM, Map.of("an", "9/1910", "surname", "Duplikat", "name", "Test2"))
        );
        PersonRegistry registry = new PersonRegistry();

        List<MatchSuggestion> suggestions = new PersonMatchAnalyzer(repository)
                .assign(extractAll(repository), registry);

        assertTrue(suggestions.isEmpty());
        assertTrue(registry.getPeople().isEmpty(), "conflicted acts must not produce persons");
    }

    @Test
    void ownedAppearancesAreNotReassigned() {
        Index baptism = index(BookType.LIBER_BAPTISMORUM, Map.of("an", "1/1900", "surname", "Nowak", "name", "Jan"));
        StubRepository repository = StubRepository.of(baptism);
        PersonRegistry registry = new PersonRegistry();
        PersonMatchAnalyzer analyzer = new PersonMatchAnalyzer(repository);

        analyzer.assign(extractAll(repository), registry);
        int peopleAfterFirstRun = registry.getPeople().size();
        analyzer.assign(extractAll(repository), registry);

        assertEquals(peopleAfterFirstRun, registry.getPeople().size(), "second run must be a no-op");
    }
}
