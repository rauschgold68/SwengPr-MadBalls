package mm.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mm.FxToGameObject;
import mm.GameObjectConverter;
import mm.ObjectImporter;
import mm.PhysicsVisualPair;
import mm.core.physics.ResettableAnimationTimer;
import mm.model.objects.GameObject;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.ArrayList;
import java.util.List;

public class simulation {

    private World world;
    private List<PhysicsVisualPair> pairs;
    private Pane simSpace;
    private ResettableAnimationTimer timer;
    private HBox bottomBar;

    public Scene getScene(Stage primaryStage) {
        // main layout container
        BorderPane mainPane = new BorderPane();
        mainPane.setId("root-pane");

        // simulation area
        simSpace = new Pane();
        simSpace.getStyleClass().add("sim-space");
        mainPane.setCenter(simSpace);

        // sidebar with menu buttons
        VBox sideBar = new VBox();
        sideBar.getStyleClass().add("side-bar");
        sideBar.setPrefWidth(200);

        StackPane inventoryBox = new StackPane();
        inventoryBox.getStyleClass().add("inventory-box");
        VBox.setVgrow(inventoryBox, Priority.ALWAYS);

        HBox squareContainer = new HBox();
        squareContainer.getStyleClass().add("square-container");
        squareContainer.setAlignment(Pos.CENTER);

        StackPane menuSquare = new StackPane();
        menuSquare.getStyleClass().add("menu-square");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("menu-grid");

        // overlay pane for settings (initially hidden)
        StackPane overlaySettings = createQuickMenuOverlay(primaryStage);
        overlaySettings.setVisible(false);

        // populate grid with buttons/icons
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = new Button();
                btn.getStyleClass().add("menu-button");
                FontIcon icon = null;

                if (row == 0 && col == 0) {
                    icon = new FontIcon(FontAwesomeSolid.PLAY);
                    btn.setOnAction(e -> timer.start());

                } else if (row == 0 && col == 1) {
                    icon = new FontIcon(FontAwesomeSolid.STOP);
                    btn.setOnAction(e -> {
                        timer.stop();
                        setupSimulation();
                    });

                } else if (row == 0 && col == 2) {
                    icon = new FontIcon(FontAwesomeSolid.COGS);
                    btn.setOnAction(e -> {
                        overlaySettings.setVisible(true);
                        timer.stop();
                    });

                } else if (row == 1 && col == 2) {
                    icon = new FontIcon(FontAwesomeSolid.SAVE);
                    btn.setOnAction(e -> {
                        timer.stop();
                        setupSimulation();
                        exportLevel();
                    });
                }

                if (icon != null) {
                    icon.setIconSize(16);
                    icon.setIconColor(Color.WHITE);
                    btn.setGraphic(icon);
                }

                grid.add(btn, col, row);
            }
        }

        menuSquare.getChildren().add(grid);
        squareContainer.getChildren().add(menuSquare);
        sideBar.getChildren().addAll(inventoryBox, squareContainer);
        mainPane.setRight(sideBar);

        // bottom bar
        bottomBar = new HBox();
        bottomBar.getStyleClass().add("bottom-bar");
        bottomBar.setPrefHeight(150);
        mainPane.setBottom(bottomBar);

        setupSimulation();

        // root stack to layer overlay on top of mainPane
        StackPane rootStack = new StackPane();
        rootStack.getChildren().addAll(mainPane, overlaySettings);
        rootStack.prefWidthProperty().bind(primaryStage.widthProperty());
        rootStack.prefHeightProperty().bind(primaryStage.heightProperty());

        Scene scene = new Scene(rootStack);
        scene.getStylesheets().add(
                getClass().getResource("/styling/simulation.css").toExternalForm());

        // toggle overlay with ESC
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                overlaySettings.setVisible(!overlaySettings.isVisible());
                event.consume();
            }
        });

        Platform.runLater(() -> {
            rootStack.applyCss();
            rootStack.layout();
        });

        return scene;
    }

    private StackPane createQuickMenuOverlay(Stage ownerStage) {
        // semi-transparent background
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(30, 30, 50, 0.7);");
        overlay.setPickOnBounds(true);

        // settings window container
        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(15));
        window.setMaxWidth(300);
        window.setMaxHeight(180);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(10, 10, 20, 0.9), new CornerRadii(10), Insets.EMPTY)));

        // top row with close button aligned to right
        HBox topRow = new HBox();
        Button btnClose = new Button("✕");
        btnClose.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        btnClose.setOnAction(e -> {
            overlay.setVisible(false);
            timer.start();
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(spacer, btnClose);

        // "Back to Title Screen" button
        Button btnBack = new Button("Back to Title Screen");
        btnBack.getStyleClass().add("menu-button");
        btnBack.setMaxWidth(Double.MAX_VALUE);
        btnBack.setOnAction(e -> {
            overlay.setVisible(false);
            TitleScreen titleScreen = new TitleScreen();
            Scene titleScene = titleScreen.createTitleScene(ownerStage);
            ownerStage.setScene(titleScene);
        });
        btnBack.setPrefHeight(40);

        // "Quit Game" button
        Button btnQuit = new Button("Quit Game");
        btnQuit.getStyleClass().add("menu-button");
        btnQuit.setMaxWidth(Double.MAX_VALUE);
        btnQuit.setOnAction(e -> Platform.exit());
        btnQuit.setPrefHeight(40);

        window.getChildren().addAll(topRow, btnBack, btnQuit);

        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    private void setupSimulation() {
        simSpace.getChildren().removeIf(node -> !(node instanceof Button));

        world = new World(new Vec2(0.0f, 9.8f));
        pairs = new ArrayList<>();

        ObjectImporter importer = new ObjectImporter();
        List<GameObject> gameObjects = importer.getGameObjects();

        for (GameObject obj : gameObjects) {
            PhysicsVisualPair pair = GameObjectConverter.convert(obj, world);
            if (pair.visual != null) {
                simSpace.getChildren().add(pair.visual);
                pairs.add(pair);
            }
        }

        timer = new ResettableAnimationTimer(world, pairs);
    }

    private void exportLevel() {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        for (PhysicsVisualPair pair : pairs) {
            GameObject obj = FxToGameObject.convertBack(pair);
            gameObjects.add(obj);
        }
        System.out.println("export done!");
    }
}
