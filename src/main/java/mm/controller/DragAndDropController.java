package mm.controller;

import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import mm.model.GameObject;
import mm.model.InventoryObject;
import mm.model.PhysicsVisualPair;
import mm.model.Position;
import mm.model.SimulationModel;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Controller for handling drag and drop operations in the simulation.
 * This class manages the process of dragging inventory items into the simulation area.
 */
public class DragAndDropController {
    private final SimulationModel model;
    private final Pane simSpace;
    private final Map<GameObject, PhysicsVisualPair> gameObjectToPairMap;
    private final Runnable onInventoryUpdated;
    private final BiConsumer<PhysicsVisualPair, GameObject> setupMoveHandlers;

    /**
     * Parameter object for the DragAndDropController constructor.
     */
    public static class Params {
        private final SimulationModel model;
        private final Pane simSpace;
        private final Map<GameObject, PhysicsVisualPair> gameObjectToPairMap;
        private final Runnable onInventoryUpdated;
        private final BiConsumer<PhysicsVisualPair, GameObject> setupMoveHandlers;

        private Params(Builder builder) {
            this.model = builder.model;
            this.simSpace = builder.simSpace;
            this.gameObjectToPairMap = builder.gameObjectToPairMap;
            this.onInventoryUpdated = builder.onInventoryUpdated;
            this.setupMoveHandlers = builder.setupMoveHandlers;
        }

        /**
         * Builder class for constructing Params objects.
         * <p>
         * This builder provides a fluent API for setting all required parameters
         * for the DragAndDropController.
         * </p>
         */
        public static class Builder {
            private SimulationModel model;
            private Pane simSpace;
            private Map<GameObject, PhysicsVisualPair> gameObjectToPairMap;
            private Runnable onInventoryUpdated;
            private BiConsumer<PhysicsVisualPair, GameObject> setupMoveHandlers;

            /**
             * Sets the simulation model.
             * 
             * @param model the simulation model to use
             * @return this builder instance for method chaining
             */
            public Builder setModel(SimulationModel model) {
                this.model = model;
                return this;
            }

            /**
             * Sets the simulation space pane.
             * 
             * @param simSpace the JavaFX pane representing the simulation area
             * @return this builder instance for method chaining
             */
            public Builder setSimSpace(Pane simSpace) {
                this.simSpace = simSpace;
                return this;
            }

            /**
             * Sets the map that tracks game objects and their physics-visual pairs.
             * 
             * @param map the map linking game objects to their visual representations
             * @return this builder instance for method chaining
             */
            public Builder setGameObjectToPairMap(Map<GameObject, PhysicsVisualPair> map) {
                this.gameObjectToPairMap = map;
                return this;
            }

            /**
             * Sets the callback to be invoked when inventory is updated.
             * 
             * @param callback the callback to run when inventory changes
             * @return this builder instance for method chaining
             */
            public Builder setOnInventoryUpdated(Runnable callback) {
                this.onInventoryUpdated = callback;
                return this;
            }

            /**
             * Sets the callback to configure move handlers for objects.
             * 
             * @param handlers the callback to set up movement handlers
             * @return this builder instance for method chaining
             */
            public Builder setSetupMoveHandlers(BiConsumer<PhysicsVisualPair, GameObject> handlers) {
                this.setupMoveHandlers = handlers;
                return this;
            }

            /**
             * Builds and returns a new Params object with the configured values.
             * 
             * @return a new Params instance
             * @throws IllegalStateException if any required parameter is null
             */
            public Params build() {
                if (model == null || simSpace == null || gameObjectToPairMap == null) {
                    throw new IllegalStateException("Required parameters must not be null");
                }
                return new Params(this);
            }
        }
    }

    /**
     * Creates a new DragAndDropController with the provided parameters.
     *
     * @param params The parameter object containing all required components
     */
    public DragAndDropController(Params params) {
        this.model = params.model;
        this.simSpace = params.simSpace;
        this.gameObjectToPairMap = params.gameObjectToPairMap;
        this.onInventoryUpdated = params.onInventoryUpdated;
        this.setupMoveHandlers = params.setupMoveHandlers;
    }

    /**
     * Sets up drag and drop functionality for placing inventory objects into the
     * simulation area.
     */
    public void setupDragAndDrop() {
        simSpace.setOnDragOver(event -> {
            PhysicsAnimationController timer = model.getTimer();
            if ((timer == null || !timer.isRunning()) && event.getGestureSource() != simSpace
                    && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        simSpace.setOnDragDropped(event -> {
            // Early returns for invalid states
            if (!isDropAllowed(event)) {
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

            // Try to handle the drop operation
            boolean success = handleDrop(event, x, y);
            
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Checks if dropping is currently allowed based on simulation state.
     */
    private boolean isDropAllowed(javafx.scene.input.DragEvent event) {
        PhysicsAnimationController timer = model.getTimer();
        return (timer == null || !timer.isRunning()) && event.getDragboard().hasString();
    }

    /**
     * Handles the drop operation, creating and placing objects as needed.
     * 
     * @param event The drag event
     * @param x The x coordinate
     * @param y The y coordinate
     * @return true if drop was successful, false otherwise
     */
    private boolean handleDrop(javafx.scene.input.DragEvent event, double x, double y) {
        Dragboard db = event.getDragboard();
        if (!db.hasString()) {
            return false;
        }
        
        String name = db.getString();
        InventoryObject template = model.findInventoryObjectByName(name);
        if (template == null) {
            return false;
        }
        
        return createAndPlaceObject(template, x, y);
    }

    /**
     * Creates and places a game object based on the template.
     */
    private boolean createAndPlaceObject(InventoryObject template, double x, double y) {
        GameObject simObj = createGameObjectFromTemplate(template, x, y);
        PhysicsVisualPair pair = GameObjectController.convert(simObj, model.getWorld());
        
        if (pair.visual == null) {
            return false;
        }
        
        // Position the visual at the calculated position
        pair.visual.setTranslateX(simObj.getPosition().getX());
        pair.visual.setTranslateY(simObj.getPosition().getY());
        pair.visual.setRotate(simObj.getAngle());
        
        // Check for collision before placing the object
        if (model.wouldCauseOverlap(pair, simObj.getPosition().getX(), 
                simObj.getPosition().getY(), simObj.getAngle())) {
            return false;
        }
        
        // Create parameter object for AddObjectController
        AddObjectController.AddObjectParams params = new AddObjectController.AddObjectParams(
                model, simSpace, gameObjectToPairMap, onInventoryUpdated);
        
        // Create and execute add command
        AddObjectController addCommand = new AddObjectController(params, simObj, pair);
        model.getUndoRedoManager().executeCommand(addCommand);
        
        // Add this line to setup move handlers
        setupMoveHandlers.accept(pair, simObj);
        
        return true;
    }

    /**
     * Creates a GameObject based on the provided template at the specified position.
     */
    private GameObject createGameObjectFromTemplate(InventoryObject template, double x, double y) {
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

        return simObj;
    }
}