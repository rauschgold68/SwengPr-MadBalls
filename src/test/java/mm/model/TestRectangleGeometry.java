package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestRectangleGeometry {
    
    private Position testPosition;
    private RectangleGeometry rectangleGeometry;
    
    @BeforeEach
    public void setUp() {
        testPosition = new Position(10.0f, 20.0f);
        rectangleGeometry = new RectangleGeometry(testPosition, 6.0, 4.0);
    }
    
    @Test
    public void testConstructorWithoutRotation() {
        RectangleGeometry rect = new RectangleGeometry(testPosition, 6.0, 4.0);
        assertNotNull(rect);
        assertEquals(6.0, rect.getWidth(), 0.001);
        assertEquals(4.0, rect.getHeight(), 0.001);
        assertEquals(testPosition, rect.getPosition());
        assertEquals(0.0, rect.getRotation(), 0.001);
    }
    
    @Test
    public void testConstructorWithRotation() {
        RectangleGeometry rect = new RectangleGeometry(testPosition, 6.0, 4.0, 45.0);
        assertNotNull(rect);
        assertEquals(6.0, rect.getWidth(), 0.001);
        assertEquals(4.0, rect.getHeight(), 0.001);
        assertEquals(testPosition, rect.getPosition());
        assertEquals(45.0, rect.getRotation(), 0.001);
    }
    
    @Test
    public void testContainsPointInsideRectangleNoRotation() {
        // Point inside rectangle
        assertTrue(rectangleGeometry.containsPoint(12.0, 22.0));
        
        // Point at corners
        assertTrue(rectangleGeometry.containsPoint(10.0, 20.0)); // bottom-left
        assertTrue(rectangleGeometry.containsPoint(16.0, 24.0)); // top-right
    }
    
    @Test
    public void testContainsPointOutsideRectangleNoRotation() {
        // Points outside rectangle
        assertFalse(rectangleGeometry.containsPoint(9.0, 22.0));   // left
        assertFalse(rectangleGeometry.containsPoint(17.0, 22.0));  // right
        assertFalse(rectangleGeometry.containsPoint(12.0, 19.0));  // below
        assertFalse(rectangleGeometry.containsPoint(12.0, 25.0));  // above
    }
    
    @Test
    public void testContainsPointWithRotation() {
        // Create a rotated rectangle (45 degrees)
        RectangleGeometry rotatedRect = new RectangleGeometry(testPosition, 6.0, 4.0, 45.0);
        
        // Center point should always be inside regardless of rotation
        double centerX = testPosition.getX() + 3.0; // width/2
        double centerY = testPosition.getY() + 2.0; // height/2
        assertTrue(rotatedRect.containsPoint(centerX, centerY));
    }
    
    @Test
    public void testGetBoundsNoRotation() {
        double[] bounds = rectangleGeometry.getBounds();
        
        assertEquals(4, bounds.length);
        // Expected bounds: [10, 20, 16, 24]
        assertEquals(10.0, bounds[0], 0.001); // minX
        assertEquals(20.0, bounds[1], 0.001); // minY
        assertEquals(16.0, bounds[2], 0.001); // maxX
        assertEquals(24.0, bounds[3], 0.001); // maxY
    }
    
    @Test
    public void testGetBoundsWithRotation() {
        RectangleGeometry rotatedRect = new RectangleGeometry(testPosition, 6.0, 4.0, 45.0);
        double[] bounds = rotatedRect.getBounds();
        
        assertEquals(4, bounds.length);
        // With 45 degree rotation, bounds should be expanded
        // We just verify that bounds are larger than the original rectangle
        assertTrue(bounds[2] - bounds[0] > 6.0); // width > original width
        assertTrue(bounds[3] - bounds[1] > 4.0); // height > original height
    }
    
    @Test
    public void testZeroSizeRectangle() {
        RectangleGeometry zeroRect = new RectangleGeometry(testPosition, 0.0, 0.0);
        
        // Only the exact position should be inside
        assertTrue(zeroRect.containsPoint(10.0, 20.0));
        assertFalse(zeroRect.containsPoint(10.1, 20.0));
    }
    
    @Test
    public void testEdgeCasesRotation() {
        // Test 90 degree rotation
        RectangleGeometry rect90 = new RectangleGeometry(testPosition, 6.0, 4.0, 90.0);
        double centerX = testPosition.getX() + 3.0;
        double centerY = testPosition.getY() + 2.0;
        assertTrue(rect90.containsPoint(centerX, centerY));
        
        // Test 180 degree rotation
        RectangleGeometry rect180 = new RectangleGeometry(testPosition, 6.0, 4.0, 180.0);
        assertTrue(rect180.containsPoint(centerX, centerY));
        
        // Test 360 degree rotation (should be same as 0)
        RectangleGeometry rect360 = new RectangleGeometry(testPosition, 6.0, 4.0, 360.0);
        assertTrue(rect360.containsPoint(centerX, centerY));
    }
}
