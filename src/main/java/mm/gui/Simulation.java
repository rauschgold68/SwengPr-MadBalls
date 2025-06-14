package mm.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import mm.GameObjectConverter;
import mm.InventoryObjectConverter;
import mm.LevelImporter;
import mm.PhysicsVisualPair;
import mm.core.physics.ResettableAnimationTimer;
import mm.model.objects.GameObject;
import mm.model.objects.InventoryObject;
import mm.model.objects.LevelExport;
import mm.model.objects.Position;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.ArrayList;
import java.util.List;

/**
 * Main simulation GUI class for the MadBalls game.
 * <p>
 * Handles the simulation area, inventory, sidebar, and menu overlays.
 * Responsible for setting up the simulation, inventory, and exporting levels.
 * Provides drag-and-drop functionality for inventory objects, manages simulation state,
 * and handles user interactions such as pausing, resuming, and exporting levels.
 * </p>
 * <ul>
 *   <li>Supports level selection and loading via {@code levelPath}.</li>
 *   <li>Manages the physics world and synchronizes JavaFX visuals with Box2D bodies.</li>
 *   <li>Handles inventory drag-and-drop, placement restrictions, and visual feedback.</li>
 *   <li>Provides quick menu overlay for settings, returning to title, and quitting.</li>
 *   <li>Exports the current simulation state to a JSON level file.</li>
 * </ul>
 * 
 * Example usage:
 * <pre>
 *     Simulation sim = new Simulation("/level/level1.json");
 *     Scene scene = sim.getScene(primaryStage);
 *     primaryStage.setScene(scene);
 * </pre>
 */
public class Simulation {
    /** Level path for different levels. */
    private String levelPath = "/level/level1.json"; // default
    /** The physics world for the simulation. */
    private World world;
    /** List of pairs of physics objects and their visuals. */
    private List<PhysicsVisualPair> pairs;
    /** The pane where simulation objects are displayed. */
    private Pane simSpace;
    /** Animation timer for the simulation. */
    private ResettableAnimationTimer timer;
    /** The bottom bar of the UI. */
    private HBox bottomBar;
    /** The inventory box container. */
    private StackPane inventoryBox;
    /** The VBox containing inventory items. */
    private VBox inventoryItemBox;
    /** The storage for dropped objects while playing. */
    private final List<GameObject> droppedObjects = new ArrayList<>();
    /** The storage for dropped items while playing as InventoryObjects. */
    private List<InventoryObject> inventoryObjects = new ArrayList<>();
    /** The inventory objects to be manipulated. */
    private final List<StackPane> inventroyWrappers = new ArrayList<>();
    /** The noPlaceZones existing inside the simulation. */
    private final List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();

    /**
     * Default constructor.
     * Initializes the simulation with the default level path.
     */
    public Simulation() {}

    /**
     * Constructor for specific level selection.
     * 
     * @param levelPath the resource path to the level JSON file (e.g., "/level/level1.json")
     */
    public Simulation(String levelPath) {
        this.levelPath = levelPath;
    }

