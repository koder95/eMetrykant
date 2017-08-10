/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.emt.searching;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import pl.koder95.emt.idf.ActNumber;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.145
 */
public class SearchQuery extends AbstractSearchQuery {
    
    private final SearchPhrase phrase;

    public SearchQuery(SearchPhrase phrase) {
        this.phrase = phrase;
    }

    public SearchQuery(String value) {
        this(SearchPhrase.createDefault(value));
    }

    public SearchQuery(ActNumber an) {
        this(an.toString());
    }

    public SearchQuery(int id) {
        this("#" + id);
    }

    @Override
    int getID() {
        if (phrase.getIDWordIndex() < 0) return -1;
        String idWord = phrase.getIDWord();
        try {
            return Integer.parseInt(idWord.substring(1));
        } catch (NumberFormatException ex) { return -1; }
    }

    @Override
    int getYear() {
        int year;
        try (Scanner scan = new Scanner(String.join(" ", phrase.getWords()))) {
            scan.useDelimiter(" ");
            year = scan.nextInt();
        } catch (InputMismatchException ex) {
            ActNumber an = getActNumber();
            if (an == null) return -1;
            return an.getYear();
        }
        return year;
    }

    @Override
    ActNumber getActNumber() {
        if (phrase.getANWordIndex() < 0) return null;
        
        ActNumber an = ActNumber.parseActNumber(phrase.getANWord());
        if (an!= null) return an;
        
        String[] words = phrase.getWords();
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

    @Override
    String[] getData() {
        LinkedList<String> data = new LinkedList<>();
        data.addAll(Arrays.asList(phrase.getWords()));
        int idIndex = phrase.getIDWordIndex();
        int anIndex = phrase.getANWordIndex();
        
        if (idIndex >= 0) {
            if (anIndex >= 0) {
                if (idIndex == anIndex) data.remove(idIndex);
                else if (idIndex > anIndex) {
                    data.remove(idIndex);
                    data.remove(anIndex);
                }
                else {
                    data.remove(anIndex);
                    data.remove(idIndex);
                }
            }
            else data.remove(idIndex);
        }
        else if (anIndex >= 0) data.remove(anIndex);
        if (getYear() > 0) data.remove(getYear() + "");
        
        return data.toArray(new String[data.size()]);
    }

    @Override
    String getData(int i) {
        return getData()[i];
    }

    @Override
    public SearchFilter getIDFilter() {
        return (i)-> {
            return getID() == i.ID;
        };
    }

    @Override
    public SearchFilter getActNumberFilter() {
        return (i)-> {
            return getActNumber().compareTo(i.getActNumber()) == 0;
        };
    }

    @Override
    public SearchFilter getYearFilter() {
        return (i)-> {
            return getYear() == i.getActNumber().getYear();
        };
    }

    @Override
    public SearchFilter getDataFilter() {
        return (i)-> {
            String[] data = getData();
            boolean accept = true;
            for (int ii = 0; ii < data.length; ii++) {
                accept = accept && i.getData(ii).startsWith(data[ii]);
            }
            return accept;
        };
    }
}
