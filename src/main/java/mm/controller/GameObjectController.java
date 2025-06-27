package mm.controller;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import mm.model.GameObject;
import mm.model.Physics;
import mm.model.PhysicsVisualPair;
import mm.view.PatternViewFactory;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;

/**
 * Utility class for converting {@link GameObject} instances into their corresponding
 * JavaFX visual representations and Box2D physics bodies for use in the simulation.
 * <p>
 * This class provides a static method to convert a {@code GameObject} into a {@link PhysicsVisualPair},
 * which contains both the JavaFX {@link Shape} for rendering and the JBox2D {@link Body} for physics simulation.
 * The conversion process uses the properties of the {@code GameObject} (such as type, size, position, color, and physics)
 * to create the appropriate visual and physical representations.
 * </p>
 * <b>Supported types:</b>
 * <ul>
 *   <li>Rectangle</li>
 *   <li>Circle</li>
 * </ul>
 * <b>Special names:</b>
 * <ul>
 *   <li>"winZone": Rendered with a special pattern and created as a static sensor body.</li>
 *   <li>"noPlaceZone": Rendered with a special pattern and created as a static sensor body.</li>
 * </ul>
 * <b>Other objects</b> are rendered and simulated according to their properties.
 * <b>Usage example:</b>
 * <pre>
 *     GameObject obj = ...;
 *     World world = ...;
 *     PhysicsVisualPair pair = GameObjectController.convert(obj, world);
 * </pre>
 */
public class GameObjectController {
    /**
     * Scale factor for converting between game units (pixels) and physics world units (meters).
     * Used to ensure consistency between the visual and physical representations.
     */
    private static final float SCALE = 50.0f;

    /**
     * Converts a {@link GameObject} to a {@link PhysicsVisualPair}, which contains both the JavaFX visual
     * representation and the Box2D physics body.
     * <p>
     * Supports rectangle and circle shapes. The created visual and body are configured
     * according to the {@code GameObject}'s properties. Special handling is provided for objects
     * named "winZone" and "noPlaceZone", which are rendered with custom patterns and created as static sensor bodies.
     * </p>
     *
     * @param obj   The {@link GameObject} to be converted. Must not be {@code null}.
     * @param world The Box2D {@link World} where the body is created. Must not be {@code null}.
     * @return      A {@link PhysicsVisualPair} containing the JavaFX visual and physics body.
     * @throws IllegalArgumentException if the object type is not supported
     */
    public static PhysicsVisualPair convert(GameObject obj, World world) {
        String type = obj.getType();
        Shape visual;
        Body body;

        if ("rectangle".equalsIgnoreCase(type)) {
            visual = createRectangleVisual(obj);
            body = createRectangleBody(obj, world);
        } else if ("circle".equalsIgnoreCase(type)) {
            visual = createCircleVisual(obj);
            body = createCircleBody(obj, world);
        } else {
            throw new IllegalArgumentException("Unsupported shape type: " + type);
        }

        // Apply special naming for winning objects
        applyWinningObjectLogic(obj, body);

        return new PhysicsVisualPair(visual, body);
    }

    /**
     * Creates a JavaFX Rectangle visual representation for a rectangular GameObject.
     * <p>
     * Handles special cases for "noPlaceZone" (red pattern) and "winZone" (green pattern),
     * while regular rectangles use the object's specified color. The rectangle is positioned
     * according to the GameObject's position coordinates.
     * </p>
     *
     * @param obj The GameObject containing rectangle properties and position
     * @return A configured Rectangle shape positioned for visual rendering
     */
    private static Rectangle createRectangleVisual(GameObject obj) {
        float width = obj.getSize().getWidth();
        float height = obj.getSize().getHeight();
        float x = obj.getPosition().getX();
        float y = obj.getPosition().getY();
        
        Rectangle rect = new Rectangle(width, height);
        
        // Apply special patterns for zone objects
        if (obj.getName().equalsIgnoreCase("noPlaceZone")) {
            rect.setFill(PatternViewFactory.createPlaceZone(width, height, Color.RED));
        } else if (obj.getName().equalsIgnoreCase("winZone")) {
            rect.setFill(PatternViewFactory.createPlaceZone(width, height, Color.GREEN));
        } else {
            rect.setFill(Color.valueOf(obj.getColour()));
            // TODO: Add sprite code implementation here for textured objects
        }
        
        // Position the rectangle in the scene
        rect.setTranslateX(x);
        rect.setTranslateY(y);
        
        return rect;
    }

