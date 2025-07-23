package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for the CollisionGeometry class.
 * Tests all geometric collision detection algorithms including:
 * - Rectangle-rectangle collision (axis-aligned and rotated)
 * - Rectangle-circle collision (axis-aligned and rotated)
 * - Helper classes and parameter objects
 * 
 * Note: The getRotatedBounds method is excluded from testing as it depends on JavaFX.
 */
public class TestCollisionGeometry {
    
    private CollisionGeometry collisionGeometry;
    
    @BeforeEach
    public void setUp() {
        collisionGeometry = new CollisionGeometry();
    }
    
    // ========== RectangleParams Tests ==========
    
    /**
     * Tests basic RectangleParams construction without rotation.
     */
    @Test
    public void testRectangleParamsBasicConstruction() {
        CollisionGeometry.RectangleParams params = 
            new CollisionGeometry.RectangleParams(10.0, 20.0, 30.0, 40.0);
        
        assertEquals(10.0, params.centerX, 0.001);
        assertEquals(20.0, params.centerY, 0.001);
        assertEquals(30.0, params.width, 0.001);
        assertEquals(40.0, params.height, 0.001);
        assertEquals(0.0, params.rotation, 0.001);
    }
    
    /**
     * Tests RectangleParams withRotation method.
     */
    @Test
    public void testRectangleParamsWithRotation() {
        CollisionGeometry.RectangleParams baseParams = 
            new CollisionGeometry.RectangleParams(5.0, 10.0, 15.0, 20.0);
        
        CollisionGeometry.RectangleParams rotatedParams = baseParams.withRotation(45.0);
        
        assertEquals(5.0, rotatedParams.centerX, 0.001);
        assertEquals(10.0, rotatedParams.centerY, 0.001);
        assertEquals(15.0, rotatedParams.width, 0.001);
        assertEquals(20.0, rotatedParams.height, 0.001);
        assertEquals(45.0, rotatedParams.rotation, 0.001);
        
        // Original should remain unchanged
        assertEquals(0.0, baseParams.rotation, 0.001);
    }
    
    /**
     * Tests RectangleParams static factory method withRotation.
     */
    @Test
    public void testRectangleParamsStaticWithRotation() {
        CollisionGeometry.RectangleParams params = 
            CollisionGeometry.RectangleParams.withRotation(1.0, 2.0, 3.0, 4.0);
        
        assertEquals(1.0, params.centerX, 0.001);
        assertEquals(2.0, params.centerY, 0.001);
        assertEquals(3.0, params.width, 0.001);
        assertEquals(4.0, params.height, 0.001);
        assertEquals(0.0, params.rotation, 0.001);
    }
    
    /**
     * Tests RectangleParams static factory method with base and rotation.
     */
    @Test
    public void testRectangleParamsStaticWithRotationBase() {
        CollisionGeometry.RectangleParams base = 
            new CollisionGeometry.RectangleParams(10.0, 20.0, 30.0, 40.0);
        
        CollisionGeometry.RectangleParams rotated = 
            CollisionGeometry.RectangleParams.withRotation(base, 90.0);
        
        assertEquals(10.0, rotated.centerX, 0.001);
        assertEquals(20.0, rotated.centerY, 0.001);
        assertEquals(30.0, rotated.width, 0.001);
        assertEquals(40.0, rotated.height, 0.001);
        assertEquals(90.0, rotated.rotation, 0.001);
    }
    
    // ========== CollisionRectangle Tests ==========
    
    /**
     * Tests CollisionRectangle construction with direct parameters.
     */
    @Test
    public void testCollisionRectangleDirectConstruction() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(5.0, 10.0, 20.0, 30.0);
        
