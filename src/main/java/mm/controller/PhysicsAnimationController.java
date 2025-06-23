package mm.controller;

import javafx.animation.AnimationTimer;
import mm.model.PhysicsVisualPair;

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
    /** The last timestamp when the timer was handled (nanoseconds). */
    private long lastTime = 0;
    /** The Box2D world to step. */
    private World world;
    /** The list of physics-visual pairs to update. */
    private List<PhysicsVisualPair> pairs;
    /** Whether the timer is currently running. */
    private boolean running = false;

    /**
     * Constructs a ResettableAnimationTimer for the given physics world and visual pairs.
     *
     * @param world the Box2D world to step
     * @param pairs the list of {@link PhysicsVisualPair} objects to update
     */
    public PhysicsAnimationController(World world, List<PhysicsVisualPair> pairs) {
        this.world = world;
        this.pairs = pairs;
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

        for (PhysicsVisualPair pair: pairs) {
            String tmp_name = (String) pair.body.getUserData();
            if (tmp_name.equalsIgnoreCase("ballon")) {
                float up = /*-6.15f;*/-1 / pair.body.getFixtureList().getDensity();
                Vec2 boyancy = new Vec2(0f, up);
                pair.body.applyForceToCenter(boyancy);
            }
        }

        float timeStep = (now - lastTime) / 1_000_000_000.0f;
        world.step(timeStep, 8, 3);
        lastTime = now;

        for (PhysicsVisualPair pair : pairs) {
            if (pair.visual != null && pair.body != null) {
                // SCALE must match the one used in GameObjectController
                float SCALE = 50.0f;
                Vec2 pos = pair.body.getPosition();
                double angle = Math.toDegrees(pair.body.getAngle());

                // For rectangles, set center
                if (pair.visual instanceof javafx.scene.shape.Rectangle) {
                    javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) pair.visual;
                    rect.setTranslateX(pos.x * SCALE - rect.getWidth() / 2);
                    rect.setTranslateY(pos.y * SCALE - rect.getHeight() / 2);
                    rect.setRotate(angle);
                }
                // For circles, set center
                else if (pair.visual instanceof javafx.scene.shape.Circle) {
                    javafx.scene.shape.Circle circ = (javafx.scene.shape.Circle) pair.visual;
                    circ.setTranslateX(pos.x * SCALE);
                    circ.setTranslateY(pos.y * SCALE);
                    circ.setRotate(angle);
                }
            }
        }
    }


    /**
     * Resets the timer so that the next call to {@link #handle(long)} will reinitialize timing.
     * Useful for restarting the simulation.
     */
    public void reset() {
        lastTime = 0;
    }

    /**
     * Returns whether the timer is currently running.
     *
     * @return {@code true} if the timer is running, {@code false} otherwise
     */
    public boolean isRunning(){
        return running;
    }
}