package mm.model.objects;

/**
 * Represents an item that can be stored in an inventory.
 * <p>
 * Each InventoryObject has a name, type, count, size, sprite, colour, physics properties, and a radius.
 * </p>
 */
public class InventoryObject {
    /** Name of the inventory object */
    private String name;
    /** Type/category of the object (e.g., weapon, consumable) */
    private String type;
    /** Number of this item in the inventory */
    private int count;
    /** initial angle of object in degrees */
    private float angle;
    /** Size of the object (width, height) */
    private Size size;
    /** Path or identifier for the object's sprite image */
    private String sprite;
    /** Colour of the object (could be a string representation) */
    private String colour;
    /** Physics properties for the object (e.g., for simulation) */
    private Physics physics;
    /** Radius for circular objects (used for placement or collision) */
    private float radius;

    /** 
     * Default constructor.
     */
    public InventoryObject() {}

    /**
     * Constructs an InventoryObject with the specified attributes.
     * <p>
     * Sprite may be set manually after creation.
     * Do NOT create new InventoryObject for similar objects:
     * if the attributes are the same, just increment count.
     * </p>
     *
     * @param name the name of the item
     * @param type the type/category of the item
     * @param count the count of this item
     * @param angle the angle of object before placing 
     * @param size the size (width, height)
     * @param colour the colour of the item
     * @param physics the physics information (e.g., for jBox2d)
     * @param radius the radius for circular shapes
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
     * @return the type of the object
     */
    public String getType() {return this.type;}

    /**
     * Returns the name of the object.
     * @return the name of the object
     */
    public String getName() {return name;}

    /**
     * Sets the name of the object.
     * @param newName the new name to set
     */
    public void setName(String newName) {this.name = newName;}

    /**
     * Returns the count of this item.
     * @return the count of the item
     */
    public int getCount() {return this.count;}

    /**
     * Sets the count of this item.
     * @param newCount the new count to set
     */
    public void setCount(int newCount) {this.count = newCount;}
    
    /**
     * Gets the inital angle of the object 
     * @return the angle of the object
     */
    public float getAngle() {return this.angle;}

    /**
     * Sets the angle of the placeable object
     * @param newAngle the new angle of the object
     */

    /**
     * Returns the size of the object.
     * @return the size of the object
     */
    public Size getSize() {return this.size;}

    /**
     * Sets the size of the object.
     * @param newSize the new size to set
     */
    public void setSize(Size newSize) {this.size = newSize;}

    /**
     * Returns the sprite identifier/path.
     * @return the sprite identifier or path
     */
    public String getSprite() {return this.sprite;}

    /**
     * Sets the sprite identifier/path.
     * @param newSprite the new sprite identifier or path
     */
    public void setSprite(String newSprite) {this.sprite = newSprite;}

    /**
     * Returns the colour of the object.
     * @return the colour of the object
     */
    public String getColour() {return this.colour;}

    /**
     * Sets the colour of the object.
     * @param newColour the new colour to set
     */
    public void setColor(String newColour) {this.colour = newColour;}

    /**
     * Returns the physics properties of the object.
     * @return the physics properties
     */
    public Physics getPhysics() {return this.physics;}

    /**
     * Sets the physics properties of the object.
     * @param newPhysics the new physics properties to set
     */
    public void setPhysics(Physics newPhysics) {this.physics = newPhysics;}

    /**
     * Returns the radius (for circular objects).
     * @return the radius of the object
     */
    public float getRadius() {return this.radius;}

    /**
     * Sets the radius (for circular objects).
     * @param newRadius the new radius to set
     */
    public void setRadius(float newRadius) {this.radius = newRadius;}

}