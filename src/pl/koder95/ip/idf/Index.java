/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.idf;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import pl.koder95.ip.Main;

/**
 *
 * @author Kamil
 */
public class Index implements Comparable<Index> {
    public final int ID;
    public final ActNumber AN;
    private final String[] data;

    private Index(int id, ActNumber an, String[] data) {
        this.ID = id;
        this.AN = an;
        this.data = data;
    }

    public String[] getData() {
        return data;
    }

    public String getData(int index) {
        return data[index];
    }

    @Override
    public String toString() {
        return ID + ": " + AN + " " + Arrays.deepToString(data);
    }
    
    public static Index create(int id, String line) {
        ArrayList<String> dataL = new ArrayList<>();
        dataL.addAll(Arrays.asList(line.split(";")));
        int year = Integer.parseInt(dataL.remove(dataL.size()-1));
        String sign = dataL.remove(dataL.size()-1);
        String[] data = dataL.toArray(new String[dataL.size()]);
        dataL.clear();
        return create(id, sign, year, data);
    }
    
    public static Index create(int id, String sign, int year, String[] data) {
        return new Index(id, new ActNumber(sign, year), data);
    }

    @Override
    public int compareTo(Index o) {
        Collator col = Main.DEFAULT_COLLATOR;
        int ln = col.compare(data[0], o.data[0]);
        //System.out.println("C " + data[0] + " " + o.data[0] + " " + ln);
        if (ln == 0) {
            return col.compare(data[1], o.data[1]);
        }
        return ln;
    }
}
