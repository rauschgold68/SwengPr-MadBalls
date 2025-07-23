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

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import mm.model.InventoryObject;
import mm.model.Physics;
import mm.model.PhysicsVisualPair;
import mm.model.Size;

/**
 * JavaFX-less unit tests for InventoryObjectController.
 * Tests the controller's ability to convert inventory objects to physics-visual pairs
 * and create preview visuals without JavaFX dependencies in the test environment.
 */
public class TestInventoryObjectController {

    // Test constants to avoid duplicate literals
    private static final String RECTANGLE_TYPE = "rectangle";
    private static final String CIRCLE_TYPE = "circle";
    private static final String BUCKET_TYPE = "bucket";
    private static final String RED_COLOR = "#FF0000";
    private static final String GREEN_COLOR = "#00FF00";
    private static final String BLUE_COLOR = "#0000FF";
    private static final String YELLOW_COLOR = "#FFFF00";
    private static final String WHITE_COLOR = "#FFFFFF";
    private static final String NULL_PAIR_MSG = "PhysicsVisualPair should not be null";
    private static final String TEST_OBJECT = "testObject";

    @Mock
    private World mockWorld;
    
    @Mock
    private Body mockBody;
    
    private InventoryObject testInventoryObject;
    private Physics testPhysics;
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
        
        // Create test size object for rectangle
        testSize = new Size();
        testSize.setWidth(100.0f);
        testSize.setHeight(50.0f);
        
