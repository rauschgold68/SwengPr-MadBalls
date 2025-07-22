package mm.model;

/**
 * Geometry data for circular shapes.
 * <p>
 * This class represents the mathematical properties of a circle, including its position,
 * radius, and rotation. It provides methods for geometric calculations such as point
 * containment testing and bounding box computation.
 * </p>
 * <p>
 * The circle is positioned using its top-left corner coordinate system, where the center
 * of the circle is calculated as (position.x + radius, position.y + radius).
 * </p>
 * <p>
 * Contains only the mathematical representation without UI dependencies, making it
 * suitable for use in physics calculations and collision detection.
 * </p>
 * 
 * @author MadBalls Team
 * @version 1.0
 * @since 1.0
 */
public class CircleGeometry extends GeometryData {
    /** The radius of the circle in units */
    private final double radius;
    
    /**
     * Constructs a new CircleGeometry with the specified position, radius, and rotation.
     * <p>
     * The position represents the top-left corner of the bounding square that would
     * contain this circle. The actual center of the circle is at (position.x + radius,
     * position.y + radius).
     * </p>
     *
     * @param position the position of the circle's top-left bounding corner, must not be null
     * @param radius the radius of the circle, must be positive
     * @param rotation the rotation angle of the circle in degrees (note: rotation doesn't
     *                 affect circular geometry calculations but may be used for rendering)
     * @throws IllegalArgumentException if radius is negative or zero
     * @throws NullPointerException if position is null
     */
    public CircleGeometry(Position position, double radius, double rotation) {
        super(position, rotation);
        this.radius = radius;
    }
    
    /**
     * Constructs a new CircleGeometry with the specified position and radius, with zero rotation.
     * <p>
     * This is a convenience constructor that defaults the rotation to 0.0 degrees.
     * The position represents the top-left corner of the bounding square that would
     * contain this circle.
     * </p>
     *
     * @param position the position of the circle's top-left bounding corner, must not be null
     * @param radius the radius of the circle, must be positive
     * @throws IllegalArgumentException if radius is negative or zero
     * @throws NullPointerException if position is null
     */
    public CircleGeometry(Position position, double radius) {
        this(position, radius, 0.0);
    }
    
    /**
     * Returns the radius of this circle.
     *
     * @return the radius of the circle in units, always positive
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * Tests whether a given point lies within this circle.
     * <p>
     * This method calculates the Euclidean distance from the given point to the center
     * of the circle and compares it with the radius. A point is considered inside the
     * circle if its distance from the center is less than or equal to the radius.
     * </p>
     * <p>
     * The circle's center is calculated as (position.x + radius, position.y + radius)
     * based on the top-left positioning system used by this class.
     * </p>
     *
     * @param x the x-coordinate of the point to test
     * @param y the y-coordinate of the point to test
     * @return {@code true} if the point (x, y) is inside or on the boundary of the circle,
     *         {@code false} otherwise
     */
    @Override
    public boolean containsPoint(double x, double y) {
        double centerX = position.getX() + radius;
        double centerY = position.getY() + radius;
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        return distance <= radius;
    }
    
    /**
     * Returns the axis-aligned bounding box of this circle.
     * <p>
     * The bounding box is the smallest rectangle that completely contains the circle,
     * with sides parallel to the coordinate axes. This is useful for broad-phase
     * collision detection and spatial partitioning algorithms.
     * </p>
     * <p>
     * The returned array contains four values representing the bounding rectangle:
     * <ul>
     *   <li>bounds[0] = minimum x-coordinate (left edge)</li>
     *   <li>bounds[1] = minimum y-coordinate (top edge)</li>
     *   <li>bounds[2] = maximum x-coordinate (right edge)</li>
     *   <li>bounds[3] = maximum y-coordinate (bottom edge)</li>
     * </ul>
     * </p>
     *
     * @return a four-element double array containing [minX, minY, maxX, maxY] of the
     *         bounding rectangle that encloses this circle
     */
    @Override
    public double[] getBounds() {
        double centerX = position.getX() + radius;
        double centerY = position.getY() + radius;
        return new double[]{
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        };
    }
}
