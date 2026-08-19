package pl.koder95.eme.application;

import pl.koder95.eme.core.spi.MutableIndexRepository;
import pl.koder95.eme.domain.index.ActNumber;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Serwis aplikacyjny odpowiedzialny za zarządzanie rekordami indeksów:
 * dodawanie, modyfikowanie, usuwanie i utrwalanie zmian.
 *
 * <p>Zmiany trafiają najpierw do repozytorium trzymanego w pamięci, dopiero
 * {@link #save()} zapisuje je trwale, a {@link #discardChanges()} pozwala
 * porzucić modyfikacje i wrócić do ostatnio zapisanego stanu.</p>
 */
public class IndexManagementService {

    /**
     * Znak, którym w danych rozdzielane są kolejne imiona i człony nazwisk.
     */
    public static final char NAME_SEPARATOR = '_';

    private static final String ACT_NUMBER = "an";

    private final MutableIndexRepository indexRepository;
    private boolean unsavedChanges;

    public IndexManagementService(MutableIndexRepository indexRepository) {
        this.indexRepository = Objects.requireNonNull(indexRepository, "indexRepository must not be null");
    }

    public List<Index> getIndices(BookType type) {
        return indexRepository.getIndices(type);
    }

    /**
     * Sprawdza dane rekordu względem schematu księgi.
     *
     * @param type typ księgi
     * @param data atrybuty rekordu
     * @return nazwy pól, których wartości są nieprawidłowe; pusta lista oznacza dane poprawne
     */
    public List<String> validate(BookType type, Map<String, String> data) {
        Objects.requireNonNull(type, "type must not be null");
        Map<String, String> normalized = normalize(data);
        List<String> invalid = new ArrayList<>();
        for (String field : type.getFieldSchema()) {
            String value = normalized.getOrDefault(field, "");
            boolean valid = ACT_NUMBER.equals(field)
                    ? ActNumber.parseActNumber(value) != null
                    : !value.isBlank();
            if (!valid) {
                invalid.add(field);
            }
        }
        return invalid;
    }

    public Index add(BookType type, Map<String, String> data) {
        requireValid(type, data);
        Index added = indexRepository.add(type, normalize(data));
        unsavedChanges = true;
        return added;
    }

    public Index update(BookType type, Index index, Map<String, String> data) {
        requireValid(type, data);
        Index updated = indexRepository.replace(type, index, normalize(data));
        unsavedChanges = true;
        return updated;
    }

    public boolean remove(BookType type, Index index) {
        boolean removed = indexRepository.remove(type, index);
        if (removed) {
            unsavedChanges = true;
        }
        return removed;
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }

    /**
     * Utrwala wszystkie zmiany wprowadzone od ostatniego zapisu.
     */
    public void save() {
        indexRepository.saveAll();
        unsavedChanges = false;
    }

    /**
     * Porzuca niezapisane zmiany, wczytując ponownie ostatnio zapisany stan danych.
     */
    public void discardChanges() {
        indexRepository.reloadAll();
        unsavedChanges = false;
    }

    /**
     * Przygotowuje dane do zapisu: usuwa zbędne odstępy, a w polach osobowych zamienia
     * odstępy na {@link #NAME_SEPARATOR znak rozdzielający}, którego oczekuje reszta programu.
     */
    private Map<String, String> normalize(Map<String, String> data) {
        Map<String, String> normalized = new LinkedHashMap<>();
        if (data == null) {
            return normalized;
        }
        data.forEach((field, value) -> {
            if (field == null || field.isBlank()) {
                return;
            }
            String trimmed = value == null ? "" : value.trim();
            normalized.put(field, ACT_NUMBER.equals(field) ? trimmed : trimmed.replace(' ', NAME_SEPARATOR));
        });
        return normalized;
    }

    private void requireValid(BookType type, Map<String, String> data) {
        List<String> invalid = validate(type, data);
        if (!invalid.isEmpty()) {
            throw new IllegalArgumentException("Nieprawidłowe pola rekordu: " + invalid);
        }
    }
}
