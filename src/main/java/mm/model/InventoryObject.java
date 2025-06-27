package mm.model;

/**
 * Represents an item that can be stored in an inventory.
 * <p>
 * Each {@code InventoryObject} has a name, type, count, angle, size, optional sprite, colour, physics properties, and a radius.
 * This class is used for objects that can be collected, stacked, or placed from an inventory system.
 * </p>
 * <ul>
 *   <li><b>Name:</b> The name of the inventory item.</li>
 *   <li><b>Type:</b> The category or type of the item (e.g., weapon, consumable).</li>
 *   <li><b>Count:</b> The number of this item in the inventory.</li>
 *   <li><b>Angle:</b> The initial rotation of the object in degrees before placement.</li>
 *   <li><b>Size:</b> The dimensions (width, height) of the object.</li>
 *   <li><b>Sprite:</b> Optional graphical representation (image path or identifier).</li>
 *   <li><b>Colour:</b> Colour used if no sprite is set.</li>
 *   <li><b>Physics:</b> Physics properties for simulation (e.g., jBox2d).</li>
 *   <li><b>Radius:</b> The radius for circular objects (used for placement or collision).</li>
 * </ul>
 * <p>
 * <b>Note:</b> Do not create new {@code InventoryObject} instances for identical items; instead, increment the count.
 * </p>
 */
public class InventoryObject extends AbstractObject {
    /** The count/quantity of this item in inventory */
    private int count;

    /** 
     * Default constructor.
     * Creates a new InventoryObject with default values.
     */
    public InventoryObject() {
        super();
        this.count = 0;
    }

    /**
     * Constructs an InventoryObject with basic properties.
     * Additional properties can be set using setter methods.
     *
     * @param name the name of the item
     * @param type the type/category of the item
     * @param size the size (width, height) of the object
     */
    public InventoryObject(String name, String type, Size size) {
        super(name, type, size);
        this.count = 1; // Default count for new inventory items
    }

    /**
     * Constructs an InventoryObject with a specific count.
     *
     * @param name the name of the item
     * @param type the type/category of the item
     * @param size the size (width, height) of the object
     * @param count the count of this item (should be >= 1)
     */
    public InventoryObject(String name, String type, Size size, int count) {
        super(name, type, size);
        this.count = count;
    }
    /**
     * Returns the count of this item.
     * 
     * @return the count of the item
     */
    public int getCount() {return this.count;}

    /**
     * Sets the count of this item.
     * 
     * @param newCount the new count to set
     */
    public void setCount(int newCount) {this.count = newCount;}

}