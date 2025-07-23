package mm.controller;

import org.jbox2d.common.Vec2;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import mm.model.GameObject;
import mm.model.PhysicsVisualPair;

/**
 * Utility class for calculating and comparing positions between physics bodies
 * and game objects in the simulation.
 * <p>
 * This class handles various shape-specific position calculations and comparisons,
 * providing a clean interface for the SimulationController to use.
 * </p>
 */
public class PositionCalculator {

    /**
     * Helper class to hold expected position coordinates.
     */
    public static class ExpectedPosition {
        public final float x;
        public final float y;

        /**
         * Constructor
         * @param x the x coordinate
         * @param y the y coordinate
         */
        public ExpectedPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Calculates the expected visual position from the physics body position.
     * 
     * @param pair The physics-visual pair to calculate position for
     * @return The expected position
     */
    public static ExpectedPosition calculateExpectedPosition(PhysicsVisualPair pair) {
        Vec2 bodyPos = pair.body.getPosition();

        if (pair.visual instanceof Rectangle) {
            return calculateRectanglePosition(pair, bodyPos);
        } else if (pair.visual instanceof Polygon) {
            return calculatePolygonPosition(pair, bodyPos);
        } else {
            // Default case for circles and other shapes
            return new ExpectedPosition(bodyPos.x * 50.0f, bodyPos.y * 50.0f);
        }
    }

    /**
     * Calculates expected position for rectangle shapes.
     * 
     * @param pair The physics-visual pair containing a rectangle
     * @param bodyPos The body position from the physics engine
     * @return The expected position
     */
    private static ExpectedPosition calculateRectanglePosition(PhysicsVisualPair pair, Vec2 bodyPos) {
        Rectangle rect = (Rectangle) pair.visual;
        float expectedX = (float) (bodyPos.x * 50.0f - rect.getWidth() / 2);
        float expectedY = (float) (bodyPos.y * 50.0f - rect.getHeight() / 2);
        return new ExpectedPosition(expectedX, expectedY);
    }

    /**
     * Calculates expected position for polygon shapes (buckets).
     * 
     * @param pair The physics-visual pair containing a polygon
     * @param bodyPos The body position from the physics engine
     * @return The expected position
     */
    private static ExpectedPosition calculatePolygonPosition(PhysicsVisualPair pair, Vec2 bodyPos) {
        Polygon polygon = (Polygon) pair.visual;
        javafx.geometry.Bounds bounds = polygon.getBoundsInLocal();
        float expectedX = (float) (bodyPos.x * 50.0f - bounds.getWidth() / 2);
        float expectedY = (float) (bodyPos.y * 50.0f - bounds.getHeight() / 2);
        return new ExpectedPosition(expectedX, expectedY);
    }

    /**
     * Checks if a GameObject's position matches the expected position within tolerance.
     * 
     * @param obj The game object to check
     * @param expected The expected position
     * @return true if positions match within tolerance
     */
    public static boolean isPositionMatch(GameObject obj, ExpectedPosition expected) {
        float tolerance = 1.0f; // Small tolerance for floating point precision
        return Math.abs(obj.getPosition().getX() - expected.x) < tolerance &&
                Math.abs(obj.getPosition().getY() - expected.y) < tolerance;
    }
}