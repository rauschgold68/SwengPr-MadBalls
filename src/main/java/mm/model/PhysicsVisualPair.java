package mm.model;

import javafx.scene.shape.Shape;
import org.jbox2d.dynamics.Body;

/**
 * Represents a pair consisting of a JavaFX visual {@link Shape} and a JBox2D {@link Body}.
 * <p>
 * This class is used to associate a visual representation of a physics object (for rendering in JavaFX)
 * with its corresponding physical body (for simulation in JBox2D). 
 * It is commonly used as a return type for converters that generate both the visual and physical
 * representations of game objects, ensuring they remain linked throughout the simulation and rendering process.
 * </p>
 * <p>
 * The {@link #visual} field holds the JavaFX {@code Shape} used for drawing the object on screen,
 * while the {@link #body} field holds the JBox2D {@code Body} used for physics simulation.
 * </p>
 * <b>Usage example:</b>
 * <pre>
 *     PhysicsVisualPair pair = GameObjectController.convert(gameObject, world);
 *     Shape visual = pair.getVisual();
 *     Body body = pair.getBody();
 * </pre>
 */
public class PhysicsVisualPair {
    /** The JavaFX visual representation of the object (for rendering). */
    public final Shape visual;

    /** The JBox2D physics body associated with the visual (for simulation). */
    public final Body body;

    /**
     * Constructs a new {@code PhysicsVisualPair} with the specified visual and body.
     *
     * @param visual the JavaFX {@link Shape} representing the object visually (may be {@code null})
     * @param body   the JBox2D {@link Body} representing the object's physics (may be {@code null})
     */
    public PhysicsVisualPair(Shape visual, Body body){
        this.visual = visual;
        this.body = body;
    }

    /**
     * Returns the JavaFX visual {@link Shape} associated with this pair.
     *
     * @return the visual {@link Shape}, or {@code null} if not set
     */
    public Shape getVisual() {
        return this.visual;
    }

    /**
     * Returns the JBox2D {@link Body} associated with this pair.
     *
     * @return the physics {@link Body}, or {@code null} if not set
     */
    public Body getBody() {
        return this.body;
    }
}
