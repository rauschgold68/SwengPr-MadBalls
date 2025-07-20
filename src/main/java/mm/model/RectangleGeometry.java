package mm.model;

/**
 * Geometry data for rectangular shapes.
 * Contains only the mathematical representation without UI dependencies.
 */
public class RectangleGeometry extends GeometryData {
    private final double width;
    private final double height;
    
    public RectangleGeometry(Position position, double width, double height, double rotation) {
        super(position, rotation);
        this.width = width;
        this.height = height;
    }
    
    public RectangleGeometry(Position position, double width, double height) {
        this(position, width, height, 0.0);
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
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
