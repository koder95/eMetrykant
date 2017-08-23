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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import java.awt.event.KeyListener;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import pl.koder95.eme.idf.Index;
import pl.koder95.eme.idf.Indices;

/**
 * Klasa zarządza całym interfejsem graficznym.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.202, 2017-08-23
 * @since 0.0.201
 */
public class GUIMediator implements KeyListener {
    
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
    
    /**
     * Tworzy GUI zależnie od zbioru indeksów.
     * 
     * @param indices zbiór indeksów
     */
    public GUIMediator(Indices indices) {
        gui = new GUI(indices);
        gui.frame.addKeyListener((KeyListener) this);
        
        gui.getPrevButton().addActionListener((e) -> prevIndex());
        gui.getNextButton().addActionListener((e) -> nextIndex());
        gui.getSearchingPanel().getActSearching()
                .addActionListener((e) -> switchSearching());
        gui.getSearchingPanel().getYearCombo()
                .addActionListener((e) -> loadActComboBoxModel());
        
        ActionListener search = (e) -> search();
        gui.getSearchingPanel().getSearchButton().addActionListener(search);
        gui.getSearchingPanel().getSearchField().addActionListener(search);
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
        setIndex(gui.getSearcher().get(gui.getInfoPanel().getIndexID()+1));
    }
    
    /**
     * Ustawia poprzedni indeks.
     */
    public void prevIndex() {
        setIndex(gui.getSearcher().get(gui.getInfoPanel().getIndexID()-1));
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
        gui.getSearchingPanel().getSearchButton().setEnabled(actSearching);
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

    @Override
    public void keyPressed(KeyEvent e) {
        if (VK_LEFT == e.getKeyCode()) gui.getPrevButton().doClick();
        if (VK_RIGHT == e.getKeyCode()) gui.getNextButton().doClick();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // do nothing
    }
    
    /**
     * Pokazuje okienko wyszukiwania.
     */
    public void showFrame() {
        gui.frame.setVisible(true);
        updateFooter();
    }
}
