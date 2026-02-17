package pl.koder95.eme.fx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Fabryka dialogów JavaFX używanych przez kontrolery i serwisy UI.
 */
public class FxDialogs {

    public Alert createConfirmationAlert(Scene ownerScene, String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (ownerScene != null) {
            alert.initOwner(ownerScene.getWindow());
        }
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    public Alert createErrorAlert(Scene ownerScene, String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (ownerScene != null) {
            alert.initOwner(ownerScene.getWindow());
        }
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    public Dialog<Boolean> createProgressDialog(Scene ownerScene, String messageText) {
        Dialog<Boolean> dialog = new Dialog<>();
        ProgressIndicator indicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        Label label = new Label(messageText);
        label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, 24));

        VBox root = new VBox(indicator, label);
        root.setMaxWidth(Double.MAX_VALUE);
        root.setSpacing(10);
        root.setFillWidth(true);
        root.setAlignment(Pos.CENTER);

        DialogPane pane = new DialogPane();
        pane.setContent(root);
        dialog.setDialogPane(pane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setOnCloseRequest(event -> dialog.close());

        if (ownerScene != null) {
            dialog.initOwner(ownerScene.getWindow());
        }
        return dialog;
    }
}
