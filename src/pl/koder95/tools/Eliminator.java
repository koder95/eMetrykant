/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.tools;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Kamil Jan Mularski
 */
public class Eliminator<T> {
    private final ArrayList<T> array = new ArrayList<>();
    private final ArrayList<Filter<T>> filters = new ArrayList<>();
    
    public Eliminator(T[] t) {
        array.addAll(Arrays.asList(t));
    }
    
    private void accept(Filter<T>[] ff) {
        boolean wlife = true;
        int index = 0;
        while (wlife) {
            if (index < 0) index = 0;
            T t = array.get(index);
            boolean c = false;
            for (Filter<T> f: ff) c = c || f.requestRemoving(t);
            if (c) {
                array.remove(t);
                index--;
            }
            else index++;
            if (index >= array.size()) wlife = false;
        }
    }
    
    public void addFilter(Filter<T> f) {
        filters.add(f);
    }
    
    public Object[] getResult() {
        accept(filters.toArray(new Filter[filters.size()]));
        return (T[]) array.toArray();
    }
        
    public Object[] getResultAndConsume() {
        Object[] result = getResult();
        filters.clear();
        return result;
    }
    
    public int getFilterCount() {
        return filters.size();
    }

    public void clear() {
        array.clear();
        filters.clear();
    }

    public static interface Filter<T> {
        boolean requestRemoving(T t);
    }
}
