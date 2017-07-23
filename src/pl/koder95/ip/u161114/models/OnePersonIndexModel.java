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
public class OnePersonIndexModel extends PersonIndexModel {
    
    public OnePersonIndexModel(Index index) {
        super(index);
    }

    public OnePersonIndexModel() {
    }

    @Override
    public int getNumberOfPeaple() {
        return 1;
    }

    public String getLastName() {
        if (getIndex() == null) return "";
        return super.getLastName(1);
    }

    public String getName() {
        if (getIndex() == null) return "";
        return super.getName(1);
    }

    @Deprecated
    @Override
    public String getLastName(int personID) {
        return super.getLastName(1);
    }

    @Deprecated
    @Override
    public String getName(int personID) {
        return super.getName(1);
    }
}
