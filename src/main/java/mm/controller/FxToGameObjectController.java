package mm.controller;

import mm.model.GameObject;
import mm.model.Physics;
import mm.model.PhysicsVisualPair;
import mm.model.Position;
import mm.model.Size;

import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import org.jbox2d.dynamics.*;

/**
 * Utility class for converting JavaFX visual shapes and their associated physics
 * (PhysicsVisualPair) back into {@link GameObject} instances.
 * <p>
 * This is typically used when exporting or serializing the current state of the simulation,
 * allowing the visual and physical properties of objects to be captured in a {@code GameObject}
 * representation. The conversion extracts all relevant properties from the JavaFX {@code Shape}
 * and the JBox2D {@code Body}, such as position, size, color, angle, and physics parameters.
 * </p>
 * <b>Supported shapes:</b>
 * <ul>
 *   <li>{@link javafx.scene.shape.Rectangle}</li>
 *   <li>{@link javafx.scene.shape.Circle}</li>
 * </ul>
 * <b>Unsupported shapes</b> will result in an {@link IllegalArgumentException}.
 * <b>Usage example:</b>
 * <pre>
 *     PhysicsVisualPair pair = ...;
 *     GameObject obj = FxToGameObjectController.convertBack(pair);
 * </pre>
 */
public class FxToGameObjectController {
    
    /**
     * Converts a {@link PhysicsVisualPair} (which contains a JavaFX {@link Shape} and a JBox2D {@link Body})
     * back into a {@link GameObject}, extracting all relevant properties such as position,
     * size, color, angle, and physics parameters.
     *
     * @param pair The {@code PhysicsVisualPair} containing the visual ({@link Shape}) and physical ({@link Body}) representation.
     * @return A new {@link GameObject} instance representing the same object.
     * @throws IllegalArgumentException if the shape type is not supported.
     */
    public static GameObject convertBack(PhysicsVisualPair pair) {
        String name = (String) pair.body.getUserData();
        float angle = (float) Math.toDegrees(pair.body.getAngle());
        boolean winning = "winObject".equalsIgnoreCase((String) pair.body.getUserData());
        Shape shape = pair.visual;

        String sprite = extractSprite(pair, shape);
        String colour = (sprite != null) ? "BLACK" : extractColour(pair, shape);
        Position position = new Position(0, 0);
        Size size = new Size();
        String type = extractShapeProperties(shape, position, size);
        Physics physics = extractPhysics(pair.body);

        // Create GameObject with basic constructor
        GameObject gameObject = new GameObject(name, type, position, size);
        gameObject.setSprite(sprite);
        // Set additional properties using setters
        gameObject.setPhysics(physics);
        gameObject.setAngle(angle);
        gameObject.setColour(colour);
        gameObject.setWinning(winning);
        
        return gameObject;
    }

    /**
     * Extracts the color from the shape, handling special cases for certain object types.
     * <p>
     * This method handles the color extraction logic with special consideration for certain
     * object types that should not have their color extracted (winZone and noPlaceZone).
     * For regular objects, it extracts the fill color from the JavaFX Shape and converts
     * it to a string representation, defaulting to "BLACK" if no color is set.
     * </p>
     * 
     * @param pair The PhysicsVisualPair containing the body with user data
     * @param shape The JavaFX Shape from which to extract the color
     * @return The color as a string representation, null for special zones, or "BLACK" as default
     */
    private static String extractColour(PhysicsVisualPair pair, Shape shape) {
        if (pair.body.getUserData().equals("winZone") || pair.body.getUserData().equals("noPlaceZone")) {
            return null;
        }
        Color tmp = (Color) shape.getFill();
        return (tmp != null) ? tmp.toString() : "BLACK";
    }

