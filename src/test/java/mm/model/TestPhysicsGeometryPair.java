package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPhysicsGeometryPair {
    
    private GeometryData testGeometry;
    private Body testBody;
    private World testWorld;
    
    @BeforeEach
    public void setUp() {
        testGeometry = new CircleGeometry(new Position(10.0f, 20.0f), 5.0);
        
        // Create a minimal physics world and body for testing
        testWorld = new World(new Vec2(0, 0));
        BodyDef bodyDef = new BodyDef();
        testBody = testWorld.createBody(bodyDef);
    }
    
    @Test
    public void testConstructorWithBothParameters() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(testGeometry, testBody);
        
        assertNotNull(pair);
        assertEquals(testGeometry, pair.getGeometry());
        assertEquals(testBody, pair.getBody());
        assertEquals(testGeometry, pair.geometry);
        assertEquals(testBody, pair.body);
    }
    
    @Test
    public void testConstructorWithNullGeometry() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(null, testBody);
        
        assertNotNull(pair);
        assertNull(pair.getGeometry());
        assertEquals(testBody, pair.getBody());
        assertNull(pair.geometry);
        assertEquals(testBody, pair.body);
    }
    
    @Test
    public void testConstructorWithNullBody() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(testGeometry, null);
        
        assertNotNull(pair);
        assertEquals(testGeometry, pair.getGeometry());
        assertNull(pair.getBody());
        assertEquals(testGeometry, pair.geometry);
        assertNull(pair.body);
    }
    
    @Test
    public void testConstructorWithBothNull() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(null, null);
        
        assertNotNull(pair);
        assertNull(pair.getGeometry());
        assertNull(pair.getBody());
        assertNull(pair.geometry);
        assertNull(pair.body);
    }
    
    @Test
    public void testPublicFieldsAccess() {
        PhysicsGeometryPair pair = new PhysicsGeometryPair(testGeometry, testBody);
        
        // Test direct field access (which is public final)
        assertEquals(testGeometry, pair.geometry);
        assertEquals(testBody, pair.body);
    }
}
