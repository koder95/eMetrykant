package pl.koder95.eme.util;

import java.util.Optional;

/**
 * Rekord umożliwia dostarczenie jednego lub dwóch obiektów do pola, gdy istnieje
 * taka logiczna możliwość.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.5.0, 2025-12-16
 * @since 0.5.0
 *
 * @param first pierwszy obiekt dostarczany jako spodziewany
 * @param second drugi obiekt dostarczany jako alternatywny
 * @param <T> typ wspólny obiektów
 */
public record OneOrTwo<T>(T first, Optional<T> second) {
}
