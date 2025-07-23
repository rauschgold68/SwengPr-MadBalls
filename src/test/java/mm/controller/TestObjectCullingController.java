package mm.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.dynamics.Body;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mm.model.GameObject;
import mm.model.PhysicsVisualPair;
import mm.model.Position;
import mm.model.SimulationBounds;
import mm.model.SimulationModel;
import mm.model.Size;

/**
 * Test class for ObjectCullingController.
 * Tests all business logic without requiring JavaFX runtime.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ObjectCullingController Tests")
class TestObjectCullingController {

    // Test constants to avoid duplicate literals
    private static final String TEST_OBJECT = "testObject";
    private static final String BALLOON_TYPE = "ballon";
    private static final String RECTANGLE_TYPE = "rectangle";
    private static final String CIRCLE_TYPE = "circle";
    private static final String OBJECT_1 = "object1";
    private static final String OBJECT_2 = "object2";
    private static final double BOUNDS_WIDTH = 800.0;
    private static final double BOUNDS_HEIGHT = 600.0;

    @Mock
    private SimulationModel mockModel;
    
    @Mock
    private SimulationBounds mockBounds;
    
    @Mock
    private Body mockBody;
    
    private ObjectCullingController controller;
    private List<GameObject> droppedObjects;

    @BeforeEach
    void setUp() {
        controller = new ObjectCullingController(mockModel);
        droppedObjects = new ArrayList<>();
        // Use lenient stubbing to avoid unnecessary stubbing exceptions
        lenient().when(mockModel.getDroppedObjects()).thenReturn(droppedObjects);
    }

    @Nested
    @DisplayName("Constructor Tests")
    /**
     * Tests for ObjectCullingController constructor functionality.
     */
    class ConstructorTests {
        
        @Test
        @DisplayName("Should initialize with empty removal lists")
        void shouldInitializeWithEmptyRemovalLists() {
            ObjectCullingController newController = new ObjectCullingController(mockModel);
            
            assertTrue(newController.getPairsToRemove().isEmpty());
            assertTrue(newController.getObjectsToRemove().isEmpty());
            assertTrue(newController.getVisualsToRemove().isEmpty());
        }
    }

    @Nested
    @DisplayName("Culling Decision Tests")
    /**
     * Tests for the shouldCullObject method decision logic.
     */
    class CullingDecisionTests {
        
        @Test
        @DisplayName("Should cull object when X position is too far left")
        void shouldCullObjectWhenXTooFarLeft() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            boolean result = controller.shouldCullObject(-150.0, 300.0, mockBounds, TEST_OBJECT);
            
            assertTrue(result, "Object should be culled when X is too far left");
        }
        
        @Test
        @DisplayName("Should cull object when X position is too far right")
        void shouldCullObjectWhenXTooFarRight() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            boolean result = controller.shouldCullObject(950.0, 300.0, mockBounds, TEST_OBJECT);
            
            assertTrue(result, "Object should be culled when X is too far right");
        }
        
        @Test
        @DisplayName("Should cull object when Y position is too high")
        void shouldCullObjectWhenYTooHigh() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            boolean result = controller.shouldCullObject(400.0, -150.0, mockBounds, TEST_OBJECT);
            
            assertTrue(result, "Object should be culled when Y is too high");
        }
        
        @Test
        @DisplayName("Should cull object when Y position is too low")
        void shouldCullObjectWhenYTooLow() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            boolean result = controller.shouldCullObject(400.0, 750.0, mockBounds, TEST_OBJECT);
            
            assertTrue(result, "Object should be culled when Y is too low");
        }
        
        @Test
        @DisplayName("Should NOT cull object when position is within bounds")
        void shouldNotCullObjectWhenWithinBounds() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            boolean result = controller.shouldCullObject(400.0, 300.0, mockBounds, TEST_OBJECT);
            
            assertFalse(result, "Object should NOT be culled when within bounds");
        }
        
        @Test
        @DisplayName("Should handle edge case at exact boundary")
        void shouldHandleEdgeCaseAtBoundary() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            // At boundary + margin + 1 (901px for regular objects should be culled)
            boolean result = controller.shouldCullObject(901.0, 300.0, mockBounds, TEST_OBJECT);
            
            assertTrue(result, "Object should be culled beyond boundary + margin");
        }
    }

    @Nested
    @DisplayName("Balloon-Specific Culling Tests")
    /**
     * Tests for balloon-specific culling behavior with different margins and height restrictions.
     */
    class BalloonCullingTests {
        
        @Test
        @DisplayName("Balloon should have smaller margin than regular objects")
        void balloonShouldHaveSmallerMargin() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            // Position where regular object would be safe but balloon gets culled
            boolean regularObject = controller.shouldCullObject(-75.0, 300.0, mockBounds, TEST_OBJECT);
            boolean balloon = controller.shouldCullObject(-75.0, 300.0, mockBounds, BALLOON_TYPE);
            
            assertFalse(regularObject, "Regular object should be safe at -75px");
            assertTrue(balloon, "Balloon should be culled at -75px (outside 50px margin)");
        }
        
        @Test
        @DisplayName("Balloon should be culled when going too high")
        void balloonShouldBeCulledWhenGoingTooHigh() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            // Position within normal bounds but too high for balloon (< 10% of height = 60)
            boolean result = controller.shouldCullObject(400.0, 50.0, mockBounds, BALLOON_TYPE);
            
            assertTrue(result, "Balloon should be culled when going too high");
        }
        
        @Test
        @DisplayName("Balloon should NOT be culled at acceptable height")
        void balloonShouldNotBeCulledAtAcceptableHeight() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            // Position at acceptable height (> 10% of height = 60)
            boolean result = controller.shouldCullObject(400.0, 100.0, mockBounds, BALLOON_TYPE);
            
            assertFalse(result, "Balloon should NOT be culled at acceptable height");
        }
        
        @Test
        @DisplayName("Should handle case-insensitive balloon detection")
        void shouldHandleCaseInsensitiveBalloonDetection() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            boolean upperCase = controller.shouldCullObject(-75.0, 300.0, mockBounds, "BALLON");
            boolean mixedCase = controller.shouldCullObject(-75.0, 300.0, mockBounds, "BaLLoN");
            
            assertTrue(upperCase, "Should detect balloon in upper case");
            assertTrue(mixedCase, "Should detect balloon in mixed case");
        }
    }

    @Nested
    @DisplayName("Object Culling Operations Tests")
    /**
     * Tests for object culling operations and removal list management.
     */
    class ObjectCullingOperationsTests {
        
        @Test
        @DisplayName("Should add pair to removal list when culling")
        void shouldAddPairToRemovalListWhenCulling() {
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            
            controller.cullObject(mockPair, TEST_OBJECT);
            
            assertTrue(controller.getPairsToRemove().contains(mockPair));
            assertEquals(1, controller.getPairsToRemove().size());
        }
        
        @Test
        @DisplayName("Should find and add matching object to removal list")
        void shouldFindAndAddMatchingObjectToRemovalList() {
            // Setup a game object in the dropped objects list
            Position position = new Position(100.0f, 200.0f);
            Size size = new Size(50.0f, 50.0f);
            GameObject testObject = new GameObject(TEST_OBJECT, RECTANGLE_TYPE, position, size);
            droppedObjects.add(testObject);
            
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            
            controller.cullObject(mockPair, TEST_OBJECT);
            
            assertTrue(controller.getObjectsToRemove().contains(testObject));
            assertEquals(1, controller.getObjectsToRemove().size());
        }
        
        @Test
        @DisplayName("Should handle culling when no matching object found")
        void shouldHandleCullingWhenNoMatchingObjectFound() {
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            
            controller.cullObject(mockPair, "nonExistentObject");
            
            assertEquals(1, controller.getPairsToRemove().size());
            assertEquals(0, controller.getObjectsToRemove().size());
        }
        
        @Test
        @DisplayName("Should clear all removal lists")
        void shouldClearAllRemovalLists() {
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            controller.cullObject(mockPair, TEST_OBJECT);
            
            // Verify lists have content
            assertFalse(controller.getPairsToRemove().isEmpty());
            
            controller.clearRemovalLists();
            
            assertTrue(controller.getPairsToRemove().isEmpty());
            assertTrue(controller.getObjectsToRemove().isEmpty());
            assertTrue(controller.getVisualsToRemove().isEmpty());
        }
    }

    @Nested
    @DisplayName("Object Cache Tests")
    /**
     * Tests for object caching functionality to improve performance.
     */
    class ObjectCacheTests {
        
        @Test
        @DisplayName("Should cache found objects for faster subsequent access")
        void shouldCacheFoundObjectsForFasterAccess() {
            Position position = new Position(100.0f, 200.0f);
            Size size = new Size(50.0f, 50.0f);
            GameObject testObject = new GameObject(TEST_OBJECT, RECTANGLE_TYPE, position, size);
            droppedObjects.add(testObject);
            
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            
            // First call should search and cache
            controller.cullObject(mockPair, TEST_OBJECT);
            controller.clearRemovalLists();
            
            // Second call should use cache (verify by removing from dropped objects)
            droppedObjects.clear();
            controller.cullObject(mockPair, TEST_OBJECT);
            
            // Should still find the object because it's cached
            assertTrue(controller.getObjectsToRemove().contains(testObject));
        }
        
        @Test
        @DisplayName("Should update object cache")
        void shouldUpdateObjectCache() {
            Position position1 = new Position(100.0f, 200.0f);
            Position position2 = new Position(150.0f, 250.0f);
            Size size1 = new Size(50.0f, 50.0f);
            Size size2 = new Size(30.0f, 30.0f);
            GameObject object1 = new GameObject(OBJECT_1, RECTANGLE_TYPE, position1, size1);
            GameObject object2 = new GameObject(OBJECT_2, CIRCLE_TYPE, position2, size2);
            
            droppedObjects.add(object1);
            droppedObjects.add(object2);
            
            controller.updateObjectCache();
            
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            
            // Should find both objects after cache update
            controller.cullObject(mockPair, OBJECT_1);
            controller.cullObject(mockPair, OBJECT_2);
            
            assertEquals(2, controller.getObjectsToRemove().size());
            assertTrue(controller.getObjectsToRemove().contains(object1));
            assertTrue(controller.getObjectsToRemove().contains(object2));
        }
        
        @Test
        @DisplayName("Should clear object cache")
        void shouldClearObjectCache() {
            Position position = new Position(100.0f, 200.0f);
            Size size = new Size(50.0f, 50.0f);
            GameObject testObject = new GameObject(TEST_OBJECT, RECTANGLE_TYPE, position, size);
            droppedObjects.add(testObject);
            
            // Cache the object
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            controller.cullObject(mockPair, TEST_OBJECT);
            controller.clearRemovalLists();
            
            // Clear cache and remove from dropped objects
            controller.clearObjectCache();
            droppedObjects.clear();
            
            // Should not find the object anymore
            controller.cullObject(mockPair, TEST_OBJECT);
            assertEquals(0, controller.getObjectsToRemove().size());
        }
        
        @Test
        @DisplayName("Should remove specific object from cache")
        void shouldRemoveSpecificObjectFromCache() {
            Position position1 = new Position(100.0f, 200.0f);
            Position position2 = new Position(150.0f, 250.0f);
            Size size1 = new Size(50.0f, 50.0f);
            Size size2 = new Size(30.0f, 30.0f);
            GameObject object1 = new GameObject(OBJECT_1, RECTANGLE_TYPE, position1, size1);
            GameObject object2 = new GameObject(OBJECT_2, CIRCLE_TYPE, position2, size2);
            
            droppedObjects.add(object1);
            droppedObjects.add(object2);
            
            // Cache both objects
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            controller.cullObject(mockPair, OBJECT_1);
            controller.cullObject(mockPair, OBJECT_2);
            controller.clearRemovalLists();
            
            // Remove object1 from cache and dropped objects
            controller.removeFromCache(OBJECT_1);
            droppedObjects.remove(object1);
            
            // Should still find object2 but not object1
            controller.cullObject(mockPair, OBJECT_1);
            controller.cullObject(mockPair, OBJECT_2);
            
            assertEquals(1, controller.getObjectsToRemove().size());
            assertTrue(controller.getObjectsToRemove().contains(object2));
            assertFalse(controller.getObjectsToRemove().contains(object1));
        }
    }

    @Nested
    @DisplayName("Object Restoration Tests")
    /**
     * Tests for restoring culled objects to their original positions.
     */
    class ObjectRestorationTests {
        
        @Test
        @DisplayName("Should restore culled objects to original positions")
        void shouldRestoreCulledObjectsToOriginalPositions() {
            Position originalPosition = new Position(100.0f, 200.0f);
            Size size = new Size(50.0f, 50.0f);
            GameObject testObject = new GameObject(TEST_OBJECT, RECTANGLE_TYPE, originalPosition, size);
            droppedObjects.add(testObject);
            
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            
            // Cull the object
            controller.cullObject(mockPair, TEST_OBJECT);
            
            // Simulate object position change
            testObject.getPosition().setX(500.0f);
            testObject.getPosition().setY(600.0f);
            
            // Remove from dropped objects (simulating actual culling)
            droppedObjects.remove(testObject);
            
            // Restore objects
            controller.restoreAllCulledObjects();
            
            // Verify position was restored
            assertEquals(100.0f, testObject.getPosition().getX(), 0.001f);
            assertEquals(200.0f, testObject.getPosition().getY(), 0.001f);
            
            // Verify object was added back to model
            verify(mockModel).addDroppedObject(testObject);
            verify(mockModel).decrementInventoryCount(TEST_OBJECT);
        }
        
        @Test
        @DisplayName("Should handle restoration of multiple objects")
        void shouldHandleRestorationOfMultipleObjects() {
            Position pos1 = new Position(100.0f, 200.0f);
            Position pos2 = new Position(300.0f, 400.0f);
            Size size1 = new Size(50.0f, 50.0f);
            Size size2 = new Size(30.0f, 30.0f);
            GameObject object1 = new GameObject(OBJECT_1, RECTANGLE_TYPE, pos1, size1);
            GameObject object2 = new GameObject(OBJECT_2, CIRCLE_TYPE, pos2, size2);
            
            droppedObjects.add(object1);
            droppedObjects.add(object2);
            
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            
            // Cull both objects
            controller.cullObject(mockPair, OBJECT_1);
            controller.cullObject(mockPair, OBJECT_2);
            
            // Restore objects
            controller.restoreAllCulledObjects();
            
            // Verify both objects were restored
            verify(mockModel).addDroppedObject(object1);
            verify(mockModel).addDroppedObject(object2);
            verify(mockModel).decrementInventoryCount(OBJECT_1);
            verify(mockModel).decrementInventoryCount(OBJECT_2);
            
            assertEquals(100.0f, object1.getPosition().getX(), 0.001f);
            assertEquals(200.0f, object1.getPosition().getY(), 0.001f);
            assertEquals(300.0f, object2.getPosition().getX(), 0.001f);
            assertEquals(400.0f, object2.getPosition().getY(), 0.001f);
        }
        
        @Test
        @DisplayName("Should handle empty restoration gracefully")
        void shouldHandleEmptyRestorationGracefully() {
            // No objects culled
            assertDoesNotThrow(() -> controller.restoreAllCulledObjects());
            
            // Verify no interactions with model
            verify(mockModel, never()).addDroppedObject(any());
            verify(mockModel, never()).decrementInventoryCount(any());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    /**
     * Tests for edge cases and error handling scenarios.
     */
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Should handle null object name in culling decision")
        void shouldHandleNullObjectNameInCullingDecision() {
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            // Should not throw exception and treat as regular object (not balloon)
            assertDoesNotThrow(() -> {
                boolean result = controller.shouldCullObject(-150.0, 300.0, mockBounds, null);
                assertTrue(result); // Should still be culled due to position
            });
        }
        
        @Test
        @DisplayName("Should handle negative bounds dimensions")
        void shouldHandleNegativeBoundsDimensions() {
            lenient().when(mockBounds.getWidth()).thenReturn(-BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(-BOUNDS_HEIGHT);
            
            // Should not throw exception
            assertDoesNotThrow(() -> {
                controller.shouldCullObject(0.0, 0.0, mockBounds, TEST_OBJECT);
            });
        }
        
        @Test
        @DisplayName("Should handle zero bounds dimensions")
        void shouldHandleZeroBoundsDimensions() {
            lenient().when(mockBounds.getWidth()).thenReturn(0.0);
            lenient().when(mockBounds.getHeight()).thenReturn(0.0);
            
            boolean result = controller.shouldCullObject(0.0, 0.0, mockBounds, TEST_OBJECT);
            
            // With 100px margin, anything outside -100 to 100 should be culled
            assertFalse(result, "Object at origin should not be culled with zero bounds");
        }
        
        @Test
        @DisplayName("Should handle multiple calls to restoration")
        void shouldHandleMultipleCallsToRestoration() {
            Position originalPosition = new Position(100.0f, 200.0f);
            Size size = new Size(50.0f, 50.0f);
            GameObject testObject = new GameObject(TEST_OBJECT, RECTANGLE_TYPE, originalPosition, size);
            droppedObjects.add(testObject);
            
            PhysicsVisualPair mockPair = new PhysicsVisualPair(null, mockBody);
            controller.cullObject(mockPair, TEST_OBJECT);
            
            // First restoration
            controller.restoreAllCulledObjects();
            
            // Second restoration should not cause issues
            assertDoesNotThrow(() -> controller.restoreAllCulledObjects());
            
            // Should only have called model methods once
            verify(mockModel, times(1)).addDroppedObject(testObject);
            verify(mockModel, times(1)).decrementInventoryCount(TEST_OBJECT);
        }
    }

    @Nested
    @DisplayName("Integration-like Tests")
    /**
     * Integration tests that verify complete workflows combining multiple operations.
     */
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complete cull and restore cycle - setup and culling decisions")
        void shouldHandleCompleteCullAndRestoreCycleSetupAndDecisions() {
            // Setup multiple objects
            Position pos1 = new Position(100.0f, 200.0f);
            Position pos2 = new Position(300.0f, 400.0f);
            Size size1 = new Size(50.0f, 50.0f);
            Size size2 = new Size(30.0f, 30.0f);
            GameObject object1 = new GameObject(OBJECT_1, RECTANGLE_TYPE, pos1, size1);
            GameObject object2 = new GameObject(BALLOON_TYPE, CIRCLE_TYPE, pos2, size2);
            
            droppedObjects.add(object1);
            droppedObjects.add(object2);
            
            lenient().when(mockBounds.getWidth()).thenReturn(BOUNDS_WIDTH);
            lenient().when(mockBounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
            
            // Test culling decisions
            assertTrue(controller.shouldCullObject(-150.0, 300.0, mockBounds, OBJECT_1));
            assertTrue(controller.shouldCullObject(-75.0, 300.0, mockBounds, BALLOON_TYPE)); // balloon has smaller margin
        }
        
        @Test
        @DisplayName("Should handle complete cull and restore cycle - culling operations")
        void shouldHandleCompleteCullAndRestoreCycleCullingOperations() {
            // Setup multiple objects
            Position pos1 = new Position(100.0f, 200.0f);
            Position pos2 = new Position(300.0f, 400.0f);
            Size size1 = new Size(50.0f, 50.0f);
            Size size2 = new Size(30.0f, 30.0f);
            GameObject object1 = new GameObject(OBJECT_1, RECTANGLE_TYPE, pos1, size1);
            GameObject object2 = new GameObject(BALLOON_TYPE, CIRCLE_TYPE, pos2, size2);
            
            droppedObjects.add(object1);
            droppedObjects.add(object2);
            
            PhysicsVisualPair pair1 = new PhysicsVisualPair(null, mockBody);
            PhysicsVisualPair pair2 = new PhysicsVisualPair(null, mock(Body.class));
            
            // Cull objects
            controller.cullObject(pair1, OBJECT_1);
            controller.cullObject(pair2, BALLOON_TYPE);
            
            // Verify culling
            assertEquals(2, controller.getPairsToRemove().size());
            assertEquals(2, controller.getObjectsToRemove().size());
        }
        
        @Test
        @DisplayName("Should handle complete cull and restore cycle - restoration")
        void shouldHandleCompleteCullAndRestoreCycleRestoration() {
            // Setup multiple objects
            Position pos1 = new Position(100.0f, 200.0f);
            Position pos2 = new Position(300.0f, 400.0f);
            Size size1 = new Size(50.0f, 50.0f);
            Size size2 = new Size(30.0f, 30.0f);
            GameObject object1 = new GameObject(OBJECT_1, RECTANGLE_TYPE, pos1, size1);
            GameObject object2 = new GameObject(BALLOON_TYPE, CIRCLE_TYPE, pos2, size2);
            
            droppedObjects.add(object1);
            droppedObjects.add(object2);
            
            PhysicsVisualPair pair1 = new PhysicsVisualPair(null, mockBody);
            PhysicsVisualPair pair2 = new PhysicsVisualPair(null, mock(Body.class));
            
            // Cull objects and clear lists
            controller.cullObject(pair1, OBJECT_1);
            controller.cullObject(pair2, BALLOON_TYPE);
            controller.clearRemovalLists();
            
            // Restore and verify
            controller.restoreAllCulledObjects();
            
            verify(mockModel).addDroppedObject(object1);
            verify(mockModel).addDroppedObject(object2);
            verify(mockModel).decrementInventoryCount(OBJECT_1);
            verify(mockModel).decrementInventoryCount(BALLOON_TYPE);
        }
        
        @Test
        @DisplayName("Should handle complete cull and restore cycle - position verification")
        void shouldHandleCompleteCullAndRestoreCyclePositionVerification() {
            // Setup multiple objects
            Position pos1 = new Position(100.0f, 200.0f);
            Position pos2 = new Position(300.0f, 400.0f);
            Size size1 = new Size(50.0f, 50.0f);
            Size size2 = new Size(30.0f, 30.0f);
            GameObject object1 = new GameObject(OBJECT_1, RECTANGLE_TYPE, pos1, size1);
            GameObject object2 = new GameObject(BALLOON_TYPE, CIRCLE_TYPE, pos2, size2);
            
            droppedObjects.add(object1);
            droppedObjects.add(object2);
            
            PhysicsVisualPair pair1 = new PhysicsVisualPair(null, mockBody);
            PhysicsVisualPair pair2 = new PhysicsVisualPair(null, mock(Body.class));
            
            // Cull, clear, and restore
            controller.cullObject(pair1, OBJECT_1);
            controller.cullObject(pair2, BALLOON_TYPE);
            controller.clearRemovalLists();
            controller.restoreAllCulledObjects();
            
            // Verify positions restored
            assertEquals(100.0f, object1.getPosition().getX(), 0.001f);
            assertEquals(300.0f, object2.getPosition().getX(), 0.001f);
        }
    }
}
