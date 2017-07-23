/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114.models;

import pl.koder95.ip.u161114.Index;

/**
 *
 * @author Kamil
 */
public class PersonIndexModel extends IndexModel {
    
    public PersonIndexModel(Index index) {
        super(index);
    }

    public PersonIndexModel() {
    }
    
    @Override
    public String getActNumber() {
        if (getIndex() == null) return "";
        return this.getIndex().getValue(getIndex().getSize()-2);
    }
    
    @Override
    public String getYear() {
        if (getIndex() == null) return "";
        return this.getIndex().getValue(getIndex().getSize()-1);
    }
    
    public int getNumberOfPeaple() {
        int actNumberIndex = getIndex().getSize()-2;
        return actNumberIndex%2 == 0? actNumberIndex/2: (actNumberIndex-1)/2;
    }
    
    public String getLastName(int personID) {
        if (getIndex() == null) return "";
        personID = checkPersonID(personID);
        if (personID < 1) return null;
        int index = personID-1;
        return getIndex().getValue(index*2);
    }
    
    public String getName(int personID) {
        if (getIndex() == null) return "";
        personID = checkPersonID(personID);
        if (personID < 1) return null;
        int index = personID-1;
        return getIndex().getValue(index*2+1);
    }
    
    private int checkPersonID(int personID) {
        return personID<=getNumberOfPeaple()? personID : -1;
    }
}
