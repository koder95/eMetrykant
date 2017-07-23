/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.idf;

import java.text.Collator;
import java.util.regex.Matcher;
import pl.koder95.ip.Main;
import static pl.koder95.ip.Main.DIGITS_STRING_PATTERN;

/**
 *
 * @author Kamil
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
}
