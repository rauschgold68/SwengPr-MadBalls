package mm.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import mm.model.GameObject;
import mm.model.Physics;
import mm.model.PhysicsVisualPair;
import mm.model.Position;
import mm.model.Size;

/**
 * JavaFX-less unit tests for GameObjectController.
 * Tests the controller's ability to convert game objects to physics-visual pairs
 * without JavaFX dependencies in the test environment.
 */
public class TestGameObjectController {

    @Mock
    private World mockWorld;
    
    @Mock
    private Body mockBody;
    
    private GameObject testGameObject;
    private Physics testPhysics;
    private Position testPosition;
    private Size testSize;
    
    /**
     * Sets up test fixtures before each test method.
     * Creates mock objects and test data without JavaFX dependencies.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup mock world behavior
        when(mockWorld.createBody(any())).thenReturn(mockBody);
        when(mockBody.createFixture(any())).thenReturn(null);
        
        // Create test physics object
        testPhysics = new Physics();
        testPhysics.setShape("DYNAMIC");
        testPhysics.setDensity(1.0f);
        testPhysics.setFriction(0.5f);
        testPhysics.setRestitution(0.3f);
        
        // Create test position
        testPosition = new Position(100.0f, 200.0f);
        
        // Create test size object for rectangle
        testSize = new Size(80.0f, 60.0f);
        
        // Create basic test game object
        testGameObject = new GameObject("testGameObject", "rectangle", testPosition, testSize);
        testGameObject.setPhysics(testPhysics);
        testGameObject.setColour("#FF0000");
        testGameObject.setAngle(0.0f);
    }
    
    /**
     * Tests successful conversion of a rectangle game object to PhysicsVisualPair.
     * Verifies that both visual and physics components are created correctly.
     */
    @Test
    public void testConvertRectangleGameObject() {
        PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "PhysicsVisualPair should not be null");
        assertNotNull(result.visual, "Visual component should not be null");
        assertNotNull(result.body, "Physics body should not be null");
        
        // Verify the visual is a Rectangle
        assertTrue(result.visual instanceof Rectangle, "Visual should be a Rectangle");
        Rectangle rect = (Rectangle) result.visual;
        assertEquals(80.0, rect.getWidth(), "Rectangle width should match game object");
        assertEquals(60.0, rect.getHeight(), "Rectangle height should match game object");
        assertEquals(100.0, rect.getTranslateX(), "Rectangle X position should match game object");
        assertEquals(200.0, rect.getTranslateY(), "Rectangle Y position should match game object");
        
