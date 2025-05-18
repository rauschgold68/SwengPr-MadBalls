package mm.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class SanityTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Kreis erstellen
        Circle circle = new Circle(75, Color.RED);

        // Button erstellen
        Button btn = new Button("Klick mich!");
        btn.setOnAction(e -> {
            btn.setText("Geklickt!");
            circle.setFill(Color.BLUE);
        });

        // Layout
        StackPane root = new StackPane();
        root.getChildren().addAll(circle, btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Sanity Check: JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}