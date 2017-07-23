/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114.models;

import pl.koder95.ip.u161114.Index;
import pl.koder95.ip.u161114.Sex;

/**
 *
 * @author Kamil
 */
public class SexIndexModel extends PersonIndexModel {
    
    public SexIndexModel(Index index) {
        super(index);
    }

    public SexIndexModel() {
    }

    public String getLastName(Sex s) {
        if (getIndex() == null) return "";
        return super.getLastName(s.ordinal());
    }

    public String getName(Sex s) {
        if (getIndex() == null) return "";
        return super.getName(s.ordinal());
    }
    
}
