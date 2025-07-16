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
     * Helper class to represent a rectangle for collision detection.
     */
    private static class CollisionRectangle {
        final double centerX;
        final double centerY;
        final double width;
        final double height;
        
        CollisionRectangle(double centerX, double centerY, double width, double height) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Helper class to represent a circle for collision detection.
     */
    private static class CollisionCircle {
        final double centerX;
        final double centerY;
        final double radius;
        
        CollisionCircle(double centerX, double centerY, double radius) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
        }
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
        } else if (movingPair.visual instanceof javafx.scene.shape.Polygon) {
            return checkPolygonCollision(movingPair, otherPair, newX, newY);
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
        } else if (otherPair.visual instanceof javafx.scene.shape.Polygon) {
            return checkRectangleToPolygonCollision(movingRect, otherPair, newX, newY);
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
        } else if (otherPair.visual instanceof javafx.scene.shape.Polygon) {
            return checkCircleToPolygonCollision(movingCircle, otherPair, newX, newY);
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
        double rectCenterX = newX + movingWidth / 2;
        double rectCenterY = newY + movingHeight / 2;
        
        CollisionRectangle rect = new CollisionRectangle(rectCenterX, rectCenterY, movingWidth, movingHeight);
        CollisionCircle circle = new CollisionCircle(
            otherCircle.getTranslateX(), 
            otherCircle.getTranslateY(), 
            otherCircle.getRadius()
        );
        
        return isRectangleCircleCollision(rect, circle);
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
        
        double otherX = otherRect.getTranslateX();
        double otherY = otherRect.getTranslateY();
        double otherWidth = otherRect.getWidth();
        double otherHeight = otherRect.getHeight();
        
        CollisionRectangle rect = new CollisionRectangle(
            otherX + otherWidth / 2, 
            otherY + otherHeight / 2, 
            otherWidth, 
            otherHeight
        );
        CollisionCircle circle = new CollisionCircle(newX, newY, movingCircle.getRadius());
        
        return isRectangleCircleCollision(rect, circle);
    }
    
    /**
     * Core algorithm for rectangle-circle collision detection.
     * 
     * @param rect The rectangle for collision detection
     * @param circle The circle for collision detection
     * @return true if the rectangle and circle would collide, false otherwise
     */
    private boolean isRectangleCircleCollision(CollisionRectangle rect, CollisionCircle circle) {
        double deltaX = Math.abs(rect.centerX - circle.centerX);
        double deltaY = Math.abs(rect.centerY - circle.centerY);
        
        // Early exit if clearly no collision
        if (deltaX > (rect.width / 2 + circle.radius) || deltaY > (rect.height / 2 + circle.radius)) {
            return false;
        }
        
        // Collision if circle center is within rectangle bounds
        if (deltaX <= (rect.width / 2) || deltaY <= (rect.height / 2)) {
            return true;
        }
        
        // Check corner collision
        double cornerDistanceSquared = Math.pow(deltaX - rect.width / 2, 2) + 
                                      Math.pow(deltaY - rect.height / 2, 2);
        return cornerDistanceSquared <= Math.pow(circle.radius, 2);
    }
    
    /**
     * Checks collision between a rectangle and a polygon.
     * Uses bounding box approximation for collision detection.
     */
    private boolean checkRectangleToPolygonCollision(javafx.scene.shape.Rectangle movingRect, 
                                                    PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Polygon otherPolygon = (javafx.scene.shape.Polygon) otherPair.visual;
        
        // Get rectangle bounds at new position
        double rectWidth = movingRect.getWidth();
        double rectHeight = movingRect.getHeight();
        
        // Get polygon bounding box
        javafx.geometry.Bounds polygonBounds = otherPolygon.getBoundsInLocal();
        double polygonX = otherPolygon.getTranslateX();
        double polygonY = otherPolygon.getTranslateY();
        double polygonWidth = polygonBounds.getWidth();
        double polygonHeight = polygonBounds.getHeight();
        
        // AABB collision detection
        return newX < polygonX + polygonWidth &&
               newX + rectWidth > polygonX &&
               newY < polygonY + polygonHeight &&
               newY + rectHeight > polygonY;
    }
    
    /**
     * Checks collision between a circle and a polygon.
     * Uses bounding box approximation for collision detection.
     */
    private boolean checkCircleToPolygonCollision(javafx.scene.shape.Circle movingCircle, 
                                                 PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Polygon otherPolygon = (javafx.scene.shape.Polygon) otherPair.visual;
        
        // Get polygon bounding box
        javafx.geometry.Bounds polygonBounds = otherPolygon.getBoundsInLocal();
        double polygonX = otherPolygon.getTranslateX();
        double polygonY = otherPolygon.getTranslateY();
        double polygonCenterX = polygonX + polygonBounds.getWidth() / 2;
        double polygonCenterY = polygonY + polygonBounds.getHeight() / 2;
        
        CollisionRectangle rect = new CollisionRectangle(
            polygonCenterX, 
            polygonCenterY, 
            polygonBounds.getWidth(), 
            polygonBounds.getHeight()
        );
        CollisionCircle circle = new CollisionCircle(newX, newY, movingCircle.getRadius());
        
        return isRectangleCircleCollision(rect, circle);
    }
    
    /**
     * Checks collision when the moving object is a polygon (bucket).
     */
    private boolean checkPolygonCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                         double newX, double newY) {
        javafx.scene.shape.Polygon movingPolygon = (javafx.scene.shape.Polygon) movingPair.visual;
        
        if (otherPair.visual instanceof javafx.scene.shape.Rectangle) {
            return checkPolygonToRectangleCollision(movingPolygon, otherPair, newX, newY);
        } else if (otherPair.visual instanceof javafx.scene.shape.Circle) {
            return checkPolygonToCircleCollision(movingPolygon, otherPair, newX, newY);
        } else if (otherPair.visual instanceof javafx.scene.shape.Polygon) {
            return checkPolygonToPolygonCollision(movingPolygon, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision between a polygon and a rectangle.
     * Uses bounding box approximation for polygon collision detection.
     */
    private boolean checkPolygonToRectangleCollision(javafx.scene.shape.Polygon movingPolygon, 
                                                    PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Rectangle otherRect = (javafx.scene.shape.Rectangle) otherPair.visual;
        
        // Get bounding box of polygon at new position
        javafx.geometry.Bounds polygonBounds = movingPolygon.getBoundsInLocal();
        double polygonX = newX;
        double polygonY = newY;
        double polygonWidth = polygonBounds.getWidth();
        double polygonHeight = polygonBounds.getHeight();
        
        // Get rectangle bounds
        double rectX = otherRect.getTranslateX();
        double rectY = otherRect.getTranslateY();
        double rectWidth = otherRect.getWidth();
        double rectHeight = otherRect.getHeight();
        
        // AABB collision detection using bounding boxes
        return polygonX < rectX + rectWidth &&
               polygonX + polygonWidth > rectX &&
               polygonY < rectY + rectHeight &&
               polygonY + polygonHeight > rectY;
    }
    
    /**
     * Checks collision between a polygon and a circle.
     * Uses bounding box approximation for polygon collision detection.
     */
    private boolean checkPolygonToCircleCollision(javafx.scene.shape.Polygon movingPolygon, 
                                                 PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Circle otherCircle = (javafx.scene.shape.Circle) otherPair.visual;
        
        // Get bounding box of polygon at new position
        javafx.geometry.Bounds polygonBounds = movingPolygon.getBoundsInLocal();
        double polygonCenterX = newX + polygonBounds.getWidth() / 2;
        double polygonCenterY = newY + polygonBounds.getHeight() / 2;
        
        CollisionRectangle rect = new CollisionRectangle(
            polygonCenterX, 
            polygonCenterY, 
            polygonBounds.getWidth(), 
            polygonBounds.getHeight()
        );
        CollisionCircle circle = new CollisionCircle(
            otherCircle.getTranslateX(), 
            otherCircle.getTranslateY(), 
            otherCircle.getRadius()
        );
        
        return isRectangleCircleCollision(rect, circle);
    }
    
    /**
     * Checks collision between two polygons.
     * Uses bounding box approximation for polygon collision detection.
     */
    private boolean checkPolygonToPolygonCollision(javafx.scene.shape.Polygon movingPolygon, 
                                                  PhysicsVisualPair otherPair, double newX, double newY) {
        javafx.scene.shape.Polygon otherPolygon = (javafx.scene.shape.Polygon) otherPair.visual;
        
        // Get bounding box of moving polygon at new position
        javafx.geometry.Bounds movingBounds = movingPolygon.getBoundsInLocal();
        double movingX = newX;
        double movingY = newY;
        double movingWidth = movingBounds.getWidth();
        double movingHeight = movingBounds.getHeight();
        
        // Get bounding box of other polygon
        javafx.geometry.Bounds otherBounds = otherPolygon.getBoundsInLocal();
        double otherX = otherPolygon.getTranslateX();
        double otherY = otherPolygon.getTranslateY();
        double otherWidth = otherBounds.getWidth();
        double otherHeight = otherBounds.getHeight();
        
        // AABB collision detection using bounding boxes
        return movingX < otherX + otherWidth &&
               movingX + movingWidth > otherX &&
               movingY < otherY + otherHeight &&
               movingY + movingHeight > otherY;
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
        } else if (pair.visual instanceof javafx.scene.shape.Polygon) {
            javafx.scene.shape.Polygon polygon = (javafx.scene.shape.Polygon) pair.visual;
            javafx.geometry.Bounds bounds = polygon.getBoundsInLocal();
            double centerX = polygon.getTranslateX() + bounds.getWidth() / 2;
            double centerY = polygon.getTranslateY() + bounds.getHeight() / 2;
            return model.isInWinZone(centerX, centerY);
        }
        return false;
    }
}