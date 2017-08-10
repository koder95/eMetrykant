/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.emt.searching;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import pl.koder95.emt.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.145
 */
class AutoSearchStrategy extends SearchStrategy {
    
    private final ANSearchStrategy an;
    private final DataSearchStrategy data;
    private final IDSearchStrategy id;
    private final YearSearchStrategy year;

    public AutoSearchStrategy(AbstractSearchQuery query) {
        super(query);
        an = new ANSearchStrategy(query);
        data = new DataSearchStrategy(query);
        id = new IDSearchStrategy(query);
        year = new YearSearchStrategy(query);
    }

    @Override
    public LinkedList<Index> searchFor(List<Index> list) {
        if (query == null) return new LinkedList<>();
        setQuery(query);
        
        if (query.getID() > 0) return this.id.searchFor(list);
        if (query.getActNumber() != null) return this.an.searchFor(list);
        
        String[] data = query.getData();
        int year = query.getYear();
        System.out.println("data=" + Arrays.toString(data));
        System.out.println("year=" + year);
        if (data != null && data.length != 0) {
            LinkedList<Index> listL;
            
            if (year > 0) listL = this.year.searchFor(list);
            else return this.data.searchFor(list);
            
            return this.data.searchFor(listL);
        }
        if (year > 0) return this.year.searchFor(list);
        return new LinkedList<>();
    }

    @Override
    public void setQuery(AbstractSearchQuery query) {
        this.query = query;
        an.query = query;
        data.query = query;
        id.query = query;
        year.query = query;
    }
}