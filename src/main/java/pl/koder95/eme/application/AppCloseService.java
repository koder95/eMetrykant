package pl.koder95.eme.application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.RequiredArgsConstructor;
import pl.koder95.eme.bootstrap.AppConfig;
import pl.koder95.eme.fx.FxDialogs;

/**
 * Serwis aplikacyjny odpowiedzialny za scenariusz zamykania aplikacji.
 */
@RequiredArgsConstructor
public class AppCloseService {

    private final AppConfig appConfig;
    private final FxDialogs dialogs;

    public void closeWithConfirmation(Scene scene) {
        closeWithConfirmation(scene, Platform::exit);
    }

    public void closeWithConfirmation(Scene scene, Runnable onConfirm) {
        Alert alert = dialogs.createConfirmationAlert(
                scene,
                appConfig.bundle().getString("ALERT_CONFIRM_CLOSE_TITLE"),
                appConfig.bundle().getString("ALERT_CONFIRM_CLOSE_HEADER"),
                appConfig.bundle().getString("ALERT_CONFIRM_CLOSE_CONTENT")
        );
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            onConfirm.run();
        }
    }
}
