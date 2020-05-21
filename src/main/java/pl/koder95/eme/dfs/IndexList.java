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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pl.koder95.eme.Files;
import pl.koder95.eme.Main;
import pl.koder95.eme.MemoryUtils;
import pl.koder95.eme.fx.SuggestCreatingMethod;
import pl.koder95.eme.fx.SuggestCreator;
import static pl.koder95.eme.fx.SuggestCreator.prepareData;
import pl.koder95.eme.searching.SearchContext;

/**
 * Zawiera wszystkie typy ksiąg i zawierających się w nich zbiorach indeksów.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.1.11
 */
public enum IndexList implements IndexContainer {

    /**
     * Zbiór indeksów osób ochrzczonych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok chrztu.
     */
    LIBER_BAPTISMORUM("Księga ochrzczonych", new LinkedList<>(Arrays.asList(
            "name", "surname", "an"
    )), SuggestCreator.DEFAULT_METHOD),

    /**
     * Zbiór indeksów osób bierzmowanych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok bierzmowania.
     */
    LIBER_CONFIRMATORUM("Księga bierzmowanych", new LinkedList<>(Arrays.asList(
            "name", "surname", "an"
    )), SuggestCreator.DEFAULT_METHOD),

    /**
     * Zbiór indeksów osób zaślubionych. Indeksy zawierają dane: nazwisko męża,
     * imiona męża, nazwisko panieńskie żony, imiona żony, nr aktu, rok ślubu.
     */
    LIBER_MATRIMONIORUM("Księga zaślubionych", new LinkedList<>(Arrays.asList(
            "husband-surname", "husband-name", "wife-surname", "wife-name", "an"
    )), (i) -> {
        StringBuilder builder = new StringBuilder();
        builder.append(prepareData(i.getData("husband-surname")));
        builder.append(" ").append(prepareData(i.getData("husband-name")));
        builder.append(" ").append(prepareData(i.getData("wife-surname")));
        builder.append(" ").append(prepareData(i.getData("wife-name")));
        builder.append(" ").append(prepareData(i.getData("an")));
        return builder.toString();
    }),

    /**
     * Zbiór indeksów osób zmarłych. Indeksy zawierają dane: nazwisko,
     * imiona, nr aktu, rok śmierci.
     */
    LIBER_DEFUNCTORUM("Księga zmarłych", new LinkedList<>(Arrays.asList(
            "name", "surname", "an"
    )), SuggestCreator.DEFAULT_METHOD);
    
    private List<Index> loaded;
    private final String name;
    private final Queue<String> nameQueue;
    private final SuggestCreatingMethod scm;

    IndexList(String name, Queue<String> nameQueue,
              SuggestCreatingMethod scm) {
        this.name = name;
        this.nameQueue = nameQueue;
        this.scm = scm;
    }

    @Override
    public Index get(int id) {
        System.out.println("get[id]=" + id);
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
        loaded = new LinkedList<>();
        if (BOOKS == null) loadBooks();
        BOOKS.stream().filter((b)->b.getName().equalsIgnoreCase(name))
        .forEach((b)->{
            MemoryUtils.memory();
            loaded.addAll(b.indices);
            MemoryUtils.memory();
            b.indices.forEach((i) -> i.getDataNames().stream()
                    .filter(nameQueue::contains)
                    .forEachOrdered(nameQueue::add));
            MemoryUtils.memory();
        });
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

    public SuggestCreatingMethod getSCM() {
        return scm;
    }
    
    /**
     * Tworzy i zwraca kolejkę nazw, które służą do pobrania odpowiednich danych
     * z indeksów.
     * 
     * @return nazwy danych w postaci kolejki
     */
    public Queue<String> queueNames() {
        return new LinkedList<>(nameQueue);
    }
    
    private static List<Book> BOOKS;
    
    private static void loadBooks() {
        try {
            BOOKS = Book.load(new File(Files.XML_DIR, "indices.xml"));
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            System.err.println(ex);
        }
    }
    
    public Index search(String query) {
        SearchContext context = new SearchContext();
        context.setAutoSearch();
        return context.select(context.search(loaded));
    }
}
