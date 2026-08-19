package pl.koder95.eme.domain.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Rejestr pilnujący unikalności {@link UniqueActNumber} w obrębie wczytanych indeksów.
 *
 * <p>Numer, który wystąpił więcej niż raz, przestaje być kluczem wyszukiwania —
 * trafia do {@link #getConflicts() raportu konfliktów} i wymaga ręcznej poprawki
 * danych źródłowych. Indeksy z {@link UniqueActNumber#UNKNOWN nieznanym numerem}
 * gromadzone są osobno.</p>
 */
public class UniqueActNumberRegistry {

    private final Map<UniqueActNumber, Index> unique = new LinkedHashMap<>();
    private final Map<UniqueActNumber, List<Index>> conflicts = new LinkedHashMap<>();
    private final List<Index> unknown = new ArrayList<>();

    /**
     * Buduje rejestr z kolekcji indeksów.
     */
    public static UniqueActNumberRegistry of(Collection<Index> indices) {
        UniqueActNumberRegistry registry = new UniqueActNumberRegistry();
        if (indices != null) {
            indices.forEach(registry::register);
        }
        return registry;
    }

    /**
     * Rejestruje indeks. Kolizja przenosi numer do puli konfliktów.
     */
    public void register(Index index) {
        if (index == null) {
            return;
        }
        UniqueActNumber uan = index.getUniqueActNumber();
        if (uan == null || UniqueActNumber.UNKNOWN.equals(uan)) {
            unknown.add(index);
            return;
        }
        List<Index> conflicted = conflicts.get(uan);
        if (conflicted != null) {
            conflicted.add(index);
            return;
        }
        Index existing = unique.putIfAbsent(uan, index);
        if (existing != null && existing != index) {
            unique.remove(uan);
            List<Index> entry = new ArrayList<>();
            entry.add(existing);
            entry.add(index);
            conflicts.put(uan, entry);
        }
    }

    /**
     * Zwraca indeks o podanym numerze, o ile numer jest jednoznaczny.
     *
     * @param uan unikalny numer aktu
     * @return indeks albo {@link Optional#empty()} gdy numer nie istnieje,
     * jest {@link UniqueActNumber#UNKNOWN} albo jest skonfliktowany
     */
    public Optional<Index> find(UniqueActNumber uan) {
        if (uan == null || UniqueActNumber.UNKNOWN.equals(uan)) {
            return Optional.empty();
        }
        return Optional.ofNullable(unique.get(uan));
    }

    /**
     * @return numery przypisane do więcej niż jednego indeksu wraz z tymi indeksami
     */
    public Map<UniqueActNumber, List<Index>> getConflicts() {
        return Collections.unmodifiableMap(conflicts);
    }

    /**
     * @return indeksy bez rozpoznawalnego unikalnego numeru
     */
    public List<Index> getUnknown() {
        return Collections.unmodifiableList(unknown);
    }

    /**
     * @return liczba jednoznacznie zarejestrowanych numerów
     */
    public int size() {
        return unique.size();
    }
}
