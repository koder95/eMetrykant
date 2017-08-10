/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.emt.searching;
import pl.koder95.emt.idf.ActNumber;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.147
 */
public abstract class AbstractSearchQuery {
    
    public abstract SearchFilter getIDFilter();
    public abstract SearchFilter getActNumberFilter();
    public abstract SearchFilter getYearFilter();
    public abstract SearchFilter getDataFilter();

    abstract int getID();
    abstract int getYear();
    abstract ActNumber getActNumber();
    abstract String[] getData();

    String getData(int i) {
        return getData()[i];
    }
}
