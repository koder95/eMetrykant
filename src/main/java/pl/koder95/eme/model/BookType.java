package pl.koder95.eme.model;

/**
 * Typ wyliczeniowy reprezentuje typy ksiąg metrykalnych.
 * Przyjęto nazewnictwo łacińskie.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.5.0, 2025-12-16
 * @since 0.5.0
 */
public enum BookType {
    /**
     * Reprezentuje księgę ochrzczonych.
     */
    LIBER_BAPTISMORUM,
    /**
     * Reprezentuje księgę bierzmowanych.
     */
    LIBER_CONFIRMATORUM,
    /**
     * Reprezentuje księgę zaślubionych.
     */
    LIBER_MATRIMONIORUM,
    /**
     * Reprezentuje księgę zmarłych.
     */
    LIBER_DEFUNCTORUM
}
