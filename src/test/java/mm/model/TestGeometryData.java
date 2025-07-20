package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGeometryData {
    
    private Position testPosition;
    private GeometryData testGeometry;
    
    @BeforeEach
    public void setUp() {
        testPosition = new Position(10.0f, 20.0f);
        // Use CircleGeometry as concrete implementation for testing
        testGeometry = new CircleGeometry(testPosition, 5.0, 30.0);
    }
    
    @Test
    public void testGetPosition() {
        assertEquals(testPosition, testGeometry.getPosition());
    }
    
    @Test
    public void testGetRotation() {
        assertEquals(30.0, testGeometry.getRotation(), 0.001);
    }
    
    @Test
    public void testZeroRotation() {
        GeometryData zeroRotationGeometry = new CircleGeometry(testPosition, 5.0, 0.0);
        assertEquals(0.0, zeroRotationGeometry.getRotation(), 0.001);
    }
    
    @Test
    public void testNegativeRotation() {
        GeometryData negativeRotationGeometry = new CircleGeometry(testPosition, 5.0, -45.0);
        assertEquals(-45.0, negativeRotationGeometry.getRotation(), 0.001);
    }
    
    @Test
    public void testAbstractMethodsExist() {
        // Verify that abstract methods are implemented
        assertNotNull(testGeometry.containsPoint(15.0, 25.0));
        assertNotNull(testGeometry.getBounds());
    }
}
