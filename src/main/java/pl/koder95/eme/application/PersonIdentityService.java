package pl.koder95.eme.application;

import lombok.NonNull;
import lombok.extern.java.Log;
import pl.koder95.eme.application.PersonMatchAnalyzer.MatchSuggestion;
import pl.koder95.eme.core.spi.IndexRepository;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.person.PersonAppearance;
import pl.koder95.eme.domain.person.PersonAppearanceExtractor;
import pl.koder95.eme.domain.person.PersonAppearanceExtractor.ExtractedPerson;
import pl.koder95.eme.domain.person.PersonRegistry;
import pl.koder95.eme.domain.person.UniquePerson;
import pl.koder95.eme.io.FilePersonStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Serwis aplikacyjny tożsamości osób: buduje rejestr osób z indeksów,
 * utrwala decyzje w {@code people.xml} i udostępnia operacje scalania/podziału.
 */
@Log
public class PersonIdentityService {

    private final IndexRepository indexRepository;
    private final PersonRegistry registry;
    private final FilePersonStore store;
    private final PersonAppearanceExtractor extractor = new PersonAppearanceExtractor();
    private final PersonMatchAnalyzer analyzer;
    private final List<MatchSuggestion> suggestions = new ArrayList<>();

    public PersonIdentityService(@NonNull IndexRepository indexRepository,
                                 @NonNull PersonRegistry registry,
                                 @NonNull FilePersonStore store) {
        this.indexRepository = indexRepository;
        this.registry = registry;
        this.store = store;
        this.analyzer = new PersonMatchAnalyzer(indexRepository);
    }

    /**
     * Przebudowuje rejestr osób: wczytuje utrwalone tożsamości, przypisuje
     * nowe wystąpienia z indeksów i odświeża sugestie scalenia.
     */
    public synchronized void rebuild() {
        try {
            store.load(registry);
        } catch (IOException ex) {
            log.log(Level.WARNING, "Nie udało się wczytać rejestru osób", ex);
        }
        List<ExtractedPerson> extractions = new ArrayList<>();
        for (BookType type : BookType.values()) {
            indexRepository.getIndices(type).forEach(index -> extractions.addAll(extractor.extract(index)));
        }
        suggestions.clear();
        suggestions.addAll(analyzer.assign(extractions, registry));
        save();
    }

    /**
     * Scala dwie osoby (decyzja ręczna) i utrwala wynik.
     *
     * @return osoba docelowa
     */
    public synchronized UniquePerson merge(UUID targetId, UUID sourceId) {
        UniquePerson merged = registry.merge(targetId, sourceId);
        suggestions.removeIf(s -> involves(s, sourceId));
        save();
        return merged;
    }

    /**
     * Wydziela wskazane wystąpienia do nowej osoby (decyzja ręczna) i utrwala wynik.
     *
     * @return nowo utworzona osoba
     */
    public synchronized UniquePerson split(UUID personId, Set<PersonAppearance> extracted) {
        UniquePerson created = registry.split(personId, extracted);
        save();
        return created;
    }

    public synchronized Collection<UniquePerson> getPeople() {
        return registry.getPeople();
    }

    public synchronized Set<PersonAppearance> getAppearances(UUID personId) {
        return registry.getAppearances(personId);
    }

    public synchronized List<MatchSuggestion> getSuggestions() {
        return Collections.unmodifiableList(new ArrayList<>(suggestions));
    }

    private void save() {
        try {
            store.save(registry);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Nie udało się zapisać rejestru osób", ex);
        }
    }

    private static boolean involves(MatchSuggestion suggestion, UUID personId) {
        return suggestion.first().equals(personId) || suggestion.second().equals(personId);
    }
}
