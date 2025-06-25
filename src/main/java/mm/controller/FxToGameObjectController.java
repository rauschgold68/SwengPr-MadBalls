package mm.controller;

import mm.model.GameObject;
import mm.model.Physics;
import mm.model.PhysicsVisualPair;
import mm.model.Position;
import mm.model.Size;

import javafx.scene.shape.*;
import javafx.scene.paint.Color;
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
        GameObject gameObject = null;

        String name = (String) pair.body.getUserData();
        Position position = new Position(0,0);
        // Angle is converted from radians (JBox2D) to degrees
        float angle = (float) Math.toDegrees(pair.body.getAngle());
        Size size = new Size();
        String colour = null;
        String type;
        Physics physics = new Physics();
        boolean winning = pair.body.getUserData().equals("winObject");

        // The JavaFX Shape representing the visual part of the object
        Shape shape = pair.visual;

        if (!pair.body.getUserData().equals("winZone") && !pair.body.getUserData().equals("noPlaceZone")) {
            // Extract color from the shape, defaulting to "BLACK" if not set
            Color tmp = (Color) shape.getFill();
            colour = (tmp != null) ? tmp.toString():"BLACK";
        }

        // Handle Rectangle shapes
        if (shape instanceof Rectangle) {
            type = "Rectangle";
            javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) shape;
            float x = (float) rect.getTranslateX();
            float y = (float) rect.getTranslateY();
            float width = (float) rect.getWidth();
            float height = (float) rect.getHeight();

            // Set position and size
            position.setX(x);
            position.setY(y);

            size.setHeight(height);
            size.setWidth(width);

        // Handle Circle shapes
        } else if (shape instanceof Circle) {
            type = "Circle";
            javafx.scene.shape.Circle circle = (javafx.scene.shape.Circle) shape;
            float x = (float) circle.getTranslateX();
            float y = (float) circle.getTranslateY();
            float r = (float) circle.getRadius();

            // Set position and radius (width/height are zero for circles)
            position.setX(x);
            position.setY(y);
            size.setHeight(0);
            size.setWidth(0);
            size.setRadius(r);

        // Throw an error for unsupported shapes
        } else {
            throw new IllegalArgumentException("Shape-Typ nicht unterstützt: " + shape.getClass());
        }

        // Extract physics properties from the JBox2D body and fixture
        physics.setShape(pair.body.getType().toString().toUpperCase());
        Fixture fixture = pair.body.getFixtureList();
        physics.setDensity(fixture.getDensity());
        physics.setRestitution(fixture.getRestitution());
        physics.setFriction(fixture.getFriction());
        
        // Create and return the new GameObject
        gameObject = new GameObject(name, type, position, angle, size, colour, physics, winning);
        return gameObject;
    }
}