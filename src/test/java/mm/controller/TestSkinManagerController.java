package mm.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import mm.model.GameObject;
import mm.model.InventoryObject;
import mm.model.Size;
import mm.model.Position;
import mm.model.Physics;

/**
 * Comprehensive unit tests for SkinManagerController.
 * Tests singleton behavior, skin selection, and sprite updating functionality
 * without JavaFX dependencies.
 */
@DisplayName("SkinManagerController Tests")
public class TestSkinManagerController {

    private SkinManagerController skinManager;
    
    // Constants to avoid duplicate literals
    private static final String DEFAULT_SKIN = "Default";
    private static final String LEGACY_SKIN = "Legacy";
    private static final String RECTANGLE_TYPE = "rectangle";
    private static final String CIRCLE_TYPE = "circle";
    private static final String DYNAMIC_PHYSICS = "dynamic";

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton instance before each test using reflection
        resetSingletonInstance();
        skinManager = SkinManagerController.getInstance();
    }

    /**
     * Reset the singleton instance using reflection to ensure clean test state
     */
    private void resetSingletonInstance() throws Exception {
        Field instanceField = SkinManagerController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Nested
    @DisplayName("Singleton Pattern Tests")
    /**
     * Tests for verifying singleton pattern implementation and behavior.
     */
    class SingletonTests {

        @Test
        @DisplayName("Should return same instance on multiple calls")
        void testSingletonInstance() {
            SkinManagerController instance1 = SkinManagerController.getInstance();
            SkinManagerController instance2 = SkinManagerController.getInstance();
            
            assertSame(instance1, instance2, "getInstance should return the same instance");
        }

        @Test
        @DisplayName("Should initialize with default skin")
        void testDefaultSkinInitialization() {
            assertEquals(DEFAULT_SKIN, skinManager.getSelectedSkin(), 
                "Should initialize with Default skin");
            assertFalse(skinManager.isLegacySkin(), 
                "Should not be legacy skin initially");
        }
    }

    @Nested
    @DisplayName("Skin Selection Tests")
    /**
     * Tests for verifying skin selection functionality and validation.
     */
    class SkinSelectionTests {

        @Test
        @DisplayName("Should accept valid Default skin selection")
        void testSetValidDefaultSkin() {
            skinManager.setSelectedSkin(DEFAULT_SKIN);
            
            assertEquals(DEFAULT_SKIN, skinManager.getSelectedSkin(), 
                "Should set Default skin");
            assertFalse(skinManager.isLegacySkin(), 
                "Should not be legacy skin");
        }

        @Test
        @DisplayName("Should accept valid Legacy skin selection")
        void testSetValidLegacySkin() {
            skinManager.setSelectedSkin(LEGACY_SKIN);
            
            assertEquals(LEGACY_SKIN, skinManager.getSelectedSkin(), 
                "Should set Legacy skin");
            assertTrue(skinManager.isLegacySkin(), 
                "Should be legacy skin");
        }

        @Test
        @DisplayName("Should reject invalid skin selection")
        void testSetInvalidSkin() {
            String originalSkin = skinManager.getSelectedSkin();
            
            skinManager.setSelectedSkin("InvalidSkin");
            
            assertEquals(originalSkin, skinManager.getSelectedSkin(), 
                "Should not change skin for invalid input");
        }

        @Test
        @DisplayName("Should reject null skin selection")
        void testSetNullSkin() {
            String originalSkin = skinManager.getSelectedSkin();
            
            skinManager.setSelectedSkin(null);
            
            assertEquals(originalSkin, skinManager.getSelectedSkin(), 
                "Should not change skin for null input");
        }

        @Test
        @DisplayName("Should handle empty string skin selection")
        void testSetEmptySkin() {
            String originalSkin = skinManager.getSelectedSkin();
            
            skinManager.setSelectedSkin("");
            
            assertEquals(originalSkin, skinManager.getSelectedSkin(), 
                "Should not change skin for empty string");
        }

        @Test
        @DisplayName("Should be case sensitive for skin names")
        void testCaseSensitiveSkinNames() {
            String originalSkin = skinManager.getSelectedSkin();
            
            skinManager.setSelectedSkin("default");  // lowercase
            assertEquals(originalSkin, skinManager.getSelectedSkin(), 
                "Should be case sensitive - 'default' should be rejected");
            
            skinManager.setSelectedSkin("LEGACY");   // uppercase
            assertEquals(originalSkin, skinManager.getSelectedSkin(), 
                "Should be case sensitive - 'LEGACY' should be rejected");
        }
    }

    @Nested
    @DisplayName("Legacy Skin Detection Tests")
    /**
     * Tests for verifying legacy skin detection and state management.
     */
    class LegacySkinDetectionTests {

        @Test
        @DisplayName("Should return false for Default skin")
        void testIsLegacySkinForDefault() {
            skinManager.setSelectedSkin(DEFAULT_SKIN);
            assertFalse(skinManager.isLegacySkin(), 
                "Default skin should not be legacy");
        }

        @Test
        @DisplayName("Should return true for Legacy skin")
        void testIsLegacySkinForLegacy() {
            skinManager.setSelectedSkin(LEGACY_SKIN);
            assertTrue(skinManager.isLegacySkin(), 
                "Legacy skin should be legacy");
        }

        @Test
        @DisplayName("Should maintain legacy state across calls")
        void testLegacyStateConsistency() {
            skinManager.setSelectedSkin(LEGACY_SKIN);
            
            assertTrue(skinManager.isLegacySkin(), 
                "Should be legacy after setting");
            assertTrue(skinManager.isLegacySkin(), 
                "Should remain legacy on second call");
            
            skinManager.setSelectedSkin(DEFAULT_SKIN);
            assertFalse(skinManager.isLegacySkin(), 
                "Should not be legacy after changing to Default");
        }
    }

    @Nested
    @DisplayName("Inventory Objects Sprite Update Tests")
    /**
     * Tests for verifying inventory object sprite updating functionality.
     */
    class InventoryObjectsSpriteUpdateTests {

        private List<InventoryObject> createTestInventoryObjects() {
            List<InventoryObject> objects = new ArrayList<>();
            
            // Objects with names in DEFAULT_SPRITES map
            objects.add(createInventoryObject("Domino", RECTANGLE_TYPE));
            objects.add(createInventoryObject("platform", RECTANGLE_TYPE));
            objects.add(createInventoryObject("tennisball", CIRCLE_TYPE));
            objects.add(createInventoryObject("ballon", CIRCLE_TYPE));
            
            // Objects not in DEFAULT_SPRITES map
            objects.add(createInventoryObject("customObject", RECTANGLE_TYPE));
            objects.add(createInventoryObject("specialItem", CIRCLE_TYPE));
            
            return objects;
        }

        private InventoryObject createInventoryObject(String name, String type) {
            InventoryObject obj = new InventoryObject(name, type, new Size(30f, 30f));
            obj.setPhysics(new Physics(1.0f, 0.5f, 0.3f, DYNAMIC_PHYSICS));
            obj.setColour("BLACK");
            return obj;
        }

        @Test
        @DisplayName("Should update sprites for objects with default mappings - Default skin")
        void testUpdateInventorySpritesDefaultSkin() {
            skinManager.setSelectedSkin(DEFAULT_SKIN);
            List<InventoryObject> objects = createTestInventoryObjects();
            
            skinManager.updateInventorySpritesForSkin(objects);
            
            // Check objects with default sprite mappings
            assertEquals("/objectSkins/Default/madDomino.png", 
                objects.get(0).getSprite(), "Domino should have correct Default sprite");
            assertEquals("/objectSkins/Default/madPlatform.png", 
                objects.get(1).getSprite(), "Platform should have correct Default sprite");
            assertEquals("/objectSkins/Default/madTennis.png", 
                objects.get(2).getSprite(), "Tennisball should have correct Default sprite");
            assertEquals("/objectSkins/Default/madBalloon.png", 
                objects.get(3).getSprite(), "Ballon should have correct Default sprite");
        }

        @Test
        @DisplayName("Should update sprites for objects with default mappings - Legacy skin")
        void testUpdateInventorySpritesLegacySkin() {
            skinManager.setSelectedSkin(LEGACY_SKIN);
            List<InventoryObject> objects = createTestInventoryObjects();
            
            skinManager.updateInventorySpritesForSkin(objects);
            
            // Check objects with default sprite mappings
            assertEquals("/objectSkins/Legacy/madDomino.png", 
                objects.get(0).getSprite(), "Domino should have correct Legacy sprite");
            assertEquals("/objectSkins/Legacy/madPlatform.png", 
                objects.get(1).getSprite(), "Platform should have correct Legacy sprite");
            assertEquals("/objectSkins/Legacy/madTennis.png", 
                objects.get(2).getSprite(), "Tennisball should have correct Legacy sprite");
            assertEquals("/objectSkins/Legacy/madBalloon.png", 
                objects.get(3).getSprite(), "Ballon should have correct Legacy sprite");
        }

        @Test
        @DisplayName("Should handle objects without default sprite mappings with null sprites")
        void testUpdateInventorySpritesNoDefaultMappingNullSprite() {
            List<InventoryObject> objects = createTestInventoryObjects();
            
            // Ensure custom objects have null sprites initially
            objects.get(4).setSprite(null);  // customObject
            objects.get(5).setSprite(null);  // specialItem
            
            skinManager.updateInventorySpritesForSkin(objects);
            
            // Objects without default mappings and null sprites should remain null
            assertNull(objects.get(4).getSprite(), 
                "Custom object with null sprite should remain null");
            assertNull(objects.get(5).getSprite(), 
                "Special item with null sprite should remain null");
        }

        @Test
        @DisplayName("Should handle objects with existing sprite paths")
        void testUpdateInventorySpritesWithExistingPaths() {
            List<InventoryObject> objects = createTestInventoryObjects();
            
            // Set custom sprites for objects not in default map
            objects.get(4).setSprite("/objectSkins/Default/customSprite.png");
            objects.get(5).setSprite("simpleSprite.png");
            
            skinManager.setSelectedSkin(LEGACY_SKIN);
            skinManager.updateInventorySpritesForSkin(objects);
            
            // Should replace skin folder for full paths
            assertEquals("/objectSkins/Legacy/customSprite.png", 
                objects.get(4).getSprite(), "Should replace skin folder in full path");
            
            // Should prepend skin folder for simple filenames
            assertEquals("/objectSkins/Legacy/simpleSprite.png", 
                objects.get(5).getSprite(), "Should prepend skin folder to simple filename");
        }

        @Test
        @DisplayName("Should handle empty sprite strings")
        void testUpdateInventorySpritesWithEmptySprites() {
            List<InventoryObject> objects = createTestInventoryObjects();
            
            objects.get(4).setSprite("");
            objects.get(5).setSprite("   ");  // whitespace only
            
            skinManager.updateInventorySpritesForSkin(objects);
            
            // Empty sprites should remain unchanged
            assertEquals("", objects.get(4).getSprite(), 
                "Empty sprite should remain empty");
            assertEquals("   ", objects.get(5).getSprite(), 
                "Whitespace sprite should remain unchanged");
        }

        @Test
        @DisplayName("Should handle empty inventory list")
        void testUpdateInventorySpritesEmptyList() {
            List<InventoryObject> emptyList = new ArrayList<>();
            
            // Should not throw exception
            assertDoesNotThrow(() -> {
                skinManager.updateInventorySpritesForSkin(emptyList);
            }, "Should handle empty list without throwing exception");
        }

        @Test
        @DisplayName("Should handle null inventory list")
        void testUpdateInventorySpritesNullList() {
            // Should not throw NullPointerException
            assertThrows(NullPointerException.class, () -> {
                skinManager.updateInventorySpritesForSkin(null);
            }, "Should throw NullPointerException for null list");
        }
    }

    @Nested
    @DisplayName("Game Objects Sprite Update Tests")
    /**
     * Tests for verifying game object sprite updating functionality.
     */
    class GameObjectsSpriteUpdateTests {

        private List<GameObject> createTestGameObjects() {
            List<GameObject> objects = new ArrayList<>();
            
            // Objects with names in DEFAULT_SPRITES map
            objects.add(createGameObject("Domino", RECTANGLE_TYPE));
            objects.add(createGameObject("log", RECTANGLE_TYPE));
            objects.add(createGameObject("bowlingball", CIRCLE_TYPE));
            objects.add(createGameObject("ball", CIRCLE_TYPE));
            
            // Objects not in DEFAULT_SPRITES map
            objects.add(createGameObject("customGameObject", RECTANGLE_TYPE));
            objects.add(createGameObject("specialGameItem", CIRCLE_TYPE));
            
            return objects;
        }

        private GameObject createGameObject(String name, String type) {
            GameObject obj = new GameObject(name, type, new Position(10f, 20f), new Size(30f, 30f));
            obj.setPhysics(new Physics(1.0f, 0.5f, 0.3f, DYNAMIC_PHYSICS));
            obj.setColour("BLACK");
            return obj;
        }

        @Test
        @DisplayName("Should update sprites for game objects with default mappings - Default skin")
        void testUpdateGameObjectSpritesDefaultSkin() {
            skinManager.setSelectedSkin(DEFAULT_SKIN);
            List<GameObject> objects = createTestGameObjects();
            
            skinManager.updateGameObjectSpritesForSkin(objects);
            
            // Check objects with default sprite mappings
            assertEquals("/objectSkins/Default/madDomino.png", 
                objects.get(0).getSprite(), "Domino should have correct Default sprite");
            assertEquals("/objectSkins/Default/madLog.png", 
                objects.get(1).getSprite(), "Log should have correct Default sprite");
            assertEquals("/objectSkins/Default/madBowling.png", 
                objects.get(2).getSprite(), "Bowlingball should have correct Default sprite");
            assertEquals("/objectSkins/Default/madBall.png", 
                objects.get(3).getSprite(), "Ball should have correct Default sprite");
        }

        @Test
        @DisplayName("Should update sprites for game objects with default mappings - Legacy skin")
        void testUpdateGameObjectSpritesLegacySkin() {
            skinManager.setSelectedSkin(LEGACY_SKIN);
            List<GameObject> objects = createTestGameObjects();
            
            skinManager.updateGameObjectSpritesForSkin(objects);
            
            // Check objects with default sprite mappings
            assertEquals("/objectSkins/Legacy/madDomino.png", 
                objects.get(0).getSprite(), "Domino should have correct Legacy sprite");
            assertEquals("/objectSkins/Legacy/madLog.png", 
                objects.get(1).getSprite(), "Log should have correct Legacy sprite");
            assertEquals("/objectSkins/Legacy/madBowling.png", 
                objects.get(2).getSprite(), "Bowlingball should have correct Legacy sprite");
            assertEquals("/objectSkins/Legacy/madBall.png", 
                objects.get(3).getSprite(), "Ball should have correct Legacy sprite");
        }

        @Test
        @DisplayName("Should handle game objects with existing sprite paths")
        void testUpdateGameObjectSpritesWithExistingPaths() {
            List<GameObject> objects = createTestGameObjects();
            
            // Set custom sprites for objects not in default map
            objects.get(4).setSprite("/objectSkins/Default/customGameSprite.png");
            objects.get(5).setSprite("simpleGameSprite.png");
            
            skinManager.setSelectedSkin(LEGACY_SKIN);
            skinManager.updateGameObjectSpritesForSkin(objects);
            
            // Should replace skin folder for full paths
            assertEquals("/objectSkins/Legacy/customGameSprite.png", 
                objects.get(4).getSprite(), "Should replace skin folder in full path");
            
            // Should prepend skin folder for simple filenames
            assertEquals("/objectSkins/Legacy/simpleGameSprite.png", 
                objects.get(5).getSprite(), "Should prepend skin folder to simple filename");
        }

        @Test
        @DisplayName("Should handle empty game object list")
        void testUpdateGameObjectSpritesEmptyList() {
            List<GameObject> emptyList = new ArrayList<>();
            
            // Should not throw exception
            assertDoesNotThrow(() -> {
                skinManager.updateGameObjectSpritesForSkin(emptyList);
            }, "Should handle empty list without throwing exception");
        }

        @Test
        @DisplayName("Should handle null game object list")
        void testUpdateGameObjectSpritesNullList() {
            // Should not throw NullPointerException
            assertThrows(NullPointerException.class, () -> {
                skinManager.updateGameObjectSpritesForSkin(null);
            }, "Should throw NullPointerException for null list");
        }
    }

    @Nested
    @DisplayName("Mixed Object Type Tests")
    /**
     * Tests for verifying behavior with mixed inventory and game object types.
     */
    class MixedObjectTypeTests {

        @Test
        @DisplayName("Should handle mixed object types correctly")
        void testMixedObjectTypeUpdates() {
            List<InventoryObject> inventoryObjects = Arrays.asList(
                createInventoryObject("Domino", RECTANGLE_TYPE),
                createInventoryObject("customInv", CIRCLE_TYPE)
            );
            
            List<GameObject> gameObjects = Arrays.asList(
                createGameObject("ball", CIRCLE_TYPE),
                createGameObject("customGame", RECTANGLE_TYPE)
            );
            
            // Set existing sprites for custom objects
            inventoryObjects.get(1).setSprite("/objectSkins/Default/custom.png");
            gameObjects.get(1).setSprite("another.png");
            
            skinManager.setSelectedSkin(LEGACY_SKIN);
            
            // Update both types
            skinManager.updateInventorySpritesForSkin(inventoryObjects);
            skinManager.updateGameObjectSpritesForSkin(gameObjects);
            
            // Verify results
            assertEquals("/objectSkins/Legacy/madDomino.png", 
                inventoryObjects.get(0).getSprite(), "Inventory Domino should be updated");
            assertEquals("/objectSkins/Legacy/custom.png", 
                inventoryObjects.get(1).getSprite(), "Inventory custom should replace path");
            
            assertEquals("/objectSkins/Legacy/madBall.png", 
                gameObjects.get(0).getSprite(), "Game ball should be updated");
            assertEquals("/objectSkins/Legacy/another.png", 
                gameObjects.get(1).getSprite(), "Game custom should prepend path");
        }

        private InventoryObject createInventoryObject(String name, String type) {
            InventoryObject obj = new InventoryObject(name, type, new Size(25f, 25f));
            obj.setPhysics(new Physics(1.0f, 0.5f, 0.3f, DYNAMIC_PHYSICS));
            obj.setColour("RED");
            return obj;
        }

        private GameObject createGameObject(String name, String type) {
            GameObject obj = new GameObject(name, type, new Position(5f, 15f), new Size(25f, 25f));
            obj.setPhysics(new Physics(1.0f, 0.5f, 0.3f, DYNAMIC_PHYSICS));
            obj.setColour("BLUE");
            return obj;
        }
    }

    @Nested
    @DisplayName("Edge Cases and Robustness Tests")
    /**
     * Tests for verifying behavior under edge cases and robustness scenarios.
     */
    class EdgeCasesAndRobustnessTests {

        @Test
        @DisplayName("Should handle objects with box1 mapping")
        void testBox1Mapping() {
            InventoryObject box1Obj = new InventoryObject("box1", RECTANGLE_TYPE, new Size(20f, 20f));
            List<InventoryObject> objects = Arrays.asList(box1Obj);
            
            skinManager.setSelectedSkin(DEFAULT_SKIN);
            skinManager.updateInventorySpritesForSkin(objects);
            
            assertEquals("/objectSkins/Default/madBox.png", 
                box1Obj.getSprite(), "box1 should map to madBox.png");
        }

        @Test
        @DisplayName("Should handle multiple skin switches")
        void testMultipleSkinSwitches() {
            InventoryObject obj = new InventoryObject("platform", RECTANGLE_TYPE, new Size(40f, 10f));
            List<InventoryObject> objects = Arrays.asList(obj);
            
            // Default -> Legacy -> Default
            skinManager.setSelectedSkin(DEFAULT_SKIN);
            skinManager.updateInventorySpritesForSkin(objects);
            assertEquals("/objectSkins/Default/madPlatform.png", obj.getSprite());
            
            skinManager.setSelectedSkin(LEGACY_SKIN);
            skinManager.updateInventorySpritesForSkin(objects);
            assertEquals("/objectSkins/Legacy/madPlatform.png", obj.getSprite());
            
            skinManager.setSelectedSkin(DEFAULT_SKIN);
            skinManager.updateInventorySpritesForSkin(objects);
            assertEquals("/objectSkins/Default/madPlatform.png", obj.getSprite());
        }

        @Test
        @DisplayName("Should handle sprite path with multiple skin folder occurrences")
        void testMultipleSkinFolderOccurrences() {
            InventoryObject obj = new InventoryObject("custom", RECTANGLE_TYPE, new Size(20f, 20f));
            obj.setSprite("/objectSkins/Default/subfolder/objectSkins/Default/sprite.png");
            List<InventoryObject> objects = Arrays.asList(obj);
            
            skinManager.setSelectedSkin(LEGACY_SKIN);
            skinManager.updateInventorySpritesForSkin(objects);
            
            // Should replace the first occurrence
            assertEquals("/objectSkins/Legacy/subfolder/objectSkins/Default/sprite.png", 
                obj.getSprite(), "Should replace first skin folder occurrence");
        }

        @Test
        @DisplayName("Should preserve singleton across multiple resets")
        void testSingletonPersistence() throws Exception {
            SkinManagerController first = SkinManagerController.getInstance();
            first.setSelectedSkin(LEGACY_SKIN);
            
            SkinManagerController second = SkinManagerController.getInstance();
            assertEquals(LEGACY_SKIN, second.getSelectedSkin(), 
                "Singleton should preserve state");
            
            // Reset and verify new instance has default state
            resetSingletonInstance();
            SkinManagerController third = SkinManagerController.getInstance();
            assertEquals(DEFAULT_SKIN, third.getSelectedSkin(), 
                "New singleton should have default state");
        }

        @Test
        @DisplayName("Should handle concurrent access to singleton")
        void testConcurrentSingletonAccess() {
            // Simple test for thread safety of getInstance
            SkinManagerController[] instances = new SkinManagerController[10];
            
            Thread[] threads = new Thread[10];
            for (int i = 0; i < 10; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    instances[index] = SkinManagerController.getInstance();
                });
            }
            
            for (Thread thread : threads) {
                thread.start();
            }
            
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    fail("Thread interrupted");
                }
            }
            
            // All instances should be the same
            for (int i = 1; i < instances.length; i++) {
                assertSame(instances[0], instances[i], 
                    "All instances should be the same object");
            }
        }
    }
}
