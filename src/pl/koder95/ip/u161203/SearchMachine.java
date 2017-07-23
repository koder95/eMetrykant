/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161203;

/**
 *
 * @author Kamil
 */
public abstract class SearchMachine {
    
    private final String query;

    private SearchMachine(String query) {
        this.query = query;
    }
    
    public Index array(Index[] indices) {
        for (Index i: indices) {
            String face = createFace(i);
            if (face != null && query.equals(face)) return i;
        }
        return null;
    }
    
    public abstract String createFace(Index i);
    
    public static SearchMachine findPerson(String query) {
        return new SearchMachine(query) {
            @Override
            public String createFace(Index i) {
                if (i.getSize() == 4) {
                    return i.getValue(0) + " " + i.getValue(1);
                }
                return null;
            }
        };
    }
    
    public static SearchMachine findMale(String query) {
        return new SearchMachine(query) {
            @Override
            public String createFace(Index i) {
                if (i.getSize() == 6) {
                    return i.getValue(0) + " " + i.getValue(1);
                }
                return null;
            }
        };
    }
    
    public static SearchMachine findFemale(String query) {
        return new SearchMachine(query) {
            @Override
            public String createFace(Index i) {
                if (i.getSize() == 6) {
                    return i.getValue(2) + " " + i.getValue(3);
                }
                return null;
            }
        };
    }
    
    public static SearchMachine findAct(String query) {
        return new SearchMachine(query) {
            @Override
            public String createFace(Index i) {
                if (i.getSize() >= 2) {
                    int act = i.getSize()-2, year = i.getSize()-1;
                    return i.getValue(act) + "/" + i.getValue(year);
                }
                return null;
            }
        };
    }
}
