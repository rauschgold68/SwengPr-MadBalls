package mm.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import mm.model.GameObject;
import mm.model.InventoryObject;
import mm.model.PhysicsVisualPair;
import mm.model.SimulationModel;
import mm.view.SimulationView;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * The {@code SimulationController} class coordinates the interaction between
 * the {@link SimulationModel} and {@link SimulationView}
 * in the MadBalls game, following the MVC (Model-View-Controller) pattern.
 * This controller is responsible for:
 * <ul>
 * <li>Initializing and updating the simulation area and inventory UI based on
 * the model state.</li>
 * <li>Handling all user input and UI events, such as drag-and-drop, keyboard
 * shortcuts, and menu actions.</li>
 * <li>Updating the model in response to user actions and ensuring the view
 * reflects the current simulation state.</li>
 * <li>Managing the wiring of event handlers for UI components, including
 * overlay toggling and inventory interactions.</li>
 * </ul>
 * <b>Note:</b> All business logic and event handling should be managed here.
 * The model contains only simulation state and logic,
 * and the view contains only UI construction and accessors.
 *
 * <h2>Main Responsibilities:</h2>
 * <ul>
 * <li>Setup and refresh the simulation area and inventory UI.</li>
 * <li>Enable drag-and-drop of inventory objects into the simulation area.</li>
 * <li>Wire up menu and overlay actions (e.g., play, stop, save, settings).</li>
 * <li>Toggle overlays and handle keyboard shortcuts.</li>
 * </ul>
 *
 * <h2>Fields:</h2>
 * <ul>
 * <li>{@code model} - The simulation model holding all simulation state and
 * logic.</li>
 * <li>{@code view} - The simulation view containing all JavaFX UI
 * components.</li>
 * <li>{@code inventoryWrappers} - List of StackPane wrappers for inventory
 * visuals, used for drag-and-drop.</li>
 * </ul>
 *
 * @author MadBalls
 */
public class SimulationController {

    private final SimulationModel model;
    private final SimulationView view;
    private final List<StackPane> inventoryWrappers = new ArrayList<>();
    private final Stage primaryStage;
    private final boolean isPuzzleMode;
    private boolean atPuzzlesEnd;

    /**
     * Constructs the SimulationController, sets up the model and view, and wires up
     * event handlers.
     *
     * @param primaryStage the primary stage of the application
     * @param levelPath    the resource path to the level JSON file
     */
    public SimulationController(Stage primaryStage, String levelPath, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        this.primaryStage = primaryStage;
        this.model = new SimulationModel(levelPath);
        this.view = new SimulationView(primaryStage, isPuzzleMode, atPuzzlesEnd);
        this.isPuzzleMode = isPuzzleMode;

        // WIN-LISTENER setzen
        this.model.setWinListener(() -> {
            Platform.runLater(() -> view.getWinScreenOverlay().setVisible(true));
        });

        setupSimulation();
        setupInventory();
        setupDragAndDrop();
        setupMenuButtons();
        setupOverlayToggle();
        setupWinNextLevel();
    }

    /**
     * Returns the simulation scene to be set on the primary stage.
     * 
     * @return the JavaFX Scene for the simulation
     */
    public Scene getScene() {
        return view.getScene();
    }

    /**
     * Initializes the simulation area by loading objects and setting up the physics
     * world.
     * <p>
     * Clears the simulation area, loads all objects from the model, and adds their
     * visuals to the view.
     * </p>
     */
    private void setupSimulation() {
        Pane simSpace = view.getSimSpace();
        simSpace.getChildren().clear();

        model.setupSimulation();

        // Add visuals to simSpace and restore angles for dropped objects
        List<GameObject> dropped = model.getDroppedObjects();
        List<PhysicsVisualPair> pairs = model.getPairs();
        for (PhysicsVisualPair pair : pairs) {
            if (pair.visual != null) {
                for (GameObject obj : dropped) {
                    if (obj.getName().equals(pair.body.getUserData())) {
                        pair.visual.setRotate(obj.getAngle());
                        addMoveHandlersToDroppedVisual(pair, obj);
                        break;
                    }
                }
                simSpace.getChildren().add(pair.visual);
            }
        }
    }

