/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.searching;

import java.util.Arrays;
import java.util.LinkedList;
import pl.koder95.ip.idf.ActNumber;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.145
 */
public class SearchQuery {
    
    private final String value;

    public SearchQuery(String value) {
        this.value = value;
    }

    public SearchQuery(ActNumber an) {
        this(an.toString());
    }

    public SearchQuery(int id) {
        this("#" + id);
    }

    int getID() {
        try {
            return Integer.parseInt(value.substring(1));
        } catch (NumberFormatException ex) {/* do nothing */}
        
        String[] words = value.split(" ");
        for (String word: words) {
            if (word.startsWith("#")) {
                try {
                    return Integer.parseInt(value.substring(1));
                } catch (NumberFormatException ex) {/* do nothing */}
            }
        }
        return -1;
    }

    ActNumber getActNumber() {
        ActNumber an = ActNumber.parseActNumber(value);
        if (an!= null) return an;
        if (!value.contains("/")) return null;
        
        String[] words = value.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("/")) {
                if (words[i].equals("/") && i-1 >= 0 && i+1 < words.length) {
                    return ActNumber
                            .parseActNumber(words[i-1] + "/" + words[i+1]);
                }
                int slash = words[i].indexOf("/");
                if (slash != 0 && slash != words[i].length()-1)
                    return ActNumber.parseActNumber(words[i]);
            }
        }
        return null;
    }

    String[] getData() {
        LinkedList<String> data = new LinkedList<>();
        data.addAll(Arrays.asList(value.split(" ")));
        data.stream().filter((s)->{
            boolean test = !s.startsWith("#");
            test = test && !s.contains("/");
            return test;
        });
        return data.toArray(new String[data.size()]);
    }

    String getData(int i) {
        return getData()[i];
    }

}
