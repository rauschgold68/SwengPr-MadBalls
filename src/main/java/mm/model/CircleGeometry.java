package mm.model;

/**
 * Geometry data for circular shapes.
 * Contains only the mathematical representation without UI dependencies.
 */
public class CircleGeometry extends GeometryData {
    private final double radius;
    
    public CircleGeometry(Position position, double radius, double rotation) {
        super(position, rotation);
        this.radius = radius;
    }
    
    public CircleGeometry(Position position, double radius) {
        this(position, radius, 0.0);
    }
    
    public double getRadius() {
        return radius;
    }
    
    @Override
    public boolean containsPoint(double x, double y) {
        double centerX = position.getX() + radius;
        double centerY = position.getY() + radius;
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        return distance <= radius;
    }
    
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
