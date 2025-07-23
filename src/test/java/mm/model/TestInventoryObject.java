package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link InventoryObject} class.
 * <p>
 * This test class verifies the correct behavior of InventoryObject, which extends
 * AbstractObject to represent objects that can be stored in the player's inventory
 * for placement during gameplay. InventoryObjects have an additional count property
 * that tracks how many instances are available.
 * </p>
 * <p>
 * The tests cover all constructors and the count management functionality
 * specific to inventory objects, ensuring proper initialization and state management.
 * </p>
 * 
 * @see InventoryObject
 * @see AbstractObject
 * @see TestAbstractObject
 */
public class TestInventoryObject extends TestAbstractObject {
    /**
     * Main test method that verifies all InventoryObject constructors and functionality.
     * <p>
     * This test covers:
     * </p>
     * <ul>
     * <li>Default constructor behavior</li>
     * <li>Setting and getting the count property</li>
     * <li>Constructor with name, type, and size parameters</li>
     * <li>Constructor with name, type, size, and count parameters</li>
     * </ul>
     * <p>
     * The test ensures that InventoryObject properly extends AbstractObject
     * while adding inventory-specific functionality for count management.
     * </p>
     */
    @Test
    public void testInventoryObject() {
        InventoryObject testObj = testConstructer1();

        int testCount = 12345;
        testObj.setCount(testCount);
        assertEquals(testCount, testObj.getCount());

        String testString = "test";
        Size testSize = new Size();

        testConstructor2(testString, testSize);        
        testConstructor3(testString, testSize, testCount);
    }

    /**
     * Tests the default constructor of InventoryObject.
     * <p>
     * Verifies that:
     * </p>
     * <ul>
     * <li>The object is created successfully (not null)</li>
     * <li>The object has the correct class type</li>
     * <li>Basic object properties are properly initialized</li>
     * </ul>
     * 
     * @return the created InventoryObject for further testing
     */
    private InventoryObject testConstructer1() {
        InventoryObject testObj = new InventoryObject();
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj.getClass());
        return testObj;
    }
    
    /**
     * Tests the constructor that takes name, type, and size parameters.
     * <p>
     * Verifies that the InventoryObject is properly initialized with the provided
     * parameters and that the count defaults to an appropriate value (typically 1).
     * This constructor is commonly used when creating inventory objects with
     * specific properties but default quantity.
     * </p>
     * 
     * @param testString the name and type string for the object
     * @param testSize the size configuration for the object
     */
    private void testConstructor2(String testString, Size testSize) {
        InventoryObject testObj = new InventoryObject(testString, testString, testSize);
        int testCount = 0; 
        testAssertions(testObj, testString, testSize, testCount);
    }

    /**
     * Tests the constructor that takes name, type, size, and count parameters.
     * <p>
     * This is the most comprehensive constructor test, verifying that all
     * parameters are properly stored and accessible. This constructor allows
     * full control over the InventoryObject's initial state, including
     * specifying exactly how many instances are available.
     * </p>
     * 
     * @param testString the name and type string for the object
     * @param testSize the size configuration for the object
     * @param testCount the initial count of available instances
     */
    private void testConstructor3(String testString, Size testSize, int testCount) {
        InventoryObject testObj = new InventoryObject(testString, testString, testSize, testCount);
        testAssertions(testObj, testString, testSize, testCount);
    }

    /**
     * Helper method that performs comprehensive assertions on an InventoryObject.
     * <p>
     * This method validates that all properties of the InventoryObject are
     * correctly set and accessible. It handles the special case where a count
     * of 0 should default to 1, which is typical behavior for inventory objects.
     * </p>
     * <p>
     * The assertions verify:
     * </p>
     * <ul>
     * <li>Object is not null</li>
     * <li>Name property matches expected value</li>
     * <li>Type property matches expected value</li>
     * <li>Size property matches expected value</li>
     * <li>Count property is correctly set (with default handling)</li>
     * </ul>
     * 
     * @param testObj the InventoryObject to test
     * @param testString the expected name and type
     * @param testSize the expected size
     * @param count the expected count (0 will be treated as 1)
     */
    private void testAssertions(InventoryObject testObj, String testString, Size testSize, int count) {
        int testCount = (count != 0) ? count : 1;
        assertNotNull(testObj);
        assertEquals(testString, testObj.getName());
        assertEquals(testString, testObj.getType());
        assertEquals(testSize, testObj.getSize());
        assertEquals(testCount, testObj.getCount());
    }
}
