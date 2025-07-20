package mm.controller;

import javafx.scene.shape.*;
import mm.model.*;

/**
 * Utility class for converting between JavaFX shapes and geometry data.
 * This class helps bridge the gap between the view layer (JavaFX) and
 * the model layer (view-agnostic geometry).
 */
public class GeometryConverter {
    
    /**
     * Converts a JavaFX Shape to geometry data.
     * 
     * @param shape the JavaFX shape to convert
     * @return the corresponding geometry data, or null if conversion is not supported
     */
    public static GeometryData fromJavaFXShape(Shape shape) {
        if (shape instanceof Rectangle) {
            return fromRectangle((Rectangle) shape);
        } else if (shape instanceof Circle) {
            return fromCircle((Circle) shape);
        }
        return null;
    }
    
    /**
     * Converts a JavaFX Rectangle to RectangleGeometry.
     */
    private static RectangleGeometry fromRectangle(Rectangle rect) {
        Position position = new Position((float) rect.getTranslateX(), (float) rect.getTranslateY());
        return new RectangleGeometry(position, rect.getWidth(), rect.getHeight(), rect.getRotate());
    }
    
    /**
     * Converts a JavaFX Circle to CircleGeometry.
     */
    private static CircleGeometry fromCircle(Circle circle) {
        // Adjust position since Circle's translate is for the center, but our geometry expects top-left
        float posX = (float) (circle.getTranslateX() - circle.getRadius());
        float posY = (float) (circle.getTranslateY() - circle.getRadius());
        Position position = new Position(posX, posY);
        return new CircleGeometry(position, circle.getRadius(), circle.getRotate());
    }
    
    /**
     * Converts a PhysicsVisualPair to a PhysicsGeometryPair.
     * 
     * @param visualPair the JavaFX-based pair to convert
     * @return the geometry-based pair
     */
    public static PhysicsGeometryPair fromVisualPair(PhysicsVisualPair visualPair) {
        GeometryData geometry = null;
        if (visualPair.visual != null) {
            geometry = fromJavaFXShape(visualPair.visual);
        }
        return new PhysicsGeometryPair(geometry, visualPair.body);
    }
    
    /**
     * Updates geometry data from a JavaFX shape's current position and rotation.
     * This is useful for synchronizing geometry data with animated JavaFX shapes.
     */
    public static GeometryData updateFromShape(Shape shape) {
        return fromJavaFXShape(shape);
    }
}
