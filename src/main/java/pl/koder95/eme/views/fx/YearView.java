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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.dfs.IndexTemplate;
import pl.koder95.eme.views.IYearView;

/**
 * Implementacja interfejsu {@link IYearView}, która rozszerza klasę
 * {@link HBox} i umożliwia za pomocą JavaFX wyświetlić widok roku.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.3.0
 */
public class YearView extends HBox implements IYearView {
    
    private final ToggleGroup group = new ToggleGroup();
    private final VBox content = new VBox();
    private final Label rightTitle = new Label("Nr aktu:");
    private final ScrollPane scroll = new ScrollPane(content);
    private final VBox rightPane = new VBox(rightTitle, scroll);
    private final IndexView indexView;

    public YearView(IndexView indexView) {
        this.indexView = indexView;
        this.scroll.setMinViewportWidth(50);
        this.scroll.setMinViewportHeight(0);
        this.scroll.setFitToWidth(true);
        this.scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scroll.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(this.scroll, Priority.ALWAYS);
        HBox.setHgrow(this.indexView, Priority.ALWAYS);
        VBox.setVgrow(this.indexView, Priority.ALWAYS);
        super.getChildren().addAll(this.indexView, this.rightPane);
        super.getStyleClass().add("year-view");
    }

    public YearView(IndexTemplate template) {
        this(new IndexView(template));
    }

    public YearView() {
        this(new IndexView());
    }
    
    public void setTemplate(IndexTemplate template) {
        if (indexView == null) return;
        indexView.setTemplate(template);
    }

    @Override
    public void displayIndices(List<Index> year) {
        content.getChildren().clear();
        indexView.reset();
        
        if (year == null || year.isEmpty()) return;
        Map<String, Index> indexMap = new HashMap<>();
        year.stream().forEach(i -> addToggleButton(i.getActNumber().getSign(), i));
        indexMap.clear();
    }
    
    private void addToggleButton(String text, Index index) {
        ToggleButton button = new ToggleButton(text);
        button.setToggleGroup(group);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> indexView.displayIndex(index));
        button.getStyleClass().add("sign-toggle-button");
        content.getChildren().add(button);
    }

    void selectSign(String sign) {
        Node y = content.getChildren().stream()
                .reduce(null, (r, c) -> (c instanceof ToggleButton)?
                        ((ToggleButton) c).getText().equals(sign)? c : r
                        : r);
        if (y != null) {
            ToggleButton b = (ToggleButton) y;
            b.requestFocus();
            b.fire();
        }
    }
}
