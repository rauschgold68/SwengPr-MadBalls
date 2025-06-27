package mm.model;

/**
 * Abstract base class representing a game object in the MadBalls simulation.
 * <p>
 * This class provides common properties and functionality for all objects that can
 * exist in the game world, including position, size, appearance, and physics properties.
 * </p>
 * 
 * @author MadBalls Development Team
 * @version 1.0
 * @since 1.0
 */
public class AbstractObject {
    /** The unique name identifier of the object */
    private String name;
    /** The type or category of the object */
    private String type;
    /** The size (width, height) of the object */
    private Size size;
    /** The rotation angle of the object in degrees */
    private float angle;
    /** The sprite identifier or path for complex graphics (may be null) */
    private String sprite;
    /** The colour of the object (only visible if no sprite is used) */
    private String colour;
    /** The physics properties for jBox2d simulation */
    private Physics physics;
    /** Whether this object triggers the win condition */
    private boolean winning;

    /**
     * Default constructor.
     * Creates a new AbstractObject with default values.
     */
    public AbstractObject() {}

    /**
     * Constructs an AbstractObject with basic properties.
     * Additional properties can be set using setter methods.
     *
     * @param name the unique identifier for the object
     * @param type the type or category of the object
     * @param size the size (width, height) of the object
     */
    public AbstractObject(String name, String type, Size size) {
        this.name = name; 
        this.type = type;
        this.size = size;
        this.angle = 0.0f;
        this.winning = false;
    }

    /**
     * Constructs an AbstractObject with essential properties.
     * 
     * @param name the unique identifier for the object
     * @param type the type or category of the object
     * @param size the size (width, height) of the object
     * @param physics the physics properties for simulation
     */
    public AbstractObject(String name, String type, Size size, Physics physics) {
        this(name, type, size);
        this.physics = physics;
    }

    /**
     * Returns the unique name of the object.
     *
     * @return the name of the object
     */
    public String getName() {return this.name;}
    // No setName() because name is a unique identifier and shouldn't be changed

    /**
     * Returns the type of the object.
     *
     * @return the type of the object
     */
    public String getType() {return this.type;}
    // No setType() because if type changes, a new unique object should be made

    /**
     * Returns the angle (rotation) of the object in degrees.
     *
     * @return the angle of the object in degrees
     */
    public float getAngle() {return this.angle;}

    /**
     * Sets the angle (rotation) of the object in degrees.
     *
     * @param newAngle the new angle to set, in degrees
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
    public void setColour(String newColour) {this.colour = newColour;}

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
     * Returns whether this object triggers the win condition.
     *
     * @return true if this object triggers win condition, false otherwise
     */
    public boolean isWinning() {return this.winning;}

    /**
     * Sets whether this object triggers the win condition.
     *
     * @param winning true if this object should trigger win condition
     */
    public void setWinning(boolean winning) {this.winning = winning;}

} 