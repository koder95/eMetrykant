/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip.gui;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import java.awt.event.KeyListener;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import pl.koder95.ip.ActManager;
import pl.koder95.ip.SuggestIndexManager;
import pl.koder95.ip.idf.Index;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.147
 */
public class IndexBrowserMediator implements KeyListener {
    private IndexSearcher searcher;
    private IndexFooterPanel footerPanel;
    private IndexInfoPanel infoPanel;
    private IndexSearchingPanel searchingPanel;
    private JButton next;
    private JButton prev;
    private JList<String> suggestList;
    private JScrollPane suggestScroll;
    private Index firstIndex, lastIndex;
    private ActManager.ActPrevNext apn;
    private final SuggestIndexManager sim = new SuggestIndexManager();

    public void registerSearcher(IndexSearcher searcher) {
        this.searcher = searcher;
        firstIndex = searcher.getFirst();
        lastIndex = searcher.getLast();
        sim.add(searcher.getIndices());
        sim.sortByData();
    }

    public void registerFooterPanel(IndexFooterPanel footerPanel) {
        this.footerPanel = footerPanel;
    }

    public void registerInfoPanel(IndexInfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public void registerNextButton(JButton next) {
        this.next = next;
    }

    public void registerPrevButton(JButton prev) {
        this.prev = prev;
    }

    public void registerSearchingPanel(IndexSearchingPanel searchingPanel) {
        this.searchingPanel = searchingPanel;
    }

    public void registerSuggestList(JList<String> suggestList) {
        this.suggestList = suggestList;
    }

    public void registerSuggestScroll(JScrollPane suggestScroll) {
        this.suggestScroll = suggestScroll;
    }
    
    public void setIndex(Index i) {
        if (i == null) Toolkit.getDefaultToolkit().beep();
        else {
            infoPanel.setIndex(i);
            ActManager actM = searcher.getActManager();
            int index = actM.indexOf(i.getActNumber().getYear(),
                    i.getActNumber().getSign());
            apn = actM.create(i.getActNumber().getYear(), index);
        }
    }

    public ActManager.ActPrevNext getAPN() {
        return apn;
    }

    public void resetIndex() {
        infoPanel.resetIndex();
    }
    
    public void nextIndex() {
        int actI = apn.getNextIndex();
        int year = apn.getCurrentYear();
        if (actI == 0) year = apn.getNextYear();
        
        Index[] result = searcher.find(year,
                searcher.getActManager().get(year, actI));
        setIndex(result[0]);
    }
    
    public void prevIndex() {
        int actI = apn.getPrevIndex();
        int year = apn.getCurrentYear();
        if (actI == searcher.getLoaded().size()) year = apn.getPrevYear();
        
        Index[] result = searcher.find(year,
                searcher.getActManager().get(year, actI));
        setIndex(result[0]);
    }
    
    public void setActSearchingEnable(boolean actSearching) {
        searchingPanel.getActCombo().setEnabled(actSearching);
        searchingPanel.getYearCombo().setEnabled(actSearching);
        searchingPanel.getSearchButton().setEnabled(actSearching);
        if (actSearching) {
            DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<>();
            int minYear = firstIndex.getActNumber().getYear(),
                    maxYear = lastIndex.getActNumber().getYear();
            for (int i = maxYear; i >= minYear; i--) {
                if (searcher.isYear(i)) model.addElement(i);
            }
            searchingPanel.getYearCombo().setModel(model);
            loadActComboBoxModel();
        } else {
            searchingPanel.getActCombo().setModel(new DefaultComboBoxModel<>());
            searchingPanel.getYearCombo().setModel(new DefaultComboBoxModel<>());
        }
        searchingPanel.getSearchField().setEnabled(!actSearching);
        suggestScroll.setVisible(false);
    }
    
    public boolean isActSearchingEnable() {
        return searchingPanel.getActSearching().isSelected();
    }

    public void switchSearching() {
        setActSearchingEnable(isActSearchingEnable());
    }

    public void loadActComboBoxModel() {
        Index[] years = searcher.find((Integer) searchingPanel.getYearCombo().getSelectedItem());
        Arrays.sort(years, (Index o1, Index o2)
                -> o1.getActNumber().compareTo(o2.getActNumber()));
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Index i: years) {
            model.addElement(i.getActNumber().getSign());
        }
        searchingPanel.getActCombo().setModel(model);
    }

