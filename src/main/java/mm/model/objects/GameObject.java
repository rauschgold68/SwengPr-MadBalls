package mm.model.objects;

/**
 * Represents a game object that can be placed inside a level.
 * <p>
 * Each {@code GameObject} has a unique name, type, position, size, optional sprite, colour, and physics properties.
 * This class serves as a base for all objects that can be manipulated within the game world.
 * </p>
 * 
 * <ul>
 *   <li><b>Name:</b> Unique identifier for the object.</li>
 *   <li><b>Type:</b> JavaFX type or category of the object.</li>
 *   <li><b>Position:</b> Location of the object in the level.</li>
 *   <li><b>Angle:</b> Rotation of the object in degrees.</li>
 *   <li><b>Size:</b> Dimensions (width, height) of the object.</li>
 *   <li><b>Sprite:</b> Optional graphical representation.</li>
 *   <li><b>Colour:</b> Colour used if no sprite is set.</li>
 *   <li><b>Physics:</b> Physics properties for simulation.</li>
 * </ul>
 * 
 */
public class GameObject {
    /** The name of the placed object (unique identifier). */
    private String name;
    /** The type in JavaFX (e.g., Rectangle, Circle, etc.). */
    private String type;
    /** The position of the object in the level. */
    private Position position;
    /** The rotation of the object in the level, in degrees. */
    private float angle;
    /** The size of the object (width, height). */
    private Size size;
    /** The sprite used for complex graphics (may be null). */
    private String sprite;
    /** The colour of the object (only visible if the object doesn't use a sprite). */
    private String colour;
    /** The physics object for jBox2d, containing simulation properties. */
    private Physics physics;

    /**
     * Default constructor.
     * <p>
     * Creates a new {@code GameObject} with default values. All fields are initialized to {@code null} or zero.
     * </p>
     */
    public GameObject() {}

    /**
     * Constructs a {@code GameObject} with the specified attributes.
     * <p>
     * The sprite may be set manually after creation.
     * </p>
     *
     * @param name     the unique identifier for the object (must not be {@code null})
     * @param type     the JavaFX type or category (must not be {@code null})
     * @param position the position where to place the object (must not be {@code null})
     * @param angle    the initial rotation of the object in degrees
     * @param size     the size (width, height) of the object (must not be {@code null})
     * @param colour   the colour of the object (may be {@code null} if sprite is used)
     * @param physics  the jBox2d physics information (may be {@code null} if not simulated)
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
     * Returns the position of the object.
     *
     * @return the position of the object
     */
    public Position getPosition() {return this.position;}

    /**
     * Sets the position of the object.
     *
     * @param newPosition the new position to set (must not be {@code null})
     */
    public void setPosition(Position newPosition) {this.position = newPosition;}

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

}