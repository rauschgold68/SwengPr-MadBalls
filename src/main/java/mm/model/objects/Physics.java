package mm.model.objects;

/**
 * Represents the physics properties for a game object, used for configuring jBox2D bodies.
 * <p>
 * This class encapsulates physical attributes such as density, friction, restitution (bounciness),
 * and the body type (shape) of the object (e.g., DYNAMIC, STATIC).
 * These properties are used to control the physical behavior of objects in the simulation.
 * </p>
 * <ul>
 *   <li><b>density</b>: The mass per unit area of the object.</li>
 *   <li><b>friction</b>: The resistance to sliding motion.</li>
 *   <li><b>restitution</b>: The bounciness of the object (0 = no bounce, 1 = perfect bounce).</li>
 *   <li><b>shape</b>: The body type as a string (e.g., "DYNAMIC", "STATIC").</li>
 * </ul>
 * 
 * Example usage:
 * <pre>
 *     Physics physics = new Physics();
 *     physics.setDensity(1.0f);
 *     physics.setFriction(0.5f);
 *     physics.setRestitution(0.3f);
 *     physics.setShape("DYNAMIC");
 * </pre>
 */
public class Physics {
    /** The density of the object (mass per unit area). */
    private float density;
    /** The friction coefficient of the object (resistance to sliding). */
    private float friction;
    /** The restitution (bounciness) of the object (0 = no bounce, 1 = perfect bounce). */
    private float restitution;
    /** The shape/body type of the object (e.g., "DYNAMIC", "STATIC"). */
    private String shape;

    /**
     * Gets the density of the object.
     * 
     * @return the density value
     */
    public float getDensity() { return this.density; }

    /**
     * Sets the density of the object.
     * 
     * @param newDensity the new density to set
     */
    public void setDensity(float newDensity) { this.density = newDensity; }
    
    /**
     * Gets the friction coefficient of the object.
     * 
     * @return the friction value
     */
    public float getFriction() { return this.friction; }

    /**
     * Sets the friction coefficient of the object.
     * 
     * @param newFriction the new friction to set
     */
    public void setFriction(float newFriction) { this.friction = newFriction; }
    
    /**
     * Gets the restitution (bounciness) of the object.
     * 
     * @return the restitution value
     */
    public float getRestitution() { return this.restitution; }

    /**
     * Sets the restitution (bounciness) of the object.
     * 
     * @param newRestitution the new restitution to set
     */
    public void setRestitution(float newRestitution) { this.restitution = newRestitution; }
    
    /**
     * Gets the shape/body type of the object.
     * 
     * @return the shape as a string (e.g., "DYNAMIC", "STATIC")
     */
    public String getShape() { return this.shape; }

    /**
     * Sets the shape/body type of the object.
     * 
     * @param newShape the new shape to set (e.g., "DYNAMIC", "STATIC")
     */
    public void setShape(String newShape) { this.shape = newShape; }
}