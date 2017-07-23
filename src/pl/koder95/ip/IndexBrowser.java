/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import pl.koder95.ip.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import pl.koder95.ip.idf.Index;
import static pl.koder95.ip.Main.*;

/**
 *
 * @author Kamil
 */
public class IndexBrowser {
    private static final String[] FILENAMES = {
        "Indeks ochrzczonych.csv", //NOI18N
        "Indeks bierzmowanych.csv", //NOI18N
        "Indeks zmarłych.csv" //NOI18N
    };
    private final ArrayList<Index> loaded = new ArrayList<>();
    private final String fileName;
    private final String title;
    private final ActManager actManager = new ActManager();

    public IndexBrowser() {
        this(0);
    }

    public IndexBrowser(int option) {
        File csv = new File(DATA_DIR, FILENAMES[option]);
        fileName = csv.getName();
        title = fileName.substring(0, fileName.length()-4);
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csv), CSV_DEFAULT_CHARSET)
        )) {
            int id = 0;
            while (reader.ready()) {
                Index i = Index.create(++id, reader.readLine());
                if (i != null) {
                    actManager.create(i);
                    loaded.add(i);
                }
            }
        } catch (FileNotFoundException ex) {
            showErrorMessage(null, READ_CSV_ERR_MESSAGE, READ_CSV_ERR_TITLE, true);
        } catch (IOException ex) {
            showErrorMessage(null, BUNDLE.getString("ERR_EX_IO"), BUNDLE.getString("ERR_EX_IO_TITLE"), true);
        }
    }
    
    public Index find(String lastName, String name, String act, int year)
            throws ObjectNotFoundException {
        for (Index i: loaded) {
            if (i.getData(0).equals(lastName) && i.getData(1).equals(name) &&
                    i.AN.getSign().equals(act) && i.AN.getYear() == year)
                return i;
        }
        throw new ObjectNotFoundException(Index.class, lastName, name, act, year);
    }
    
    public Index find(String lastName, String name, String act)
            throws ObjectNotFoundException {
        for (Index i: loaded) {
            if (i.getData(0).equals(lastName) && i.getData(1).equals(name) &&
                    i.AN.getSign().equals(act)) return i;
        }
        throw new ObjectNotFoundException(Index.class, lastName, name, act);
    }
    
    public Index find(String lastName, String name)
            throws ObjectNotFoundException {
        for (Index i: loaded) {
            if (i.getData(0).equals(lastName) && i.getData(1).equals(name)) return i;
        }
        throw new ObjectNotFoundException(Index.class, lastName, name);
    }
    
    public Index find(String names) throws ObjectNotFoundException {
        String lastName = names.substring(0, names.indexOf(" ")); //NOI18N
        String name = names.substring(names.indexOf(" ")+1, names.length()); //NOI18N
        return find(lastName, name);
    }
    
    public Index[] find(int year) {
        ArrayList<Index> indices = new ArrayList<>();
        loaded.stream().filter((i) -> (i.AN.getYear() == year)).forEach((i) -> {
            indices.add(i);
        });
        return indices.toArray(new Index[indices.size()]);
    }
    
    public Index find(int year, String act) throws ObjectNotFoundException {
        for (Index i: find(year)) {
            if (i.AN.getSign().equals(act)) return i;
        }
        throw new ObjectNotFoundException(Index.class, year, act);
    }
    
    public Index find(Person p, String act, int year) throws ObjectNotFoundException {
        for (Index i: loaded) {
            if (i.getData(0).equals(p.getLastName()) && i.getData(1).equals(p.getName())
                    && i.AN.getSign().equals(act) && i.AN.getYear() == year)
                return i;
        }
        throw new ObjectNotFoundException(Index.class, p, act, year);
    }
    
    public Index[] find(Person p) throws ObjectNotFoundException {
        ArrayList<Index> foundL = new ArrayList<>();
        loaded.stream().filter((i) -> (i.getData(0).equals(p.getLastName()) && i.getData(1).equals(p.getName()))).forEach((i) -> {
            foundL.add(i);
        });
        if (foundL.isEmpty()) throw new ObjectNotFoundException(Index.class, p);
        Index[] found = foundL.toArray(new Index[foundL.size()]);
        foundL.clear();
        return found;
    }

    public Index[] getLoaded() {
        return loaded.toArray(new Index[loaded.size()]);
    }
    
    public Index get(int index) {
        return loaded.get(index);
    }
    
    public int indexOf(Index index) {
        return loaded.indexOf(index);
    }
    
    public Index getFirst() {
        return get(loaded.size()-1);
    }
    
    public Index getLast() {
        return get(0);
    }
    
    public void sortByData() {
        loaded.sort((Index o1, Index o2) -> o1.compareTo(o2));
    }
    
    public void sortByAN() {
        loaded.sort((Index o1, Index o2) -> o1.AN.compareTo(o2.AN));
    }

    public String getFileName() {
        return fileName;
    }
    
    public boolean isYear(int year) {
        return loaded.stream().anyMatch((i) -> (i.AN.getYear() == year));
    }

    public String getTitle() {
        return title;
    }

    public ActManager getActManager() {
        return actManager;
    }
}