    /**
     * Creates and returns the main simulation scene.
     * <p>
     * Sets up the main layout, simulation area, inventory, sidebar, and menu overlays.
     * Handles drag-and-drop for inventory objects and manages simulation state.
     * </p>
     *
     * @param primaryStage the primary stage of the application
     * @return the constructed Scene for the simulation
     */
    public Scene getScene(Stage primaryStage) {
        // main layout container
        BorderPane mainPane = new BorderPane();
        mainPane.setId("root-pane");

        // simulation area
        simSpace = new Pane();
        simSpace.getStyleClass().add("sim-space");
        mainPane.setCenter(simSpace);

        //Drag inventory objects and place them
        // Only allow drag over if simulation is paused (not running)

        simSpace.setOnDragOver(event -> {
            if ((timer == null || !timer.isRunning()) && event.getGestureSource() != simSpace && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        // Only allow drop if simulation is paused (not running)
        simSpace.setOnDragDropped(event -> {
            if (timer != null && timer.isRunning()) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            double x = event.getX();
            double y = event.getY();

            for (PhysicsVisualPair zone : noPlaceZones) {
                if (zone.visual instanceof Rectangle) {
                    Rectangle rect = (Rectangle) zone.visual;
                    double zoneX = rect.getTranslateX();
                    double zoneY = rect.getTranslateY();
                    double zoneW = rect.getWidth();
                    double zoneH = rect.getHeight();
                    if (x >= zoneX && x <= zoneX + zoneW && y >= zoneY && y <= zoneY + zoneH) {
                        event.setDropCompleted(false);
                        event.consume();
                        return;
                    }
                }
            }

            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String name = db.getString(); // Use name instead of type
                LevelImporter importer = new LevelImporter(levelPath);

                this.inventoryObjects = importer.getInventoryObjects();
                InventoryObject template = this.inventoryObjects.stream()
                    .filter(obj -> obj.getName().equals(name)) // Match by name
                    .findFirst().orElse(null);

                if (template != null) {
                    InventoryObject newObj = new InventoryObject(

                        template.getName(), template.getType(), template.getCount(),
                        template.getAngle(), template.getSize(), template.getColour(), 
                        template.getPhysics(), template.getRadius()
                    );

                    // Center the object around the mouse
                    float offsetX = (float) (newObj.getSize().getWidth() / 2.0);
                    float offsetY = (float) (newObj.getSize().getHeight() / 2.0);

                    GameObject simObj = new GameObject(
                        newObj.getName(), newObj.getType(),
                        new Position((float) x - offsetX, (float) y - offsetY), 
                        newObj.getAngle(),
                        newObj.getSize(), 
                        newObj.getColour(), 
                        newObj.getPhysics()
                    );


                    // Add the objects that are dropped to be displayed again
                    droppedObjects.add(simObj);
                    PhysicsVisualPair pair = GameObjectConverter.convert(simObj, world);
                    if (pair.visual != null) {
                        simSpace.getChildren().add(pair.visual);
                        pairs.add(pair);
                    }
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // sidebar with menu buttons
        VBox sideBar = new VBox();
        sideBar.getStyleClass().add("side-bar");
        sideBar.setPrefWidth(200);

        // Part of sidebar where items are displayed
        inventoryBox = new StackPane();
        inventoryBox.getStyleClass().add("inventory-box");
        VBox.setVgrow(inventoryBox, Priority.ALWAYS);
        inventoryItemBox = new VBox();
        inventoryBox.getChildren().add(inventoryItemBox);
        inventoryItemBox.getStyleClass().add("inventoryItemBox");

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
                    btn.setOnAction(e -> {
                        timer.start(); 
                        updateInventoryVisuals();
                    });


                } else if (row == 0 && col == 1) {
                    icon = new FontIcon(FontAwesomeSolid.STOP); // Stop
                    btn.setOnAction(e -> {
                        timer.stop();
                        setupSimulation();
                        updateInventoryVisuals();
                    });

                } else if (row == 0 && col == 2) {
                    icon = new FontIcon(FontAwesomeSolid.COGS); // Quick settings
                    btn.setOnAction(e -> {
                        overlaySettings.setVisible(true);
                        timer.stop();
                    });

                } else if (row == 1 && col == 0) {
                    icon = new FontIcon(FontAwesomeSolid.TRASH_ALT); // Delete all objects
                    btn.setOnAction(e -> {
                        System.out.println("delete all objects");
                        timer.stop();
                    });

                } else if (row == 1 && col == 1) {
                    icon = new FontIcon(FontAwesomeSolid.FOLDER_PLUS); // Import level
                    btn.setOnAction(e -> {
                        System.out.println("import level");
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
        setupInventory();
        

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

    /**
     * Creates the quick menu overlay for settings, back to title, and quit.
     * <p>
     * The overlay is a semi-transparent pane with options to return to the title screen or quit the game.
     * </p>
     *
     * @param ownerStage the owner stage for the overlay
     * @return the StackPane overlay containing the quick menu
     */
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
            ownerStage.setWidth(1920);
            ownerStage.setHeight(1080);
        });
        btnBack.setPrefHeight(40);

        // "Quit Game" button
        Button btnQuit = new Button("Quit Game");
        btnQuit.getStyleClass().add("menu-button");
        btnQuit.setMaxWidth(Double.MAX_VALUE);
        btnQuit.setPrefHeight(40);
        btnQuit.setOnAction(e -> Platform.exit());

        window.getChildren().addAll(topRow, btnBack, btnQuit);

        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Sets up the simulation area by loading GameObjects and initializing the
     * physics world.
     * <p>
     * Adds visual representations of objects to the simulation pane and sets up the physics world.
     * Also adds any dropped objects and initializes the animation timer and contact listener.
     * </p>
     */
    private void setupSimulation() {
        simSpace.getChildren().removeIf(node -> !(node instanceof Button));
        world = new World(new Vec2(0.0f, 9.8f));
        pairs = new ArrayList<>();

        LevelImporter importer = new LevelImporter(levelPath); // use selected path
        List<GameObject> gameObjects = importer.getGameObjects();

        // Add level objects
        for (GameObject obj : gameObjects) {
            PhysicsVisualPair pair = GameObjectConverter.convert(obj, world);
            if (pair.visual != null) {
                simSpace.getChildren().add(pair.visual);
                pairs.add(pair);
            }
            if (obj.getName().equals("noPlaceZone")){
                noPlaceZones.add(pair);
            }
        }

        // Add dropped objects
        for (GameObject obj : droppedObjects) {
            PhysicsVisualPair pair = GameObjectConverter.convert(obj, world);
            if (pair.visual != null) {
                simSpace.getChildren().add(pair.visual);
                pairs.add(pair);
            }
            if (obj.getName().equals("noPlaceZone")){
                noPlaceZones.add(pair);
            }
        }

        timer = new ResettableAnimationTimer(world, pairs);

        // Set contact listener for every possible world.
        listenContact();
    }

    /**
     * Sets up the inventory area by loading InventoryObjects and initializing the
     * inventory pane.
     * <p>
     * Adds visual representations of inventory items to the inventory pane and enables drag-and-drop.
     * </p>
     */
    private void setupInventory() {

        LevelImporter importer = new LevelImporter(levelPath); // use selected path
        List<InventoryObject> inventoryObjects = importer.getInventoryObjects();

        for (InventoryObject obj : inventoryObjects) {
            PhysicsVisualPair pair = InventoryObjectConverter.convert(obj, world);
            if (pair.visual != null) {

                StackPane wrapper = new StackPane(pair.visual);
                wrapper.setPrefSize(60, 60);
                inventroyWrappers.add(wrapper);

                // In setupInventory, prevent drag start if simulation is running

                wrapper.setOnDragDetected(event -> {
                    if (timer != null && timer.isRunning()) {
                        event.consume();
                        return;
                    }
                    Dragboard db = wrapper.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(obj.getName()); // Use unique name
                    db.setContent(content);

                    // Create a snapshot of the visual for drag view
                    javafx.scene.image.WritableImage snapshot = pair.visual.snapshot(null, null);
                    db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2); // Centered

                    event.consume();
                });

                inventoryItemBox.getChildren().add(wrapper);
            }
        }

    }

    /**
     * Exports the current level by converting all PhysicsVisualPairs to
     * GameObjects and writing them to a JSON file.
     * <p>
     * Uses {@link LevelExport} to serialize the current simulation state.
     * Prints a confirmation message to the console.
     * </p>
     */
    private void exportLevel() {
        LevelExport LE = new LevelExport();
        LE.export(this.pairs, this.inventoryObjects);
    }
    /**
     * Sets up a contact listener for the physics world to detect collisions.
     * <p>
     * Used for implementing win conditions and other collision-based logic.
     * </p>
     */
    private void listenContact() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();

                // Add more if - statements for wins with diffrent collisions
                if ((a != null && b != null)){
                    if ((a.equals("winPlat") && b.equals("ball1")) ||
                        (a.equals("ball1") && b.equals("winPlat"))) {
                        System.out.println("WIN! ball1 hit the winPlat!");
                    }else if ((a.equals("winZone") && b.equals("ball1")) || 
                        (a.equals("ball1") && b.equals(("winZone")))) {
                        System.out.println("WIN ball1 is in the winZone!");
                    }
                }
            }
            @Override
            public void endContact(Contact contact) {}
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }
    
    /**
     * Updates the inventory objects while simulating, showing that they are not placeable.
     * <p>
     * Disables inventory visuals when the simulation is running.
     * </p>
     */
    private void updateInventoryVisuals() {
        boolean disabled = timer != null && timer.isRunning();
        for (StackPane wrapper : inventroyWrappers) {
            if (disabled) {
                if (!wrapper.getStyleClass().contains("inventory-item-disabled")) {
                    wrapper.getStyleClass().add("inventory-item-disabled");
                }
            } else {
                wrapper.getStyleClass().remove("inventory-item-disabled");
            }
        }
    }
}
