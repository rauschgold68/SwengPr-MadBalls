package mm.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mm.GameObjectConverter;
import mm.ObjectImporter;
import mm.PhysicsVisualPair;
import mm.core.physics.ResettableAnimationTimer;
import mm.model.objects.GameObject;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class simulation extends Application {

    private World world;
    private List<PhysicsVisualPair> pairs;
    private Pane root;
    private ResettableAnimationTimer timer;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        Button btnStart = new Button("Start Simulation");
        btnStart.setTranslateX(10);
        btnStart.setTranslateY(10);

        Button btnReset = new Button("Reset Simulation");
        btnReset.setTranslateX(120);
        btnReset.setTranslateY(10);

        root.getChildren().addAll(btnStart, btnReset);

        setupSimulation();

        btnStart.setOnAction(e -> timer.start());
        btnReset.setOnAction(e -> {
            timer.stop();
            setupSimulation();
        });

        primaryStage.setTitle("JavaFX + JBox2D Physics Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupSimulation() {
        // Remove all visuals except buttons
        root.getChildren().removeIf(node -> !(node instanceof Button));

        // Create new world and pairs
        world = new World(new Vec2(0.0f, 9.8f));
        pairs = new ArrayList<>();

        ObjectImporter importer = new ObjectImporter();
        List<GameObject> gameObjects = importer.getGameObjects();

        for (GameObject obj : gameObjects) {
            PhysicsVisualPair pair = GameObjectConverter.convert(obj, world);
            if (pair.visual != null) {
                root.getChildren().add(pair.visual);
                pairs.add(pair);
            }
        }

        // Create a new timer with the new world and pairs
        timer = new ResettableAnimationTimer(world, pairs);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
