package mm.controller;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import mm.model.InventoryObject;
import mm.model.Physics;
import mm.model.PhysicsVisualPair;
import mm.view.PatternViewFactory;

/**
 * Utility class for converting {@link InventoryObject} instances into their corresponding
 * JavaFX visual representations and Box2D physics bodies for use in the inventory system.
 * <p>
 * This class provides a static method to convert an {@code InventoryObject} into a {@link PhysicsVisualPair},
 * which contains both the JavaFX {@link Shape} for rendering and the JBox2D {@link Body} for physics simulation.
 * The conversion process uses the properties of the {@code InventoryObject} (such as type, size, color, and physics)
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
 *     InventoryObject obj = ...;
 *     World world = ...;
 *     PhysicsVisualPair pair = InventoryObjectController.convert(obj, world);
 * </pre>
 */
public class InventoryObjectController {
    /** Scale factor for converting between game units and physics world units */
    private static final float SCALE = 50.0f;
    
    /**
     * Converts an {@link InventoryObject} to a {@link PhysicsVisualPair}, which contains both the JavaFX visual
     * representation and the Box2D physics body.
     * <p>
     * Supports rectangle and circle shapes. The created visual and body are configured
     * according to the {@code InventoryObject}'s properties. Special handling is provided for objects
     * named "winZone" and "noPlaceZone", which are rendered with custom patterns and created as static sensor bodies.
     * </p>
     *
     * @param obj   The individual {@link InventoryObject} to be converted. Must not be {@code null}.
     * @param world The Box2D {@link World} where the body is created. Must not be {@code null}.
     * @return      A {@link PhysicsVisualPair} containing the visual and physics body.
     */
    public static PhysicsVisualPair convert(InventoryObject obj, World world){

        Physics physics = obj.getPhysics();
        String type = obj.getType(); // e.g., "rectangle", "circle"
        Shape visual = null;
        Body body = null;

        if ("rectangle".equalsIgnoreCase(type)){

            float width = obj.getSize().getWidth();
            float height = obj.getSize().getHeight();

            if (obj.getName().equalsIgnoreCase("winZone")){
                Rectangle rect = new Rectangle(width, height);
                rect.setFill(PatternViewFactory.createWinzone(width, height));
                visual = rect;

                BodyDef def = new BodyDef();
                def.type = BodyType.STATIC;
                body = world.createBody(def);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(width / 2 / SCALE, height / 2 / SCALE);

                FixtureDef fixture = new FixtureDef();
                fixture.shape = shape;
                fixture.isSensor = true;
                body.createFixture(fixture);
                
            } else if (obj.getName().equalsIgnoreCase("noPlaceZone")){
                Rectangle rect = new Rectangle(width, height);
                rect.setFill(PatternViewFactory.createNoPlaceZone(width, height));
                visual = rect;

                BodyDef def = new BodyDef();
                def.type = BodyType.STATIC;
                body = world.createBody(def);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(width / 2 / SCALE, height / 2 / SCALE);

                FixtureDef fixture = new FixtureDef();
                fixture.shape = shape;
                fixture.isSensor = true;
                body.createFixture(fixture);

            } else { 

                Rectangle rect = new Rectangle(width, height, Color.valueOf(obj.getColour()));
                visual = rect;

                BodyDef def = new BodyDef();
                def.type = (physics.getShape().equals("DYNAMIC")) ? BodyType.DYNAMIC : BodyType.STATIC;
                body = world.createBody(def);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(width / 2 / SCALE, height / 2 / SCALE);

                FixtureDef fixture = new FixtureDef();
                fixture.shape = shape;
                fixture.density = physics.getDensity();
                fixture.friction = physics.getFriction();
                fixture.restitution = physics.getRestitution();
                body.createFixture(fixture);
            }

        } else if ("circle".equalsIgnoreCase(type)) {
            float radius = obj.getSize().getRadius();
            Circle circ = new Circle(radius, Color.valueOf(obj.getColour()));
            visual = circ;

            BodyDef def = new BodyDef();
            def.type = (physics.getShape().equals("DYNAMIC")) ? BodyType.DYNAMIC : BodyType.STATIC;
            body = world.createBody(def);

            CircleShape shape = new CircleShape();
            shape.setRadius(radius / SCALE);

            FixtureDef fixture = new FixtureDef();
            fixture.shape = shape;
            fixture.density = physics.getDensity();
            fixture.friction = physics.getFriction();
            fixture.restitution = physics.getRestitution();
            body.createFixture(fixture);
        }

        return new PhysicsVisualPair(visual, body);
    }
}
