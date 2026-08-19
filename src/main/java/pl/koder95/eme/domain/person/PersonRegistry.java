package pl.koder95.eme.domain.person;

import pl.koder95.eme.model.RepositoryException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Rejestr fizycznych osób i ich wystąpień w aktach.
 *
 * <p>Inwarianty:</p>
 * <ul>
 *     <li>para (akt, rola) należy najwyżej do jednej osoby,</li>
 *     <li>osoba ma najwyżej jedno wystąpienie roli {@link Role#isUniquePerPerson() unikalnej}.</li>
 * </ul>
 *
 * <p>Operacje {@link #merge(UUID, UUID)} i {@link #split(UUID, Set)} pozwalają
 * scalać dane omyłkowo rozdzielone między dwie osoby oraz dzielić dane
 * omyłkowo przypisane jednej osobie.</p>
 */
public class PersonRegistry {

    private final Map<UUID, UniquePerson> people = new LinkedHashMap<>();
    private final Map<UUID, Set<PersonAppearance>> appearances = new LinkedHashMap<>();
    private final Map<PersonAppearance, UUID> owners = new HashMap<>();

    /**
     * Rejestruje osobę bez wystąpień.
     *
     * @throws RepositoryException gdy osoba jest nieznana albo już zarejestrowana
     */
    public UniquePerson register(UniquePerson person) {
        if (person == null || UniquePerson.UNKNOWN.equals(person)) {
            throw new RepositoryException("Cannot register an unknown person");
        }
        if (people.containsKey(person.uuid())) {
            throw new RepositoryException("Person already registered: " + person.uuid());
        }
        people.put(person.uuid(), person);
        appearances.put(person.uuid(), new LinkedHashSet<>());
        return person;
    }

    /**
     * Przypisuje osobie wystąpienie w akcie.
     *
     * @throws RepositoryException gdy wystąpienie należy już do innej osoby
     * albo narusza unikalność roli
     */
    public void addAppearance(UUID personId, PersonAppearance appearance) {
        UniquePerson person = require(personId);
        Objects.requireNonNull(appearance, "appearance must not be null");
        UUID owner = owners.get(appearance);
        if (owner != null) {
            if (owner.equals(personId)) {
                return;
            }
            throw new RepositoryException("Appearance " + appearance + " already belongs to person " + owner);
        }
        Set<PersonAppearance> own = appearances.get(person.uuid());
        if (appearance.role().isUniquePerPerson()
                && own.stream().anyMatch(a -> a.role() == appearance.role())) {
            throw new RepositoryException("Person " + personId + " already has a "
                    + appearance.role() + " appearance");
        }
        own.add(appearance);
        owners.put(appearance, personId);
    }

    /**
     * Scala dwie osoby w jedną: wystąpienia {@code sourceId} przechodzą
     * na {@code targetId}, a osoba źródłowa znika z rejestru.
     *
     * @return osoba docelowa
     * @throws RepositoryException gdy scalenie naruszyłoby unikalność ról
     * (np. obie osoby mają akt chrztu)
     */
    public UniquePerson merge(UUID targetId, UUID sourceId) {
        UniquePerson target = require(targetId);
        UniquePerson source = require(sourceId);
        if (targetId.equals(sourceId)) {
            return target;
        }
        Set<PersonAppearance> targetApps = appearances.get(targetId);
        Set<PersonAppearance> sourceApps = appearances.get(sourceId);
        for (PersonAppearance sourceApp : sourceApps) {
            if (sourceApp.role().isUniquePerPerson()
                    && targetApps.stream().anyMatch(a -> a.role() == sourceApp.role())) {
                throw new RepositoryException("Cannot merge " + source.uuid() + " into " + target.uuid()
                        + ": both have a " + sourceApp.role() + " appearance");
            }
        }
        sourceApps.forEach(app -> owners.put(app, targetId));
        targetApps.addAll(sourceApps);
        appearances.remove(sourceId);
        people.remove(sourceId);
        return target;
    }

    /**
     * Wydziela część wystąpień osoby do nowej osoby (nowy UUID, te same dane personalne).
     *
     * @return nowo utworzona osoba
     * @throws RepositoryException gdy wskazane wystąpienia nie należą do osoby
     * albo obejmują wszystkie jej wystąpienia
     */
    public UniquePerson split(UUID personId, Set<PersonAppearance> extracted) {
        UniquePerson person = require(personId);
        if (extracted == null || extracted.isEmpty()) {
            throw new RepositoryException("Nothing to split from person " + personId);
        }
        Set<PersonAppearance> own = appearances.get(personId);
        if (!own.containsAll(extracted)) {
            throw new RepositoryException("Some appearances do not belong to person " + personId);
        }
        if (own.size() == extracted.size()) {
            throw new RepositoryException("Cannot extract all appearances from person " + personId);
        }
        UniquePerson created = register(new UniquePerson(person.surname(), person.name()));
        own.removeAll(extracted);
        Set<PersonAppearance> createdApps = appearances.get(created.uuid());
        createdApps.addAll(extracted);
        extracted.forEach(app -> owners.put(app, created.uuid()));
        return created;
    }

    public Optional<UniquePerson> find(UUID personId) {
        return Optional.ofNullable(people.get(personId));
    }

    public Collection<UniquePerson> getPeople() {
        return Collections.unmodifiableCollection(people.values());
    }

    public Set<PersonAppearance> getAppearances(UUID personId) {
        require(personId);
        return Collections.unmodifiableSet(appearances.get(personId));
    }

    /**
     * @return właściciel wystąpienia, jeśli ktoś je już posiada
     */
    public Optional<UUID> findOwner(PersonAppearance appearance) {
        return Optional.ofNullable(owners.get(appearance));
    }

    public boolean isOwned(PersonAppearance appearance) {
        return owners.containsKey(appearance);
    }

    private UniquePerson require(UUID personId) {
        UniquePerson person = people.get(Objects.requireNonNull(personId, "personId must not be null"));
        if (person == null) {
            throw new RepositoryException("Unknown person: " + personId);
        }
        return person;
    }
}
