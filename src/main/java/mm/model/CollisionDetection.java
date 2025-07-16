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
        final double rotation; // Added rotation in degrees
        
        CollisionRectangle(double centerX, double centerY, double width, double height) {
            this(centerX, centerY, width, height, 0.0);
        }
        
        CollisionRectangle(double centerX, double centerY, double width, double height, double rotation) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
            this.rotation = rotation;
        }
        
        boolean isRotated() {
            return Math.abs(rotation) >= 0.01;
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
            return checkRectangleCollision(movingPair, otherPair, newX, newY);
        } else if (movingPair.visual instanceof Circle) {
            return checkCircleCollision(movingPair, otherPair, newX, newY);
        } else if (movingPair.visual instanceof Polygon) {
            return checkPolygonCollision(movingPair, otherPair, newX, newY);
        }
        
        return false;
    }

    private boolean isWinZone(PhysicsVisualPair pair){
        Object userData = pair.body.getUserData();
        return "winzone".equals(userData) || "winPlat".equals(userData) || "winObject".equals(userData);
    }
    
    /**
     * Checks collision when the moving object is a rectangle.
     */
    private boolean checkRectangleCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                           double newX, double newY) {
        Rectangle movingRect = (Rectangle) movingPair.visual;
        
        if (otherPair.visual instanceof Rectangle) {
            return checkRectangleToRectangleCollision(movingRect, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Circle) {
            return checkRectangleToCircleCollision(movingRect, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Polygon) {
            return checkRectangleToPolygonCollision(movingRect, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision when the moving object is a circle.
     */
    private boolean checkCircleCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                        double newX, double newY) {
        Circle movingCircle = (Circle) movingPair.visual;
        
        if (otherPair.visual instanceof Circle) {
            return checkCircleToCircleCollision(movingCircle, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Rectangle) {
            return checkCircleToRectangleCollision(movingCircle, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Polygon) {
            return checkCircleToPolygonCollision(movingCircle, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision between two rectangles using proper OBB collision detection.
     */
    private boolean checkRectangleToRectangleCollision(Rectangle movingRect, 
                                                  PhysicsVisualPair otherPair, double newX, double newY) {
    Rectangle otherRect = (Rectangle) otherPair.visual;
    
    // Create collision rectangles with proper center positions
    double movingCenterX = newX + movingRect.getWidth() / 2;
    double movingCenterY = newY + movingRect.getHeight() / 2;
    CollisionRectangle moving = new CollisionRectangle(
        movingCenterX, movingCenterY, 
        movingRect.getWidth(), movingRect.getHeight(), 
        movingRect.getRotate()
    );
    
    double otherCenterX = otherRect.getTranslateX() + otherRect.getWidth() / 2;
    double otherCenterY = otherRect.getTranslateY() + otherRect.getHeight() / 2;
    CollisionRectangle other = new CollisionRectangle(
        otherCenterX, otherCenterY,
        otherRect.getWidth(), otherRect.getHeight(),
        otherRect.getRotate()
    );
    
    return checkOBBCollision(moving, other);
}

/**
 * Checks collision between two oriented bounding boxes (OBB).
 * Uses the Separating Axis Theorem for accurate rotated rectangle collision detection.
 */
private boolean checkOBBCollision(CollisionRectangle rect1, CollisionRectangle rect2) {
    // If neither rectangle is rotated, use simpler axis-aligned check
    if (!rect1.isRotated() && !rect2.isRotated()) {
        return checkAxisAlignedRectangleCollision(rect1, rect2);
    }
    
    // Get the four corners of each rectangle
    double[][] corners1 = getRectangleCorners(rect1);
    double[][] corners2 = getRectangleCorners(rect2);
    
    // Test separation along the axes of both rectangles
    return !isSeparated(corners1, corners2, rect1) && !isSeparated(corners1, corners2, rect2);
}

/**
 * Checks if two rectangles are axis-aligned and colliding.
 */
private boolean checkAxisAlignedRectangleCollision(CollisionRectangle rect1, CollisionRectangle rect2) {
    double left1 = rect1.centerX - rect1.width / 2;
    double right1 = rect1.centerX + rect1.width / 2;
    double top1 = rect1.centerY - rect1.height / 2;
    double bottom1 = rect1.centerY + rect1.height / 2;
    
    double left2 = rect2.centerX - rect2.width / 2;
    double right2 = rect2.centerX + rect2.width / 2;
    double top2 = rect2.centerY - rect2.height / 2;
    double bottom2 = rect2.centerY + rect2.height / 2;
    
    return !(right1 <= left2 || left1 >= right2 || bottom1 <= top2 || top1 >= bottom2);
}

/**
 * Gets the four corners of a rectangle in world coordinates.
 */
private double[][] getRectangleCorners(CollisionRectangle rect) {
    double halfWidth = rect.width / 2;
    double halfHeight = rect.height / 2;
    
    // Local corners (relative to center)
    double[][] localCorners = {
        {-halfWidth, -halfHeight},
        {halfWidth, -halfHeight},
        {halfWidth, halfHeight},
        {-halfWidth, halfHeight}
    };
    
    double[][] worldCorners = new double[4][2];
    double rotRad = Math.toRadians(rect.rotation);
    double cos = Math.cos(rotRad);
    double sin = Math.sin(rotRad);
    
    // Transform to world coordinates
    for (int i = 0; i < 4; i++) {
        double localX = localCorners[i][0];
        double localY = localCorners[i][1];
        
        worldCorners[i][0] = rect.centerX + localX * cos - localY * sin;
        worldCorners[i][1] = rect.centerY + localX * sin + localY * cos;
    }
    
    return worldCorners;
}

/**
 * Tests if two sets of corners are separated along the axes of a given rectangle.
 */
private boolean isSeparated(double[][] corners1, double[][] corners2, CollisionRectangle rect) {
    double rotRad = Math.toRadians(rect.rotation);
    
    // Test along both axes of the rectangle
    double[] axes = {rotRad, rotRad + Math.PI / 2}; // 0° and 90° relative to rectangle
    
    for (double axisAngle : axes) {
        double axisX = Math.cos(axisAngle);
        double axisY = Math.sin(axisAngle);
        
        // Project all corners onto this axis
        double min1 = Double.MAX_VALUE, max1 = -Double.MAX_VALUE;
        double min2 = Double.MAX_VALUE, max2 = -Double.MAX_VALUE;
        
        for (double[] corner : corners1) {
            double projection = corner[0] * axisX + corner[1] * axisY;
            min1 = Math.min(min1, projection);
            max1 = Math.max(max1, projection);
        }
        
        for (double[] corner : corners2) {
            double projection = corner[0] * axisX + corner[1] * axisY;
            min2 = Math.min(min2, projection);
            max2 = Math.max(max2, projection);
        }
        
        // Check if projections are separated
        if (max1 < min2 || max2 < min1) {
            return true; // Separated along this axis
        }
    }
    
    return false; // Not separated
}
    
    /**
     * Checks collision between a rectangle and a circle.
     */
    private boolean checkRectangleToCircleCollision(Rectangle movingRect, 
                                                   PhysicsVisualPair otherPair, double newX, double newY) {
        Circle otherCircle = (Circle) otherPair.visual;
        
        double movingWidth = movingRect.getWidth();
        double movingHeight = movingRect.getHeight();
        double rectCenterX = newX + movingWidth / 2;
        double rectCenterY = newY + movingHeight / 2;
        double rectRotation = movingRect.getRotate();
        
        CollisionRectangle rect = new CollisionRectangle(rectCenterX, rectCenterY, movingWidth, movingHeight, rectRotation);
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
    private boolean checkCircleToCircleCollision(Circle movingCircle, 
                                                PhysicsVisualPair otherPair, double newX, double newY) {
        Circle otherCircle = (Circle) otherPair.visual;
        
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
    private boolean checkCircleToRectangleCollision(Circle movingCircle, 
                                                   PhysicsVisualPair otherPair, double newX, double newY) {
        Rectangle otherRect = (Rectangle) otherPair.visual;
        
        double otherX = otherRect.getTranslateX();
        double otherY = otherRect.getTranslateY();
        double otherWidth = otherRect.getWidth();
        double otherHeight = otherRect.getHeight();
        double otherRotation = otherRect.getRotate();
        
        CollisionRectangle rect = new CollisionRectangle(
            otherX + otherWidth / 2, 
            otherY + otherHeight / 2, 
            otherWidth, 
            otherHeight,
            otherRotation
        );
        CollisionCircle circle = new CollisionCircle(newX, newY, movingCircle.getRadius());
        
        return isRectangleCircleCollision(rect, circle);
    }
    
    /**
     * Core algorithm for rectangle-circle collision detection with rotation support.
     * 
     * @param rect The rectangle for collision detection (with rotation)
     * @param circle The circle for collision detection
     * @return true if the rectangle and circle would collide, false otherwise
     */
    private boolean isRectangleCircleCollision(CollisionRectangle rect, CollisionCircle circle) {
        if (!rect.isRotated()) {
            return checkAxisAlignedRectangleCircleCollision(rect, circle);
        }
        return checkRotatedRectangleCircleCollision(rect, circle);
    }
    
    /**
     * Handles collision detection for axis-aligned rectangles (no rotation).
     */
    private boolean checkAxisAlignedRectangleCircleCollision(CollisionRectangle rect, CollisionCircle circle) {
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
     * Handles collision detection for rotated rectangles.
     */
    private boolean checkRotatedRectangleCircleCollision(CollisionRectangle rect, CollisionCircle circle) {
        // Transform circle to rectangle's local coordinate system
        double rotationRad = Math.toRadians(-rect.rotation);
        double cos = Math.cos(rotationRad);
        double sin = Math.sin(rotationRad);
        
        // Translate and rotate circle center to rectangle's coordinate system
        double deltaX = circle.centerX - rect.centerX;
        double deltaY = circle.centerY - rect.centerY;
        double localX = deltaX * cos - deltaY * sin;
        double localY = deltaX * sin + deltaY * cos;
        
        // Use the axis-aligned algorithm in local space
        CollisionCircle localCircle = new CollisionCircle(localX, localY, circle.radius);
        CollisionRectangle localRect = new CollisionRectangle(0, 0, rect.width, rect.height);
        
        return checkAxisAlignedRectangleCircleCollision(localRect, localCircle);
    }
    
    /**
     * Gets the rotated bounding box of a rectangle.
     * 
     * @param rect The rectangle
     * @param x The x position
     * @param y The y position
     * @param rotation The rotation angle in degrees
     * @return The bounds of the rotated rectangle
     */
    private javafx.geometry.Bounds getRotatedBounds(Rectangle rect, double x, double y, double rotation) {
        // Create a temporary rectangle at the specified position and rotation
        Rectangle tempRect = new Rectangle(rect.getWidth(), rect.getHeight());
        tempRect.setTranslateX(x);
        tempRect.setTranslateY(y);
        tempRect.setRotate(rotation);
        
        // Get the bounds in parent coordinate system (which includes rotation)
        return tempRect.getBoundsInParent();
    }
    
    /**
     * Checks collision between a rectangle and a polygon.
     * Uses bounding box approximation for collision detection.
     */
    private boolean checkRectangleToPolygonCollision(Rectangle movingRect, 
                                                    PhysicsVisualPair otherPair, double newX, double newY) {
        Polygon otherPolygon = (Polygon) otherPair.visual;
        
        // Get rotated bounds for moving rectangle
        Bounds rectBounds = getRotatedBounds(movingRect, newX, newY, movingRect.getRotate());
        
        // Get polygon bounding box
        Bounds polygonBounds = otherPolygon.getBoundsInParent();
        
        // Check overlap using bounding boxes
        return rectBounds.intersects(polygonBounds);
    }
    
    /**
     * Checks collision between a circle and a polygon.
     * Uses bounding box approximation for collision detection.
     */
    private boolean checkCircleToPolygonCollision(Circle movingCircle, 
                                                 PhysicsVisualPair otherPair, double newX, double newY) {
        Polygon otherPolygon = (Polygon) otherPair.visual;
        
        // Get polygon bounding box
        Bounds polygonBounds = otherPolygon.getBoundsInParent();
        double polygonCenterX = polygonBounds.getMinX() + polygonBounds.getWidth() / 2;
        double polygonCenterY = polygonBounds.getMinY() + polygonBounds.getHeight() / 2;
        
        CollisionRectangle rect = new CollisionRectangle(
            polygonCenterX, 
            polygonCenterY, 
            polygonBounds.getWidth(), 
            polygonBounds.getHeight(),
            0 // Polygons don't have simple rotation in this implementation
        );
        CollisionCircle circle = new CollisionCircle(newX, newY, movingCircle.getRadius());
        
        return isRectangleCircleCollision(rect, circle);
    }
    
    /**
     * Checks collision when the moving object is a polygon (bucket).
     */
    private boolean checkPolygonCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                         double newX, double newY) {
        Polygon movingPolygon = (Polygon) movingPair.visual;
        
        if (otherPair.visual instanceof Rectangle) {
            return checkPolygonToRectangleCollision(movingPolygon, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Circle) {
            return checkPolygonToCircleCollision(movingPolygon, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Polygon) {
            return checkPolygonToPolygonCollision(movingPolygon, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision between a polygon and a rectangle.
     * Uses bounding box approximation for polygon collision detection.
     */
    private boolean checkPolygonToRectangleCollision(Polygon movingPolygon, 
                                                    PhysicsVisualPair otherPair, double newX, double newY) {
        Rectangle otherRect = (Rectangle) otherPair.visual;

        // Move polygon to new position
        Polygon tempPolygon = new Polygon();
        tempPolygon.getPoints().addAll(movingPolygon.getPoints());
        tempPolygon.setTranslateX(newX);
        tempPolygon.setTranslateY(newY);
        tempPolygon.setRotate(movingPolygon.getRotate());

        Rectangle tempRect = new Rectangle(otherRect.getWidth(), otherRect.getHeight());
        tempRect.setTranslateX(otherRect.getTranslateX());
        tempRect.setTranslateY(otherRect.getTranslateY());
        tempRect.setRotate(otherRect.getRotate());

        javafx.scene.shape.Shape intersection = javafx.scene.shape.Shape.intersect(tempPolygon, tempRect);
        return intersection.getBoundsInLocal().getWidth() > 0 && intersection.getBoundsInLocal().getHeight() > 0;
    }
    
    /**
     * Checks collision between a polygon and a circle.
     * Uses bounding box approximation for polygon collision detection.
     */
    private boolean checkPolygonToCircleCollision(Polygon movingPolygon, 
                                                 PhysicsVisualPair otherPair, double newX, double newY) {
        Circle otherCircle = (Circle) otherPair.visual;
        
        // Create temporary polygon at new position to get its bounds
        Polygon tempPolygon = new Polygon();
        tempPolygon.getPoints().addAll(movingPolygon.getPoints());
        tempPolygon.setTranslateX(newX);
        tempPolygon.setTranslateY(newY);
        tempPolygon.setRotate(movingPolygon.getRotate());
        
        javafx.geometry.Bounds polygonBounds = tempPolygon.getBoundsInParent();
        double polygonCenterX = polygonBounds.getMinX() + polygonBounds.getWidth() / 2;
        double polygonCenterY = polygonBounds.getMinY() + polygonBounds.getHeight() / 2;
        
        CollisionRectangle rect = new CollisionRectangle(
            polygonCenterX, 
            polygonCenterY, 
            polygonBounds.getWidth(), 
            polygonBounds.getHeight(),
            0 // Polygons don't have simple rotation in this implementation
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
    private boolean checkPolygonToPolygonCollision(Polygon movingPolygon, 
                                                  PhysicsVisualPair otherPair, double newX, double newY) {
        Polygon otherPolygon = (Polygon) otherPair.visual;
        
        // Create temporary polygon at new position to get its bounds
        Polygon tempPolygon = new Polygon();
        tempPolygon.getPoints().addAll(movingPolygon.getPoints());
        tempPolygon.setTranslateX(newX);
        tempPolygon.setTranslateY(newY);
        tempPolygon.setRotate(movingPolygon.getRotate());
        
        javafx.geometry.Bounds movingBounds = tempPolygon.getBoundsInParent();
        javafx.geometry.Bounds otherBounds = otherPolygon.getBoundsInParent();
        
        // Check overlap using bounding boxes
        return movingBounds.intersects(otherBounds);
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