package mm.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import mm.Generated;
import mm.model.PhysicsVisualPair;
import mm.model.SimulationModel;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.List;

/**
 * An extension of {@link AnimationTimer} that advances the physics simulation and updates
 * the JavaFX visuals for all {@link PhysicsVisualPair} objects in real time.
 * <p>
 * This timer can be reset and queried for its running state, making it suitable for
 * pausing, resuming, and restarting the simulation. It steps the Box2D {@link World}
 * and synchronizes the position and rotation of JavaFX shapes (rectangles and circles)
 * with their corresponding physics bodies.
 * </p>
 * <ul>
 *   <li>Uses a fixed scale (SCALE = 50.0f) to convert between physics and JavaFX coordinates.</li>
 *   <li>Handles both rectangles and circles, updating their translation and rotation.</li>
 *   <li>Provides a {@link #reset()} method to restart the timer and a {@link #isRunning()} method to check state.</li>
 * </ul>
 * 
 * Example usage:
 * <pre>
 *     ResettableAnimationTimer timer = new ResettableAnimationTimer(world, pairs);
 *     timer.start();
 *     // ...
 *     timer.reset();
 *     timer.stop();
 * </pre>
 */
public class PhysicsAnimationController extends AnimationTimer {
    // Core simulation fields
    private long lastTime = 0;
    private final World world;
    private final List<PhysicsVisualPair> pairs;
    private final SimulationModel model;
    private boolean running = false;
    private float accumulator = 0.0f;
    
    // Simulation space dimensions
    private double simSpaceWidth;
    private double simSpaceHeight;
    
    // Helper classes following MVC pattern
    private final PhysicsPerformanceMonitor performanceMonitor;
    private final ObjectCullingManager cullingManager;
    private final VisualUpdateHandler visualHandler;