    /**
     * Initializes the inventory area by loading inventory objects and setting up
     * drag-and-drop.
     * <p>
     * Clears the inventory UI, loads inventory objects from the model, and creates
     * drag sources for each.
     * </p>
     */
    private void setupInventory() {
        VBox inventoryItemBox = view.getInventoryItemBox();
        inventoryItemBox.getChildren().clear();
        inventoryWrappers.clear();

        model.setupInvetoryData();

        for (InventoryObject obj : model.getInventoryObjects()) {
            PhysicsVisualPair pair = mm.controller.InventoryObjectController.convert(obj, model.getWorld());
            if (pair.visual != null) {
                pair.visual.setRotate(obj.getAngle());                

                // Dynamically adjust wrapper size based on rotated dimensions
                double rotatedWidth = pair.visual.getBoundsInParent().getWidth();
                double rotatedHeight = pair.visual.getBoundsInParent().getHeight();

                StackPane wrapper = new StackPane(pair.visual);
                wrapper.setPrefSize(rotatedWidth + 20, rotatedHeight + 20); // Add padding to prevent overlap
                inventoryWrappers.add(wrapper);

                wrapper.setOnDragDetected(event -> {
                    PhysicsAnimationController timer = model.getTimer();
                    if (timer != null && timer.isRunning()) {
                        event.consume();
                        return;
                    }
                    Dragboard db = wrapper.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(obj.getName());
                    db.setContent(content);

                    // Create a snapshot with transparent background
                    javafx.scene.SnapshotParameters snapshotParameters = new javafx.scene.SnapshotParameters();
                    snapshotParameters.setFill(javafx.scene.paint.Color.TRANSPARENT); // Set transparent background
                    javafx.scene.image.WritableImage snapshot = pair.visual.snapshot(snapshotParameters, null);

                    db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);

                    event.consume();
                });

                inventoryItemBox.getChildren().add(wrapper);
            }
        }

