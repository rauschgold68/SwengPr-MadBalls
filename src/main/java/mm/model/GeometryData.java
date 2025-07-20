package mm.model;

/**
 * Abstract base class for geometry data that represents visual objects
 * without depending on any specific UI framework.
 * This allows the model to work with geometric shapes while maintaining
 * separation from JavaFX or other view technologies.
 */
public abstract class GeometryData {
    protected final Position position;
    protected final double rotation;
    
    protected GeometryData(Position position, double rotation) {
        this.position = position;
        this.rotation = rotation;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public double getRotation() {
        return rotation;
    }
    
    /**
     * Check if a point is inside this geometry.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the point is inside this geometry
     */
    public abstract boolean containsPoint(double x, double y);
    
    /**
     * Get the bounding box of this geometry.
     * @return the bounding box as [minX, minY, maxX, maxY]
     */
    public abstract double[] getBounds();
}
