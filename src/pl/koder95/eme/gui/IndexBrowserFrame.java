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

import pl.koder95.eme.Main;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import pl.koder95.eme.dfs.IndexList;

/**
 * Ramka wyświetlająca interfejs wyszukiwania indeksów.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.0.201
 */
public class IndexBrowserFrame extends JFrame {

    private static final long serialVersionUID = -2027617319030960915L;
    
    /**
     * Nazwy danych i przypisane im etykiety, czyli nazwy wyświetlane.
     */
    private final Map<String, String> names = new HashMap<>();
    private final IndexSearcher searcher;

    /**
     * Podstawowy konstruktor obiektu.
     * 
     * @param indices zbiór indeksów
     */
    IndexBrowserFrame(IndexList indices) {
        this.searcher = new IndexSearcher(indices);
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setIconImage(Main.FAVICON);
        super.setTitle("eMetrykant — " + searcher.getIndices().getName());
        super.setMinimumSize(new java.awt.Dimension(384, 365));
        super.setResizable(false);
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                indices.clear();
            }
        });
        this.dataPanel = new SimpleDataPanel(new LabeledDataShowing(), names);
        initComponents();

        GroupLayout l = new GroupLayout(super.getContentPane());
        super.getContentPane().setLayout(l);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, l.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(layers,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        l.setVerticalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(l.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(layers,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        super.pack();
        super.setLocationRelativeTo(null);
        
        suggestScroll.setVisible(false);
        rootPane.setDefaultButton(searchingPanel.getSearchButton());
    }

    private void initComponents() {
        mainPanel.setMinimumSize(new java.awt.Dimension(384, 365));
        mainPanel.setMaximumSize(new java.awt.Dimension(384, 365));

        prev.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        prev.setText("<");
        prev.setMargin(new java.awt.Insets(0, 5, 0, 5));

        next.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        next.setText(">");
        next.setMargin(new Insets(0, 5, 0, 5));

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(hLine)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(prev)
                        .addGap(0, 0, 0)
                        .addComponent(dataPanel,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(next))
                    .addComponent(footerPanel)
                    .addComponent(searchingPanel))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchingPanel)
                .addGap(127, 127, 127)
                .addGroup(mainPanelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(prev,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE)
                    .addComponent(dataPanel,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE)
                    .addComponent(next,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(hLine,
                        GroupLayout.PREFERRED_SIZE,
                        10,
                        GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(footerPanel)
                .addGap(20, 20, 20))
        );

        suggestPanel.setMaximumSize(new java.awt.Dimension(384, 365));
        suggestPanel.setMinimumSize(new java.awt.Dimension(384, 365));
        suggestPanel.setPreferredSize(new java.awt.Dimension(384, 365));
        suggestPanel.setOpaque(false);
        suggestPanel.setLayout(null);

        suggestList.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        suggestScroll.setViewportView(suggestList);
        suggestPanel.add(suggestScroll);

        layers.setLayer(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layers.setLayer(suggestPanel, JLayeredPane.POPUP_LAYER);

        GroupLayout layersLayout = new GroupLayout(layers);
        layers.setLayout(layersLayout);
        layersLayout.setHorizontalGroup(
            layersLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layersLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(mainPanel,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
            .addGroup(layersLayout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(suggestPanel,
                        GroupLayout.PREFERRED_SIZE,
                        384,
                        GroupLayout.PREFERRED_SIZE))
        );
        layersLayout.setVerticalGroup(
            layersLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
            .addGroup(layersLayout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(suggestPanel,
                        GroupLayout.Alignment.TRAILING,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE))
        );
    }

    /**
     * @return pomocnik wyszukiwania
     */
    public IndexSearcher getSearcher() {
        return searcher;
    }

    /**
     * @return stopka
     */
    public IndexFooterPanel getFooterPanel() {
        return footerPanel;
    }

    /**
     * @return panel informacyjny
     */
    public DataPanel getDataPanel() {
        return dataPanel;
    }

    /**
     * @return panel wyszukiwania
     */
    public IndexSearchingPanel getSearchingPanel() {
        return searchingPanel;
    }

    /**
     * @return przycisk do wyświetlenia późniejszego indeksu
     */
    public JButton getNext() {
        return next;
    }

    /**
     * @return przycisk do wyświetlenia wczyśniejszego indeksu
     */
    public JButton getPrev() {
        return prev;
    }

    /**
     * @return lista sugestii
     */
    public JList<String> getSuggestList() {
        return suggestList;
    }

    /**
     * @return panel główny
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * @return panel dla sugestii
     */
    public JPanel getSuggestPanel() {
        return suggestPanel;
    }

    /**
     * @return panel przewijany dla sugestii
     */
    public JScrollPane getSuggestScroll() {
        return suggestScroll;
    }
    
    /**
     * Dla podanej nazwy ustawia nazwę, która będzie wyświetlana,
     * czyli etykietę.
     * 
     * @param name nazwa systemowa
     * @param label nazwa wyświetlana - etykieta
     */
    public void setDataNameLabel(String name, String label) {
        names.put(name, label);
        System.out.println(name.hashCode());
    }
    

    // Variables declaration - do not modify
    private final IndexFooterPanel footerPanel = new IndexFooterPanel();
    private final DataPanel dataPanel;
    private final IndexSearchingPanel searchingPanel
            = new IndexSearchingPanel();
    private final JButton next = new JButton();
    private final JButton prev = new JButton();
    private final JLayeredPane layers = new JLayeredPane();
    private final JList<String> suggestList = new JList<>();
    private final JPanel mainPanel = new JPanel();
    private final JPanel suggestPanel = new JPanel();
    private final JSeparator hLine = new JSeparator();
    private final JScrollPane suggestScroll = new JScrollPane();
    // End of variables declaration
}