        assertEquals(5.0, rect.centerX, 0.001);
        assertEquals(10.0, rect.centerY, 0.001);
        assertEquals(20.0, rect.width, 0.001);
        assertEquals(30.0, rect.height, 0.001);
        assertEquals(0.0, rect.rotation, 0.001);
    }
    
    /**
     * Tests CollisionRectangle construction with RectangleParams.
     */
    @Test
    public void testCollisionRectangleParamsConstruction() {
        CollisionGeometry.RectangleParams params = 
            new CollisionGeometry.RectangleParams(1.0, 2.0, 3.0, 4.0).withRotation(45.0);
        
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(params);
        
        assertEquals(1.0, rect.centerX, 0.001);
        assertEquals(2.0, rect.centerY, 0.001);
        assertEquals(3.0, rect.width, 0.001);
        assertEquals(4.0, rect.height, 0.001);
        assertEquals(45.0, rect.rotation, 0.001);
    }
    
    /**
     * Tests CollisionRectangle isRotated method.
     */
    @Test
    public void testCollisionRectangleIsRotated() {
        // Not rotated (0 degrees)
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        assertFalse(rect1.isRotated());
        
        // Very small rotation (should be considered not rotated)
        CollisionGeometry.RectangleParams params2 = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(0.005);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(params2);
        assertFalse(rect2.isRotated());
        
        // Clearly rotated
        CollisionGeometry.RectangleParams params3 = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(45.0);
        CollisionGeometry.CollisionRectangle rect3 = 
            new CollisionGeometry.CollisionRectangle(params3);
        assertTrue(rect3.isRotated());
        
        // Negative rotation
        CollisionGeometry.RectangleParams params4 = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(-30.0);
        CollisionGeometry.CollisionRectangle rect4 = 
            new CollisionGeometry.CollisionRectangle(params4);
        assertTrue(rect4.isRotated());
    }
    
    // ========== CollisionCircle Tests ==========
    
    /**
     * Tests CollisionCircle construction.
     */
    @Test
    public void testCollisionCircleConstruction() {
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(15.0, 25.0, 10.0);
        
        assertEquals(15.0, circle.centerX, 0.001);
        assertEquals(25.0, circle.centerY, 0.001);
        assertEquals(10.0, circle.radius, 0.001);
    }
    
    // ========== Axis-Aligned Rectangle-Rectangle Collision Tests ==========
    
    /**
     * Tests collision detection between two non-overlapping axis-aligned rectangles.
     */
    @Test
    public void testAxisAlignedRectangleNoCollision() {
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(20, 20, 10, 10);
        
        assertFalse(collisionGeometry.checkOBBCollision(rect1, rect2));
    }
    
    /**
     * Tests collision detection between two overlapping axis-aligned rectangles.
     */
    @Test
    public void testAxisAlignedRectangleWithCollision() {
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(5, 5, 10, 10);
        
        assertTrue(collisionGeometry.checkOBBCollision(rect1, rect2));
    }
    
    /**
     * Tests collision detection when rectangles are touching at edges.
     */
    @Test
    public void testAxisAlignedRectangleTouchingEdges() {
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(10, 0, 10, 10);
        
        // Rectangles touching at edges should not be considered colliding
        assertFalse(collisionGeometry.checkOBBCollision(rect1, rect2));
    }
    
    /**
     * Tests collision detection when one rectangle is completely inside another.
     */
    @Test
    public void testAxisAlignedRectangleCompletelyInside() {
        CollisionGeometry.CollisionRectangle outerRect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 20, 20);
        CollisionGeometry.CollisionRectangle innerRect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 5, 5);
        
        assertTrue(collisionGeometry.checkOBBCollision(outerRect, innerRect));
    }
    
    // ========== Rotated Rectangle-Rectangle Collision Tests ==========
    
    /**
     * Tests collision detection between rotated rectangles that don't collide.
     */
    @Test
    public void testRotatedRectangleNoCollision() {
        CollisionGeometry.RectangleParams params1 = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(45.0);
        CollisionGeometry.RectangleParams params2 = 
            new CollisionGeometry.RectangleParams(20, 20, 10, 10).withRotation(30.0);
        
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(params1);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(params2);
        
        assertFalse(collisionGeometry.checkOBBCollision(rect1, rect2));
    }
    
    /**
     * Tests collision detection between rotated rectangles that do collide.
     */
    @Test
    public void testRotatedRectangleWithCollision() {
        CollisionGeometry.RectangleParams params1 = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(45.0);
        CollisionGeometry.RectangleParams params2 = 
            new CollisionGeometry.RectangleParams(5, 5, 10, 10).withRotation(30.0);
        
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(params1);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(params2);
        
        assertTrue(collisionGeometry.checkOBBCollision(rect1, rect2));
    }
    
    /**
     * Tests collision detection between axis-aligned and rotated rectangle.
     */
    @Test
    public void testMixedRotationRectangleCollision() {
        CollisionGeometry.CollisionRectangle axisAligned = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        
        CollisionGeometry.RectangleParams rotatedParams = 
            new CollisionGeometry.RectangleParams(3, 3, 8, 8).withRotation(45.0);
        CollisionGeometry.CollisionRectangle rotated = 
            new CollisionGeometry.CollisionRectangle(rotatedParams);
        
        assertTrue(collisionGeometry.checkOBBCollision(axisAligned, rotated));
    }
    
    // ========== Axis-Aligned Rectangle-Circle Collision Tests ==========
    
    /**
     * Tests no collision between axis-aligned rectangle and circle.
     */
    @Test
    public void testAxisAlignedRectangleCircleNoCollision() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(20, 20, 5);
        
        assertFalse(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    /**
     * Tests collision when circle center is inside rectangle.
     */
    @Test
    public void testAxisAlignedRectangleCircleCenterInside() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 20, 20);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(5, 5, 3);
        
        assertTrue(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    /**
     * Tests collision when circle overlaps rectangle edge.
     */
    @Test
    public void testAxisAlignedRectangleCircleEdgeOverlap() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(8, 0, 5);
        
        assertTrue(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    /**
     * Tests collision at rectangle corner.
     */
    @Test
    public void testAxisAlignedRectangleCircleCornerCollision() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(8, 8, 4);
        
        assertTrue(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    /**
     * Tests no collision when circle is near rectangle corner but not touching.
     */
    @Test
    public void testAxisAlignedRectangleCircleCornerNoCollision() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(8, 8, 2);
        
        assertFalse(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    /**
     * Tests early exit condition for clearly separated rectangle and circle.
     */
    @Test
    public void testAxisAlignedRectangleCircleEarlyExit() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(50, 50, 5);
        
        assertFalse(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    // ========== Rotated Rectangle-Circle Collision Tests ==========
    
    /**
     * Tests no collision between rotated rectangle and circle.
     */
    @Test
    public void testRotatedRectangleCircleNoCollision() {
        CollisionGeometry.RectangleParams params = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(45.0);
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(params);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(20, 20, 5);
        
        assertFalse(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    /**
     * Tests collision between rotated rectangle and circle.
     */
    @Test
    public void testRotatedRectangleCircleWithCollision() {
        CollisionGeometry.RectangleParams params = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(45.0);
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(params);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(3, 3, 4);
        
        assertTrue(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    /**
     * Tests collision with various rotation angles.
     */
    @Test
    public void testRotatedRectangleCircleVariousAngles() {
        double[] angles = {30.0, 60.0, 90.0, 120.0, 180.0, 270.0};
        
        for (double angle : angles) {
            CollisionGeometry.RectangleParams params = 
                new CollisionGeometry.RectangleParams(0, 0, 20, 10).withRotation(angle);
            CollisionGeometry.CollisionRectangle rect = 
                new CollisionGeometry.CollisionRectangle(params);
            CollisionGeometry.CollisionCircle circle = 
                new CollisionGeometry.CollisionCircle(0, 0, 3);
            
            assertTrue(collisionGeometry.isRectangleCircleCollision(rect, circle),
                "Collision should be detected at rotation angle: " + angle);
        }
    }
    
    /**
     * Tests negative rotation angles.
     */
    @Test
    public void testRotatedRectangleCircleNegativeRotation() {
        CollisionGeometry.RectangleParams params = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(-45.0);
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(params);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(2, 2, 3);
        
        assertTrue(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    // ========== Edge Cases and Boundary Tests ==========
    
    /**
     * Tests collision detection with zero-sized rectangle.
     */
    @Test
    public void testZeroSizedRectangle() {
        CollisionGeometry.CollisionRectangle zeroRect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 0, 0);
        CollisionGeometry.CollisionRectangle normalRect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        
        assertTrue(collisionGeometry.checkOBBCollision(zeroRect, normalRect));
    }
    
    /**
     * Tests collision detection with zero-radius circle.
     */
    @Test
    public void testZeroRadiusCircle() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(0, 0, 10, 10);
        CollisionGeometry.CollisionCircle zeroCircle = 
            new CollisionGeometry.CollisionCircle(0, 0, 0);
        
        assertTrue(collisionGeometry.isRectangleCircleCollision(rect, zeroCircle));
    }
    
    /**
     * Tests collision detection with very large numbers.
     */
    @Test
    public void testLargeNumbers() {
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(1000000, 1000000, 100, 100);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(1000050, 1000050, 100, 100);
        
        assertTrue(collisionGeometry.checkOBBCollision(rect1, rect2));
    }
    
    /**
     * Tests collision detection with very small numbers.
     */
    @Test
    public void testSmallNumbers() {
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(0.0001, 0.0001, 0.01, 0.01);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(0.0002, 0.0002, 0.01, 0.01);
        
        assertTrue(collisionGeometry.checkOBBCollision(rect1, rect2));
    }
    
    /**
     * Tests collision detection with identical rectangles.
     */
    @Test
    public void testIdenticalRectangles() {
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(5, 5, 10, 10);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(5, 5, 10, 10);
        
        assertTrue(collisionGeometry.checkOBBCollision(rect1, rect2));
    }
    
    /**
     * Tests collision detection with identical rectangle and circle at same position.
     */
    @Test
    public void testIdenticalRectangleCirclePosition() {
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(5, 5, 10, 10);
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(5, 5, 3);
        
        assertTrue(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
    
    /**
     * Tests rotation boundary at exactly 0.01 degrees (should be considered rotated).
     */
    @Test
    public void testRotationBoundary() {
        // Exactly at boundary (should be considered rotated)
        CollisionGeometry.RectangleParams params1 = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(0.01);
        CollisionGeometry.CollisionRectangle rect1 = 
            new CollisionGeometry.CollisionRectangle(params1);
        assertTrue(rect1.isRotated());
        
        // Just below boundary (should not be considered rotated)
        CollisionGeometry.RectangleParams params2 = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(0.009);
        CollisionGeometry.CollisionRectangle rect2 = 
            new CollisionGeometry.CollisionRectangle(params2);
        assertFalse(rect2.isRotated());
    }
    
    /**
     * Tests 360-degree rotation (should be equivalent to no rotation).
     */
    @Test
    public void testFullRotation() {
        CollisionGeometry.RectangleParams params = 
            new CollisionGeometry.RectangleParams(0, 0, 10, 10).withRotation(360.0);
        CollisionGeometry.CollisionRectangle rect = 
            new CollisionGeometry.CollisionRectangle(params);
        
        assertTrue(rect.isRotated()); // Still considered rotated due to non-zero value
        
        CollisionGeometry.CollisionCircle circle = 
            new CollisionGeometry.CollisionCircle(0, 0, 3);
        assertTrue(collisionGeometry.isRectangleCircleCollision(rect, circle));
    }
}
