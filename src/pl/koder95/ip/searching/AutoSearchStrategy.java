/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.searching;

import java.util.LinkedList;
import pl.koder95.ip.idf.ActNumber;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.145
 */
class AutoSearchStrategy extends SearchStrategy {
    
    private final ANSearchStrategy an;
    private final DataSearchStrategy data;
    private final IDSearchStrategy id;

    public AutoSearchStrategy(SearchQuery query) {
        super(query);
        an = new ANSearchStrategy(query);
        data = new DataSearchStrategy(query);
        id = new IDSearchStrategy(query);
    }

    @Override
    public LinkedList<Index> searchFor(Index[] list) {
        if (query == null) return new LinkedList<>();
        int id = query.getID();
        ActNumber lookingFor = query.getActNumber();
        String[] data = query.getData();
        boolean hasID = id > 0;
        boolean hasAN = lookingFor != null;
        boolean hasData = data != null && data.length != 0;
        
        LinkedList<Index> resultA = new LinkedList<>();
        if (hasData) {
            for (Index l: list) {
                boolean add = false;
                for (int i = 0; i < l.getData().length; i++) {
                    if (i < data.length) {
                        if (l.getData(i).startsWith(data[i])) {
                            add = true;
                            break;
                        }
                    }
                }
                if (add) resultA.add(l);
            }
        }
        
        LinkedList<Index> resultB = new LinkedList<>();
        if (hasAN) {
            for (Index i: list) {
                if (lookingFor.equals(i.getActNumber())) resultB.add(i);
            }
        }
        
        LinkedList<Index> resultC = new LinkedList<>();
        if (hasID) {
            for (Index i: list) {
                if (i.ID == id) resultC.add(i);
            }
        }
        
        if (hasData) {
            if (hasAN) {
                if (hasID) {
                    LinkedList<Index> remove = new LinkedList<>(resultA);
                    remove.removeAll(resultB);
                    remove.removeAll(resultC);
                    resultB.clear();
                    resultC.clear();
                    resultA.removeAll(remove);
                    return resultA;
                } else {
                    LinkedList<Index> remove = new LinkedList<>(resultA);
                    remove.removeAll(resultB);
                    resultB.clear();
                    resultA.removeAll(remove);
                    return resultA;
                }
            } else {
                if (hasID) {
                    LinkedList<Index> remove = new LinkedList<>(resultA);
                    remove.removeAll(resultC);
                    resultC.clear();
                    resultA.removeAll(remove);
                    return resultA;
                } else {
                    Index[] result = resultA.toArray(new Index[resultA.size()]);
                    resultA.clear();
                    return resultA;
                }
            }
        } else {
            if (hasAN) {
                if (hasID) {
                    LinkedList<Index> remove = new LinkedList<>(resultB);
                    remove.removeAll(resultC);
                    resultC.clear();
                    resultB.removeAll(remove);
                    return resultB;
                } else return resultB;
            } else {
                if (hasID) return resultC;
                else return new LinkedList<>();
            }
        }
    }
}