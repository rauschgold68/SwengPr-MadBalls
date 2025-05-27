package mm.model.objects;

import java.util.List;
/**
     * POJO (Plain Old Java Object) for JSON input for Levels
     * @author B.Schroeder
     * @version 1.0
     */
public class Level {
    /**
     * List of already prepositioned Gameobjects
     */
    private List<GameObject> levelObjects;
    /**
     * List of Items in Inventory (to be placed by the Player)
     */
    private List<InventoryObject> inventory;

    public Level() {}
    
    /**
     * Getter for levelObjects
     * @return List of placed GameObjects
     */
    public List<GameObject> getLevelObjects() {return levelObjects;}
    /**
     * Setter for levelObjects
     * @param objects The List of Objects as levelObjects
     */
    public void setLevelObjects(List<GameObject> objects) {this.levelObjects = objects;}
    
    /**
     * Getter for inventory
     * @return List of Inventory Items
     */
    public List<InventoryObject> getInventoryObject() {return inventory;}
    /**
     * Setter for inventory
     * @param inventory The List of Items as inventory
     */
    public void setInventory(List<InventoryObject> inventory) {this.inventory = inventory;}

}