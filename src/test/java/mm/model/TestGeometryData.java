package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link GeometryData} abstract class and its implementations.
 * <p>
 * This test class verifies the correct behavior of GeometryData and its concrete
 * implementations (such as CircleGeometry) used for view-agnostic geometric
 * representations in the model layer.
 * </p>
 * <p>
 * GeometryData provides the mathematical foundation for shape collision detection,
 * rendering calculations, and physics simulation without being tied to any specific
 * UI framework like JavaFX.
 * </p>
 * 
 * @see GeometryData
 * @see CircleGeometry
 * @see Position
 */
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
