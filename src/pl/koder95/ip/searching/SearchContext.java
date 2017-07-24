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
public class SearchContext {
    
    private SearchStrategy strategy = new AutoSearchStrategy(null);

    public void setQuery(SearchQuery query) {
        strategy.query = query;
    }

    public Index[] search(Index[] loaded) {
        LinkedList<Index> search = strategy.searchFor(loaded);
        Index[] array = search.toArray(new Index[search.size()]);
        search.clear();
        return array;
    }

    public void setAutoSearch() {
        strategy = new AutoSearchStrategy(strategy.query);
    }

    public void setDataSearch() {
        strategy = new DataSearchStrategy(strategy.query);
    }

    public void setActNumberSearch() {
        strategy = new ANSearchStrategy(strategy.query);
    }

}
