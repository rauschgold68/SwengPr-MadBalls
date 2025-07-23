package mm.model;

import java.util.List;

/**
 * Service class responsible for all collision detection logic in the simulation.
 * <p>
 * Encapsulates collision detection algorithms and business rules using pure
 * geometric calculations without any UI framework dependencies.
 * This allows the model to remain independent of JavaFX or any other view technology.
 * </p>
 * <p>
 * The class provides collision detection for various geometry types including rectangles
 * and circles, with support for rotation and special zone handling. It uses a two-phase
 * approach: broad-phase detection using bounding boxes, followed by detailed geometric
 * intersection calculations.
 * </p>
 * 
 * @author MadBalls Team
 * @version 1.0
 * @since 1.0
 */
public class GeometricCollisionDetection {
    
    /** Reference to the simulation model that owns this collision detection service */
    private final SimulationModel model;

    /**
     * Data class to hold transformation parameters for collision detection.
     * <p>
     * This immutable data structure encapsulates the position and rotation
     * parameters needed for geometric transformations during collision testing.
     * It helps reduce parameter lists and groups related transformation data.
     * </p>
     */
    private static class TransformData {
        /** The x-coordinate of the transformation */
        final double x;
        /** The y-coordinate of the transformation */
        final double y;
        /** The rotation angle in degrees */
        final double angle;
        
        /**
         * Creates a new transformation data object.
         *
         * @param x the x-coordinate
         * @param y the y-coordinate
         * @param angle the rotation angle in degrees
         */
        TransformData(double x, double y, double angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }
    
    
    /**
     * Package-private constructor - only the SimulationModel should create this service.
     * <p>
     * This constructor is intentionally package-private to enforce that collision
     * detection services are only created by the simulation model, maintaining
     * proper encapsulation and preventing external instantiation.
     * </p>
     *
     * @param model the simulation model that owns this collision detection service,
     *              must not be null
     * @throws NullPointerException if model is null
     */
    GeometricCollisionDetection(SimulationModel model) {
        this.model = model;
    }

