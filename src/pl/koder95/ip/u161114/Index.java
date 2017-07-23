/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Kamil
 */
public class Index {
    
    public final int ID;
    private final String[] values;

    private Index(int ID, String[] values) {
        this.ID = ID;
        this.values = values;
    }

    public String getValue(int i) {
        return values[i];
    }
    
    public void trimAllValues() {
        for (int i = 0; i < values.length; i++) {
            this.values[i] = this.values[i].trim();
        }
    }
    
    public int getSize() {
        return values.length;
    }

    @Override
    public String toString() {
        return ID + ": " + Arrays.toString(values);
    }
    
    public static Index[] load(InputStreamReader isr) throws IOException {
        BufferedReader reader = new BufferedReader(isr);
        ArrayList<Index> indices = new ArrayList<>();
        int index = 0;
        while (reader.ready()) {
            String line = reader.readLine();
            Index i;
            if (line == null) continue;
            else {
                i = new Index(++index, line.split(";"));
                i.trimAllValues();
            }
            indices.add(i);
        }
        System.out.println("loaded " + indices.size() + " indices");
        return indices.toArray(new Index[indices.size()]);
    }
    
    public static Index[] load(File f, Charset c) throws IOException {
        return load(new InputStreamReader(new FileInputStream(f), c));
    }
    
    public static Index[] load(File f) throws IOException {
        return load(new InputStreamReader(new FileInputStream(f)));
    }
}
