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
import org.controlsfx.control.textfield.TextFields;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.dfs.IndexList;

/**
 * FXML Controller class
 *
 * @author Kamil Jan Mularski [@koder95]
 */
public class LiberMatrimoniorumController implements Initializable {

    @FXML
    private TextField searcher;
    @FXML
    private GridPane info;
    @FXML
    private Label act;
    @FXML
    private TitledPane liberMatrimoniorum;
    @FXML
    private Label husbandName;
    @FXML
    private Label husbandSurname;
    @FXML
    private Label wifeName;
    @FXML
    private Label wifeSurname;
    
    private Collection<String> possibleSuggestions;
    
    private static final IndexList LIST = IndexList.LIBER_MATRIMONIORUM;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        searcher.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        if (LIST.getLoaded() == null || LIST.getLoaded().isEmpty()) {
            LIST.load();
            Collection<String> pc = new LinkedList<>();
            LIST.getLoaded().forEach((index) -> {
                pc.add(LIST.getSCM().createSuggestion(index));
            });
            this.possibleSuggestions = new ArrayList<>(pc);
            pc.clear();
        }
        searcher.setOnAction((event) -> {
            IndexSearcher isearcher = new IndexSearcher(LIST);
            Index[] result = isearcher.find(searcher.getText());
            if (result != null) setIndex(isearcher.selectOne(result));
            else Toolkit.getDefaultToolkit().beep();
            pl.koder95.eme.Main.releaseMemory();
        });
        TextFields.bindAutoCompletion(searcher, possibleSuggestions);
    }

    public String getHusbandName() {
        return husbandName.getText();
    }

    public String getHusbandSurname() {
        return husbandSurname.getText();
    }

    public void setHusbandName(String name) {
        this.husbandName.setText(name);
    }

    public void setHusbandSurname(String surname) {
        this.husbandSurname.setText(surname);
    }

    public String getWifeName() {
        return wifeName.getText();
    }

    public String getWifeSurname() {
        return wifeSurname.getText();
    }

    public void setWifeName(String name) {
        this.wifeName.setText(name);
    }

    public void setWifeSurname(String surname) {
        this.wifeSurname.setText(surname);
    }

    public String getAct() {
        return act.getText();
    }

    public void setAct(String act) {
        this.act.setText(act);
    }
    
    private void setIndex(Index i) {
        setHusbandName(i!= null? i.getData("husband-name"): "-");
        setHusbandSurname(i!= null? i.getData("husband-surname"): "-");
        setWifeName(i!= null? i.getData("wife-name"): "-");
        setWifeSurname(i!= null? i.getData("wife-surname"): "-");
        setAct(i!= null? i.getData("an"): "-");
        searcher.setText("");
    }
}
