/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161203;

import java.util.Objects;

/**
 *
 * @author Kamil
 */
public abstract class SearchingMode {
    
    protected final Mediator mediator;
    
    public SearchingMode (Mediator mediator) {
        this.mediator = Objects.requireNonNull(mediator);
    }
    
    public abstract void search();
    
    public static final class Person extends SearchingMode {

        public Person(Mediator mediator) {
            super(mediator);
        }

        @Override
        public void search() {
            mediator.searchPerson();
        }
        
    }
    
    public static final class Sex extends SearchingMode {

        public Sex(Mediator mediator) {
            super(mediator);
        }

        @Override
        public void search() {
            mediator.searchSex();
        }
        
    }
    
    public static final class Act extends SearchingMode {

        public Act(Mediator mediator) {
            super(mediator);
        }

        @Override
        public void search() {
            mediator.searchAct();
        }
        
    }
}
