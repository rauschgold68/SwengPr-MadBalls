package mm.model;

import javafx.scene.shape.Rectangle;
import mm.Generated;

/**
 * Contains geometric collision detection algorithms and helper classes.
 * This class encapsulates the mathematical aspects of collision detection.
 */
class CollisionGeometry {
    
    /**
     * Parameter object for creating CollisionRectangle instances.
     * Encapsulates all rectangle properties to avoid excessive parameter lists.
     */
    static class RectangleParams {
        final double centerX;
        final double centerY;
        final double width;
        final double height;
        final double rotation;
        
        RectangleParams(double centerX, double centerY, double width, double height) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
            this.rotation = 0.0;
        }
        
        /**
         * Private constructor for creating rotated rectangles.
         */
        private RectangleParams(double centerX, double centerY, double width, double height, double rotation) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
            this.rotation = rotation;
        }
        
        /**
         * Creates a rectangle with rotation.
         */
        RectangleParams withRotation(double rotation) {
            return new RectangleParams(this.centerX, this.centerY, this.width, this.height, rotation);
        }
        
        /**
         * Factory method for creating a rotated rectangle.
         */
        static RectangleParams withRotation(double centerX, double centerY, double width, double height) {
            return new RectangleParams(centerX, centerY, width, height).withRotation(0.0);
        }
        
        /**
         * Factory method for creating a rotated rectangle with specific rotation.
         */
        static RectangleParams withRotation(RectangleParams base, double rotation) {
            return base.withRotation(rotation);
        }
    }

    /**
     * Helper class to represent a rectangle for collision detection.
     */
    static class CollisionRectangle {
        final double centerX;
        final double centerY;
        final double width;
        final double height;
        final double rotation; // rotation in degrees
        
        CollisionRectangle(double centerX, double centerY, double width, double height) {
            this(new RectangleParams(centerX, centerY, width, height));
        }
        
        CollisionRectangle(RectangleParams params) {
            this.centerX = params.centerX;
            this.centerY = params.centerY;
            this.width = params.width;
            this.height = params.height;
            this.rotation = params.rotation;
        }
        
        boolean isRotated() {
            return Math.abs(rotation) >= 0.01;
        }
    }

    /**
     * Helper class to represent a circle for collision detection.
     */
    static class CollisionCircle {
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
     * Checks collision between two oriented bounding boxes (OBB).
     * Uses the Separating Axis Theorem for accurate rotated rectangle collision detection.
     */
    boolean checkOBBCollision(CollisionRectangle rect1, CollisionRectangle rect2) {
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
     * Core algorithm for rectangle-circle collision detection with rotation support.
     */
    boolean isRectangleCircleCollision(CollisionRectangle rect, CollisionCircle circle) {
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
     */
    @Generated
    javafx.geometry.Bounds getRotatedBounds(Rectangle rect, double x, double y, double rotation) {
        // Create a temporary rectangle at the specified position and rotation
        Rectangle tempRect = new Rectangle(rect.getWidth(), rect.getHeight());
        tempRect.setTranslateX(x);
        tempRect.setTranslateY(y);
        tempRect.setRotate(rotation);
        
        // Get the bounds in parent coordinate system (which includes rotation)
        return tempRect.getBoundsInParent();
    }
}
