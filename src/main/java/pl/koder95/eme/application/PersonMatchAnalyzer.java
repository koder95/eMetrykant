package pl.koder95.eme.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.person.PersonAppearance;
import pl.koder95.eme.domain.person.PersonAppearanceExtractor.ExtractedPerson;
import pl.koder95.eme.domain.person.PersonRegistry;
import pl.koder95.eme.domain.person.Role;
import pl.koder95.eme.domain.person.UniquePerson;
import pl.koder95.eme.model.RepositoryException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Analizator tożsamości osób — rozstrzyga, czy wystąpienia o tych samych
 * danych personalnych mogą dotyczyć jednej fizycznej osoby.
 *
 * <p>Polityka konserwatywna: automatycznie jednej osobie przypisywana jest
 * grupa wystąpień tylko wtedy, gdy nie narusza to unikalności ról (najwyżej
 * jeden chrzest, jedno bierzmowanie, jeden zgon). W pozostałych przypadkach
 * każde wystąpienie staje się osobną osobą, a analizator produkuje
 * {@link MatchSuggestion sugestie scalenia} do ręcznej decyzji.</p>
 */
@Log
@RequiredArgsConstructor
public class PersonMatchAnalyzer {

    /**
     * Sugestia scalenia dwóch osób.
     *
     * @param first      pierwsza osoba
     * @param second     druga osoba
     * @param confidence pewność w przedziale (0, 1]
     * @param reasons    powody sugestii (czytelne dla człowieka)
     */
    public record MatchSuggestion(UUID first, UUID second, double confidence, List<String> reasons) {
    }

    private final IndexRepository indexRepository;

    /**
     * Przypisuje nieprzypisane wystąpienia do osób w rejestrze i zwraca sugestie scalenia.
     *
     * @param extractions kandydaci wyciągnięci z indeksów
     * @param registry    rejestr osób (modyfikowany)
     * @return sugestie scalenia osób o tych samych danych personalnych
     */
    public List<MatchSuggestion> assign(List<ExtractedPerson> extractions, PersonRegistry registry) {
        Map<String, List<ExtractedPerson>> groups = new LinkedHashMap<>();
        for (ExtractedPerson extraction : extractions) {
            if (registry.isOwned(extraction.appearance())) {
                continue;
            }
            if (indexRepository.findByActNumber(extraction.appearance().act()).isEmpty()) {
                // UAN nieznany albo skonfliktowany — wystąpienie nie wskazuje jednoznacznie aktu
                log.warning(() -> "Pominięto wystąpienie ze skonfliktowanym numerem aktu: "
                        + extraction.appearance().act());
                continue;
            }
            groups.computeIfAbsent(extraction.personalDataKey(), ignored -> new ArrayList<>()).add(extraction);
        }

        List<MatchSuggestion> suggestions = new ArrayList<>();
        for (List<ExtractedPerson> group : groups.values()) {
            if (canBeSinglePerson(group)) {
                assignToSinglePerson(group, registry);
            } else {
                List<UUID> created = assignEachSeparately(group, registry);
                suggestions.addAll(suggestMerges(created, registry));
            }
        }
        return suggestions;
    }

    private boolean canBeSinglePerson(List<ExtractedPerson> group) {
        Map<Role, Integer> uniqueRoleCounts = new EnumMap<>(Role.class);
        for (ExtractedPerson extraction : group) {
            Role role = extraction.appearance().role();
            if (role.isUniquePerPerson()
                    && uniqueRoleCounts.merge(role, 1, Integer::sum) > 1) {
                return false;
            }
        }
        return true;
    }

    private void assignToSinglePerson(List<ExtractedPerson> group, PersonRegistry registry) {
        ExtractedPerson first = group.get(0);
        UniquePerson person = registry.register(new UniquePerson(first.surname(), first.name()));
        for (ExtractedPerson extraction : group) {
            registry.addAppearance(person.uuid(), extraction.appearance());
        }
    }

    private List<UUID> assignEachSeparately(List<ExtractedPerson> group, PersonRegistry registry) {
        List<UUID> created = new ArrayList<>();
        for (ExtractedPerson extraction : group) {
            UniquePerson person = registry.register(new UniquePerson(extraction.surname(), extraction.name()));
            registry.addAppearance(person.uuid(), extraction.appearance());
            created.add(person.uuid());
        }
        return created;
    }

    private List<MatchSuggestion> suggestMerges(List<UUID> persons, PersonRegistry registry) {
        List<MatchSuggestion> suggestions = new ArrayList<>();
        for (int i = 0; i < persons.size(); i++) {
            for (int j = i + 1; j < persons.size(); j++) {
                UUID a = persons.get(i);
                UUID b = persons.get(j);
                if (!unionKeepsRolesUnique(a, b, registry)) {
                    continue;
                }
                List<String> reasons = new ArrayList<>();
                reasons.add("Te same dane personalne");
                double confidence = 0.5;
                if (parentsMatch(a, b, registry)) {
                    reasons.add("Zgodne dane rodziców w powiązanych aktach");
                    confidence += 0.3;
                }
                suggestions.add(new MatchSuggestion(a, b, confidence, List.copyOf(reasons)));
            }
        }
        return suggestions;
    }

    private boolean unionKeepsRolesUnique(UUID a, UUID b, PersonRegistry registry) {
        Map<Role, Integer> counts = new EnumMap<>(Role.class);
        for (UUID personId : List.of(a, b)) {
            for (PersonAppearance appearance : registry.getAppearances(personId)) {
                Role role = appearance.role();
                if (role.isUniquePerPerson() && counts.merge(role, 1, Integer::sum) > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean parentsMatch(UUID a, UUID b, PersonRegistry registry) {
        Optional<String> parentsA = parentsOf(a, registry);
        Optional<String> parentsB = parentsOf(b, registry);
        return parentsA.isPresent() && parentsA.equals(parentsB);
    }

    private Optional<String> parentsOf(UUID personId, PersonRegistry registry) {
        for (PersonAppearance appearance : registry.getAppearances(personId)) {
            Optional<Index> index = indexRepository.findByActNumber(appearance.act());
            if (index.isEmpty()) {
                continue;
            }
            String father = index.get().getData("father-name").trim();
            String mother = index.get().getData("mother-name").trim();
            if (!father.isBlank() || !mother.isBlank()) {
                return Optional.of((father + "|" + mother).toLowerCase(Locale.ROOT));
            }
        }
        return Optional.empty();
    }

    /**
     * Scala dwie osoby po ręcznym potwierdzeniu sugestii.
     *
     * @throws RepositoryException gdy scalenie narusza spójność rejestru
     */
    public UniquePerson merge(MatchSuggestion suggestion, PersonRegistry registry) {
        return registry.merge(suggestion.first(), suggestion.second());
    }
}