    /**
     * Creates a JBox2D Body for a rectangular GameObject.
     * <p>
     * Configures the body type (dynamic/static) based on physics properties,
     * creates a box shape with appropriate dimensions, and sets up fixture
     * properties including special sensor configuration for zone objects.
     * The body position is calculated with proper center offset for rectangles.
     * </p>
     *
     * @param obj The GameObject containing rectangle, physics, and position properties
     * @param world The JBox2D World in which to create the body
     * @return A configured Body for physics simulation
     */
    private static Body createRectangleBody(GameObject obj, World world) {
        Physics physics = obj.getPhysics();
        float width = obj.getSize().getWidth();
        float height = obj.getSize().getHeight();
        float x = obj.getPosition().getX();
        float y = obj.getPosition().getY();

        // Create body definition with position and rotation
        BodyDef def = new BodyDef();
        def.type = physics.getShape().equalsIgnoreCase("Dynamic") ? BodyType.DYNAMIC : BodyType.STATIC;
        // Position at center of rectangle for proper physics simulation
        def.position.set((x + width / 2) / SCALE, (y + height / 2) / SCALE);
        def.angle = (float) Math.toRadians(obj.getAngle());
        
        Body body = world.createBody(def);
        body.setUserData(obj.getName());

        // Create box shape (half-extents for JBox2D)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / SCALE, height / 2 / SCALE);

        // Configure fixture with physics properties
        FixtureDef fixture = createFixtureDef(shape, physics);
        
        // Set sensor flag for special zone objects
        if (isZoneObject(obj.getName())) {
            fixture.isSensor = true;
        }
        
        body.createFixture(fixture);
        return body;
    }

    /**
     * Creates a JavaFX Circle visual representation for a circular GameObject.
     * <p>
     * Creates a circle with the specified radius and color from the GameObject,
     * positioned according to the object's coordinates.
     * </p>
     *
     * @param obj The GameObject containing circle properties and position
     * @return A configured Circle shape positioned for visual rendering
     */
    private static Circle createCircleVisual(GameObject obj) {
        float radius = obj.getSize().getRadius();
        float x = obj.getPosition().getX();
        float y = obj.getPosition().getY();

        Circle circ = new Circle(radius, Color.valueOf(obj.getColour()));
        circ.setTranslateX(x);
        circ.setTranslateY(y);
        
        return circ;
    }

    /**
     * Creates a JBox2D Body for a circular GameObject.
     * <p>
     * Configures the body type based on physics properties, creates a circle
     * shape with appropriate radius, and sets up fixture properties.
     * The body is positioned directly at the GameObject's coordinates.
     * </p>
     *
     * @param obj The GameObject containing circle, physics, and position properties
     * @param world The JBox2D World in which to create the body
     * @return A configured Body for physics simulation
     */
    private static Body createCircleBody(GameObject obj, World world) {
        Physics physics = obj.getPhysics();
        float radius = obj.getSize().getRadius();
        float x = obj.getPosition().getX();
        float y = obj.getPosition().getY();

        // Create body definition with position and rotation
        BodyDef def = new BodyDef();
        def.type = physics.getShape().equalsIgnoreCase("DYNAMIC") ? BodyType.DYNAMIC : BodyType.STATIC;
        def.position.set(x / SCALE, y / SCALE);
        def.angle = (float) Math.toRadians(obj.getAngle());
        
        Body body = world.createBody(def);
        body.setUserData(obj.getName());

        // Create circle shape
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / SCALE);

        // Configure and attach fixture
        FixtureDef fixture = createFixtureDef(shape, physics);
        body.createFixture(fixture);
        
        return body;
    }

    /**
     * Creates a FixtureDef with the specified shape and physics properties.
     * <p>
     * This helper method consolidates the common fixture configuration logic,
     * setting density, friction, and restitution from the Physics object.
     * </p>
     *
     * @param shape The JBox2D shape (CircleShape or PolygonShape)
     * @param physics The Physics object containing material properties
     * @return A configured FixtureDef ready for body attachment
     */
    private static FixtureDef createFixtureDef(org.jbox2d.collision.shapes.Shape shape, Physics physics) {
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = physics.getDensity();
        fixture.friction = physics.getFriction();
        fixture.restitution = physics.getRestitution();
        return fixture;
    }

    /**
     * Checks if the given object name represents a special zone object.
     * <p>
     * Zone objects (noPlaceZone, winZone) require special handling such as
     * sensor physics bodies and custom visual patterns.
     * </p>
     *
     * @param name The name of the object to check
     * @return true if the object is a zone object, false otherwise
     */
    private static boolean isZoneObject(String name) {
        return name.equalsIgnoreCase("noPlaceZone") || name.equalsIgnoreCase("winZone");
    }

    /**
     * Applies special naming logic for winning objects and platform objects.
     * <p>
     * Objects marked as winning (except winPlat and winZone) are renamed to "winObject"
     * for collision detection purposes. This method handles the complex logic for
     * determining when to apply the winning object naming convention.
     * </p>
     * <p>
     * <b>Note:</b> The original logic appears to have a bug with OR instead of AND.
     * Current implementation preserves original behavior but may need review.
     * </p>
     *
     * @param obj The GameObject to check for winning status
     * @param body The JBox2D Body to potentially rename
     */
    private static void applyWinningObjectLogic(GameObject obj, Body body) {
        // Original logic: (!winplat OR !winZone) - this is always true due to OR
        // This preserves the original behavior but may need review
        if (!obj.getName().equalsIgnoreCase("winplat") || !obj.getName().equalsIgnoreCase("winZone")) {
            String name = obj.isWinning() ? "winObject" : obj.getName();
            body.setUserData(name);
        }
    }
}