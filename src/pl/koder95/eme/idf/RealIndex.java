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
 * @version 0.1.5, 2017-09-08
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

    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        try {
            for (int i = 0; i < data.length; i++) {
                data[i] = null;
            }
        } finally {
            super.finalize();
        }
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
     * @exception IllegalArgumentException linia nie jest pusta, a nie zawiera
     * delimitera ({@code ';'})
     * @return nowy rzeczywisty indeks, {@code null} - linia jest pusta lub
     * rozpoczyna się od delimitera ({@code ';'})
     */
    public static RealIndex create(int id, String line)
            throws IllegalArgumentException{
        if (line.isEmpty()) return null;
        if (line.startsWith(";")) return null;
        if (!line.contains(";")) 
            throw new IllegalArgumentException(
                    "line does not contains specified delimiter"
            );
        
        ArrayList<String> dataL = new ArrayList<>();
        dataL.addAll(Arrays.asList(line.split(";")));
        if (dataL.get(dataL.size()-1).matches("( )+"))
            dataL.remove(dataL.size()-1);
        
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
     * @param an numer aktu
     * @param data dane, jakie zawierać ma indeks
     * @return nowy rzeczywisty indeks
     * @since 0.1.4
     */
    static RealIndex create(int id, ActNumber an, String[] data) {
        return new RealIndex(id, an, data);
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
        return create(id, new ActNumber(sign, year), data);
    }
}
