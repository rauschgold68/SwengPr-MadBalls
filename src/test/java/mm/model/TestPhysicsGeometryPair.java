package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link PhysicsGeometryPair} class.
 * <p>
 * This test class verifies the correct behavior of PhysicsGeometryPair, which represents
 * the pairing of view-agnostic geometry data with JBox2D physics bodies. This class is
 * part of the model layer refactoring to remove direct JavaFX dependencies while
 * maintaining the connection between mathematical shape representations and physics simulation.
 * </p>
 * <p>
 * The tests cover various scenarios including:
 * </p>
 * <ul>
 * <li>Constructor behavior with different parameter combinations</li>
 * <li>Proper handling of null geometry and body parameters</li>
 * <li>Field access through both public fields and getter methods</li>
 * <li>Object creation robustness under edge conditions</li>
 * </ul>
 * 
 * @see PhysicsGeometryPair
 * @see GeometryData
 * @see org.jbox2d.dynamics.Body
 */
public class TestPhysicsGeometryPair {
    
    /** Test geometry data used for creating physics-geometry pairs. */
    private GeometryData testGeometry;
    /** JBox2D physics body used for testing physics integration. */
    private Body testBody;
    /** Physics world instance required for body creation. */
    private World testWorld;
    
    /**
     * Sets up test fixtures before each test method.
     * <p>
     * Creates a test geometry (CircleGeometry) and a minimal JBox2D physics
     * world with a body for testing the pairing functionality. This setup
     * provides realistic objects for testing the PhysicsGeometryPair behavior.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        testGeometry = new CircleGeometry(new Position(10.0f, 20.0f), 5.0);
        
        // Create a minimal physics world and body for testing
        testWorld = new World(new Vec2(0, 0));
        BodyDef bodyDef = new BodyDef();
        testBody = testWorld.createBody(bodyDef);
    }
    
    /**
     * Tests the constructor with both geometry and body parameters.
     * <p>
     * Verifies that when both parameters are provided, the PhysicsGeometryPair
     * correctly stores both references and makes them accessible through both
     * direct field access and getter methods. This represents the typical
     * use case where both components are available.
     * </p>
     */
    @Test
    public void testConstructorWithBothParameters() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(testGeometry, testBody);
        
        assertNotNull(pair);
        assertEquals(testGeometry, pair.getGeometry());
        assertEquals(testBody, pair.getBody());
        assertEquals(testGeometry, pair.geometry);
        assertEquals(testBody, pair.body);
    }
    
    /**
     * Tests the constructor with null geometry parameter.
     * <p>
     * Verifies that the pair can be created with a null geometry while
     * maintaining a valid physics body. This scenario might occur when
     * geometry data is not available or during certain initialization phases.
     * The constructor should handle this gracefully without throwing exceptions.
     * </p>
     */
    @Test
    public void testConstructorWithNullGeometry() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(null, testBody);
        
        assertNotNull(pair);
        assertNull(pair.getGeometry());
        assertEquals(testBody, pair.getBody());
        assertNull(pair.geometry);
        assertEquals(testBody, pair.body);
    }
    
    /**
     * Tests the constructor with null body parameter.
     * <p>
     * Verifies that the pair can be created with null physics body while
     * maintaining valid geometry data. This might occur in scenarios where
     * only geometric calculations are needed without physics simulation.
     * </p>
     */
    @Test
    public void testConstructorWithNullBody() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(testGeometry, null);
        
        assertNotNull(pair);
        assertEquals(testGeometry, pair.getGeometry());
        assertNull(pair.getBody());
        assertEquals(testGeometry, pair.geometry);
        assertNull(pair.body);
    }
    
    /**
     * Tests the constructor with both parameters as null.
     * <p>
     * Verifies that the pair can be created even when both geometry and body
     * are null. This represents an edge case that should be handled gracefully,
     * potentially useful for placeholder objects or during error conditions.
     * </p>
     */
    @Test
    public void testConstructorWithBothNull() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(null, null);
        
        assertNotNull(pair);
        assertNull(pair.getGeometry());
        assertNull(pair.getBody());
        assertNull(pair.geometry);
        assertNull(pair.body);
    }
    
    /**
     * Tests direct access to public fields.
     * <p>
     * Verifies that the public final fields can be accessed directly and
     * contain the same values as those returned by the getter methods.
     * This ensures consistency between the two access patterns and confirms
     * that the fields are properly declared as public final.
     * </p>
     */
    @Test
    public void testPublicFieldsAccess() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(testGeometry, testBody);
        
        // Test direct field access (which is public final)
        assertEquals(testGeometry, pair.geometry);
        assertEquals(testBody, pair.body);
    }
}
