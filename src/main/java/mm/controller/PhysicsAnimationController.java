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

        float timeStep = (now - lastTime) / 1_000_000_000.0f;
        world.step(timeStep, 8, 3);
        lastTime = now;

        // Create lists to track objects to remove
        List<PhysicsVisualPair> pairsToRemove = new ArrayList<>();
        List<mm.model.GameObject> objectsToRemove = new ArrayList<>();

        for (PhysicsVisualPair pair : pairs) {
            if (pair.visual != null && pair.body != null) {
                float SCALE = 50.0f;
                Vec2 pos = pair.body.getPosition();
                double angle = Math.toDegrees(pair.body.getAngle());

                // Check if object is out of bounds
                double scaledX = pos.x * SCALE;
                double scaledY = pos.y * SCALE;
                
                // Get object type
                String objectName = (String) pair.body.getUserData();
                
                // Use smaller margin for balloons to cull them faster
                double margin = objectName.equalsIgnoreCase("ballon") ? 50.0 : 100.0;
                
                // Add extra vertical check for balloons to catch them earlier when rising
                boolean shouldCull = scaledX < -margin || scaledX > simSpaceWidth + margin || 
                                   scaledY < -margin || scaledY > simSpaceHeight + margin;
                                   
                if (objectName.equalsIgnoreCase("ballon")) {
                    // Additional early culling check for balloons going up
                    shouldCull = shouldCull || scaledY < simSpaceHeight * 0.1; // Cull if in top 10% of screen
                }

                if (shouldCull) {
                    pairsToRemove.add(pair);
                    
                    // Store original position and object for restoration
                    for (mm.model.GameObject obj : model.getDroppedObjects()) {
                        if (obj.getName().equals(objectName)) {
                            objectsToRemove.add(obj);
                            culledObjects.add(obj);
                            originalPositions.add(new Vec2(obj.getPosition().getX(), obj.getPosition().getY()));
                            break;
                        }
                    }
                    continue;
                }

                // Apply balloon physics if applicable
                String tmp_name = (String) pair.body.getUserData();
                if (tmp_name.equalsIgnoreCase("ballon")) {
                    float up = -1 / pair.body.getFixtureList().getDensity();
                    Vec2 boyancy = new Vec2(0f, up);
                    pair.body.applyForceToCenter(boyancy);
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
                }
            }
        }

        // Remove culled objects
        if (!pairsToRemove.isEmpty()) {
            // Remove physics bodies from world
            for (PhysicsVisualPair pair : pairsToRemove) {
                world.destroyBody(pair.body);
                System.out.println(pair.getBody().getUserData());
                if (pair.visual != null && pair.visual.getParent() != null) {
                    // Remove visual from UI on JavaFX thread
                    Platform.runLater(() -> {
                        Parent parent = pair.visual.getParent();
                        if (parent instanceof Group) {
                            ((Group) parent).getChildren().remove(pair.visual);
                        } else if (parent instanceof Pane) {
                            ((Pane) parent).getChildren().remove(pair.visual);
                        }
                    });
                }
            }

            // Update model collections
            pairs.removeAll(pairsToRemove);
            model.getDroppedObjects().removeAll(objectsToRemove);
            model.getDroppedPhysicsVisualPairs().removeAll(pairsToRemove);

            // Restore inventory counts for removed objects
            for (mm.model.GameObject obj : objectsToRemove) {
                model.incrementInventoryCount(obj.getName());
            }
        }
    }

    /**
     * Resets the timer and restores all culled objects to their original positions.
     */
    public void reset() {
        lastTime = 0;
        
        // Restore all culled objects to their original positions
        for (int i = 0; i < culledObjects.size(); i++) {
            mm.model.GameObject obj = culledObjects.get(i);
            Vec2 originalPos = originalPositions.get(i);
            
            // Reset object position
            obj.getPosition().setX(originalPos.x);
            obj.getPosition().setY(originalPos.y);
            
            // Add back to model
            model.addDroppedObject(obj);
            
            // Decrement inventory count since object is being restored
            model.decrementInventoryCount(obj.getName());
        }
        
        // Clear the culled objects lists
        culledObjects.clear();
        originalPositions.clear();
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