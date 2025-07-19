package mm.controller;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import mm.model.InventoryObject;
import mm.model.Physics;
import mm.model.PhysicsVisualPair;
import mm.view.PatternViewFactory;

/**
 * Utility class for converting {@link InventoryObject} instances into their
 * corresponding
 * JavaFX visual representations and Box2D physics bodies for use in the
 * inventory system.
 * <p>
 * This class provides a static method to convert an {@code InventoryObject}
 * into a {@link PhysicsVisualPair},
 * which contains both the JavaFX {@link Shape} for rendering and the JBox2D
 * {@link Body} for physics simulation.
 * The conversion process uses the properties of the {@code InventoryObject}
 * (such as type, size, color, and physics)
 * to create the appropriate visual and physical representations.
 * </p>
 * <b>Supported types:</b>
 * <ul>
 * <li>Rectangle</li>
 * <li>Circle</li>
 * </ul>
 * <b>Special names:</b>
 * <ul>
 * <li>"winZone": Rendered with a special pattern and created as a static sensor
 * body.</li>
 * <li>"noPlaceZone": Rendered with a special pattern and created as a static
 * sensor body.</li>
 * </ul>
 * <b>Other objects</b> are rendered and simulated according to their
 * properties.
 * <b>Usage example:</b>
 * 
 * <pre>
 *     InventoryObject obj = ...;
 *     World world = ...;
 *     PhysicsVisualPair pair = InventoryObjectController.convert(obj, world);
 * </pre>
 */
public class InventoryObjectController {
    /** Scale factor for converting between game units and physics world units */
    private static final float SCALE = 50.0f;

    /**
     * Converts an {@link InventoryObject} to a {@link PhysicsVisualPair}, which
     * contains both the JavaFX visual
     * representation and the Box2D physics body.
     * <p>
     * Supports rectangle and circle shapes. The created visual and body are
     * configured
     * according to the {@code InventoryObject}'s properties. Special handling is
     * provided for objects
     * named "winZone" and "noPlaceZone", which are rendered with custom patterns
     * and created as static sensor bodies.
     * </p>
     *
     * @param obj   The individual {@link InventoryObject} to be converted. Must not
     *              be {@code null}.
     * @param world The Box2D {@link World} where the body is created. Must not be
     *              {@code null}.
     * @return A {@link PhysicsVisualPair} containing the visual and physics body.
     */
    public static PhysicsVisualPair convert(InventoryObject obj, World world) {
        String type = obj.getType();
        Shape visual;
        Body body;

        if ("rectangle".equalsIgnoreCase(type)) {
            visual = createRectangleVisual(obj);
            body = createRectangleBody(obj, world);
        } else if ("circle".equalsIgnoreCase(type)) {
            visual = createCircleVisual(obj);
            body = createCircleBody(obj, world);
        } else if ("bucket".equalsIgnoreCase(type)) {
            visual = createBucketVisual(obj);
            body = createBucketBody(obj, world);
        } else {
            throw new IllegalArgumentException("Unsupported shape type: " + type);
        }

        return new PhysicsVisualPair(visual, body);
    }

    /**
     * Creates a JavaFX Rectangle visual representation for a rectangular
     * InventoryObject.
     * <p>
     * Handles special cases for "noPlaceZone" (red pattern) and "winZone" (green
     * pattern),
     * while regular rectangles use the object's specified color.
     * </p>
     *
     * @param obj The InventoryObject containing rectangle properties
     * @return A configured Rectangle shape for visual rendering
     */
    private static Rectangle createRectangleVisual(InventoryObject obj) {
        float width = obj.getSize().getWidth();
        float height = obj.getSize().getHeight();

        Rectangle rect = new Rectangle(width, height);

        if (obj.getSprite() != null) {
            Image image = null;
            try {
                String spritePath = obj.getSprite();
                image = new Image(InventoryObjectController.class.getResource(spritePath).toExternalForm());
            } catch (Exception e) {
                // Image loading failed, fall back to color fill
            }
            if (image != null && !image.isError()) {
                rect.setFill(new ImagePattern(image));
            } else if (obj.getName().equalsIgnoreCase("noPlaceZone")) {
                rect.setFill(PatternViewFactory.createPlaceZone(width, height, Color.RED));
            } else if (obj.getName().equalsIgnoreCase("winZone")) {
                rect.setFill(PatternViewFactory.createPlaceZone(width, height, Color.GREEN));
            } else {
                rect.setFill(Color.valueOf(obj.getColour()));
            }
        } else if (obj.getName().equalsIgnoreCase("noPlaceZone")) {
            rect.setFill(PatternViewFactory.createPlaceZone(width, height, Color.RED));
        } else if (obj.getName().equalsIgnoreCase("winZone")) {
            rect.setFill(PatternViewFactory.createPlaceZone(width, height, Color.GREEN));
        } else {
            rect.setFill(Color.valueOf(obj.getColour()));
        }

        return rect;
    }

