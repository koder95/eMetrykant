/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.text.BadLocationException;
import pl.koder95.ip.idf.Index;
import pl.koder95.ip.idf.Indices;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.136
 */
public class IndexBrowserFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = -2027617319030960915L;
    
    private final IndexBrowserMediator mediator = new IndexBrowserMediator();
    private final IndexBrowser indices;
    private Index selected;

    public IndexBrowserFrame(IndexBrowser indices) {
        this.indices = indices;
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setIconImage(Main.FAVICON);
        super.setTitle(this.indices.getIndices().getName());
        super.setMinimumSize(new java.awt.Dimension(384, 365));
        super.setResizable(false);
        super.addKeyListener(mediator);
        initComponents();

        GroupLayout l = new GroupLayout(super.getContentPane());
        super.getContentPane().setLayout(l);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, l.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(layers, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        l.setVerticalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(l.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(layers, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        super.pack();
        super.setLocationRelativeTo(null);
        
        suggestScroll.setVisible(false);
        rootPane.setDefaultButton(searchingPanel.getSearchButton());
        
        mediator.registerBrowser(this.indices);
        mediator.registerFooterPanel(footerPanel);
        mediator.registerInfoPanel(infoPanel);
        mediator.registerNextButton(next);
        mediator.registerPrevButton(prev);
        mediator.registerSearchingPanel(searchingPanel);
        mediator.registerSuggestList(suggestList);
        mediator.registerSuggestScroll(suggestScroll);
        mediator.resetIndex();
        mediator.updateFooter();
    }

    public IndexBrowserFrame(int option) {
        this(new IndexSearcher(option));
    }

    public IndexBrowserFrame(Indices indices) {
        this(new IndexSearcher(indices));
    }

    public IndexBrowserFrame() {
        this(new IndexSearcher());
    }

    private void initComponents() {
        mainPanel.setMinimumSize(new java.awt.Dimension(384, 365));
        mainPanel.setMaximumSize(new java.awt.Dimension(384, 365));

        searchingPanel.getSearchField().addCaretListener((e) -> mediator.caretService(e));

        prev.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        prev.setText("<");
        prev.setMargin(new java.awt.Insets(0, 5, 0, 5));
        prev.addActionListener((e) -> mediator.prevIndex());

        next.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        next.setText(">");
        next.setMargin(new Insets(0, 5, 0, 5));
        next.addActionListener((e) -> mediator.nextIndex());

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hLine)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(prev)
                        .addGap(0, 0, 0)
                        .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(next))
                    .addComponent(footerPanel)
                    .addComponent(searchingPanel))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchingPanel)
                .addGap(127, 127, 127)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prev, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(next, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(hLine, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(footerPanel)
                .addGap(20, 20, 20))
        );

        suggestPanel.setMaximumSize(new java.awt.Dimension(384, 365));
        suggestPanel.setMinimumSize(new java.awt.Dimension(384, 365));
        suggestPanel.setPreferredSize(new java.awt.Dimension(384, 365));
        suggestPanel.setOpaque(false);
        suggestPanel.setLayout(null);

        suggestList.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        suggestList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                suggestListMouseClicked(evt);
            }
        });
        suggestList.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                suggestListKeyPressed(evt);
            }
        });
        suggestScroll.setViewportView(suggestList);
        suggestPanel.add(suggestScroll);

        layers.setLayer(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layers.setLayer(suggestPanel, JLayeredPane.POPUP_LAYER);

        javax.swing.GroupLayout layersLayout = new javax.swing.GroupLayout(layers);
        layers.setLayout(layersLayout);
        layersLayout.setHorizontalGroup(
            layersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layersLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
            .addGroup(layersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(suggestPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layersLayout.setVerticalGroup(
            layersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(suggestPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }

    private void searchCaretUpdate(javax.swing.event.CaretEvent evt) {                                   
        int dot = evt.getDot();
        suggestScroll.setVisible(false);
        if (dot > 0) try {
            String searchQuery = searchingPanel.getSearchField().getText(0, dot).toLowerCase();
            List<String> sugg = new ArrayList<>();
            for (Index i: indices.getLoaded()) {
                //System.out.println(i);
                String[] words = searchQuery.split(" "); //NOI18N
                if (i.getData(0).toLowerCase().startsWith(words[0])) {
                    //System.out.println("YES");
                    String label = Arrays.deepToString(i.getData());
                    label = label.replaceAll("[,\\[\\]]", "");
                    if (words.length == 1) sugg.add(label);
                    else {
                        String nameQuery = ""; //NOI18N
                        for (int in = 1; in < words.length; in++) {
                            nameQuery += words[in];
                            if (in < words.length-1) nameQuery += " "; //NOI18N
                        }
                        if (i.getData(1).toLowerCase().startsWith(nameQuery))
                            sugg.add(label);
                    }
                }
            }
            if (sugg.isEmpty()) return;
            suggestList.setListData(sugg.toArray(new String[sugg.size()]));
            mediator.locateSuggestScroll();
            suggestScroll.setVisible(true);
        } catch (BadLocationException ex) {
            System.err.println(ex);
        }
    }

    private void suggestListMouseClicked(java.awt.event.MouseEvent evt) {                                         
        if (evt.getClickCount() < 2) return;
        setFoundSelectedIndex();
        setForm();
    }

    private void suggestListKeyPressed(java.awt.event.KeyEvent evt) {                                       
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && suggestList.getSelectedIndex()
                >= 0) {
            setFoundSelectedIndex();
            setForm();
        }
    }

    private void setForm() {
        suggestScroll.setVisible(false);
        searchingPanel.getSearchField().setText(""); //NOI18N
        mediator.setIndex(selected);
    }
    
    public void setSelectedIndex(Index selected) {
        this.selected = selected;
    }
    
    public Index getSelectedIndex() {
        return selected;
    }
    
    private void setFoundSelectedIndex() {
        try {
            setSelectedIndex(indices.find(suggestList.getSelectedValue()));
        } catch (ObjectNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                "Wystąpił błąd, ponieważ nie znaleziono indeksu.\n\n"
                        + "Błąd typu:\n\t" + ex,
                "Nie znaleziono indeksu", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setSuggestionList(String[] suggestions) {
        suggestList.setListData(suggestions);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        IndexBrowserFrame ibf = new IndexBrowserFrame(Main.OPTION_BI);
        ibf.setVisible(true);
    }

    // Variables declaration - do not modify
    private final IndexFooterPanel footerPanel = new IndexFooterPanel();
    private final IndexInfoPanel infoPanel = new IndexInfoPanel();
    private final IndexSearchingPanel searchingPanel = new IndexSearchingPanel(mediator);
    private final JButton next = new javax.swing.JButton();
    private final JButton prev = new javax.swing.JButton();
    private final JLayeredPane layers = new javax.swing.JLayeredPane();
    private final JList<String> suggestList = new javax.swing.JList<>();
    private final JPanel mainPanel = new javax.swing.JPanel();
    private final JPanel suggestPanel = new javax.swing.JPanel();
    private final JSeparator hLine = new javax.swing.JSeparator();
    private final JScrollPane suggestScroll = new javax.swing.JScrollPane();
    // End of variables declaration
}
