package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive test suite for the BoundsValidator class.
 * Tests all boundary validation methods and edge cases without JavaFX dependencies.
 * 
 * The BoundsValidator is a utility class that validates whether game objects
 * are positioned within the simulation space boundaries. It handles both
 * rectangular and circular objects, accounting for rotation.
 */
public class TestBoundsValidator {
    
    private static final double SIMULATION_WIDTH = 800.0;
    private static final double SIMULATION_HEIGHT = 600.0;
    
    // Test constants to avoid PMD duplicate literals
    private static final String TEST_OBJECT_NAME = "testObject";
    private static final String BALL_NAME = "ball";
    private static final String NULL_OBJECT = "nullObject";
    private static final String ALL_OBJECTS_POSITIONED_CORRECTLY = "All objects are positioned correctly";
    
    private JsonStateService.SimulationState mockState;
    private List<GameObject> testObjects;
    
    @BeforeEach
    public void setUp() {
        mockState = Mockito.mock(JsonStateService.SimulationState.class);
        testObjects = new ArrayList<>();
        Mockito.when(mockState.getDroppedObjects()).thenReturn(testObjects);
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Creates a test GameObject with specified parameters.
     */
    private GameObject createTestObject(String name, float x, float y, float width, float height) {
        GameObject obj = new GameObject(name, "rectangle", new Position(x, y), new Size(width, height));
        obj.setPhysics(new Physics(1.0f, 0.4f, 0.1f, "dynamic"));
        obj.setColour("BLACK");
        return obj;
    }
    
    /**
     * Creates a test circular GameObject.
     */
    private GameObject createCircularObject(String name, float x, float y, float radius) {
        GameObject obj = new GameObject(name, "circle", new Position(x, y), new Size(radius));
        obj.setPhysics(new Physics(1.0f, 0.4f, 0.1f, "dynamic"));
        obj.setColour("RED");
        return obj;
    }
    
    /**
     * Creates a rotated test GameObject.
     */
    private GameObject createRotatedObject(String name, float x, float y, float width, float height, float angle) {
        GameObject obj = createTestObject(name, x, y, width, height);
        obj.setAngle(angle);
        return obj;
    }
    
    // ========== Valid Objects Tests ==========
    
    /**
     * Tests validation with empty object list.
     */
    @Test
    public void testValidateObjectBoundsEmptyList() {
        // Empty list should be valid
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with null object list.
     */
    @Test
    public void testValidateObjectBoundsNullList() {
        Mockito.when(mockState.getDroppedObjects()).thenReturn(null);
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with objects well within bounds.
     */
    @Test
    public void testValidateObjectBoundsValidObjects() {
        testObjects.add(createTestObject(TEST_OBJECT_NAME, 100, 100, 50, 50));
        testObjects.add(createTestObject(BALL_NAME, 200, 200, 30, 30));
        testObjects.add(createCircularObject("circle", 300, 300, 25));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with objects at simulation boundaries (valid).
     */
    @Test
    public void testValidateObjectBoundsAtBoundaries() {
        // Object at top-left corner
        testObjects.add(createTestObject("topLeft", 25, 25, 50, 50));
        
        // Object at bottom-right corner
        testObjects.add(createTestObject("bottomRight", 775, 575, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    // ========== Invalid Objects Tests ==========
    
    /**
     * Tests validation with object positioned too far left.
     */
    @Test
    public void testValidateObjectBoundsTooFarLeft() {
        testObjects.add(createTestObject("leftObject", -10, 100, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertAll("Message should contain specific boundary violation info",
            () -> assertTrue(result.getMessage().contains("leftObject")),
            () -> assertTrue(result.getMessage().contains("too far left")),
            () -> assertTrue(result.getMessage().contains("(-10, 100)"))
        );
    }
    
    /**
     * Tests validation with object positioned too far right.
     */
    @Test
    public void testValidateObjectBoundsTooFarRight() {
        testObjects.add(createTestObject("rightObject", 790, 100, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertAll("Message should contain specific boundary violation info",
            () -> assertTrue(result.getMessage().contains("rightObject")),
            () -> assertTrue(result.getMessage().contains("too far right")),
            () -> assertTrue(result.getMessage().contains("(790, 100)"))
        );
    }
    
    /**
     * Tests validation with object positioned too far up.
     */
    @Test
    public void testValidateObjectBoundsTooFarUp() {
        testObjects.add(createTestObject("upObject", 100, -10, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertAll("Message should contain specific boundary violation info",
            () -> assertTrue(result.getMessage().contains("upObject")),
            () -> assertTrue(result.getMessage().contains("too far up")),
            () -> assertTrue(result.getMessage().contains("(100, -10)"))
        );
    }
    
    /**
     * Tests validation with object positioned too far down.
     */
    @Test
    public void testValidateObjectBoundsTooFarDown() {
        testObjects.add(createTestObject("downObject", 100, 590, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertAll("Message should contain specific boundary violation info",
            () -> assertTrue(result.getMessage().contains("downObject")),
            () -> assertTrue(result.getMessage().contains("too far down")),
            () -> assertTrue(result.getMessage().contains("(100, 590)"))
        );
    }
    
    /**
     * Tests validation with object violating multiple boundaries.
     */
    @Test
    public void testValidateObjectBoundsMultipleBoundaryViolations() {
        testObjects.add(createTestObject("cornerObject", -10, -10, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertAll("Message should contain multiple boundary violations",
            () -> assertTrue(result.getMessage().contains("cornerObject")),
            () -> assertTrue(result.getMessage().contains("too far left")),
            () -> assertTrue(result.getMessage().contains("too far up")),
            () -> assertTrue(result.getMessage().contains("and"))
        );
    }
    
    // ========== Circular Objects Tests ==========
    
    /**
     * Tests validation with circular object within bounds.
     */
    @Test
    public void testValidateCircularObjectWithinBounds() {
        testObjects.add(createCircularObject("validCircle", 100, 100, 25));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with circular object outside bounds.
     */
    @Test
    public void testValidateCircularObjectOutsideBounds() {
        // Circle with radius 30 centered at (15, 15) - extends beyond bounds
        testObjects.add(createCircularObject("invalidCircle", 15, 15, 30));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertAll("Message should contain boundary violation info for circle",
            () -> assertTrue(result.getMessage().contains("invalidCircle")),
            () -> assertTrue(result.getMessage().contains("(15, 15)"))
        );
    }
    
    /**
     * Tests validation with large circular object.
     */
    @Test
    public void testValidateLargeCircularObject() {
        testObjects.add(createCircularObject("largeCircle", 400, 300, 150));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    // ========== Rotated Objects Tests ==========
    
    /**
     * Tests validation with rotated object within bounds.
     */
    @Test
    public void testValidateRotatedObjectWithinBounds() {
        testObjects.add(createRotatedObject("rotatedValid", 400, 300, 100, 50, 45.0f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with rotated object outside bounds.
     */
    @Test
    public void testValidateRotatedObjectOutsideBounds() {
        // Large rotated object near edge that extends beyond bounds when rotated
        testObjects.add(createRotatedObject("rotatedInvalid", 750, 100, 100, 50, 45.0f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertAll("Message should contain rotation info and boundary violation",
            () -> assertTrue(result.getMessage().contains("rotatedInvalid")),
            () -> assertTrue(result.getMessage().contains("(750, 100)")),
            () -> assertTrue(result.getMessage().contains("(rotated 45°)"))
        );
    }
    
    /**
     * Tests validation with various rotation angles.
     */
    @Test
    public void testValidateObjectsWithVariousRotations() {
        testObjects.add(createRotatedObject("rot90", 400, 300, 60, 40, 90.0f));
        testObjects.add(createRotatedObject("rot180", 400, 300, 60, 40, 180.0f));
        testObjects.add(createRotatedObject("rot270", 400, 300, 60, 40, 270.0f));
        testObjects.add(createRotatedObject("rotNeg", 400, 300, 60, 40, -45.0f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests that small rotation angles are not mentioned in error messages.
     */
    @Test
    public void testSmallRotationNotMentioned() {
        testObjects.add(createRotatedObject("smallRot", -10, 100, 50, 50, 0.05f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertFalse(result.getMessage().contains("rotated"),
            "Small rotation should not be mentioned in error message");
    }
    
    // ========== Special Cases Tests ==========
    
    /**
     * Tests validation with object having null position.
     */
    @Test
    public void testValidateObjectWithNullPosition() {
        GameObject objWithNullPos = new GameObject(NULL_OBJECT, "rectangle", null, new Size(50, 50));
        objWithNullPos.setPhysics(new Physics(1.0f, 0.4f, 0.1f, "dynamic"));
        objWithNullPos.setColour("BLACK");
        testObjects.add(objWithNullPos);
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with object having null size.
     */
    @Test
    public void testValidateObjectWithNullSize() {
        GameObject objWithNullSize = new GameObject("nullSize", "rectangle", new Position(100, 100), null);
        objWithNullSize.setPhysics(new Physics(1.0f, 0.4f, 0.1f, "dynamic"));
        objWithNullSize.setColour("BLACK");
        testObjects.add(objWithNullSize);
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with zero-sized object.
     */
    @Test
    public void testValidateZeroSizedObject() {
        testObjects.add(createTestObject("zeroSize", 100, 100, 0, 0));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with very large object.
     */
    @Test
    public void testValidateVeryLargeObject() {
        testObjects.add(createTestObject("largeObject", 400, 300, 1000, 800));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("largeObject"));
    }
    
    /**
     * Tests validation with object having null name.
     */
    @Test
    public void testValidateObjectWithNullName() {
        GameObject objWithNullName = createTestObject(null, -10, 100, 50, 50);
        testObjects.add(objWithNullName);
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("object"),
            "Should use 'object' as fallback when name is null");
    }
    
    // ========== Edge Cases and Boundary Tests ==========
    
    /**
     * Tests validation with objects at exact boundary positions.
     */
    @Test
    public void testValidateObjectsAtExactBoundaries() {
        // Object with left edge at x=0
        testObjects.add(createTestObject("leftEdge", 25, 100, 50, 50));
        
        // Object with right edge at simulation width
        testObjects.add(createTestObject("rightEdge", 775, 100, 50, 50));
        
        // Object with top edge at y=0
        testObjects.add(createTestObject("topEdge", 100, 25, 50, 50));
        
        // Object with bottom edge at simulation height
        testObjects.add(createTestObject("bottomEdge", 100, 575, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with objects exactly at boundary violations.
     */
    @Test
    public void testValidateObjectsExactlyAtBoundaryViolations() {
        // Object with left edge exactly at x=-1 (just outside)
        testObjects.add(createTestObject("justOutside", 24, 100, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("justOutside"));
    }
    
    /**
     * Tests validation with mixed valid and invalid objects.
     */
    @Test
    public void testValidateMixedValidAndInvalidObjects() {
        testObjects.add(createTestObject("valid1", 100, 100, 50, 50));
        testObjects.add(createTestObject("invalid", -10, 100, 50, 50)); // This should fail
        testObjects.add(createTestObject("valid2", 200, 200, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("invalid"));
    }
    
    /**
     * Tests validation stops at first invalid object.
     */
    @Test
    public void testValidationStopsAtFirstInvalidObject() {
        testObjects.add(createTestObject("valid", 100, 100, 50, 50));
        testObjects.add(createTestObject("firstInvalid", -10, 100, 50, 50));
        testObjects.add(createTestObject("secondInvalid", 900, 100, 50, 50));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("firstInvalid"));
        assertFalse(result.getMessage().contains("secondInvalid"),
            "Should stop at first invalid object");
    }
    
    // ========== Precision and Floating Point Tests ==========
    
    /**
     * Tests validation with floating point precision edge cases.
     */
    @Test
    public void testValidateFloatingPointPrecision() {
        // Object positioned with high precision coordinates
        testObjects.add(createTestObject("precise", 100.12345f, 200.67890f, 50.5f, 30.3f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with very small dimensions.
     */
    @Test
    public void testValidateVerySmallDimensions() {
        testObjects.add(createTestObject("tiny", 100, 100, 0.1f, 0.1f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with negative dimensions (edge case).
     */
    @Test
    public void testValidateNegativeDimensions() {
        testObjects.add(createTestObject("negative", 100, 100, -10, -10));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        // Should still validate as the algorithm uses absolute positioning
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    // ========== Simulation Space Dimension Tests ==========
    
    /**
     * Tests validation with very small simulation space.
     */
    @Test
    public void testValidateSmallSimulationSpace() {
        testObjects.add(createTestObject("tooLarge", 5, 5, 20, 20));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, 10.0, 10.0);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("tooLarge"));
    }
    
    /**
     * Tests validation with very large simulation space.
     */
    @Test
    public void testValidateLargeSimulationSpace() {
        testObjects.add(createTestObject("smallInLarge", 1000, 1000, 100, 100));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, 2000.0, 2000.0);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with zero simulation space dimensions.
     */
    @Test
    public void testValidateZeroSimulationSpace() {
        testObjects.add(createTestObject("anyObject", 0, 0, 10, 10));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, 0.0, 0.0);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("anyObject"));
    }
    
    // ========== Complex Rotation Scenarios ==========
    
    /**
     * Tests validation with 360-degree rotation.
     */
    @Test
    public void testValidateFullRotation() {
        testObjects.add(createRotatedObject("fullRotation", 400, 300, 60, 40, 360.0f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertTrue(result.isValid());
        assertEquals(ALL_OBJECTS_POSITIONED_CORRECTLY, result.getMessage());
    }
    
    /**
     * Tests validation with multiple rotation boundary scenarios.
     */
    @Test
    public void testValidateRotationBoundaryScenarios() {
        // Test object that fits when not rotated but exceeds bounds when rotated
        testObjects.add(createRotatedObject("rotationTest", 760, 300, 80, 20, 90.0f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        assertAll("Should handle rotated boundary violation",
            () -> assertTrue(result.getMessage().contains("rotationTest")),
            () -> assertTrue(result.getMessage().contains("(rotated 90°)"))
        );
    }
    
    // ========== Error Message Format Tests ==========
    
    /**
     * Tests that error messages contain all required information.
     */
    @Test
    public void testErrorMessageFormat() {
        testObjects.add(createRotatedObject("testMessage", -5, -10, 30, 20, 45.0f));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        String message = result.getMessage();
        
        assertAll("Error message should contain all required elements",
            () -> assertTrue(message.contains("testMessage"), "Should contain object name"),
            () -> assertTrue(message.contains("(-5, -10)"), "Should contain position"),
            () -> assertTrue(message.contains("(rotated 45°)"), "Should contain rotation info"),
            () -> assertTrue(message.contains("too far left"), "Should contain left boundary violation"),
            () -> assertTrue(message.contains("too far up"), "Should contain up boundary violation"),
            () -> assertTrue(message.contains("and"), "Should connect multiple violations"),
            () -> assertTrue(message.contains("not be visible"), "Should explain consequence"),
            () -> assertTrue(message.contains("closer to the center"), "Should provide solution")
        );
    }
    
    /**
     * Tests error message formatting for circular objects.
     */
    @Test
    public void testCircularObjectErrorMessageFormat() {
        testObjects.add(createCircularObject("circleTest", 10, 10, 20));
        
        JsonStateService.ValidationResult result = 
            BoundsValidator.validateObjectBounds(mockState, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        assertFalse(result.isValid());
        String message = result.getMessage();
        
        assertAll("Circular object error message should be properly formatted",
            () -> assertTrue(message.contains("circleTest")),
            () -> assertTrue(message.contains("(10, 10)")),
            () -> assertFalse(message.contains("rotated"), "Circles don't show rotation info")
        );
    }
}
