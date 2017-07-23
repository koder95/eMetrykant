/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161114;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.text.BadLocationException;
import pl.koder95.ip.u161114.views.IndexView;

/**
 *
 * @author Kamil
 */
public class Mediator {
    
    private final DefaultListModel<String> suggestions = new DefaultListModel<>();
    private final JScrollPane suggestionPane = new JScrollPane(new JList<>(suggestions));
    private final JTextField searchingField = new JTextField();
    private final Index[] indices;
    private final JLabel min = new JLabel(), max = new JLabel();
    private int cellHeight = 0;

    public Mediator(Index[] indices) {
        this.indices = indices;
        suggestionPane.setVisible(false);
        Index first = indices[0], last = indices[indices.length-1];
        min.setText(last.getValue(last.getSize()-2) + "/" + last.getValue(last.getSize()-1));
        max.setText(first.getValue(first.getSize()-2) + "/" + first.getValue(first.getSize()-1));
        searchingField.addCaretListener((e)->caretUpdate(e));
    }

    public DefaultListModel<String> getSuggestions() {
        return suggestions;
    }

    public JTextField getSearchingField() {
        return searchingField;
    }

    public JScrollPane getSuggestionPane() {
        return suggestionPane;
    }

    public JLabel getMinLabel() {
        return min;
    }

    public JLabel getMaxLabel() {
        return max;
    }
    
    public void setFoundIndex(IndexView view) {
        
    }
    
    private void caretUpdate(CaretEvent e) {
        if (searchingField.getText().isEmpty()) setSuggestionsVisible(false);
        else {
            setSuggestionsVisible(true);
        }
        int dot = e.getDot();
        suggestionPane.setVisible(false);
        if (dot > 0) try {
            String searchQuery = searchingField.getText(0, dot).toLowerCase();
            List<String> sugg = new ArrayList<>();
            for (Index i: indices) {
                System.out.println(i);
                String[] words = searchQuery.split(" "); //NOI18N
                if (i.getValue(0).toLowerCase().startsWith(words[0])) {
                    System.out.println("YES");
                    if (words.length == 1) sugg.add(i.toString());
                    else {
                        String nameQuery = ""; //NOI18N
                        for (int in = 1; in < words.length; in++) {
                            nameQuery += words[in];
                            if (in < words.length-1) nameQuery += " "; //NOI18N
                        }
                        if (i.getValue(1).toLowerCase().startsWith(nameQuery))
                            sugg.add(i.toString());
                    }
                }
            }
            if (sugg.isEmpty()) return;
            JList<String> suggestList = (JList<String>) suggestionPane.getViewport().getView();
            suggestList.setListData(sugg.toArray(new String[sugg.size()]));
            Rectangle searchB = searchingField.getBounds();
            suggestionPane.setLocation(searchB.x, searchB.y + searchB.height);
            int height = suggestList.getCellBounds(0,0).height*suggestList.getModel().getSize()+5;
            if (height > suggestList.getCellBounds(0,0).height*8+5) {
                height = suggestList.getCellBounds(0,0).height*8+5;
            }
            suggestionPane.setSize(searchB.width, height);
            suggestionPane.setVisible(true);
        } catch (BadLocationException ex) {
            System.err.println(ex);
        }
    }
    
    private void boundsSuggestionPane(int count) {
        int x = searchingField.getX(), w = searchingField.getWidth(),
                y = searchingField.getY() + searchingField.getHeight(),
                h = count * cellHeight;
        suggestionPane.setBounds(x, y, w, h);
    }
    
    private void setSuggestionsVisible(boolean visible) {
        if (visible) {
            boundsSuggestionPane(8);
        }
        else suggestionPane.setVisible(visible);
        suggestionPane.repaint();
        suggestionPane.validate();
        System.out.println(suggestionPane.getBounds());
    }

    void calculateCellHeight() {
        int height = ((JList<String>)suggestionPane.getViewport().getView()).getFixedCellHeight();
        if (height <= 0) height = 20;
        cellHeight = height;
    }
}
