package pl.koder95.eme.fx;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pl.koder95.eme.dfs.IndexList;

import java.util.concurrent.atomic.AtomicLong;

public class ProgressDialog extends Dialog<Boolean> {
    private static final AtomicLong PROGRESS_DIALOG_THREAD_SEQ = new AtomicLong();

    private ProgressDialog() {
        this.setDialogPane(createDialogPane());
        setOnShown(event -> Platform.runLater(createProgressThread()::start));
    }

    private Thread createProgressThread() {
        return new Thread(() -> {
            reloadAllIndexLists();
            Platform.runLater(() -> {
                setResult(true);
                close();
            });
        }, "Progress Dialog Thread #" + PROGRESS_DIALOG_THREAD_SEQ.incrementAndGet());
    }

    private DialogPane createDialogPane() {
        ProgressIndicator indicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        Label label = new Label("Czekaj...");
        label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, 24));
        DialogPane pane = new DialogPane();
        pane.setContent(createMainPane(indicator, label));
        return pane;
    }

    private Parent createMainPane(ProgressIndicator indicator, Label label) {
        VBox main = new VBox(indicator, label);
        main.setMaxWidth(Double.MAX_VALUE);
        main.setSpacing(10);
        main.setFillWidth(true);
        main.setAlignment(Pos.CENTER);
        return main;
    }

    private static void reloadAllIndexLists() {
        for (IndexList value : IndexList.values()) {
            reload(value);
        }
    }

    private static void reload(IndexList value) {
        value.clear();
        value.load();
    }

    public static void start() {
        Platform.runLater(() -> new ProgressDialog().showAndWait());
    }
}
