package mm.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import mm.model.Position;
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
    private String selectedSkin = "Default";

    private final SimulationModel model;
    private final SimulationView view;
    private final List<StackPane> inventoryWrappers = new ArrayList<>();
    private final Stage primaryStage;
    private boolean atPuzzlesEnd;
    // Map to track correspondence between GameObjects and their PhysicsVisualPairs
    private final java.util.Map<GameObject, PhysicsVisualPair> gameObjectToPairMap = new java.util.HashMap<>();

    // Drag start position and angle for undo functionality
    private Position dragStartPosition;
    private float dragStartAngle;

    /**
     * Constructs the SimulationController, sets up the model and view, and wires up
     * event handlers.
     *
     * @param primaryStage the primary stage of the application
     * @param levelPath    the resource path to the level JSON file
     */

    /**
     * Constructs the SimulationController, sets up the model and view, and wires up
     * event handlers. Accepts a skin selection for inventory sprites.
     *
     * @param primaryStage the primary stage of the application
     * @param levelPath    the resource path to the level JSON file
     * @param isPuzzleMode whether puzzle mode is enabled
     * @param atPuzzlesEnd whether at the end of puzzles
     * @param selectedSkin the selected skin ("Default" or "Legacy")
     */
    public SimulationController(Stage primaryStage, String levelPath, boolean isPuzzleMode, boolean atPuzzlesEnd,
            String selectedSkin) {
        this.primaryStage = primaryStage;
        this.model = new SimulationModel(levelPath);
        this.view = new SimulationView(primaryStage, isPuzzleMode, atPuzzlesEnd);
        this.selectedSkin = selectedSkin != null ? selectedSkin : "Default";

        // Set win listener
        this.model.setWinListener(() -> {
            Platform.runLater(() -> view.getWinScreenOverlay().setVisible(true));
        });

        setupSimulation();
        setupInventory(true); // Load data from file on initial setup
        updateInventorySpritesForSkin(); // Update sprites AFTER inventory is loaded
        refreshInventoryDisplay(); // Refresh the display with updated sprite paths
        setupDragAndDrop();
        setupMenuButtons();
        setupOverlayToggle();
        setupWinNextLevel();
    }

    /**
     * Updates all inventory object sprite paths to use the selected skin folder.
     */
    private void updateInventorySpritesForSkin() {
        SkinManager.getInstance().updateInventorySpritesForSkin(model.getInventoryObjects());
    }

    /**
     * Updates the skin choice and refreshes the inventory display.
     * This method should be called when returning from the title screen.
     */
    public void updateSkinChoice() {
        String currentSkin = SkinManager.getInstance().getSelectedSkin();

        if (!currentSkin.equals(this.selectedSkin)) {
            this.selectedSkin = currentSkin;
            updateInventorySpritesForSkin();
            refreshInventoryDisplay();
        }
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
        // Clear the mapping and rebuild it during setup
        gameObjectToPairMap.clear();

        // Process all physics-visual pairs
        List<GameObject> dropped = model.getDroppedObjects();
        List<PhysicsVisualPair> pairs = model.getPairs();

        for (PhysicsVisualPair pair : pairs) {
            if (pair.visual != null) {
                processPhysicsVisualPair(pair, dropped, simSpace);
            }
        }
    }

    /**
     * Processes a single physics-visual pair, matching it with dropped objects
     * and adding it to the simulation space.
     */
    private void processPhysicsVisualPair(PhysicsVisualPair pair, List<GameObject> dropped, Pane simSpace) {
        GameObject matchedDroppedObject = findMatchingDroppedObject(pair, dropped);

        if (matchedDroppedObject != null) {
            configureMatchedObject(pair, matchedDroppedObject);
        }

        simSpace.getChildren().add(pair.visual);
    }

    /**
     * Finds a dropped object that matches the given physics-visual pair.
     */
    private GameObject findMatchingDroppedObject(PhysicsVisualPair pair, List<GameObject> dropped) {
        for (GameObject obj : dropped) {
            if (isMatchingObject(pair, obj)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Checks if a GameObject matches a PhysicsVisualPair based on name and
     * position.
     */
    private boolean isMatchingObject(PhysicsVisualPair pair, GameObject obj) {
        Object userData = pair.body.getUserData();
        boolean nameMatches = obj.getName().equals(userData)
                // Also match if this is the winning object
                || (obj.isWinning() && "winObject".equals(userData));
        if (!nameMatches) {
            return false;
        }
        ExpectedPosition expectedPos = calculateExpectedPosition(pair);
        return isPositionMatch(obj, expectedPos);
    }

    /**
     * Helper class to hold expected position coordinates.
     */
    private static class ExpectedPosition {
        final float x;
        final float y;

        ExpectedPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Calculates the expected visual position from the physics body position.
     */
    private ExpectedPosition calculateExpectedPosition(PhysicsVisualPair pair) {
        org.jbox2d.common.Vec2 bodyPos = pair.body.getPosition();

        if (pair.visual instanceof javafx.scene.shape.Rectangle) {
            return calculateRectanglePosition(pair, bodyPos);
        } else if (pair.visual instanceof javafx.scene.shape.Polygon) {
            return calculatePolygonPosition(pair, bodyPos);
        } else {
            // Default case for circles and other shapes
            return new ExpectedPosition(bodyPos.x * 50.0f, bodyPos.y * 50.0f);
        }
    }

    /**
     * Calculates expected position for rectangle shapes.
     */
    private ExpectedPosition calculateRectanglePosition(PhysicsVisualPair pair, org.jbox2d.common.Vec2 bodyPos) {
        javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) pair.visual;
        float expectedX = (float) (bodyPos.x * 50.0f - rect.getWidth() / 2);
        float expectedY = (float) (bodyPos.y * 50.0f - rect.getHeight() / 2);
        return new ExpectedPosition(expectedX, expectedY);
    }

    /**
     * Calculates expected position for polygon shapes (buckets).
     */
    private ExpectedPosition calculatePolygonPosition(PhysicsVisualPair pair, org.jbox2d.common.Vec2 bodyPos) {
        javafx.scene.shape.Polygon polygon = (javafx.scene.shape.Polygon) pair.visual;
        javafx.geometry.Bounds bounds = polygon.getBoundsInLocal();
        float expectedX = (float) (bodyPos.x * 50.0f - bounds.getWidth() / 2);
        float expectedY = (float) (bodyPos.y * 50.0f - bounds.getHeight() / 2);
        return new ExpectedPosition(expectedX, expectedY);
    }

    /**
     * Checks if a GameObject's position matches the expected position within
     * tolerance.
     */
    private boolean isPositionMatch(GameObject obj, ExpectedPosition expected) {
        float tolerance = 1.0f; // Small tolerance for floating point precision
        return Math.abs(obj.getPosition().getX() - expected.x) < tolerance &&
                Math.abs(obj.getPosition().getY() - expected.y) < tolerance;
    }

    /**
     * Configures a matched object by setting its rotation and adding handlers.
     */
    private void configureMatchedObject(PhysicsVisualPair pair, GameObject matchedDroppedObject) {
        pair.visual.setRotate(matchedDroppedObject.getAngle());
        addMoveHandlersToDroppedVisual(pair, matchedDroppedObject);
        gameObjectToPairMap.put(matchedDroppedObject, pair);
    }

    /**
     * Initializes or refreshes the inventory area.
     * <p>
     * Clears the inventory UI, optionally reloads inventory data from file,
     * and creates drag sources for each item.
     * </p>
     * 
     * @param reloadData if true, reloads inventory data from JSON file;
     *                   if false, uses existing data with current counts
     */
    private void setupInventory(boolean reloadData) {
        VBox inventoryItemBox = view.getInventoryItemBox();
        inventoryItemBox.getChildren().clear();
        inventoryWrappers.clear();

        // Only reload data from file if explicitly requested
        if (reloadData) {
            model.setupInvetoryData();
        }

        // Delegate inventory item creation to the view
        for (InventoryObject obj : model.getInventoryObjects()) {
            StackPane itemWrapper = createInventoryItemWrapper(obj);
            if (itemWrapper != null) {
                inventoryWrappers.add(itemWrapper);
                setupInventoryItemHandlers(itemWrapper, obj);
                inventoryItemBox.getChildren().add(itemWrapper);
            }
        }

        // Configure inventory layout
        inventoryItemBox.setSpacing(15);
    }

    /**
     * Creates a wrapper for an inventory item with visual styling.
     * <p>
     * This is a temporary helper method that should eventually be moved to the view
     * layer
     * for better MVC compliance.
     * </p>
     * 
     * @param obj the inventory object to create a wrapper for
     * @return a StackPane wrapper containing the visual representation
     */
    private StackPane createInventoryItemWrapper(InventoryObject obj) {
        PhysicsVisualPair pair = mm.controller.InventoryObjectController.convert(obj, model.getWorld());
        if (pair.visual == null) {
            return null;
        }

        pair.visual.setRotate(obj.getAngle());

        // Dynamically adjust wrapper size based on rotated dimensions
        double rotatedWidth = pair.visual.getBoundsInParent().getWidth();
        double rotatedHeight = pair.visual.getBoundsInParent().getHeight();

        StackPane wrapper = new StackPane(pair.visual);
        wrapper.setPrefSize(rotatedWidth + 20, rotatedHeight + 20);

        Label countLabel = new Label(Integer.toString(obj.getCount()));
        countLabel.setMouseTransparent(true);

        // Apply appropriate CSS class based on count
        if (obj.getCount() <= 0) {
            countLabel.getStyleClass().add("item-count-no");
            wrapper.setStyle("-fx-opacity: 0.5;");
        } else {
            countLabel.getStyleClass().add("item-count-yes");
            wrapper.setStyle("");
        }

        wrapper.getChildren().add(countLabel);
        StackPane.setAlignment(countLabel, javafx.geometry.Pos.CENTER_RIGHT);

        return wrapper;
    }

    /**
     * Sets up event handlers for an inventory item wrapper.
     * <p>
     * This method contains the controller logic for drag-and-drop behavior
     * while keeping UI creation in the view.
     * </p>
     * 
     * @param wrapper the inventory item wrapper
     * @param obj     the inventory object associated with this wrapper
     */
    private void setupInventoryItemHandlers(StackPane wrapper, InventoryObject obj) {
        wrapper.setOnDragDetected(event -> {
            // Business logic: check if drag should be allowed
            if (!isDragAllowed(obj)) {
                event.consume();
                return;
            }

            // Delegate drag setup to helper method
            startInventoryItemDrag(wrapper, obj, event);
        });
    }

    /**
     * Determines if dragging/moving is allowed based on simulation state.
     * <p>
     * Business logic method that checks if the simulation is currently running.
     * </p>
     * 
     * @return true if dragging/moving is allowed, false if simulation is running
     */
    private boolean isInteractionAllowed() {
        PhysicsAnimationController timer = model.getTimer();
        return timer == null || !timer.isRunning();
    }

    /**
     * Determines if an inventory item can be dragged.
     * <p>
     * Business logic method that checks game state and item availability.
     * </p>
     * 
     * @param obj the inventory object to check
     * @return true if the item can be dragged, false otherwise
     */
    private boolean isDragAllowed(InventoryObject obj) {
        return isInteractionAllowed() && obj.getCount() > 0;
    }

    /**
     * Starts the drag operation for an inventory item.
     * <p>
     * Helper method to reduce complexity in the main drag handler.
     * </p>
     */
    private void startInventoryItemDrag(StackPane wrapper, InventoryObject obj, javafx.scene.input.MouseEvent event) {
        Dragboard db = wrapper.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        content.putString(obj.getName());
        db.setContent(content);

        // Create drag image from the visual component
        javafx.scene.Node visual = wrapper.getChildren().get(0); // First child should be the visual
        javafx.scene.SnapshotParameters snapshotParameters = new javafx.scene.SnapshotParameters();
        snapshotParameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
        javafx.scene.image.WritableImage snapshot = visual.snapshot(snapshotParameters, null);

        db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
        event.consume();
    }

    /**
     * Refreshes the inventory display without reloading data from file.
     * <p>
     * This method updates the visual representation of inventory items
     * based on current count values, without resetting counts from the JSON file.
     * </p>
     */
    private void refreshInventoryDisplay() {
        setupInventory(false);
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
                    // Create the GameObject but don't modify inventory count yet
                    GameObject simObj = new GameObject(
                            template.getName(),
                            template.getType(),
                            new Position((float) x - template.getSize().getWidth() / 2,
                                    (float) y - template.getSize().getHeight() / 2),
                            template.getSize());

                    // Set additional properties
                    simObj.setPhysics(template.getPhysics());
                    simObj.setAngle(template.getAngle());
                    simObj.setColour(template.getColour());
                    simObj.setSprite(template.getSprite());
                    simObj.setWinning(template.isWinning());

                    PhysicsVisualPair pair = mm.controller.GameObjectController.convert(simObj, model.getWorld());
                    if (pair.visual != null) {
                        // Position the visual at the calculated position
                        pair.visual.setTranslateX(simObj.getPosition().getX());
                        pair.visual.setTranslateY(simObj.getPosition().getY());
                        pair.visual.setRotate(simObj.getAngle());

                        // Check for collision before placing the object
                        if (!wouldCauseOverlap(pair, simObj.getPosition().getX(), simObj.getPosition().getY(),
                                simObj.getAngle())) {
                            // Create parameter object for AddObjectController
                            AddObjectController.AddObjectParams params = new AddObjectController.AddObjectParams(
                                    model, simSpace, gameObjectToPairMap, this::refreshInventoryDisplay);

                            // Create and execute add command
                            AddObjectController addCommand = new AddObjectController(params, simObj, pair);
                            model.getUndoRedoManager().executeCommand(addCommand);

                            addMoveHandlersToDroppedVisual(pair, simObj);
                            success = true;
                        }
                        // If collision would occur, don't place the object
                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Sets up menu button actions (play, stop, settings, delete, import, save).
     * <p>
     * Retrieves button groups from the refactored view and wires up event handlers.
     * </p>
     */
    private void setupMenuButtons() {
        // Get button groups from the refactored view
        SimulationView.SimulationButtons simButtons = view.getSimulationButtons();

        setupPlayButton(simButtons);
        setupStopButton(simButtons);
        setupSettingsButton(simButtons);
        setupUndoRedoButtons(simButtons);
        setupDeleteButton(simButtons);
        setupFileButtons(simButtons);
        setupCrownButton(simButtons);
    }

    /**
     * Sets up the play button action.
     */
    private void setupPlayButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.playButton != null) {
            simButtons.playButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    timer.start();
                    setInventoryItemsDisabled(true);
                }
            });
        }
    }

    /**
     * Sets up the stop button action.
     */
    private void setupStopButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.stopButton != null) {
            simButtons.stopButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                    timer.reset();

                    // Clear undo/redo history when stopping simulation to prevent inconsistent
                    // state
                    model.getUndoRedoManager().clear();

                    // Reset simulation to state before play was pressed
                    model.setDroppedObjects(model.getDroppedObjects());
                    model.setDroppedVisualPairs(model.getDroppedPhysicsVisualPairs());
                    gameObjectToPairMap.clear();
                    setInventoryItemsDisabled(false);
                    setupSimulation();
                    refreshInventoryDisplay();
                }
            });
        }
    }

    /**
     * Sets up the settings button action.
     */
    private void setupSettingsButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.settingsButton != null) {
            simButtons.settingsButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                view.getOverlaySettings().setVisible(true);
            });
        }
    }

    /**
     * Sets up the undo and redo button actions.
     */
    private void setupUndoRedoButtons(SimulationView.SimulationButtons simButtons) {
        if (simButtons.undoButton != null) {
            simButtons.undoButton.setOnAction(e -> {
                if (isInteractionAllowed()) {
                    model.getUndoRedoManager().undo();
                }
            });
        }

        if (simButtons.redoButton != null) {
            simButtons.redoButton.setOnAction(e -> {
                if (isInteractionAllowed()) {
                    model.getUndoRedoManager().redo();
                }
            });
        }
    }

    /**
     * Sets up the delete button action.
     */
    private void setupDeleteButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.deleteButton != null) {
            simButtons.deleteButton.setOnAction(e -> {
                // Clear undo/redo history when deleting all objects
                model.getUndoRedoManager().clear();

                // Restore inventory counts before clearing objects
                model.restoreInventoryCounts();

                model.setDroppedObjects(new ArrayList<>());
                model.setDroppedVisualPairs(new ArrayList<>());
                gameObjectToPairMap.clear();
                setInventoryItemsDisabled(false);
                setupSimulation();
                refreshInventoryDisplay();
            });
        }
    }

    /**
     * Sets up the import and save button actions.
     */
    private void setupFileButtons(SimulationView.SimulationButtons simButtons) {
        if (simButtons.importButton != null) {
            simButtons.importButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Import your level!");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    model.setLevelPath("/level/" + file.getName());
                }
                setupSimulation();
                setupInventory(true); // Reload data when importing new level
            });
        }

        if (simButtons.saveButton != null) {
            simButtons.saveButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    model.exportLevel();
                }
            });
        }
    }

    /**
     * Sets up the crown button action.
     */
    private void setupCrownButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.crownButton != null) {
            simButtons.crownButton.setOnAction(e -> {
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

        // Get button groups from the refactored view
        SimulationView.OverlayButtons overlayButtons = view.getOverlayButtons();
        SimulationView.WinScreenButtons winButtons = view.getWinScreenButtons();

        overlayButtons.overlayCloseButton.setOnAction(e -> {
            view.getOverlaySettings().setVisible(false);
        });

        overlayButtons.overlayBackButton.setOnAction(e -> {
            // Hide the overlay before switching scenes to avoid overlay showing on title
            // screen
            view.getOverlaySettings().setVisible(false);
            Scene newScreen = ApplicationController.titleScreenController.getScene();
            primaryStage.setScene(newScreen);
            primaryStage.setWidth(scene.getWidth());
            primaryStage.setHeight(scene.getHeight());
        });

        overlayButtons.overlayQuitButton.setOnAction(e -> {
            Platform.exit();
        });

        // win screen overlay functions
        winButtons.btnWinHome.setOnAction(e -> {
            view.getWinScreenOverlay().setVisible(false);
            Scene newScreen = ApplicationController.titleScreenController.getScene();
            primaryStage.setScene(newScreen);
            primaryStage.setWidth(scene.getWidth());
            primaryStage.setHeight(scene.getHeight());
        });

        if (winButtons.btnWinExport != null) {
            winButtons.btnWinExport.setOnAction(e -> {
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
        if (view.getWinScreenButtons().btnWinNext != null) {
            view.getWinScreenButtons().btnWinNext.setOnAction(e -> {
                SimulationController simController = new SimulationController(primaryStage, nextLevelPath,
                        true, atPuzzlesEnd, selectedSkin);
                Scene simScene = simController.getScene();
                primaryStage.setScene(simScene);
            });
        }
    }

    /**
     * Adds mouse event handlers to a dropped object's visual node to enable moving
     * it within the simulation area.
     */
    private void addMoveHandlersToDroppedVisual(PhysicsVisualPair pair, GameObject simObj) {
        javafx.scene.Node visual = pair.visual;
        final double[] dragDelta = new double[2];

        visual.setOnMouseEntered(event -> visual.setCursor(javafx.scene.Cursor.CLOSED_HAND));
        visual.setOnMouseExited(event -> visual.setCursor(javafx.scene.Cursor.DEFAULT));

        visual.setOnMousePressed(event -> {
            if (!isInteractionAllowed()) {
                event.consume();
                return;
            }

            // Store starting position and angle for undo
            dragStartPosition = new Position(simObj.getPosition().getX(), simObj.getPosition().getY());
            dragStartAngle = simObj.getAngle();

            dragDelta[0] = event.getSceneX() - visual.getTranslateX();
            dragDelta[1] = event.getSceneY() - visual.getTranslateY();
            event.consume();
        });

        visual.setOnMouseDragged(event -> {
            if (!isInteractionAllowed()) {
                event.consume();
                return;
            }

            double newX = event.getSceneX() - dragDelta[0];
            double newY = event.getSceneY() - dragDelta[1];

            // Check for collision before allowing the move
            if (!wouldCauseOverlap(pair, newX, newY, simObj.getAngle())) {
                visual.setTranslateX(newX);
                visual.setTranslateY(newY);

                // Update the GameObject's position to match the visual position
                simObj.getPosition().setX((float) newX);
                simObj.getPosition().setY((float) newY);

                // Update physics body position
                if (visual instanceof javafx.scene.shape.Rectangle) {
                    javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) visual;
                    float centerX = (float) (newX + rect.getWidth() / 2);
                    float centerY = (float) (newY + rect.getHeight() / 2);
                    pair.body.setTransform(
                            new org.jbox2d.common.Vec2(centerX / 50.0f, centerY / 50.0f),
                            pair.body.getAngle());
                } else if (visual instanceof javafx.scene.shape.Circle) {
                    pair.body.setTransform(
                            new org.jbox2d.common.Vec2((float) (newX / 50.0), (float) (newY / 50.0)),
                            pair.body.getAngle());
                } else if (visual instanceof javafx.scene.shape.Polygon) {
                    // Handle bucket (polygon) positioning - center like rectangles
                    javafx.scene.shape.Polygon polygon = (javafx.scene.shape.Polygon) visual;
                    javafx.geometry.Bounds bounds = polygon.getBoundsInLocal();
                    float centerX = (float) (newX + bounds.getWidth() / 2);
                    float centerY = (float) (newY + bounds.getHeight() / 2);
                    pair.body.setTransform(
                            new org.jbox2d.common.Vec2(centerX / 50.0f, centerY / 50.0f),
                            pair.body.getAngle());
                }
            }
            // If collision would occur, simply don't update the position - object stays in
            // place

            event.consume();
        });

        visual.setOnMouseReleased(event -> {
            if (!isInteractionAllowed()) {
                event.consume();
                return;
            }

            // Create move command if position or angle changed
            Position currentPosition = new Position(simObj.getPosition().getX(), simObj.getPosition().getY());
            float currentAngle = simObj.getAngle();

            if (dragStartPosition != null &&
                    (Math.abs(dragStartPosition.getX() - currentPosition.getX()) > 1.0f ||
                            Math.abs(dragStartPosition.getY() - currentPosition.getY()) > 1.0f ||
                            Math.abs(dragStartAngle - currentAngle) > 1.0f)) {

                MoveObjectController.MoveObjectParams moveParams = new MoveObjectController.MoveObjectParams.Builder()
                        .setGameObject(simObj)
                        .setPair(pair)
                        .setPositions(dragStartPosition, currentPosition)
                        .setAngles(dragStartAngle, currentAngle)
                        .build();
                MoveObjectController moveCommand = new MoveObjectController(moveParams);
                model.getUndoRedoManager().executeCommand(moveCommand);
            }

            event.consume();
        });

        visual.setOnScroll(event -> {
            if (!isInteractionAllowed()) {
                event.consume();
                return;
            }

            float startAngle = simObj.getAngle();
            Position currentPosition = new Position(simObj.getPosition().getX(), simObj.getPosition().getY());
            float newAngle = startAngle + 5;

            // Check for collision before allowing the rotation
            if (!wouldCauseOverlap(pair, currentPosition.getX(), currentPosition.getY(), newAngle)) {
                // Update visual rotation
                pair.visual.setRotate(newAngle);

                // Update GameObject angle
                simObj.setAngle(newAngle);

                // Update physics body rotation
                pair.body.setTransform(
                        pair.body.getPosition(),
                        (float) Math.toRadians(newAngle));

                // Create move command for rotation
                MoveObjectController.MoveObjectParams rotateParams = new MoveObjectController.MoveObjectParams.Builder()
                        .setGameObject(simObj)
                        .setPair(pair)
                        .setPositions(currentPosition, currentPosition)
                        .setAngles(startAngle, newAngle)
                        .build();
                MoveObjectController rotateCommand = new MoveObjectController(rotateParams);
                model.getUndoRedoManager().executeCommand(rotateCommand);
            }
            // If collision would occur, do nothing (deny rotation)

            event.consume();
        });
    }

    /**
     * Checks if moving or rotating an object to a new position/angle would cause it
     * to overlap with other objects.
     * Delegates to the model's collision detection service.
     *
     * @param movingPair The physics-visual pair being moved
     * @param newX       The proposed new X position
     * @param newY       The proposed new Y position

     * @param newAngle   The proposed new angle (in degrees)
     * @return true if the new transform would cause an overlap, false otherwise
     */
    private boolean wouldCauseOverlap(PhysicsVisualPair movingPair, double newX, double newY, float newAngle) {
        return model.wouldCauseOverlap(movingPair, newX, newY, newAngle);
    }
}