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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.koder95.eme.dfs.ActNumber;
import pl.koder95.eme.dfs.BookTemplate;
import pl.koder95.eme.dfs.Index;
import pl.koder95.eme.dfs.IndexList;
import pl.koder95.eme.views.IBookView;

/**
 * Implementacja interfejsu {@link IBookView}, która rozszerza klasę
 * {@link HBox} i umożliwia za pomocą JavaFX wyświetlić widok księgi.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.3.0
 */
public class BookView extends HBox implements IBookView {
    
    private final ToggleGroup group = new ToggleGroup();
    private final VBox content = new VBox();
    private final Label leftTitle = new Label("Rok:");
    private final ScrollPane scroll = new ScrollPane(content);
    private final VBox leftPane = new VBox(leftTitle, scroll);
    private final YearView yearView;
    private BookTemplate template;

    public BookView(BookTemplate template, YearView yearView) {
        this.template = template;
        this.yearView = yearView;
        super.getChildren().addAll(this.leftPane, this.yearView);
        this.scroll.setMinViewportWidth(50);
        this.scroll.setMinViewportHeight(0);
        this.scroll.setFitToWidth(true);
        this.scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scroll.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(this.scroll, Priority.ALWAYS);
        HBox.setHgrow(this.yearView, Priority.ALWAYS);
        VBox.setVgrow(this.yearView, Priority.ALWAYS);
        super.getStyleClass().add("book-view");
    }

    public BookView(YearView yearView) {
        this(null, yearView);
    }

    public BookView() {
        this(new YearView());
    }

    public void setTemplate(BookTemplate template) {
        this.template = template;
    }

    @Override
    public void displayIndexList(IndexList list) {
        content.getChildren().clear();
        yearView.reset();
        
        if (list == null || template == null) return;
        List<Index> loaded = list.getLoaded();
        if (loaded == null) {
            list.load();
            loaded = list.getLoaded();
        }
        if (loaded.isEmpty()) return;
        Map<Integer, List<Index>> indexMap = new HashMap<>();
        loaded.stream().filter(i -> i.getActNumber() != null)
                .sorted((y1, y2) -> y1.getActNumber()
                .compareTo(y2.getActNumber()))
                .forEach(i -> {
            ActNumber an = i.getActNumber();
            if (an == null) return;
            List<Index> indices = indexMap.getOrDefault(an.getYear(),
                    new LinkedList<>());
            indices.add(i);
            indexMap.put(an.getYear(), indices);
        });
        yearView.setTemplate(template.createIndexTemplate());
        indexMap.forEach((year, indices) -> addToggleButton("" + year, indices));
        indexMap.clear();
    }
    
    private void addToggleButton(String text, List<Index> indices) {
        ToggleButton button = new ToggleButton(text);
        button.setToggleGroup(group);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> yearView.displayIndices(indices));
        button.getStyleClass().add("year-toggle-button");
        content.getChildren().add(button);
    }

    void selectYear(String year) {
        Node y = content.getChildren().stream()
                .reduce(null, (r, c) -> (c instanceof ToggleButton)?
                        ((ToggleButton) c).getText().equals(year)? c : r
                        : r);
        if (y != null) {
            ToggleButton b = (ToggleButton) y;
            b.requestFocus();
            b.fire();
            Bounds layoutBounds = scroll.getContent().getLayoutBounds();
            System.out.println("layoutBoundsMinY=" + layoutBounds.getMinY());
            System.out.println("layoutBoundsMaxY=" + layoutBounds.getMaxY());
            Bounds layoutBoundsOfButton = b.getBoundsInParent();
            System.out.println("layoutBoundsOfButtonMinY=" + layoutBoundsOfButton.getMinY());
            System.out.println("layoutBoundsOfButtonMaxY=" + layoutBoundsOfButton.getMaxY());
            double centerPoint = layoutBoundsOfButton.getMaxY()
                    -layoutBoundsOfButton.getHeight()/2;
            System.out.println("centerPoint=" + centerPoint);
            centerPoint = centerPoint/layoutBounds.getMaxY();
            System.out.println("normal centerPoint=" + centerPoint);
            scroll.setVvalue(centerPoint);
        }
    }

    void selectSign(String sign) {
        this.yearView.selectSign(sign);
    }
}
