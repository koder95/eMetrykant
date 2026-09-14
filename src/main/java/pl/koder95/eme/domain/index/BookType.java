package pl.koder95.eme.domain.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Typ księgi indeksowej.
 */
@Getter
@RequiredArgsConstructor
public enum BookType {
    LIBER_BAPTISMORUM("Księga ochrzczonych", List.of("name", "surname", "an")),
    LIBER_CONFIRMATORUM("Księga bierzmowanych", List.of("name", "surname", "an")),
    LIBER_MATRIMONIORUM("Księga zaślubionych", List.of("husband-surname", "husband-name", "wife-surname", "wife-name", "an")),
    LIBER_DEFUNCTORUM("Księga zmarłych", List.of("name", "surname", "an"));

    private final String bookName;
    private final List<String> fieldSchema;

    /**
     * Odnajduje typ księgi po nazwie wyświetlanej (bez rozróżniania wielkości liter).
     *
     * @param bookName nazwa wyświetlana księgi
     * @return typ księgi albo {@link Optional#empty()}
     */
    public static Optional<BookType> ofBookName(String bookName) {
        if (bookName == null || bookName.isBlank()) {
            return Optional.empty();
        }
        String normalized = bookName.trim();
        return Arrays.stream(values())
                .filter(type -> type.getBookName().equalsIgnoreCase(normalized))
                .findFirst();
    }
}
