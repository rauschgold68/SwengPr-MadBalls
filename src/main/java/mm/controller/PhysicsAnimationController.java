package mm.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import mm.Generated;
import mm.model.PhysicsVisualPair;
import mm.model.SimulationBounds;
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
    // Core simulation components
    private final World world;
    private final List<PhysicsVisualPair> pairs;
    private final SimulationModel model;
    
    // State management
    private final SimulationStateManager stateManager;
    
    // Helper classes following MVC pattern
    private final PhysicsPerformanceMonitor performanceMonitor;
    private final ObjectCullingController cullingManager;
    private final VisualUpdateController visualHandler;

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
        this.cullingManager = new ObjectCullingController(model);
        this.visualHandler = new VisualUpdateController();
        
        // Initialize unified state manager
        this.stateManager = new SimulationStateManager();
        
        // Setup space tracking
        this.stateManager.setupSpaceTracking(simSpace);
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
        this.cullingManager = new ObjectCullingController(model);
        this.visualHandler = new VisualUpdateController();
        
        // Initialize unified state manager with default dimensions
        this.stateManager = new SimulationStateManager();
    }

    /**
     * Sets the simulation space pane and initializes bounds tracking.
     * 
     * @param simSpace the JavaFX Pane representing the simulation space
     */
    @Generated
    public void setSimSpace(Pane simSpace) {
        this.stateManager.setupSpaceTracking(simSpace);
    }

    /**
     * Starts the animation timer and marks it as running.
     */
    @Generated
    @Override
    public void start(){
        stateManager.setRunning(true);
        super.start();
    }
    
    /**
     * Gets the current simulation space width.
     * @return the width of the simulation space
     */
    public double getSimSpaceWidth() {
        return stateManager.getSimSpaceWidth();
    }
    
    /**
     * Gets the current simulation space height.
     * @return the height of the simulation space
     */
    public double getSimSpaceHeight() {
        return stateManager.getSimSpaceHeight();
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
        if (stateManager.getLastTime() == 0) {
            stateManager.setLastTime(now);
            return;
        }

        long actualFrameTimeNs = now - stateManager.getLastTime();
        float frameTime = actualFrameTimeNs / 1_000_000_000.0f;
        stateManager.setLastTime(now);
        
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
        stateManager.addToAccumulator(cappedFrameTime);
        
        while (stateManager.getAccumulator() >= fixedTimeStep) {
            world.step(fixedTimeStep, performanceMonitor.getVelocityIterations(), 
                      performanceMonitor.getPositionIterations());
            stateManager.subtractFromAccumulator(fixedTimeStep);
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
            if (shouldCullPair(pos, objectName)) {
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
    private boolean shouldCullPair(Vec2 pos, String objectName) {
        double scaledX = pos.x * 50.0f; // Using SCALE constant
        double scaledY = pos.y * 50.0f;

        // Create SimulationBounds object using stateManager dimensions
        SimulationBounds bounds = new SimulationBounds(
            stateManager.getSimSpaceWidth(), 
            stateManager.getSimSpaceHeight()
        );

        return cullingManager.shouldCullObject(scaledX, scaledY, bounds, objectName);
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
        stateManager.reset();
        
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
        return stateManager.isRunning();
    }

    @Generated
    @Override
    public void stop() {
        stateManager.setRunning(false);
        super.stop();
    }
}