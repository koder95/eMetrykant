/*
 * Copyright (C) 2018 Kamil Jan Mularski [@koder95]
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
package pl.koder95.eme.fx;

import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import pl.koder95.eme.ac.ACCallback;
import pl.koder95.eme.ac.StringConverter;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.dfs.IndexList;

/**
 * FXML Controller class
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.2.0, 2018-10-07
 * @since 0.1.12-alt
 */
public class LiberConfirmatorumController implements Initializable {

    @FXML
    private TitledPane liberConfirmatorum;
    @FXML
    private TextField searcher;
    @FXML
    private GridPane info;
    @FXML
    private Label name;
    @FXML
    private Label surname;
    @FXML
    private Label act;
    
    private static final IndexList LIST = IndexList.LIBER_CONFIRMATORUM;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (LIST.getLoaded() == null || LIST.getLoaded().isEmpty()) {
            LIST.load();
        }
        searcher.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        searcher.setOnAction((event) -> {
            IndexSearcher isearcher = new IndexSearcher(LIST);
            Index[] result = isearcher.find(searcher.getText());
            if (result != null) setIndex(isearcher.selectOne(result));
            else Toolkit.getDefaultToolkit().beep();
            pl.koder95.eme.Main.releaseMemory();
        });
        AutoCompletionBinding binding = TextFields.bindAutoCompletion(
                searcher,
                new ACCallback(LIST.getLoaded()),
                new StringConverter(LIST.getSCM())
        );
        binding.maxWidthProperty().bind(searcher.widthProperty());
        binding.minWidthProperty().bind(searcher.widthProperty());
        binding.prefWidthProperty().bind(searcher.widthProperty());
        binding.setVisibleRowCount(8);
    }    

    public String getName() {
        return name.getText();
    }

    public String getSurname() {
        return surname.getText();
    }

    public void setName(String name) {
        this.name.setText(prepare(name));
    }

    public void setSurname(String surname) {
        this.surname.setText(prepare(surname));
    }

    public String getAct() {
        return act.getText();
    }

    public void setAct(String act) {
        this.act.setText(prepare(act));
    }
    
    private void setIndex(Index i) {
        setName(i!= null? i.getData("name"): "-");
        setSurname(i!= null? i.getData("surname"): "-");
        setAct(i!= null? i.getData("an"): "-");
        searcher.setText("");
    }
    
    private String prepare(String value) {
        return value.replaceAll("_", " ");
    }
}
