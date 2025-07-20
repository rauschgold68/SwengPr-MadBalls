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

public class TestGeometricCollisionDetection {
    
    private SimulationModel mockModel;
    private GeometricCollisionDetection collisionDetection;
    private World testWorld;
    private List<PhysicsGeometryPair> testPairs;
    
    @BeforeEach
    public void setUp() {
        mockModel = mock(SimulationModel.class);
        collisionDetection = new GeometricCollisionDetection(mockModel);
        testWorld = new World(new Vec2(0, 0));
        testPairs = new ArrayList<>();
        
        when(mockModel.getGeometryPairs()).thenReturn(testPairs);
    }
    
    private Body createTestBody(String userData) {
        BodyDef bodyDef = new BodyDef();
        Body body = testWorld.createBody(bodyDef);
        body.setUserData(userData);
        return body;
    }
    
    private PhysicsGeometryPair createTestPair(GeometryData geometry, String userData) {
        Body body = createTestBody(userData);
        return new PhysicsGeometryPair(geometry, body);
    }
    
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
    
    @Test
    public void testSkipSelfCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        
        testPairs.add(pair1);
        
        // Should not collide with itself
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }
    
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
    
    @Test
    public void testEmptyPairsList() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        
        // Empty pairs list
        testPairs.clear();
        
        // Should not cause collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }
    
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
}
