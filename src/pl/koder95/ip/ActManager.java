/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import java.util.ArrayList;
import java.util.HashMap;
import pl.koder95.ip.idf.ActNumber;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil
 */
public class ActManager {
    
    private final HashMap<Integer, ArrayList<String>> map = new HashMap<>();

    public ActManager() { }
    
    public void create(String sign, int year) {
        ArrayList<String> acts;
        if (contains(year)) {
            acts = map.get(year);
        } else {
            acts = new ArrayList<>();
        }
        if (acts.contains(sign)) return;
        if (!acts.add(sign)) return;
        if (!contains(year)) map.put(year, acts);
    }
    
    public void create(Index i) {
        if (i != null) create(i.getActNumber());
    }
    
    public void create(ActNumber an) {
        if (an != null) create(an.getSign(), an.getYear());
    }
    
    public boolean contains(String sign, int year) {
        if (contains(year)) {
            ArrayList<String> signs = map.get(year);
            return signs.contains(sign);
        } return false;
    }
    
    public int indexOf(int year, String sign) {
        if (!contains(year)) return -1;
        return map.get(year).indexOf(sign);
    }
    
    public boolean contains(int year) {
        return map.containsKey(year);
    }
    
    public String get(int year, int index) {
        if (!contains(year)) return null;
        return map.get(year).get(index);
    }
    
    public ActPrevNext create(int year, int index) {
        int i0 = index-1, i1 = index, i2 = index+1;
        int y0 = year-1, y1 = year, y2 = year+1;
        if (!contains(y1)) return null;
        if (!contains(y0)) y0 = -1; if (!contains(y2)) y2 = -1;
        
        if (y0 >= 0 && i0 < 0) i0 = map.get(y0).size()-1;
        if (y2 >= 0 && i2 >= map.get(y1).size()) i2 = 0;
        return new ActPrevNext(i0, i1, i2, y0, y1, y2);
    }
    
    public static class ActPrevNext {
        private final int[] i = new int[3], y = new int[3];
        public int getCurrentIndex() { return i[1]; }
        public int getCurrentYear() { return y[1]; }
        public int getNextIndex() { return i[2]; }
        public int getNextYear() { return y[2]; }
        public int getPrevIndex() { return i[0]; }
        public int getPrevYear() { return y[0]; }
        
        private ActPrevNext(int i0, int i1, int i2, int y0, int y1, int y2) {
            i[0] = i2; y[0] = y0;
            i[1] = i1; y[1] = y1;
            i[2] = i0; y[2] = y2;
        }
    }
}