    /**
     * Constructs a PhysicsAnimationController for the given physics world and visual pairs.
     *
     * @param world the Box2D world to step
     * @param pairs the list of {@link PhysicsVisualPair} objects to update
     * @param model the SimulationModel to update when objects are culled
     * @param simSpace the JavaFX Pane representing the simulation space
     */
    @Generated
    public PhysicsAnimationController(World world, List<PhysicsVisualPair> pairs, SimulationModel model, Pane simSpace) {
        this.world = world;
        this.pairs = pairs;
        this.model = model;
        
        // Initialize helper classes
        this.performanceMonitor = new PhysicsPerformanceMonitor();
        this.cullingManager = new ObjectCullingManager(model);
        this.visualHandler = new VisualUpdateHandler();
        
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
     * Constructs a PhysicsAnimationController without simSpace (to be set later).
     *
     * @param world the Box2D world to step
     * @param pairs the list of {@link PhysicsVisualPair} objects to update
     * @param model the SimulationModel to update when objects are culled
     */
    public PhysicsAnimationController(World world, List<PhysicsVisualPair> pairs, SimulationModel model) {
        this.world = world;
        this.pairs = pairs;
        this.model = model;
        
        // Initialize helper classes
        this.performanceMonitor = new PhysicsPerformanceMonitor();
        this.cullingManager = new ObjectCullingManager(model);
        this.visualHandler = new VisualUpdateHandler();
        
        // Default dimensions - will be updated when simSpace is set
        this.simSpaceWidth = 800.0;
        this.simSpaceHeight = 600.0;
    }

    /**
     * Sets the simulation space pane and initializes bounds tracking.
     * 
     * @param simSpace the JavaFX Pane representing the simulation space
     */
    @Generated
    public void setSimSpace(Pane simSpace) {
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
     * Starts the animation timer and marks it as running.
     */
    @Generated
    @Override
    public void start(){
        running = true;
        super.start();
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
     * Advances the physics simulation and updates the JavaFX visuals.
     * <p>
     * Called automatically by the JavaFX runtime. Steps the Box2D world by the elapsed time,
     * then updates the position and rotation of each visual shape to match its physics body.
     * </p>
     *
     * @param now the current timestamp in nanoseconds
     */
    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        long actualFrameTimeNs = now - lastTime;
        float frameTime = actualFrameTimeNs / 1_000_000_000.0f;
        lastTime = now;
        
        // Update performance monitoring
        performanceMonitor.updatePerformance(actualFrameTimeNs);
        
        // Step physics simulation
        stepPhysicsSimulation(frameTime);
        
        // Process all physics-visual pairs
        processPhysicsVisualPairs();
        
        // Handle object removal
        handleObjectRemoval();
    }
    
    /**
     * Steps the physics simulation with fixed time steps.
     * 
     * @param frameTime the frame time in seconds
     */
    private void stepPhysicsSimulation(float frameTime) {
        // Cap maximum frame time to prevent physics instability
        float cappedFrameTime = Math.min(frameTime, 0.05f); // Max 50ms frame time
        
        // Fixed time step for deterministic physics (60 FPS)
        final float fixedTimeStep = 1.0f / 60.0f;
        
        // Accumulate time and step physics in fixed intervals
        accumulator += cappedFrameTime;
        
        while (accumulator >= fixedTimeStep) {
            world.step(fixedTimeStep, performanceMonitor.getVelocityIterations(), 
                      performanceMonitor.getPositionIterations());
            accumulator -= fixedTimeStep;
        }
    }
    
    /**
     * Processes all physics-visual pairs for culling and updates.
     */
    private void processPhysicsVisualPairs() {
        cullingManager.clearRemovalLists();

        for (PhysicsVisualPair pair : pairs) {
            if (pair.body == null) {
                continue;
            }
            
            Vec2 pos = pair.body.getPosition();
            double angle = Math.toDegrees(pair.body.getAngle());
            String objectName = (String) pair.body.getUserData();
            
            // Check for culling
            if (shouldCullPair(pair, pos, objectName)) {
                cullingManager.cullObject(pair, objectName);
                continue;
            }
            
            // Apply balloon physics if needed
            applyBalloonPhysics(pair, objectName);
            
            // Update visual position
            visualHandler.updateVisualPosition(pair, pos, angle);
        }
    }
    
    /**
     * Determines if a pair should be culled based on position.
     */
    private boolean shouldCullPair(PhysicsVisualPair pair, Vec2 pos, String objectName) {
        double scaledX = pos.x * 50.0f; // Using SCALE constant
        double scaledY = pos.y * 50.0f;
        
        return cullingManager.shouldCullObject(scaledX, scaledY, simSpaceWidth, simSpaceHeight, objectName);
    }
    
    /**
     * Applies balloon-specific physics forces.
     */
    private void applyBalloonPhysics(PhysicsVisualPair pair, String objectName) {
        if ("ballon".equalsIgnoreCase(objectName)) {
            Vec2 buoyancy = new Vec2(0f, -3.8f);
            pair.body.applyForceToCenter(buoyancy);
        }
    }
    
    /**
     * Handles removal of culled objects from simulation.
     */
    private void handleObjectRemoval() {
        if (cullingManager.getPairsToRemove().isEmpty()) {
            return;
        }
        
        // Remove physics bodies from world
        removePhysicsBodies();
        
        // Remove visuals from scene
        visualHandler.batchRemoveVisuals(cullingManager.getVisualsToRemove());
        
        // Update model collections
        updateModelCollections();
        
        // Restore inventory counts
        restoreInventoryCounts();
    }
    
    /**
     * Removes physics bodies from the world.
     */
    private void removePhysicsBodies() {
        for (PhysicsVisualPair pair : cullingManager.getPairsToRemove()) {
            world.destroyBody(pair.body);
            System.out.println(pair.getBody().getUserData());
        }
    }
    
    /**
     * Updates model collections after object removal.
     */
    private void updateModelCollections() {
        pairs.removeAll(cullingManager.getPairsToRemove());
        model.getDroppedObjects().removeAll(cullingManager.getObjectsToRemove());
        model.getDroppedPhysicsVisualPairs().removeAll(cullingManager.getPairsToRemove());
    }
    
    /**
     * Restores inventory counts for removed objects.
     */
    private void restoreInventoryCounts() {
        for (mm.model.GameObject obj : cullingManager.getObjectsToRemove()) {
            model.incrementInventoryCount(obj.getName());
            cullingManager.removeFromCache(obj.getName());
        }
    }

    /**
     * Resets the timer and restores all culled objects to their original positions.
     */
    @Generated
    public void reset() {
        lastTime = 0;
        accumulator = 0.0f; // Reset physics accumulator
        
        // Clear object cache and restore objects
        cullingManager.clearObjectCache();
        cullingManager.restoreAllCulledObjects();
    }
    
    /**
     * Updates the object name mapping cache when new objects are added.
     * This should be called when objects are added to the simulation.
     */
    public void updateObjectCache() {
        cullingManager.updateObjectCache();
    }

    /**
     * Returns whether the timer is currently running.
     *
     * @return {@code true} if the timer is running, {@code false} otherwise
     */
    public boolean isRunning(){
        return running;
    }

    @Generated
    @Override
    public void stop() {
        running = false;
        super.stop();
    }
}