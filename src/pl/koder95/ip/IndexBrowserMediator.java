/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import java.awt.event.KeyListener;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import pl.koder95.ip.idf.Index;
import pl.koder95.tools.Eliminator;

/**
 *
 * @author Kamil
 */
public class IndexBrowserMediator implements KeyListener {
    private IndexBrowser browser;
    private IndexFooterPanel footerPanel;
    private IndexInfoPanel infoPanel;
    private IndexSearchingPanel searchingPanel;
    private JButton next;
    private JButton prev;
    private JList<String> suggestList;
    private JPanel suggestPanel;
    private JScrollPane suggestScroll;
    private Index firstIndex, lastIndex;
    private Eliminator<Index> suggestIndexManager;

    public void registerBrowser(IndexBrowser browser) {
        this.browser = browser;
        firstIndex = browser.getFirst();
        lastIndex = browser.getLast();
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

    public void registerSuggestPanel(JPanel suggestPanel) {
        this.suggestPanel = suggestPanel;
    }

    public void registerSuggestScroll(JScrollPane suggestScroll) {
        this.suggestScroll = suggestScroll;
    }

    private ActManager.ActPrevNext apn;
    
    public void setIndex(Index i) {
        infoPanel.setIndex(i);
        apn = browser.getActManager().create(i.AN.getYear(),
                browser.getActManager().indexOf(i.AN.getYear(), i.AN.getSign()));
        
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
        try {
            setIndex(browser.find(year, browser.getActManager().get(year, actI)));
        } catch (ObjectNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                "Wystąpił błąd, ponieważ nie znaleziono indeksu.\n\n"
                        + "Błąd typu:\n\t" + ex,
                "Nie znaleziono indeksu", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void prevIndex() {
        int actI = apn.getPrevIndex();
        int year = apn.getCurrentYear();
        if (actI == browser.getLoaded().length) year = apn.getPrevYear();
        try {
            setIndex(browser.find(year, browser.getActManager().get(year, actI)));
        } catch (ObjectNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                "Wystąpił błąd, ponieważ nie znaleziono indeksu.\n\n"
                        + "Błąd typu:\n\t" + ex,
                "Nie znaleziono indeksu", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setActSearchingEnable(boolean actSearching) {
        searchingPanel.getActCombo().setEnabled(actSearching);
        searchingPanel.getYearCombo().setEnabled(actSearching);
        searchingPanel.getSearchButton().setEnabled(actSearching);
        if (actSearching) {
            DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<>();
            int minYear = firstIndex.AN.getYear(), maxYear = lastIndex.AN.getYear();
            for (int i = maxYear; i >= minYear; i--) {
                if (browser.isYear(i)) model.addElement(i);
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
        Index[] years = browser.find((Integer) searchingPanel.getYearCombo().getSelectedItem());
        Arrays.sort(years, (Index o1, Index o2) -> o1.AN.compareTo(o2.AN));
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Index i: years) {
            model.addElement(i.AN.getSign());
        }
        searchingPanel.getActCombo().setModel(model);
    }

    private void setForm(Index i) {
        suggestScroll.setVisible(false);
        searchingPanel.getSearchField().setText(""); //NOI18N
        setIndex(i);
    }

    public void search() {
        try {
            setForm(browser.find((Integer) searchingPanel.getYearCombo().getSelectedItem(),
                    (String) searchingPanel.getActCombo().getSelectedItem()));
        } catch (ObjectNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                "Wystąpił błąd, ponieważ nie znaleziono indeksu.\n\n"
                        + "Błąd typu:\n\t" + ex,
                "Nie znaleziono indeksu", JOptionPane.ERROR_MESSAGE);
        }
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
        return browser.getTitle();
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
        footerPanel.getTitle().setText(browser.getTitle());
        footerPanel.getMin().setText(firstIndex.AN.getSign() + "/" + firstIndex.AN.getYear());
        footerPanel.getMax().setText(lastIndex.AN.getSign() + "/" + lastIndex.AN.getYear());
    }
    
    public void locateSuggestScroll() {
        suggestScroll.setBounds(calcBoundsSuggestScroll());
    }
    
    private Rectangle calcBoundsSuggestScroll() {
        int x = searchingPanel.getSearchField().getX(),
                y = searchingPanel.getSearchField().getY()+searchingPanel.getSearchField().getHeight()-3,
                w = searchingPanel.getSearchField().getWidth();
        int height = suggestList.getCellBounds(0,0).height*suggestList.getModel().getSize()+5;
        if (height > suggestList.getCellBounds(0,0).height*8+5) {
            height = suggestList.getCellBounds(0,0).height*8+5;
        }
        return new Rectangle(x+searchingPanel.getX(),y+searchingPanel.getY(),w,height);
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
    
    private void initSIM() {
        suggestIndexManager = new Eliminator<>(browser.getLoaded());
    }
    
    private void acceptChar(int index, char c) {
        suggestIndexManager.addFilter((i)->{
            boolean cch = false;
            for (String data: i.getData()) {
                if (data.length() <= index) continue;
                if (data.charAt(index) == c) {
                    System.out.print("DATA ACCEPT ");
                    System.out.println(data);
                    cch = true;
                }
            }
            return !cch;
        });
    }
    
    private void updateSuggestList() {
        Object[] indices = suggestIndexManager.getResult();
        String[] listData = new String[indices.length];
        for (int i = 0; i < indices.length; i++) {
            listData[i] = indices[i].toString();
        }
        suggestList.setListData(listData);
    }
    
    //private int lastCaretDot = -1;
    private SuggestIndexManager sim = new SuggestIndexManager();
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
    }
}
