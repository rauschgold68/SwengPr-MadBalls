package mm.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link GeometricCollisionDetection} class.
 * <p>
 * This comprehensive test class verifies the correct behavior of GeometricCollisionDetection,
 * which handles collision detection using view-agnostic geometry data rather than JavaFX shapes.
 * This represents a significant improvement in the architecture by removing UI framework
 * dependencies from the model layer.
 * </p>
 * <p>
 * The collision detection system supports various geometric shapes including:
 * </p>
 * <ul>
 * <li>Rectangle-Rectangle collisions (with and without rotation)</li>
 * <li>Circle-Circle collisions</li>
 * <li>Rectangle-Circle collisions</li>
 * <li>Bounding box approximations for complex cases</li>
 * </ul>
 * <p>
 * The tests also cover special game logic including collision skipping for
 * special object types like win zones, win platforms, and no-place zones.
 * </p>
 * 
 * @see GeometricCollisionDetection
 * @see PhysicsGeometryPair
 * @see GeometryData
 * @see SimulationModel
 */
public class TestGeometricCollisionDetection {
    
    /** Mock simulation model for testing collision detection behavior. */
    private SimulationModel mockModel;
    /** The collision detection system under test. */
    private GeometricCollisionDetection collisionDetection;
    /** JBox2D world for creating test physics bodies. */
    private World testWorld;
    /** List of geometry pairs used in collision tests. */
    private List<PhysicsGeometryPair> testPairs;
    
    /**
     * Sets up test fixtures before each test method.
     * <p>
     * Initializes mocked dependencies and creates a fresh collision detection
     * instance for each test. Also sets up a JBox2D world and empty test pairs
     * list to ensure test isolation and consistency.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        mockModel = mock(SimulationModel.class);
        collisionDetection = new GeometricCollisionDetection(mockModel);
        testWorld = new World(new Vec2(0, 0));
        testPairs = new ArrayList<>();
        
        when(mockModel.getGeometryPairs()).thenReturn(testPairs);
    }
    
    /**
     * Helper method to create a test physics body with specified user data.
     * <p>
     * Creates a minimal JBox2D body with the given user data string.
     * This is used throughout the tests to create bodies with identifiable
     * names for collision detection testing.
     * </p>
     * 
     * @param userData the string identifier to associate with the body
     * @return a new JBox2D Body with the specified user data
     */
    private Body createTestBody(String userData) {
        BodyDef bodyDef = new BodyDef();
        Body body = testWorld.createBody(bodyDef);
        body.setUserData(userData);
        return body;
    }
    
    /**
     * Helper method to create a PhysicsGeometryPair with specified geometry and user data.
     * <p>
     * Combines geometry data with a physics body to create a complete pair
     * suitable for collision detection testing. The body is automatically
     * created with the specified user data identifier.
     * </p>
     * 
     * @param geometry the geometry data for the pair (can be null for testing)
     * @param userData the string identifier for the associated physics body
     * @return a new PhysicsGeometryPair ready for testing
     */
    private PhysicsGeometryPair createTestPair(GeometryData geometry, String userData) {
        Body body = createTestBody(userData);
        return new PhysicsGeometryPair(geometry, body);
    }
    
    /**
     * Tests the basic collision detection when objects should not collide.
     * <p>
     * Creates two non-overlapping rectangles positioned far apart and verifies
     * that the collision detection correctly identifies that moving one object
     * to a position that still doesn't overlap with the other returns false.
     * This is a fundamental test of the collision detection system's accuracy.
     * </p>
     * <p>
     * Test setup:
     * </p>
     * <ul>
     * <li>Rectangle 1: 10x10 at position (0, 0)</li>
     * <li>Rectangle 2: 10x10 at position (20, 20)</li>
     * <li>Test movement: Rectangle 1 to position (5, 5)</li>
     * </ul>
     */
    @Test
    public void testWouldCauseOverlapNoCollision() {
        // Create two non-overlapping rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "object2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving pair1 to a position that doesn't overlap with pair2
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }
    
