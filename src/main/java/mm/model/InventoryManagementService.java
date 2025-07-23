package mm.model;

/**
 * Service class responsible for managing inventory operations.
 * This class handles inventory object creation, count management,
 * and template-based object generation.
 */
public class InventoryManagementService {
    
    private final SimulationModel.GameObjectCollections gameObjects;
    
    /**
     * Constructs an InventoryManagementService with access to game object collections.
     * 
     * @param gameObjects the game object collections containing inventory data
     */
    public InventoryManagementService(SimulationModel.GameObjectCollections gameObjects) {
        this.gameObjects = gameObjects;
    }
    
    /**
     * Finds an inventory object template by its name.
     *
     * @param name the name of the inventory object to find
     * @return the InventoryObject with the given name, or null if not found
     */
    public InventoryObject findInventoryObjectByName(String name) {
        for (InventoryObject obj : gameObjects.inventoryObjects) {
            if (obj.getName().equals(name)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Creates a new GameObject from an InventoryObject template and a specified position.
     *
     * @param template the InventoryObject template to use
     * @param x        the x-coordinate for the new object
     * @param y        the y-coordinate for the new object
     * @return a new GameObject instance based on the template and position
     */
    public GameObject createGameObjectFromInventory(InventoryObject template, float x, float y) {
        // Calculate offset to center the object on the drop position
        float offsetX = template.getSize().getWidth() / 2;
        float offsetY = template.getSize().getHeight() / 2;

        // Create new GameObject with adjusted position
        GameObject gameObject = new GameObject(
                template.getName(),
                template.getType(),
                new Position(x - offsetX, y - offsetY),
                template.getSize());
        
        // Set additional properties using setters
        gameObject.setPhysics(template.getPhysics());
        gameObject.setAngle(template.getAngle());
        gameObject.setColour(template.getColour());
        gameObject.setSprite(template.getSprite());
        gameObject.setWinning(template.isWinning());

        // Don't modify inventory count here - let the command handle it
        // template.setCount(template.getCount() - 1);
        
        return gameObject;
    }

    /**
     * Increments the inventory count for a specific item.
     * Used when undoing object placement.
     * 
     * @param itemName the name of the inventory item to increment
     */
    public void incrementInventoryCount(String itemName) {
        for (InventoryObject obj : gameObjects.inventoryObjects) {
            if (obj.getName().equals(itemName)) {
                obj.setCount(obj.getCount() + 1);
                break;
            }
        }
    }
    
    /**
     * Decrements the inventory count for a specific item.
     * Used when redoing object placement.
     * 
     * @param itemName the name of the inventory item to decrement
     */
    public void decrementInventoryCount(String itemName) {
        for (InventoryObject obj : gameObjects.inventoryObjects) {
            if (obj.getName().equals(itemName)) {
                int currentCount = obj.getCount();
                if (currentCount > 0) {
                    obj.setCount(currentCount - 1);
                }
                break;
            }
        }
    }

    /**
     * Restores inventory counts for all dropped objects.
     * This method should be called when clearing all dropped objects to return
     * their counts to the inventory.
     * 
     * @param droppedObjects the list of dropped objects to restore counts for
     */
    public void restoreInventoryCounts(java.util.List<GameObject> droppedObjects) {
        for (GameObject droppedObj : droppedObjects) {
            InventoryObject inventoryTemplate = findInventoryObjectByName(droppedObj.getName());
            if (inventoryTemplate != null) {
                inventoryTemplate.setCount(inventoryTemplate.getCount() + 1);
            }
        }
    }
}
