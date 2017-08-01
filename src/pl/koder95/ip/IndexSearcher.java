/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip;

import pl.koder95.ip.idf.ActNumber;
import pl.koder95.ip.idf.Index;
import pl.koder95.ip.idf.Indices;
import pl.koder95.ip.searching.SearchContext;
import pl.koder95.ip.searching.SearchQuery;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.145
 */
public class IndexSearcher extends IndexBrowser {
    
    private final SearchContext context = new SearchContext();

    public IndexSearcher() {
        this(0);
    }

    public IndexSearcher(int option) {
        this(Indices.values()[option]);
    }

    public IndexSearcher(Indices indices) {
        super(indices);
    }

    public Index find(SearchQuery query) throws ObjectNotFoundException {
        context.setQuery(query);
        System.out.println("query=" + query);
        Index[] result = context.search(getLoaded());
        if (result.length == 0) return null;
        else {
            int iterator = 0;
            for (Index i: result) {
                System.out.println(iterator++ + ": " + i);
            }
            return result[0];
        }
    }

    @Override
    public Index find(String label) throws ObjectNotFoundException {
        String[] words = label.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].endsWith(":")) {
                words[i] = "#" + words[i].substring(0, words[i].lastIndexOf(':'));
            }
        }
        label = String.join(" ", words);
        System.out.println("label=" + label);
        context.setAutoSearch();
        return find(new SearchQuery(label));
    }

    @Override
    public Index find(String lastName, String name) throws ObjectNotFoundException {
        context.setDataSearch();
        return find(lastName + " " + name);
    }

    @Override
    public Index find(int year, String act) throws ObjectNotFoundException {
        context.setActNumberSearch();
        return find(new SearchQuery(new ActNumber(act, year)));
    }

    @Override
    public Index[] find(Person p) throws ObjectNotFoundException {
        context.setDataSearch();
        System.out.println(p);
        return super.find(p); //To change body of generated methods, choose Tools | Templates.
    }

}
