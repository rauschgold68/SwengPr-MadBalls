package mm.model;

import org.jbox2d.dynamics.Body;

/**
 * Represents a pair consisting of geometry data and a JBox2D {@link Body}.
 * <p>
 * This class replaces PhysicsVisualPair and removes direct JavaFX dependencies
 * from the model layer. The geometry data provides mathematical representations
 * that can be used by any view technology.
 * </p>
 * <p>
 * The {@link #geometry} field holds the mathematical shape representation,
 * while the {@link #body} field holds the JBox2D {@code Body} used for physics simulation.
 * </p>
 */
public class PhysicsGeometryPair {
    /** The geometry representation of the object (view-agnostic). */
    public final GeometryData geometry;

    /** The JBox2D physics body associated with the geometry (for simulation). */
    public final Body body;

    /**
     * Constructs a new {@code PhysicsGeometryPair} with the specified geometry and body.
     *
     * @param geometry the geometry data representing the object (may be {@code null})
     * @param body     the JBox2D {@link Body} representing the object's physics (may be {@code null})
     */
    public PhysicsGeometryPair(GeometryData geometry, Body body) {
        this.geometry = geometry;
        this.body = body;
    }

    /**
     * Returns the geometry data associated with this pair.
     *
     * @return the geometry data, or {@code null} if not set
     */
    public GeometryData getGeometry() {
        return this.geometry;
    }

    /**
     * Returns the JBox2D physics {@link Body} associated with this pair.
     *
     * @return the physics {@link Body}, or {@code null} if not set
     */
    public Body getBody() {
        return this.body;
    }
}