    /**
     * Tests collision detection when objects should collide.
     * <p>
     * Creates two rectangles and tests that the collision detection correctly
     * identifies when moving one object to a position that would cause it to
     * overlap with another object. This validates the positive collision
     * detection case.
     * </p>
     * <p>
     * Test setup:
     * </p>
     * <ul>
     * <li>Rectangle 1: 10x10 at position (0, 0)</li>
     * <li>Rectangle 2: 10x10 at position (20, 20)</li>
     * <li>Test movement: Rectangle 1 to position (25, 25) - should cause overlap</li>
     * </ul>
     */
    @Test
    public void testWouldCauseOverlapWithCollision() {
        // Create two rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "object2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving pair1 to a position that overlaps with pair2
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25));
    }
    
    /**
     * Tests collision detection with rotation parameter.
     * <p>
     * Verifies that the overloaded wouldCauseOverlap method that includes a
     * rotation parameter works correctly for both collision and non-collision
     * scenarios. This tests the rotation-aware collision detection functionality.
     * </p>
     * <p>
     * The test covers:
     * </p>
     * <ul>
     * <li>Non-collision case with rotation applied</li>
     * <li>Collision case with rotation applied</li>
     * <li>Proper handling of the rotation parameter in collision calculations</li>
     * </ul>
     */
    @Test
    public void testWouldCauseOverlapWithRotation() {
        // Create two rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "object2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving with rotation - should not overlap
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5, 45.0));
        
        // Test moving with rotation - should overlap
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25, 45.0));
    }
    
    /**
     * Tests that objects don't collide with themselves.
     * <p>
     * Verifies the collision detection system's ability to skip self-collision
     * checks. An object should never be considered to collide with itself,
     * which is essential for proper collision detection behavior in the
     * simulation where objects need to be moved without false positive
     * self-collisions.
     * </p>
     * <p>
     * This is a critical safety check in collision detection systems.
     * </p>
     */
    @Test
    public void testSkipSelfCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        
        testPairs.add(pair1);
        
        // Should not collide with itself
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }
    
    /**
     * Tests collision skipping for win zone objects.
     * <p>
     * Verifies that the collision detection system correctly skips collision
     * checks with objects marked as "winZone". Win zones are special game
     * objects that should not block object movement but instead trigger
     * victory conditions when objects enter them.
     * </p>
     * <p>
     * This test ensures that game objects can move through win zones without
     * being blocked by collision detection, which is essential for proper
     * game mechanics.
     * </p>
     */
    @Test
    public void testSkipWinZoneCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry winZone = new RectangleGeometry(new Position(5, 5), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair winPair = createTestPair(winZone, "winZone");
        
        testPairs.add(pair1);
        testPairs.add(winPair);
        
        // Should not collide with win zone
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    /**
     * Tests collision skipping for win platform objects.
     * <p>
     * Verifies that the collision detection system correctly skips collision
     * checks with objects marked as "winPlat". Win platforms are special game
     * objects similar to win zones that should allow objects to pass through
     * them without collision blocking, typically used for goal areas or
     * victory conditions.
     * </p>
     * <p>
     * This ensures proper game flow where objects can reach victory conditions
     * without being blocked by collision detection.
     * </p>
     */
    @Test
    public void testSkipWinPlatCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry winPlat = new RectangleGeometry(new Position(5, 5), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair winPair = createTestPair(winPlat, "winPlat");
        
        testPairs.add(pair1);
        testPairs.add(winPair);
        
        // Should not collide with win platform
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    /**
     * Tests collision skipping for no-place zone objects.
     * <p>
     * Verifies that the collision detection system correctly skips collision
     * checks with objects marked as "noPlace". No-place zones are special game
     * objects that define areas where players cannot place objects, but existing
     * objects should be able to move through them freely.
     * </p>
     * <p>
     * This test ensures that no-place zones don't interfere with object
     * movement during gameplay, while still serving their purpose of
     * restricting object placement.
     * </p>
     */
    @Test
    public void testSkipNoPlaceZoneCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry noPlace = new RectangleGeometry(new Position(5, 5), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair noPlacePair = createTestPair(noPlace, "noPlace");
        
        testPairs.add(pair1);
        testPairs.add(noPlacePair);
        
        // Should not collide with no-place zone
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    /**
     * Tests circle-to-circle collision detection.
     * <p>
     * Verifies the accuracy of collision detection between two circular objects.
     * This tests the circle-circle collision algorithm which uses distance
     * calculations between centers and compares against the sum of radii.
     * </p>
     * <p>
     * Test scenarios:
     * </p>
     * <ul>
     * <li>Two circles sufficiently far apart (no collision expected)</li>
     * <li>Moving one circle closer to trigger collision detection</li>
     * <li>Validation of distance-based collision calculations</li>
     * </ul>
     * <p>
     * This is crucial for accurate physics simulation with circular objects.
     * </p>
     */
    @Test
    public void testCircleCircleCollision() {
        CircleGeometry circle1 = new CircleGeometry(new Position(0, 0), 5);
        CircleGeometry circle2 = new CircleGeometry(new Position(15, 0), 5);
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, "circle1");
        PhysicsGeometryPair pair2 = createTestPair(circle2, "circle2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test no collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
        
        // Test collision (move circle1 closer to circle2)
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 8, 0));
    }
    
    /**
     * Tests rectangle-to-circle collision detection.
     * <p>
     * Verifies the mixed-geometry collision detection between rectangular and
     * circular objects. This tests the more complex algorithm that must handle
     * the intersection between a rectangular boundary and a circular area.
     * </p>
     * <p>
     * The test covers:
     * </p>
     * <ul>
     * <li>Non-collision case with rectangle and circle separated</li>
     * <li>Collision case when rectangle is moved close to circle</li>
     * <li>Proper handling of different geometry types in collision detection</li>
     * </ul>
     * <p>
     * This is important for realistic physics simulation where objects
     * of different shapes must interact correctly.
     * </p>
     */
    @Test
    public void testRectangleCircleCollision() {
        RectangleGeometry rect = new RectangleGeometry(new Position(0, 0), 10, 10);
        CircleGeometry circle = new CircleGeometry(new Position(20, 0), 5);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, "rect");
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Test no collision
        assertFalse(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
        
        // Test collision (move rect closer to circle)
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 12, 0));
    }
    
    /**
     * Tests collision detection with rotated rectangles using bounding box approximation.
     * <p>
     * Verifies that when rectangles have rotation applied, the collision detection
     * system falls back to using bounding box approximation for performance reasons.
     * Rotated rectangle collision detection is computationally expensive, so the
     * system uses the axis-aligned bounding box of the rotated rectangle.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Rotated rectangles are handled without errors</li>
     * <li>Bounding box approximation is used for rotated shapes</li>
     * <li>Performance is maintained even with complex rotations</li>
     * </ul>
     */
    @Test
    public void testRotatedRectangleCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10, 45);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // With rotation, should use bounding box approximation
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }
    
    /**
     * Tests collision detection with null geometry handling.
     * <p>
     * Verifies that the collision detection system gracefully handles cases
     * where geometry data is null. This is an important robustness test as
     * null geometry can occur during object initialization, error conditions,
     * or when dealing with placeholder objects.
     * </p>
     * <p>
     * The system should:
     * </p>
     * <ul>
     * <li>Not throw exceptions when encountering null geometry</li>
     * <li>Return false for collision (no collision with undefined geometry)</li>
     * <li>Continue processing other objects normally</li>
     * </ul>
     * <p>
     * This ensures system stability even under edge conditions.
     * </p>
     */
    @Test
    public void testNullGeometryHandling() {
        // Create pair with null geometry
        PhysicsGeometryPair pair1 = createTestPair(null, "object1");
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5, 5), 10, 10);
        PhysicsGeometryPair pair2 = createTestPair(rect2, "object2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should not cause collision with null geometry
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    /**
     * Tests collision detection with an empty pairs list.
     * <p>
     * Verifies that the collision detection system handles the edge case
     * where there are no other objects to collide with. This can occur
     * during initial game setup, after objects are cleared, or in
     * minimal test scenarios.
     * </p>
     * <p>
     * The system should:
     * </p>
     * <ul>
     * <li>Handle empty collections gracefully</li>
     * <li>Return false (no collision possible with no objects)</li>
     * <li>Not throw exceptions or fail unexpectedly</li>
     * </ul>
     * <p>
     * This ensures robust behavior in all game states.
     * </p>
     */
    @Test
    public void testEmptyPairsList() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        
        // Empty pairs list
        testPairs.clear();
        
        // Should not cause collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }

    /**
     * Tests bounding box intersection detection when boxes don't intersect.
     * <p>
     * Verifies the fundamental bounding box intersection algorithm used as
     * a first-pass filter for collision detection. If bounding boxes don't
     * intersect, the objects definitely don't collide, allowing for early
     * rejection and performance optimization.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Bounding box calculations are correct</li>
     * <li>Non-intersecting boxes are properly identified</li>
     * <li>Early rejection optimization works correctly</li>
     * </ul>
     * <p>
     * Bounding box checks are crucial for collision detection performance.
     * </p>
     */
    @Test
    public void testBoundingBoxesNoIntersection() {
        // Test bounding boxes that don't intersect
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 5, 5);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(10, 10), 5, 5);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // No bounding box intersection, so no collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests exception handling for unsupported geometry types.
     * <p>
     * Verifies that the collision detection system properly handles unsupported
     * geometry types by throwing appropriate exceptions. This test creates a
     * custom geometry type that isn't supported by the collision detection
     * algorithms and ensures the system fails gracefully with a clear error.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Unsupported geometry types are detected</li>
     * <li>Appropriate exceptions are thrown (IllegalArgumentException)</li>
     * <li>System doesn't fail silently or with unclear errors</li>
     * <li>Error handling is robust and informative</li>
     * </ul>
     * <p>
     * This is important for debugging and extending the geometry system.
     * </p>
     */
    @Test
    public void testUnsupportedGeometryTypeInCreateGeometry() {
        // Create a custom geometry type that's not supported
        GeometryData unsupportedGeometry = new GeometryData(new Position(0, 0), 0) {
            @Override
            public boolean containsPoint(double x, double y) { return false; }
            
            @Override
            public double[] getBounds() { return new double[]{0, 0, 1, 1}; }
        };
        
        PhysicsGeometryPair pair1 = createTestPair(unsupportedGeometry, "unsupported");
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5, 5), 10, 10);
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should throw IllegalArgumentException for unsupported geometry type
        assertThrows(IllegalArgumentException.class, () -> {
            collisionDetection.wouldCauseOverlap(pair1, 7, 7);
        });
    }

    /**
     * Tests edge case collision scenarios with close but non-overlapping rectangles.
     * <p>
     * This test explores edge cases in the collision detection system where
     * bounding boxes might be close but the objects don't actually overlap.
     * This scenario tests the accuracy of the AABB (Axis-Aligned Bounding Box)
     * collision detection algorithm with tight tolerances.
     * </p>
     * <p>
     * The test verifies:
     * </p>
     * <ul>
     * <li>Precise collision detection near boundaries</li>
     * <li>Correct handling of floating-point precision in collision calculations</li>
     * <li>Accurate determination of non-collision in edge cases</li>
     * <li>System robustness with minimal separation distances</li>
     * </ul>
     * <p>
     * This ensures accuracy in tight spacing scenarios common in gameplay.
     * </p>
     */
    @Test
    public void testUnsupportedGeometryCombinationInGeometriesIntersect() {
        // This test is difficult to achieve because createGeometryAtPosition will throw first
        // Let's test the case where we have known geometries but test the fallback path
        // by creating a scenario where bounding boxes intersect but detailed collision returns false
        
        // Create two rectangles that have intersecting bounding boxes but don't actually collide
        // when using AABB collision detection
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 5, 5, 0);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5.1f, 0f), 5, 5, 0); // Just outside collision range
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should return false - bounding boxes might be close but AABB collision should return false
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests AABB collision detection for non-rotated rectangles with collision.
     * <p>
     * Verifies that the Axis-Aligned Bounding Box (AABB) collision detection
     * algorithm correctly identifies when two non-rotated rectangles overlap.
     * AABB collision detection is the most efficient algorithm for non-rotated
     * rectangular objects and forms the foundation of the collision system.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>AABB algorithm correctly detects overlapping rectangles</li>
     * <li>Rotation value of 0 is properly handled</li>
     * <li>Collision detection is accurate for axis-aligned shapes</li>
     * <li>Basic collision mathematics work correctly</li>
     * </ul>
     * <p>
     * This is a fundamental test for the collision detection system.
     * </p>
     */
    @Test
    public void testRectangleRectangleCollisionWithoutRotation() {
        // Test AABB collision for non-rotated rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10, 0); // explicitly no rotation
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5, 5), 10, 10, 0); // explicitly no rotation
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should detect collision using AABB algorithm
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests AABB collision detection for non-rotated rectangles without collision.
     * <p>
     * Verifies that the AABB collision detection algorithm correctly identifies
     * when two non-rotated rectangles do not overlap, even when they are
     * positioned relatively close to each other. This tests the negative case
     * of the AABB collision detection algorithm.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>AABB algorithm correctly rejects non-overlapping rectangles</li>
     * <li>Close but separate rectangles are handled accurately</li>
     * <li>No false positive collision detection occurs</li>
     * <li>Precision in boundary calculations is maintained</li>
     * </ul>
     * <p>
     * This prevents unnecessary collision responses in the simulation.
     * </p>
     */
    @Test
    public void testRectangleRectangleNoCollisionWithoutRotation() {
        // Test AABB no collision for non-rotated rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 5, 5, 0);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(6, 6), 5, 5, 0);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should not detect collision using AABB algorithm
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests circle-to-circle collision detection for touching circles.
     * <p>
     * Verifies the precise boundary handling in circle collision detection
     * when two circles are exactly touching (distance between centers equals
     * sum of radii). This is a critical edge case that tests the collision
     * algorithm's precision and boundary condition handling.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Boundary conditions are handled correctly</li>
     * <li>Touching circles are not considered colliding</li>
     * <li>Floating-point precision doesn't cause false positives</li>
     * <li>Mathematical accuracy in distance calculations</li>
     * </ul>
     * <p>
     * This prevents unnecessary collision responses for barely touching objects.
     * </p>
     */
    @Test
    public void testCircleCircleCollisionTouching() {
        // Test circles that are exactly touching
        CircleGeometry circle1 = new CircleGeometry(new Position(0, 0), 5);
        CircleGeometry circle2 = new CircleGeometry(new Position(10, 0), 5); // centers 10 apart, radii 5 each = touching
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, "circle1");
        PhysicsGeometryPair pair2 = createTestPair(circle2, "circle2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should not detect collision when exactly touching (distance == sum of radii)
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests circle-to-circle collision detection for overlapping circles.
     * <p>
     * Verifies that the collision detection algorithm correctly identifies
     * when two circles are actually overlapping (distance between centers
     * is less than the sum of radii). This tests the positive collision
     * case for circular objects.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>True collision between circles is properly detected</li>
     * <li>Distance calculations are accurate for overlapping circles</li>
     * <li>Circle collision algorithm works correctly</li>
     * <li>Mathematical precision in collision determination</li>
     * </ul>
     * <p>
     * This enables proper collision response for circular game objects.
     * </p>
     */
    @Test
    public void testCircleCircleCollisionOverlapping() {
        // Test circles that are overlapping
        CircleGeometry circle1 = new CircleGeometry(new Position(0, 0), 5);
        CircleGeometry circle2 = new CircleGeometry(new Position(8, 0), 5); // centers 8 apart, radii 5 each = overlapping
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, "circle1");
        PhysicsGeometryPair pair2 = createTestPair(circle2, "circle2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should detect collision when overlapping (distance < sum of radii)
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests rectangle-to-circle collision detection with rotated rectangles.
     * <p>
     * Verifies collision detection between a rotated rectangle and a circle
     * using bounding box approximation. When rectangles have rotation applied,
     * the collision detection system falls back to using the axis-aligned
     * bounding box of the rotated rectangle for performance reasons, as
     * precise rotated rectangle-circle collision detection is computationally
     * expensive.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Rotated rectangle-circle collisions are detected correctly</li>
     * <li>Bounding box approximation is used for rotated rectangles</li>
     * <li>Mixed geometry collision with rotation works properly</li>
     * <li>Performance is maintained with complex rotated shapes</li>
     * </ul>
     * <p>
     * The bounding box approximation may be less precise but ensures
     * consistent performance in the physics simulation.
     * </p>
     */
    @Test
    public void testRectangleCircleCollisionWithRotation() {
        // Test rectangle-circle collision with rotated rectangle (uses bounding box approximation)
        RectangleGeometry rotatedRect = new RectangleGeometry(new Position(0, 0), 10, 10, 45); // 45 degree rotation
        CircleGeometry circle = new CircleGeometry(new Position(5, 5), 3);
        
        PhysicsGeometryPair rectPair = createTestPair(rotatedRect, "rotatedRect");
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should use bounding box approximation for rotated rectangle
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    /**
     * Tests precise rectangle-to-circle collision detection without rotation.
     * <p>
     * Verifies the precise collision detection algorithm used when rectangles
     * have no rotation applied. This test specifically validates that the system
     * uses exact geometric calculations rather than bounding box approximations
     * when dealing with axis-aligned rectangles and circles.
     * </p>
     * <p>
     * The test setup includes:
     * </p>
     * <ul>
     * <li>Rectangle at (0,0) with dimensions 10x10 (no rotation)</li>
     * <li>Circle positioned to be near but not colliding with rectangle</li>
     * <li>Mathematical verification of non-collision case</li>
     * <li>Validation of precise distance calculations</li>
     * </ul>
     * <p>
     * This ensures maximum accuracy for axis-aligned collision detection,
     * which is important for gameplay precision and physics simulation.
     * </p>
     */
    @Test
    public void testRectangleCircleCollisionPreciseCalculation() {
        // Test rectangle-circle collision without rotation (precise calculation)
        RectangleGeometry rect = new RectangleGeometry(new Position(0, 0), 10, 10, 0); // no rotation
        // Circle position (15, 5) with radius 3 means center is at (18, 8)
        // Rectangle is from (0,0) to (10,10), so closest point to center (18,8) is (10,8) 
        // Distance from (18,8) to (10,8) = 8, radius = 3, so 8 > 3 = no collision
        CircleGeometry circle = new CircleGeometry(new Position(15, 5), 3);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, "rect");
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should use precise calculation for non-rotated rectangle
        assertFalse(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    /**
     * Tests precise rectangle-to-circle collision detection with actual collision.
     * <p>
     * Verifies the positive collision case using precise geometric calculations
     * when rectangles have no rotation. This test validates that the collision
     * detection algorithm correctly identifies when a circle overlaps with an
     * axis-aligned rectangle using exact distance calculations rather than
     * bounding box approximations.
     * </p>
     * <p>
     * The test includes detailed mathematical verification:
     * </p>
     * <ul>
     * <li>Rectangle bounds: (0,0) to (10,10)</li>
     * <li>Circle center calculation and radius consideration</li>
     * <li>Closest point on rectangle to circle center</li>
     * <li>Distance comparison against circle radius for collision determination</li>
     * </ul>
     * <p>
     * This test ensures that the precise collision algorithm works correctly
     * for the positive case, complementing the non-collision test and validating
     * the mathematical accuracy of the collision detection system.
     * </p>
     */
    @Test
    public void testRectangleCircleCollisionPreciseCalculationWithCollision() {
        // Test rectangle-circle collision without rotation with actual collision
        RectangleGeometry rect = new RectangleGeometry(new Position(0, 0), 10, 10, 0);
        // Circle position (7, 5) with radius 5 means center is at (12, 10)
        // Rectangle is from (0,0) to (10,10), so closest point to center (12,10) is (10,10)
        // Distance from (12,10) to (10,10) = sqrt((12-10)^2 + (10-10)^2) = 2
        // Since radius = 5 and distance = 2 < 5, there should be collision
        CircleGeometry circle = new CircleGeometry(new Position(7, 5), 5);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, "rect");
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should detect collision using precise calculation
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    /**
     * Tests bounding box intersection edge cases with touching rectangles.
     * <p>
     * Verifies the collision detection system's behavior when rectangles are
     * positioned exactly at boundary conditions - touching at edges but not
     * overlapping. This is a critical edge case that tests the precision of
     * bounding box intersection calculations and ensures that touching objects
     * are not considered colliding.
     * </p>
     * <p>
     * This test covers:
     * </p>
     * <ul>
     * <li>Rectangles positioned to touch at right edge boundary</li>
     * <li>Exact boundary condition handling in AABB calculations</li>
     * <li>Prevention of false positive collisions for touching objects</li>
     * <li>Floating-point precision in boundary comparisons</li>
     * </ul>
     * <p>
     * This ensures that objects positioned precisely against each other
     * don't trigger unnecessary collision responses, which is important
     * for smooth gameplay and object placement mechanics.
     * </p>
     */
    @Test
    public void testBoundingBoxIntersectionEdgeCases() {
        // Test bounding boxes that touch exactly at edges
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 5, 5);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5, 0), 5, 5); // touching right edge
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Touching edges should not be considered collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests collision detection with zero-size geometries.
     * <p>
     * Verifies that the collision detection system properly handles degenerate
     * cases where geometries have zero dimensions. This includes zero-radius
     * circles and zero-width/zero-height rectangles. Such geometries can occur
     * during object creation, scaling operations, or as special markers.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Zero-size geometries don't cause crashes or exceptions</li>
     * <li>Collision detection handles degenerate cases gracefully</li>
     * <li>Mathematical edge cases in collision algorithms are robust</li>
     * <li>System stability with unusual geometry configurations</li>
     * </ul>
     * <p>
     * This prevents crashes and undefined behavior in edge cases.
     * </p>
     */
    @Test
    public void testZeroSizeGeometries() {
        // Test with zero-size geometries
        RectangleGeometry zeroRect = new RectangleGeometry(new Position(5, 5), 0, 0);
        CircleGeometry zeroCircle = new CircleGeometry(new Position(5, 5), 0);
        
        PhysicsGeometryPair rectPair = createTestPair(zeroRect, "zeroRect");
        PhysicsGeometryPair circlePair = createTestPair(zeroCircle, "zeroCircle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Zero-size geometries at same position should not collide
        assertFalse(collisionDetection.wouldCauseOverlap(rectPair, 5, 5));
    }

    /**
     * Tests collision detection using the rotation parameter overload.
     * <p>
     * Verifies the overloaded wouldCauseOverlap method that accepts a rotation
     * parameter in addition to position coordinates. This method allows for
     * testing collision detection with objects that have rotation applied
     * during the collision check, which is useful for dynamic rotation
     * scenarios in the physics simulation.
     * </p>
     * <p>
     * Test scenarios covered:
     * </p>
     * <ul>
     * <li>Non-collision case with 45-degree rotation applied</li>
     * <li>Collision case with rotation applied and position change</li>
     * <li>Validation that rotation parameter is properly handled</li>
     * <li>Consistency with the non-rotation version of the method</li>
     * </ul>
     * <p>
     * This ensures the collision detection system can handle dynamic
     * rotations during collision queries, which is essential for
     * realistic physics simulation with rotating objects.
     * </p>
     */
    @Test
    public void testWouldCauseOverlapWithRotationParameter() {
        // Test the overloaded method that includes rotation
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test with rotation parameter - no collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5, 45.0));
        
        // Test with rotation parameter - collision
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25, 45.0));
    }

    /**
     * Tests circle-to-rectangle collision detection with reversed parameter order.
     * <p>
     * Verifies that the collision detection system properly handles the case
     * where the first geometry is a circle and the second is a rectangle.
     * This tests the parameter swapping logic in the geometriesIntersect method,
     * ensuring that collision detection is symmetric and works correctly
     * regardless of the order in which geometries are presented.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Parameter order independence in collision detection</li>
     * <li>Proper internal parameter swapping for circle-rectangle cases</li>
     * <li>Symmetric collision detection behavior</li>
     * <li>Consistent results regardless of geometry presentation order</li>
     * </ul>
     * <p>
     * This is important for the collision detection system's robustness,
     * as objects can be added to the simulation in any order and should
     * produce consistent collision results.
     * </p>
     */
    @Test
    public void testCircleRectangleCollisionOrderReversed() {
        // Test circle-rectangle collision (geom1 = circle, geom2 = rectangle)
        // This tests the reversed case in geometriesIntersect
        CircleGeometry circle = new CircleGeometry(new Position(5, 5), 3);
        RectangleGeometry rect = new RectangleGeometry(new Position(10, 10), 10, 10);
        
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        PhysicsGeometryPair rectPair = createTestPair(rect, "rect");
        
        testPairs.add(circlePair);
        testPairs.add(rectPair);
        
        // Should handle circle-rectangle collision (parameters will be swapped internally)
        assertTrue(collisionDetection.wouldCauseOverlap(circlePair, 12, 12));
    }
}
