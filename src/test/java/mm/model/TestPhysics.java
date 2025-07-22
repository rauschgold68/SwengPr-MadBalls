package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Physics} class.
 * <p>
 * This test class verifies the functionality of the Physics class including all
 * setter and getter methods for physical properties such as density, friction,
 * restitution, and shape configuration.
 * </p>
 * 
 * <h2>Test Coverage Areas:</h2>
 * <ul>
 * <li><b>Constructor Testing:</b> Tests both default and parameterized constructors</li>
 * <li><b>Property Management:</b> Tests all setter/getter methods for physical properties</li>
 * <li><b>Object Integrity:</b> Ensures proper object creation and type consistency</li>
 * </ul>
 * 
 * @author MadBalls Team
 * @version 1.0
 * @since 1.0
 * 
 * @see Physics
 * @see org.junit.jupiter.api.Test
 */
public class TestPhysics {
    
    /**
     * Tests the basic functionality of the Physics class.
     * <p>
     * This test method verifies:
     * </p>
     * <ul>
     * <li>Object creation using default constructor</li>
     * <li>Object is not null after creation</li>
     * <li>Correct class type assignment</li>
     * <li>Parameterized constructor with all physics properties</li>
     * <li>Proper assignment of constructor parameters</li>
     * </ul>
     * 
     * @see Physics#Physics()
     * @see Physics#Physics(float, float, float, String)
     */
    @Test
    public void testPhysics() {
        Physics testPhysics = new Physics();
        assertNotNull(testPhysics);
        assertEquals(Physics.class, testPhysics.getClass());
        float testFloat = 0.123f;
        String testString = "test";
        testPhysics = new Physics(testFloat, testFloat, testFloat, testString);
        testAssertions(testPhysics, testFloat, testFloat, testFloat, testString);
    }

    /**
     * Tests all setter and getter methods of the Physics class.
     * <p>
     * This test method validates:
     * </p>
     * <ul>
     * <li>Density property setter and getter functionality</li>
     * <li>Friction property setter and getter functionality</li>
     * <li>Restitution property setter and getter functionality</li>
     * <li>Shape property setter and getter functionality</li>
     * <li>Proper value retention after setting properties</li>
     * </ul>
     * 
     * @see Physics#setDensity(float)
     * @see Physics#getDensity()
     * @see Physics#setFriction(float)
     * @see Physics#getFriction()
     * @see Physics#setRestitution(float)
     * @see Physics#getRestitution()
     * @see Physics#setShape(String)
     * @see Physics#getShape()
     */
    @Test
    public void testSetterGetter() {
        float testDensity = 0.123f;
        float testFriction = 456.7f;
        float testRestitution = 8.90f;
        String testShape = "DYNAMIC";
        Physics testPhysics = new Physics();
        testPhysics.setDensity(testDensity);
        testPhysics.setFriction(testFriction);
        testPhysics.setRestitution(testRestitution);
        testPhysics.setShape(testShape);
        testAssertions(testPhysics, testDensity, testFriction, testRestitution, testShape);
    }
    private void testAssertions(Physics testPhysics, float testDensity, float testFriction, float testRestitution, String testShape) {
        assertEquals(testDensity, testPhysics.getDensity(),0.00001);
        assertEquals(testFriction, testPhysics.getFriction(),0.00001);
        assertEquals(testRestitution, testPhysics.getRestitution(),0.00001);
        assertEquals(testShape, testPhysics.getShape());
    }
}