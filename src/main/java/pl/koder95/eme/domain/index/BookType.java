package pl.koder95.eme.domain.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
}
