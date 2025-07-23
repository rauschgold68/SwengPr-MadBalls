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
     * Tests the basic constructor functionality of {@link PhysicsVisualPair}.
     * <p>
     * This test verifies that:
     * </p>
     * <ul>
     * <li>Constructor successfully creates a non-null object</li>
     * <li>Object has the correct class type</li>
     * <li>Constructor properly handles null visual parameter</li>
     * </ul>
     * <p>
     * This focused test ensures the fundamental object creation works correctly
     * without overwhelming the test method with too many assertions.
     * </p>
     * 
     * @see PhysicsVisualPair#PhysicsVisualPair(javafx.scene.shape.Shape, org.jbox2d.dynamics.Body)
     */
    @Test
    public void testConstructorBasics() {
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
        assertEquals(PhysicsVisualPair.class, testPair.getClass());
    }
    
    /**
     * Tests the visual field access and getter method of {@link PhysicsVisualPair}.
     * <p>
     * This test verifies that:
     * </p>
     * <ul>
     * <li>Visual field correctly stores null value when passed to constructor</li>
     * <li>Visual getter method returns the same value as direct field access</li>
     * <li>Both field and getter consistently return null for null input</li>
     * </ul>
     * 
     * @see PhysicsVisualPair#getVisual()
     * @see PhysicsVisualPair#visual
     */
    @Test
    public void testVisualFieldAccess() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        Body testBody = testWorld.createBody(bodyDef);
        
        PhysicsVisualPair testPair = new PhysicsVisualPair(null, testBody);
        assertNull(testPair.visual);
        assertNull(testPair.getVisual());
        assertEquals(testPair.visual, testPair.getVisual());
    }
    
    /**
     * Tests the body field access and getter method of {@link PhysicsVisualPair}.
     * <p>
     * This test verifies that:
     * </p>
     * <ul>
     * <li>Body field correctly stores the JBox2D Body reference</li>
     * <li>Body getter method returns the same reference as direct field access</li>
     * <li>Both field and getter return the exact same Body object instance</li>
     * </ul>
     * 
     * @see PhysicsVisualPair#getBody()
     * @see PhysicsVisualPair#body
     */
    @Test
    public void testBodyFieldAccess() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        Body testBody = testWorld.createBody(bodyDef);
        
        PhysicsVisualPair testPair = new PhysicsVisualPair(null, testBody);
        assertNotNull(testPair.body);
        assertNotNull(testPair.getBody());
        assertEquals(testBody, testPair.body);
        assertEquals(testBody, testPair.getBody());
        assertEquals(testPair.body, testPair.getBody());
    }
}
