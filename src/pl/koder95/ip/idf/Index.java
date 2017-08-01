/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.idf;

import java.text.Collator;
import java.util.Arrays;
import pl.koder95.ip.Main;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.136
 */
public abstract class Index implements Comparable<Index> {
    public final int ID;

    Index(int id) {
        this.ID = id;
    }

    public abstract String[] getData();
    public abstract String getData(int index);
    public abstract ActNumber getActNumber();

    @Override
    public String toString() {
       return ID + ": " + getActNumber() + " " + Arrays.deepToString(getData());
    }

    @Override
    public int compareTo(Index o) {
        Collator col = Main.DEFAULT_COLLATOR;
        int ln = col.compare(getData(0), o.getData(0));
        return ln == 0? col.compare(getData(1), o.getData(1)) : ln;
    }
}
