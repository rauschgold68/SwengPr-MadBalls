package mm.model.objects;

/**
 * Representing the size of an object for JavaFX and physics calculations.
 * <p>
 * Stores width, height, and radius as floats. Used for both rectangular and circular shapes.
 * For rectangles, width and height are used. For circles, radius is used.
 * </p>
 * <ul>
 *   <li><b>width</b>: The width of the object (for rectangles).</li>
 *   <li><b>height</b>: The height of the object (for rectangles).</li>
 *   <li><b>radius</b>: The radius of the object (for circles).</li>
 * </ul>
 * 
 * Example usage:
 * <pre>
 *     Size rectSize = new Size();
 *     rectSize.setWidth(100.0f);
 *     rectSize.setHeight(50.0f);
 *     
 *     Size circleSize = new Size();
 *     circleSize.setRadius(25.0f);
 * </pre>
 */
public class Size {
    /** The width of the object (for rectangles). */
    private float width;
    /** The height of the object (for rectangles). */
    private float height;
    /** The radius of the object (for circular shapes). */
    private float radius;

    /**
     * Returns the width of the object.
     * 
     * @return the width as a float
     */
    public float getWidth() {return this.width;}

    /**
     * Sets the width of the object.
     * 
     * @param w the new width to set
     */
    public void setWidth(float w) {this.width = w;}

    /**
     * Returns the height of the object.
     * 
     * @return the height as a float
     */
    public float getHeight() {return this.height;}

    /**
     * Sets the height of the object.
     * 
     * @param h the new height to set
     */
    public void setHeight(float h) {this.height = h;}

    /**
     * Returns the radius of the object.
     * 
     * @return the radius as a float
     */
    public float getRadius() {return this.radius;}

    /**
     * Sets the radius of the object.
     * 
     * @param r the new radius to set
     */
    public void setRadius(float r) {this.radius= r;}
}