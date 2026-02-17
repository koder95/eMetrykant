package pl.koder95.eme.domain.index;

import pl.koder95.eme.Main;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;

import static pl.koder95.eme.Main.DIGITS_STRING_PATTERN;

/**
 * Reprezentuje numer aktu (sygnatura/rok).
 */
public class ActNumber implements Comparable<ActNumber>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String sign;
    private final int year;

    public ActNumber(String sign, int year) {
        String normalizedSign = Objects.requireNonNull(sign, "sign must not be null").trim();
        if (normalizedSign.isEmpty()) {
            throw new IllegalArgumentException("sign must not be blank");
        }
        this.sign = normalizedSign;
        this.year = year;
    }

    public String getSign() {
        return sign;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return getSign() + "/" + getYear();
    }

    @Override
    public int compareTo(ActNumber o) {
        int compared = Integer.compare(getYear(), o.getYear());
        if (compared != 0) {
            return compared;
        }

        Matcher digit1 = DIGITS_STRING_PATTERN.matcher(getSign());
        Matcher digit2 = DIGITS_STRING_PATTERN.matcher(o.getSign());

        boolean hasDigits1 = digit1.find() && digit1.group(1) != null && !digit1.group(1).isEmpty();
        boolean hasDigits2 = digit2.find() && digit2.group(1) != null && !digit2.group(1).isEmpty();

        if (hasDigits1 && hasDigits2) {
            int act1 = Integer.parseInt(digit1.group(1));
            int act2 = Integer.parseInt(digit2.group(1));
            int byNumber = Integer.compare(act1, act2);
            if (byNumber != 0) {
                return byNumber;
            }

            String remainder1 = getSign().substring(Math.min(getSign().length(), digit1.end(1)));
            String remainder2 = o.getSign().substring(Math.min(o.getSign().length(), digit2.end(1)));
            return Main.DEFAULT_COLLATOR.compare(remainder1, remainder2);
        }

        return Main.DEFAULT_COLLATOR.compare(getSign(), o.getSign());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActNumber other)) {
            return false;
        }
        return year == other.year && sign.equals(other.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sign, year);
    }

    public static ActNumber parseActNumber(String s) {
        if (s == null || !s.contains("/")) {
            return null;
        }
        int slash = s.indexOf('/');
        if (slash == 0 || slash == s.length() - 1) {
            return null;
        }
        String sign = s.substring(0, slash);
        String yearS = s.substring(slash + 1);
        try {
            int year = Integer.parseInt(yearS);
            return new ActNumber(sign, year);
        } catch (NumberFormatException | IllegalArgumentException ex) {
            return null;
        }
    }
}
