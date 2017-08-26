/*
 * Copyright (C) 2017 Kamil Jan Mularski [@koder95]
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
package pl.koder95.eme.gui;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import pl.koder95.eme.Main;
import pl.koder95.eme.idf.Index;
import pl.koder95.eme.idf.Indices;

/**
 * Klasa zarządza całym interfejsem graficznym.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.203, 2017-08-26
 * @since 0.0.201
 */
public class GUIMediator {
    
    private class GUI {
        IndexBrowserFrame frame;
        
        GUI(Indices indices) {
            this.frame = new IndexBrowserFrame(indices);
        }
        
        IndexSearcher getSearcher() {
            return frame.getSearcher();
        }
        IndexFooterPanel getFooterPanel() {
            return frame.getFooterPanel();
        }
        IndexInfoPanel getInfoPanel() {
            return frame.getInfoPanel();
        }
        IndexSearchingPanel getSearchingPanel() {
            return frame.getSearchingPanel();
        }
        JButton getNextButton() {
            return frame.getNext();
        }
        JButton getPrevButton() {
            return frame.getPrev();
        }
        JList<String> getSuggestList() {
            return frame.getSuggestList();
        }
        JScrollPane getSuggestScroll() {
            return frame.getSuggestScroll();
        }
        Index getFirstIndex() {
            return frame.getSearcher().getFirst();
        }
        Index getLastIndex() {
            return frame.getSearcher().getLast();
        }
    }
    
    private final GUI gui;
    private final ActionMap actions = new ActionMap();
    private final InputMap input;
    
    /**
     * Tworzy GUI zależnie od zbioru indeksów.
     * 
     * @param indices zbiór indeksów
     */
    public GUIMediator(Indices indices) {
        gui = new GUI(indices);
        input = new ComponentInputMap((JComponent) gui.frame.getContentPane());
        ((JPanel) gui.frame.getContentPane()).setActionMap(actions);
        ((JPanel) gui.frame.getContentPane())
                .setInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, input);
        
        gui.getSearchingPanel().getYearCombo()
                .addActionListener((e) -> loadActComboBoxModel());
        actions.put("search", new AbstractAction("Szukaj") {
            private static final long serialVersionUID = 4377386270269629176L;
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        
        gui.getSearchingPanel().getActSearching().setAction(
                new AbstractAction() {
            private static final long serialVersionUID = -5644390861803492172L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setActSearchingEnable(!gui.getSearchingPanel().getActCombo()
                        .isEnabled());
            }
        });
        gui.getPrevButton().setAction(new AbstractAction("<") {
            private static final long serialVersionUID = 4377386270269629176L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setIndex(gui.getInfoPanel().getIndexID()-1);
            }
        });
        gui.getNextButton().setAction(new AbstractAction(">") {
            private static final long serialVersionUID = -5644390861803492172L;
            @Override
            public void actionPerformed(ActionEvent e) {
                setIndex(gui.getInfoPanel().getIndexID()+1);
            }
        });
        actions.put("switchSearching", gui.getSearchingPanel().getActSearching()
                .getAction());
        actions.put("prevIndex", gui.getPrevButton().getAction());
        actions.put("nextIndex", gui.getNextButton().getAction());
        
        gui.getSearchingPanel().getSearchButton()
                .setAction(actions.get("search"));
        gui.getSearchingPanel().getSearchField()
                .setAction(actions.get("search"));
        
