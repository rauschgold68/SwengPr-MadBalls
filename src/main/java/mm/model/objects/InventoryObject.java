package mm.model.objects;

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
public class InventoryObject {
    /** Name of the inventory object (unique identifier for the item). */
    private String name;
    /** Type/category of the object (e.g., weapon, consumable, etc.). */
    private String type;
    /** Number of this item in the inventory. */
    private int count;
    /** Initial angle of the object in degrees before placement. */
    private float angle;
    /** Size of the object (width, height). */
    private Size size;
    /** Path or identifier for the object's sprite image (may be null). */
    private String sprite;
    /** Colour of the object (used if no sprite is set, may be null). */
    private String colour;
    /** Physics properties for the object (e.g., for simulation, may be null). */
    private Physics physics;
    /** Radius for circular objects (used for placement or collision). */
    private float radius;

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
     * @param radius  the radius for circular shapes (used for placement/collision)
     */
    public InventoryObject(String name, String type, int count, float angle, Size size, String colour, Physics physics, float radius) {
        this.name = name;
        this.type = type;
        this.count = count;
        this.angle = angle;
        this.size = size;
        this.colour = colour;
        this.physics = physics;
        this.radius = radius;
        this.count = count;
    }

    /**
     * Returns the type/category of the object.
     * 
     * @return the type of the object
     */
    public String getType() {return this.type;}

    /**
     * Returns the name of the object.
     * 
     * @return the name of the object
     */
    public String getName() {return name;}

    /**
     * Sets the name of the object.
     * 
     * @param newName the new name to set
     */
    public void setName(String newName) {this.name = newName;}

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
    
    /**
     * Returns the initial angle of the object before placement.
     * 
     * @return the angle of the object in degrees
     */
    public float getAngle() {return this.angle;}

    /**
     * Sets the angle of the placeable object.
     * 
     * @param newAngle the new angle of the object in degrees
     */
    public void setAngle(float newAngle) {this.angle = newAngle;}

    /**
     * Returns the size of the object.
     * 
     * @return the size of the object (width, height)
     */
    public Size getSize() {return this.size;}

    /**
     * Sets the size of the object.
     * 
     * @param newSize the new size to set (must not be {@code null})
     */
    public void setSize(Size newSize) {this.size = newSize;}

    /**
     * Returns the sprite identifier or path for the object.
     * 
     * @return the sprite identifier or path, or {@code null} if not set
     */
    public String getSprite() {return this.sprite;}

    /**
     * Sets the sprite identifier or path for the object.
     * 
     * @param newSprite the new sprite identifier or path (may be {@code null})
     */
    public void setSprite(String newSprite) {this.sprite = newSprite;}

    /**
     * Returns the colour of the object.
     * 
     * @return the colour of the object, or {@code null} if not set
     */
    public String getColour() {return this.colour;}

    /**
     * Sets the colour of the object.
     * 
     * @param newColour the new colour to set (may be {@code null})
     */
    public void setColor(String newColour) {this.colour = newColour;}

    /**
     * Returns the physics properties of the object.
     * 
     * @return the physics properties, or {@code null} if not set
     */
    public Physics getPhysics() {return this.physics;}

    /**
     * Sets the physics properties of the object.
     * 
     * @param newPhysics the new physics properties to set (may be {@code null})
     */
    public void setPhysics(Physics newPhysics) {this.physics = newPhysics;}

    /**
     * Returns the radius (for circular objects).
     * 
     * @return the radius of the object
     */
    public float getRadius() {return this.radius;}

    /**
     * Sets the radius (for circular objects).
     * 
     * @param newRadius the new radius to set
     */
    public void setRadius(float newRadius) {this.radius = newRadius;}

}