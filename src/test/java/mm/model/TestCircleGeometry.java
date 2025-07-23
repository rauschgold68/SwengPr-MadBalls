package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link CircleGeometry} class.
 * <p>
 * This test class verifies the correct behavior of CircleGeometry, which represents
 * circular shapes in the model layer using view-agnostic geometric data. CircleGeometry
 * extends GeometryData to provide circle-specific functionality including radius-based
 * calculations, point containment testing, and bounding box computation.
 * </p>
 * <p>
 * The tests cover various scenarios including:
 * </p>
 * <ul>
 * <li>Constructor behavior with and without rotation parameters</li>
 * <li>Point containment testing for various positions relative to the circle</li>
 * <li>Bounding box calculation accuracy</li>
 * <li>Edge cases such as zero radius circles</li>
 * </ul>
 * 
 * @see CircleGeometry
 * @see GeometryData
 * @see Position
 */
public class TestCircleGeometry {
    
    /** Test position used for creating circle geometries in tests. */
    private Position testPosition;
    /** Circle geometry instance used across multiple test methods. */
    private CircleGeometry circleGeometry;
    
    /**
     * Sets up test fixtures before each test method.
     * <p>
     * Initializes a test position at coordinates (10, 20) and creates a
     * CircleGeometry with radius 5.0 centered at that position. This provides
     * a consistent baseline for testing various circle operations.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        testPosition = new Position(10.0f, 20.0f);
        circleGeometry = new CircleGeometry(testPosition, 5.0);
    }
    
    /**
     * Tests the constructor that creates a CircleGeometry without explicit rotation.
     * <p>
     * Verifies that:
     * </p>
     * <ul>
     * <li>The circle is created successfully</li>
     * <li>Radius is correctly stored and retrievable</li>
     * <li>Position reference is correctly maintained</li>
     * <li>Rotation defaults to 0.0 when not specified</li>
     * </ul>
     */
    @Test
    public void testConstructorWithoutRotation() {
        CircleGeometry circle = new CircleGeometry(testPosition, 5.0);
        assertNotNull(circle);
        assertEquals(5.0, circle.getRadius(), 0.001);
        assertEquals(testPosition, circle.getPosition());
        assertEquals(0.0, circle.getRotation(), 0.001);
    }
    
    /**
     * Tests the constructor that creates a CircleGeometry with explicit rotation.
     * <p>
     * Verifies that all parameters including rotation are properly stored.
     * While rotation doesn't affect circle geometry calculations (circles are
     * rotationally symmetric), it's important to ensure the property is correctly
     * maintained for consistency with other geometry types.
     * </p>
     */
    @Test
    public void testConstructorWithRotation() {
        CircleGeometry circle = new CircleGeometry(testPosition, 5.0, 45.0);
        assertNotNull(circle);
        assertEquals(5.0, circle.getRadius(), 0.001);
        assertEquals(testPosition, circle.getPosition());
        assertEquals(45.0, circle.getRotation(), 0.001);
    }
    
    /**
     * Tests point containment for points inside the circle.
     * <p>
     * Verifies that the containsPoint method correctly identifies points
     * that are within the circle's radius. This includes both the center
     * point and points at various distances from the center that are
     * still within the circular boundary.
     * </p>
     * <p>
     * The test uses a circle centered at (15, 25) with radius 5, derived
     * from the test position (10, 20) with appropriate offset calculations.
     * </p>
     */
    @Test
    public void testContainsPointInsideCircle() {
        // Point at center of circle (15, 25)
        assertTrue(circleGeometry.containsPoint(15.0, 25.0));
        
        // Point just inside circle
        assertTrue(circleGeometry.containsPoint(18.0, 25.0)); // 3 units from center
    }
    
    /**
     * Tests point containment for points exactly on the circle's edge.
     * <p>
     * Verifies that points located exactly at the circle's radius distance
     * from the center are correctly identified as being contained within
     * the circle. This tests the boundary condition handling of the
     * containsPoint method.
     * </p>
     */
    @Test
    public void testContainsPointOnCircleEdge() {
        // Point exactly on circle edge
        assertTrue(circleGeometry.containsPoint(20.0, 25.0)); // 5 units from center
    }
    
    /**
     * Tests point containment for points outside the circle.
     * <p>
     * Verifies that points located beyond the circle's radius are correctly
     * identified as not being contained within the circle. This includes
     * both points that are clearly outside and points that are just barely
     * outside the circular boundary.
     * </p>
     */
    @Test
    public void testContainsPointOutsideCircle() {
        // Point clearly outside circle
        assertFalse(circleGeometry.containsPoint(25.0, 25.0)); // 10 units from center
        
        // Point just outside circle
        assertFalse(circleGeometry.containsPoint(20.1, 25.0)); // 5.1 units from center
    }
    
    /**
     * Tests the accuracy of bounding box calculation.
     * <p>
     * Verifies that the getBounds method returns the correct axis-aligned
     * bounding box for the circle. The bounding box should be the smallest
     * rectangle that completely contains the circle, with coordinates
     * calculated as center ± radius in both dimensions.
     * </p>
     * <p>
     * Expected bounds format: [minX, minY, maxX, maxY]
     * </p>
     */
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
    
    /**
     * Tests the behavior of circles with zero radius.
     * <p>
     * This edge case test verifies that circles with zero radius behave
     * correctly - they should contain only their center point and exclude
     * all other points, no matter how close. This is important for handling
     * degenerate cases in collision detection and geometric calculations.
     * </p>
     */
    @Test
    public void testZeroRadius() {
        CircleGeometry zeroCircle = new CircleGeometry(testPosition, 0.0);
        
        // Point at center should be inside
        assertTrue(zeroCircle.containsPoint(10.0, 20.0));
        
        // Any other point should be outside
        assertFalse(zeroCircle.containsPoint(10.1, 20.0));
    }
}
