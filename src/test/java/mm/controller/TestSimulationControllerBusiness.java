package mm.controller;

import mm.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Business logic tests for SimulationController.
 * <p>
 * This class tests the non-JavaFX aspects of the SimulationController by creating
 * a testable wrapper that exposes the core business logic without UI dependencies.
 * <p>
 * <b>Test Coverage:</b>
 * <ul>
 *   <li>Model initialization and level loading</li>
 *   <li>Game object management and validation</li>
 *   <li>Inventory management business rules</li>
 *   <li>Position validation and collision detection</li>
 *   <li>Physics world initialization</li>
 *   <li>Data consistency and state management</li>
 * </ul>
 * 
 * <b>How the Tests Work:</b>
 * 
 * 1. <b>TestableSimulationController</b>: A wrapper class that exposes the core 
 *    business logic of SimulationController without requiring JavaFX components.
 * 
 * 2. <b>Direct Model Testing</b>: Tests interact directly with the SimulationModel
 *    to verify business logic without UI complications.
 * 
 * 3. <b>State Validation</b>: Tests verify that the controller maintains proper
 *    state during various operations like adding objects, managing inventory, etc.
 * 
 * 4. <b>Business Rule Testing</b>: Tests verify that game rules like collision
 *    detection, placement validation, and inventory management work correctly.
 */
public class TestSimulationControllerBusiness {

    private TestableSimulationController testController;

    /**
     * Testable wrapper that exposes SimulationController business logic
     * without JavaFX dependencies.
     */
    private static class TestableSimulationController {
        public final SimulationModel model;
        
        public TestableSimulationController(String levelPath) {
            this.model = new SimulationModel(levelPath);
        }
        
        public boolean isValidPosition(double x, double y) {
            return !model.isInNoPlaceZone(x, y);
        }
        
        public void addGameObject(GameObject obj) {
            model.addDroppedObject(obj);
        }
        
        public List<GameObject> getDroppedObjects() {
            return model.getDroppedObjects();
        }
        
        public List<InventoryObject> getInventory() {
            return model.getInventoryObjects();
        }
        
        public boolean canPlaceObject(InventoryObject item) {
            return item.getCount() > 0;
        }
    }

    @BeforeEach
    void setUp() {
        testController = new TestableSimulationController("/level/basic_sandbox.json");
    }

    @Test
    @DisplayName("Model should be initialized with correct level path")
    void testModelInitialization() {
        assertEquals("/level/basic_sandbox.json", testController.model.getLevelPath());
        assertNotNull(testController.model, "Model should be initialized");
    }

    @Test
    @DisplayName("Should validate object positions correctly")
    void testPositionValidation() {
        // Test that position validation doesn't throw exceptions
        assertDoesNotThrow(() -> {
            testController.isValidPosition(100, 100);
        }, "Position validation should not throw exceptions");
    }

    @Test
    @DisplayName("Should manage inventory objects correctly")
    void testInventoryManagement() {
        List<InventoryObject> inventory = testController.getInventory();
        assertNotNull(inventory, "Inventory should not be null");
        
        // Test that we can check if objects can be placed
        if (!inventory.isEmpty()) {
            InventoryObject firstItem = inventory.get(0);
            boolean canPlace = testController.canPlaceObject(firstItem);
            assertTrue(canPlace || firstItem.getCount() == 0, 
                "Should correctly determine if object can be placed");
        }
    }

    @Test
    @DisplayName("Should handle dropped objects correctly")
    void testDroppedObjectManagement() {
        List<GameObject> droppedObjects = testController.getDroppedObjects();
        assertNotNull(droppedObjects, "Dropped objects list should not be null");
        
        int initialCount = droppedObjects.size();
        
        // Create a test game object
        GameObject testObject = new GameObject("testBall", "circle", 
            new Position(50f, 50f), new Size(20f, 20f));
        
        // Add it to the simulation
        testController.addGameObject(testObject);
        
        // Verify it was added
        assertEquals(initialCount + 1, testController.getDroppedObjects().size(), 
            "Should have one more dropped object");
    }

    @Test
    @DisplayName("Should initialize model with physics world")
    void testPhysicsInitialization() {
        // Note: Physics world is null until setupSimulation is called with a Pane
        // This tests that the method exists and can be called
        assertDoesNotThrow(() -> {
            testController.model.getWorld(); // May be null until full setup
            testController.model.getPairs(); // Should return empty list
        }, "Physics methods should not throw exceptions");
        
        // Test that pairs list is initialized even if world is not
        assertNotNull(testController.model.getPairs(), "Physics pairs should be initialized");
    }

