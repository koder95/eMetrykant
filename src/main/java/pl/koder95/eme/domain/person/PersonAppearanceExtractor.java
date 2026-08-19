package pl.koder95.eme.domain.person;

import pl.koder95.eme.domain.index.Index;
import pl.koder95.eme.domain.index.UniqueActNumber;
import pl.koder95.eme.util.OneOrTwo;

import java.util.List;
import java.util.Optional;

/**
 * Wyciąga z indeksu podmioty aktu jako kandydatów na {@link UniquePerson osoby}.
 *
 * <p>Akty chrztu, bierzmowania i zgonu mają jeden podmiot; akt małżeństwa dwa
 * (mąż i żona) — stąd wynik w postaci {@link OneOrTwo}. Indeksy bez
 * jednoznacznego {@link UniqueActNumber} są pomijane, bo wystąpienia
 * odwołują się do aktów wyłącznie przez ich unikalne numery.</p>
 */
public class PersonAppearanceExtractor {

    /**
     * Kandydat na osobę: dane personalne z indeksu + wystąpienie w akcie.
     */
    public record ExtractedPerson(String surname, String name, PersonAppearance appearance) {

        /**
         * @return klucz grupowania po znormalizowanych danych personalnych
         */
        public String personalDataKey() {
            return (normalize(surname) + "|" + normalize(name)).toLowerCase();
        }
    }

    /**
     * @return podmioty aktu albo {@link Optional#empty()} gdy indeksu nie da się
     * jednoznacznie powiązać z aktem
     */
    public Optional<OneOrTwo<ExtractedPerson>> extractSubjects(Index index) {
        if (index == null) {
            return Optional.empty();
        }
        UniqueActNumber uan = index.getUniqueActNumber();
        if (uan == null || UniqueActNumber.UNKNOWN.equals(uan) || uan.bookType() == null) {
            return Optional.empty();
        }
        return switch (uan.bookType()) {
            case LIBER_BAPTISMORUM -> single(index, uan, Role.BAPTIZED);
            case LIBER_CONFIRMATORUM -> single(index, uan, Role.CONFIRMED);
            case LIBER_DEFUNCTORUM -> single(index, uan, Role.DECEASED);
            case LIBER_MATRIMONIORUM -> couple(index, uan);
        };
    }

    /**
     * @return podmioty aktu jako płaska lista (małżeństwo daje dwa elementy)
     */
    public List<ExtractedPerson> extract(Index index) {
        return extractSubjects(index)
                .map(subjects -> subjects.second()
                        .map(second -> List.of(subjects.first(), second))
                        .orElseGet(() -> List.of(subjects.first())))
                .orElseGet(List::of);
    }

    private Optional<OneOrTwo<ExtractedPerson>> single(Index index, UniqueActNumber uan, Role role) {
        ExtractedPerson person = extracted(index.getData("surname"), index.getData("name"), uan, role);
        return person == null
                ? Optional.empty()
                : Optional.of(new OneOrTwo<>(person, Optional.empty()));
    }

    private Optional<OneOrTwo<ExtractedPerson>> couple(Index index, UniqueActNumber uan) {
        ExtractedPerson husband = extracted(
                index.getData("husband-surname"), index.getData("husband-name"), uan, Role.HUSBAND);
        ExtractedPerson wife = extracted(
                index.getData("wife-surname"), index.getData("wife-name"), uan, Role.WIFE);
        if (husband == null && wife == null) {
            return Optional.empty();
        }
        if (husband == null) {
            return Optional.of(new OneOrTwo<>(wife, Optional.empty()));
        }
        return Optional.of(new OneOrTwo<>(husband, Optional.ofNullable(wife)));
    }

    private static ExtractedPerson extracted(String surname, String name, UniqueActNumber uan, Role role) {
        String normalizedSurname = normalize(surname);
        String normalizedName = normalize(name);
        if (normalizedSurname.isBlank() && normalizedName.isBlank()) {
            return null;
        }
        return new ExtractedPerson(normalizedSurname, normalizedName, new PersonAppearance(uan, role));
    }

    private static final String UTF8_BOM = "\uFEFF";

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.startsWith(UTF8_BOM) ? value.substring(1) : value;
        return normalized.trim();
    }
}
