/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

package pl.koder95.ip.searching;

import java.util.LinkedList;
import java.util.List;
import pl.koder95.ip.idf.ActNumber;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.145
 */
class ANSearchStrategy extends SearchStrategy {

    ANSearchStrategy(AbstractSearchQuery query) {
        super(query);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null || query.getActNumber() == null)
            return new LinkedList<>();
        
        LinkedList<Index> result = new LinkedList<>();
        ActNumber an = query.getActNumber();
        list.stream().filter((i) -> (i.getActNumber().compareTo(an) == 0))
                .forEach((i) -> result.add(i));
        return result;
    }

}
