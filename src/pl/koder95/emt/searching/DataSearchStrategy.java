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
 * @version 0.0.150, 2017-08-10
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
            for (int i = 0; i < qData.length; i++) {
                for (int d = i; d < element.getData().length; d+=2) {
                    String dataE = element.getData(d);
                    String dataQ = qData[i];
                    if (dataE.startsWith(dataQ)) add = true;
                }
            }
            if (add) result.add(element);
        }
        return result;
    }

}
