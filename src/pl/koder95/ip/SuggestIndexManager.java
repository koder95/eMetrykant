/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import static pl.koder95.ip.Main.BUNDLE;
import static pl.koder95.ip.Main.CSV_DEFAULT_CHARSET;
import static pl.koder95.ip.Main.DATA_DIR;
import static pl.koder95.ip.Main.READ_CSV_ERR_MESSAGE;
import static pl.koder95.ip.Main.READ_CSV_ERR_TITLE;
import static pl.koder95.ip.Main.showErrorMessage;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil
 */
public class SuggestIndexManager {
    private static final String[] FILENAMES = {
        "Indeks ochrzczonych.csv", //NOI18N
        "Indeks bierzmowanych.csv", //NOI18N
        "Indeks zmarłych.csv" //NOI18N
    };
    
    private final ArrayList<Index> loaded = new ArrayList<>();
    private final ArrayList<String> words = new ArrayList<>();
    private int selectedIndex = -1;
    
    public void add(Index[] indices) {
        loaded.addAll(Arrays.asList(indices));
    }
    
    public void newWord() { words.add(""); selectedIndex++; }
    
    public String getSelectedWord() {
        return words.get(selectedIndex);
    }
    
    public void selectWord(int index) {
        selectedIndex = index;
    }
    
    public void add(char c) {
        words.set(selectedIndex, getSelectedWord() + c);
        for (int i = 0; i < loaded.size();i++) {
            Index ind = loaded.get(i);
            if (!ind.getData(selectedIndex).startsWith(words.get(selectedIndex))) {
                loaded.remove(i--);
            }
        }
    }
    
    public void add(int index, char c) {
        String value = getSelectedWord().substring(0, index)
                + c
                + getSelectedWord().substring(index);
        words.set(selectedIndex, value);
    }
    
    public void set(int index, char c) {
        String value = getSelectedWord().substring(0, index)
                + c
                + getSelectedWord().substring(index+1);
        words.set(selectedIndex, value);
    }
    
    public void setIfDifferent(int index, char c) {
        if (getSelectedWord().charAt(index) != c) set(index, c);
    }
    
    public void remove(int index) {
        String value = getSelectedWord().substring(0, index)
                + getSelectedWord().substring(index+1);
        words.set(selectedIndex, value);
    }
    
    public void clear() {
        words.clear();
        selectedIndex = -1;
    }
    
    public void close() {
        clear();
        loaded.clear();
    }
    
    public static Index[] load(int option) {
        final ArrayList<Index> loaded = new ArrayList<>();
        File csv = new File(DATA_DIR, FILENAMES[option]);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csv), CSV_DEFAULT_CHARSET)
        )) {
            int id = 0;
            while (reader.ready()) {
                Index i = Index.create(++id, reader.readLine());
                if (i != null) {
                    loaded.add(i);
                }
            }
        } catch (FileNotFoundException ex) {
            showErrorMessage(null, READ_CSV_ERR_MESSAGE, READ_CSV_ERR_TITLE, true);
        } catch (IOException ex) {
            showErrorMessage(null, BUNDLE.getString("ERR_EX_IO"), BUNDLE.getString("ERR_EX_IO_TITLE"), true);
        }
        return loaded.toArray(new Index[loaded.size()]);
    }
    
    public String[] getSuggestions() {
        final ArrayList<String> sugg = new ArrayList<>();
        for (Index i: loaded) {
            sugg.add(i.toString());
        }
        return sugg.toArray(new String[sugg.size()]);
    }
    
    public void serviceEvent(CaretEvent e) {
        JTextField src = (JTextField) e.getSource();
        String txt = src.getText();
        System.out.println("txt=" + txt);
        int i = 0;
        for (; i < txt.length(); i++) {
            if (txt.charAt(i) == ' ') newWord();
            else setIfDifferent(i, txt.charAt(i));
        }
        remove(i);
    }
    
    public static void main(String[] args) {
        SuggestIndexManager sim = new SuggestIndexManager();
        sim.add(load(0));
        sim.newWord();
        sim.add('D');
        sim.add('o');
        sim.add('b');
        sim.add('o');
        sim.add('s');
        sim.add('z');
        sim.newWord();
        sim.add('M');
        sim.add('a');
        sim.add('r');
        sim.add('c');
        System.out.println(sim.getSelectedWord());
        printlnArray(sim.getSuggestions());
    }
    
    public static void printlnArray(Object[] obj) {
        Arrays.asList(obj).forEach((i)->{
            System.out.println(i);
        });
    }
    
    public static interface Condition {
        
        public boolean canAdd(String data, int index, char c);
    }
}
