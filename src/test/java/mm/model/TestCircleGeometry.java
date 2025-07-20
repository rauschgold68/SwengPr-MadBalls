package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestCircleGeometry {
    
    private Position testPosition;
    private CircleGeometry circleGeometry;
    
    @BeforeEach
    public void setUp() {
        testPosition = new Position(10.0f, 20.0f);
        circleGeometry = new CircleGeometry(testPosition, 5.0);
    }
    
    @Test
    public void testConstructorWithoutRotation() {
        CircleGeometry circle = new CircleGeometry(testPosition, 5.0);
        assertNotNull(circle);
        assertEquals(5.0, circle.getRadius(), 0.001);
        assertEquals(testPosition, circle.getPosition());
        assertEquals(0.0, circle.getRotation(), 0.001);
    }
    
    @Test
    public void testConstructorWithRotation() {
        CircleGeometry circle = new CircleGeometry(testPosition, 5.0, 45.0);
        assertNotNull(circle);
        assertEquals(5.0, circle.getRadius(), 0.001);
        assertEquals(testPosition, circle.getPosition());
        assertEquals(45.0, circle.getRotation(), 0.001);
    }
    
    @Test
    public void testContainsPointInsideCircle() {
        // Point at center of circle (15, 25)
        assertTrue(circleGeometry.containsPoint(15.0, 25.0));
        
        // Point just inside circle
        assertTrue(circleGeometry.containsPoint(18.0, 25.0)); // 3 units from center
    }
    
    @Test
    public void testContainsPointOnCircleEdge() {
        // Point exactly on circle edge
        assertTrue(circleGeometry.containsPoint(20.0, 25.0)); // 5 units from center
    }
    
    @Test
    public void testContainsPointOutsideCircle() {
        // Point clearly outside circle
        assertFalse(circleGeometry.containsPoint(25.0, 25.0)); // 10 units from center
        
        // Point just outside circle
        assertFalse(circleGeometry.containsPoint(20.1, 25.0)); // 5.1 units from center
    }
    
    @Test
    public void testGetBounds() {
        double[] bounds = circleGeometry.getBounds();
        
        assertEquals(4, bounds.length);
        // Expected bounds: [10, 20, 20, 30] (center at 15,25, radius 5)
        assertEquals(10.0, bounds[0], 0.001); // minX
        assertEquals(20.0, bounds[1], 0.001); // minY
        assertEquals(20.0, bounds[2], 0.001); // maxX
        assertEquals(30.0, bounds[3], 0.001); // maxY
    }
    
    @Test
    public void testZeroRadius() {
        CircleGeometry zeroCircle = new CircleGeometry(testPosition, 0.0);
        
        // Point at center should be inside
        assertTrue(zeroCircle.containsPoint(10.0, 20.0));
        
        // Any other point should be outside
        assertFalse(zeroCircle.containsPoint(10.1, 20.0));
    }
}
