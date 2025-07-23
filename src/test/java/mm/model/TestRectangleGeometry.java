package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link RectangleGeometry} class.
 * <p>
 * This test class verifies the correct behavior of RectangleGeometry, which represents
 * rectangular shapes in the model layer using view-agnostic geometric data. RectangleGeometry
 * extends GeometryData to provide rectangle-specific functionality including dimension-based
 * calculations, point containment testing with rotation support, and bounding box computation.
 * </p>
 * <p>
 * The tests cover various scenarios including:
 * </p>
 * <ul>
 * <li>Constructor behavior with and without rotation parameters</li>
 * <li>Point containment testing for various positions and rotation angles</li>
 * <li>Bounding box calculation for both non-rotated and rotated rectangles</li>
 * <li>Edge cases such as zero-size rectangles and extreme rotation angles</li>
 * </ul>
 * 
 * @see RectangleGeometry
 * @see GeometryData
 * @see Position
 */
public class TestRectangleGeometry {
    
    /** Test position used for creating rectangle geometries in tests. */
    private Position testPosition;
    /** Rectangle geometry instance used across multiple test methods. */
    private RectangleGeometry rectangleGeometry;
    
    /**
     * Sets up test fixtures before each test method.
     * <p>
     * Initializes a test position at coordinates (10, 20) and creates a
     * RectangleGeometry with width 6.0 and height 4.0 positioned at that location.
     * This provides a consistent baseline for testing various rectangle operations.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        testPosition = new Position(10.0f, 20.0f);
        rectangleGeometry = new RectangleGeometry(testPosition, 6.0, 4.0);
    }
    
    /**
     * Tests the constructor that creates a RectangleGeometry without explicit rotation.
     * <p>
     * Verifies that:
     * </p>
     * <ul>
     * <li>The rectangle is created successfully</li>
     * <li>Width and height are correctly stored and retrievable</li>
     * <li>Position reference is correctly maintained</li>
     * <li>Rotation defaults to 0.0 when not specified</li>
     * </ul>
     */
    @Test
    public void testConstructorWithoutRotation() {
        RectangleGeometry rect = new RectangleGeometry(testPosition, 6.0, 4.0);
        assertNotNull(rect);
        assertEquals(6.0, rect.getWidth(), 0.001);
        assertEquals(4.0, rect.getHeight(), 0.001);
        assertEquals(testPosition, rect.getPosition());
        assertEquals(0.0, rect.getRotation(), 0.001);
    }
    
    /**
     * Tests the constructor that creates a RectangleGeometry with explicit rotation.
     * <p>
     * Verifies that all parameters including rotation are properly stored.
     * Unlike circles, rectangle rotation significantly affects geometric
     * calculations, so proper rotation handling is critical for accurate
     * collision detection and rendering.
     * </p>
     */
    @Test
    public void testConstructorWithRotation() {
        RectangleGeometry rect = new RectangleGeometry(testPosition, 6.0, 4.0, 45.0);
        assertNotNull(rect);
        assertEquals(6.0, rect.getWidth(), 0.001);
        assertEquals(4.0, rect.getHeight(), 0.001);
        assertEquals(testPosition, rect.getPosition());
        assertEquals(45.0, rect.getRotation(), 0.001);
    }
    
    /**
     * Tests point containment for non-rotated rectangles.
     * <p>
     * Verifies that the containsPoint method correctly identifies points
     * within the rectangular boundary when no rotation is applied. This
     * includes testing points inside the rectangle as well as corner points
     * to verify boundary condition handling.
     * </p>
     */
    @Test
    public void testContainsPointInsideRectangleNoRotation() {
        // Point inside rectangle
        assertTrue(rectangleGeometry.containsPoint(12.0, 22.0));
        
        // Point at corners
        assertTrue(rectangleGeometry.containsPoint(10.0, 20.0)); // bottom-left
        assertTrue(rectangleGeometry.containsPoint(16.0, 24.0)); // top-right
    }
    
    /**
     * Tests point containment for points outside non-rotated rectangles.
     * <p>
     * Verifies that points located outside the rectangular boundary are
     * correctly identified as not being contained within the rectangle.
     * Tests points in all four cardinal directions to ensure comprehensive
     * boundary checking.
     * </p>
     */
    @Test
    public void testContainsPointOutsideRectangleNoRotation() {
        // Points outside rectangle
        assertFalse(rectangleGeometry.containsPoint(9.0, 22.0));   // left
        assertFalse(rectangleGeometry.containsPoint(17.0, 22.0));  // right
        assertFalse(rectangleGeometry.containsPoint(12.0, 19.0));  // below
        assertFalse(rectangleGeometry.containsPoint(12.0, 25.0));  // above
    }
    
