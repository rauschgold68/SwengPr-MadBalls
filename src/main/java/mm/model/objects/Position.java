package mm.model.objects;

/**
 * Representing the position of an object in a level.
 * <p>
 * Stores the x and y coordinates as floats. Used for specifying the location of
 * game objects and other entities within the simulation or level.
 * </p>
 * <ul>
 *   <li><b>x</b>: The horizontal coordinate.</li>
 *   <li><b>y</b>: The vertical coordinate.</li>
 * </ul>
 * 
 * Example usage:
 * <pre>
 *     Position pos = new Position(100.0f, 200.0f);
 *     float x = pos.getX();
 *     pos.setY(150.0f);
 * </pre>
 */
public class Position {
    /** The x-coordinate of the position. */
    private float x;
    /** The y-coordinate of the position. */
    private float y;

    /**
     * Constructs a Position with specified x and y coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Position(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a Position at the origin (0, 0).
     * This is the default constructor.
     */
    public Position(){
        this.x = 0;
        this.y = 0;
    }

    /**
     * Returns the x-coordinate.
     * 
     * @return the x-coordinate as a float
     */
    public float getX() {return this.x;}

    /**
     * Sets the x-coordinate.
     * 
     * @param newX the new x-coordinate to set
     */
    public void setX(float newX) {this.x = newX;}

    /**
     * Returns the y-coordinate.
     * 
     * @return the y-coordinate as a float
     */
    public float getY() {return this.y;}

    /**
     * Sets the y-coordinate.
     * 
     * @param newY the new y-coordinate to set
     */
    public void setY(float newY) {this.y = newY;}

}