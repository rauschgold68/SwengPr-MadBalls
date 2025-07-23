package mm.model;

import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Service class responsible for all collision detection logic in the simulation.
 * <p>
 * Encapsulates collision detection algorithms and business rules. 
 * It provides collision detection as a core business service for the simulation model.
 * </p>
 */
public class CollisionDetection {
    
    private final SimulationModel model;
    private final CollisionShapeHandler shapeHandler;
    
    /**
     * Package-private constructor - only the SimulationModel should create this service.
     * 
     * @param model The simulation model this collision detection service belongs to
     */
    CollisionDetection(SimulationModel model) {
        this.model = model;
        this.shapeHandler = new CollisionShapeHandler();
    }
    
    /**
     * Checks if moving an object to a new position would cause it to overlap with other objects.
     * Excludes objects that are in the win zone from collision detection.
     * 
     * @param movingPair The physics-visual pair being moved
     * @param newX The proposed new X position
     * @param newY The proposed new Y position
     * @return true if the new position would cause an overlap, false otherwise
     */
    public boolean wouldCauseOverlap(PhysicsVisualPair movingPair, double newX, double newY) {
        // Check against ALL pairs in the simulation, not just dropped ones
        List<PhysicsVisualPair> allPairs = model.getPairs();
        
        for (PhysicsVisualPair otherPair : allPairs) {
            if (shouldSkipCollisionCheck(movingPair, otherPair)) {
                continue;
            }
            
            if (hasCollision(movingPair, otherPair, newX, newY)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if moving an object to a new position with a new rotation would cause it to overlap with other objects.
     * Excludes objects that are in the win zone from collision detection.
     * This method temporarily applies the rotation to check for collisions and then restores the original rotation.
     * 
     * @param movingPair The physics-visual pair being moved
     * @param newX The proposed new X position
     * @param newY The proposed new Y position
     * @param newAngle The proposed new rotation angle
     * @return true if the new position and rotation would cause an overlap, false otherwise
     */
    public boolean wouldCauseOverlap(PhysicsVisualPair movingPair, double newX, double newY, float newAngle) {
        List<PhysicsVisualPair> allPairs = model.getPairs();

        // Save original angle
        double originalAngle = movingPair.visual.getRotate();
        movingPair.visual.setRotate(newAngle);

        boolean overlap = false;
        for (PhysicsVisualPair otherPair : allPairs) {
            if (shouldSkipCollisionCheck(movingPair, otherPair)) {
                continue;
            }
            if (hasCollision(movingPair, otherPair, newX, newY)) {
                overlap = true;
                break;
            }
        }

        // Restore original angle
        movingPair.visual.setRotate(originalAngle);

        return overlap;
    }
    
    /**
     * Determines if collision checking should be skipped for a pair of objects.
     * 
     * @param movingPair The object being moved
     * @param otherPair The other object to check against
     * @return true if collision check should be skipped, false otherwise
     */
    private boolean shouldSkipCollisionCheck(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair) {
        return otherPair == movingPair || isObjectInWinZone(otherPair) || isWinZone(otherPair);
    }
    
    /**
     * Checks if two objects would collide at the given position.
     * Dispatches to appropriate collision detection method based on shape types.
     * 
     * @param movingPair The object being moved
     * @param otherPair The stationary object to check against
     * @param newX The proposed new X position for the moving object
     * @param newY The proposed new Y position for the moving object
     * @return true if collision would occur, false otherwise
     */
    private boolean hasCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, double newX, double newY) {
        if (movingPair.visual instanceof Rectangle) {
            return shapeHandler.checkRectangleCollision(movingPair, otherPair, newX, newY);
        } else if (movingPair.visual instanceof Circle) {
            return shapeHandler.checkCircleCollision(movingPair, otherPair, newX, newY);
        } else if (movingPair.visual instanceof Polygon) {
            return shapeHandler.checkPolygonCollision(movingPair, otherPair, newX, newY);
        }
        
        return false;
    }

    private boolean isWinZone(PhysicsVisualPair pair){
        Object userData = pair.body.getUserData();
        return "winzone".equals(userData);
    }
    
    /**
     * Checks if an object is located within the win zone.
     * 
     * @param pair The physics-visual pair to check
     * @return true if the object is in the win zone, false otherwise
     */
    private boolean isObjectInWinZone(PhysicsVisualPair pair) {
        if (pair.visual instanceof Rectangle) {
            Rectangle rect = (Rectangle) pair.visual;
            double centerX = rect.getTranslateX() + rect.getWidth() / 2;
            double centerY = rect.getTranslateY() + rect.getHeight() / 2;
            return model.isInWinZone(centerX, centerY);
        } else if (pair.visual instanceof Circle) {
            Circle circle = (Circle) pair.visual;
            return model.isInWinZone(circle.getTranslateX(), circle.getTranslateY());
        } else if (pair.visual instanceof Polygon) {
            Polygon polygon = (Polygon) pair.visual;
            Bounds bounds = polygon.getBoundsInLocal();
            double centerX = polygon.getTranslateX() + bounds.getWidth() / 2;
            double centerY = polygon.getTranslateY() + bounds.getHeight() / 2;
            return model.isInWinZone(centerX, centerY);
        }
        return false;
    }

    
}