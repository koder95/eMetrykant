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

import static java.lang.Double.MAX_VALUE;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Priority.ALWAYS;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.dfs.IndexTemplate;
import pl.koder95.eme.views.IIndexView;

/**
 * Implementacja interfejsu {@link IIndexView}, która rozszerza klasę
 * {@link VBox} i umożliwia za pomocą JavaFX wyświetlić widok indeksu.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.3.0
 */
public class IndexView extends VBox implements IIndexView {

    private IndexTemplate template;

    public IndexView() {
        this(null);
    }

    public IndexView(IndexTemplate template) {
        this.template = template;
        super.setFillWidth(true);
        super.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        super.setPadding(new Insets(25));
        super.setMinSize(300, 300);
        super.getStyleClass().add("index-view");
    }

    public void setTemplate(IndexTemplate template) {
        this.template = template;
    }
    
    @Override
    public void displayIndex(Index i) {
        super.getChildren().clear();
        if (i == null) {
            super.getChildren().add(new Label("Nothing to show"));
            return;
        }
        if (template == null) return;
        i.getDataNames().stream().sorted().forEach(name -> {
            String label = name.equalsIgnoreCase("an")? "Nr aktu"
                    : template.getLabel(name);
            addData(label, i.getData(name));
        });
        
    }
    
    private void addData(String label, String value) {
        Label l = new Label(label + ":");
        l.getStyleClass().add("data-label");
        Label v = new Label(value);
        v.getStyleClass().add("data-value");
        v.setFont(Font.font(v.getFont().getFamily(), FontWeight.BOLD, 18));
        HBox space = new HBox();
        HBox line = new HBox(l, space, v);
        line.setMaxWidth(MAX_VALUE);
        
        HBox.setHgrow(space, ALWAYS);
        HBox.setHgrow(line, ALWAYS);
        super.getChildren().add(line);
    }
    
}
