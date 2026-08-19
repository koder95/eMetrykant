package pl.koder95.eme.domain.person;

import java.util.UUID;

/**
 * Rekord zawiera dane osoby, który z powodu UUID jest widziany przez system jako niepowtarzalny.
 * Jeśli {@code uuid == null || surname == null || name == null || surname.isBlank() || name.isBlank()}
 * to rekord przyjmuje wartości takie jak {@link UniquePerson#UNKNOWN nieznana osoba}.
 *
 * @param uuid    uniwersalny unikalny identyfikator
 * @param surname nazwisko
 * @param name    imię lub imiona rozdzielone spacją
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.5.0, 2025-12-16
 * @since 0.5.0
 */
public record UniquePerson(UUID uuid, String surname, String name) {

    private static final UUID NIL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String NOMEN = "N.";
    public static final UniquePerson UNKNOWN = new UniquePerson(NIL_UUID, NOMEN, NOMEN);

    public UniquePerson(UUID uuid, String surname, String name) {
        boolean unknown = uuid == null || surname == null || name == null
                || surname.isBlank() || name.isBlank();
        this.uuid = unknown ? NIL_UUID : uuid;
        this.surname = unknown ? NOMEN : surname;
        this.name = unknown ? NOMEN : name;
    }

    /**
     * Alternatywny konstruktor, który domyślnie generuje UUID.
     */
    public UniquePerson(String surname, String name) {
        this(UUID.randomUUID(), surname, name);
    }
}
