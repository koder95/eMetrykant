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
 * @version 0.0.146, 2017-08-02
 * @since 0.0.145
 */
class IDSearchStrategy extends SearchStrategy {

    IDSearchStrategy(SearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(Index[] list) {
        if (query == null || query.getID() < 1) return new LinkedList<>();
        
        for (Index i: list) {
            if (i.ID == query.getID()) {
                LinkedList<Index> result = new LinkedList<>();
                result.add(i);
                return result;
            }
        }
        return new LinkedList<>();
    }

}
