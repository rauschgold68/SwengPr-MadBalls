package mm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.common.Vec2;

import mm.Generated;
import mm.model.GameObject;
import mm.model.PhysicsVisualPair;
import mm.model.SimulationBounds;
import mm.model.SimulationModel;

/**
 * Handles object culling operations for the physics simulation.
 * This class manages out-of-bounds detection, object removal, and restoration.
 */
public class ObjectCullingController {
    private final SimulationModel model;
    private final List<GameObject> culledObjects = new ArrayList<>();
    private final List<Vec2> originalPositions = new ArrayList<>();
    private final Map<String, GameObject> objectNameMap = new HashMap<>();
    
    // Pre-allocated lists for removal operations
    private final List<PhysicsVisualPair> pairsToRemove = new ArrayList<>();
    private final List<GameObject> objectsToRemove = new ArrayList<>();
    private final List<javafx.scene.Node> visualsToRemove = new ArrayList<>();
    
    /**
     * Constructs an ObjectCullingManager for the given simulation model.
     * 
     * @param model the simulation model to manage
     */
    public ObjectCullingController(SimulationModel model) {
        this.model = model;
    }
    
    /**
     * Gets the list of pairs marked for removal.
     * @return the pairs to remove list
     */
    public List<PhysicsVisualPair> getPairsToRemove() {
        return pairsToRemove;
    }
    
    /**
     * Gets the list of objects marked for removal.
     * @return the objects to remove list
     */
    public List<GameObject> getObjectsToRemove() {
        return objectsToRemove;
    }
    
    /**
     * Gets the list of visuals marked for removal.
     * @return the visuals to remove list
     */
    @Generated
    public List<javafx.scene.Node> getVisualsToRemove() {
        return visualsToRemove;
    }
    
    /**
     * Clears all removal lists for reuse.
     */
    public void clearRemovalLists() {
        pairsToRemove.clear();
        objectsToRemove.clear();
        visualsToRemove.clear();
    }
    
    /**
     * Checks if an object should be culled based on position.
     * 
     * @param scaledX the scaled X position
     * @param scaledY the scaled Y position
     * @param bounds the simulation bounds containing width and height
     * @param objectName the object name for type-specific checks
     * @return true if the object should be culled
     */
    public boolean shouldCullObject(double scaledX, double scaledY, SimulationBounds bounds, 
                                   String objectName) {
        boolean isBalloon = "ballon".equalsIgnoreCase(objectName);
        double margin = isBalloon ? 50.0 : 100.0;
        
        boolean shouldCull = scaledX < -margin || scaledX > bounds.getWidth() + margin || 
                           scaledY < -margin || scaledY > bounds.getHeight() + margin;
                           
        if (isBalloon) {
            // Additional early culling check for balloons going up
            shouldCull = shouldCull || scaledY < bounds.getHeight() * 0.1;
        }
        
        return shouldCull;
    }
    
    /**
     * Handles culling of a specific object pair.
     * 
     * @param pair the physics-visual pair to cull
     * @param objectName the name of the object
     */
    public void cullObject(PhysicsVisualPair pair, String objectName) {
        pairsToRemove.add(pair);
        
        GameObject matchedObj = findMatchedObject(objectName);
        if (matchedObj != null) {
            objectsToRemove.add(matchedObj);
            culledObjects.add(matchedObj);
            originalPositions.add(new Vec2(matchedObj.getPosition().getX(), matchedObj.getPosition().getY()));
        }
        
        // Queue visual for removal
        if (pair.visual != null && pair.visual.getParent() != null) {
            visualsToRemove.add(pair.visual);
        }
    }
    
    /**
     * Finds a matching GameObject using cache first, then search.
     */
    private GameObject findMatchedObject(String objectName) {
        GameObject matchedObj = objectNameMap.get(objectName);
        if (matchedObj != null) {
            return matchedObj;
        }
        
        for (GameObject obj : model.getDroppedObjects()) {
            if (obj.getName().equals(objectName)) {
                objectNameMap.put(objectName, obj);
                return obj;
            }
        }
        
        return null;
    }
    
    /**
     * Restores all culled objects to their original positions.
     */
    public void restoreAllCulledObjects() {
        for (int i = 0; i < culledObjects.size(); i++) {
            GameObject obj = culledObjects.get(i);
            Vec2 originalPos = originalPositions.get(i);
            
            // Reset object position
            obj.getPosition().setX(originalPos.x);
            obj.getPosition().setY(originalPos.y);
            
            // Add back to model
            model.addDroppedObject(obj);
            objectNameMap.put(obj.getName(), obj);
            model.decrementInventoryCount(obj.getName());
        }
        
        culledObjects.clear();
        originalPositions.clear();
    }
    
    /**
     * Updates the object cache when new objects are added.
     */
    public void updateObjectCache() {
        objectNameMap.clear();
        for (GameObject obj : model.getDroppedObjects()) {
            objectNameMap.put(obj.getName(), obj);
        }
    }
    
    /**
     * Clears the object cache.
     */
    public void clearObjectCache() {
        objectNameMap.clear();
    }
    
    /**
     * Removes an object from the cache.
     * 
     * @param objectName the name of the object to remove
     */
    public void removeFromCache(String objectName) {
        objectNameMap.remove(objectName);
    }
}
