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
     * Creates a new GameObject with default values.
     */
    public GameObject() {
        super();
    }

    /**
     * Constructs a GameObject with basic properties.
     *
     * @param name the unique identifier for the object
     * @param type the type or category of the object
     * @param position the position where to place the object
     * @param size the size (width, height) of the object
     */
    public GameObject(String name, String type, Position position, Size size) {
        super(name, type, size);
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