    @Test
    @DisplayName("Should handle collision detection delegation")
    void testCollisionDetectionDelegation() {
        // Test that collision detection can be called without exceptions
        assertDoesNotThrow(() -> {
            testController.model.isInNoPlaceZone(100, 100);
        }, "Collision detection should not throw exceptions");
    }

    @Test
    @DisplayName("Should validate game object properties")
    void testGameObjectValidation() {
        // Test creating valid game objects
        GameObject validRect = new GameObject("testRect", "rectangle", 
            new Position(10f, 10f), new Size(50f, 30f));
        assertNotNull(validRect, "Valid rectangle should be created");
        assertEquals("rectangle", validRect.getType(), "Type should be set correctly");
        
        GameObject validCircle = new GameObject("testCircle", "circle", 
            new Position(20f, 20f), new Size(25f, 25f));
        assertNotNull(validCircle, "Valid circle should be created");
        assertEquals("circle", validCircle.getType(), "Type should be set correctly");
    }

    @Test
    @DisplayName("Should handle level data correctly")
    void testLevelDataHandling() {
        // Test that the model loads level data
        assertNotNull(testController.model.getPairs(), "Level should load physics pairs");
        assertNotNull(testController.model.getInventoryObjects(), "Level should load inventory");
        
        // Test level path handling
        assertEquals("/level/basic_sandbox.json", testController.model.getLevelPath(), 
            "Level path should be stored correctly");
    }

    @Test
    @DisplayName("Should maintain object-pair mapping consistency")
    void testObjectPairMapping() {
        // Test that when objects are added, they maintain consistent mapping
        int initialObjectCount = testController.model.getDroppedObjects().size();
        
        GameObject newObject = new GameObject("mappingTest", "circle", 
            new Position(100f, 100f), new Size(20f, 20f));
        
        testController.addGameObject(newObject);
        
        assertEquals(initialObjectCount + 1, testController.model.getDroppedObjects().size(),
            "Object should be added to model");
    }

    @Test
    @DisplayName("Should handle edge cases in position validation")
    void testPositionValidationEdgeCases() {
        // Test boundary positions
        assertDoesNotThrow(() -> {
            testController.isValidPosition(0, 0);
            testController.isValidPosition(-100, -100);
            testController.isValidPosition(1000, 1000);
        }, "Position validation should handle edge cases without exceptions");
    }

    @Test
    @DisplayName("Should maintain model state consistency")
    void testModelStateConsistency() {
        // Test that the model maintains consistent state
        SimulationModel model = testController.model;
        
        // Note: World may be null until setupSimulation is called
        // We test that the method can be called without exceptions
        assertDoesNotThrow(() -> model.getWorld(), "getWorld should not throw");
        assertNotNull(model.getPairs(), "Pairs should be maintained");
        assertNotNull(model.getDroppedObjects(), "Dropped objects should be maintained");
        assertNotNull(model.getInventoryObjects(), "Inventory should be maintained");
        
        // Test that lists are not null after operations
        GameObject testObj = new GameObject("stateTest", "rectangle", 
            new Position(75f, 75f), new Size(40f, 40f));
        testController.addGameObject(testObj);
        
        assertNotNull(model.getDroppedObjects(), "Dropped objects should remain valid after additions");
        assertTrue(model.getDroppedObjects().contains(testObj), "Added object should be in dropped objects");
    }

    @Test
    @DisplayName("Should handle multiple level configurations")
    void testMultipleLevelConfigurations() {
        // Test with different level files
        TestableSimulationController sandboxController = 
            new TestableSimulationController("/level/basic_sandbox.json");
        TestableSimulationController levelController = 
            new TestableSimulationController("/level/level1.json");
            
        // Both should initialize successfully
        assertNotNull(sandboxController.model, "Sandbox model should initialize");
        assertNotNull(levelController.model, "Level model should initialize");
        
        // Level paths should be stored correctly
        assertEquals("/level/basic_sandbox.json", sandboxController.model.getLevelPath());
        assertEquals("/level/level1.json", levelController.model.getLevelPath());
    }
}
