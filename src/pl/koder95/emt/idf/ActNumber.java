/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.emt.idf;

import java.util.regex.Matcher;
import pl.koder95.emt.Main;
import static pl.koder95.emt.Main.DIGITS_STRING_PATTERN;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.136
 */
public class ActNumber implements Comparable<ActNumber> {
    private final String sign;
    private final int year;

    public ActNumber(String sign, int year) {
        this.sign = sign;
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
        return sign + "/" + year;
    }

    @Override
    public int compareTo(ActNumber o) {
        if (year > o.year) return 1;
        else if (year < o.year) return -1;
        else {
            Matcher digit1 = DIGITS_STRING_PATTERN.matcher(sign);
            Matcher digit2 = DIGITS_STRING_PATTERN.matcher(o.sign);
            if (digit1.find() && digit2.find()) {
                int act1 = Integer.parseInt(digit1.group(1));
                int act2 = Integer.parseInt(digit2.group(1));
                return act1 > act2? 1: act1 == act2? 0: -1;
            }
            String remainder1 = sign.substring(digit1.end(1)+1);
            String remainder2 = sign.substring(digit2.end(1)+1);
            return Main.DEFAULT_COLLATOR.compare(remainder1, remainder2);
        }
    }
    
    public static ActNumber parseActNumber(String s) {
        if (!s.contains("/")) return null;
        
        int slash = s.indexOf("/");
        if (slash == 0 || slash == s.length()-1) return null;
        
        String sign = s.substring(0, slash);
        String yearS = s.substring(slash+1);
        try {
            int year = Integer.parseInt(yearS);
            return new ActNumber(sign, year);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
