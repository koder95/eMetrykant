/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import pl.koder95.ip.idf.Index;
import static pl.koder95.ip.Main.*;
import pl.koder95.ip.idf.Indices;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
public class IndexBrowser {
    private static final String[] FILENAMES = {
        "Indeks ochrzczonych.csv", //NOI18N
        "Indeks bierzmowanych.csv", //NOI18N
        "Indeks zmarłych.csv" //NOI18N
    };
    private final Indices indices;
    private final String fileName;
    private final String title;
    private final ActManager actManager = new ActManager();

    public IndexBrowser() {
        this(0);
    }

    public IndexBrowser(int option) {
        indices = Indices.values()[option];
        File csv = new File(DATA_DIR, FILENAMES[option]);
        fileName = csv.getName();
        title = fileName.substring(0, fileName.length()-4);
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csv), CSV_DEFAULT_CHARSET)
        )) {
            while (reader.ready()) {
                int id = indices.load(reader.readLine());
                if (id > 0) {
                    actManager.create(indices.get(id));
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
        for (Index i: indices.getLoaded()) {
            if (i.getData(0).equals(lastName) && i.getData(1).equals(name)
                    && i.getActNumber().getSign().equals(act)
                    && i.getActNumber().getYear() == year)
                return i;
        }
        throw new ObjectNotFoundException(Index.class, lastName, name, act, year);
    }
    
    public Index find(String lastName, String name, String act)
            throws ObjectNotFoundException {
        for (Index i: indices.getLoaded()) {
            if (i.getData(0).equals(lastName) && i.getData(1).equals(name) &&
                    i.getActNumber().getSign().equals(act)) return i;
        }
        throw new ObjectNotFoundException(Index.class, lastName, name, act);
    }
    
    public Index find(String lastName, String name)
            throws ObjectNotFoundException {
        for (Index i: indices.getLoaded()) {
            if (i.getData(0).equals(lastName) && i.getData(1).equals(name)) return i;
        }
        throw new ObjectNotFoundException(Index.class, lastName, name);
    }
    
    public Index find(String names) throws ObjectNotFoundException {
        String lastName = names.substring(0, names.indexOf(" ")); //NOI18N
        String name = names.substring(names.indexOf(" ")+1, names.length());
        return find(lastName, name);
    }
    
    public Index[] find(int year) {
        ArrayList<Index> indices = new ArrayList<>();
        this.indices.getLoaded().stream().filter((i)
                -> (i.getActNumber().getYear() == year)).forEach((i) -> {
            indices.add(i);
        });
        return indices.toArray(new Index[indices.size()]);
    }
    
    public Index find(int year, String act) throws ObjectNotFoundException {
        for (Index i: find(year)) {
            if (i.getActNumber().getSign().equals(act)) return i;
        }
        throw new ObjectNotFoundException(Index.class, year, act);
    }
    
    public Index find(Person p, String act, int year)
            throws ObjectNotFoundException {
        for (Index i: indices.getLoaded()) {
            if (i.getData(0).equals(p.getLastName())
                    && i.getData(1).equals(p.getName())
                    && i.getActNumber().getSign().equals(act)
                    && i.getActNumber().getYear() == year)
                return i;
        }
        throw new ObjectNotFoundException(Index.class, p, act, year);
    }
    
    public Index[] find(Person p) throws ObjectNotFoundException {
        ArrayList<Index> foundL = new ArrayList<>();
        indices.getLoaded().stream().filter((i) -> (
                i.getData(0).equals(p.getLastName())
                && i.getData(1).equals(p.getName()))).forEach((i) -> {
            foundL.add(i);
        });
        if (foundL.isEmpty()) throw new ObjectNotFoundException(Index.class, p);
        Index[] found = foundL.toArray(new Index[foundL.size()]);
        foundL.clear();
        return found;
    }

    public Index[] getLoaded() {
      return indices.getLoaded().toArray(new Index[indices.getLoaded().size()]);
    }
    
    public Index get(int index) {
        return indices.getLoaded().get(index);
    }
    
    public int indexOf(Index index) {
        return indices.getLoaded().indexOf(index);
    }
    
    public Index getFirst() {
        return get(indices.getLoaded().size()-1);
    }
    
    public Index getLast() {
        return get(0);
    }
    
    public void sortByData() {
        indices.getLoaded().sort((Index o1, Index o2) -> o1.compareTo(o2));
    }
    
    public void sortByAN() {
        indices.getLoaded().sort((Index o1, Index o2)
                -> o1.getActNumber().compareTo(o2.getActNumber()));
    }

    public String getFileName() {
        return fileName;
    }
    
    public boolean isYear(int year) {
        return indices.getLoaded().stream().anyMatch((i)
                -> (i.getActNumber().getYear() == year));
    }

    public String getTitle() {
        return title;
    }

    public ActManager getActManager() {
        return actManager;
    }
}
