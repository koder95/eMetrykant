/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

package pl.koder95.ip.searching;

import java.util.LinkedList;
import java.util.List;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.147
 */
class FilterSearchStrategy extends SearchStrategy {

    private SearchFilter filter;
    
    FilterSearchStrategy(AbstractSearchQuery query) {
        this(query, DEFAULT_FILTER);
    }
    
    FilterSearchStrategy(AbstractSearchQuery query, SearchFilter filter) {
        super(query);
        this.filter = filter;
    }
    
    void setFilter(SearchFilter f) {
        this.filter = f;
    }
    
    void setDefaultFilter() {
        setFilter(DEFAULT_FILTER);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null || filter == null) return new LinkedList<>();
        
        LinkedList<Index> result = new LinkedList<>();
        for (Index i: list) {
            if (filter.accept(i)) result.add(i);
        }
        return result;
    }

    private static final SearchFilter DEFAULT_FILTER = (i)-> true;
}
