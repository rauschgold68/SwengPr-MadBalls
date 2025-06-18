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
    private int count;

    /** 
     * Default constructor.
     * <p>
     * Creates a new {@code InventoryObject} with default values. All fields are initialized to {@code null}, zero, or their default values.
     * </p>
     */
    public InventoryObject() {}

    /**
     * Constructs an {@code InventoryObject} with the specified attributes.
     * <p>
     * The sprite may be set manually after creation.
     * <b>Do NOT create new {@code InventoryObject} for similar items:</b>
     * if the attributes are the same, just increment the count.
     * </p>
     *
     * @param name    the name of the item (must not be {@code null})
     * @param type    the type/category of the item (must not be {@code null})
     * @param count   the count of this item (should be &gt;= 1)
     * @param angle   the angle of the object before placing, in degrees
     * @param size    the size (width, height) of the object (must not be {@code null})
     * @param colour  the colour of the item (may be {@code null} if sprite is used)
     * @param physics the physics information (may be {@code null} if not simulated)
     */
    public InventoryObject(String name, String type, int count, float angle, Size size, String colour, Physics physics, boolean winning) {
        super(name, type, angle, size, colour, physics, winning);
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