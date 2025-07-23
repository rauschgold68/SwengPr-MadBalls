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

    // Add a field for the move handler setup callback
    private final BiConsumer<PhysicsVisualPair, GameObject> setupMoveHandlers;

    /**
     * Creates a new DragAndDropController.
     *
     * @param model The simulation model
     * @param simSpace The simulation space pane
     * @param gameObjectToPairMap Map tracking game objects and their visual pairs
     * @param onInventoryUpdated Callback when inventory is updated
     * @param setupMoveHandlers Callback to setup move handlers for game objects
     */
    public DragAndDropController(
            SimulationModel model,
            Pane simSpace,
            Map<GameObject, PhysicsVisualPair> gameObjectToPairMap,
            Runnable onInventoryUpdated,
            BiConsumer<PhysicsVisualPair, GameObject> setupMoveHandlers) {
        this.model = model;
        this.simSpace = simSpace;
        this.gameObjectToPairMap = gameObjectToPairMap;
        this.onInventoryUpdated = onInventoryUpdated;
        this.setupMoveHandlers = setupMoveHandlers;
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
                    GameObject simObj = createGameObjectFromTemplate(template, x, y);

                    PhysicsVisualPair pair = GameObjectController.convert(simObj, model.getWorld());
                    if (pair.visual != null) {
                        // Position the visual at the calculated position
                        pair.visual.setTranslateX(simObj.getPosition().getX());
                        pair.visual.setTranslateY(simObj.getPosition().getY());
                        pair.visual.setRotate(simObj.getAngle());

                        // Check for collision before placing the object
                        if (!model.wouldCauseOverlap(pair, simObj.getPosition().getX(), simObj.getPosition().getY(),
                                simObj.getAngle())) {
                            // Create parameter object for AddObjectController
                            AddObjectController.AddObjectParams params = new AddObjectController.AddObjectParams(
                                    model, simSpace, gameObjectToPairMap, onInventoryUpdated);

                            // Create and execute add command
                            AddObjectController addCommand = new AddObjectController(params, simObj, pair);
                            model.getUndoRedoManager().executeCommand(addCommand);

                            // Add this line to setup move handlers
                            setupMoveHandlers.accept(pair, simObj);

                            success = true;
                        }
                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
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