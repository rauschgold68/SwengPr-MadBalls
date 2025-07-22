package mm.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import mm.model.PhysicsVisualPair;
import mm.model.SimulationModel;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
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
    /** The last timestamp when the timer was handled (nanoseconds). */
    private long lastTime = 0;
    /** The Box2D world to step. */
    private World world;
    /** The list of physics-visual pairs to update. */
    private List<PhysicsVisualPair> pairs;
    /** Whether the timer is currently running. */
    private boolean running = false;

    // Add new fields
    private double simSpaceWidth;
    private double simSpaceHeight;
    private final SimulationModel model;

    // Add new field to store culled objects
    private final List<mm.model.GameObject> culledObjects = new ArrayList<>();
    private final List<Vec2> originalPositions = new ArrayList<>();
    
    // Physics timing accumulator for fixed time step
    private float accumulator = 0.0f;
    
    // Cache for object-to-pair mapping to avoid linear searches
    private final java.util.Map<String, mm.model.GameObject> objectNameMap = new java.util.HashMap<>();
    
    // Pre-allocated lists for removal to avoid creating new objects each frame
    private final List<PhysicsVisualPair> pairsToRemove = new ArrayList<>();
    private final List<mm.model.GameObject> objectsToRemove = new ArrayList<>();
    private final List<javafx.scene.Node> visualsToRemove = new ArrayList<>();
    
    // Adaptive performance settings
    private int velocityIterations = 6;
    private int positionIterations = 2;
    private long frameTimeHistory = 0;
    private int frameCount = 0;
    private static final long TARGET_FRAME_TIME_NS = 16_666_666L; // ~60 FPS in nanoseconds

    /**
     * Constructs a PhysicsAnimationController for the given physics world and visual pairs.
     *
     * @param world the Box2D world to step
     * @param pairs the list of {@link PhysicsVisualPair} objects to update
     * @param model the SimulationModel to update when objects are culled
     * @param simSpace the JavaFX Pane representing the simulation space
     */
    public PhysicsAnimationController(World world, List<PhysicsVisualPair> pairs, SimulationModel model, Pane simSpace) {
        this.world = world;
        this.pairs = pairs;
        this.model = model;
        
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
        
        // Default dimensions - will be updated when simSpace is set
        this.simSpaceWidth = 800.0;
        this.simSpaceHeight = 600.0;
    }

    /**
     * Sets the simulation space pane and initializes bounds tracking.
     * 
     * @param simSpace the JavaFX Pane representing the simulation space
     */
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
    @Override
    public void start(){
        running = true;
        super.start();
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

        // Use fixed time step with accumulation for deterministic physics
        float frameTime = (now - lastTime) / 1_000_000_000.0f;
        long actualFrameTimeNs = now - lastTime;
        lastTime = now;
        
        // Adaptive quality adjustment based on performance
        updateAdaptiveQuality(actualFrameTimeNs);
        
        // Cap maximum frame time to prevent physics instability
        frameTime = Math.min(frameTime, 0.05f); // Max 50ms frame time
        
        // Fixed time step for deterministic physics (60 FPS)
        final float fixedTimeStep = 1.0f / 60.0f;
        
        // Accumulate time and step physics in fixed intervals
        accumulator += frameTime;
        
        // Reduced iteration counts for better performance on older hardware
        while (accumulator >= fixedTimeStep) {
            world.step(fixedTimeStep, velocityIterations, positionIterations);
            accumulator -= fixedTimeStep;
        }

        // Clear pre-allocated removal lists for reuse
        pairsToRemove.clear();
        objectsToRemove.clear();
        visualsToRemove.clear();

        for (PhysicsVisualPair pair : pairs) {
            if (pair.visual != null && pair.body != null) {
                float SCALE = 50.0f;
                Vec2 pos = pair.body.getPosition();
                double angle = Math.toDegrees(pair.body.getAngle());

                // Check if object is out of bounds
                double scaledX = pos.x * SCALE;
                double scaledY = pos.y * SCALE;
                
                // Get object type - cache the string to avoid repeated getUserData() calls
                String objectName = (String) pair.body.getUserData();
                boolean isBalloon = "ballon".equalsIgnoreCase(objectName);
                
                // Use smaller margin for balloons to cull them faster
                double margin = isBalloon ? 50.0 : 100.0;
                
                // Add extra vertical check for balloons to catch them earlier when rising
                boolean shouldCull = scaledX < -margin || scaledX > simSpaceWidth + margin || 
                                   scaledY < -margin || scaledY > simSpaceHeight + margin;
                                   
                if (isBalloon) {
                    // Additional early culling check for balloons going up
                    shouldCull = shouldCull || scaledY < simSpaceHeight * 0.1; // Cull if in top 1% of screen
                }

                if (shouldCull) {
                    pairsToRemove.add(pair);
                    
                    // Use cached mapping if available, otherwise fall back to linear search
                    mm.model.GameObject matchedObj = objectNameMap.get(objectName);
                    if (matchedObj == null) {
                        // Fall back to linear search and cache the result
                        for (mm.model.GameObject obj : model.getDroppedObjects()) {
                            if (obj.getName().equals(objectName)) {
                                matchedObj = obj;
                                objectNameMap.put(objectName, obj);
                                break;
                            }
                        }
                    }
                    
                    if (matchedObj != null) {
                        objectsToRemove.add(matchedObj);
                        culledObjects.add(matchedObj);
                        originalPositions.add(new Vec2(matchedObj.getPosition().getX(), matchedObj.getPosition().getY()));
                    }
                    
                    // Queue visual for removal
                    if (pair.visual.getParent() != null) {
                        visualsToRemove.add(pair.visual);
                    }
                    continue;
                }

                if (isBalloon) {
    
                    Vec2 buoyancy = new Vec2(0f, -3.8f);
                    pair.body.applyForceToCenter(buoyancy);
                }
                
                // Update visual positions as before
                if (pair.visual instanceof javafx.scene.shape.Rectangle) {
                    javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) pair.visual;
                    rect.setTranslateX(pos.x * SCALE - rect.getWidth() / 2);
                    rect.setTranslateY(pos.y * SCALE - rect.getHeight() / 2);
                    rect.setRotate(angle);
                } else if (pair.visual instanceof javafx.scene.shape.Circle) {
                    javafx.scene.shape.Circle circ = (javafx.scene.shape.Circle) pair.visual;
                    circ.setTranslateX(pos.x * SCALE);
                    circ.setTranslateY(pos.y * SCALE);
                    circ.setRotate(angle);
                } else if (pair.visual instanceof javafx.scene.shape.Polygon) {
                    // Handle bucket (polygon) positioning - center like rectangles
                    javafx.scene.shape.Polygon polygon = (javafx.scene.shape.Polygon) pair.visual;
                    javafx.geometry.Bounds bounds = polygon.getBoundsInLocal();
                    polygon.setTranslateX(pos.x * SCALE - bounds.getWidth() / 2);
                    polygon.setTranslateY(pos.y * SCALE - bounds.getHeight() / 2);
                    polygon.setRotate(angle);
                }
            }
        }

        // Remove culled objects
        if (!pairsToRemove.isEmpty()) {
            // Remove physics bodies from world
            for (PhysicsVisualPair pair : pairsToRemove) {
                world.destroyBody(pair.body);
                System.out.println(pair.getBody().getUserData());
            }
            
            // Batch visual removal in a single Platform.runLater call for better performance
            if (!visualsToRemove.isEmpty()) {
                Platform.runLater(() -> {
                    for (javafx.scene.Node visual : visualsToRemove) {
                        Parent parent = visual.getParent();
                        if (parent instanceof Group) {
                            ((Group) parent).getChildren().remove(visual);
                        } else if (parent instanceof Pane) {
                            ((Pane) parent).getChildren().remove(visual);
                        }
                    }
                });
            }

            // Update model collections
            pairs.removeAll(pairsToRemove);
            model.getDroppedObjects().removeAll(objectsToRemove);
            model.getDroppedPhysicsVisualPairs().removeAll(pairsToRemove);

            // Restore inventory counts for removed objects
            for (mm.model.GameObject obj : objectsToRemove) {
                model.incrementInventoryCount(obj.getName());
                // Remove from cache since object is being removed
                objectNameMap.remove(obj.getName());
            }
        }
    }

    /**
     * Resets the timer and restores all culled objects to their original positions.
     */
    public void reset() {
        lastTime = 0;
        accumulator = 0.0f; // Reset physics accumulator
        
        // Clear object cache since we're resetting
        objectNameMap.clear();
        
        // Restore all culled objects to their original positions
        for (int i = 0; i < culledObjects.size(); i++) {
            mm.model.GameObject obj = culledObjects.get(i);
            Vec2 originalPos = originalPositions.get(i);
            
            // Reset object position
            obj.getPosition().setX(originalPos.x);
            obj.getPosition().setY(originalPos.y);
            
            // Add back to model
            model.addDroppedObject(obj);
            
            // Update cache
            objectNameMap.put(obj.getName(), obj);
            
            // Decrement inventory count since object is being restored
            model.decrementInventoryCount(obj.getName());
        }
        
        // Clear the culled objects lists
        culledObjects.clear();
        originalPositions.clear();
    }
    
    /**
     * Updates the object name mapping cache when new objects are added.
     * This should be called when objects are added to the simulation.
     */
    public void updateObjectCache() {
        objectNameMap.clear();
        for (mm.model.GameObject obj : model.getDroppedObjects()) {
            objectNameMap.put(obj.getName(), obj);
        }
    }
    
    /**
     * Adaptively adjusts physics quality based on frame performance.
     * Reduces iteration counts when performance is poor to maintain smooth simulation.
     */
    private void updateAdaptiveQuality(long frameTimeNs) {
        frameCount++;
        frameTimeHistory += frameTimeNs;
        
        // Adjust quality every 60 frames (about once per second at 60 FPS)
        if (frameCount >= 60) {
            long avgFrameTime = frameTimeHistory / frameCount;
            
            if (avgFrameTime > TARGET_FRAME_TIME_NS * 1.5) {
                // Performance is poor, reduce quality
                if (velocityIterations > 3) velocityIterations--;
                if (positionIterations > 1) positionIterations--;
            } else if (avgFrameTime < TARGET_FRAME_TIME_NS * 0.8) {
                // Performance is good, can increase quality
                if (velocityIterations < 8) velocityIterations++;
                if (positionIterations < 3) positionIterations++;
            }
            
            // Reset counters
            frameCount = 0;
            frameTimeHistory = 0;
        }
    }

    /**
     * Returns whether the timer is currently running.
     *
     * @return {@code true} if the timer is running, {@code false} otherwise
     */
    public boolean isRunning(){
        return running;
    }

    @Override
    public void stop() {
        running = false;
        super.stop();
    }
}