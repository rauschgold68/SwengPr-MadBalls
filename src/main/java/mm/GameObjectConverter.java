package mm;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import mm.model.objects.GameObject;
import mm.model.objects.Physics;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;

/**
 * Utility class for converting GameObject instances into their corresponding
 * JavaFX visual representations and Box2D physics bodies for use in the simulation.
 */
public class GameObjectConverter {
    /** Scale factor for converting between game units and physics world units */
    private static final float SCALE = 50.0f;

    /**
     * Converts a GameObject to a PhysicsVisualPair, which contains both the JavaFX visual
     * representation and the Box2D physics body.
     * <p>
     * Supports rectangle and circle shapes. The created visual and body are configured
     * according to the GameObject's properties.
     * </p>
     *
     * @param obj   The game object to be converted
     * @param world The Box2D world where the body is created
     * @return      A PhysicsVisualPair containing the visual and physics body
     */
    public static PhysicsVisualPair convert(GameObject obj, World world) {
        Physics physics = obj.getPhysics();
        String type = obj.getType(); // e.g., "rectangle", "circle"
        Shape visual = null;
        Body body = null;

        if ("rectangle".equalsIgnoreCase(type)) {
            float width = obj.getSize().getWidth();
            float height = obj.getSize().getHeight();
            float x = obj.getPosition().getX();
            float y = obj.getPosition().getY();

            // JavaFX visual
            Rectangle rect = new Rectangle(width, height, Color.valueOf(obj.getColour()) );
            rect.setTranslateX(x);
            rect.setTranslateY(y);
            visual = rect;

            // JBox2D body
            BodyDef def = new BodyDef();
            def.type = (physics.getShape().equals("DYNAMIC")) ? BodyType.DYNAMIC : BodyType.STATIC;
            def.position.set((x + width / 2) / SCALE, (y + height / 2) / SCALE);
            body = world.createBody(def);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2 / SCALE, height / 2 / SCALE);

            FixtureDef fixture = new FixtureDef();
            fixture.shape = shape;
            fixture.density = physics.getDensity();
            fixture.friction = physics.getFriction();
            fixture.restitution = physics.getRestitution();
            body.createFixture(fixture);

        } else if ("circle".equalsIgnoreCase(type)) {
            float radius = obj.getSize().getRadius();
            float x = obj.getPosition().getX();
            float y = obj.getPosition().getY();

            // JavaFX visual
            Circle circ = new Circle(radius, Color.valueOf(obj.getColour()));
            circ.setTranslateX(x);
            circ.setTranslateY(y);
            visual = circ;

            // JBox2D body
            BodyDef def = new BodyDef();
            def.type = (physics.getShape().equals("DYNAMIC")) ? BodyType.DYNAMIC : BodyType.STATIC;
            def.position.set(x / SCALE, y / SCALE);
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