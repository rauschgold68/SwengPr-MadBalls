package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Size} class.
 * <p>
 * This test class verifies the functionality of the Size class including all
 * dimensional properties such as width, height, and radius. It ensures proper
 * initialization, setter/getter operations, and data integrity for size objects
 * used throughout the game simulation.
 * </p>
 * 
 * <h2>Test Coverage Areas:</h2>
 * <ul>
 * <li><b>Constructor Testing:</b> Tests both default and parameterized constructors</li>
 * <li><b>Dimension Management:</b> Tests width, height, and radius properties</li>
 * <li><b>Data Integrity:</b> Ensures proper value storage and retrieval</li>
 * <li><b>Type Safety:</b> Verifies correct class types and precision handling</li>
 * </ul>
 * 
 * <h2>Tested Properties:</h2>
 * <ul>
 * <li><b>Width:</b> Horizontal dimension for rectangular objects</li>
 * <li><b>Height:</b> Vertical dimension for rectangular objects</li>
 * <li><b>Radius:</b> Circular dimension for round objects</li>
 * </ul>
 * 
 * @author MadBalls Team
 * @version 1.0
 * @since 1.0
 * 
 * @see Size
 * @see RectangleGeometry
 * @see CircleGeometry
 */
public class TestSize {
    
    /**
     * Tests the basic functionality of the Size class.
     * <p>
     * This test verifies object creation using the default constructor and ensures
     * that the created object is not null and has the correct class type.
     * </p>
     * 
     * @see Size#Size()
     */
    @Test
    public void testConstructr1() {
        Size testSize = new Size();
        assertNotNull(testSize);
        assertEquals(Size.class, testSize.getClass());        
    }

    /**
     * Tests the constructor functionality for rectangle Size
     */
    @Test
    public void testConstructor2() {
        float testFloat = 0.123f;
        Size testSize = new Size(testFloat, testFloat);
        assertNotNull(testSize);
        assertEquals(Size.class, testSize.getClass());
        assertEquals(testFloat, testSize.getWidth(), 0.0001);
        assertEquals(testFloat, testSize.getHeight(), 0.0001);
    }

    /**
     * Tests the Constructor functionallity for circular Size
     */
    @Test
    public void testConstructor3() {
        float testFloat = 0.123f;
        Size testSize = new Size(testFloat);
        assertNotNull(testSize);
        assertEquals(Size.class, testSize.getClass());
        assertEquals(testFloat, testSize.getRadius(), 0.0001);
    }

    /**
     * Tests the width setter and getter methods of the Size class.
     * <p>
     * This test verifies that the width property can be set and retrieved correctly,
     * ensuring proper value storage and precision handling for floating-point values.
     * </p>
     * 
     * @see Size#setWidth(float)
     * @see Size#getWidth()
     */
    @Test
    public void testWidthGetterAndSetter() {
        Size size = new Size();
        float expectedWidth = 123.45f;
        size.setWidth(expectedWidth);
        assertEquals(expectedWidth, size.getWidth(), 0.0001f);
    }

    /**
     * Tests the height setter and getter methods of the Size class.
     * <p>
     * This test verifies that the height property can be set and retrieved correctly,
     * ensuring proper value storage and precision handling for floating-point values.
     * </p>
     * 
     * @see Size#setHeight(float)
     * @see Size#getHeight()
     */
    @Test
    public void testHeightGetterAndSetter() {
        Size size = new Size();
        float expectedHeight = 67.89f;
        size.setHeight(expectedHeight);
        assertEquals(expectedHeight, size.getHeight(), 0.0001f);
    }

    /**
     * Tests the radius setter and getter methods of the Size class.
     * <p>
     * This test verifies that the radius property can be set and retrieved correctly,
     * ensuring proper value storage and precision handling for floating-point values.
     * </p>
     * 
     * @see Size#setRadius(float)
     * @see Size#getRadius()
     */
    @Test
    public void testRadiusGetterAndSetter() {
        Size size = new Size();
        float expectedRadius = 42.0f;
        size.setRadius(expectedRadius);
        assertEquals(expectedRadius, size.getRadius(), 0.0001f);
    }
}