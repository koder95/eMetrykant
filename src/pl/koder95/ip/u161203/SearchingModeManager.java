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
public class SearchingModeManager {
    
    private final SearchingMode[] modes;
    private SearchingMode current;
    
    public SearchingModeManager (Mediator m) {
        this.modes = new SearchingMode[] { new SearchingMode.Person(m), new SearchingMode.Sex(m), new SearchingMode.Act(m) };
    }
    
    public static final int PERSON_MODE = 0;
    public static final int SEX_MODE = 1;
    public static final int ACT_MODE = 2;
    public void setPersonSearching() {
        setMode(PERSON_MODE);
    }
    
    public void setSexSearching() {
        setMode(SEX_MODE);
    }
    
    public void setActSearching() {
        setMode(ACT_MODE);
    }
    
    public void setMode(int mode) {
        current = modes[mode];
    }

    public SearchingMode getCurrent() {
        return current;
    }
}
