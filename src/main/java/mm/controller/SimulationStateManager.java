package mm.controller;

import javafx.scene.layout.Pane;

/**
 * Manages all simulation state including timing, dimensions, and running status.
 * This class consolidates state management to reduce field count in the main controller
 * while providing a cohesive interface for simulation state operations.
 */
public class SimulationStateManager {
    // Time-related state
    private long lastTime = 0;
    private float accumulator = 0.0f;
    private boolean running = false;
    
    // Space-related state
    private double simSpaceWidth;
    private double simSpaceHeight;
    
    /**
     * Creates a SimulationStateManager with default dimensions.
     */
    public SimulationStateManager() {
        this.simSpaceWidth = 800.0;
        this.simSpaceHeight = 600.0;
    }
    
    /**
     * Creates a SimulationStateManager with specified dimensions.
     * @param width the initial width
     * @param height the initial height
     */
    public SimulationStateManager(double width, double height) {
        this.simSpaceWidth = width;
        this.simSpaceHeight = height;
    }
    
    // ===== Time State Management =====
    
    /**
     * Gets the last recorded time.
     * @return the last time in nanoseconds
     */
    public long getLastTime() {
        return lastTime;
    }
    
    /**
     * Sets the last recorded time.
     * @param lastTime the time in nanoseconds
     */
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
    
    /**
     * Gets the physics accumulator value.
     * @return the accumulator value in seconds
     */
    public float getAccumulator() {
        return accumulator;
    }
    
    /**
     * Sets the physics accumulator value.
     * @param accumulator the accumulator value in seconds
     */
    public void setAccumulator(float accumulator) {
        this.accumulator = accumulator;
    }
    
    /**
     * Adds time to the accumulator.
     * @param deltaTime the time to add in seconds
     */
    public void addToAccumulator(float deltaTime) {
        this.accumulator += deltaTime;
    }
    
    /**
     * Subtracts time from the accumulator.
     * @param deltaTime the time to subtract in seconds
     */
    public void subtractFromAccumulator(float deltaTime) {
        this.accumulator -= deltaTime;
    }
    
    /**
     * Checks if the simulation is running.
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Sets the running state.
     * @param running the running state
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    // ===== Space State Management =====
    
    /**
     * Sets up dimension tracking for a simulation space pane.
     * @param simSpace the JavaFX Pane representing the simulation space
     */
    public void setupSpaceTracking(Pane simSpace) {
        // Get actual simulation space bounds
        this.simSpaceWidth = simSpace.getWidth();
        this.simSpaceHeight = simSpace.getHeight();
        
        // Listen for size changes
        simSpace.widthProperty().addListener((obs, old, newVal) -> 
            this.simSpaceWidth = newVal.doubleValue());
        simSpace.heightProperty().addListener((obs, old, newVal) -> 
            this.simSpaceHeight = newVal.doubleValue());
    }
    
    /**
     * Gets the current simulation space width.
     * @return the width of the simulation space
     */
    public double getSimSpaceWidth() {
        return simSpaceWidth;
    }
    
    /**
     * Gets the current simulation space height.
     * @return the height of the simulation space
     */
    public double getSimSpaceHeight() {
        return simSpaceHeight;
    }
    
    /**
     * Sets the simulation space width.
     * @param width the new width
     */
    public void setSimSpaceWidth(double width) {
        this.simSpaceWidth = width;
    }
    
    /**
     * Sets the simulation space height.
     * @param height the new height
     */
    public void setSimSpaceHeight(double height) {
        this.simSpaceHeight = height;
    }
    
    // ===== Combined State Operations =====
    
    /**
     * Resets all simulation state to initial values.
     */
    public void reset() {
        lastTime = 0;
        accumulator = 0.0f;
        // Note: running state and space dimensions are not reset
        // as they represent persistent configuration
    }
    
    /**
     * Gets a summary of the current simulation state for debugging.
     * @return a string representation of the current state
     */
    public String getStateInfo() {
        return String.format("SimulationState[running=%b, lastTime=%d, accumulator=%.3f, space=%.0fx%.0f]",
                running, lastTime, accumulator, simSpaceWidth, simSpaceHeight);
    }
}
