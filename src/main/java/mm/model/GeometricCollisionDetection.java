package mm.model;

import java.util.List;

/**
 * Service class responsible for all collision detection logic in the simulation.
 * <p>
 * Encapsulates collision detection algorithms and business rules using pure
 * geometric calculations without any UI framework dependencies.
 * This allows the model to remain independent of JavaFX or any other view technology.
 * </p>
 */
public class GeometricCollisionDetection {
    
    /**
     * Data class to hold transformation parameters for collision detection.
     */
    private static class TransformData {
        final double x;
        final double y;
        final double angle;
        
        TransformData(double x, double y, double angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }
    
    private final SimulationModel model;
    
    /**
     * Package-private constructor - only the SimulationModel should create this service.
     */
    GeometricCollisionDetection(SimulationModel model) {
        this.model = model;
    }

    /**
     * Checks if moving an object to a new position would cause it to overlap with other objects.
     * Excludes objects that are in the win zone from collision detection.
     * 
     * @param movingPair The physics-geometry pair being moved
     * @param newX The proposed new X position
     * @param newY The proposed new Y position
     * @return true if the new position would cause an overlap, false otherwise
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
     * Checks collision with rotation.
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
     * Checks if two geometry pairs would collide at given positions.
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
     * Checks collision with rotation.
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
     * Checks if two geometries intersect using bounding box and detailed collision detection.
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
     * Quick bounding box intersection check.
     */
    private boolean boundingBoxesIntersect(double[] bounds1, double[] bounds2) {
        return !(bounds1[2] < bounds2[0] || bounds2[2] < bounds1[0] || 
                 bounds1[3] < bounds2[1] || bounds2[3] < bounds1[1]);
    }
    
    /**
     * Detailed rectangle-rectangle intersection.
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
     * Circle-circle intersection.
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
     * Rectangle-circle intersection.
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
