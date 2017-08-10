/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

package pl.koder95.emt.searching;

import java.util.LinkedList;
import java.util.List;
import pl.koder95.emt.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.145
 */
abstract class SearchStrategy {
    
    AbstractSearchQuery query;

    public SearchStrategy(AbstractSearchQuery query) {
        this.query = query;
    }

    public abstract LinkedList<Index> searchFor(List<Index> list);

    public void setQuery(AbstractSearchQuery query) {
        this.query = query;
    }
}
