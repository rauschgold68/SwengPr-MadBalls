package mm;

import javafx.scene.shape.Shape;
import org.jbox2d.dynamics.Body;

/**
 * Represents a pair consisting of a JavaFX visual and a JBox2D Body.
 * <p>
 * This class is used to associate a visual representation of a physics object (for rendering in JavaFX)
 * with its corresponding physical body (for simulation in JBox2D). 
 * </p>
 *
 */
public class PhysicsVisualPair {
    /** The JavaFX visual representation*/
    public final Shape visual;

    /** The JBox2D physics body associated with the visual.*/
    public final Body body;

    /**
     * Constructs a new PhysicsVisualPair} with the specified visual and body.
     *
     * @param visual the JavaFX Shape representing the object visually.
     * @param body   the JBox2D Body representing the object's physics.
     */
    public PhysicsVisualPair(Shape visual, Body body){
        this.visual = visual;
        this.body = body;
    }

    /**
     * Returns the JavaFX visual Shape associated with this pair.
     *
     * @return the visual Shape}, or null if not set
     */
    public Shape getVisual() {
        return this.visual;
    }

    /**
     * Returns the JBox2D Body associated with this pair.
     *
     * @return the physics Body, or null if not set
     */
    public Body getBody() {
        return this.body;
    }
}