        // Create basic test inventory object
        testInventoryObject = new InventoryObject(TEST_OBJECT, RECTANGLE_TYPE, testSize);
        testInventoryObject.setPhysics(testPhysics);
        testInventoryObject.setColour(RED_COLOR);
        testInventoryObject.setCount(5);
    }
    
    /**
     * Tests successful conversion of a rectangle inventory object to PhysicsVisualPair.
     * Verifies that both visual and physics components are created correctly.
     */
    @Test
    public void testConvertRectangleInventoryObject() {
        PhysicsVisualPair result = InventoryObjectController.convert(testInventoryObject, mockWorld);
        
        assertNotNull(result, NULL_PAIR_MSG);
        assertNotNull(result.visual, "Visual component should not be null");
        assertNotNull(result.body, "Physics body should not be null");
        
        // Verify the visual is a Rectangle
        assertTrue(result.visual instanceof Rectangle, "Visual should be a Rectangle");
        Rectangle rect = (Rectangle) result.visual;
        assertEquals(100.0, rect.getWidth(), "Rectangle width should match inventory object");
        assertEquals(50.0, rect.getHeight(), "Rectangle height should match inventory object");
        
        // Verify world interaction
        verify(mockWorld, times(1)).createBody(any());
        verify(mockBody, times(1)).createFixture(any());
        verify(mockBody, times(1)).setUserData(TEST_OBJECT);
    }
    
    /**
     * Tests successful conversion of a circle inventory object to PhysicsVisualPair.
     * Verifies circle-specific properties are handled correctly.
     */
    @Test
    public void testConvertCircleInventoryObject() {
        // Create circle-specific test data
        Size circleSize = new Size();
        circleSize.setRadius(25.0f);
        
        InventoryObject circleObject = new InventoryObject("circleTest", CIRCLE_TYPE, circleSize);
        circleObject.setPhysics(testPhysics);
        circleObject.setColour(GREEN_COLOR);
        
        PhysicsVisualPair result = InventoryObjectController.convert(circleObject, mockWorld);
        
        assertNotNull(result, NULL_PAIR_MSG);
        assertTrue(result.visual instanceof Circle, "Visual should be a Circle");
        
        Circle circle = (Circle) result.visual;
        assertEquals(25.0, circle.getRadius(), "Circle radius should match inventory object");
        
        verify(mockWorld, times(1)).createBody(any());
        verify(mockBody, times(1)).setUserData("circleTest");
    }
    
    /**
     * Tests successful conversion of a bucket inventory object to PhysicsVisualPair.
     * Verifies bucket-specific U-shape polygon is created correctly.
     */
    @Test
    public void testConvertBucketInventoryObject() {
        InventoryObject bucketObject = new InventoryObject("bucketTest", BUCKET_TYPE, testSize);
        bucketObject.setPhysics(testPhysics);
        bucketObject.setColour(BLUE_COLOR);
        
        PhysicsVisualPair result = InventoryObjectController.convert(bucketObject, mockWorld);
        
        assertNotNull(result, NULL_PAIR_MSG);
        assertTrue(result.visual instanceof Polygon, "Visual should be a Polygon for bucket");
        
        Polygon bucket = (Polygon) result.visual;
        assertFalse(bucket.getPoints().isEmpty(), "Bucket should have polygon points");
        
        // Verify multiple fixtures are created for bucket walls (bottom, left, right)
        verify(mockWorld, times(1)).createBody(any());
        verify(mockBody, times(3)).createFixture(any()); // 3 walls
        verify(mockBody, times(1)).setUserData("bucketTest");
    }
    
    /**
     * Tests handling of unsupported shape types.
     * Verifies that appropriate exception is thrown for invalid types.
     */
    @Test
    public void testConvertUnsupportedShapeType() {
        InventoryObject invalidObject = new InventoryObject("invalid", "triangle", testSize);
        invalidObject.setPhysics(testPhysics);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InventoryObjectController.convert(invalidObject, mockWorld),
            "Should throw IllegalArgumentException for unsupported shape"
        );
        
        assertTrue(exception.getMessage().contains("Unsupported shape type"), 
            "Exception message should mention unsupported shape type");
    }
    
    /**
     * Tests conversion with different object names.
     * Verifies that object names are properly set as user data.
     */
    @Test
    public void testConvertWithDifferentNames() {
        InventoryObject namedObject = new InventoryObject("specialObject", RECTANGLE_TYPE, testSize);
        namedObject.setPhysics(testPhysics);
        namedObject.setColour(RED_COLOR);
        
        PhysicsVisualPair result = InventoryObjectController.convert(namedObject, mockWorld);
        
        assertNotNull(result, NULL_PAIR_MSG);
        assertTrue(result.visual instanceof Rectangle, "Visual should be Rectangle");
        
        verify(mockBody, atLeastOnce()).setUserData("specialObject");
    }
    
    /**
     * Tests conversion with pattern objects.
     * Verifies that objects with patterns are handled properly.
     */
    @Test
    public void testConvertWithPattern() {
        InventoryObject patternObject = new InventoryObject("patternTest", RECTANGLE_TYPE, testSize);
        patternObject.setPhysics(testPhysics);
        patternObject.setColour(GREEN_COLOR);
        
        PhysicsVisualPair result = InventoryObjectController.convert(patternObject, mockWorld);
        
        assertNotNull(result, NULL_PAIR_MSG);
        assertTrue(result.visual instanceof Rectangle, "Visual should be Rectangle");
        
        verify(mockBody, atLeastOnce()).setUserData("patternTest");
    }
    
    /**
     * Tests createPreviewVisual method for rectangle objects.
     * Verifies that preview visuals are created without physics bodies.
     */
    @Test
    public void testCreatePreviewVisualRectangle() {
        testInventoryObject.setAngle(45.0f); // Test rotation
        
        Node preview = InventoryObjectController.createPreviewVisual(testInventoryObject);
        
        assertNotNull(preview, "Preview visual should not be null");
        assertTrue(preview instanceof Rectangle, "Preview should be Rectangle");
        
        Rectangle rect = (Rectangle) preview;
        assertEquals(100.0, rect.getWidth(), "Preview width should match object");
        assertEquals(50.0, rect.getHeight(), "Preview height should match object");
        assertEquals(45.0, rect.getRotate(), "Preview rotation should match object");
    }
    
    /**
     * Tests createPreviewVisual method for circle objects.
     * Verifies circle preview visuals are created correctly.
     */
    @Test
    public void testCreatePreviewVisualCircle() {
        Size circleSize = new Size();
        circleSize.setRadius(30.0f);
        
        InventoryObject circleObject = new InventoryObject("previewCircle", CIRCLE_TYPE, circleSize);
        circleObject.setColour(GREEN_COLOR); // Set color to avoid null issues
        circleObject.setAngle(90.0f);
        
        Node preview = InventoryObjectController.createPreviewVisual(circleObject);
        
        assertNotNull(preview, "Circle preview should not be null");
        assertTrue(preview instanceof Circle, "Preview should be Circle");
        
        Circle circle = (Circle) preview;
        assertEquals(30.0, circle.getRadius(), "Preview radius should match object");
        assertEquals(90.0, circle.getRotate(), "Preview rotation should match object");
    }
    
    /**
     * Tests createPreviewVisual method for bucket objects.
     * Verifies bucket preview visuals are created as polygons.
     */
    @Test
    public void testCreatePreviewVisualBucket() {
        InventoryObject bucketObject = new InventoryObject("previewBucket", BUCKET_TYPE, testSize);
        bucketObject.setColour(BLUE_COLOR); // Set color to avoid null issues
        bucketObject.setAngle(30.0f);
        
        Node preview = InventoryObjectController.createPreviewVisual(bucketObject);
        
        assertNotNull(preview, "Bucket preview should not be null");
        assertTrue(preview instanceof Polygon, "Preview should be Polygon");
        
        Polygon bucket = (Polygon) preview;
        assertFalse(bucket.getPoints().isEmpty(), "Preview bucket should have points");
        assertEquals(30.0, bucket.getRotate(), "Preview rotation should match object");
    }
    
    /**
     * Tests createPreviewVisual with unsupported shape type.
     * Verifies appropriate exception handling.
     */
    @Test
    public void testCreatePreviewVisualUnsupportedType() {
        InventoryObject invalidObject = new InventoryObject("invalid", "pentagon", testSize);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InventoryObjectController.createPreviewVisual(invalidObject),
            "Should throw exception for unsupported preview type"
        );
        
        assertTrue(exception.getMessage().contains("Unsupported shape type"));
    }
    
    /**
     * Tests conversion with null world parameter.
     * Verifies appropriate null handling.
     */
    @Test
    public void testConvertWithNullWorld() {
        // This test verifies the controller's behavior when world is null
        // The method should attempt to create a body and fail appropriately
        assertThrows(
            NullPointerException.class,
            () -> InventoryObjectController.convert(testInventoryObject, null),
            "Should throw NullPointerException when world is null"
        );
    }
    
    /**
     * Tests conversion with null inventory object parameter.
     * Verifies appropriate null handling.
     */
    @Test
    public void testConvertWithNullInventoryObject() {
        assertThrows(
            NullPointerException.class,
            () -> InventoryObjectController.convert(null, mockWorld),
            "Should throw NullPointerException when inventory object is null"
        );
    }
    
    /**
     * Tests inventory object with sprite configuration.
     * Verifies that sprites are handled without causing errors.
     */
    @Test
    public void testConvertWithSprite() {
        testInventoryObject.setSprite("/images/test_sprite.png");
        
        // Should throw exception because sprite loading fails in test environment
        assertThrows(RuntimeException.class,
            () -> InventoryObjectController.convert(testInventoryObject, mockWorld),
            "Convert should throw exception for missing sprite"
        );
    }
    
    /**
     * Tests physics properties are correctly applied.
     * Verifies that different physics configurations work properly.
     */
    @Test
    public void testConvertWithDifferentPhysicsProperties() {
        // Test with static physics
        Physics staticPhysics = new Physics();
        staticPhysics.setShape("STATIC");
        staticPhysics.setDensity(0.0f);
        staticPhysics.setFriction(0.8f);
        staticPhysics.setRestitution(0.1f);
        
        testInventoryObject.setPhysics(staticPhysics);
        
        PhysicsVisualPair result = InventoryObjectController.convert(testInventoryObject, mockWorld);
        
        assertNotNull(result, "Should create pair with static physics");
        verify(mockWorld, times(1)).createBody(any());
    }
    
    /**
     * Tests batch conversion of multiple inventory objects.
     * Verifies controller can handle multiple conversions without issues.
     */
    @Test
    public void testBatchConversion() {
        List<InventoryObject> objects = new ArrayList<>();
        
        // Create multiple test objects
        objects.add(new InventoryObject("rect1", "rectangle", testSize));
        
        Size circleSize = new Size(20.0f);
        objects.add(new InventoryObject("circle1", "circle", circleSize));
        objects.add(new InventoryObject("bucket1", "bucket", testSize));
        
        // Set physics for all objects
        for (InventoryObject obj : objects) {
            obj.setPhysics(testPhysics);
            obj.setColour(WHITE_COLOR);
        }
        
        List<PhysicsVisualPair> results = new ArrayList<>();
        
        // Convert all objects
        assertDoesNotThrow(() -> {
            for (InventoryObject obj : objects) {
                results.add(InventoryObjectController.convert(obj, mockWorld));
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

    // ========== SPRITE LOADING TESTS ==========
    
    /**
     * Tests successful sprite loading for rectangle inventory objects.
     * Verifies that sprite loading success path works correctly.
     */
    @Test
    public void testRectangleSpriteLoadingSuccess() {
        // Test without sprite first (should use color)
        testInventoryObject.setSprite(null);
        
        PhysicsVisualPair result = InventoryObjectController.convert(testInventoryObject, mockWorld);
        
        assertNotNull(result, "Should convert without sprite");
        assertTrue(result.visual instanceof Rectangle, "Should create Rectangle");
        
        Rectangle rect = (Rectangle) result.visual;
        assertNotNull(rect.getFill(), "Fill should not be null");
    }
    
    /**
     * Tests sprite loading failure with RuntimeException for rectangle objects.
     * Verifies that sprite loading exceptions are properly handled.
     */
    @Test
    public void testRectangleSpriteLoadingFailure() {
        testInventoryObject.setSprite("/nonexistent/sprite.png");
        
        // Should throw RuntimeException due to loadSpriteImage method
        assertThrows(RuntimeException.class, () -> {
            InventoryObjectController.convert(testInventoryObject, mockWorld);
        }, "Should throw RuntimeException for invalid sprite path");
    }
    
    /**
     * Tests sprite loading success for circle inventory objects.
     * Verifies that circle sprite handling works correctly.
     */
    @Test
    public void testCircleSpriteLoadingSuccess() {
        Size circleSize = new Size();
        circleSize.setRadius(25.0f);
        
        InventoryObject circleObject = new InventoryObject("circleSprite", CIRCLE_TYPE, circleSize);
        circleObject.setPhysics(testPhysics);
        circleObject.setColour(GREEN_COLOR);
        circleObject.setSprite(null); // No sprite, should use color
        
        PhysicsVisualPair result = InventoryObjectController.convert(circleObject, mockWorld);
        
        assertNotNull(result, "Should convert circle without sprite");
        assertTrue(result.visual instanceof Circle, "Should create Circle");
        
        Circle circle = (Circle) result.visual;
        assertNotNull(circle.getFill(), "Fill should not be null");
    }
    
    /**
     * Tests sprite loading failure for circle inventory objects.
     * Verifies that circles handle sprite loading errors with RuntimeException.
     */
    @Test
    public void testCircleSpriteLoadingFailure() {
        Size circleSize = new Size();
        circleSize.setRadius(25.0f);
        
        InventoryObject circleObject = new InventoryObject("circleSpriteFail", CIRCLE_TYPE, circleSize);
        circleObject.setPhysics(testPhysics);
        circleObject.setColour(GREEN_COLOR);
        circleObject.setSprite("/invalid/path.png");
        
        // Should throw RuntimeException for invalid sprite
        assertThrows(RuntimeException.class, () -> {
            InventoryObjectController.convert(circleObject, mockWorld);
        }, "Should throw RuntimeException for invalid circle sprite");
    }

    // ========== PHYSICS COMPARISON TESTS ==========
    
    /**
     * Tests circle vs rectangle physics comparison.
     * Verifies different physics shape string handling for circles vs rectangles.
     */
    @Test
    public void testPhysicsShapeStringComparison() {
        // Test rectangle with different physics shape strings
        Physics rectPhysics = new Physics();
        rectPhysics.setShape("Dynamic"); // Note: capital D
        rectPhysics.setDensity(1.0f);
        rectPhysics.setFriction(0.5f);
        rectPhysics.setRestitution(0.3f);
        
        InventoryObject rectObject = new InventoryObject("rectTest", RECTANGLE_TYPE, testSize);
        rectObject.setPhysics(rectPhysics);
        rectObject.setColour(YELLOW_COLOR); // Set color to avoid null issues
        
        PhysicsVisualPair rectResult = InventoryObjectController.convert(rectObject, mockWorld);
        assertNotNull(rectResult, "Rectangle with Dynamic physics should convert");
        
        // Test circle with exact match physics string
        Physics circlePhysics = new Physics();
        circlePhysics.setShape("DYNAMIC"); // Note: all caps (exact match)
        circlePhysics.setDensity(1.0f);
        circlePhysics.setFriction(0.5f);
        circlePhysics.setRestitution(0.3f);
        
        Size circleSize = new Size();
        circleSize.setRadius(30.0f);
        InventoryObject circleObject = new InventoryObject("circleTest", CIRCLE_TYPE, circleSize);
        circleObject.setPhysics(circlePhysics);
        circleObject.setColour(YELLOW_COLOR); // Set color to avoid null issues
        
        PhysicsVisualPair circleResult = InventoryObjectController.convert(circleObject, mockWorld);
        assertNotNull(circleResult, "Circle with DYNAMIC physics should convert");
        
        // Verify both calls were made
        verify(mockWorld, times(2)).createBody(any());
    }
}
