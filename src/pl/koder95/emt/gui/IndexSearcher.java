/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.emt.gui;

import java.util.List;
import pl.koder95.emt.idf.ActNumber;
import pl.koder95.emt.idf.Index;
import pl.koder95.emt.idf.Indices;
import pl.koder95.emt.searching.AbstractSearchQuery;
import pl.koder95.emt.searching.SearchContext;
import pl.koder95.emt.searching.SearchQuery;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.150, 2017-08-10
 * @since 0.0.147
 */
public class IndexSearcher {
    
    private final SearchContext context = new SearchContext();
    private final Indices indices;

    public IndexSearcher() {
        this(0);
    }

    public IndexSearcher(int option) {
        this(Indices.values()[option]);
    }

    public IndexSearcher(Indices indices) {
        this.indices = indices;
        this.indices.load();
    }

    public Indices getIndices() {
        return indices;
    }

    public List<Index> getLoaded() {
      return indices.getLoaded();
    }
    
    public Index get(int id) {
        return indices.get(id);
    }
    
    public int indexOf(Index index) {
        return index.ID-1;
    }
    
    public boolean isYear(int year) {
        return indices.getLoaded().stream().anyMatch((i)
                -> (i.getActNumber().getYear() == year));
    }
    
    public Index getFirst() {
        return indices.getFirst();
    }
    
    public Index getLast() {
        return indices.getLast();
    }

    public Index[] find(AbstractSearchQuery query) {
        context.setQuery(query);
        Index[] result = context.search(getLoaded());
        return result.length == 0? null : result;
    }

    public Index[] find(String label) {
        String[] words = label.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].endsWith(":")) {
                words[i] = "#" + words[i].substring(0, words[i].lastIndexOf(':'));
            }
        }
        context.setAutoSearch();
        return find(new SearchQuery(String.join(" ", words)));
    }

    public Index[] find(String... data) {
        context.setDataSearch();
        return find(new SearchQuery(String.join(" ", data)));
    }

    public Index[] find(int year, String act) {
        context.setActNumberSearch();
        return find(new SearchQuery(new ActNumber(act, year)));
    }

    public Index[] find(int year) {
        System.out.println("find year " + year);
        context.setYearSearch();
        return find(new SearchQuery("" + year));
    }

    Index selectOne(Index[] result) {
        return context.select(result);
    }

}
