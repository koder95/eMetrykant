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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import pl.koder95.ip.ActManager;
import static pl.koder95.ip.Main.BUNDLE;
import static pl.koder95.ip.Main.CSV_DEFAULT_CHARSET;
import static pl.koder95.ip.Main.DATA_DIR;
import static pl.koder95.ip.Main.READ_CSV_ERR_MESSAGE;
import static pl.koder95.ip.Main.READ_CSV_ERR_TITLE;
import static pl.koder95.ip.Main.showErrorMessage;
import pl.koder95.ip.gui.IndexInfoPanel;
import pl.koder95.ip.gui.MarriageIndexInfoPanel;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.138
 */
public enum Indices {

    LIBER_BAPTIZATORUM("Indeks ochrzczonych.csv", new IndexInfoPanel()),
    LIBER_CONFIRMATORUM("Indeks bierzmowanych.csv", new IndexInfoPanel()),
    LIBER_MATRIMONIORUM("Indeks zaślubionych.csv",new MarriageIndexInfoPanel()),
    LIBER_DEFUNCTORUM("Indeks zmarłych.csv", new IndexInfoPanel());
    
    private List<RealIndex> loaded;
    private final ActManager acts = new ActManager();
    private final String fileName, name;
    private final IndexInfoPanel infoPanel;

    private Indices(String fileName, IndexInfoPanel info) {
        this.fileName = fileName;
        name = fileName.substring(0, fileName.length()-4);
        infoPanel = info;
    }
    
    public Index get(int id) {
        return getReal(id).toVirtualIndex(this);
    }
    
    RealIndex getReal(int id) {
        return loaded.get(id-1);
    }

    public List<Index> getLoaded() {
        List<Index> virtual = new LinkedList<>();
        loaded.stream().forEach((ri)
                -> virtual.add(new VirtualIndex(ri.ID, this)));
        return virtual;
    }
    
    private int load(String line) {
        int id = loaded.size();
        if (id == 0) id = 1;
        RealIndex r = RealIndex.create(id, line);
        if (r == null) return -1;
        if (loaded.add(r)) {
            acts.create(r);
            return id;
        }
        return -1;
    }
    
    public void load() {
        loaded = new LinkedList<>();
        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(new FileInputStream(
                        new File(DATA_DIR, fileName)), CSV_DEFAULT_CHARSET))) {
            while (reader.ready()) load(reader.readLine());
        } catch (FileNotFoundException ex) {
            showErrorMessage(READ_CSV_ERR_MESSAGE, READ_CSV_ERR_TITLE, true);
        } catch (IOException ex) {
            showErrorMessage(BUNDLE.getString("ERR_EX_IO"),
                    BUNDLE.getString("ERR_EX_IO_TITLE"), true);
        }
        loaded = new ArrayList<>(loaded);
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public ActManager getActManager() {
        return acts;
    }
    
    public int size() {
        return loaded.size();
    }
    
    public Index getFirst() {
        return get(size());
    }
    
    public Index getLast() {
        return get(1);
    }

    public IndexInfoPanel getInfoPanel() {
        return infoPanel;
    }
}
