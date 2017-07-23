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
public class IndexModel {
    
    private Index index;

    public IndexModel(Index index) {
        this.index = index;
    }

    public IndexModel() {
        this.index = null;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }
    
    public String getActNumber() {
        if (getIndex() == null) return "";
        return this.getIndex().getValue(getIndex().getSize()-2);
    }
    
    public String getYear() {
        if (getIndex() == null) return "";
        return this.getIndex().getValue(getIndex().getSize()-1);
    }
}