        input.put(KeyStroke.getKeyStroke("pressed ENTER"), "search");
        input.put(KeyStroke.getKeyStroke("pressed INSERT"), "switchSearching");
        input.put(KeyStroke.getKeyStroke("pressed PAGE_DOWN"), "prevIndex");
        input.put(KeyStroke.getKeyStroke("pressed PAGE_UP"), "nextIndex");
    }
    
    /**
     * Ustawia indeks, który ma zostać wyświetlony w panelu informacyjnym.
     * 
     * @param i indeks
     */
    public void setIndex(Index i) {
        if (i == null) Toolkit.getDefaultToolkit().beep();
        else {
            gui.getInfoPanel().setIndex(i);
        }
    }
    
    /**
     * Ustawia indeks, który ma zostać wyświetlony w panelu informacyjnym.
     * 
     * @param id identyfikator
     */
    public void setIndex(int id) {
        int size = gui.getSearcher().getIndices().size();
        if (id < 1 || id > size) setIndex(null);
        else setIndex(gui.getSearcher().get(id));
    }

    /**
     * Przywraca początkowy stan panelu informacyjnego.
     * @see IndexInfoPanel#resetIndex()
     */
    public void resetIndex() {
        gui.getInfoPanel().resetIndex();
    }
    
    /**
     * Ustawia następny indeks.
     */
    public void nextIndex() {
        setIndex(gui.getInfoPanel().getIndexID()+1);
    }
    
    /**
     * Ustawia poprzedni indeks.
     */
    public void prevIndex() {
        setIndex(gui.getInfoPanel().getIndexID()-1);
    }
    
    /**
     * Włącza lub wyłącza wyszukiwanie po numerze aktu.
     * 
     * @param actSearching jeśli {@code true}, włącza wyszukiwanie po numerze
     * aktu i wyłącza wyszukiwanie standardowe, jeśli {@code false} wyłącza
     * wyszukiwanie po numerze aktu i włącza wyszukiwanie standardowe
     */
    public void setActSearchingEnable(boolean actSearching) {
        gui.getSearchingPanel().getActCombo().setEnabled(actSearching);
        gui.getSearchingPanel().getYearCombo().setEnabled(actSearching);
        gui.getSearchingPanel().getActSearching().setSelected(actSearching);
        if (actSearching) {
            DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<>();
            int minYear = gui.getFirstIndex().getActNumber().getYear(),
                    maxYear = gui.getLastIndex().getActNumber().getYear();
            for (int i = maxYear; i >= minYear; i--) {
                if (gui.getSearcher().hasYear(i)) model.addElement(i);
            }
            gui.getSearchingPanel().getYearCombo().setModel(model);
            loadActComboBoxModel();
        } else {
            gui.getSearchingPanel().getActCombo()
                    .setModel(new DefaultComboBoxModel<>());
            gui.getSearchingPanel().getYearCombo()
                    .setModel(new DefaultComboBoxModel<>());
        }
        gui.getSearchingPanel().getSearchField().setEnabled(!actSearching);
        gui.getSuggestScroll().setVisible(false);
    }
    
    /**
     * 
     * @return {@code true} - jeśli wyszukiwanie po numerze aktu jest włączone,
     * w przeciwnym razie - {@code false}
     */
    public boolean isActSearchingEnable() {
        return gui.getSearchingPanel().getActSearching().isSelected();
    }

    /**
     * Przełącza wyszukiwania. Jeśli ustawione jest standardowe, zostanie
     * włączone wyszukiwanie po numerze aktu. Jeśli ustawione jest wyszukiwanie
     * po numerze aktu, zostanie włączone wyszukiwanie standardowe.
     */
    public void switchSearching() {
        setActSearchingEnable(isActSearchingEnable());
    }

    /**
     * Wczytuje numery aktów dla wybranego roku do modelu listy rozwijanej.
     */
    public void loadActComboBoxModel() {
        int y = (int) gui.getSearchingPanel().getYearCombo().getSelectedItem();
        Index[] years = gui.getSearcher().find(y);
        Arrays.sort(years, (Index o1, Index o2)
                -> o1.getActNumber().compareTo(o2.getActNumber()));
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Index i: years) {
            model.addElement(i.getActNumber().getSign());
        }
        gui.getSearchingPanel().getActCombo().setModel(model);
    }

    private void setForm(Index i) {
        gui.getSuggestScroll().setVisible(false);
        gui.getSearchingPanel().getSearchField().setText(""); //NOI18N
        setIndex(i);
    }

    /**
     * Uruchamia wyszukiwanie na podstawie wprowadzonego tekstu do pola
     * wyszukiwania. Wyszukany indeks wyświetla w panelu informacyjnym. Jeżeli
     * wyszukiwanie nie miało wyników to zostanie odtworzony systemowy komunikat
     * błędu.
     */
    public void search() {
        Index[] result;
        if (isActSearchingEnable()) {
            result = gui.getSearcher().find(
                (int) gui.getSearchingPanel().getYearCombo().getSelectedItem(),
                (String) gui.getSearchingPanel().getActCombo().getSelectedItem()
            );
            System.out.println("result=" + Arrays.toString(result));
        } else {
            result = gui.getSearcher()
                    .find(gui.getSearchingPanel().getSearchField().getText());
            System.out.println("result=" + Arrays.toString(result));
        }
        if (result != null) setForm(gui.getSearcher().selectOne(result));
        else Toolkit.getDefaultToolkit().beep();
        Main.releaseMemory();
    }
    
    /**
     * Aktualizuje stopkę.
     */
    public void updateFooter() {
        IndexFooterPanel footer = gui.getFooterPanel();
        footer.getTitle().setText(gui.getSearcher().getIndices().getName());
        footer.getMin().setText(gui.getFirstIndex().getActNumber().getSign()
                + "/" + gui.getFirstIndex().getActNumber().getYear());
        footer.getMax().setText(gui.getLastIndex().getActNumber().getSign()
                + "/" + gui.getLastIndex().getActNumber().getYear());
        footer.getSum().setText("" + gui.getSearcher().getIndices().size());
    }
    
    /**
     * Określa dokładne rozmiary i położenie panelu przewijanego dla sugestii.
     */
    public void locateSuggestScroll() {
        gui.getSuggestScroll().setBounds(calcBoundsSuggestScroll());
    }
    
    private Rectangle calcBoundsSuggestScroll() {
        int x = gui.getSearchingPanel().getSearchField().getX(),
                y = gui.getSearchingPanel().getSearchField().getY()
                    + gui.getSearchingPanel().getSearchField().getHeight()-3,
                w = gui.getSearchingPanel().getSearchField().getWidth();
        
        int cellH = gui.getSuggestList().getFixedCellHeight();
        if (cellH == 0) cellH = 10;
        int h = cellH*gui.getSuggestList().getModel().getSize()+5;
        int maxH = cellH*8+5;
        if (h > maxH) h = maxH;
        
        return new Rectangle(
                gui.getSearchingPanel().getX() + x,
                gui.getSearchingPanel().getY() + y,
                w,
                h
        );
    }
    
    /**
     * Pokazuje okienko wyszukiwania.
     */
    public void showFrame() {
        gui.frame.setVisible(true);
        updateFooter();
    }
}
