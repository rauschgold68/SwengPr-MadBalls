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
        
        PhysicsTestValues expectedValues = new PhysicsTestValues(testFloat, testFloat, testFloat, testString);
        testAssertions(testPhysics, expectedValues);
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
        
        // Direct assertions to satisfy PMD JUnitTestsShouldIncludeAssert rule
        assertNotNull(testPhysics);
        assertEquals(testDensity, testPhysics.getDensity(), 0.00001f);
        
        PhysicsTestValues expectedValues = new PhysicsTestValues(testDensity, testFriction, testRestitution, testShape);
        testAssertions(testPhysics, expectedValues);
    }

    /**
     * Helper method to validate all physics properties match expected values.
     * <p>
     * This method performs comprehensive validation of all physics properties
     * to ensure they match the expected values with appropriate floating-point
     * precision tolerance.
     * </p>
     * 
     * @param physics the Physics object to validate
     * @param expectedValues container holding all expected physics property values
     * 
     * @see Physics#getDensity()
     * @see Physics#getFriction()
     * @see Physics#getRestitution()
     * @see Physics#getShape()
     */
    private void testAssertions(Physics physics, PhysicsTestValues expectedValues) {
        assertEquals(expectedValues.density, physics.getDensity(), 0.00001f);
        assertEquals(expectedValues.friction, physics.getFriction(), 0.00001f);
        assertEquals(expectedValues.restitution, physics.getRestitution(), 0.00001f);
        assertEquals(expectedValues.shape, physics.getShape());
    }

    /**
     * Container class for physics test values to reduce parameter count in helper methods.
     * <p>
     * This inner class encapsulates all physics property values used in testing,
     * following the Parameter Object pattern to improve code maintainability
     * and reduce method parameter lists.
     * </p>
     */
    private static class PhysicsTestValues {
        /** The density value for physics testing. */
        final float density;
        /** The friction value for physics testing. */
        final float friction;
        /** The restitution (bounciness) value for physics testing. */
        final float restitution;
        /** The shape type string for physics testing. */
        final String shape;

        /**
         * Constructs a PhysicsTestValues container with all physics properties.
         * 
         * @param density the density value
         * @param friction the friction value
         * @param restitution the restitution value
         * @param shape the shape type string
         */
        PhysicsTestValues(float density, float friction, float restitution, String shape) {
            this.density = density;
            this.friction = friction;
            this.restitution = restitution;
            this.shape = shape;
        }
    }
}