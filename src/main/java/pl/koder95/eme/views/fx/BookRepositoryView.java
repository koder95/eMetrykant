/*
 * Copyright (C) 2018 Kamil Mularski
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
package pl.koder95.eme.views.fx;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import static java.lang.Double.MAX_VALUE;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Priority.ALWAYS;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javax.xml.parsers.ParserConfigurationException;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.xml.sax.SAXException;
import static pl.koder95.eme.Main.releaseMemory;
import pl.koder95.eme.ac.*;
import pl.koder95.eme.dfs.ActNumber;
import pl.koder95.eme.dfs.BookTemplate;
import pl.koder95.eme.dfs.BookTemplateLoader;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.dfs.IndexList;
import pl.koder95.eme.searching.Searching;
import pl.koder95.eme.views.IBookRepositoryView;

/**
 * Implementacja interfejsu {@link IBookRepositoryView}, która rozszerza klasę
 * {@link VBox} i umożliwia za pomocą JavaFX wyświetlić
 * widok repozytorium ksiąg.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.3.0
 */
public class BookRepositoryView extends VBox implements IBookRepositoryView {
    
    private final SegmentedButton books = new SegmentedButton();
    private final BookView bookView;
    private final Map<String, BookTemplate> templatesMap;
    private final TextField searchField = TextFields.createClearableTextField();
    private Searching searching;

    public BookRepositoryView(BookView bookView,
            Map<String, BookTemplate> templatesMap) {
        this.bookView = bookView;
        this.templatesMap = templatesMap;
        this.books.setMaxWidth(MAX_VALUE);
        super.setMaxSize(MAX_VALUE, MAX_VALUE);
        HBox box = new HBox(searchField);
        box.setMaxWidth(MAX_VALUE);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(box, ALWAYS);
        super.getChildren().addAll(box, books, this.bookView);
        setVgrow(this.bookView, ALWAYS);
        super.getStylesheets().add(ClassLoader.getSystemResource("pl/koder95/eme/views.fx/view.css")
                .toExternalForm());
        super.getStyleClass().add("book-repository-view");
    }

    @Override
    public void displayBooks() {
        reset();
        for (IndexList list : IndexList.values()) {
            books.getButtons().add(createBookButton(list.getName(), list));
        }
        searchField.setMaxWidth(MAX_VALUE);
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, ALWAYS);
        searchField.setFont(Font.font(searchField.getFont().getFamily(), 16));
        searchField.setPromptText("Wybierz najpierw księgę, aby móc wyszukiwać");
        searchField.setDisable(true);
        searchField.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        searchField.setOnAction((e) -> {
            setIndex(searching.search(searchField.getText()));
            releaseMemory();
        });
    }
    
    private AutoCompletionBinding binding;
    
    private ToggleButton createBookButton(String name, IndexList list) {
        ToggleButton button = new ToggleButton(name);
        button.setMaxWidth(MAX_VALUE);
        HBox.setHgrow(button, ALWAYS);
        button.setOnAction(e -> {
            list.clear();
            list.load();
            BookRepositoryView.this.searching = new Searching(new ACCallback(list.getLoaded()));
            bookView.setTemplate(templatesMap.get(name));
            bookView.displayIndexList(list);
            if (searchField.isDisable()) {
                searchField.setPromptText("Wpisz frazę do wyszukania");
                searchField.setDisable(false);
            }
            if (binding != null) binding.dispose();
            binding = TextFields.bindAutoCompletion(
                    searchField,
                    searching.getSuggestionCallback(),
                    new StringConverter(list.getSCM())
            );
            binding.maxWidthProperty().bind(searchField.widthProperty());
            binding.minWidthProperty().bind(searchField.widthProperty());
            binding.prefWidthProperty().bind(searchField.widthProperty());
            binding.setVisibleRowCount(8);
        });
        button.getStyleClass().add("book-toggle-button");
        return button;
    }

    @Override
    public void reset() {
        searchField.clear();
        books.getButtons().clear();
        bookView.reset();
    }
    
    public static BookRepositoryView create(Map<String, BookTemplate> map) {
        return new BookRepositoryView(new BookView(), map);
    }
    
    public static BookRepositoryView loadAndCreateLater(File xml)
            throws IOException, SAXException, ParserConfigurationException {
        return create(BookTemplateLoader.load(xml));
    }

    private void setIndex(Index selectOne) {
        if (selectOne == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        ActNumber an = selectOne.getActNumber();
        if (an == null) return;
        setIndex("" + an.getYear(), an.getSign());
    }

    private void setIndex(String year, String act) {
        selectYear(year);
        selectSign(act);
    }
    
    private void selectYear(String year) {
        bookView.selectYear(year);
    }

    private void selectSign(String sign) {
        bookView.selectSign(sign);
    }
}
