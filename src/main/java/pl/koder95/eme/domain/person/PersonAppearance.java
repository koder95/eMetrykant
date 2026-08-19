package pl.koder95.eme.domain.person;

import pl.koder95.eme.domain.index.UniqueActNumber;

/**
 * Wystąpienie osoby w akcie: unikalny numer aktu + rola.
 * Wiąże osobę z aktem niezależnie od typu księgi — numer niesie tę informację.
 *
 * @param act  unikalny numer aktu; nie może być {@code null} ani {@link UniqueActNumber#UNKNOWN}
 * @param role rola osoby w akcie
 */
public record PersonAppearance(UniqueActNumber act, Role role) {

    public PersonAppearance {
        if (act == null || UniqueActNumber.UNKNOWN.equals(act)) {
            throw new IllegalArgumentException("act must be a known unique act number");
        }
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }
    }
}
