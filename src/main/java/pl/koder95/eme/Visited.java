package pl.koder95.eme;

/**
 * Interfejs dla wzorca Odwiedzający.
 * Odwiedzający ({@link Visitor}) odwiedza odwiedzanego ({@link Visited}).
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.1, 2020-05-20
 * @since 0.3.1
 */
public interface Visited {

    /**
     * Zezwolenie na odwiedzenie jest domyślne i następuje przez wywołanie metody
     * {@link Visitor#visit(Visited) visitor.visit(this)}.
     *
     * @param visitor odwiedzający ten obiekt
     */
    default void accept(Visitor<Visited> visitor) {
        visitor.visit(Visited.this);
    }
}
