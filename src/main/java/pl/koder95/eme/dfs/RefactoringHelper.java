package pl.koder95.eme.dfs;

import pl.koder95.eme.model.BookType;
import pl.koder95.eme.model.UniqueActNumber;

import java.util.function.Function;

/**
 * Ta klasa zostanie usunięta po zakończeniu procesu refaktoryzacji.
 */
@Deprecated
class RefactoringHelper {

    public static UniqueActNumber convert(ActNumber an, String bookName) {
        if (an == null) return null;
        if (bookName == null || bookName.isBlank()) return UniqueActNumber.UNKNOWN;
        BookType bookType = BOOK_TYPE_CONVERTER.apply(bookName);
        ActNumberSign sign = ACT_NUMBER_CONVERTER.apply(an.getSign());
        return new UniqueActNumber(bookType, an.getYear(), sign.number(), sign.suffix());
    }

    private static final Function<String, ActNumberSign> ACT_NUMBER_CONVERTER = (sign) -> {
        int lastDigitIndex = -1;
        for (int i = 0; i < sign.length(); i++) {
            if (Character.isDigit(sign.charAt(i))) {
                lastDigitIndex = i;
            } else break;
        }
        if (lastDigitIndex < 0) return null;
        String digits = sign.substring(0, lastDigitIndex + 1);
        int number = Integer.parseInt(digits);
        if (lastDigitIndex == sign.length() - 1) {
            return new ActNumberSign(number, "");
        }
        String suffix = sign.substring(lastDigitIndex + 1);
        return new ActNumberSign(number, suffix);
    };

    private static final Function<String, BookType> BOOK_TYPE_CONVERTER = (name) -> {
        if (name.equals(IndexList.LIBER_BAPTISMORUM.getName())) {
            return BookType.LIBER_BAPTISMORUM;
        } else if (name.equals(IndexList.LIBER_CONFIRMATORUM.getName())) {
            return BookType.LIBER_CONFIRMATORUM;
        } else if (name.equals(IndexList.LIBER_MATRIMONIORUM.getName())) {
            return BookType.LIBER_MATRIMONIORUM;
        } else if (name.equals(IndexList.LIBER_DEFUNCTORUM.getName())) {
            return BookType.LIBER_DEFUNCTORUM;
        }
        return null;
    };

    record ActNumberSign(int number, String suffix) { }
}
