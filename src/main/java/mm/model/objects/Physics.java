package mm.model.objects;

/**
 * POJO representing the physics information for an object, used for jBox2d.
 * Contains properties such as body type, density, friction, restitution, and shape.
 */
public class Physics {
    /** The density of the object */
    private float density;
    /** The friction coefficient of the object */
    private float friction;
    /** The restitution (bounciness) of the object */
    private float restitution;
    /** The shape of the object (shows type of Object e.g. DYNAMIC, STATIC) */
    private String shape;

    /**
     * Gets the density of the object.
     * @return the density
     */
    public float getDensity() {return this.density;}

    /**
     * Sets the density of the object.
     * @param newDensity the new density to set
     */
    public void setDensity(float newDensity) {this.density = newDensity;}
    
    /**
     * Gets the friction coefficient of the object.
     * @return the friction
     */
    public float getFriction() {return this.friction;}

    /**
     * Sets the friction coefficient of the object.
     * @param newFriction the new friction to set
     */
    public void setFriction(float newFriction) {this.friction = newFriction;}
    
    /**
     * Gets the restitution (bounciness) of the object.
     * @return the restitution
     */
    public float getRestitution() {return this.restitution;}

    /**
     * Sets the restitution (bounciness) of the object.
     * @param newRestitution the new restitution to set
     */
    public void setRestitution(float newRestitution) {this.restitution = newRestitution;}
    
    /**
     * Gets the shape of the object.
     * @return the shape
     */
    public String getShape() {return this.shape;}

    /**
     * Sets the shape of the object.
     * @param newShape the new shape to set
     */
    public void setShape(String newShape) {this.shape = newShape;}

}