package pl.koder95.eme.domain.index;

import pl.koder95.eme.Main;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

/**
 * Rekord zawiera dane o numerze aktu, który jest unikalny w systemie.
 * Jeśli {@code bookType == null || year < MIN_YEAR || year > ACTUAL_YEAR || signNumber < 1}
 * to rekord przyjmuje wartości domyślne (oprócz {@code signSuffix}, który ustawiany jest na
 * wartość {@code ""}) i jest tożsamy z {@link UniqueActNumber#UNKNOWN nieznanym numerem aktu}.
 *
 * @param bookType   typ księgi, w której znajduje się numer aktu
 * @param year       rok wystawienia aktu
 * @param signNumber liczba umieszczona w sygnaturze aktu
 * @param signSuffix ciąg znaków występujący po liczbie umieszczonej w sygnaturze aktu
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.5.0, 2025-12-16
 * @since 0.5.0
 */
public record UniqueActNumber(BookType bookType, int year, int signNumber, String signSuffix)
        implements Comparable<UniqueActNumber> {

    private static final int MIN_YEAR = 1563; // — the year of the Council of Trent ended
    private static final int ACTUAL_YEAR = LocalDate.now().getYear();

    /**
     * Instancja rekordu przeznaczona do oddania sytuacji,
     * gdy akt zawiera numer nieznany lub błędny (np. nieunikalny, niejednoznaczny, wybrakowany).
     */
    public static final UniqueActNumber UNKNOWN = new UniqueActNumber(null, 0, 0, "");

    private static final Comparator<UniqueActNumber> STANDARD_COMPARATOR =
            Comparator.nullsLast(new StandardComparator());

    public UniqueActNumber(BookType bookType, int year, int signNumber, String signSuffix) {
        boolean unknown = bookType == null || year < MIN_YEAR || year > ACTUAL_YEAR || signNumber < 1;
        this.bookType = unknown ? null : bookType;
        this.year = unknown ? 0 : year;
        this.signNumber = unknown ? 0 : signNumber;
        this.signSuffix = unknown || signSuffix == null ? "" : signSuffix;
    }

    /**
     * Buduje unikalny numer aktu z typu księgi i legacy {@link ActNumber}.
     */
    public static UniqueActNumber from(BookType bookType, ActNumber actNumber) {
        if (actNumber == null) {
            return null;
        }
        if (bookType == null) {
            return UNKNOWN;
        }
        ActNumberSign sign = splitSign(actNumber.getSign());
        if (sign == null) {
            return UNKNOWN;
        }
        return new UniqueActNumber(bookType, actNumber.getYear(), sign.number(), sign.suffix());
    }

    /**
     * Rozpoznaje typ księgi po nazwie wyświetlanej i buduje unikalny numer.
     */
    public static UniqueActNumber from(String bookName, ActNumber actNumber) {
        return from(resolveBookType(bookName), actNumber);
    }

    private static BookType resolveBookType(String bookName) {
        if (bookName == null || bookName.isBlank()) {
            return null;
        }
        for (BookType type : BookType.values()) {
            if (type.getBookName().equalsIgnoreCase(bookName.trim())) {
                return type;
            }
        }
        return null;
    }

    private static ActNumberSign splitSign(String sign) {
        if (sign == null || sign.isBlank()) {
            return null;
        }
        int lastDigitIndex = -1;
        for (int i = 0; i < sign.length(); i++) {
            if (Character.isDigit(sign.charAt(i))) {
                lastDigitIndex = i;
            } else {
                break;
            }
        }
        if (lastDigitIndex < 0) {
            return null;
        }
        int number = Integer.parseInt(sign.substring(0, lastDigitIndex + 1));
        String suffix = lastDigitIndex == sign.length() - 1 ? "" : sign.substring(lastDigitIndex + 1);
        return new ActNumberSign(number, suffix);
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

    private record ActNumberSign(int number, String suffix) {
    }

    private static class StandardComparator implements Comparator<UniqueActNumber> {

        @Override
        public int compare(UniqueActNumber o1, UniqueActNumber o2) {
            if (o1.bookType == null || o2.bookType == null) {
                return Objects.compare(o1.bookType, o2.bookType, Comparator.nullsLast(Enum::compareTo));
            }
            int compared = o1.bookType.compareTo(o2.bookType);
            if (compared != 0) {
                return compared;
            }
            compared = Integer.compare(o1.year, o2.year);
            if (compared != 0) {
                return compared;
            }
            compared = Integer.compare(o1.signNumber, o2.signNumber);
            if (compared != 0) {
                return compared;
            }
            return Main.DEFAULT_COLLATOR.compare(o1.signSuffix, o2.signSuffix);
        }
    }
}
