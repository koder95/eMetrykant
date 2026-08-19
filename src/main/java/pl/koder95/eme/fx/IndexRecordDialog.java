package pl.koder95.eme.fx;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pl.koder95.eme.domain.index.BookType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Dialog pozwalający wprowadzić lub poprawić dane pojedynczego rekordu indeksu.
 *
 * <p>Pola dialogu wynikają ze {@link BookType#getFieldSchema() schematu księgi},
 * a wynikiem jest mapa atrybutów rekordu, albo brak wyniku gdy użytkownik zrezygnował.
 * Atrybuty spoza schematu są zachowywane bez zmian.</p>
 */
public class IndexRecordDialog extends Dialog<Map<String, String>> {

    private final Map<String, TextField> fields = new LinkedHashMap<>();
    private final Map<String, String> initialData;
    private final ResourceBundle bundle;

    /**
     * @param type typ księgi, której dotyczy rekord
     * @param initialData atrybuty pokazywane na start, np. dane modyfikowanego rekordu
     * @param editing {@code true} dla modyfikacji istniejącego rekordu, {@code false} dla nowego
     * @param bundle pakiet językowy
     */
    public IndexRecordDialog(BookType type, Map<String, String> initialData, boolean editing, ResourceBundle bundle) {
        Objects.requireNonNull(type, "type must not be null");
        this.bundle = Objects.requireNonNull(bundle, "bundle must not be null");
        this.initialData = initialData == null ? Map.of() : Map.copyOf(initialData);

        setTitle(string(editing ? "FX_DIALOG_RECORD_EDIT_TITLE" : "FX_DIALOG_RECORD_ADD_TITLE"));
        setHeaderText(type.getBookName());
        setResizable(true);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().setContent(createContent(type));
        setResultConverter(button -> button != null && button.getButtonData() == ButtonBar.ButtonData.OK_DONE
                ? collectData()
                : null);
        setOnShown(event -> fields.values().stream().findFirst().ifPresent(TextField::requestFocus));
    }

    private VBox createContent(BookType type) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        int row = 0;
        for (String field : type.getFieldSchema()) {
            TextField input = new TextField(initialData.getOrDefault(field, ""));
            input.setPromptText(fieldLabel(field));
            input.setPrefColumnCount(24);
            fields.put(field, input);
            grid.add(new Label(fieldLabel(field)), 0, row);
            grid.add(input, 1, row);
            row++;
        }

        Label hint = new Label(string("FX_DIALOG_RECORD_HINT"));
        hint.setWrapText(true);
        hint.setMaxWidth(380);

        VBox content = new VBox(15, grid, hint);
        content.setPadding(new Insets(5));
        return content;
    }

    private Map<String, String> collectData() {
        Map<String, String> data = new LinkedHashMap<>(initialData);
        fields.forEach((field, input) -> data.put(field, input.getText() == null ? "" : input.getText().trim()));
        return data;
    }

    private String fieldLabel(String field) {
        return FieldLabels.get(bundle, field);
    }

    private String string(String key) {
        return bundle.getString(key);
    }
}
