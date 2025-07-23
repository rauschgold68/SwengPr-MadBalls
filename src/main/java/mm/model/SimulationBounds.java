package mm.model;

/**
 * Represents the bounds of the simulation space.
 */
public class SimulationBounds {
    private final double width;
    private final double height;
    
    /**
     * Creates a new SimulationBounds instance.
     * 
     * @param width the width of the simulation space
     * @param height the height of the simulation space
     */
    public SimulationBounds(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Gets the width of the simulation space.
     * 
     * @return the width
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the simulation space.
     * 
     * @return the height
     */
    public double getHeight() {
        return height;
    }
}
