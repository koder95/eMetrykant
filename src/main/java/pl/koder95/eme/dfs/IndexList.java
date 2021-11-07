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
package pl.koder95.eme.dfs;

import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pl.koder95.eme.Files;
import pl.koder95.eme.Main;
import pl.koder95.eme.MemoryUtils;

/**
 * Zawiera wszystkie typy ksiąg i zawierających się w nich zbiorach indeksów.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.4.1, 2021-11-07
 * @since 0.1.11
 */
public enum IndexList implements IndexContainer {

    /**
     * Zbiór indeksów osób ochrzczonych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok chrztu.
     */
    LIBER_BAPTISMORUM("Księga ochrzczonych", new LinkedList<>(Arrays.asList(
            "name", "surname", "an"
    ))),

    /**
     * Zbiór indeksów osób bierzmowanych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok bierzmowania.
     */
    LIBER_CONFIRMATORUM("Księga bierzmowanych", new LinkedList<>(Arrays.asList(
            "name", "surname", "an"
    ))),

    /**
     * Zbiór indeksów osób zaślubionych. Indeksy zawierają dane: nazwisko męża,
     * imiona męża, nazwisko panieńskie żony, imiona żony, nr aktu, rok ślubu.
     */
    LIBER_MATRIMONIORUM("Księga zaślubionych", new LinkedList<>(Arrays.asList(
            "husband-surname", "husband-name", "wife-surname", "wife-name", "an"
    ))),

    /**
     * Zbiór indeksów osób zmarłych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok śmierci.
     */
    LIBER_DEFUNCTORUM("Księga zmarłych", new LinkedList<>(Arrays.asList(
            "name", "surname", "an"
    )));

    private List<Index> loaded = new LinkedList<>();
    private final String name;
    private final Queue<String> nameQueue;

    IndexList(String name, Queue<String> nameQueue) {
        this.name = name;
        this.nameQueue = nameQueue;
    }

    @Override
    public Index get(int id) {
        return loaded.get(id-1);
    }
    
    @Override
    public List<Index> getLoaded() {
        return loaded;
    }
    
    /**
     * Wczytuje z dysku indeksy zapisane w pliku XML.
     */
    public void load() {
        MemoryUtils.memory();
        this.loaded = new LinkedList<>();
        if (BOOKS == null) loadBooks();
        BOOKS.stream().filter((b)->b.getName().equalsIgnoreCase(name))
        .forEach((b)->{
            MemoryUtils.memory();
            this.loaded.addAll(b.indices);
            MemoryUtils.memory();
            b.indices.forEach((i) -> i.getDataNames().stream()
                    .filter(nameQueue::contains)
                    .forEachOrdered(nameQueue::add));
            MemoryUtils.memory();
        });
        List<Index> loaded = this.loaded;
        this.loaded = new ArrayList<>(loaded);
        loaded.clear();
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
    
    /**
     * @param i indeks
     * @return kolejny indeks
     */
    public Index getNext(Index i) {
        int index = loaded.indexOf(i);
        return index < 0? null : loaded.get(index-1);
    }
    
    /**
     * @param i indeks
     * @return poprzedni indeks
     */
    public Index getPrev(Index i) {
        int index = loaded.indexOf(i);
        return index < loaded.size()? loaded.get(index+1) : null;
    }
    
    @Override
    public Index getLast() {
        return get(1);
    }

    @Override
    public void clear() {
        if (loaded == null) return;
        loaded.clear();
        Main.releaseMemory();
    }
    
    private static List<Book> BOOKS;
    
    private static void loadBooks() {
        try {
            BOOKS = Book.load(Files.INDICES_XML.toFile());
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            System.err.println(ex);
        }
    }
}
