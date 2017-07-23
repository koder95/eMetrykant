/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.marriage;

import pl.koder95.ip.LineParser;
import pl.koder95.ip.Person;

/**
 *
 * @author Kamil
 */
public final class MarriageIndex {
    
    public final Person male, female;
    private final String act;
    public final int year;

    public MarriageIndex(Person male, Person female, String act, int year) {
        this.male = male;
        this.female = female;
        this.act = act;
        this.year = year;
    }

    public String getAct() {
        return act;
    }

    public String getYearString() {
        return "" + year; //NOI18N
    }

    public String toLabelString(boolean male) {
        return male? this.male + " - " + this.female : this.female + " - " + this.male; //NOI18N
    }

    @Override
    public String toString() {
        return String.format(toLabelString(true) + " - %s %d", this.act, this.year); //NOI18N
    }
    
    public static final LineParser<MarriageIndex> PARSER = (line) -> {
        if (line == null || line.isEmpty()) return null;
        
        String[] cell = line.split(";"); //NOI18N
        if (cell.length != 6) return null;
        Person male = new Person(cell[0], cell[1]);
        Person female = new Person(cell[2], cell[3]);
        return new MarriageIndex(male, female, cell[4], Integer.parseInt(cell[5]));
    };
}
