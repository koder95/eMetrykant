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
 * @since 0.0.145
 */
class DataSearchStrategy extends SearchStrategy {

    DataSearchStrategy(AbstractSearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null || query.getData().length == 0)
            return new LinkedList<>();
        
        LinkedList<Index> result = new LinkedList<>();
        String[] qData = query.getData();
        for (Index element: list) {
            boolean add = false;
            for (int i = 0; i < element.getData().length; i++) {
                if (i >= qData.length) break;
                if (element.getData(i).startsWith(qData[i])) add = true;
            }
            if (add) result.add(element);
        }
        return result;
    }

}
