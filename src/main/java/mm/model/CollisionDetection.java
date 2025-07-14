package mm.model;

import java.util.List;

/**
 * Service class responsible for all collision detection logic in the simulation.
 * <p>
 * Encapsulates collision detection algorithms and business rules. 
 * It provides collision detection as a core business service for the simulation model.
 * </p>
 */
public class CollisionDetection {
    
    private final SimulationModel model;
    
    /**
     * Package-private constructor - only the SimulationModel should create this service.
     */
    CollisionDetection(SimulationModel model) {
        this.model = model;
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
        List<PhysicsVisualPair> allPairs = model.getDroppedPhysicsVisualPairs();
        
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
     * Determines if collision checking should be skipped for a pair of objects.
     * 
     * @param movingPair The object being moved
     * @param otherPair The other object to check against
     * @return true if collision check should be skipped, false otherwise
     */
    private boolean shouldSkipCollisionCheck(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair) {
        return otherPair == movingPair || isObjectInWinZone(otherPair);
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
        if (movingPair.visual instanceof javafx.scene.shape.Rectangle) {
            return checkRectangleCollision(movingPair, otherPair, newX, newY);
        } else if (movingPair.visual instanceof javafx.scene.shape.Circle) {
            return checkCircleCollision(movingPair, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision when the moving object is a rectangle.
     */
    private boolean checkRectangleCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                           double newX, double newY) {
        javafx.scene.shape.Rectangle movingRect = (javafx.scene.shape.Rectangle) movingPair.visual;
        
        if (otherPair.visual instanceof javafx.scene.shape.Rectangle) {
            return checkRectangleToRectangleCollision(movingRect, otherPair, newX, newY);
        } else if (otherPair.visual instanceof javafx.scene.shape.Circle) {
            return checkRectangleToCircleCollision(movingRect, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision when the moving object is a circle.
     */
    private boolean checkCircleCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                        double newX, double newY) {
        javafx.scene.shape.Circle movingCircle = (javafx.scene.shape.Circle) movingPair.visual;
        
        if (otherPair.visual instanceof javafx.scene.shape.Circle) {
            return checkCircleToCircleCollision(movingCircle, otherPair, newX, newY);
        } else if (otherPair.visual instanceof javafx.scene.shape.Rectangle) {
            return checkCircleToRectangleCollision(movingCircle, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision between two rectangles using AABB collision detection.
     */
    private boolean checkRectangleToRectangleCollision(javafx.scene.shape.Rectangle movingRect, 
                                                      PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Rectangle otherRect = (javafx.scene.shape.Rectangle) otherPair.visual;
        
        double movingWidth = movingRect.getWidth();
        double movingHeight = movingRect.getHeight();
        double otherX = otherRect.getTranslateX();
        double otherY = otherRect.getTranslateY();
        double otherWidth = otherRect.getWidth();
        double otherHeight = otherRect.getHeight();
        
        // AABB (Axis-Aligned Bounding Box) collision detection
        return newX < otherX + otherWidth &&
               newX + movingWidth > otherX &&
               newY < otherY + otherHeight &&
               newY + movingHeight > otherY;
    }
    
    /**
     * Checks collision between a rectangle and a circle.
     */
    private boolean checkRectangleToCircleCollision(javafx.scene.shape.Rectangle movingRect, 
                                                   PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Circle otherCircle = (javafx.scene.shape.Circle) otherPair.visual;
        
        double movingWidth = movingRect.getWidth();
        double movingHeight = movingRect.getHeight();
        double otherCenterX = otherCircle.getTranslateX();
        double otherCenterY = otherCircle.getTranslateY();
        double otherRadius = otherCircle.getRadius();
        
        double rectCenterX = newX + movingWidth / 2;
        double rectCenterY = newY + movingHeight / 2;
        
        return isRectangleCircleCollision(
            rectCenterX, rectCenterY, movingWidth, movingHeight,
            otherCenterX, otherCenterY, otherRadius
        );
    }
    
    /**
     * Checks collision between two circles.
     */
    private boolean checkCircleToCircleCollision(javafx.scene.shape.Circle movingCircle, 
                                                PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Circle otherCircle = (javafx.scene.shape.Circle) otherPair.visual;
        
        double movingRadius = movingCircle.getRadius();
        double otherX = otherCircle.getTranslateX();
        double otherY = otherCircle.getTranslateY();
        double otherRadius = otherCircle.getRadius();
        
        // Circle-Circle collision detection
        double distance = Math.sqrt(Math.pow(newX - otherX, 2) + Math.pow(newY - otherY, 2));
        return distance < movingRadius + otherRadius;
    }
    
    /**
     * Checks collision between a circle and a rectangle.
     */
    private boolean checkCircleToRectangleCollision(javafx.scene.shape.Circle movingCircle, 
                                                   PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Rectangle otherRect = (javafx.scene.shape.Rectangle) otherPair.visual;
        
        double movingRadius = movingCircle.getRadius();
        double otherX = otherRect.getTranslateX();
        double otherY = otherRect.getTranslateY();
        double otherWidth = otherRect.getWidth();
        double otherHeight = otherRect.getHeight();
        
        double rectCenterX = otherX + otherWidth / 2;
        double rectCenterY = otherY + otherHeight / 2;
        
        return isRectangleCircleCollision(
            rectCenterX, rectCenterY, otherWidth, otherHeight,
            newX, newY, movingRadius
        );
    }
    
    /**
     * Core algorithm for rectangle-circle collision detection.
     * 
     * @param rectCenterX X position of the rectangle's center
     * @param rectCenterY Y position of the rectangle's center
     * @param rectWidth Width of the rectangle
     * @param rectHeight Height of the rectangle
     * @param circleX X position of the circle's center
     * @param circleY Y position of the circle's center
     * @param circleRadius Radius of the circle
     * @return true if the rectangle and circle would collide, false otherwise
     */
    private boolean isRectangleCircleCollision(double rectCenterX, double rectCenterY, 
                                              double rectWidth, double rectHeight,
                                              double circleX, double circleY, double circleRadius) {
        double deltaX = Math.abs(rectCenterX - circleX);
        double deltaY = Math.abs(rectCenterY - circleY);
        
        // Early exit if clearly no collision
        if (deltaX > (rectWidth / 2 + circleRadius) || deltaY > (rectHeight / 2 + circleRadius)) {
            return false;
        }
        
        // Collision if circle center is within rectangle bounds
        if (deltaX <= (rectWidth / 2) || deltaY <= (rectHeight / 2)) {
            return true;
        }
        
        // Check corner collision
        double cornerDistanceSquared = Math.pow(deltaX - rectWidth / 2, 2) + 
                                      Math.pow(deltaY - rectHeight / 2, 2);
        return cornerDistanceSquared <= Math.pow(circleRadius, 2);
    }
    
    /**
     * Checks if an object is located within the win zone.
     * 
     * @param pair The physics-visual pair to check
     * @return true if the object is in the win zone, false otherwise
     */
    private boolean isObjectInWinZone(PhysicsVisualPair pair) {
        if (pair.visual instanceof javafx.scene.shape.Rectangle) {
            javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) pair.visual;
            double centerX = rect.getTranslateX() + rect.getWidth() / 2;
            double centerY = rect.getTranslateY() + rect.getHeight() / 2;
            return model.isInWinZone(centerX, centerY);
        } else if (pair.visual instanceof javafx.scene.shape.Circle) {
            javafx.scene.shape.Circle circle = (javafx.scene.shape.Circle) pair.visual;
            return model.isInWinZone(circle.getTranslateX(), circle.getTranslateY());
        }
        return false;
    }
}