package mm.view;

import javafx.scene.shape.*;
import mm.model.*;

/**
 * Factory class for creating JavaFX shapes from geometry data.
 * This bridges the model's view-agnostic geometry representations
 * with JavaFX-specific visual components.
 */
public class ShapeFactory {
    
    /**
     * Creates a JavaFX Shape from geometry data.
     * 
     * @param geometry the geometry data to convert
     * @return the corresponding JavaFX shape
     */
    public static Shape createShape(GeometryData geometry) {
        if (geometry instanceof RectangleGeometry) {
            return createRectangle((RectangleGeometry) geometry);
        } else if (geometry instanceof CircleGeometry) {
            return createCircle((CircleGeometry) geometry);
        }
        throw new IllegalArgumentException("Unsupported geometry type: " + geometry.getClass());
    }
    
    /**
     * Creates a JavaFX Rectangle from rectangle geometry.
     */
    private static Rectangle createRectangle(RectangleGeometry rectGeom) {
        Rectangle rect = new Rectangle(rectGeom.getWidth(), rectGeom.getHeight());
        rect.setTranslateX(rectGeom.getPosition().getX());
        rect.setTranslateY(rectGeom.getPosition().getY());
        rect.setRotate(rectGeom.getRotation());
        return rect;
    }
    
    /**
     * Creates a JavaFX Circle from circle geometry.
     */
    private static Circle createCircle(CircleGeometry circleGeom) {
        Circle circle = new Circle(circleGeom.getRadius());
        Position pos = circleGeom.getPosition();
        circle.setTranslateX(pos.getX() + circleGeom.getRadius());
        circle.setTranslateY(pos.getY() + circleGeom.getRadius());
        circle.setRotate(circleGeom.getRotation());
        return circle;
    }
    
    /**
     * Updates an existing JavaFX shape with new geometry data.
     * This is useful for animation updates where shapes need to move/rotate.
     */
    public static void updateShape(Shape shape, GeometryData geometry) {
        if (geometry instanceof RectangleGeometry && shape instanceof Rectangle) {
            updateRectangle((Rectangle) shape, (RectangleGeometry) geometry);
        } else if (geometry instanceof CircleGeometry && shape instanceof Circle) {
            updateCircle((Circle) shape, (CircleGeometry) geometry);
        }
    }
    
    private static void updateRectangle(Rectangle rect, RectangleGeometry geometry) {
        rect.setTranslateX(geometry.getPosition().getX());
        rect.setTranslateY(geometry.getPosition().getY());
        rect.setRotate(geometry.getRotation());
    }
    
    private static void updateCircle(Circle circle, CircleGeometry geometry) {
        Position pos = geometry.getPosition();
        circle.setTranslateX(pos.getX() + geometry.getRadius());
        circle.setTranslateY(pos.getY() + geometry.getRadius());
        circle.setRotate(geometry.getRotation());
    }
}
