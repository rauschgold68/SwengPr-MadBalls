package mm.controller;

import mm.model.GameObject;
import mm.model.PhysicsVisualPair;
import mm.model.SimulationModel;
import javafx.scene.layout.Pane;
import java.util.Map;

/**
 * Command for adding an object to the simulation.
 * Handles both the model and view updates for undo/redo.
 */
public class AddObjectController implements Command {
    private final SimulationModel model;
    private final Pane simSpace;
    private final GameObject gameObject;
    private final PhysicsVisualPair pair;
    private final Map<GameObject, PhysicsVisualPair> gameObjectToPairMap;
    private final Runnable refreshInventoryCallback;
    
    /**
     * Parameter object containing all dependencies for AddObjectController.
     */
    public static class AddObjectParams {
        public final SimulationModel model;
        public final Pane simSpace;
        public final Map<GameObject, PhysicsVisualPair> gameObjectToPairMap;
        public final Runnable refreshInventoryCallback;
        
        /**
         * Constructs a new AddObjectParams with the required simulation dependencies.
         * 
         * @param model The simulation model to manage game state
         * @param simSpace The JavaFX pane where visual objects are displayed
         * @param gameObjectToPairMap The mapping between game objects and their physics-visual pairs
         * @param refreshInventoryCallback The callback to refresh the inventory display
         */
        public AddObjectParams(SimulationModel model, Pane simSpace, 
                              Map<GameObject, PhysicsVisualPair> gameObjectToPairMap,
                              Runnable refreshInventoryCallback) {
            this.model = model;
            this.simSpace = simSpace;
            this.gameObjectToPairMap = gameObjectToPairMap;
            this.refreshInventoryCallback = refreshInventoryCallback;
        }
    }

    /**
     * Constructs a new AddObjectController command.
     * 
     * @param params The parameter object containing simulation dependencies
     * @param gameObject The game object to be added to the simulation
     * @param pair The physics-visual pair containing both physics body and visual representation
     */
    public AddObjectController(AddObjectParams params, GameObject gameObject, PhysicsVisualPair pair) {
        this.model = params.model;
        this.simSpace = params.simSpace;
        this.gameObjectToPairMap = params.gameObjectToPairMap;
        this.refreshInventoryCallback = params.refreshInventoryCallback;
        this.gameObject = gameObject;
        this.pair = pair;
    }
    
    @Override
    public void execute() {
        // Add to model
        model.addDroppedObject(gameObject);
        model.getPairs().add(pair);
        model.getDroppedPhysicsVisualPairs().add(pair);
        
        // Add to view
        if (!simSpace.getChildren().contains(pair.visual)) {
            simSpace.getChildren().add(pair.visual);
        }
        
        // Update mapping
        gameObjectToPairMap.put(gameObject, pair);
        
        // Decrease inventory count when executing (for redo operations)
        model.decrementInventoryCount(gameObject.getName());
        
        // Refresh inventory display
        refreshInventoryCallback.run();
    }
    
    @Override
    public void undo() {
        // Remove from model
        model.getDroppedObjects().remove(gameObject);
        model.getPairs().remove(pair);
        model.getDroppedPhysicsVisualPairs().remove(pair);
        
        // Remove from view
        simSpace.getChildren().remove(pair.visual);
        
        // Update mapping
        gameObjectToPairMap.remove(gameObject);
        
        // Restore inventory count
        model.incrementInventoryCount(gameObject.getName());
        
        // Refresh inventory display
        refreshInventoryCallback.run();
    }
    
    @Override
    public String getDescription() {
        return "Add " + gameObject.getName();
    }
}