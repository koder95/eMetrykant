/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.marriage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import static pl.koder95.ip.Main.*;
import pl.koder95.ip.ObjectNotFoundException;
import pl.koder95.ip.Person;

/**
 *
 * @author Kamil
 */
public class IndexBrowser {
    
    private static final String FILENAME = "Indeks zaślubionych.csv"; //NOI18N
    private final ArrayList<MarriageIndex> loaded = new ArrayList<>();
    private final String fileName;

    public IndexBrowser() {
        File csv = new File(DATA_DIR, FILENAME);
        fileName = csv.getName();
        if (!csv.canRead()) showErrorMessage(null, READ_CSV_ERR_MESSAGE, READ_CSV_ERR_TITLE, true);
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csv), CSV_DEFAULT_CHARSET)
        )) {
            while (reader.ready()) {
                MarriageIndex i = MarriageIndex.PARSER.parseOf(reader.readLine());
                if (i != null) loaded.add(i);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    public MarriageIndex find(int act) throws ObjectNotFoundException {
        for (MarriageIndex i: loaded) {
            if (i.getAct().equals(act)) return i;
        }
        throw new ObjectNotFoundException(MarriageIndex.class, act);
    }
    
    public Person findPerson(String lastName, String name)
            throws ObjectNotFoundException {
        for (MarriageIndex i: loaded) {
            if (i.female.getLastName().equals(lastName) &&
                    i.female.getName().equals(name)) {
                return i.female;
            }
            if (i.male.getLastName().equals(lastName) &&
                    i.male.getName().equals(name)) {
                return i.male;
            }
        }
        throw new ObjectNotFoundException(Person.class, lastName, name);
    }
    
    public Person findPerson(String names) throws ObjectNotFoundException {
        String lastName = names.substring(0, names.indexOf(" ")); //NOI18N
        String name = names.substring(names.indexOf(" ")+1, names.length()); //NOI18N
        return findPerson(lastName, name);
    }
    
    public MarriageIndex find(Person male, Person female, String act, int year)
            throws ObjectNotFoundException {
        for (MarriageIndex i: loaded) {
            if (i.getAct().equals(act) &&
                    i.female.getLastName().equals(female.getLastName()) &&
                    i.female.getName().equals(female.getName()) &&
                    i.male.getLastName().equals(male.getLastName()) &&
                    i.male.getName().equals(male.getName()) &&
                    i.year == year) {
                return i;
            }
        }
        throw new ObjectNotFoundException(MarriageIndex.class, male, female, act, year);
    }
    
    public MarriageIndex find(Person male, Person female, String act)
            throws ObjectNotFoundException {
        for (MarriageIndex i: loaded) {
            if (i.getAct().equals(act) &&
                    i.female.getLastName().equals(female.getLastName()) &&
                    i.female.getName().equals(female.getName()) &&
                    i.male.getLastName().equals(male.getLastName()) &&
                    i.male.getName().equals(male.getName())) {
                return i;
            }
        }
        throw new ObjectNotFoundException(MarriageIndex.class, male, female, act);
    }
    
    public MarriageIndex find(Person male, Person female) throws ObjectNotFoundException {
        for (MarriageIndex i: loaded) {
            if (i.female.getLastName().equals(female.getLastName()) &&
                    i.female.getName().equals(female.getName()) &&
                    i.male.getLastName().equals(male.getLastName()) &&
                    i.male.getName().equals(male.getName())) {
                return i;
            }
        }
        throw new ObjectNotFoundException(MarriageIndex.class, male, female);
    }
    
    public MarriageIndex find(String names, boolean firstMale)
            throws ObjectNotFoundException {
        String[] people = names.split(" - "); //NOI18N
        Person male = firstMale? findPerson(people[0]) : findPerson(people[1]);
        Person female = firstMale? findPerson(people[1]) : findPerson(people[0]);
        for (MarriageIndex i: loaded) {
            if (i.female.getLastName().equals(female.getLastName()) &&
                    i.female.getName().equals(female.getName()) &&
                    i.male.getLastName().equals(male.getLastName()) &&
                    i.male.getName().equals(male.getName())) {
                return i;
            }
        }
        throw new ObjectNotFoundException(MarriageIndex.class, names, male, female);
    }

    public MarriageIndex[] getLoaded() {
        return loaded.toArray(new MarriageIndex[loaded.size()]);
    }
    
    public MarriageIndex get(int index) {
        return loaded.get(index);
    }
    
    public MarriageIndex getFirst() {
        return get(loaded.size()-1);
    }
    
    public MarriageIndex getLast() {
        return get(0);
    }

    public String getFileName() {
        return fileName;
    }

    public void sort(boolean male) {
        loaded.sort((MarriageIndex o1, MarriageIndex o2) -> {
            if (male) {
                return o1.male.compareTo(o2.male);
            }
            else {
                return o1.female.compareTo(o2.female);
            }
        });
    }
    
    public boolean isYear(int year) {
        for (MarriageIndex i: loaded) {
            if (i.year == year) return true;
        }
        return false;
    }
}
