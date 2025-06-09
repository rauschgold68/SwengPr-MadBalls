package mm.gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneUtil {

    public static void switchScene(Stage stage, Scene scene) {
        stage.setScene(scene);

        Platform.runLater(() -> {
            stage.getScene().getRoot().requestLayout();
        });
    }
}
