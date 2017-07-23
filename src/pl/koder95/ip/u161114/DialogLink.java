/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import pl.koder95.ip.Main;
import pl.koder95.ip.u161114.views.IndexView;
import pl.koder95.ip.u161114.views.OnePersonIndexView;
import pl.koder95.ip.u161114.views.SexIndexView;

/**
 *
 * @author Kamil
 */
public enum DialogLink {
    BAPTISATORUM("Indeks ochrzczonych.csv", "<html><center>l i b e r<br/>baptizatorum</center></html>", new IndexViewCreator<OnePersonIndexView>() {
        @Override
        public OnePersonIndexView create(Index index) { return new OnePersonIndexView(index); }

        @Override
        public OnePersonIndexView create() { return new OnePersonIndexView(); }
    }),
    CONFIRMATORUM("Indeks bierzmowanych.csv", "<html><center>l i b e r<br/>confirmatorum</center></html>", new IndexViewCreator<OnePersonIndexView>() {
        @Override
        public OnePersonIndexView create(Index index) { return new OnePersonIndexView(index); }

        @Override
        public OnePersonIndexView create() { return new OnePersonIndexView(); }
    }),
    MATRIMONIORUM("Indeks zaślubionych.csv", "<html><center>l i b e r<br/>matrimoniorum</center></html>", new IndexViewCreator<SexIndexView>() {
        @Override
        public SexIndexView create(Index index) { return new SexIndexView(index); }

        @Override
        public SexIndexView create() { return new SexIndexView(); }
    }),
    DEFUNCTORUM("Indeks zmarłych.csv", "<html><center>l i b e r<br/>defunctorum</center></html>", new IndexViewCreator<OnePersonIndexView>() {
        @Override
        public OnePersonIndexView create(Index index) { return new OnePersonIndexView(index); }

        @Override
        public OnePersonIndexView create() { return new OnePersonIndexView(); }
    });
    
    private final String filename;
    private final String buttonLabel;
    private final IndexViewCreator creator;
    
    private DialogLink(String filename, String buttonLabel, IndexViewCreator creator) {
        this.filename = filename;
        this.buttonLabel = buttonLabel;
        this.creator = creator;
    }

    public String getFileName() {
        return filename;
    }
    
    public String getDialogTitle() {
        return filename.substring(0, filename.length()-4);
    }
    
    public String getButtonLabel() {
        return buttonLabel;
    }
    
    public Index[] loadIndices(Charset c) throws IOException {
        return Index.load(new File(Main.DATA_DIR, filename), c);
    }
    
    public Index[] loadIndices() throws IOException {
        return Index.load(Main.DATA_DIR);
    }
    
    public IndexView createIndexView(Index index) {
        return creator.create(index);
    }
    
    public IndexView createIndexView() {
        return creator.create();
    }
    
    private static interface IndexViewCreator<Type extends IndexView> {
        public Type create(Index index);
        public Type create();
    }
}
