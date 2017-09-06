/*
 * Copyright (C) 2017 Kamil Jan Mularski [@koder95]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.koder95.eme.idf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pl.koder95.eme.Main;
import static pl.koder95.eme.Main.BUNDLE;
import static pl.koder95.eme.Main.DATA_DIR;
import static pl.koder95.eme.Main.READ_CSV_ERR_MESSAGE;
import static pl.koder95.eme.Main.READ_CSV_ERR_TITLE;
import static pl.koder95.eme.Main.showErrorMessage;
import pl.koder95.eme.gui.IndexInfoPanel;
import pl.koder95.eme.gui.MarriageIndexInfoPanel;

/**
 * Zawiera wszystkie typy ksiąg i zawierających się w nich zbiorach indeksów.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.4, 2017-09-06
 * @since 0.0.201
 */
public enum Indices implements IndexContainer {

    /**
     * Zbiór indeksów osób ochrzczonych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok chrztu.
     */
    LIBER_BAPTIZATORUM("Księga ochrzczonych.csv", new IndexInfoPanel()),

    /**
     * Zbiór indeksów osób bierzmowanych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok bierzmowania.
     */
    LIBER_CONFIRMATORUM("Księga bierzmowanych.csv", new IndexInfoPanel()),

    /**
     * Zbiór indeksów osób zaślubionych. Indeksy zawierają dane: nazwisko męża,
     * imiona męża, nazwisko panieńskie żony, imiona żony, nr aktu, rok ślubu.
     */
    LIBER_MATRIMONIORUM("Księga zaślubionych.csv",new MarriageIndexInfoPanel()),

    /**
     * Zbiór indeksów osób zmarłych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok śmierci.
     */
    LIBER_DEFUNCTORUM("Księga zmarłych.csv", new IndexInfoPanel());
    
    private List<RealIndex> loaded;
    private final String fileName, name;
    private final IndexInfoPanel infoPanel;

    private Indices(String fileName, IndexInfoPanel info) {
        this.fileName = fileName;
        name = fileName.substring(0, fileName.length()-4);
        infoPanel = info;
    }
    
    @Override
    public Index get(int id) {
        return getReal(id).toVirtualIndex(this);
    }
    
    RealIndex getReal(int id) {
        return loaded.get(id-1);
    }
    
    @Override
    public List<Index> getLoaded() {
        List<Index> virtual = new LinkedList<>();
        loaded.stream().forEach((ri)
                -> virtual.add(get(ri.ID)));
        return virtual;
    }
    
    /**
     * Wczytuje z dysku indeksy zapisane w pliku XML.
     */
    public void load() {
        BookLoader books = BookLoader.get();
        try {
            books.loadBookTemplate(new File(DATA_DIR, "templates.xml"), name);
            books.createIndicesLoader(name);
            loaded = books.load(new File(DATA_DIR, "indices.xml"), name);
        } catch (FileNotFoundException ex) {
            showErrorMessage(READ_CSV_ERR_MESSAGE, READ_CSV_ERR_TITLE, true);
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            showErrorMessage(BUNDLE.getString("ERR_EX_IO"),
                    BUNDLE.getString("ERR_EX_IO_TITLE"), true);
        }
    }

    /**
     * @return nazwa pliku, który zawiera indeksy
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return nazwa księgi
     */
    public String getName() {
        return name;
    }
    
    @Override
    public int size() {
        return loaded.size();
    }
    
    @Override
    public Index getFirst() {
        return get(size());
    }
    
    @Override
    public Index getLast() {
        return get(1);
    }

    /**
     * @return panel informacyjny dla wyświetlenia danych o indeksie
     */
    public IndexInfoPanel getInfoPanel() {
        return infoPanel;
    }

    @Override
    public void clear() {
        loaded.clear();
        Main.releaseMemory();
    }
}
