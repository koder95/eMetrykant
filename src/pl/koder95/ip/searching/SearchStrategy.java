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
abstract class SearchStrategy {
    
    SearchQuery query;

    public SearchStrategy(SearchQuery query) {
        this.query = query;
    }

    public abstract LinkedList<Index> searchFor(Index[] list);
}
