package pl.koder95.eme.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.koder95.eme.Main;
import pl.koder95.eme.application.IndexManagementService;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Wytwarza i pokazuje okno zarządzania danymi.
 *
 * <p>Fabryka zna zależności {@link DataManagementView kontrolera}, dzięki czemu
 * widoki, które otwierają to okno, nie muszą ich tworzyć.</p>
 *
 * @since 0.5.0
 */
public class DataManagementViewFactory {

    private static final String FXML_RESOURCE = "DataManagementView.fxml";

    private final IndexManagementService indexManagementService;
    private final FxDialogs dialogs;
    private final ResourceBundle bundle;

    public DataManagementViewFactory(IndexManagementService indexManagementService,
                                     FxDialogs dialogs,
                                     ResourceBundle bundle) {
        this.indexManagementService = Objects.requireNonNull(indexManagementService,
                "indexManagementService must not be null");
        this.dialogs = Objects.requireNonNull(dialogs, "dialogs must not be null");
        this.bundle = Objects.requireNonNull(bundle, "bundle must not be null");
    }

    /**
     * Otwiera modalne okno zarządzania danymi i czeka na jego zamknięcie.
     *
     * @param owner okno, dla którego nowe okno jest modalne, może być {@code null}
     * @return {@code true} gdy dane zostały zmienione i widoki wymagają odświeżenia
     * @throws IOException gdy nie udało się wczytać widoku
     */
    public boolean showAndWait(Window owner) throws IOException {
        URL url = DataManagementView.class.getResource(FXML_RESOURCE);
        if (url == null) {
            throw new IOException("Nie znaleziono zasobu: " + FXML_RESOURCE);
        }
        FXMLLoader loader = new FXMLLoader(url, bundle);
        loader.setControllerFactory(type -> {
            if (type == DataManagementView.class) {
                return new DataManagementView(indexManagementService, dialogs, bundle);
            }
            throw new IllegalArgumentException("Nieobsługiwany kontroler FXML: " + type.getName());
        });
        Parent root = loader.load();
        DataManagementView view = loader.getController();

        Stage stage = new Stage();
        stage.getIcons().add(new Image(Main.FAVICON_PATH));
        stage.setTitle(bundle.getString("FX_WINDOW_DATA_MANAGEMENT_TITLE"));
        stage.setScene(new Scene(root));
        stage.setMinWidth(640);
        stage.setMinHeight(400);
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        }
        stage.setOnCloseRequest(event -> {
            event.consume();
            view.requestClose();
        });
        stage.showAndWait();
        return view.isDataChanged();
    }
}
