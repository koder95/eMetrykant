/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

package pl.koder95.ip.idf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import pl.koder95.ip.ActManager;
import static pl.koder95.ip.Main.BUNDLE;
import static pl.koder95.ip.Main.CSV_DEFAULT_CHARSET;
import static pl.koder95.ip.Main.DATA_DIR;
import static pl.koder95.ip.Main.READ_CSV_ERR_MESSAGE;
import static pl.koder95.ip.Main.READ_CSV_ERR_TITLE;
import static pl.koder95.ip.Main.showErrorMessage;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version %I%, %G%
 */
public enum Indices {

    LIBER_BAPTIZATORUM("Indeks ochrzczonych.csv"),
    LIBER_CONFIRMATORUM("Indeks bierzmowanych.csv"),
    LIBER_MATRIMONIORUM("Indeks zaślubionych.csv"),
    LIBER_DEFUNCTORUM("Indeks zmarłych.csv");
    
    private List<Index> loaded;
    private final ActManager acts = new ActManager();
    private final String fileName, title;

    private Indices(String fileName) {
        this.fileName = fileName;
        title = fileName.substring(0, fileName.length()-4);
    }
    
    public Index get(int id) {
        return loaded.get(id-1);
    }

    public List<Index> getLoaded() {
        return loaded;
    }
    
    private int load(String line) {
        int id = loaded.size();
        RealIndex r = RealIndex.create(id, line);
        if (loaded.add(r)) {
            acts.create(r);
            return id;
        }
        return -1;
    }
    
    public void load() {
        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(new FileInputStream(
                        new File(DATA_DIR, fileName)), CSV_DEFAULT_CHARSET))) {
            while (reader.ready()) load(reader.readLine());
        } catch (FileNotFoundException ex) {
            showErrorMessage(null, READ_CSV_ERR_MESSAGE, READ_CSV_ERR_TITLE, true);
        } catch (IOException ex) {
            showErrorMessage(null, BUNDLE.getString("ERR_EX_IO"), BUNDLE.getString("ERR_EX_IO_TITLE"), true);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getTitle() {
        return title;
    }

    public ActManager getActManager() {
        return acts;
    }
    
    public void sortByData() {
        loaded.sort((Index o1, Index o2) -> o1.compareTo(o2));
    }
    
    public void sortByActNumber() {
        loaded.sort((Index o1, Index o2)
                -> o1.getActNumber().compareTo(o2.getActNumber()));
    }
}