        // Add spacing between items in the inventory
        inventoryItemBox.setSpacing(15); // Adjust spacing as needed
    }

    /**
     * Sets the visual state of all inventory item wrappers to indicate whether they
     * are enabled or disabled.
     * 
     * @param disabled {@code true} to visually disable inventory items,
     *                 {@code false} to enable them
     */
    private void setInventoryItemsDisabled(boolean disabled) {
        for (StackPane wrapper : inventoryWrappers) {
            if (disabled) {
                if (!wrapper.getStyleClass().contains("inventory-item-disabled")) {
                    wrapper.getStyleClass().add("inventory-item-disabled");
                }
            } else {
                wrapper.getStyleClass().remove("inventory-item-disabled");
            }
        }
    }

    /**
     * Sets up drag-and-drop functionality for placing inventory objects into the
     * simulation area.
     * <p>
     * Handles drag-over and drag-dropped events on the simulation area, checks
     * placement restrictions,
     * and updates the model and view with new objects as needed.
     * </p>
     */
    private void setupDragAndDrop() {
        Pane simSpace = view.getSimSpace();

        simSpace.setOnDragOver(event -> {
            PhysicsAnimationController timer = model.getTimer();
            if ((timer == null || !timer.isRunning()) && event.getGestureSource() != simSpace
                    && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        simSpace.setOnDragDropped(event -> {
            PhysicsAnimationController timer = model.getTimer();
            if (timer != null && timer.isRunning()) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            double x = event.getX();
            double y = event.getY();

            if (model.isInNoPlaceZone(x, y)) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String name = db.getString();
                InventoryObject template = model.findInventoryObjectByName(name);

                if (template != null) {
                    GameObject simObj = model.createGameObjectFromInventory(template, (float) x, (float) y);
                    model.addDroppedObject(simObj);

                    PhysicsVisualPair pair = mm.controller.GameObjectController.convert(simObj, model.getWorld());
                    if (pair.visual != null) {
                        pair.visual.setRotate(simObj.getAngle());
                        simSpace.getChildren().add(pair.visual);
                        model.getPairs().add(pair);
                        model.getDroppedPhysicsVisualPairs().add(pair);

                        addMoveHandlersToDroppedVisual(pair, simObj);
                    }
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Sets up menu button actions (play, stop, settings, delete, import, save).
     * <p>
     * The actual buttons should be retrieved from the view and wired here.
     * This is a placeholder for actual button wiring, which depends on your view
     * implementation.
     * </p>
     */
    private void setupMenuButtons() {
        // Start simulation.
        if (view.playButton != null) {
            view.playButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    timer.start();
                    setInventoryItemsDisabled(true);
                }
            });
        }
        // Stop and reset simulation.
        if (view.stopButton != null) {
            view.stopButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                    timer.reset();
                    setInventoryItemsDisabled(false);
                }
                setupSimulation();
            });
        }
        // Open the settings menu.
        if (view.settingsButton != null) {
            view.settingsButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                view.getOverlaySettings().setVisible(true);
            });
        }
        // Delete all added objects to the simulation environment.
        if (view.deleteButton != null) {
            view.deleteButton.setOnAction(e -> {
                model.setDroppedObjects(new ArrayList<>());
                model.setDroppedVisualPairs(new ArrayList<>());
                setInventoryItemsDisabled(false);
                setupSimulation();
            });
        }

        // Import level from .json - File (to implment)
        if (view.importButton != null) {
            view.importButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Import your level!");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    model.setLevelPath("/level/" + file.getName());
                }
                setupSimulation();
                setupInventory();
            });
        }
        if (view.saveButton != null) {
            view.saveButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    model.exportLevel();
                }
            });
        }
        if (view.crownButton != null) {
            view.crownButton.setOnAction(e -> {
                view.getWinScreenOverlay().setVisible(true);
            });
        }
    }

    /**
     * Sets up the ESC key to toggle the overlay menu.
     * <p>
     * Adds a key event handler to the scene to show or hide the overlay settings
     * menu when ESC is pressed.
     * </p>
     */
    private void setupOverlayToggle() {
        Scene scene = view.getScene();
        StackPane overlaySettings = view.getOverlaySettings();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE && !model.isWinScreenVisible()) {
                overlaySettings.setVisible(!overlaySettings.isVisible());
                event.consume();
            }

        });

        view.overlayCloseButton.setOnAction(e -> {
            view.getOverlaySettings().setVisible(false);
        });

        view.overlayBackButton.setOnAction(e -> {
            // Hide the overlay before switching scenes to avoid overlay showing on title
            // screen
            view.getOverlaySettings().setVisible(false);
            TitleScreenController titleScreenView = new TitleScreenController(primaryStage);
            Scene newScreen = titleScreenView.getScene();
            primaryStage.setScene(newScreen);
            primaryStage.setWidth(scene.getWidth());
            primaryStage.setHeight(scene.getHeight());

        });

        view.overlayQuitButton.setOnAction(e -> {
            Platform.exit();
        });

        // win screen overlay functions
        view.btnWinHome.setOnAction(e -> {
            view.getWinScreenOverlay().setVisible(false);
            TitleScreenController titleScreenView = new TitleScreenController(primaryStage);
            Scene newScreen = titleScreenView.getScene();
            primaryStage.setScene(newScreen);
            primaryStage.setWidth(scene.getWidth());
            primaryStage.setHeight(scene.getHeight());
        });

        if (view.btnWinExport != null) {
            view.btnWinExport.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    model.exportLevel();
                }
            });
        }
    }

    /**
     * Extracts the level number from the level path.
     * 
     * @param levelPath the resource path to the level JSON file
     * @return the extracted level number, or -1 if not found
     */
    private int extractLevelNumber(String levelPath) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("level(\\d+)\\.json").matcher(levelPath);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1; // oder eine andere Fehlerbehandlung
    }

    /**
     * Sets up the next level button on the win screen.
     * <p>
     * This method determines the next level based on the current level number and
     * wires up the button to start the next level.
     * </p>
     */
    private void setupWinNextLevel() {
        int currentLevel = extractLevelNumber(model.getLevelPath());
        String nextLevel = "1";
        ;
        switch (currentLevel) {
            case 1:
                nextLevel = "2";
                atPuzzlesEnd = false;
                break;

            case 2:
                nextLevel = "3";
                atPuzzlesEnd = true;
                break;
            default:
                break;
        }
        String nextLevelPath = "/level/level" + nextLevel + ".json";
        if (view.btnWinNext != null) {
            view.btnWinNext.setOnAction(e -> {
                SimulationController simController = new SimulationController(primaryStage, nextLevelPath,
                        true, atPuzzlesEnd);
                Scene simScene = simController.getScene();
                primaryStage.setScene(simScene);
                primaryStage.sizeToScene();
            });
        }
    }

    /**
     * Adds mouse event handlers to a dropped object's visual node to enable moving it within the simulation area.
     * <ul>
     *   <li>Only objects that originated from the inventory (i.e., dropped objects) should use this handler.</li>
     *   <li>The method updates the {@link GameObject}'s position and the {@link PhysicsVisualPair}'s Box2D body.</li>
     *   <li>Visual feedback includes cursor changes and can be extended for more effects (e.g., opacity, shadows).</li>
     *   <li>Optionally, checks for placement restrictions (such as no-place zones) can be added.</li>
     * </ul>
     *
     * @param pair   the {@link PhysicsVisualPair} containing the visual node and Box2D body to be moved
     * @param simObj the {@link GameObject} model instance associated with the visual
     */
    private void addMoveHandlersToDroppedVisual(PhysicsVisualPair pair, GameObject simObj) {
        javafx.scene.Node visual = pair.visual;
        final double[] dragDelta = new double[2];

        visual.setOnMouseEntered(event -> visual.setCursor(javafx.scene.Cursor.CLOSED_HAND));
        visual.setOnMouseExited(event -> visual.setCursor(javafx.scene.Cursor.DEFAULT));

        visual.setOnMousePressed(event -> {
            dragDelta[0] = event.getSceneX() - visual.getTranslateX();
            dragDelta[1] = event.getSceneY() - visual.getTranslateY();
            event.consume();
        });

        visual.setOnMouseDragged(event -> {
            double newX = event.getSceneX() - dragDelta[0];
            double newY = event.getSceneY() - dragDelta[1];

            // Optionally: Check for no-place zones here if needed

            visual.setTranslateX(newX);
            visual.setTranslateY(newY);

            // Update the GameObject's position (for export, etc.)
            simObj.getPosition().setX((float) newX);
            simObj.getPosition().setY((float) newY);

            // JBoxd change the point of animation
            pair.body.setTransform(
                new org.jbox2d.common.Vec2((float) (newX / 50.0), (float) (newY / 50.0)), 
                pair.body.getAngle()
            );

            event.consume();
        });

        visual.setOnScroll(event -> {
            float currentAngle = simObj.getAngle();
            float newAngle = currentAngle + 15;

            pair.visual.setRotate(newAngle);
            simObj.setAngle(newAngle);

        });
    }
}