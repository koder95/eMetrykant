/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.eme.idf;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Rzeczywisty indeks, czyli klasa przechowująca każdą informację o indeksie,
 * nie tylko jego identyfikator, jak w przypadku {@link VirtualIndex indeksu
 * wirtualnego}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.201, 2017-08-16
 * @since 0.0.201
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
    
    /**
     * @param indices zbiór indeksów, z którego pochodzi ten indeks
     * @return indeks wirtualny, wolniejszy, ale lżejszy
     */
    public VirtualIndex toVirtualIndex(Indices indices) {
        return new VirtualIndex(ID, indices);
    }
    
    /**
     * Tworzy nowy indeks z podanym identyfikatorem. Numerem aktu i dane
     * odczytywane są z ciągu znaków.
     * 
     * @param id identyfikator
     * @param line linia, ciąg znaków pochodzący np. z pliku
     * @return nowy rzeczywisty indeks
     */
    public static RealIndex create(int id, String line) {
        if (line.isEmpty()) return null;
        if (line.startsWith(";")) return null;
        ArrayList<String> dataL = new ArrayList<>();
        dataL.addAll(Arrays.asList(line.split(";")));
        
        int year = Integer.parseInt(dataL.remove(dataL.size()-1));
        String sign = dataL.remove(dataL.size()-1);
        String[] data = dataL.toArray(new String[dataL.size()]);
        dataL.clear();
        return create(id, sign, year, data);
    }
    
    /**
     * Tworzy indeks na podstawie argumentów.
     * 
     * @param id identyfikator
     * @param sign sygnatura
     * @param year rok
     * @param data dane, jakie zawierać ma indeks
     * @return nowy rzeczywisty indeks
     */
    public static RealIndex create(int id, String sign, int year,
            String[] data) {
        return new RealIndex(id, new ActNumber(sign, year), data);
    }
}