    /**
     * Extracts the color from the shape, handling special cases for certain object types.
     * <p>
     * This method handles the color extraction logic with special consideration for certain
     * object types that should not have their color extracted (winZone and noPlaceZone).
     * For regular objects, it extracts the fill color from the JavaFX Shape and converts
     * it to a string representation, defaulting to "BLACK" if no color is set.
     * </p>
     * 
     * @param pair The PhysicsVisualPair containing the body with user data
     * @param shape The JavaFX Shape from which to extract the color
     * @return The color as a string representation, null for special zones, or "BLACK" as default
     */
    private static String extractSprite(PhysicsVisualPair pair, Shape shape) {
        String type = (String) pair.body.getUserData();
        if (type.equalsIgnoreCase("winZone") || type.equalsIgnoreCase("noPlaceZone") || type.equalsIgnoreCase("winPlat") || type.equalsIgnoreCase("bucket")) {
            return null;
        }
        ImagePattern pattern = (ImagePattern) shape.getFill();
        Image tmp = pattern.getImage();
        String sprite = tmp.getUrl();
        return (sprite == null) ? null : sprite.substring(sprite.lastIndexOf('/')+1, sprite.length());
    }

    /**
     * Extracts shape-specific properties (position, size) and returns the shape type.
     * <p>
     * This method acts as a dispatcher, determining the type of JavaFX Shape and
     * delegating to the appropriate handler method. It supports Rectangle and Circle
     * shapes, throwing an exception for unsupported shape types.
     * </p>
     * 
     * @param shape The JavaFX Shape to analyze
     * @param position The Position object to populate with extracted coordinates
     * @param size The Size object to populate with extracted dimensions
     * @return A string representation of the shape type ("Rectangle" or "Circle")
     * @throws IllegalArgumentException if the shape type is not supported
     */
    private static String extractShapeProperties(Shape shape, Position position, Size size) {
        if (shape instanceof Rectangle) {
            return handleRectangle((Rectangle) shape, position, size);
        } else if (shape instanceof Circle) {
            return handleCircle((Circle) shape, position, size);
        } else {
            throw new IllegalArgumentException("Shape-Typ nicht unterstützt: " + shape.getClass());
        }
    }

    /**
     * Handles rectangle shape properties extraction.
     * <p>
     * Extracts position and size properties specific to Rectangle shapes.
     * The position is taken from the translation coordinates (translateX/Y),
     * and the size includes both width and height dimensions.
     * </p>
     * 
     * @param rect The Rectangle shape to extract properties from
     * @param position The Position object to populate with x,y coordinates
     * @param size The Size object to populate with width and height
     * @return The string "Rectangle" indicating the shape type
     */
    private static String handleRectangle(Rectangle rect, Position position, Size size) {
        position.setX((float) rect.getTranslateX());
        position.setY((float) rect.getTranslateY());
        size.setWidth((float) rect.getWidth());
        size.setHeight((float) rect.getHeight());
        return "Rectangle";
    }

    /**
     * Handles circle shape properties extraction.
     * <p>
     * Extracts position and size properties specific to Circle shapes.
     * The position is taken from the translation coordinates (translateX/Y).
     * For circles, width and height are set to 0, and only the radius is used
     * to represent the size, following the GameObject model conventions.
     * </p>
     * 
     * @param circle The Circle shape to extract properties from
     * @param position The Position object to populate with x,y coordinates
     * @param size The Size object to populate with radius (width/height set to 0)
     * @return The string "Circle" indicating the shape type
     */
    private static String handleCircle(Circle circle, Position position, Size size) {
        position.setX((float) circle.getTranslateX());
        position.setY((float) circle.getTranslateY());
        size.setHeight(0);
        size.setWidth(0);
        size.setRadius((float) circle.getRadius());
        return "Circle";
    }

    /**
     * Extracts physics properties from the JBox2D body.
     * <p>
     * This method creates a new Physics object and populates it with properties
     * from the JBox2D Body and its associated Fixture. The body type is converted
     * to uppercase string format, and material properties (density, restitution,
     * friction) are extracted from the first fixture in the body's fixture list.
     * </p>
     * 
     * @param body The JBox2D Body containing the physics properties
     * @return A new Physics object populated with the body's properties
     */
    private static Physics extractPhysics(Body body) {
        Physics physics = new Physics();
        // Convert JBox2D body type to uppercase string (e.g., "DYNAMIC", "STATIC")
        physics.setShape(body.getType().toString().toUpperCase());
        
        // Extract material properties from the first fixture
        Fixture fixture = body.getFixtureList();
        physics.setDensity(fixture.getDensity());
        physics.setRestitution(fixture.getRestitution());
        physics.setFriction(fixture.getFriction());
        
        return physics;
    }
}