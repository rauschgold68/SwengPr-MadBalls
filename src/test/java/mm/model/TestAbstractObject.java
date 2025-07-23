package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AbstractObject} class.
 * <p>
 * This test class verifies the correct behavior of AbstractObject constructors
 * and basic functionality. AbstractObject serves as the base class for game objects
 * in the simulation, providing common fields and methods for name, size, and physics properties.
 * </p>
 * <p>
 * The tests cover all constructor variants to ensure proper initialization
 * of object properties under different scenarios.
 * </p>
 * 
 * @see AbstractObject
 * @see Size
 * @see Physics
 */
public class TestAbstractObject {
    @Test
    public void testAbstractObject() {
        boolean test1 = testConstructer1();
        
        String testString = "test";
        Size testSize = new Size();
        boolean test2 = testConstructor2(testString, testSize);

        Physics testPhysics = new Physics();
        boolean test3 = testConstructor3(testString, testSize, testPhysics);
        assertTrue(test1 && test2 && test3);
    }

    @Test 
    public void testStringSetterGetter() {
        String testString = "this is a test: succes!";
        AbstractObject testObj = new AbstractObject();
        testObj.setSprite(testString);
        testObj.setColour(testString);
        assertEquals(testString, testObj.getSprite());
        assertEquals(testString, testObj.getColour());
    }

    @Test
    public void testSizeSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        Size testSize = new Size();
        testObj.setSize(testSize);
        assertEquals(testSize, testObj.getSize()); 
    }

    @Test
    public void testAngleSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        float testAngle = 123.4f; 
        testObj.setAngle(testAngle);
        assertEquals(testAngle, testObj.getAngle(), 0.0001f);
    }

    @Test
    public void testPhysicsSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        Physics testPhysics = new Physics();
        testObj.setPhysics(testPhysics);
        assertEquals(testPhysics, testObj.getPhysics());
    }

    @Test
    public void testWinningSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        boolean winning = true;
        testObj.setWinning(winning);
        assertEquals(winning, testObj.isWinning());
    }

    private boolean testConstructer1() {
        AbstractObject testObj = new AbstractObject();
        assertNotNull(testObj);
        assertEquals(AbstractObject.class, testObj.getClass());
        return true;
    }
    private boolean testConstructor2(String testString, Size testSize) {
        AbstractObject testObj = new AbstractObject(testString, testString, testSize);
        testAssertions(testObj, testString, testSize, null);
        return true;
    }

    private boolean testConstructor3(String testString, Size testSize, Physics testPhysics) {
        AbstractObject testObj = new AbstractObject(testString, testString, testSize, testPhysics);
        testAssertions(testObj, testString, testSize, testPhysics);
        return true;
    }

    private void testAssertions(AbstractObject testObj, String testString, Size testSize, Physics testPhysics) {
        assertNotNull(testObj);
        assertEquals(AbstractObject.class, testObj.getClass());
        assertEquals(testString, testObj.getName());
        assertEquals(testString, testObj.getType());
        assertEquals(testSize, testObj.getSize());
        testAssertions2(testObj, testPhysics);
    }

    private void testAssertions2(AbstractObject testObj, Physics testPhysics) {
        assertEquals(0.0f, testObj.getAngle(), 0.0001f);
        //test condition inverted for less static imports because of PMD violations
        assertTrue(!testObj.isWinning());
        assertEquals(testPhysics, testObj.getPhysics());
    }
}
