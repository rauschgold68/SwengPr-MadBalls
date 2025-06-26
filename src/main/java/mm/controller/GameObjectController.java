package mm.controller;

import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
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
     */
    public static PhysicsVisualPair convert(GameObject obj, World world) {
        Physics physics = obj.getPhysics();
        String type = obj.getType();
        Shape visual = null;
        Body body = null;

        float x = obj.getPosition().getX();
        float y = obj.getPosition().getY();
        
        if ("rectangle".equalsIgnoreCase(type)) {
            float width = obj.getSize().getWidth();
            float height = obj.getSize().getHeight();
            
            Rectangle rect = new Rectangle(width, height);
            if (obj.getName().equalsIgnoreCase("noPlaceZone")) {
                rect.setFill(PatternViewFactory.createPlaceZone(width, height, Color.RED));
            } else if (obj.getName().equalsIgnoreCase("winZone")) {
                rect.setFill(PatternViewFactory.createPlaceZone(width, height, Color.GREEN));
            } else {
                rect.setFill(Color.valueOf(obj.getColour()));
                //add SpriteCodeImplementation here
            }
            rect.setTranslateX(x);
            rect.setTranslateY(y);
            visual = rect;

            // JBox2D body: dynamic or static based on physics shape property
            BodyDef def = new BodyDef();
            def.type = physics.getShape().equalsIgnoreCase("Dynamic") ? BodyType.DYNAMIC : BodyType.STATIC;
            def.position.set((x + width / 2) / SCALE, (y + height / 2) / SCALE);
            def.angle = (float) Math.toRadians(obj.getAngle());
            body = world.createBody(def); 
            body.setUserData(obj.getName());

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2 / SCALE, height / 2 / SCALE);

            FixtureDef fixture = new FixtureDef();
            fixture.shape = shape;
            fixture.density = physics.getDensity();
            fixture.friction = physics.getFriction();
            fixture.restitution = physics.getRestitution();
            if(obj.getName().equalsIgnoreCase("noplacezone") || obj.getName().equalsIgnoreCase("winzone")) {
                fixture.isSensor = true;
            }
            body.createFixture(fixture);

        } else if ("circle".equalsIgnoreCase(type)) {
            float radius = obj.getSize().getRadius();

            // JavaFX visual: colored circle
            Circle circ = new Circle(radius, Color.valueOf(obj.getColour()));
            circ.setTranslateX(x);
            circ.setTranslateY(y);
            visual = circ;

            // JBox2D body: dynamic or static based on physics shape property
            BodyDef def = new BodyDef();
            def.type = (physics.getShape().equalsIgnoreCase("DYNAMIC")) ? BodyType.DYNAMIC : BodyType.STATIC;
            def.position.set(x / SCALE, y / SCALE);
            def.angle = (float) Math.toRadians(obj.getAngle());
            body = world.createBody(def);
            body.setUserData(obj.getName());

            CircleShape shape = new CircleShape();
            shape.setRadius(radius / SCALE);

            FixtureDef fixture = new FixtureDef();
            fixture.shape = shape;
            fixture.density = physics.getDensity();
            fixture.friction = physics.getFriction();
            fixture.restitution = physics.getRestitution();
            body.createFixture(fixture);
        }
        //sets name to winwobject for winning object
        if (!obj.getName().equalsIgnoreCase("winplat") || !obj.getName().equalsIgnoreCase("winZone")) {
            String name = (obj.isWinning()) ? "winObject" : obj.getName();
            body.setUserData(name);
        }

        return new PhysicsVisualPair(visual, body);
    }
}