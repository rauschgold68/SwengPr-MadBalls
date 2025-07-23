package mm.model;

import org.jbox2d.dynamics.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link PhysicsVisualPair} class.
 * <p>
 * This test class verifies the correct behavior of the PhysicsVisualPair constructor,
 * field access, and getter methods. It tests various scenarios including construction
 * with null parameters to ensure robustness.
 * </p>
 * <p>
 * The tests use JBox2D physics objects to create realistic test scenarios that
 * mirror the actual usage of PhysicsVisualPair in the simulation environment.
 * </p>
 * 
 * @see PhysicsVisualPair
 * @see org.jbox2d.dynamics.Body
 * @see javafx.scene.shape.Shape
 */
public class TestPhysicsVisualPair {
    /** Test physics world with standard Earth gravity for realistic body simulation. */
    private World testWorld = new World(new Vec2(0.0f, -9.81f));
    
    /**
     * Tests the constructor of {@link PhysicsVisualPair} with various parameter combinations.
     * <p>
     * This test verifies that:
     * </p>
     * <ul>
     * <li>Constructor properly accepts null visual parameter (common in testing scenarios)</li>
     * <li>Constructor correctly stores the provided JBox2D Body reference</li>
     * <li>Public fields are accessible and contain expected values</li>
     * <li>Getter methods return the same values as direct field access</li>
     * <li>Object construction succeeds even with null visual component</li>
     * </ul>
     * <p>
     * The test creates a realistic JBox2D body with proper fixture definitions
     * to ensure the constructor works with actual physics objects.
     * </p>
     * 
     * @see PhysicsVisualPair#PhysicsVisualPair(javafx.scene.shape.Shape, org.jbox2d.dynamics.Body)
     * @see PhysicsVisualPair#getVisual()
     * @see PhysicsVisualPair#getBody()
     */
    @Test
    public void testConstructor() {
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(0, 0);
        
        Body testBody = testWorld.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.0f, 1.0f);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.1f;
        
        testBody.createFixture(fixtureDef);
        
        PhysicsVisualPair testPair = new PhysicsVisualPair(null, testBody);
        assertNotNull(testPair);
        assertNotNull(testPair.getClass());
        assertNull(testPair.visual);
        assertNull(testPair.getVisual());
        assertNotNull(testPair.body);
        assertNotNull(testPair.getBody());
        assertEquals(testBody, testPair.body);
        assertEquals(testBody, testPair.getBody());     
    }
}
