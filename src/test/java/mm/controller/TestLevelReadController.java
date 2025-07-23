package mm.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import com.fasterxml.jackson.databind.ObjectMapper;

import mm.model.GameObject;
import mm.model.InventoryObject;
import mm.model.Level;
import mm.model.Position;
import mm.model.Size;

/**
 * JavaFX-less unit tests for LevelReadController.
 * Tests the controller's ability to read and deserialize Level objects from JSON.
 */
public class TestLevelReadController {

    private static final String VALID_JSON = "{\n" +
        "    \"levelObjects\": [\n" +
        "        {\n" +
        "            \"name\": \"testBall\",\n" +
        "            \"type\": \"circle\",\n" +
        "            \"position\": {\"x\": 100.0, \"y\": 200.0},\n" +
        "            \"size\": {\"width\": 50.0, \"height\": 50.0},\n" +
        "            \"color\": \"#FF0000\",\n" +
        "            \"angle\": 0.0,\n" +
        "            \"sprite\": \"ball.png\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"inventoryObjects\": [\n" +
        "        {\n" +
        "            \"name\": \"testInventory\",\n" +
        "            \"count\": 5,\n" +
        "            \"sprite\": \"inventory.png\"\n" +
        "        }\n" +
        "    ]\n" +
        "}";

    private static final String INVALID_JSON = "{ invalid json }";
    private static final String EMPTY_JSON = "{}";

    @Nested
    /**
     * Tests for the LevelReadController constructor with various input scenarios.
     */
    class ConstructorTests {
        
        @Test
        void testConstructorWithValidInputStream() {
            InputStream inputStream = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
            
            LevelReadController controller = new LevelReadController(inputStream);
            
            assertNotNull(controller);
            assertNotNull(controller.getMapper());
            assertTrue(controller.getMapper() instanceof ObjectMapper);
        }

        @Test
        void testConstructorWithNullInputStream() {
            // Should not throw exception during construction
            assertDoesNotThrow(() -> new LevelReadController(null));
        }
    }

    @Nested
    /**
     * Tests for the readFile method with various JSON input scenarios.
     */
    class ReadFileTests {
        
        @Test
        void testReadFileWithValidJson() {
            InputStream inputStream = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
            LevelReadController controller = new LevelReadController(inputStream);
            
            Level result = controller.readFile();
            
            assertNotNull(result);
            assertNotNull(result.getLevelObjects());
            assertNotNull(result.getInventoryObjects());
            assertEquals(1, result.getLevelObjects().size());
            assertEquals(1, result.getInventoryObjects().size());
        }

        @Test
        void testReadFileGameObjectDetails() {
            InputStream inputStream = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
            LevelReadController controller = new LevelReadController(inputStream);
            
            Level result = controller.readFile();
            GameObject gameObject = result.getLevelObjects().get(0);
            
            assertEquals("testBall", gameObject.getName());
            assertEquals("circle", gameObject.getType());
            assertEquals(0.0f, gameObject.getAngle());
            assertEquals("ball.png", gameObject.getSprite());
        }

        @Test
        void testReadFileGameObjectPosition() {
            InputStream inputStream = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
            LevelReadController controller = new LevelReadController(inputStream);
            
            Level result = controller.readFile();
            GameObject gameObject = result.getLevelObjects().get(0);
            Position position = gameObject.getPosition();
            
            assertEquals(100.0f, position.getX());
            assertEquals(200.0f, position.getY());
        }

        @Test
        void testReadFileGameObjectSize() {
            InputStream inputStream = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
            LevelReadController controller = new LevelReadController(inputStream);
            
            Level result = controller.readFile();
            GameObject gameObject = result.getLevelObjects().get(0);
            Size size = gameObject.getSize();
            
            assertEquals(50.0f, size.getWidth());
            assertEquals(50.0f, size.getHeight());
        }

        @Test
        void testReadFileInventoryObjectDetails() {
            InputStream inputStream = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
            LevelReadController controller = new LevelReadController(inputStream);
            
            Level result = controller.readFile();
            InventoryObject inventoryObject = result.getInventoryObjects().get(0);
            
            assertEquals("testInventory", inventoryObject.getName());
            assertEquals(5, inventoryObject.getCount());
            assertEquals("inventory.png", inventoryObject.getSprite());
        }

        @Test
        void testReadFileWithInvalidJson() {
            InputStream inputStream = new ByteArrayInputStream(INVALID_JSON.getBytes(StandardCharsets.UTF_8));
            LevelReadController controller = new LevelReadController(inputStream);
            
            Level result = controller.readFile();
            
            assertNull(result);
        }

        @Test
        void testReadFileWithEmptyJson() {
            InputStream inputStream = new ByteArrayInputStream(EMPTY_JSON.getBytes(StandardCharsets.UTF_8));
            LevelReadController controller = new LevelReadController(inputStream);
            
            Level result = controller.readFile();
            
            assertNotNull(result);
            // Empty JSON should create a Level with null lists
            assertNull(result.getLevelObjects());
            assertNull(result.getInventoryObjects());
        }

        @Test
        void testReadFileWithNullInputStream() {
            LevelReadController controller = new LevelReadController(null);
            
            Level result = controller.readFile();
            
            assertNull(result);
        }

        @Test
        void testReadFileWithEmptyInputStream() {
            InputStream inputStream = new ByteArrayInputStream(new byte[0]);
            LevelReadController controller = new LevelReadController(inputStream);
            
            Level result = controller.readFile();
            
            assertNull(result);
        }
    }

    @Nested
    /**
     * Tests for the getMapper method functionality.
     */
    class GetMapperTests {
        
        @Test
        void testGetMapper() {
            InputStream inputStream = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
            LevelReadController controller = new LevelReadController(inputStream);
            
            ObjectMapper mapper = controller.getMapper();
            
            assertNotNull(mapper);
            assertTrue(mapper instanceof ObjectMapper);
        }
    }
}
