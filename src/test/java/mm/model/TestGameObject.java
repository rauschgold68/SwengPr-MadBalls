package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test-Class for GameObject class.
 * Tests basic functionality of constructor and getter setter methods.
 */
public class TestGameObject extends TestAbstractObject {
    
    /**
     * Tests the core functionality of the GameObject class.
     * 
     * This test method verifies multiple aspects of the GameObject class:
     * <ol>
     *   <li><strong>Default Constructor:</strong> Ensures the default constructor creates
     *       a valid, non-null GameObject instance with proper class initialization</li>
     *   <li><strong>Position Management:</strong> Tests the position property setter and getter
     *       methods to ensure proper encapsulation and data integrity</li>
     *   <li><strong>Parameterized Constructor:</strong> Validates that the full constructor
     *       accepting name, description, position, and size parameters works correctly</li>
     * </ol>
     * 
     * @throws AssertionError if any of the GameObject functionality tests fail
     * 
     * @see GameObject#GameObject()
     * @see GameObject#GameObject(String, String, Position, Size)
     * @see GameObject#setPosition(Position)
     * @see GameObject#getPosition()
     */
    @Test
    public void testGameObject() {
        GameObject testObj = new GameObject();
        assertNotNull(testObj);
        assertEquals(GameObject.class, testObj.getClass());

        Position testPosition = new Position();
        testObj.setPosition(testPosition);
        assertEquals(testPosition, testObj.getPosition());

        String testString = "test";
        Size testSize = new Size();
        testObj = new GameObject(testString, testString, testPosition, testSize);
        assertNotNull(testObj);
        assertEquals(GameObject.class, testObj.getClass());
    } 
}