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

public class GameObjectConverter {
    private static final float SCALE = 50.0f;

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
            Rectangle rect = new Rectangle(width, height, Color.CORNFLOWERBLUE);
            rect.setTranslateX(x);
            rect.setTranslateY(y);
            visual = rect;

            // JBox2D body
            BodyDef def = new BodyDef();
            def.type = physics.isDynamic() ? BodyType.DYNAMIC : BodyType.STATIC;
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
            Circle circ = new Circle(radius, Color.STEELBLUE);
            circ.setTranslateX(x);
            circ.setTranslateY(y);
            visual = circ;

            // JBox2D body
            BodyDef def = new BodyDef();
            def.type = physics.isDynamic() ? BodyType.DYNAMIC : BodyType.STATIC;
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