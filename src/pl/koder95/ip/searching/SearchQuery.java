/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.searching;

import pl.koder95.ip.idf.ActNumber;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ActNumber getActNumber() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    String[] getData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
