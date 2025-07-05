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
    
    public AddObjectController(SimulationModel model, Pane simSpace, GameObject gameObject, 
                           PhysicsVisualPair pair, Map<GameObject, PhysicsVisualPair> gameObjectToPairMap,
                           Runnable refreshInventoryCallback) {
        this.model = model;
        this.simSpace = simSpace;
        this.gameObject = gameObject;
        this.pair = pair;
        this.gameObjectToPairMap = gameObjectToPairMap;
        this.refreshInventoryCallback = refreshInventoryCallback;
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