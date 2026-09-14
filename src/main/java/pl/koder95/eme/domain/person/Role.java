package pl.koder95.eme.domain.person;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Rola osoby w akcie metrykalnym.
 *
 * <p>Role oznaczone jako {@link #isUniquePerPerson() unikalne} mogą wystąpić
 * u jednej osoby najwyżej raz (człowiek ma jeden chrzest, jedno bierzmowanie
 * i jeden zgon); role małżeńskie mogą się powtarzać.</p>
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    BAPTIZED(true),
    CONFIRMED(true),
    HUSBAND(false),
    WIFE(false),
    DECEASED(true);

    private final boolean uniquePerPerson;
}
