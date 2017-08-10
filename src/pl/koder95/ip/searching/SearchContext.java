/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

package pl.koder95.ip.searching;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.150, 2017-08-10
 * @since 0.0.145
 */
public class SearchContext {
    
    private SearchStrategy strategy = new AutoSearchStrategy(null);

    public void setQuery(AbstractSearchQuery query) {
        strategy.query = query;
    }

    public Index[] search(List<Index> loaded) {
        System.out.println("Strategy: " + strategy);
        System.out.println("searching...");
        LinkedList<Index> search = strategy.searchFor(loaded);
        Index[] array = search.toArray(new Index[search.size()]);
        for (Index i: array) {
            if (i == null) search.remove(i);
        }
        array = search.toArray(new Index[search.size()]);
        search.clear();
        System.out.println("searching done");
        System.out.println(java.util.Arrays.toString(array));
        return array;
    }
    
    public Index select(LinkedList<Index> searchResult) {
        if (searchResult == null || searchResult.isEmpty()) return null;
        if (searchResult.size() == 1) return searchResult.remove();
        Index theBest = searchResult.pollFirst();
        while (!searchResult.isEmpty())
            theBest = better(theBest, searchResult.pollFirst());
        return theBest;
    }
    
    public Index select(Index[] searchResultArray) {
        if (searchResultArray == null || searchResultArray.length == 0)
            return null;
        
        LinkedList<Index> searchResult = new LinkedList<>();
        searchResult.addAll(Arrays.asList(searchResultArray));
        return select(searchResult);
    }
    
    private Index better(Index i0, Index i1) {
        double s0 = similarity(i0), s1 = similarity(i1);
        return s0 > s1? i0 : i1;
    }
    
    private double similarity(Index i) {
        if (strategy.query.getID() == i.ID) return 1d;
        if (strategy.query.getActNumber()!= null) {
            if (strategy.query.getActNumber().compareTo(i.getActNumber()) == 0)
                return 1d;
        }
        
        double similarity = 1d;
        for (int ii = 0; ii < strategy.query.getData().length; ii++) {
            String data0 = strategy.query.getData(ii);
            for (int d = ii; d < i.getData().length; d+=2) {
                String data1 = i.getData(d);
                similarity*= similarity(data0, data1);
            }
        }
        return similarity;
    }
    
    private double similarity(String s0, String s1) {
        int length = s0.length() > s1.length()? s1.length() : s0.length();
        double similar = 0;
        for (int i = 0; i < length; i++) {
            if (s0.charAt(i) != s1.charAt(i)) break;
            similar++;
        }
        return (s1.length() == s0.length()? 1 : 0.5)*(similar/length);
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

    public void setYearSearch() {
        strategy = new YearSearchStrategy(strategy.query);
    }

    public void setFilterSearch(SearchFilter filter) {
        strategy = new FilterSearchStrategy(strategy.query, filter);
    }
}
