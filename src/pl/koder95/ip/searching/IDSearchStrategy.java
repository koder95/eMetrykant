/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

package pl.koder95.ip.searching;

import java.util.LinkedList;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
class IDSearchStrategy extends SearchStrategy {

    IDSearchStrategy(SearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(Index[] list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
