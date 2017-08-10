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
 * @since 0.0.147
 */
class YearSearchStrategy extends SearchStrategy {

    YearSearchStrategy(AbstractSearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null || query.getYear() < 0) {
            System.out.println("query.getYear()=" + query.getYear());
            return new LinkedList<>();
        }
        
            System.out.println("query.getYear()=" + query.getYear());
        LinkedList<Index> result = new LinkedList<>();
        for (Index i: list) {
            if (i.getActNumber().getYear() == query.getYear()) {
                result.add(i);
            }
        }
        return result;
    }

}