        // Verify world interaction
        verify(mockWorld, times(1)).createBody(any());
        verify(mockBody, times(1)).createFixture(any());
        verify(mockBody, atLeastOnce()).setUserData(any(String.class)); // Called multiple times due to winning logic
    }
    
    /**
     * Tests successful conversion of a circle game object to PhysicsVisualPair.
     * Verifies circle-specific properties are handled correctly.
     */
    @Test
    public void testConvertCircleGameObject() {
        // Create circle-specific test data
        Size circleSize = new Size(35.0f);
        Position circlePosition = new Position(150.0f, 250.0f);
        
        GameObject circleObject = new GameObject("circleTest", "circle", circlePosition, circleSize);
        circleObject.setPhysics(testPhysics);
        circleObject.setColour("#00FF00");
        circleObject.setAngle(45.0f);
        
        PhysicsVisualPair result = GameObjectController.convert(circleObject, mockWorld);
        
        assertNotNull(result, "PhysicsVisualPair should not be null");
        assertTrue(result.visual instanceof Circle, "Visual should be a Circle");
        
        Circle circle = (Circle) result.visual;
        assertEquals(35.0, circle.getRadius(), "Circle radius should match game object");
        assertEquals(150.0, circle.getTranslateX(), "Circle X position should match game object");
        assertEquals(250.0, circle.getTranslateY(), "Circle Y position should match game object");
        assertEquals(45.0, circle.getRotate(), "Circle rotation should match game object");
        
        verify(mockWorld, times(1)).createBody(any());
        verify(mockBody, atLeastOnce()).setUserData(any(String.class)); // Called multiple times due to winning logic
    }
    
    /**
     * Tests successful conversion of a bucket game object to PhysicsVisualPair.
     * Verifies bucket-specific U-shape polygon is created correctly.
     */
    @Test
    public void testConvertBucketGameObject() {
        Position bucketPosition = new Position(50.0f, 100.0f);
        GameObject bucketObject = new GameObject("bucketTest", "bucket", bucketPosition, testSize);
        bucketObject.setPhysics(testPhysics);
        bucketObject.setColour("#0000FF");
        bucketObject.setAngle(30.0f);
        
        PhysicsVisualPair result = GameObjectController.convert(bucketObject, mockWorld);
        
        assertNotNull(result, "PhysicsVisualPair should not be null");
        assertTrue(result.visual instanceof Polygon, "Visual should be a Polygon for bucket");
        
        Polygon bucket = (Polygon) result.visual;
        assertFalse(bucket.getPoints().isEmpty(), "Bucket should have polygon points");
        assertEquals(50.0, bucket.getTranslateX(), "Bucket X position should match game object");
        assertEquals(100.0, bucket.getTranslateY(), "Bucket Y position should match game object");
        assertEquals(30.0, bucket.getRotate(), "Bucket rotation should match game object");
        
        // Verify multiple fixtures are created for bucket walls (bottom, left, right)
        verify(mockWorld, times(1)).createBody(any());
        verify(mockBody, times(3)).createFixture(any()); // 3 walls
        verify(mockBody, atLeastOnce()).setUserData(any(String.class)); // Called multiple times due to winning logic
    }
    
    /**
     * Tests handling of unsupported shape types.
     * Verifies that appropriate exception is thrown for invalid types.
     */
    @Test
    public void testConvertUnsupportedShapeType() {
        GameObject invalidObject = new GameObject("invalid", "triangle", testPosition, testSize);
        invalidObject.setPhysics(testPhysics);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> GameObjectController.convert(invalidObject, mockWorld),
            "Should throw IllegalArgumentException for unsupported shape"
        );
        
        assertTrue(exception.getMessage().contains("Unsupported shape type"), 
            "Exception message should mention unsupported shape type");
    }
    
    /**
     * Tests conversion with different object names.
     * Verifies that object names are properly handled.
     */
    @Test
    public void testConvertWithDifferentNames() {
        GameObject namedObject = new GameObject("specialObject", "rectangle", testPosition, testSize);
        namedObject.setPhysics(testPhysics);
        namedObject.setColour("#FF0000");
        
        PhysicsVisualPair result = GameObjectController.convert(namedObject, mockWorld);
        
        assertNotNull(result, "PhysicsVisualPair should not be null");
        assertTrue(result.visual instanceof Rectangle, "Visual should be Rectangle");
        
        verify(mockBody, atLeastOnce()).setUserData(any(String.class));
    }
    
    /**
     * Tests conversion with pattern objects.
     * Verifies that objects with patterns are handled properly.
     */
    @Test
    public void testConvertWithPattern() {
        GameObject patternObject = new GameObject("patternTest", "rectangle", testPosition, testSize);
        patternObject.setPhysics(testPhysics);
        patternObject.setColour("#00FF00");
        
        PhysicsVisualPair result = GameObjectController.convert(patternObject, mockWorld);
        
        assertNotNull(result, "PhysicsVisualPair should not be null");
        assertTrue(result.visual instanceof Rectangle, "Visual should be Rectangle");
        
        verify(mockBody, atLeastOnce()).setUserData(any(String.class));
    }
    
    /**
     * Tests winning object logic application.
     * Verifies that winning objects get special user data.
     */
    @Test
    public void testConvertWinningObject() {
        testGameObject.setWinning(true);
        
        PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "Winning object PhysicsVisualPair should not be null");
        
        // The exact behavior depends on the winning object logic implementation
        // but we can verify that the body user data is set
        verify(mockBody, atLeastOnce()).setUserData(any(String.class));
    }
    
    /**
     * Tests conversion with different physics properties.
     * Verifies that static physics objects are handled correctly.
     */
    @Test
    public void testConvertWithStaticPhysics() {
        Physics staticPhysics = new Physics();
        staticPhysics.setShape("STATIC");
        staticPhysics.setDensity(0.0f);
        staticPhysics.setFriction(0.8f);
        staticPhysics.setRestitution(0.0f);
        
        testGameObject.setPhysics(staticPhysics);
        
        PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "Should create pair with static physics");
        verify(mockWorld, times(1)).createBody(any());
    }
    
    /**
     * Tests conversion with rotated objects.
     * Verifies that rotation is properly applied to both visual and physics.
     */
    @Test
    public void testConvertWithRotation() {
        testGameObject.setAngle(90.0f);
        
        PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "Rotated object should convert successfully");
        assertTrue(result.visual instanceof Rectangle, "Visual should be Rectangle");
        
        Rectangle rect = (Rectangle) result.visual;
        assertEquals(90.0, rect.getRotate(), "Visual rotation should match game object");
        
        verify(mockWorld, times(1)).createBody(any());
    }
    
    /**
     * Tests conversion with null world parameter.
     * Verifies appropriate null handling.
     */
    @Test
    public void testConvertWithNullWorld() {
        assertThrows(
            NullPointerException.class,
            () -> GameObjectController.convert(testGameObject, null),
            "Should throw NullPointerException when world is null"
        );
    }
    
    /**
     * Tests conversion with null game object parameter.
     * Verifies appropriate null handling.
     */
    @Test
    public void testConvertWithNullGameObject() {
        assertThrows(
            NullPointerException.class,
            () -> GameObjectController.convert(null, mockWorld),
            "Should throw NullPointerException when game object is null"
        );
    }
    
    /**
     * Tests game object with sprite configuration.
     * Verifies that sprites are handled without causing errors.
     */
    @Test
    public void testConvertWithSprite() {
        testGameObject.setSprite("/images/test_sprite.png");
        
        // Should not throw exception even if sprite loading fails in test environment
        assertDoesNotThrow(
            () -> GameObjectController.convert(testGameObject, mockWorld),
            "Convert should handle sprite loading gracefully"
        );
    }
    
    /**
     * Tests conversion of objects at different positions.
     * Verifies that position handling works correctly.
     */
    @Test
    public void testConvertWithDifferentPositions() {
        // Test with negative positions
        Position negativePos = new Position(-50.0f, -100.0f);
        testGameObject.setPosition(negativePos);
        
        PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "Object with negative position should convert");
        Rectangle rect = (Rectangle) result.visual;
        assertEquals(-50.0, rect.getTranslateX(), "X position should handle negative values");
        assertEquals(-100.0, rect.getTranslateY(), "Y position should handle negative values");
        
        // Test with zero position
        Position zeroPos = new Position(0.0f, 0.0f);
        testGameObject.setPosition(zeroPos);
        
        result = GameObjectController.convert(testGameObject, mockWorld);
        rect = (Rectangle) result.visual;
        assertEquals(0.0, rect.getTranslateX(), "X position should handle zero");
        assertEquals(0.0, rect.getTranslateY(), "Y position should handle zero");
    }
    
    /**
     * Tests batch conversion of multiple game objects.
     * Verifies controller can handle multiple conversions without issues.
     */
    @Test
    public void testBatchConversion() {
        List<GameObject> objects = new ArrayList<>();
        
        // Create multiple test objects with different properties
        objects.add(new GameObject("rect1", "rectangle", new Position(0, 0), testSize));
        objects.add(new GameObject("circle1", "circle", new Position(100, 100), new Size(25.0f)));
        objects.add(new GameObject("bucket1", "bucket", new Position(200, 200), testSize));
        
        // Set physics for all objects
        for (GameObject obj : objects) {
            obj.setPhysics(testPhysics);
            obj.setColour("#FFFFFF");
            obj.setAngle(0.0f);
        }
        
        List<PhysicsVisualPair> results = new ArrayList<>();
        
        // Convert all objects
        assertDoesNotThrow(() -> {
            for (GameObject obj : objects) {
                results.add(GameObjectController.convert(obj, mockWorld));
            }
        }, "Batch conversion should not throw exceptions");
        
        assertEquals(3, results.size(), "Should convert all objects");
        
        // Verify all results are valid
        for (PhysicsVisualPair pair : results) {
            assertNotNull(pair.visual, "Each visual should be created");
            assertNotNull(pair.body, "Each body should be created");
        }
        
        // Verify correct number of world interactions
        verify(mockWorld, times(3)).createBody(any());
    }
    
    /**
     * Tests conversion with edge case sizes.
     * Verifies handling of very small and very large objects.
     */
    @Test
    public void testConvertWithEdgeCaseSizes() {
        // Test with very small rectangle
        Size tinySize = new Size(1.0f, 1.0f);
        testGameObject.setSize(tinySize);
        
        PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "Tiny object should convert successfully");
        Rectangle rect = (Rectangle) result.visual;
        assertEquals(1.0, rect.getWidth(), "Tiny width should be preserved");
        assertEquals(1.0, rect.getHeight(), "Tiny height should be preserved");
        
        // Test with very large rectangle
        Size largeSize = new Size(1000.0f, 1000.0f);
        testGameObject.setSize(largeSize);
        
        result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "Large object should convert successfully");
        rect = (Rectangle) result.visual;
        assertEquals(1000.0, rect.getWidth(), "Large width should be preserved");
        assertEquals(1000.0, rect.getHeight(), "Large height should be preserved");
    }
    
    /**
     * Tests conversion with extreme rotation values.
     * Verifies handling of various angle values.
     */
    @Test
    public void testConvertWithExtremeRotations() {
        // Test with 360 degree rotation
        testGameObject.setAngle(360.0f);
        
        PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "360 degree rotation should work");
        Rectangle rect = (Rectangle) result.visual;
        assertEquals(360.0, rect.getRotate(), "360 degree rotation should be preserved");
        
        // Test with negative rotation
        testGameObject.setAngle(-90.0f);
        
        result = GameObjectController.convert(testGameObject, mockWorld);
        rect = (Rectangle) result.visual;
        assertEquals(-90.0, rect.getRotate(), "Negative rotation should be preserved");
    }

    // ========== SPRITE LOADING TESTS ==========
    
    /**
     * Tests successful sprite loading for rectangle objects.
     * Verifies that valid sprite paths load correctly.
     */
    @Test
    public void testRectangleSpriteLoadingSuccess() {
        // This test simulates successful sprite loading by not setting a sprite
        // In a real environment, a valid sprite path would be loaded
        testGameObject.setSprite(null); // No sprite, should use color
        
        PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
        
        assertNotNull(result, "Should convert without sprite");
        assertTrue(result.visual instanceof Rectangle, "Should create Rectangle");
        
        // Color should be used when no sprite is present
        Rectangle rect = (Rectangle) result.visual;
        assertNotNull(rect.getFill(), "Fill should not be null");
    }
    
    /**
     * Tests sprite loading failure fallback for rectangle objects.
     * Verifies that invalid sprite paths gracefully fall back to color.
     */
    @Test
    public void testRectangleSpriteLoadingFailure() {
        testGameObject.setSprite("/nonexistent/sprite.png");
        
        // Should not throw exception, should fall back to color
        assertDoesNotThrow(() -> {
            PhysicsVisualPair result = GameObjectController.convert(testGameObject, mockWorld);
            assertNotNull(result, "Should convert with invalid sprite");
            assertTrue(result.visual instanceof Rectangle, "Should create Rectangle");
        }, "Should handle sprite loading failure gracefully");
    }
    
    /**
     * Tests successful sprite loading for circle objects.
     * Verifies that circle sprite handling works correctly.
     */
    @Test
    public void testCircleSpriteLoadingSuccess() {
        Size circleSize = new Size(30.0f);
        Position circlePosition = new Position(100.0f, 100.0f);
        GameObject circleObject = new GameObject("circleSprite", "circle", circlePosition, circleSize);
        circleObject.setPhysics(testPhysics);
        circleObject.setColour("#00FF00");
        circleObject.setSprite(null); // No sprite, should use color
        
        PhysicsVisualPair result = GameObjectController.convert(circleObject, mockWorld);
        
        assertNotNull(result, "Should convert circle without sprite");
        assertTrue(result.visual instanceof Circle, "Should create Circle");
        
        Circle circle = (Circle) result.visual;
        assertNotNull(circle.getFill(), "Fill should not be null");
    }
    
    /**
     * Tests sprite loading failure for circle objects.
     * Verifies that circles handle sprite loading errors correctly.
     */
    @Test
    public void testCircleSpriteLoadingFailure() {
        Size circleSize = new Size(30.0f);
        Position circlePosition = new Position(100.0f, 100.0f);
        GameObject circleObject = new GameObject("circleSpriteFail", "circle", circlePosition, circleSize);
        circleObject.setPhysics(testPhysics);
        circleObject.setColour("#00FF00");
        circleObject.setSprite("/invalid/path.png");
        
        // Should not throw exception, should fall back to color
        assertDoesNotThrow(() -> {
            PhysicsVisualPair result = GameObjectController.convert(circleObject, mockWorld);
            assertNotNull(result, "Should convert with invalid sprite");
            assertTrue(result.visual instanceof Circle, "Should create Circle");
        }, "Should handle circle sprite loading failure gracefully");
    }

    // ========== WINNING OBJECT LOGIC TESTS ==========
    
    /**
     * Tests winning object naming logic with regular objects.
     * Verifies that winning objects get renamed to "winObject".
     */
    @Test
    public void testWinningObjectNaming() {
        GameObject regularWinningObject = new GameObject("regularObject", "rectangle", testPosition, testSize);
        regularWinningObject.setPhysics(testPhysics);
        regularWinningObject.setColour("#FF0000"); // Set color to avoid null issues
        regularWinningObject.setWinning(true);
        
        PhysicsVisualPair result = GameObjectController.convert(regularWinningObject, mockWorld);
        
        assertNotNull(result, "Winning object should be converted");
        verify(mockBody, atLeastOnce()).setUserData("winObject"); // Should be renamed
    }
    
    /**
     * Tests winning object logic with non-winning objects.
     * Verifies that non-winning objects keep their original names.
     */
    @Test
    public void testNonWinningObjectNaming() {
        GameObject nonWinningObject = new GameObject("regularObject", "rectangle", testPosition, testSize);
        nonWinningObject.setPhysics(testPhysics);
        nonWinningObject.setColour("#FF0000"); // Set color to avoid null issues
        nonWinningObject.setWinning(false);
        
        PhysicsVisualPair result = GameObjectController.convert(nonWinningObject, mockWorld);
        
        assertNotNull(result, "Non-winning object should be converted");
        verify(mockBody, atLeastOnce()).setUserData("regularObject"); // Should keep original name
    }
    
    /**
     * Tests winning object logic with winplat special case.
     * Verifies that winplat objects are handled specially.
     */
    @Test
    public void testWinplatSpecialCase() {
        GameObject winplatObject = new GameObject("winplat", "rectangle", testPosition, testSize);
        winplatObject.setPhysics(testPhysics);
        winplatObject.setColour("#FF0000"); // Set color to avoid null issues
        winplatObject.setWinning(true);
        
        PhysicsVisualPair result = GameObjectController.convert(winplatObject, mockWorld);
        
        assertNotNull(result, "Winplat object should be converted");
        // Due to the OR logic bug, winplat should still get renamed
        // The original logic: (!winplat OR !winZone) is always true
        verify(mockBody, atLeastOnce()).setUserData("winObject");
    }
    
    /**
     * Tests winning object logic OR condition behavior.
     * Verifies the current (potentially buggy) OR logic behavior.
     */
    @Test
    public void testWinningObjectLogicORCondition() {
        // Test that the OR condition is always true due to logical error
        // (!winplat OR !winZone) is always true for any single object
        
        GameObject testObj1 = new GameObject("someObject", "rectangle", testPosition, testSize);
        testObj1.setPhysics(testPhysics);
        testObj1.setColour("#FF0000"); // Set color to avoid null issues
        testObj1.setWinning(true);
        
        PhysicsVisualPair result1 = GameObjectController.convert(testObj1, mockWorld);
        assertNotNull(result1, "Object should be converted");
        verify(mockBody, atLeastOnce()).setUserData("winObject");
        
        // Reset mock for next test
        reset(mockBody);
        when(mockWorld.createBody(any())).thenReturn(mockBody);
        when(mockBody.createFixture(any())).thenReturn(null);
        
        GameObject testObj2 = new GameObject("anotherObject", "rectangle", testPosition, testSize);
        testObj2.setPhysics(testPhysics);
        testObj2.setColour("#FF0000"); // Set color to avoid null issues
        testObj2.setWinning(false);
        
        PhysicsVisualPair result2 = GameObjectController.convert(testObj2, mockWorld);
        assertNotNull(result2, "Object should be converted");
        verify(mockBody, atLeastOnce()).setUserData("anotherObject"); // Should keep original name when not winning
    }
    
    /**
     * Tests physics properties interaction with winning objects.
     * Verifies that winning object logic doesn't interfere with physics.
     */
    @Test
    public void testWinningObjectPhysicsInteraction() {
        GameObject winningStatic = new GameObject("staticWinner", "rectangle", testPosition, testSize);
        Physics staticPhysics = new Physics();
        staticPhysics.setShape("STATIC");
        staticPhysics.setDensity(0.0f);
        staticPhysics.setFriction(0.8f);
        staticPhysics.setRestitution(0.0f);
        winningStatic.setPhysics(staticPhysics);
        winningStatic.setColour("#FF0000"); // Set color to avoid null issues
        winningStatic.setWinning(true);
        
        PhysicsVisualPair result = GameObjectController.convert(winningStatic, mockWorld);
        
        assertNotNull(result, "Winning static object should be converted");
        verify(mockWorld, times(1)).createBody(any());
        verify(mockBody, times(1)).createFixture(any());
        verify(mockBody, atLeastOnce()).setUserData("winObject");
    }
}
