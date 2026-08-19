package pl.koder95.eme.fx;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import pl.koder95.eme.application.IndexManagementService;
import pl.koder95.eme.domain.index.BookType;
import pl.koder95.eme.domain.index.Index;

import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Kontroler okna zarządzania danymi: pozwala dodawać, modyfikować i usuwać rekordy
 * poszczególnych ksiąg, a następnie zapisać zmiany.
 *
 * @since 0.5.0
 */
public class DataManagementView implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(DataManagementView.class.getName());

    private final IndexManagementService indexManagementService;
    private final FxDialogs dialogs;
    private final ResourceBundle bundle;
    private final Map<BookType, BookTab> tabs = new EnumMap<>(BookType.class);
    private boolean dataChanged;

    public DataManagementView(IndexManagementService indexManagementService,
                              FxDialogs dialogs,
                              ResourceBundle bundle) {
        this.indexManagementService = Objects.requireNonNull(indexManagementService,
                "indexManagementService must not be null");
        this.dialogs = Objects.requireNonNull(dialogs, "dialogs must not be null");
        this.bundle = Objects.requireNonNull(bundle, "bundle must not be null");
    }

    @FXML
    private BorderPane root;
    @FXML
    private TabPane books;
    @FXML
    private TextField filter;
    @FXML
    private Button editButton;
    @FXML
    private Button removeButton;
    @FXML
    private Label numberOfRecords;
    @FXML
    private Label unsavedChanges;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (BookType type : BookType.values()) {
            BookTab bookTab = createBookTab(type);
            tabs.put(type, bookTab);
            books.getTabs().add(bookTab.tab());
        }
        books.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> updateStatus());
        filter.textProperty().addListener((observable, oldText, newText) -> applyFilter(newText));
        updateStatus();
    }

    /**
     * @return {@code true} gdy dane zostały zmienione, więc widoki korzystające z nich
     * wymagają odświeżenia
     */
    public boolean isDataChanged() {
        return dataChanged;
    }

    /**
     * Dodaje nowy rekord do aktualnie wybranej księgi.
     */
    public void addRecord(ActionEvent actionEvent) {
        BookType type = selectedType();
        Map<String, String> data = askForData(type, Map.of(), false);
        if (data == null) {
            return;
        }
        Index added = indexManagementService.add(type, data);
        onDataChanged(type, added);
    }

    /**
     * Modyfikuje rekord zaznaczony w aktualnie wybranej księdze.
     */
    public void editRecord(ActionEvent actionEvent) {
        BookType type = selectedType();
        Index selected = selectedRecord(type);
        if (selected == null) {
            return;
        }
        Map<String, String> data = askForData(type, selected.getAllData(), true);
        if (data == null) {
            return;
        }
        Index updated = indexManagementService.update(type, selected, data);
        onDataChanged(type, updated);
    }

    /**
     * Usuwa rekord zaznaczony w aktualnie wybranej księdze.
     */
    public void removeRecord(ActionEvent actionEvent) {
        BookType type = selectedType();
        Index selected = selectedRecord(type);
        if (selected == null) {
            return;
        }
        Alert confirmation = dialogs.createConfirmationAlert(
                scene(),
                bundle.getString("ALERT_CONFIRM_REMOVE_TITLE"),
                bundle.getString("ALERT_CONFIRM_REMOVE_HEADER"),
                describe(type, selected)
        );
        confirmation.showAndWait();
        if (confirmation.getResult() == ButtonType.OK && indexManagementService.remove(type, selected)) {
            onDataChanged(type, null);
        }
    }

    /**
     * Utrwala wszystkie wprowadzone zmiany.
     */
    public void save(ActionEvent actionEvent) {
        try {
            indexManagementService.save();
            updateStatus();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Błąd podczas zapisywania danych", ex);
            dialogs.createErrorAlert(
                    scene(),
                    bundle.getString("ALERT_SAVE_ERROR_TITLE"),
                    bundle.getString("ALERT_SAVE_ERROR_HEADER"),
                    ex.getMessage() == null ? ex.toString() : ex.getMessage()
            ).showAndWait();
        }
    }

    /**
     * Zamyka okno, pytając o niezapisane zmiany.
     */
    public void close(ActionEvent actionEvent) {
        requestClose();
    }

    /**
     * Zamyka okno, gdy nie ma niezapisanych zmian, albo gdy użytkownik zgodzi się je porzucić.
     */
    public void requestClose() {
        if (indexManagementService.hasUnsavedChanges() && !confirmDiscard()) {
            return;
        }
        Window window = window();
        if (window != null) {
            window.hide();
        }
    }

    private boolean confirmDiscard() {
        Alert confirmation = dialogs.createConfirmationAlert(
                scene(),
                bundle.getString("ALERT_CONFIRM_DISCARD_TITLE"),
                bundle.getString("ALERT_CONFIRM_DISCARD_HEADER"),
                bundle.getString("ALERT_CONFIRM_DISCARD_CONTENT")
        );
        confirmation.showAndWait();
        if (confirmation.getResult() != ButtonType.OK) {
            return false;
        }
        indexManagementService.discardChanges();
        dataChanged = true;
        return true;
    }

    /**
     * Pyta o dane rekordu tak długo, jak długo są nieprawidłowe.
     *
     * @return dane rekordu, albo {@code null} gdy użytkownik zrezygnował
     */
    private Map<String, String> askForData(BookType type, Map<String, String> initialData, boolean editing) {
        Map<String, String> data = initialData;
        while (true) {
            IndexRecordDialog dialog = new IndexRecordDialog(type, data, editing, bundle);
            Window window = window();
            if (window != null) {
                dialog.initOwner(window);
            }
            Optional<Map<String, String>> result = dialog.showAndWait();
            if (result.isEmpty()) {
                return null;
            }
            data = result.get();
            List<String> invalid = indexManagementService.validate(type, data);
            if (invalid.isEmpty()) {
                return data;
            }
            dialogs.createErrorAlert(
                    scene(),
                    bundle.getString("ALERT_INVALID_RECORD_TITLE"),
                    bundle.getString("ALERT_INVALID_RECORD_HEADER"),
                    invalid.stream().map(field -> FieldLabels.get(bundle, field)).collect(Collectors.joining(", "))
            ).showAndWait();
        }
    }

    private void onDataChanged(BookType type, Index toSelect) {
        dataChanged = true;
        BookTab bookTab = tabs.get(type);
        bookTab.records().setAll(indexManagementService.getIndices(type));
        if (toSelect != null) {
            bookTab.table().getSelectionModel().select(toSelect);
            bookTab.table().scrollTo(toSelect);
        }
        updateStatus();
    }

    private BookTab createBookTab(BookType type) {
        ObservableList<Index> records = FXCollections.observableArrayList(indexManagementService.getIndices(type));
        FilteredList<Index> filtered = new FilteredList<>(records, record -> true);
        SortedList<Index> sorted = new SortedList<>(filtered);

        TableView<Index> table = new TableView<>(sorted);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label(bundle.getString("FX_TABLE_NO_RECORDS")));
        for (String field : type.getFieldSchema()) {
            TableColumn<Index, String> column = new TableColumn<>(FieldLabels.get(bundle, field));
            column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getData(field)));
            table.getColumns().add(column);
        }
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateStatus());
        table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editRecord(null);
            }
        });

        Tab tab = new Tab(type.getBookName(), table);
        tab.setClosable(false);
        tab.setUserData(type);
        return new BookTab(type, tab, table, records, filtered);
    }

    private void applyFilter(String text) {
        String needle = text == null ? "" : text.trim().toLowerCase();
        for (BookTab bookTab : tabs.values()) {
            bookTab.filtered().setPredicate(matches(bookTab.type(), needle));
        }
        updateStatus();
    }

    /**
     * Filtr porównuje wszystkie pola rekordu, a odstępy traktuje jak znak rozdzielający imiona,
     * dzięki czemu można szukać zarówno "Jan_Maria", jak i "Jan Maria".
     */
    private Predicate<Index> matches(BookType type, String needle) {
        if (needle.isEmpty()) {
            return record -> true;
        }
        String normalized = needle.replace(' ', IndexManagementService.NAME_SEPARATOR);
        return record -> type.getFieldSchema().stream()
                .map(field -> record.getData(field).toLowerCase())
                .anyMatch(value -> value.contains(needle) || value.contains(normalized));
    }

    private void updateStatus() {
        BookTab bookTab = tabs.get(selectedType());
        boolean recordSelected = bookTab != null && bookTab.table().getSelectionModel().getSelectedItem() != null;
        editButton.setDisable(!recordSelected);
        removeButton.setDisable(!recordSelected);
        if (bookTab != null) {
            numberOfRecords.setText(bookTab.filtered().size() + " / " + bookTab.records().size());
        }
        unsavedChanges.setText(indexManagementService.hasUnsavedChanges()
                ? bundle.getString("FX_LABEL_UNSAVED_CHANGES")
                : "");
    }

    private String describe(BookType type, Index record) {
        return type.getFieldSchema().stream()
                .map(field -> FieldLabels.get(bundle, field) + ": " + record.getData(field))
                .collect(Collectors.joining("\n"));
    }

    private BookType selectedType() {
        Tab selected = books.getSelectionModel().getSelectedItem();
        return selected == null ? BookType.values()[0] : (BookType) selected.getUserData();
    }

    private Index selectedRecord(BookType type) {
        BookTab bookTab = tabs.get(type);
        return bookTab == null ? null : bookTab.table().getSelectionModel().getSelectedItem();
    }

    private Scene scene() {
        return root.getScene();
    }

    private Window window() {
        Scene scene = scene();
        return scene == null ? null : scene.getWindow();
    }

    /**
     * Elementy zakładki jednej księgi: tabela wraz z listą rekordów i jej widokiem po filtrowaniu.
     */
    private record BookTab(BookType type, Tab tab, TableView<Index> table, ObservableList<Index> records,
                           FilteredList<Index> filtered) {
    }
}
