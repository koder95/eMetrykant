/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.idf;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
class RealIndex extends Index {
    public final ActNumber AN;
    private final String[] data;

    private RealIndex(int id, ActNumber an, String[] data) {
        super(id);
        this.AN = an;
        this.data = data;
    }

    @Override
    public ActNumber getActNumber() {
        return AN;
    }

    @Override
    public String[] getData() {
        return data;
    }

    @Override
    public String getData(int index) {
        return data[index];
    }

    @Override
    public String toString() {
        return ID + ": " + AN + " " + Arrays.deepToString(data);
    }
    
    public static RealIndex create(int id, String line) {
        ArrayList<String> dataL = new ArrayList<>();
        dataL.addAll(Arrays.asList(line.split(";")));
        int year = Integer.parseInt(dataL.remove(dataL.size()-1));
        String sign = dataL.remove(dataL.size()-1);
        String[] data = dataL.toArray(new String[dataL.size()]);
        dataL.clear();
        return create(id, sign, year, data);
    }
    
    public static RealIndex create(int id, String sign, int year, String[] data) {
        return new RealIndex(id, new ActNumber(sign, year), data);
    }
}
