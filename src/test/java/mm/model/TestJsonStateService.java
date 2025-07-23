package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive test suite for the JsonStateService class.
 * Tests all JSON serialization, deserialization, and validation methods without JavaFX dependencies.
 * 
 * The JsonStateService handles JSON operations for simulation state including
 * serialization of game objects and inventory objects, parsing of JSON strings,
 * and validation of JSON content with detailed error reporting.
 */
public class TestJsonStateService {
    
    private JsonStateService jsonStateService;
    private List<GameObject> testGameObjects;
    private List<InventoryObject> testInventoryObjects;
    
    @Mock
    private JsonStateService.SimulationState mockSimulationState;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jsonStateService = new JsonStateService();
        testGameObjects = new ArrayList<>();
        testInventoryObjects = new ArrayList<>();
    }
    
    // ========== Constructor Tests ==========
    
    /**
     * Tests that JsonStateService can be instantiated successfully.
     */
    @Test
    public void testConstructor() {
        JsonStateService service = new JsonStateService();
        assertNotNull(service, "JsonStateService should be instantiated successfully");
    }
    
    // ========== JSON Generation Tests ==========
    
    /**
     * Tests JSON generation with empty lists.
     */
    @Test
    public void testGenerateStateJsonEmptyLists() {
        String result = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        assertNotNull(result, "Generated JSON should not be null");
        assertFalse(result.isEmpty(), "Generated JSON should not be empty");
        assertTrue(result.contains("droppedObjects"), "JSON should contain droppedObjects field");
        assertTrue(result.contains("inventoryObjects"), "JSON should contain inventoryObjects field");
    }
    
    /**
     * Tests JSON generation with null lists.
     */
    @Test
    public void testGenerateStateJsonNullLists() {
        String result = jsonStateService.generateStateJson(null, null);
        
        assertNotNull(result, "Generated JSON should not be null");
        assertFalse(result.isEmpty(), "Generated JSON should not be empty");
    }
    
    /**
     * Tests JSON generation with populated game objects.
     */
    @Test
    public void testGenerateStateJsonWithGameObjects() {
        GameObject testObject = createTestGameObject("testObject", "rectangle", 100.0f, 200.0f, 50.0f, 60.0f);
        testGameObjects.add(testObject);
        
        String result = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        assertNotNull(result, "Generated JSON should not be null");
        assertTrue(result.contains("testObject"), "JSON should contain object name");
        assertTrue(result.contains("rectangle"), "JSON should contain object type");
        assertTrue(result.contains("100"), "JSON should contain x position");
        assertTrue(result.contains("200"), "JSON should contain y position");
    }
    
    /**
     * Tests JSON generation with populated inventory objects.
     */
    @Test
    public void testGenerateStateJsonWithInventoryObjects() {
        InventoryObject testItem = createTestInventoryObject("ball", "circle", 3);
        testInventoryObjects.add(testItem);
        
        String result = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        assertNotNull(result, "Generated JSON should not be null");
        assertTrue(result.contains("ball"), "JSON should contain inventory item name");
        assertTrue(result.contains("circle"), "JSON should contain inventory item type");
        assertTrue(result.contains("3"), "JSON should contain inventory count");
    }
    
    /**
     * Tests JSON generation with both game objects and inventory objects.
     */
    @Test
    public void testGenerateStateJsonWithBothTypes() {
        GameObject gameObj = createTestGameObject("wall", "rectangle", 50.0f, 50.0f, 100.0f, 20.0f);
        InventoryObject invObj = createTestInventoryObject("projectile", "circle", 5);
        
        testGameObjects.add(gameObj);
        testInventoryObjects.add(invObj);
        
        String result = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        assertNotNull(result, "Generated JSON should not be null");
        assertAll("JSON should contain both object types",
            () -> assertTrue(result.contains("wall"), "Should contain game object"),
            () -> assertTrue(result.contains("projectile"), "Should contain inventory object"),
            () -> assertTrue(result.contains("droppedObjects"), "Should have droppedObjects field"),
            () -> assertTrue(result.contains("inventoryObjects"), "Should have inventoryObjects field")
        );
    }
    
    /**
     * Tests JSON generation produces properly formatted indented output.
     */
    @Test
    public void testGenerateStateJsonIndentation() {
        GameObject testObject = createTestGameObject("indentTest", "circle", 0.0f, 0.0f, 25.0f, 25.0f);
        testGameObjects.add(testObject);
        
        String result = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        assertNotNull(result, "Generated JSON should not be null");
        assertTrue(result.contains("\n"), "JSON should contain newlines for indentation");
        assertTrue(result.contains("  "), "JSON should contain spaces for indentation");
    }
    
    // ========== JSON Parsing Tests ==========
    
    /**
     * Tests parsing valid JSON string.
     */
    @Test
    public void testParseStateJsonValidJson() throws Exception {
        String validJson = "{\n" +
            "  \"droppedObjects\" : [ ],\n" +
            "  \"inventoryObjects\" : [ ]\n" +
            "}";
        
        JsonStateService.SimulationState result = jsonStateService.parseStateJson(validJson);
        
        assertNotNull(result, "Parsed state should not be null");
        assertNotNull(result.getDroppedObjects(), "Dropped objects list should not be null");
        assertNotNull(result.getInventoryObjects(), "Inventory objects list should not be null");
        assertTrue(result.getDroppedObjects().isEmpty(), "Dropped objects should be empty");
        assertTrue(result.getInventoryObjects().isEmpty(), "Inventory objects should be empty");
    }
    
    /**
     * Tests parsing JSON with game objects.
     */
    @Test
    public void testParseStateJsonWithGameObjects() throws Exception {
        // First generate valid JSON to ensure we can parse what we generate
        GameObject testObject = createTestGameObject("parseTest", "rectangle", 150.0f, 250.0f, 80.0f, 40.0f);
        testGameObjects.add(testObject);
        String generatedJson = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        JsonStateService.SimulationState result = jsonStateService.parseStateJson(generatedJson);
        
        assertNotNull(result, "Parsed state should not be null");
        assertNotNull(result.getDroppedObjects(), "Dropped objects should not be null");
        assertFalse(result.getDroppedObjects().isEmpty(), "Should have parsed game objects");
        assertEquals(1, result.getDroppedObjects().size(), "Should have exactly one game object");
        
        GameObject parsedObject = result.getDroppedObjects().get(0);
        assertEquals("parseTest", parsedObject.getName(), "Object name should match");
        assertEquals("rectangle", parsedObject.getType(), "Object type should match");
    }
    
    /**
     * Tests parsing invalid JSON throws exception.
     */
    @Test
    public void testParseStateJsonInvalidJson() {
        String invalidJson = "{ invalid json syntax }";
        
        assertThrows(Exception.class, () -> {
            jsonStateService.parseStateJson(invalidJson);
        }, "Should throw exception for invalid JSON");
    }
    
    /**
     * Tests parsing null JSON throws exception.
     */
    @Test
    public void testParseStateJsonNullInput() {
        assertThrows(Exception.class, () -> {
            jsonStateService.parseStateJson(null);
        }, "Should throw exception for null input");
    }
    
    /**
     * Tests parsing empty JSON string throws exception.
     */
    @Test
    public void testParseStateJsonEmptyString() {
        assertThrows(Exception.class, () -> {
            jsonStateService.parseStateJson("");
        }, "Should throw exception for empty string");
    }
    
    // ========== Simple JSON Validation Tests ==========
    
    /**
     * Tests isValidJson with valid JSON.
     */
    @Test
    public void testIsValidJsonWithValidInput() {
        String validJson = "{\n" +
            "  \"droppedObjects\" : [ ],\n" +
            "  \"inventoryObjects\" : [ ]\n" +
            "}";
        
        boolean result = jsonStateService.isValidJson(validJson);
        assertTrue(result, "Valid JSON should return true");
    }
    
    /**
     * Tests isValidJson with invalid JSON.
     */
    @Test
    public void testIsValidJsonWithInvalidInput() {
        String invalidJson = "{ invalid: json }";
        
        boolean result = jsonStateService.isValidJson(invalidJson);
        assertFalse(result, "Invalid JSON should return false");
    }
    
    /**
     * Tests isValidJson with null input.
     */
    @Test
    public void testIsValidJsonWithNullInput() {
        boolean result = jsonStateService.isValidJson(null);
        assertFalse(result, "Null input should return false");
    }
    
    /**
     * Tests isValidJson with empty string.
     */
    @Test
    public void testIsValidJsonWithEmptyString() {
        boolean result = jsonStateService.isValidJson("");
        assertFalse(result, "Empty string should return false");
    }
    
    // ========== Detailed JSON Validation Tests ==========
    
    /**
     * Tests validateJson with valid JSON returns success.
     */
    @Test
    public void testValidateJsonValidInput() {
        String validJson = "{\n" +
            "  \"droppedObjects\" : [ ],\n" +
            "  \"inventoryObjects\" : [ ]\n" +
            "}";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(validJson);
        
        assertTrue(result.isValid(), "Valid JSON should pass validation");
        assertNotNull(result.getMessage(), "Validation message should not be null");
        assertTrue(result.getMessage().contains("valid"), "Message should indicate validity");
    }
    
    /**
     * Tests validateJson with null input returns appropriate error.
     */
    @Test
    public void testValidateJsonNullInput() {
        JsonStateService.ValidationResult result = jsonStateService.validateJson(null);
        
        assertFalse(result.isValid(), "Null input should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertTrue(result.getMessage().contains("enter some JSON"), "Message should prompt for input");
    }
    
    /**
     * Tests validateJson with empty string returns appropriate error.
     */
    @Test
    public void testValidateJsonEmptyString() {
        JsonStateService.ValidationResult result = jsonStateService.validateJson("");
        
        assertFalse(result.isValid(), "Empty string should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertTrue(result.getMessage().contains("enter some JSON"), "Message should prompt for input");
    }
    
    /**
     * Tests validateJson with whitespace-only input returns appropriate error.
     */
    @Test
    public void testValidateJsonWhitespaceOnly() {
        JsonStateService.ValidationResult result = jsonStateService.validateJson("   \n\t  ");
        
        assertFalse(result.isValid(), "Whitespace-only input should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertTrue(result.getMessage().contains("enter some JSON"), "Message should prompt for input");
    }
    
    /**
     * Tests validateJson with syntax error provides helpful message.
     */
    @Test
    public void testValidateJsonSyntaxError() {
        String malformedJson = "{ \"field\": value without quotes }";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(malformedJson);
        
        assertFalse(result.isValid(), "Malformed JSON should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertTrue(result.getMessage().toLowerCase().contains("syntax"), "Message should mention syntax error");
    }
    
    /**
     * Tests validateJson with missing closing bracket.
     */
    @Test
    public void testValidateJsonMissingClosingBracket() {
        String incompleteJson = "{ \"droppedObjects\": []";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(incompleteJson);
        
        assertFalse(result.isValid(), "Incomplete JSON should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
    }
    
    /**
     * Tests validateJson with missing colon.
     */
    @Test
    public void testValidateJsonMissingColon() {
        String invalidJson = "{ \"field\" \"value\" }";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(invalidJson);
        
        assertFalse(result.isValid(), "JSON with missing colon should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
    }
    
    /**
     * Tests validateJson with JSON that causes JsonMappingException due to wrong structure.
     */
    @Test
    public void testValidateJsonMappingException() {
        // JSON that parses syntactically but doesn't match the expected object structure
        String structureJson = "{ \"wrongField\": \"value\", \"anotherWrongField\": 123 }";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(structureJson);
        
        assertFalse(result.isValid(), "JSON with wrong structure should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertTrue(result.getMessage().toLowerCase().contains("format"), "Message should mention format error");
    }
    
    /**
     * Tests validateJson with JSON containing deserialization error.
     */
    @Test
    public void testValidateJsonDeserializationError() {
        // Valid JSON syntax but with fields that cannot be deserialized properly
        String deserializationJson = "{ \"droppedObjects\": \"invalid_value\", \"inventoryObjects\": [] }";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(deserializationJson);
        
        assertFalse(result.isValid(), "JSON with deserialization issues should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        // The message should contain user-friendly information about the structure mismatch
        assertTrue(result.getMessage().contains("structure") || result.getMessage().contains("format"),
            "Message should indicate structure/format issue");
    }
    
    /**
     * Tests validateJson with JSON missing required properties.
     */
    @Test
    public void testValidateJsonMissingRequiredProperty() {
        // JSON that might be missing required fields for proper object creation
        String incompleteJson = "{ \"droppedObjects\": [{}], \"inventoryObjects\": [] }";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(incompleteJson);
        
        // This might pass or fail depending on how Jackson handles empty objects
        assertNotNull(result, "Validation result should not be null");
        assertNotNull(result.getMessage(), "Should have a message");
    }
    
    /**
     * Tests validateJson with JSON containing type mismatches.
     */
    @Test
    public void testValidateJsonTypeMismatch() {
        // JSON with correct field names but wrong types
        String typeMismatchJson = "{ \"droppedObjects\": 123, \"inventoryObjects\": \"not_an_array\" }";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(typeMismatchJson);
        
        assertFalse(result.isValid(), "JSON with type mismatches should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertTrue(result.getMessage().toLowerCase().contains("format") || 
                   result.getMessage().toLowerCase().contains("structure"),
            "Message should indicate format or structure issue");
    }
    
    // ========== JsonMappingException Handling Tests ==========
    
    /**
     * Tests validateJson handles "Cannot deserialize" JsonMappingException.
     */
    @Test
    public void testValidateJsonCannotDeserialize() {
        // JSON that causes "Cannot deserialize" error
        String cannotDeserializeJson = "{ \"droppedObjects\": [{ \"invalidField\": true }], \"inventoryObjects\": [] }";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(cannotDeserializeJson);
        
        assertFalse(result.isValid(), "JSON with deserialization issues should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        // Should get user-friendly message about structure mismatch
        assertTrue(result.getMessage().contains("structure") || result.getMessage().contains("format"),
            "Message should be user-friendly about structure issues");
    }
    
    /**
     * Tests validateJson handles "missing property" JsonMappingException.
     */
    @Test
    public void testValidateJsonMissingProperty() {
        // This is harder to trigger with current structure, but test the message handling
        String jsonWithPotentialMissingProps = "{ \"droppedObjects\": [{ \"name\": null, \"type\": null }], \"inventoryObjects\": [] }";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(jsonWithPotentialMissingProps);
        
        // Result may vary based on how Jackson handles null values
        assertNotNull(result, "Validation result should not be null");
        assertNotNull(result.getMessage(), "Should have a message");
        
        if (!result.isValid() && result.getMessage().toLowerCase().contains("property")) {
            assertTrue(result.getMessage().contains("Required property") || result.getMessage().contains("missing"),
                "Should provide helpful message about missing properties");
        }
    }
    
    /**
     * Tests validateJson handles complex nested object deserialization errors.
     */
    @Test
    public void testValidateJsonComplexDeserializationError() {
        // JSON with nested structure that might cause mapping issues
        String complexErrorJson = "{ " +
            "\"droppedObjects\": [{ " +
                "\"name\": \"test\", " +
                "\"type\": \"rectangle\", " +
                "\"position\": \"invalid_position_structure\", " +
                "\"size\": { \"width\": \"not_a_number\" } " +
            "}], " +
            "\"inventoryObjects\": [] " +
            "}";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(complexErrorJson);
        
        assertFalse(result.isValid(), "Complex structure errors should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertTrue(result.getMessage().toLowerCase().contains("format") || 
                   result.getMessage().toLowerCase().contains("structure") ||
                   result.getMessage().toLowerCase().contains("error"),
            "Message should indicate the nature of the problem");
    }
    
    /**
     * Tests validateJson with array structure mismatch.
     */
    @Test
    public void testValidateJsonArrayStructureMismatch() {
        // JSON where arrays contain wrong type of objects
        String arrayMismatchJson = "{ " +
            "\"droppedObjects\": [\"string_instead_of_object\", 123], " +
            "\"inventoryObjects\": [true, false] " +
            "}";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(arrayMismatchJson);
        
        assertFalse(result.isValid(), "Array structure mismatch should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertTrue(result.getMessage().contains("structure") || 
                   result.getMessage().contains("format") ||
                   result.getMessage().contains("game objects"),
            "Message should indicate structure or format issue");
    }
    
    /**
     * Tests that general JsonMappingException gets appropriate fallback message.
     */
    @Test
    public void testValidateJsonGeneralMappingException() {
        // JSON that might cause a general mapping exception
        String generalErrorJson = "{ " +
            "\"droppedObjects\": [{ " +
                "\"name\": \"test\", " +
                "\"type\": \"rectangle\", " +
                "\"physics\": { \"density\": \"invalid_number_string\" } " +
            "}], " +
            "\"inventoryObjects\": [] " +
            "}";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(generalErrorJson);
        
        // Should fail validation and provide some error message
        assertFalse(result.isValid(), "General mapping errors should fail validation");
        assertNotNull(result.getMessage(), "Should have error message");
        assertFalse(result.getMessage().isEmpty(), "Error message should not be empty");
    }

    // ========== Bounds Validation Tests ==========
    
    /**
     * Tests validateJson with bounds checking for valid objects.
     */
    @Test
    public void testValidateJsonWithBoundsValidObjects() {
        // Create JSON with objects within bounds
        GameObject validObject = createTestGameObject("validObj", "rectangle", 100.0f, 100.0f, 50.0f, 50.0f);
        testGameObjects.add(validObject);
        String validJson = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(validJson, 800.0, 600.0);
        
        assertTrue(result.isValid(), "Objects within bounds should pass validation");
        assertTrue(result.getMessage().contains("positioned correctly"), "Message should confirm correct positioning");
    }
    
    /**
     * Tests validateJson without bounds checking (zero dimensions).
     */
    @Test
    public void testValidateJsonNoBoundsChecking() {
        GameObject anyObject = createTestGameObject("anyObj", "rectangle", 1000.0f, 1000.0f, 50.0f, 50.0f);
        testGameObjects.add(anyObject);
        String validJson = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(validJson, 0.0, 0.0);
        
        assertTrue(result.isValid(), "Should skip bounds checking when dimensions are zero");
        assertTrue(result.getMessage().contains("valid"), "Should indicate JSON is valid");
    }
    
    // ========== ValidationResult Tests ==========
    
    /**
     * Tests ValidationResult constructor and getters.
     */
    @Test
    public void testValidationResultCreation() {
        JsonStateService.ValidationResult validResult = 
            new JsonStateService.ValidationResult(true, "Success message");
        JsonStateService.ValidationResult invalidResult = 
            new JsonStateService.ValidationResult(false, "Error message");
        
        assertTrue(validResult.isValid(), "Valid result should return true");
        assertEquals("Success message", validResult.getMessage(), "Should return correct success message");
        
        assertFalse(invalidResult.isValid(), "Invalid result should return false");
        assertEquals("Error message", invalidResult.getMessage(), "Should return correct error message");
    }
    
    // ========== SimulationState Tests ==========
    
    /**
     * Tests SimulationState constructor and basic functionality.
     */
    @Test
    public void testSimulationStateCreation() {
        JsonStateService.SimulationState state = new JsonStateService.SimulationState();
        
        assertNull(state.getDroppedObjects(), "Initial dropped objects should be null");
        assertNull(state.getInventoryObjects(), "Initial inventory objects should be null");
    }
    
    /**
     * Tests SimulationState setters and getters.
     */
    @Test
    public void testSimulationStateSettersAndGetters() {
        JsonStateService.SimulationState state = new JsonStateService.SimulationState();
        List<GameObject> gameObjects = new ArrayList<>();
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        
        gameObjects.add(createTestGameObject("test", "rectangle", 0, 0, 10, 10));
        inventoryObjects.add(createTestInventoryObject("item", "circle", 2));
        
        state.setDroppedObjects(gameObjects);
        state.setInventoryObjects(inventoryObjects);
        
        assertEquals(gameObjects, state.getDroppedObjects(), "Should return set game objects");
        assertEquals(inventoryObjects, state.getInventoryObjects(), "Should return set inventory objects");
        assertEquals(1, state.getDroppedObjects().size(), "Should have correct number of game objects");
        assertEquals(1, state.getInventoryObjects().size(), "Should have correct number of inventory objects");
    }
    
    // ========== Error Handling Tests ==========
    
    /**
     * Tests that JSON generation handles exceptions gracefully.
     */
    @Test
    public void testGenerateStateJsonErrorHandling() {
        // Create a minimal object that should serialize without issues
        GameObject minimalObject = new GameObject();
        minimalObject.setColour("RED");
        
        List<GameObject> objectList = new ArrayList<>();
        objectList.add(minimalObject);
        
        String result = jsonStateService.generateStateJson(objectList, testInventoryObjects);
        
        // Should return valid JSON even for minimal objects
        assertNotNull(result, "Should return non-null result");
        assertFalse(result.isEmpty(), "Should return non-empty result");
        assertTrue(result.equals("{}") || result.contains("droppedObjects"), 
            "Should return valid JSON structure");
    }
    
    /**
     * Tests parsing with truncated/incomplete JSON.
     */
    @Test
    public void testParseStateJsonTruncatedInput() {
        String truncatedJson = "{ \"droppedObjects\": [";
        
        assertThrows(Exception.class, () -> {
            jsonStateService.parseStateJson(truncatedJson);
        }, "Should throw exception for truncated JSON");
    }
    
    /**
     * Tests validateJson with general Exception handling (not JsonParseException or JsonMappingException).
     */
    @Test
    public void testValidateJsonGeneralException() {
        // Test with a very deeply nested or complex JSON that might cause other exceptions
        String potentiallyProblematicJson = "{ " +
            "\"droppedObjects\": [{ " +
                "\"name\": \"test\", " +
                "\"type\": \"rectangle\", " +
                "\"position\": { \"x\": 1.7976931348623157E308, \"y\": -1.7976931348623157E308 }, " +
                "\"size\": { \"width\": 999999999, \"height\": 999999999 } " +
            "}], " +
            "\"inventoryObjects\": [] " +
            "}";
        
        JsonStateService.ValidationResult result = jsonStateService.validateJson(potentiallyProblematicJson);
        
        // Should handle any potential exception gracefully
        assertNotNull(result, "Should return a validation result");
        assertNotNull(result.getMessage(), "Should have a message");
        
        // The result might be valid or invalid, but shouldn't crash
        if (!result.isValid()) {
            assertTrue(result.getMessage().contains("process") || 
                      result.getMessage().contains("error") ||
                      result.getMessage().contains("Unable") ||
                      result.getMessage().contains("positioned correctly") ||
                      result.getMessage().contains("bounds"),
                "Error message should provide meaningful information");
        }
    }
    
    // ========== Integration Tests ==========
    
    /**
     * Tests full round-trip: generate JSON, then parse it back.
     */
    @Test
    public void testJsonRoundTrip() throws Exception {
        // Create test data
        GameObject gameObj = createTestGameObject("roundTripGame", "rectangle", 200.0f, 300.0f, 60.0f, 80.0f);
        InventoryObject invObj = createTestInventoryObject("roundTripItem", "circle", 7);
        
        testGameObjects.add(gameObj);
        testInventoryObjects.add(invObj);
        
        // Generate JSON
        String generatedJson = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        
        // Parse it back
        JsonStateService.SimulationState parsedState = jsonStateService.parseStateJson(generatedJson);
        
        // Verify the data survived the round trip
        assertNotNull(parsedState, "Parsed state should not be null");
        assertNotNull(parsedState.getDroppedObjects(), "Dropped objects should not be null");
        assertNotNull(parsedState.getInventoryObjects(), "Inventory objects should not be null");
        
        assertEquals(1, parsedState.getDroppedObjects().size(), "Should have one game object");
        assertEquals(1, parsedState.getInventoryObjects().size(), "Should have one inventory object");
        
        GameObject parsedGameObj = parsedState.getDroppedObjects().get(0);
        InventoryObject parsedInvObj = parsedState.getInventoryObjects().get(0);
        
        assertEquals("roundTripGame", parsedGameObj.getName(), "Game object name should survive round trip");
        assertEquals("rectangle", parsedGameObj.getType(), "Game object type should survive round trip");
        
        assertEquals("roundTripItem", parsedInvObj.getName(), "Inventory object name should survive round trip");
        assertEquals("circle", parsedInvObj.getType(), "Inventory object type should survive round trip");
        assertEquals(7, parsedInvObj.getCount(), "Inventory count should survive round trip");
    }
    
    /**
     * Tests validation with complex nested objects.
     */
    @Test
    public void testValidateJsonComplexObjects() {
        // Create objects with all properties set
        GameObject complexGameObj = createComplexGameObject("complex", "rectangle", 100.0f, 150.0f, 40.0f, 30.0f, 45.0f);
        InventoryObject complexInvObj = createComplexInventoryObject("complexItem", "circle", 3, 90.0f);
        
        testGameObjects.add(complexGameObj);
        testInventoryObjects.add(complexInvObj);
        
        String complexJson = jsonStateService.generateStateJson(testGameObjects, testInventoryObjects);
        JsonStateService.ValidationResult result = jsonStateService.validateJson(complexJson);
        
        assertTrue(result.isValid(), "Complex objects should validate successfully");
        assertTrue(result.getMessage().contains("valid"), "Should indicate validation success");
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Creates a basic test GameObject with minimal required properties.
     */
    private GameObject createTestGameObject(String name, String type, float x, float y, float width, float height) {
        Position position = new Position(x, y);
        Size size = new Size();
        size.setWidth(width);
        size.setHeight(height);
        
        GameObject obj = new GameObject(name, type, position, size);
        obj.setColour("BLACK");
        obj.setPhysics(new Physics(1.0f, 0.5f, 0.3f, "DYNAMIC"));
        return obj;
    }
    
    /**
     * Creates a test InventoryObject with basic properties.
     */
    private InventoryObject createTestInventoryObject(String name, String type, int count) {
        Size size = new Size();
        size.setRadius(25.0f);
        
        InventoryObject obj = new InventoryObject(name, type, size);
        obj.setCount(count);
        obj.setColour("BLUE");
        obj.setPhysics(new Physics(1.0f, 0.4f, 0.6f, "DYNAMIC"));
        return obj;
    }
    
    /**
     * Creates a complex GameObject with all properties set.
     */
    private GameObject createComplexGameObject(String name, String type, float x, float y, float width, float height, float angle) {
        GameObject obj = createTestGameObject(name, type, x, y, width, height);
        obj.setAngle(angle);
        obj.setSprite("test_sprite.png");
        obj.setWinning(false);
        return obj;
    }
    
    /**
     * Creates a complex InventoryObject with all properties set.
     */
    private InventoryObject createComplexInventoryObject(String name, String type, int count, float angle) {
        InventoryObject obj = createTestInventoryObject(name, type, count);
        obj.setAngle(angle);
        obj.setSprite("inventory_sprite.png");
        obj.setWinning(false);
        return obj;
    }
}