    /**
     * Creates a JBox2D Body for a rectangular InventoryObject.
     * <p>
     * Configures the body type (dynamic/static) based on physics properties,
     * creates a box shape with appropriate dimensions, and sets up fixture
     * properties including special sensor configuration for zone objects.
     * </p>
     *
     * @param obj   The InventoryObject containing rectangle and physics properties
     * @param world The JBox2D World in which to create the body
     * @return A configured Body for physics simulation
     */
    private static Body createRectangleBody(InventoryObject obj, World world) {
        Physics physics = obj.getPhysics();
        float width = obj.getSize().getWidth();
        float height = obj.getSize().getHeight();

        // Create body definition with appropriate type
        BodyDef def = new BodyDef();
        def.type = physics.getShape().equalsIgnoreCase("Dynamic") ? BodyType.DYNAMIC : BodyType.STATIC;

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
     * Creates a JavaFX Circle visual representation for a circular InventoryObject.
     * <p>
     * Creates a circle with the specified radius and color from the
     * InventoryObject.
     * Currently does not handle special zone patterns for circles.
     * </p>
     *
     * @param obj The InventoryObject containing circle properties
     * @return A configured Circle shape for visual rendering
     */
    private static Circle createCircleVisual(InventoryObject obj) {
        float radius = obj.getSize().getRadius();

        Circle circ = new Circle(radius);

        if (obj.getSprite() != null) {
            Image image = null;
            try {
                String spritePath = obj.getSprite();
                image = new Image(InventoryObjectController.class.getResource(spritePath).toExternalForm());
            } catch (Exception e) {
                // Image loading failed, fall back to color fill
            }
            if (image != null && !image.isError()) {
                circ.setFill(new ImagePattern(image));
            } else {
                circ.setFill(Color.valueOf(obj.getColour()));
            }
        } else {
            circ.setFill(Color.valueOf(obj.getColour()));
        }

        return circ;
    }

    /**
     * Creates a JBox2D Body for a circular InventoryObject.
     * <p>
     * Configures the body type based on physics properties, creates a circle
     * shape with appropriate radius, and sets up fixture properties.
     * </p>
     *
     * @param obj   The InventoryObject containing circle and physics properties
     * @param world The JBox2D World in which to create the body
     * @return A configured Body for physics simulation
     */
    private static Body createCircleBody(InventoryObject obj, World world) {
        Physics physics = obj.getPhysics();
        float radius = obj.getSize().getRadius();

        // Create body definition with appropriate type
        BodyDef def = new BodyDef();
        def.type = physics.getShape().equals("DYNAMIC") ? BodyType.DYNAMIC : BodyType.STATIC;

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
     * Creates a JavaFX visual representation for a U-shaped bucket using Polygon.
     * <p>
     * Constructs a U-shaped polygon by defining 8 vertices that form the outer and inner
     * boundaries of the bucket. The wall thickness is fixed at 10.0 units.
     * </p>
     * <p>
     * The U-shape is defined by vertices in clockwise order starting from the bottom-left
     * outer corner, creating a hollow container shape suitable for catching falling objects.
     * The vertices are defined relative to the top-left corner for consistent positioning
     * with other shape types.
     * </p>
     *
     * @param obj The InventoryObject containing bucket properties including width, height, and color
     * @return A configured Polygon shape representing the U-shaped bucket for visual rendering
     */
    private static Polygon createBucketVisual(InventoryObject obj) {
        float width = obj.getSize().getWidth();
        float height = obj.getSize().getHeight();
        float wallThickness = 10.0f;
        
        Polygon bucket = new Polygon();
        
        // Define U-shape vertices (relative to top-left, matching other shapes)
        Double[] points = {
            (double)(0), (double)(height),                                    // Bottom-left outer
            (double)(0), (double)(0),                                         // Top-left outer
            (double)(wallThickness), (double)(0),                            // Top-left inner
            (double)(wallThickness), (double)(height - wallThickness),       // Bottom-left inner
            (double)(width - wallThickness), (double)(height - wallThickness), // Bottom-right inner
            (double)(width - wallThickness), (double)(0),                    // Top-right inner
            (double)(width), (double)(0),                                     // Top-right outer
            (double)(width), (double)(height)                                // Bottom-right outer
        };
        
        bucket.getPoints().addAll(points);
        bucket.setFill(Color.valueOf(obj.getColour()));
        
        return bucket;
    }

    /**
     * Creates a JBox2D Body for a U-shaped bucket using multiple PolygonShapes.
     * <p>
     * Constructs a physics body with three separate rectangular fixtures that form
     * the left wall, right wall, and bottom of the bucket, creating a hollow
     * container
     * that objects can fall into. Each wall is a separate collision shape to ensure
     * proper physics behavior.
     * </p>
     * <p>
     * The body type (dynamic/static) is determined by the physics properties of the
     * InventoryObject. Each fixture is configured with material properties such as
     * density, friction, and restitution from the Physics object.
     * </p>
     *
     * @param obj   The InventoryObject containing bucket dimensions and physics
     *              properties
     * @param world The JBox2D World in which to create the physics body
     * @return A configured Body with three-wall collision geometry for physics
     *         simulation
     */
    private static Body createBucketBody(InventoryObject obj, World world) {
        Physics physics = obj.getPhysics();
        float width = obj.getSize().getWidth();
        float height = obj.getSize().getHeight();
        float wallThickness = 10.0f;

        // Create body definition
        BodyDef def = new BodyDef();
        def.type = physics.getShape().equalsIgnoreCase("Dynamic") ? BodyType.DYNAMIC : BodyType.STATIC;

        Body body = world.createBody(def);
        body.setUserData(obj.getName());

        // Create bottom wall
        PolygonShape bottomShape = new PolygonShape();
        bottomShape.setAsBox(
                width / 2 / SCALE, // half-width
                wallThickness / 2 / SCALE, // half-height
                new Vec2(0, (height / 2 - wallThickness / 2) / SCALE), // center position
                0 // angle
        );
        FixtureDef bottomFixture = createFixtureDef(bottomShape, physics);
        body.createFixture(bottomFixture);

        // Create left wall
        PolygonShape leftShape = new PolygonShape();
        leftShape.setAsBox(
                wallThickness / 2 / SCALE, // half-width
                height / 2 / SCALE, // half-height
                new Vec2((-width / 2 + wallThickness / 2) / SCALE, 0), // center position
                0 // angle
        );
        FixtureDef leftFixture = createFixtureDef(leftShape, physics);
        body.createFixture(leftFixture);

        // Create right wall
        PolygonShape rightShape = new PolygonShape();
        rightShape.setAsBox(
                wallThickness / 2 / SCALE, // half-width
                height / 2 / SCALE, // half-height
                new Vec2((width / 2 - wallThickness / 2) / SCALE, 0), // center position
                0 // angle
        );
        FixtureDef rightFixture = createFixtureDef(rightShape, physics);
        body.createFixture(rightFixture);

        return body;
    }

    /**
     * Creates a FixtureDef with the specified shape and physics properties.
     * <p>
     * This helper method consolidates the common fixture configuration logic,
     * setting density, friction, and restitution from the Physics object.
     * </p>
     *
     * @param shape   The JBox2D shape (CircleShape or PolygonShape)
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
     * Creates a preview visual representation for an InventoryObject, similar to
     * the conversion process but without creating a physics body.
     * <p>
     * This method is used for displaying object previews in the inventory,
     * supporting the same shape types (rectangle, circle, bucket) as the main
     * conversion method.
     * </p>
     *
     * @param obj The InventoryObject to create a preview visual for. Must not be
     *            {@code null}.
     * @return A JavaFX {@link Node} representing the visual preview of the object.
     */
    public static Node createPreviewVisual(InventoryObject obj) {
        String type = obj.getType();
        Shape visual;
        
        if ("rectangle".equalsIgnoreCase(type)) {
            visual = createRectangleVisual(obj);
        } else if ("circle".equalsIgnoreCase(type)) {
            visual = createCircleVisual(obj); 
        } else if ("bucket".equalsIgnoreCase(type)) {
            visual = createBucketVisual(obj);
        } else {
            throw new IllegalArgumentException("Unsupported shape type: " + type);
        }
        
        visual.setRotate(obj.getAngle());
        return visual;
    }
}