    /**
     * Checks if moving an object to a new position would cause it to overlap with other objects.
     * <p>
     * This method performs collision detection by testing the proposed position against
     * all other objects in the simulation. It excludes special zone objects (win zones,
     * no-place zones) from collision testing, allowing objects to be placed in or moved
     * through these areas.
     * </p>
     * <p>
     * The collision detection uses the object's current rotation and only changes the
     * position for testing purposes.
     * </p>
     * 
     * @param movingPair the physics-geometry pair being moved, must not be null
     * @param newX the proposed new X position in world coordinates
     * @param newY the proposed new Y position in world coordinates
     * @return {@code true} if the new position would cause an overlap with another object,
     *         {@code false} otherwise
     * @throws NullPointerException if movingPair is null
     */
    public boolean wouldCauseOverlap(PhysicsGeometryPair movingPair, double newX, double newY) {
        List<PhysicsGeometryPair> allPairs = model.getGeometryPairs();
        
        for (PhysicsGeometryPair otherPair : allPairs) {
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
     * Checks if moving an object to a new position with rotation would cause overlap.
     * <p>
     * This overloaded method extends the basic overlap detection to include rotation
     * testing. It's particularly useful for objects that can be rotated during placement
     * or movement, ensuring that the rotated geometry doesn't intersect with other objects.
     * </p>
     * <p>
     * Like the basic version, this method excludes special zone objects from collision
     * testing and uses geometric intersection algorithms appropriate for each shape type.
     * </p>
     *
     * @param movingPair the physics-geometry pair being moved, must not be null
     * @param newX the proposed new X position in world coordinates
     * @param newY the proposed new Y position in world coordinates
     * @param newAngle the proposed new rotation angle in degrees
     * @return {@code true} if the new position and rotation would cause an overlap,
     *         {@code false} otherwise
     * @throws NullPointerException if movingPair is null
     */
    public boolean wouldCauseOverlap(PhysicsGeometryPair movingPair, double newX, double newY, double newAngle) {
        List<PhysicsGeometryPair> allPairs = model.getGeometryPairs();

        for (PhysicsGeometryPair otherPair : allPairs) {
            if (shouldSkipCollisionCheck(movingPair, otherPair)) {
                continue;
            }
            
            TransformData transform = new TransformData(newX, newY, newAngle);
            if (hasCollisionWithRotation(movingPair, otherPair, transform)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Determines whether collision checking should be skipped between two pairs.
     * <p>
     * This method implements the business rules for collision detection exclusions:
     * <ul>
     *   <li>Self-collision: An object cannot collide with itself</li>
     *   <li>Special zones: Objects in win zones, win platforms, or no-place zones
     *       are excluded from collision detection to allow free movement</li>
     * </ul>
     * </p>
     *
     * @param movingPair the pair being moved, must not be null
     * @param otherPair the pair being tested against, must not be null
     * @return {@code true} if collision checking should be skipped, {@code false} otherwise
     */
    private boolean shouldSkipCollisionCheck(PhysicsGeometryPair movingPair, PhysicsGeometryPair otherPair) {
        // Skip self-collision
        if (movingPair == otherPair) {
            return true;
        }
        
        // Skip if other object is in win zone or no-place zone
        Object userData = otherPair.body.getUserData();
        return "winZone".equals(userData) || "winPlat".equals(userData) || "noPlace".equals(userData);
    }
    
    /**
     * Checks if two geometry pairs would collide at the given positions.
     * <p>
     * This method creates a temporary geometry instance at the proposed new position
     * while maintaining the original rotation, then tests for intersection with the
     * other geometry. It handles null geometry cases gracefully by returning false.
     * </p>
     *
     * @param movingPair the pair being moved, must not be null
     * @param otherPair the pair being tested against, must not be null
     * @param newX the proposed X position for the moving object
     * @param newY the proposed Y position for the moving object
     * @return {@code true} if the geometries would intersect, {@code false} otherwise
     */
    private boolean hasCollision(PhysicsGeometryPair movingPair, PhysicsGeometryPair otherPair, double newX, double newY) {
        GeometryData movingGeom = movingPair.getGeometry();
        GeometryData otherGeom = otherPair.getGeometry();
        
        if (movingGeom == null || otherGeom == null) {
            return false;
        }
        
        // Create temporary geometry at new position for collision checking
        GeometryData tempMovingGeom = createGeometryAtPosition(movingGeom, newX, newY, movingGeom.getRotation());
        
        return geometriesIntersect(tempMovingGeom, otherGeom);
    }
    
    /**
     * Checks if two geometry pairs would collide with rotation applied.
     * <p>
     * Similar to the basic collision check, but includes rotation testing by creating
     * a temporary geometry with both the new position and new rotation angle.
     * This is essential for accurate collision detection when objects can be rotated.
     * </p>
     *
     * @param movingPair the pair being moved, must not be null
     * @param otherPair the pair being tested against, must not be null
     * @param transform the transformation data containing position and rotation
     * @return {@code true} if the geometries would intersect, {@code false} otherwise
     */
    private boolean hasCollisionWithRotation(PhysicsGeometryPair movingPair, PhysicsGeometryPair otherPair, 
                                           TransformData transform) {
        GeometryData movingGeom = movingPair.getGeometry();
        GeometryData otherGeom = otherPair.getGeometry();
        
        if (movingGeom == null || otherGeom == null) {
            return false;
        }
        
        // Create temporary geometry at new position and angle
        GeometryData tempMovingGeom = createGeometryAtPosition(movingGeom, transform.x, transform.y, transform.angle);
        
        return geometriesIntersect(tempMovingGeom, otherGeom);
    }
    
    /**
     * Creates a new geometry instance at the specified position and rotation.
     * <p>
     * This factory method creates temporary geometry instances for collision testing
     * without modifying the original objects. It supports all geometry types used
     * in the simulation and preserves the original dimensions while applying the
     * new transformation parameters.
     * </p>
     *
     * @param original the original geometry to base the new instance on, must not be null
     * @param x the new x-coordinate for the geometry
     * @param y the new y-coordinate for the geometry  
     * @param rotation the new rotation angle in degrees
     * @return a new geometry instance at the specified position and rotation
     * @throws IllegalArgumentException if the geometry type is not supported
     * @throws NullPointerException if original is null
     */
    private GeometryData createGeometryAtPosition(GeometryData original, double x, double y, double rotation) {
        Position newPos = new Position((float) x, (float) y);
        
        if (original instanceof RectangleGeometry) {
            RectangleGeometry rect = (RectangleGeometry) original;
            return new RectangleGeometry(newPos, rect.getWidth(), rect.getHeight(), rotation);
        } else if (original instanceof CircleGeometry) {
            CircleGeometry circle = (CircleGeometry) original;
            return new CircleGeometry(newPos, circle.getRadius(), rotation);
        }
        
        throw new IllegalArgumentException("Unsupported geometry type: " + original.getClass());
    }
    
    /**
     * Checks if two geometries intersect using a two-phase collision detection approach.
     * <p>
     * This method implements a broad-phase/narrow-phase collision detection strategy:
     * <ol>
     *   <li><strong>Broad-phase:</strong> Quick bounding box intersection test to eliminate
     *       obviously non-intersecting geometries</li>
     *   <li><strong>Narrow-phase:</strong> Detailed geometric intersection calculations
     *       based on the specific geometry types involved</li>
     * </ol>
     * </p>
     * <p>
     * Supported geometry combinations:
     * <ul>
     *   <li>Rectangle vs Rectangle</li>
     *   <li>Circle vs Circle</li>
     *   <li>Rectangle vs Circle</li>
     * </ul>
     * </p>
     *
     * @param geom1 the first geometry, must not be null
     * @param geom2 the second geometry, must not be null
     * @return {@code true} if the geometries intersect, {@code false} otherwise
     */
    private boolean geometriesIntersect(GeometryData geom1, GeometryData geom2) {
        // Quick bounding box check first
        if (!boundingBoxesIntersect(geom1.getBounds(), geom2.getBounds())) {
            return false;
        }
        
        // Detailed collision detection based on geometry types
        if (geom1 instanceof RectangleGeometry) {
            if (geom2 instanceof RectangleGeometry) {
                return rectanglesIntersect((RectangleGeometry) geom1, (RectangleGeometry) geom2);
            } else if (geom2 instanceof CircleGeometry) {
                return rectangleCircleIntersect((RectangleGeometry) geom1, (CircleGeometry) geom2);
            }
        } else if (geom1 instanceof CircleGeometry) {
            if (geom2 instanceof CircleGeometry) {
                return circlesIntersect((CircleGeometry) geom1, (CircleGeometry) geom2);
            } else if (geom2 instanceof RectangleGeometry) {
                return rectangleCircleIntersect((RectangleGeometry) geom2, (CircleGeometry) geom1);
            }
        }
        
        return false;
    }
    
    /**
     * Performs a quick bounding box intersection check for broad-phase collision detection.
     * <p>
     * This method tests whether two axis-aligned bounding boxes intersect. It's used
     * as a fast preliminary test before more expensive detailed collision detection.
     * The bounding boxes are represented as arrays of [minX, minY, maxX, maxY].
     * </p>
     *
     * @param bounds1 the first bounding box as [minX, minY, maxX, maxY]
     * @param bounds2 the second bounding box as [minX, minY, maxX, maxY]
     * @return {@code true} if the bounding boxes intersect, {@code false} otherwise
     */
    private boolean boundingBoxesIntersect(double[] bounds1, double[] bounds2) {
        return !(bounds1[2] < bounds2[0] || bounds2[2] < bounds1[0] || 
                 bounds1[3] < bounds2[1] || bounds2[3] < bounds1[1]);
    }
    
    /**
     * Performs detailed rectangle-rectangle intersection testing.
     * <p>
     * This method handles collision detection between two rectangular geometries.
     * For non-rotated rectangles, it uses efficient Axis-Aligned Bounding Box (AABB)
     * collision detection. For rotated rectangles, it falls back to bounding box
     * approximation for simplicity.
     * </p>
     *
     * @param rect1 the first rectangle geometry, must not be null
     * @param rect2 the second rectangle geometry, must not be null
     * @return {@code true} if the rectangles intersect, {@code false} otherwise
     */
    private boolean rectanglesIntersect(RectangleGeometry rect1, RectangleGeometry rect2) {
        // For simplicity, if either rectangle is rotated, use bounding box collision
        // A more sophisticated implementation would use SAT (Separating Axis Theorem)
        if (rect1.getRotation() != 0.0 || rect2.getRotation() != 0.0) {
            return boundingBoxesIntersect(rect1.getBounds(), rect2.getBounds());
        }
        
        // Simple AABB collision for non-rotated rectangles
        double x1 = rect1.getPosition().getX();
        double y1 = rect1.getPosition().getY();
        double x2 = rect2.getPosition().getX();
        double y2 = rect2.getPosition().getY();
        
        return !(x1 + rect1.getWidth() <= x2 || x2 + rect2.getWidth() <= x1 ||
                 y1 + rect1.getHeight() <= y2 || y2 + rect2.getHeight() <= y1);
    }
    
    /**
     * Performs circle-circle intersection testing using distance calculation.
     * <p>
     * This method calculates the distance between the centers of two circles
     * and compares it with the sum of their radii. Two circles intersect if
     * the distance between their centers is less than the sum of their radii.
     * </p>
     *
     * @param circle1 the first circle geometry, must not be null
     * @param circle2 the second circle geometry, must not be null
     * @return {@code true} if the circles intersect, {@code false} otherwise
     */
    private boolean circlesIntersect(CircleGeometry circle1, CircleGeometry circle2) {
        double centerX1 = circle1.getPosition().getX() + circle1.getRadius();
        double centerY1 = circle1.getPosition().getY() + circle1.getRadius();
        double centerX2 = circle2.getPosition().getX() + circle2.getRadius();
        double centerY2 = circle2.getPosition().getY() + circle2.getRadius();
        
        double distance = Math.sqrt(Math.pow(centerX2 - centerX1, 2) + Math.pow(centerY2 - centerY1, 2));
        return distance < (circle1.getRadius() + circle2.getRadius());
    }
    
    /**
     * Performs rectangle-circle intersection testing using closest point calculation.
     * <p>
     * This method implements precise collision detection between a rectangle and a circle
     * by finding the closest point on the rectangle to the circle's center, then checking
     * if this point is within the circle's radius.
     * </p>
     * <p>
     * For rotated rectangles, the method falls back to bounding box approximation for
     * performance reasons. For axis-aligned rectangles, it uses the more accurate
     * closest-point algorithm.
     * </p>
     * <p>
     *
     * @param rect the rectangle geometry, must not be null
     * @param circle the circle geometry, must not be null
     * @return {@code true} if the rectangle and circle intersect, {@code false} otherwise
     */
    private boolean rectangleCircleIntersect(RectangleGeometry rect, CircleGeometry circle) {
        // For rotated rectangles, use bounding box approximation
        if (rect.getRotation() != 0.0) {
            return boundingBoxesIntersect(rect.getBounds(), circle.getBounds());
        }
        
        // More precise calculation for axis-aligned rectangles
        double circleX = circle.getPosition().getX() + circle.getRadius();
        double circleY = circle.getPosition().getY() + circle.getRadius();
        double radius = circle.getRadius();
        
        double rectX = rect.getPosition().getX();
        double rectY = rect.getPosition().getY();
        double rectW = rect.getWidth();
        double rectH = rect.getHeight();
        
        // Find the closest point on the rectangle to the circle center
        double closestX = Math.max(rectX, Math.min(circleX, rectX + rectW));
        double closestY = Math.max(rectY, Math.min(circleY, rectY + rectH));
        
        // Calculate distance from circle center to closest point
        double distance = Math.sqrt(Math.pow(circleX - closestX, 2) + Math.pow(circleY - closestY, 2));
        
        return distance < radius;
    }
}