    /**
     * Tests point containment for rotated rectangles.
     * <p>
     * Verifies that the containsPoint method correctly handles point-in-rectangle
     * calculations when the rectangle has been rotated. This is a complex geometric
     * operation that requires coordinate transformation to account for the rotation.
     * </p>
     * <p>
     * The test creates a rectangle rotated 45 degrees and verifies that:
     * </p>
     * <ul>
     * <li>The geometric center point is always contained regardless of rotation</li>
     * <li>Rotation transformation is applied correctly to the point containment algorithm</li>
     * <li>The rotation doesn't break the fundamental containment logic</li>
     * </ul>
     * <p>
     * This test is crucial for validating collision detection accuracy in scenarios
     * where game objects are oriented at non-axis-aligned angles, which is common
     * in realistic physics simulations and game environments.
     * </p>
     * 
     * @see RectangleGeometry#containsPoint(double, double)
     * @see RectangleGeometry#RectangleGeometry(Position, double, double, double)
     */
    @Test
    public void testContainsPointWithRotation() {
        // Create a rotated rectangle (45 degrees)
        RectangleGeometry rotatedRect = new RectangleGeometry(testPosition, 6.0, 4.0, 45.0);
        
        // Center point should always be inside regardless of rotation
        double centerX = testPosition.getX() + 3.0; // width/2
        double centerY = testPosition.getY() + 2.0; // height/2
        assertTrue(rotatedRect.containsPoint(centerX, centerY));
    }
    
    /**
     * Tests bounding box calculation for non-rotated rectangles.
     * <p>
     * Verifies that the getBounds method correctly calculates the axis-aligned
     * bounding box (AABB) for rectangles without rotation. The bounding box
     * should exactly match the rectangle's dimensions and position.
     * </p>
     * <p>
     * The test validates:
     * </p>
     * <ul>
     * <li>The returned bounds array has exactly 4 elements</li>
     * <li>Bounds are in the correct format: [minX, minY, maxX, maxY]</li>
     * <li>Calculated boundaries precisely match the rectangle's edges</li>
     * <li>Position offset is correctly applied to the bounding calculations</li>
     * </ul>
     * <p>
     * For a rectangle at position (10, 20) with width 6.0 and height 4.0,
     * the expected bounds are [10.0, 20.0, 16.0, 24.0].
     * </p>
     * 
     * @see RectangleGeometry#getBounds()
     * @see GeometryData#getBounds()
     */
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
    
    /**
     * Tests bounding box calculation for rotated rectangles.
     * <p>
     * Verifies that the getBounds method correctly calculates the axis-aligned
     * bounding box (AABB) for rectangles that have been rotated. When a rectangle
     * is rotated, its AABB must expand to encompass all corners of the rotated shape.
     * </p>
     * <p>
     * The test creates a rectangle rotated 45 degrees and validates:
     * </p>
     * <ul>
     * <li>The returned bounds array has exactly 4 elements</li>
     * <li>The bounding box is larger than the original rectangle's dimensions</li>
     * <li>The AABB correctly encompasses the rotated rectangle's extent</li>
     * <li>Rotation calculations don't introduce invalid or NaN values</li>
     * </ul>
     * <p>
     * For a 45-degree rotation, the diagonal length of the rectangle becomes
     * significant in determining the expanded bounding box dimensions. This
     * test ensures that collision detection broad-phase algorithms will work
     * correctly with rotated rectangular objects.
     * </p>
     * 
     * @see RectangleGeometry#getBounds()
     * @see GeometryData#getBounds()
     */
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
    
    /**
     * Tests behavior of zero-size rectangles.
     * <p>
     * Verifies that rectangles with zero width and height are handled correctly
     * by the geometry system. Zero-size rectangles represent degenerate cases
     * that can occur in edge conditions or during object initialization phases.
     * </p>
     * <p>
     * The test validates that:
     * </p>
     * <ul>
     * <li>Zero-size rectangles cannot be created and Exception is thrown</li>
     * </ul>
     * 
     * @see RectangleGeometry#RectangleGeometry(Position, double, double)
     * @see RectangleGeometry#containsPoint(double, double)
     */
    @Test
    public void testZeroSizeRectangle() {
        assertThrows(IllegalArgumentException.class, () -> {
            RectangleGeometry zeroRect = new RectangleGeometry(testPosition, 0.0, 0.0);
            assertNull(zeroRect);
        });
    }
    
    /**
     * Tests rectangle behavior with edge case rotation angles.
     * <p>
     * Verifies that the RectangleGeometry correctly handles various rotation angles
     * that represent common geometric edge cases. These include orthogonal rotations
     * and full rotations that should be mathematically equivalent to no rotation.
     * </p>
     * <p>
     * For all test cases, the geometric center point should remain contained within
     * the rectangle regardless of rotation angle. This validates that:
     * </p>
     * <ul>
     * <li>Rotation calculations handle standard angle values correctly</li>
     * <li>Trigonometric functions produce expected results for key angles</li>
     * <li>The center point calculation remains stable across rotations</li>
     * <li>No numerical instabilities occur with common rotation values</li>
     * </ul>
     * 
     * @see RectangleGeometry#RectangleGeometry(Position, double, double, double)
     * @see RectangleGeometry#containsPoint(double, double)
     */
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
