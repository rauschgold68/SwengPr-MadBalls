package mm.model;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Level} class.
 * <p>
 * This test class verifies the functionality of the Level class including
 * the management of GameObject collections and InventoryObject collections.
 * It ensures proper initialization, setter/getter operations, and data integrity
 * for level configuration objects.
 * </p>
 * 
 * <h2>Test Coverage Areas:</h2>
 * <ul>
 * <li><b>Object Creation:</b> Tests Level instance creation and initialization</li>
 * <li><b>Collection Management:</b> Tests setting and getting of level objects and inventory objects</li>
 * <li><b>Data Integrity:</b> Ensures proper storage and retrieval of collections</li>
 * <li><b>Type Safety:</b> Verifies correct class types and null safety</li>
 * </ul>
 * 
 * @author MadBalls Team
 * @version 1.0
 * @since 1.0
 * 
 * @see Level
 * @see GameObject
 * @see InventoryObject
 */
public class TestLevel {
    
    /**
     * Tests the basic functionality of the Level class.
     * <p>
     * This test method verifies:
     * </p>
     * <ul>
     * <li>Proper object creation and non-null initialization</li>
     * <li>Correct class type assignment</li>
     * <li>Setter/getter methods for LevelObjects collection</li>
     * <li>Setter/getter methods for InventoryObjects collection</li>
     * <li>Data persistence through setter/getter cycles</li>
     * </ul>
     * 
     * @see Level#setLevelObjects(List)
     * @see Level#getLevelObjects()
     * @see Level#setInventoryObjects(List)
     * @see Level#getInventoryObjects()
     */
    @Test
    public void testLevel() {
        Level testLevel = new Level();
        assertNotNull(testLevel);
        assertEquals(Level.class, testLevel.getClass());

        List<GameObject> gameObjects = new ArrayList<>();
        List<InventoryObject> inventoryObjects = new ArrayList<>();

        testLevel.setLevelObjects(gameObjects);
        testLevel.setInventoryObjects(inventoryObjects);
        assertEquals(gameObjects, testLevel.getLevelObjects());
        assertEquals(inventoryObjects, testLevel.getInventoryObjects());
    }
}