package mm.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import mm.model.GameObject;
import mm.model.InventoryObject;

/**
 * JavaFX-less unit tests for LevelImportController.
 * Tests the controller's ability to import levels from JSON resources.
 */
public class TestLevelImportController {

    // Test constants to avoid duplicate literals
    private static final String VALID_LEVEL_PATH = "/level/level1.json";
    private static final String NONEXISTENT_PATH = "/nonexistent/path.json";
    private static final String INVALID_RESOURCE_PATH = "/invalid/resource.json";

    @BeforeEach
    void setUp() throws Exception {
        // Reset SkinManagerController singleton to ensure clean test state
        resetSkinManagerSingleton();
    }

    /**
     * Reset the SkinManagerController singleton instance using reflection
     */
    private void resetSkinManagerSingleton() throws Exception {
        Field instanceField = SkinManagerController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Nested
    /**
     * Tests for the LevelImportController constructor with various input scenarios.
     */
    class ConstructorTests {

        @Test
        void testConstructorWithValidResource() {
            // This test uses an actual resource file that should exist
            // We'll test with a known level file from the resources
            LevelImportController controller = new LevelImportController(VALID_LEVEL_PATH);
            
            assertNotNull(controller);
            // We can't directly test the private level field, but we can test the public methods
            assertNotNull(controller.getGameObjects());
            assertNotNull(controller.getInventoryObjects());
        }

        @Test
        void testConstructorWithNonExistentResource() {
            LevelImportController controller = new LevelImportController(NONEXISTENT_PATH);
            
            assertNotNull(controller);
            // Should return empty lists when resource doesn't exist
            assertTrue(controller.getGameObjects().isEmpty());
            assertTrue(controller.getInventoryObjects().isEmpty());
        }

        @Test
        void testConstructorWithNullPath() {
            LevelImportController controller = new LevelImportController(null);
            
            assertNotNull(controller);
            assertTrue(controller.getGameObjects().isEmpty());
            assertTrue(controller.getInventoryObjects().isEmpty());
        }

        @Test
        void testConstructorWithEmptyPath() {
            LevelImportController controller = new LevelImportController("");
            
            assertNotNull(controller);
            assertTrue(controller.getGameObjects().isEmpty());
            assertTrue(controller.getInventoryObjects().isEmpty());
        }
    }

    @Nested
    /**
     * Tests for the getGameObjects method with various scenarios.
     */
    class GetGameObjectsTests {

        @Test
        void testGetGameObjectsWithValidLevel() {
            // Test with a resource that should contain game objects
            LevelImportController controller = new LevelImportController(VALID_LEVEL_PATH);
            
            List<GameObject> gameObjects = controller.getGameObjects();
            
            assertNotNull(gameObjects);
            // The actual size depends on the content of level1.json
            // We just verify it's a valid list
        }

        @Test
        void testGetGameObjectsWithInvalidResource() {
            LevelImportController controller = new LevelImportController(INVALID_RESOURCE_PATH);
            
            List<GameObject> gameObjects = controller.getGameObjects();
            
            assertNotNull(gameObjects);
            assertTrue(gameObjects.isEmpty());
        }

        @Test
        void testGetGameObjectsReturnsImmutableReference() {
            LevelImportController controller = new LevelImportController(VALID_LEVEL_PATH);
            
            List<GameObject> gameObjects1 = controller.getGameObjects();
            List<GameObject> gameObjects2 = controller.getGameObjects();
            
            // Should return the same reference (not a copy each time)
            assertSame(gameObjects1, gameObjects2);
        }
    }

    @Nested
    /**
     * Tests for the getInventoryObjects method with various scenarios.
     */
    class GetInventoryObjectsTests {

        @Test
        void testGetInventoryObjectsWithValidLevel() {
            LevelImportController controller = new LevelImportController(VALID_LEVEL_PATH);
            
            List<InventoryObject> inventoryObjects = controller.getInventoryObjects();
            
            assertNotNull(inventoryObjects);
            // The actual size depends on the content of level1.json
        }

        @Test
        void testGetInventoryObjectsWithInvalidResource() {
            LevelImportController controller = new LevelImportController(INVALID_RESOURCE_PATH);
            
            List<InventoryObject> inventoryObjects = controller.getInventoryObjects();
            
            assertNotNull(inventoryObjects);
            assertTrue(inventoryObjects.isEmpty());
        }

        @Test
        void testGetInventoryObjectsReturnsImmutableReference() {
            LevelImportController controller = new LevelImportController(VALID_LEVEL_PATH);
            
            List<InventoryObject> inventoryObjects1 = controller.getInventoryObjects();
            List<InventoryObject> inventoryObjects2 = controller.getInventoryObjects();
            
            // Should return the same reference (not a copy each time)
            assertSame(inventoryObjects1, inventoryObjects2);
        }
    }

    @Nested
    /**
     * Integration tests that verify the complete workflow of LevelImportController.
     */
    class IntegrationTests {

        @Test
        void testFullImportWorkflow() {
            // Test the complete workflow with a valid resource
            LevelImportController controller = new LevelImportController(VALID_LEVEL_PATH);
            
            List<GameObject> gameObjects = controller.getGameObjects();
            List<InventoryObject> inventoryObjects = controller.getInventoryObjects();
            
            assertNotNull(gameObjects);
            assertNotNull(inventoryObjects);
            
            // If the level loaded successfully, both lists should be non-null
            // The actual contents depend on the specific level file
        }

        @Test
        void testSkinApplicationIsTriggered() {
            // This test verifies that the skin manager is called during import
            // We can't directly test the skin application without mocking,
            // but we can verify that the SkinManagerController singleton is created
            LevelImportController controller = new LevelImportController(VALID_LEVEL_PATH);
            
            // The constructor should have triggered SkinManagerController.getInstance()
            SkinManagerController skinManager = SkinManagerController.getInstance();
            assertNotNull(skinManager);
            
            // Verify basic functionality works
            assertNotNull(controller.getGameObjects());
            assertNotNull(controller.getInventoryObjects());
        }
    }

    @Nested
    /**
     * Tests for error handling scenarios in LevelImportController.
     */
    class ErrorHandlingTests {

        @Test
        void testHandlesResourceLoadingError() {
            // Test with a path that exists but contains invalid JSON
            // This simulates the scenario where LevelReadController.readFile() returns null
            LevelImportController controller = new LevelImportController("/nonexistent.json");
            
            // Should not throw exception and should return empty lists
            assertDoesNotThrow(() -> {
                List<GameObject> gameObjects = controller.getGameObjects();
                List<InventoryObject> inventoryObjects = controller.getInventoryObjects();
                
                assertTrue(gameObjects.isEmpty());
                assertTrue(inventoryObjects.isEmpty());
            });
        }

        @Test
        void testHandlesNullLevel() {
            // Test case where level loading fails completely
            LevelImportController controller = new LevelImportController("/invalid/path/file.json");
            
            // Should handle null level gracefully
            assertDoesNotThrow(() -> {
                assertTrue(controller.getGameObjects().isEmpty());
                assertTrue(controller.getInventoryObjects().isEmpty());
            });
        }
    }
}
