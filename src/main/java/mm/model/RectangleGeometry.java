package mm.model;

/**
 * Geometry data for rectangular shapes.
 * 
 * <p>This class represents a rectangle in 2D space with support for rotation.
 * It provides mathematical operations for point containment testing and 
 * bounding box calculations. The rectangle is defined by its position 
 * (top-left corner), width, height, and optional rotation angle.</p>
 * 
 * <p>The class handles both axis-aligned rectangles (no rotation) and 
 * rotated rectangles efficiently, using optimized algorithms for each case.
 * Contains only the mathematical representation without UI dependencies.</p>
 * 
 * @author MadBalls Team
 * @version 1.0
 * @since 1.0
 * @see GeometryData
 */
public class RectangleGeometry extends GeometryData {
    /** The width of the rectangle */
    private final double width;
    
    /** The height of the rectangle */
    private final double height;
    
    /**
     * Constructs a new RectangleGeometry with the specified dimensions, position, and rotation.
     * 
     * @param position the position of the rectangle's top-left corner, must not be null
     * @param width the width of the rectangle, must be positive
     * @param height the height of the rectangle, must be positive
     * @param rotation the rotation angle in degrees (clockwise)
     * @throws IllegalArgumentException if position is null, or if width or height is not positive
     */
    public RectangleGeometry(Position position, double width, double height, double rotation) {
        super(position, rotation);
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        this.width = width;
        this.height = height;
    }
    
    /**
     * Constructs a new RectangleGeometry with the specified dimensions and position.
     * The rectangle will have no rotation (0 degrees).
     * 
     * @param position the position of the rectangle's top-left corner, must not be null
     * @param width the width of the rectangle, must be positive
     * @param height the height of the rectangle, must be positive
     * @throws IllegalArgumentException if position is null, or if width or height is not positive
     */
    public RectangleGeometry(Position position, double width, double height) {
        this(position, width, height, 0.0);
    }
    
    /**
     * Gets the width of the rectangle.
     * 
     * @return the width of the rectangle in pixels or units
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the rectangle.
     * 
     * @return the height of the rectangle in pixels or units
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>For rectangles, this method efficiently handles both axis-aligned and 
     * rotated cases. For axis-aligned rectangles (rotation = 0), it uses simple
     * boundary checks. For rotated rectangles, it transforms the test point to
     * the rectangle's local coordinate system and then performs the containment test.</p>
     * 
     * The algorithm works by:
     * <ol>
     * <li>If no rotation: simple axis-aligned bounding box test</li>
     * <li>If rotated: transform point to local coordinates by inverse rotation,
     *     then test against unrotated rectangle bounds</li>
     * </ol>
     * 
     * @param x the x coordinate of the point to test
     * @param y the y coordinate of the point to test
     * @return true if the point is inside the rectangle (including on edges), false otherwise
     */
    @Override
    public boolean containsPoint(double x, double y) {
        if (rotation == 0.0) {
            // Simple case: no rotation
            double minX = position.getX();
            double minY = position.getY();
            double maxX = minX + width;
            double maxY = minY + height;
            return x >= minX && x <= maxX && y >= minY && y <= maxY;
        } else {
            // Handle rotation by transforming the point to local coordinates
            double centerX = position.getX() + width / 2;
            double centerY = position.getY() + height / 2;
            
            // Translate point to center
            double translatedX = x - centerX;
            double translatedY = y - centerY;
            
            // Rotate point by negative rotation angle
            double cos = Math.cos(-Math.toRadians(rotation));
            double sin = Math.sin(-Math.toRadians(rotation));
            double rotatedX = translatedX * cos - translatedY * sin;
            double rotatedY = translatedX * sin + translatedY * cos;
            
            // Check if rotated point is in unrotated rectangle
            return Math.abs(rotatedX) <= width / 2 && Math.abs(rotatedY) <= height / 2;
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>For rectangles, this method calculates the axis-aligned bounding box
     * that completely contains the rectangle. For non-rotated rectangles, this
     * is simply the rectangle itself. For rotated rectangles, it calculates the
     * positions of all four corners after rotation and finds the minimum and
     * maximum x and y coordinates.</p>
     * 
     * The algorithm works by:
     * <ol>
     * <li>If no rotation: return the rectangle's position and dimensions directly</li>
     * <li>If rotated: calculate all four corner positions after rotation,
     *     then find the min/max bounds that contain all corners</li>
     * </ol>
     * 
     * @return an array of four doubles [minX, minY, maxX, maxY] representing
     *         the smallest axis-aligned rectangle that contains this geometry
     */
    @Override
    public double[] getBounds() {
        if (rotation == 0.0) {
            return new double[]{
                position.getX(),
                position.getY(),
                position.getX() + width,
                position.getY() + height
            };
        } else {
            // Calculate rotated bounds
            double centerX = position.getX() + width / 2;
            double centerY = position.getY() + height / 2;
            double cos = Math.cos(Math.toRadians(rotation));
            double sin = Math.sin(Math.toRadians(rotation));
            
            // Calculate the four corners after rotation
            double[] cornersX = new double[4];
            double[] cornersY = new double[4];
            
            double halfW = width / 2;
            double halfH = height / 2;
            
            cornersX[0] = centerX + (-halfW * cos - -halfH * sin);
            cornersY[0] = centerY + (-halfW * sin + -halfH * cos);
            
            cornersX[1] = centerX + (halfW * cos - -halfH * sin);
            cornersY[1] = centerY + (halfW * sin + -halfH * cos);
            
            cornersX[2] = centerX + (halfW * cos - halfH * sin);
            cornersY[2] = centerY + (halfW * sin + halfH * cos);
            
            cornersX[3] = centerX + (-halfW * cos - halfH * sin);
            cornersY[3] = centerY + (-halfW * sin + halfH * cos);
            
            double minX = Math.min(Math.min(cornersX[0], cornersX[1]), Math.min(cornersX[2], cornersX[3]));
            double maxX = Math.max(Math.max(cornersX[0], cornersX[1]), Math.max(cornersX[2], cornersX[3]));
            double minY = Math.min(Math.min(cornersY[0], cornersY[1]), Math.min(cornersY[2], cornersY[3]));
            double maxY = Math.max(Math.max(cornersY[0], cornersY[1]), Math.max(cornersY[2], cornersY[3]));
            
            return new double[]{minX, minY, maxX, maxY};
        }
    }
}
