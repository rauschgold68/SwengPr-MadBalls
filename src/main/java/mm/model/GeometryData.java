package mm.model;

/**
 * Abstract base class for geometry data that represents visual objects
 * without depending on any specific UI framework.
 * This allows the model to work with geometric shapes while maintaining
 * separation from JavaFX or other view technologies.
 * 
 * <p>This class provides the foundation for all geometric shapes in the system,
 * including their position and rotation properties. Concrete implementations
 * should define specific geometric operations like point containment and
 * bounding box calculations.</p>
 * 
 * @author MadBalls Team
 * @version 1.0
 * @since 1.0
 */
public abstract class GeometryData {
    /** The position of this geometry in 2D space */
    protected final Position position;
    
    /** The rotation angle of this geometry in radians */
    protected final double rotation;
    
    /**
     * Constructs a new GeometryData with the specified position and rotation.
     * 
     * @param position the position of this geometry in 2D space, must not be null
     * @param rotation the rotation angle in radians
     * @throws IllegalArgumentException if position is null
     */
    protected GeometryData(Position position, double rotation) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.position = position;
        this.rotation = rotation;
    }
    
    /**
     * Gets the position of this geometry.
     * 
     * @return the position of this geometry in 2D space, never null
     */
    public Position getPosition() {
        return position;
    }
    
    /**
     * Gets the rotation angle of this geometry.
     * 
     * @return the rotation angle in radians
     */
    public double getRotation() {
        return rotation;
    }
    
    /**
     * Check if a point is inside this geometry.
     * 
     * <p>This method determines whether the specified point lies within
     * the boundaries of this geometric shape, taking into account the
     * geometry's position and rotation.</p>
     * 
     * @param x the x coordinate of the point to test
     * @param y the y coordinate of the point to test
     * @return true if the point (x, y) is inside this geometry, false otherwise
     */
    public abstract boolean containsPoint(double x, double y);
    
    /**
     * Get the axis-aligned bounding box of this geometry.
     * 
     * <p>Returns the smallest axis-aligned rectangle that completely
     * contains this geometry. The bounding box takes into account the
     * geometry's position and rotation.</p>
     * 
     * @return an array of four doubles representing the bounding box
     *         in the format [minX, minY, maxX, maxY], never null
     */
    public abstract double[] getBounds();
}
