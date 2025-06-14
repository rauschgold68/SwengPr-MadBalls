package mm.model.objects;

import java.util.List;

/**
 * Representing a level configuration for the game.
 * <p>
 * This class is used for deserializing JSON input that defines a level, including
 * both pre-placed game objects and inventory objects available to the player.
 * </p>
 * <ul>
 *   <li><b>levelObjects</b>: List of {@link GameObject} instances that are already placed in the level.</li>
 *   <li><b>inventoryObjects</b>: List of {@link InventoryObject} instances that the player can place during gameplay.</li>
 * </ul>
 */
public class Level {
    /**
     * List of already prepositioned GameObjects in the level.
     */
    private List<GameObject> levelObjects;

    /**
     * List of InventoryObjects available to the player for placement.
     */
    private List<InventoryObject> inventoryObjects;

    /**
     * Default constructor for Level.
     */
    public Level() {}

    /**
     * Returns the list of pre-placed GameObjects in the level.
     *
     * @return List of {@link GameObject} instances that are already placed in the level.
     */
    public List<GameObject> getLevelObjects() {
        return levelObjects;
    }

    /**
     * Sets the list of pre-placed GameObjects for the level.
     *
     * @param objects List of {@link GameObject} instances to be set as pre-placed objects.
     */
    public void setLevelObjects(List<GameObject> objects) {
        this.levelObjects = objects;
    }

    /**
     * Returns the list of InventoryObjects available to the player.
     *
     * @return List of {@link InventoryObject} instances available in the inventory.
     */
    public List<InventoryObject> getInventoryObjects() {
        return inventoryObjects;
    }

    /**
     * Sets the list of InventoryObjects available to the player.
     *
     * @param inventory List of {@link InventoryObject} instances to be set as inventory items.
     */
    public void setInventoryObjects(List<InventoryObject> inventory) {
        this.inventoryObjects = inventory;
    }
}