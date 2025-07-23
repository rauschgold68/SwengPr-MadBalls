package mm.controller;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import mm.model.GameObject;
import mm.model.PhysicsVisualPair;
import mm.model.Position;
import mm.model.SimulationModel;
import mm.view.SimulationView;

import java.util.List;

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

    public final SimulationModel model;
    public final SimulationView view;
    private final Stage primaryStage;
    // Map to track correspondence between GameObjects and their PhysicsVisualPairs
    private final java.util.Map<GameObject, PhysicsVisualPair> gameObjectToPairMap = new java.util.HashMap<>();

    // Store original window dimensions to restore them when returning to title
    // screen
    private final double originalWidth;
    private final double originalHeight;

    // Drag start position and angle for undo functionality
    private Position dragStartPosition;
    private float dragStartAngle;

    private JsonViewController jsonViewController;

    // Inventory manager for handling inventory UI and interactions
    private InventoryManager inventoryManager;

    // Controller for all DragAndDrop Features
    private DragAndDropController dragAndDropController;

    /**
     * Parameter object for SimulationController constructor to reduce parameter count.
     * <p>
     * This class encapsulates all the configuration parameters needed to create a SimulationController
     * instance, following the parameter object pattern to avoid excessive parameter lists.
     * Uses the Builder pattern for flexible and readable object construction.
     * </p>
     * 
     * @see SimulationController#SimulationController(SimulationControllerParams)
     * @see Builder
     */
    public static class SimulationControllerParams {
        /** The primary stage of the JavaFX application */
        public final Stage primaryStage;
        
        /** The resource path to the level JSON file to load */
        public final String levelPath;
        
        /** Whether the simulation is running in puzzle mode (true) or sandbox mode (false) */
        public final boolean isPuzzleMode;
        
        /** Whether the player has reached the end of all puzzle levels */
        public final boolean atPuzzlesEnd;
        
        /** The selected visual skin theme ("Default" or "Legacy") */
        public final String selectedSkin;
        
        /**
         * Private constructor to enforce use of the Builder pattern.
         * 
         * @param builder the builder instance containing all configuration values
         */
        private SimulationControllerParams(Builder builder) {
            this.primaryStage = builder.primaryStage;
            this.levelPath = builder.levelPath;
            this.isPuzzleMode = builder.isPuzzleMode;
            this.atPuzzlesEnd = builder.atPuzzlesEnd;
            this.selectedSkin = builder.selectedSkin;
        }
        
        /**
         * Builder class for constructing SimulationControllerParams instances.
         * <p>
         * Provides a fluent API for setting configuration parameters with sensible defaults.
         * Required parameters (primaryStage and levelPath) are validated in the build() method.
         * </p>
         * 
         * <h3>Usage Example:</h3>
         * <pre>{@code
         * SimulationControllerParams params = new SimulationControllerParams.Builder()
         *     .setPrimaryStage(stage)
         *     .setLevelPath("/level/level1.json")
         *     .setPuzzleMode(true)
         *     .setSelectedSkin("Legacy")
         *     .build();
         * }</pre>
         */
        public static class Builder {
            /** The primary stage - required parameter */
            private Stage primaryStage;
            
            /** The level path - required parameter */
            private String levelPath;
            
            /** Whether puzzle mode is enabled - defaults to false (sandbox mode) */
            private boolean isPuzzleMode = false;
            
            /** Whether at the end of puzzles - defaults to false */
            private boolean atPuzzlesEnd = false;
            
            /** The selected skin - defaults to "Default" */
            private String selectedSkin = "Default";
            
            /**
             * Sets the primary stage for the simulation.
             * 
             * @param primaryStage the JavaFX primary stage (required)
             * @return this builder instance for method chaining
             */
            public Builder setPrimaryStage(Stage primaryStage) {
                this.primaryStage = primaryStage;
                return this;
            }
            
            /**
             * Sets the path to the level JSON file.
             * 
             * @param levelPath the resource path to the level file (required)
             * @return this builder instance for method chaining
             */
            public Builder setLevelPath(String levelPath) {
                this.levelPath = levelPath;
                return this;
            }
            
            /**
             * Sets whether the simulation should run in puzzle mode.
             * 
             * @param isPuzzleMode true for puzzle mode, false for sandbox mode
             * @return this builder instance for method chaining
             */
            public Builder setPuzzleMode(boolean isPuzzleMode) {
                this.isPuzzleMode = isPuzzleMode;
                return this;
            }
            
            /**
             * Sets whether the player has reached the end of all puzzle levels.
             * 
             * @param atPuzzlesEnd true if at the end of puzzles, false otherwise
             * @return this builder instance for method chaining
             */
            public Builder setAtPuzzlesEnd(boolean atPuzzlesEnd) {
                this.atPuzzlesEnd = atPuzzlesEnd;
                return this;
            }
            
            /**
             * Sets the selected visual skin theme.
             * 
             * @param selectedSkin the skin name ("Default" or "Legacy")
             * @return this builder instance for method chaining
             */
            public Builder setSelectedSkin(String selectedSkin) {
                this.selectedSkin = selectedSkin;
                return this;
            }
            
            /**
             * Builds and returns a new SimulationControllerParams instance.
             * <p>
             * Validates that all required parameters (primaryStage and levelPath) have been set.
             * </p>
             * 
             * @return a new SimulationControllerParams instance with the configured values
             * @throws IllegalStateException if primaryStage or levelPath is null
             */
            public SimulationControllerParams build() {
                if (primaryStage == null || levelPath == null) {
                    throw new IllegalStateException("Primary stage and level path are required");
                }
                return new SimulationControllerParams(this);
            }
        }
    }

    /**
     * Constructs the SimulationController, sets up the model and view, and wires up
     * event handlers.
     *
     * @param params the parameter object containing all configuration values
     */
    public SimulationController(SimulationControllerParams params) {
        this.primaryStage = params.primaryStage;

        // Store original window dimensions before any changes
        this.originalWidth = params.primaryStage.getWidth();
        this.originalHeight = params.primaryStage.getHeight();

        this.model = new SimulationModel(params.levelPath);
        this.view = new SimulationView(params.primaryStage, params.isPuzzleMode, params.atPuzzlesEnd);
        this.selectedSkin = params.selectedSkin != null ? params.selectedSkin : "Default";

        // Set win listener
        this.model.setWinListener(() -> {
            Platform.runLater(() -> view.getWinScreenOverlay().setVisible(true));
        });

        setupSimulation();
        inventoryManager = new InventoryManager(
            model, 
            view.getInventoryItemBox(), 
            this::updateJsonViewer
        );
        inventoryManager.setupInventory(true);
        inventoryManager.updateInventorySpritesForSkin();
        inventoryManager.refreshInventoryDisplay();
        
        // Create and use the DragAndDropController
        dragAndDropController = new DragAndDropController(
            model,
            view.getSimSpace(),
            gameObjectToPairMap,
            this::refreshInventoryDisplay,
            this::addMoveHandlersToDroppedVisual
        );
        dragAndDropController.setupDragAndDrop();
        
        // Replace all button setup methods with ButtonManager
        ButtonManager buttonManager = new ButtonManager(
            model,
            view,
            primaryStage,
            originalWidth,
            originalHeight,
            gameObjectToPairMap,
            inventoryManager,
            this::updateJsonViewer,
            this::setupSimulation,
            params.atPuzzlesEnd,
            selectedSkin
        );
        buttonManager.setupAllButtons();
        
        updateJsonViewer();
        setupJsonController();
    }

    /**
     * Sets up JSON controller for sandbox mode.
     */
    private void setupJsonController() {
        TextArea jsonViewer = view.getJsonViewer();
        Label statusLabel = view.getJsonStatusLabel();
        if (jsonViewer != null && statusLabel != null) {
            // Create callback for when simulation needs to be refreshed
            Runnable onSimulationUpdate = () -> {
                setupSimulation();
                inventoryManager.refreshInventoryDisplay();
            };

            jsonViewController = new JsonViewController(model, jsonViewer, statusLabel, onSimulationUpdate);
        }
    }

    /**
     * Updates JSON viewer when simulation state changes.
     */
    private void updateJsonViewer() {
        if (jsonViewController != null) {
            jsonViewController.updateJsonViewer();
        }
    }

    /**
     * Updates the skin choice and refreshes the inventory display.
     * This method should be called when returning from the title screen.
     */
    public void updateSkinChoice() {
        String currentSkin = SkinManagerController.getInstance().getSelectedSkin();

        if (!currentSkin.equals(this.selectedSkin)) {
            this.selectedSkin = currentSkin;
            inventoryManager.updateInventorySpritesForSkin();
            inventoryManager.refreshInventoryDisplay();
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
        model.connectToView(simSpace);
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
        
        updateJsonViewer(); // Update JSON viewer after simulation setup
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
     * Refreshes the inventory display without reloading data from file.
     * <p>
     * This method updates the visual representation of inventory items
     * based on current count values, without resetting counts from the JSON file.
     * </p>
     */
    private void refreshInventoryDisplay() {
        inventoryManager.refreshInventoryDisplay();
        updateJsonViewer(); // Update JSON viewer when inventory changes
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
                    Rectangle rect = (javafx.scene.shape.Rectangle) visual;
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
                    Polygon polygon = (Polygon) visual;
                    javafx.geometry.Bounds bounds = polygon.getBoundsInLocal();
                    float centerX = (float) (newX + bounds.getWidth() / 2);
                    float centerY = (float) (newY + bounds.getHeight() / 2);
                    pair.body.setTransform(
                            new org.jbox2d.common.Vec2(centerX / 50.0f, centerY / 50.0f),
                            pair.body.getAngle());
                }
                
                updateJsonViewer(); // Update JSON viewer during drag
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

            updateJsonViewer(); // Update JSON viewer after drag ends
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
                
                updateJsonViewer(); // Update JSON viewer after rotation
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