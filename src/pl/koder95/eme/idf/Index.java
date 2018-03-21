/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.eme.idf;

import java.text.Collator;
import java.util.Arrays;
import pl.koder95.eme.Main;
import pl.koder95.eme.dfs.ActNumber;

/**
 * Klasa reprezentuje indeks, czyli zbiór danych posiadających unikalny
 * identyfikator nadawany podczas wczytywania indeksu ze źródła. Może również
 * posiadać numer aktu, który sprawia, że można odszukać dany indeks w księgach
 * przechowywanych na parafii.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.0.201
 */
public abstract class Index implements Comparable<Index> {

    /**
     * Unikalny identyfikator nadawany podczas wczytywania danych do programu.
     */
    public final int ID;

    Index(int id) {
        this.ID = id;
    }

    /**
     * @return dane
     */
    public abstract String[] getData();

    /**
     * @param index określa, którą konkretnie informacje należy zwrócić
     * @return konkretna informacja, pochodzi z danych
     */
    public abstract String getData(int index);

    /**
     * @return numer aktu
     */
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
