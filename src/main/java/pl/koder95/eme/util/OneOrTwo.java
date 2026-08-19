package pl.koder95.eme.util;

import java.util.Optional;

/**
 * Rekord umożliwia dostarczenie jednego lub dwóch obiektów do pola, gdy istnieje
 * taka logiczna możliwość.
 *
 * @param <T>    typ wspólny obiektów
 * @param first  pierwszy obiekt dostarczany jako spodziewany
 * @param second drugi obiekt dostarczany jako alternatywny
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.5.0, 2025-12-16
 * @since 0.5.0
 */
public record OneOrTwo<T>(T first, Optional<T> second) {
    public OneOrTwo {
        if (first == null) {
            throw new IllegalArgumentException("first cannot be null");
        }
        if (second == null) {
            throw new IllegalArgumentException("second Optional cannot be null");
        }
    }
}
