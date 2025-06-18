package mm.model;

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
public class GameObject extends AbstractObject {
    /** The position of the object in the level. */
    private Position position;

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
     * @param winning  tells if object is win condition
     */
    public GameObject(String name, String type, Position position, float angle, Size size, String colour, Physics physics, boolean winning) {
        super(name, type, angle, size, colour, physics, winning);
        this.position = position;
    }

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

}