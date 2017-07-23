/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

package pl.koder95.ip.idf;

import java.util.List;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
public enum Indices {

    LIBER_BAPTIZATORUM,
    LIBER_CONFIRMATORUM,
    LIBER_MATRIMONIORUM,
    LIBER_DEFUNCTORUM;
    
    private List<Index> loaded;
    
    public Index get(int id) {
        return loaded.get(id-1);
    }

    public List<Index> getLoaded() {
        return loaded;
    }
    
    public int load(String line) {
        int id = loaded.size();
        if (loaded.add(RealIndex.create(id, line))) return id;
        return -1;
    }
}