    private void setForm(Index i) {
        suggestScroll.setVisible(false);
        searchingPanel.getSearchField().setText(""); //NOI18N
        setIndex(i);
    }

    public void search() {
        Index[] result;
        if (isActSearchingEnable()) {
            result = searcher.find(
                (Integer) searchingPanel.getYearCombo().getSelectedItem(),
                    (String) searchingPanel.getActCombo().getSelectedItem()
            );
            System.out.println("result=" + Arrays.toString(result));
        } else {
            result = searcher.find(searchingPanel.getSearchField().getText());
            System.out.println("result=" + Arrays.toString(result));
        }
        if (result != null) setForm(searcher.selectOne(result));
        else Toolkit.getDefaultToolkit().beep();
    }

    public void searchKeyPressed(int keyCode) {
        int selected;
        switch (keyCode) {
            case KeyEvent.VK_DOWN:
                selected = suggestList.getFirstVisibleIndex();
                break;
            case KeyEvent.VK_UP:
                selected = suggestList.getLastVisibleIndex();
                break;
            default: return;
        }
        suggestList.setSelectedIndex(selected);
        suggestList.grabFocus();
    }

    public String getTitle() {
        return searcher.getIndices().getName();
    }

    public Index getFirstIndex() {
        System.out.println("firstIndex=" + firstIndex);
        return firstIndex;
    }

    public Index getLastIndex() {
        System.out.println("lastIndex=" + lastIndex);
        return lastIndex;
    }
    
    public void updateFooter() {
        footerPanel.getTitle().setText(searcher.getIndices().getName());
        footerPanel.getMin().setText(firstIndex.getActNumber().getSign()
                + "/" + firstIndex.getActNumber().getYear());
        footerPanel.getMax().setText(lastIndex.getActNumber().getSign()
                + "/" + lastIndex.getActNumber().getYear());
    }
    
    public void locateSuggestScroll() {
        suggestScroll.setBounds(calcBoundsSuggestScroll());
    }
    
    private Rectangle calcBoundsSuggestScroll() {
        int x = searchingPanel.getSearchField().getX(),
                y = searchingPanel.getSearchField().getY()
                    + searchingPanel.getSearchField().getHeight()-3,
                w = searchingPanel.getSearchField().getWidth();
        
        int cellH = suggestList.getFixedCellHeight();
        if (cellH == 0) cellH = 10;
        int h = cellH*suggestList.getModel().getSize()+5;
        int maxH = cellH*8+5;
        if (h > maxH) h = maxH;
        
        return new Rectangle(x+searchingPanel.getX(),y+searchingPanel.getY(),w,h);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (VK_LEFT == e.getKeyCode()) prev.doClick();
        if (VK_RIGHT == e.getKeyCode()) next.doClick();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // do nothing
    }
    
    //private int lastCaretDot = -1;
    public void caretService(CaretEvent e) {
        /*
        int dot = e.getDot();
        suggestScroll.setVisible(false);
        if (dot > lastCaretDot) {
            if (suggestIndexManager == null) initSIM();
            int index = dot-1;
            if (suggestIndexManager.getFilterCount() != index) {
                for (int i = 0; i < dot; i++) acceptChar(i, searchingPanel.getSearchField().getText().charAt(i));
            }
            else acceptChar(index, searchingPanel.getSearchField().getText().charAt(index));
            updateSuggestList();
            locateSuggestScroll();
            suggestScroll.setVisible(true);
        } else {
            if (suggestIndexManager != null) suggestIndexManager.clear();
            suggestIndexManager = null;
        }
        lastCaretDot = dot;
        */
        sim.serviceEvent(e);
        String[] suggestions = sim.getSuggestions();
        System.out.println("suggestions.length=" + suggestions.length);
        suggestList.setListData(suggestions);
        sim.clear();
        locateSuggestScroll();
        suggestScroll.setVisible(true);
    }
}
