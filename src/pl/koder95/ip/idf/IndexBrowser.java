/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.idf;

import java.util.Arrays;
import java.util.HashMap;
import pl.koder95.ip.ObjectNotFoundException;
import pl.koder95.tools.Eliminator;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
public class IndexBrowser {
    
    private final HashMap<Integer, HashMap<String, Index>> loaded
            = new HashMap<>();

    public IndexBrowser() {
    }
    
    public void load(Index[] array) {
        for (Index i: array) {
            HashMap<String, Index> indices
                  = loaded.containsKey(i.getActNumber().getYear())?
                    loaded.get(i.getActNumber().getYear()) : new HashMap<>();
            indices.put(i.getActNumber().getSign(), i);
            if (!loaded.containsKey(i.getActNumber().getYear()))
                loaded.put(i.getActNumber().getYear(), indices);
        }
    }
    
    public Index get(String sign, int year) {
        return loaded.get(year).get(sign);
    }
    
    private int indexOf(int year) {
        if (!loaded.containsKey(year)) return -1;
        int index = 0;
        for (Integer i : loaded.keySet()) {
            if (i == year) return index;
            index++;
        }
        return -1;
    }
    
    private int indexOf(String sign, int year) {
        if (!loaded.containsKey(year)) return -1;
        for (Integer i : loaded.keySet()) if (i == year) {
            HashMap<String, Index> yearHM = loaded.get(year);
            if (!yearHM.containsKey(sign)) return -1;
            int index = 0;
            for (String s : yearHM.keySet()) {
                if (s.equals(sign)) return index;
                index++;
            }
            return i;
        }
        return -1;
    }
    
    private Index get(int yearInd, int signInd) {
        int index = 0;
        for (Integer i : loaded.keySet()) {
            if (index == yearInd) {
                HashMap<String, Index> yearHM = loaded.get(i);
                int index2 = 0;
                for (String s : yearHM.keySet()) {
                    if (index2 == signInd) return yearHM.get(s);
                }
            }
            index++;
        }
        return null;
    }
    
    public Index getNext(String sign, int year) {
        int yI = indexOf(year);
        if (yI < 0) return null;
        int sI = indexOf(sign, year);
        if (sI < 0) return null;
        Index i = get(yI, sI+1);
        if (i == null) i = get(yI+1, sI+1);
        return i;
    }
    
    public Index getPrev(String sign, int year) {
        int yI = indexOf(year);
        if (yI < 0) return null;
        int sI = indexOf(sign, year);
        if (sI < 0) return null;
        Index i = get(yI, sI-1);
        if (i == null) i = get(yI-1, sI-1);
        return i;
    }
    
    public Index findData(String query) throws ObjectNotFoundException {
        String[] data = query.split(" ");
        if (data != null) {
            for (HashMap<String, Index> hash: loaded.values()) {
                Eliminator<Index> e = new Eliminator(
                        hash.values().toArray(new Index[hash.values().size()])
                );
                e.addFilter((i) -> !Arrays.equals(data, i.getData()));
                
                Object[] result = e.getResult();
                if (result.length == 1) return (Index) result[0];
                
                e.addFilter((i) -> i.getData().length > data.length);
                e.addFilter((i) -> {
                    for (String d: data) {
                        for (String m: i.getData()) {
                            if (d.equals(m)) return false;
                        }
                    }
                    return true;
                });
                Index i = (Index) e.getResultAndConsume()[0];
                System.out.print("pl.koder95.ip.idf.IndexBrowser.findData()");
                System.out.print(" = ");
                System.out.println(i);
                e.clear();
                return i;
            }
        }
        throw new ObjectNotFoundException(Index.class, query);
    }
}
