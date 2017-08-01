/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

package pl.koder95.ip.searching;

import java.util.LinkedList;
import pl.koder95.ip.idf.ActNumber;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.145
 */
class ANSearchStrategy extends SearchStrategy {

    ANSearchStrategy(SearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(Index[] list) {
        if (query == null || query.getActNumber() == null)
            return new LinkedList<>();
        
        LinkedList<Index> result = new LinkedList<>();
        ActNumber an = query.getActNumber();
        for (Index i: list) {
            if (i.getActNumber().compareTo(an) == 0) result.add(i);
        }
        return result;
    }

}
