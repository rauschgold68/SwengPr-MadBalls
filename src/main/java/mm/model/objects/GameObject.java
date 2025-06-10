package mm.model.objects;

/**
 * Represents a game object that can be placed inside a level.
 * <p>
 * Each GameObject has a unique name, type, position, size, optional sprite, colour, and physics properties.
 * </p>
 * 
 * @author B.Schroeder
 * @version 0.5
 */
public class GameObject {
    /** The name of the placed object (unique identifier) */
    private String name;
    /** The type in JavaFX */
    private String type;
    /** The position of the object in the level */
    private Position position;
    /** The rotation of the object in level in Degrees */
    private float angle;
    /** The size in (width, height) */
    private Size size;
    /** The sprite used for complex graphics */
    private String sprite;
    /** The colour of the object (only visible if the object doesn't use a sprite) */
    private String colour;
    /** The physics object for jBox2d */
    private Physics physics;

    /**
     * Default constructor.
     */
    public GameObject() {}

    /**
     * Constructs a GameObject with the specified attributes.
     * <p>
     * The sprite may be set manually after creation.
     * </p>
     * 
     * @param name unique identifier for the object
     * @param type the JavaFX type
     * @param position the position where to place the object
     * @param angle the initial rotation of the object in Degrees
     * @param size the size (width, height)
     * @param colour the colour of the object
     * @param physics the jBox2d physics information
     */
    public GameObject(String name, String type, Position position, float angle, Size size, String colour, Physics physics) {
        this.name = name; 
        this.type = type;
        this.position = position;
        this.angle = angle;
        this.size = size;
        this.colour = colour;
        this.physics = physics;
    }

    /**
     * Returns the unique name of the object.
     * @return the name of the object
     */
    public String getName() {return this.name;}
    // No setName() because name is a unique identifier and shouldn't be changed

    /**
     * Returns the type of the object.
     * @return the type of the object
     */
    public String getType() {return this.type;}
    // No setType() because if type changes, a new unique object should be made

    /**
     * Returns the position of the object.
     * @return the position of the object
     */
    public Position getPosition() {return this.position;}

    /**
     * Sets the position of the object.
     * @param newPosition the new position to set
     */
    public void setPosition(Position newPosition) {this.position = newPosition;}

    /**
     * Gets angle of Object
     * @return angle of object
     */
    public float getAngle() {return this.angle;}

    /**
     * Sets the angle of object
     * @param newAngle the new angle to set  
     */
    public void setAngle(float newAngle) {this.angle = newAngle;}
    
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

}