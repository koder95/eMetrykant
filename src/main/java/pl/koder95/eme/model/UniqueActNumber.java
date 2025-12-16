package pl.koder95.eme.model;

import pl.koder95.eme.Main;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * Rekord zawiera dane o numerze aktu, który jest unikalny w systemie.
 * Jeśli {@code bookType == null || year < MIN_YEAR || year > ACTUAL_YEAR || signNumber < 1}
 * to rekord przyjmuje wartości domyślne (oprócz {@code signSuffix}, który ustawiany jest na
 * wartość {@code ""}) i jest tożsamy z {@link UniqueActNumber#UNKNOWN nieznanym numerem aktu}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.5.0, 2025-12-16
 * @since 0.5.0
 *
 * @param bookType typ księgi, w której znajduje się numer aktu
 * @param year rok wystawienia aktu
 * @param signNumber liczba umieszczona w sygnaturze aktu
 * @param signSuffix ciąg znaków występujący po liczbie umieszczonej w sygnaturze aktu
 */
public record UniqueActNumber(BookType bookType, int year, int signNumber, String signSuffix)
        implements Comparable<UniqueActNumber> {

    private static final int MIN_YEAR = 1563; // — the year of the Council of Trent ended
    private static final int ACTUAL_YEAR = LocalDate.now().getYear();

    /**
     * Domyślny konstruktor rekordu
     * @param bookType typ księgi, w której znajduje się numer aktu
     * @param year rok wystawienia aktu
     * @param signNumber liczba umieszczona w sygnaturze aktu
     * @param signSuffix ciąg znaków występujący po liczbie umieszczonej w sygnaturze aktu
     */
    public UniqueActNumber(BookType bookType, int year, int signNumber, String signSuffix) {
        boolean unknown = bookType == null || year < MIN_YEAR || year > ACTUAL_YEAR || signNumber < 1;
        this.bookType = unknown ? null : bookType;
        this.year = unknown ? 0 : year;
        this.signNumber = unknown ? 0 : signNumber;
        this.signSuffix = unknown || signSuffix == null ? "" : signSuffix;
    }

    @Override
    public int compareTo(UniqueActNumber o) {
        return STANDARD_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        if (bookType == null) {
            return "eme.uan:unknown";
        }
        return "eme.uan:" + bookType.name() + '/' + year + '/' + signNumber + signSuffix;
    }

    private static final Comparator<UniqueActNumber> STANDARD_COMPARATOR = Comparator.nullsLast(new StandardComparator());

    /**
     * Instancja rekordu przeznaczona do oddania sytuacji,
     * gdy akt zawiera numer nieznany lub błędny (np. nieunikalny, niejednoznaczny, wybrakowany).
     */
    public static final UniqueActNumber UNKNOWN = new UniqueActNumber(null, 0, 0, "");

    private static class StandardComparator implements Comparator<UniqueActNumber> {

        @Override
        public int compare(UniqueActNumber o1, UniqueActNumber o2) {
            // 1. Porównywanie typów ksiąg
            int compared = o1.bookType.compareTo(o2.bookType);
            if (compared != 0) {
                return compared;
            }
            // 2. Porównywanie roczników
            compared = Integer.compare(o1.year, o2.year);
            if (compared != 0) {
                return compared;
            }
            // 3. Porównywanie liczb sygnatur
            compared = Integer.compare(o1.signNumber, o2.signNumber);
            if (compared != 0) {
                return compared;
            }
            // 4. Porównywanie ciągów znaków dołączonych do sygnatur
            return Main.DEFAULT_COLLATOR.compare(o1.signSuffix, o2.signSuffix);
        }

    }
}
