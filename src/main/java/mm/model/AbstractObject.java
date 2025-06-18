package mm.model;

public class AbstractObject {
    /** name of the object */
    private String name;
    /** The type of the object */
    private String type;
    /** The position of the object in the level. */
    private Size size;
    /** The angle of the object */
    private float angle;
    /** The sprite used for complex graphics (may be null). */
    private String sprite;
    /** The colour of the object (only visible if the object doesn't use a sprite). */
    private String colour;
    /** The physics object for jBox2d, containing simulation properties. */
    private Physics physics;
    /** If set object triggers Win condition */
    private boolean winning;

    /**
     * Default constructor.
     * <p>
     * Creates a new {@code AbstractObject} with default values. All fields are initialized to {@code null} or zero.
     * </p>
     */
    public AbstractObject() {}

    /**
     * Constructs a {@code GameObject} with the specified attributes.
     * <p>
     * The sprite may be set manually after creation.
     * </p>
     *
     * @param name     the unique identifier for the object (must not be {@code null})
     * @param type     the JavaFX type or category (must not be {@code null})
     * @param angle    the initial rotation of the object in degrees
     * @param size     the size (width, height) of the object (must not be {@code null})
     * @param colour   the colour of the object (may be {@code null} if sprite is used)
     * @param physics  the jBox2d physics information (may be {@code null} if not simulated)
     * @param winning  tells if object is win condition
     */
    public AbstractObject(String name, String type, float angle, Size size, String colour, Physics physics, boolean winning) {
        this.name = name; 
        this.type = type;
        this.angle = angle;
        this.size = size;
        this.colour = colour;
        this.physics = physics;
        this.winning = winning;
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
     * Returns the position of the object.
     *
     * @return the position of the object
     */
    public boolean isWinning() {return this.winning;}

    /**
     * Sets the position of the object.
     *
     * @param newPosition the new position to set (must not be {@code null})
     */
    public void setWinning(boolean winning) {this.winning = winning;}

} 