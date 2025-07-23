package mm.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the {@link PositionValidationService} class.
 * <p>
 * This test class verifies the correct behavior of position validation within the simulation,
 * including checking positions against no-place zones and win zones. The tests avoid JavaFX
 * dependencies by using mocked geometry data and physics bodies from JBox2D.
 * </p>
 * <p>
 * The tests cover various scenarios including:
 * </p>
 * <ul>
 * <li>Position validation in no-place zones with different geometry configurations</li>
 * <li>Position validation in win zones with proper userData handling</li>
 * <li>Edge cases with null geometry data and empty zone collections</li>
 * <li>Proper handling of geometry pair synchronization</li>
 * </ul>
 * 
 * @see PositionValidationService
 * @see SimulationModel.PhysicsComponents
 * @see SimulationModel.GameObjectCollections
 */
public class TestPositionValidationService {
    
    /** Service under test for position validation operations. */
    private PositionValidationService positionValidationService;
    
    /** Mocked physics components containing pairs and geometry data. */
    @Mock
    private SimulationModel.PhysicsComponents mockPhysicsComponents;
    
    /** Mocked game object collections containing zone information. */
    @Mock
    private SimulationModel.GameObjectCollections mockGameObjectCollections;
    
    /** Test physics world for creating bodies. */
    private World testWorld;
    
    /** List of physics-visual pairs for testing. */
    private List<PhysicsVisualPair> testPairs;
    
    /** List of physics-geometry pairs for testing. */
    private List<PhysicsGeometryPair> testGeometryPairs;
    
    /** List of no-place zones for testing. */
    private List<PhysicsVisualPair> testNoPlaceZones;
    
    /**
     * Sets up test fixtures before each test method.
     * <p>
     * Initializes the mocked dependencies, creates test collections, and sets up
     * the PositionValidationService with the mocked components. Also creates a
     * JBox2D world for realistic physics body creation.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test world and collections
        testWorld = new World(new Vec2(0.0f, 9.8f));
        testPairs = new ArrayList<>();
        testGeometryPairs = new ArrayList<>();
        testNoPlaceZones = new ArrayList<>();
        
        // Setup mock behavior
        when(mockPhysicsComponents.pairs).thenReturn(testPairs);
        when(mockPhysicsComponents.geometryPairs).thenReturn(testGeometryPairs);
        when(mockGameObjectCollections.noPlaceZones).thenReturn(testNoPlaceZones);
        
        // Create service under test
        positionValidationService = new PositionValidationService(mockPhysicsComponents, mockGameObjectCollections);
    }
    
    /**
     * Tests position validation when no no-place zones exist.
     * <p>
     * Verifies that when there are no no-place zones in the simulation,
     * any position should be considered valid (not in a no-place zone).
     * This tests the baseline behavior when the zone collection is empty.
     * </p>
     * 
     * @see PositionValidationService#isInNoPlaceZone(double, double)
     */
    @Test
    public void testIsInNoPlaceZoneWithEmptyZones() {
        // Test with empty no-place zones
        assertFalse(positionValidationService.isInNoPlaceZone(10.0, 20.0));
        assertFalse(positionValidationService.isInNoPlaceZone(0.0, 0.0));
        assertFalse(positionValidationService.isInNoPlaceZone(-5.0, 15.0));
    }
    
    /**
     * Tests position validation when a position is inside a no-place zone.
     * <p>
     * Creates a no-place zone with geometry that contains a test point and
     * verifies that the position validation correctly identifies the position
     * as being within the restricted zone.
     * </p>
     * 
     * @see PositionValidationService#isInNoPlaceZone(double, double)
     */
    @Test
    public void testIsInNoPlaceZoneWithPositionInside() {
        // Create test position and geometry
        Position testPosition = new Position(5.0f, 5.0f);
        CircleGeometry mockGeometry = new CircleGeometry(testPosition, 10.0);
        
        // Create bodies and pairs
        Body testBody = createTestBody("noPlace");
        PhysicsVisualPair noPlaceZone = new PhysicsVisualPair(null, testBody);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(mockGeometry, testBody);
        
        // Add to collections
        testNoPlaceZones.add(noPlaceZone);
        testPairs.add(noPlaceZone);
        testGeometryPairs.add(geometryPair);
        
        // Test position inside the circle (center at 15, 15, radius 10)
        assertTrue(positionValidationService.isInNoPlaceZone(15.0, 15.0));
        assertTrue(positionValidationService.isInNoPlaceZone(12.0, 15.0));
    }
    
    /**
     * Tests position validation when a position is outside a no-place zone.
     * <p>
     * Creates a no-place zone with geometry that does not contain a test point
     * and verifies that the position validation correctly identifies the position
     * as being outside the restricted zone.
     * </p>
     * 
     * @see PositionValidationService#isInNoPlaceZone(double, double)
     */
    @Test
    public void testIsInNoPlaceZoneWithPositionOutside() {
        // Create test position and geometry
        Position testPosition = new Position(5.0f, 5.0f);
        CircleGeometry mockGeometry = new CircleGeometry(testPosition, 5.0);
        
        // Create bodies and pairs
        Body testBody = createTestBody("noPlace");
        PhysicsVisualPair noPlaceZone = new PhysicsVisualPair(null, testBody);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(mockGeometry, testBody);
        
        // Add to collections
        testNoPlaceZones.add(noPlaceZone);
        testPairs.add(noPlaceZone);
        testGeometryPairs.add(geometryPair);
        
        // Test position outside the circle (center at 10, 10, radius 5)
        assertFalse(positionValidationService.isInNoPlaceZone(20.0, 20.0));
        assertFalse(positionValidationService.isInNoPlaceZone(0.0, 0.0));
    }
    
    /**
     * Tests position validation with null geometry in no-place zone.
     * <p>
     * Verifies that when a no-place zone has null geometry data, the position
     * validation handles this gracefully and does not consider any position
     * as being within that zone. This tests error handling for incomplete data.
     * </p>
     * 
     * @see PositionValidationService#isInNoPlaceZone(double, double)
     */
    @Test
    public void testIsInNoPlaceZoneWithNullGeometry() {
        // Create bodies and pairs with null geometry
        Body testBody = createTestBody("noPlace");
        PhysicsVisualPair noPlaceZone = new PhysicsVisualPair(null, testBody);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(null, testBody);
        
        // Add to collections
        testNoPlaceZones.add(noPlaceZone);
        testPairs.add(noPlaceZone);
        testGeometryPairs.add(geometryPair);
        
        // Should return false for null geometry
        assertFalse(positionValidationService.isInNoPlaceZone(10.0, 20.0));
    }
    
    /**
     * Tests position validation when geometry pairs are out of sync.
     * <p>
     * Verifies that when the physics pairs and geometry pairs collections
     * have different sizes or indices, the validation handles this gracefully
     * without throwing exceptions. This tests robustness against data inconsistencies.
     * </p>
     * 
     * @see PositionValidationService#isInNoPlaceZone(double, double)
     */
    @Test
    public void testIsInNoPlaceZoneWithMismatchedCollections() {
        // Create a no-place zone but don't add corresponding geometry pair
        Body testBody = createTestBody("noPlace");
        PhysicsVisualPair noPlaceZone = new PhysicsVisualPair(null, testBody);
        
        testNoPlaceZones.add(noPlaceZone);
        testPairs.add(noPlaceZone);
        // Intentionally not adding to testGeometryPairs to create mismatch
        
        // Should handle gracefully and return false
        assertFalse(positionValidationService.isInNoPlaceZone(10.0, 20.0));
    }
    
    /**
     * Tests win zone validation when no physics pairs exist.
     * <p>
     * Verifies that when there are no physics pairs in the simulation,
     * no position should be considered as being in a win zone.
     * This tests the baseline behavior with empty collections.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithEmptyPairs() {
        // Test with empty pairs collection
        assertFalse(positionValidationService.isInWinZone(10.0, 20.0));
        assertFalse(positionValidationService.isInWinZone(0.0, 0.0));
    }
    
    /**
     * Tests win zone validation with a position inside a win zone.
     * <p>
     * Creates a physics pair with "winZone" userData and geometry that contains
     * a test point, then verifies that the position validation correctly
     * identifies the position as being within the win zone.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithPositionInside() {
        // Create test geometry
        Position testPosition = new Position(10.0f, 10.0f);
        RectangleGeometry mockGeometry = new RectangleGeometry(testPosition, 20.0, 15.0);
        
        // Create win zone pair
        Body winZoneBody = createTestBody("winZone");
        PhysicsVisualPair winZonePair = new PhysicsVisualPair(null, winZoneBody);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(mockGeometry, winZoneBody);
        
        // Add to collections
        testPairs.add(winZonePair);
        testGeometryPairs.add(geometryPair);
        
        // Test position inside the rectangle (10,10 to 30,25)
        assertTrue(positionValidationService.isInWinZone(20.0, 20.0));
        assertTrue(positionValidationService.isInWinZone(15.0, 15.0));
    }
    
    /**
     * Tests win zone validation with a position outside a win zone.
     * <p>
     * Creates a physics pair with "winZone" userData and geometry that does not
     * contain a test point, then verifies that the position validation correctly
     * identifies the position as being outside the win zone.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithPositionOutside() {
        // Create test geometry
        Position testPosition = new Position(10.0f, 10.0f);
        RectangleGeometry mockGeometry = new RectangleGeometry(testPosition, 10.0, 10.0);
        
        // Create win zone pair
        Body winZoneBody = createTestBody("winZone");
        PhysicsVisualPair winZonePair = new PhysicsVisualPair(null, winZoneBody);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(mockGeometry, winZoneBody);
        
        // Add to collections
        testPairs.add(winZonePair);
        testGeometryPairs.add(geometryPair);
        
        // Test position outside the rectangle (10,10 to 20,20)
        assertFalse(positionValidationService.isInWinZone(25.0, 25.0));
        assertFalse(positionValidationService.isInWinZone(5.0, 5.0));
    }
    
    /**
     * Tests win zone validation with "winPlat" userData.
     * <p>
     * Verifies that physics pairs with "winPlat" userData are also treated
     * as win zones. This tests the alternative win zone identification logic
     * that handles different types of winning areas.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithWinPlatform() {
        // Create test geometry
        Position testPosition = new Position(0.0f, 0.0f);
        CircleGeometry mockGeometry = new CircleGeometry(testPosition, 8.0);
        
        // Create win platform pair
        Body winPlatBody = createTestBody("winPlat");
        PhysicsVisualPair winPlatPair = new PhysicsVisualPair(null, winPlatBody);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(mockGeometry, winPlatBody);
        
        // Add to collections
        testPairs.add(winPlatPair);
        testGeometryPairs.add(geometryPair);
        
        // Test position inside the circle (center at 8, 8, radius 8)
        assertTrue(positionValidationService.isInWinZone(8.0, 8.0));
        assertTrue(positionValidationService.isInWinZone(10.0, 8.0));
    }
    
    /**
     * Tests win zone validation with non-win zone userData.
     * <p>
     * Creates physics pairs with userData that does not indicate a win zone
     * and verifies that these are not considered win zones. This tests the
     * proper filtering of non-winning areas.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithNonWinZoneUserData() {
        // Create test geometry
        Position testPosition = new Position(5.0f, 5.0f);
        RectangleGeometry mockGeometry = new RectangleGeometry(testPosition, 10.0, 10.0);
        
        // Create regular object pair (not a win zone)
        Body regularBody = createTestBody("regularObject");
        PhysicsVisualPair regularPair = new PhysicsVisualPair(null, regularBody);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(mockGeometry, regularBody);
        
        // Add to collections
        testPairs.add(regularPair);
        testGeometryPairs.add(geometryPair);
        
        // Should not be considered a win zone
        assertFalse(positionValidationService.isInWinZone(10.0, 10.0));
    }
    
    /**
     * Tests win zone validation with null geometry.
     * <p>
     * Verifies that when a win zone pair has null geometry data, the position
     * validation handles this gracefully and does not consider any position
     * as being within that zone. This tests error handling for incomplete data.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithNullGeometry() {
        // Create win zone pair with null geometry
        Body winZoneBody = createTestBody("winZone");
        PhysicsVisualPair winZonePair = new PhysicsVisualPair(null, winZoneBody);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(null, winZoneBody);
        
        // Add to collections
        testPairs.add(winZonePair);
        testGeometryPairs.add(geometryPair);
        
        // Should return false for null geometry
        assertFalse(positionValidationService.isInWinZone(10.0, 20.0));
    }
    
    /**
     * Tests win zone validation with mismatched collection sizes.
     * <p>
     * Verifies that when the physics pairs and geometry pairs collections
     * have different sizes, the validation handles this gracefully by
     * continuing processing only valid indices. This tests robustness.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithMismatchedCollectionSizes() {
        // Create win zone pair but don't add corresponding geometry pair
        Body winZoneBody = createTestBody("winZone");
        PhysicsVisualPair winZonePair = new PhysicsVisualPair(null, winZoneBody);
        
        testPairs.add(winZonePair);
        // Intentionally not adding to testGeometryPairs to create size mismatch
        
        // Should handle gracefully and continue processing
        assertFalse(positionValidationService.isInWinZone(10.0, 20.0));
    }
    
    /**
     * Tests win zone validation with multiple win zones.
     * <p>
     * Creates multiple win zones with different geometries and userData types,
     * then tests positions that should be in one zone but not others.
     * This verifies the proper iteration and evaluation logic.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithMultipleZones() {
        // Create first win zone
        Position pos1 = new Position(0.0f, 0.0f);
        RectangleGeometry geom1 = new RectangleGeometry(pos1, 10.0, 10.0);
        Body body1 = createTestBody("winZone");
        PhysicsVisualPair pair1 = new PhysicsVisualPair(null, body1);
        PhysicsGeometryPair geomPair1 = new PhysicsGeometryPair(geom1, body1);
        
        // Create second win zone
        Position pos2 = new Position(20.0f, 20.0f);
        CircleGeometry geom2 = new CircleGeometry(pos2, 5.0);
        Body body2 = createTestBody("winPlat");
        PhysicsVisualPair pair2 = new PhysicsVisualPair(null, body2);
        PhysicsGeometryPair geomPair2 = new PhysicsGeometryPair(geom2, body2);
        
        // Add to collections
        testPairs.add(pair1);
        testGeometryPairs.add(geomPair1);
        testPairs.add(pair2);
        testGeometryPairs.add(geomPair2);
        
        // Test positions in first zone
        assertTrue(positionValidationService.isInWinZone(5.0, 5.0));
        
        // Test positions in second zone (circle center at 25, 25)
        assertTrue(positionValidationService.isInWinZone(25.0, 25.0));
        
        // Test position not in any zone
        assertFalse(positionValidationService.isInWinZone(15.0, 15.0));
    }
    
    /**
     * Tests edge case with null userData.
     * <p>
     * Verifies that physics pairs with null userData are handled gracefully
     * and do not cause exceptions. Such pairs should not be considered win zones.
     * This tests defensive programming against incomplete data.
     * </p>
     * 
     * @see PositionValidationService#isInWinZone(double, double)
     */
    @Test
    public void testIsInWinZoneWithNullUserData() {
        // Create pair with null userData
        Position testPosition = new Position(5.0f, 5.0f);
        RectangleGeometry mockGeometry = new RectangleGeometry(testPosition, 10.0, 10.0);
        
        Body bodyWithNullData = createTestBody(null);
        PhysicsVisualPair pairWithNullData = new PhysicsVisualPair(null, bodyWithNullData);
        PhysicsGeometryPair geometryPair = new PhysicsGeometryPair(mockGeometry, bodyWithNullData);
        
        // Add to collections
        testPairs.add(pairWithNullData);
        testGeometryPairs.add(geometryPair);
        
        // Should not be considered a win zone
        assertFalse(positionValidationService.isInWinZone(10.0, 10.0));
    }
    
    /**
     * Tests comprehensive position validation workflow.
     * <p>
     * Creates a scenario with both no-place zones and win zones, then tests
     * various positions to ensure the service correctly differentiates between
     * the different zone types. This integration test verifies the overall
     * functionality of the position validation service.
     * </p>
     */
    @Test
    public void testComprehensivePositionValidation() {
        // Create no-place zone
        Position noPlacePos = new Position(0.0f, 0.0f);
        CircleGeometry noPlaceGeom = new CircleGeometry(noPlacePos, 5.0);
        Body noPlaceBody = createTestBody("noPlace");
        PhysicsVisualPair noPlacePair = new PhysicsVisualPair(null, noPlaceBody);
        PhysicsGeometryPair noPlaceGeomPair = new PhysicsGeometryPair(noPlaceGeom, noPlaceBody);
        
        // Create win zone
        Position winZonePos = new Position(20.0f, 20.0f);
        RectangleGeometry winZoneGeom = new RectangleGeometry(winZonePos, 10.0, 10.0);
        Body winZoneBody = createTestBody("winZone");
        PhysicsVisualPair winZonePair = new PhysicsVisualPair(null, winZoneBody);
        PhysicsGeometryPair winZoneGeomPair = new PhysicsGeometryPair(winZoneGeom, winZoneBody);
        
        // Add to appropriate collections
        testNoPlaceZones.add(noPlacePair);
        testPairs.add(noPlacePair);
        testGeometryPairs.add(noPlaceGeomPair);
        
        testPairs.add(winZonePair);
        testGeometryPairs.add(winZoneGeomPair);
        
        // Test position in no-place zone (circle center at 5, 5)
        assertTrue(positionValidationService.isInNoPlaceZone(5.0, 5.0));
        assertFalse(positionValidationService.isInWinZone(5.0, 5.0));
        
        // Test position in win zone (rectangle 20,20 to 30,30)
        assertFalse(positionValidationService.isInNoPlaceZone(25.0, 25.0));
        assertTrue(positionValidationService.isInWinZone(25.0, 25.0));
        
        // Test position in neither zone
        assertFalse(positionValidationService.isInNoPlaceZone(15.0, 15.0));
        assertFalse(positionValidationService.isInWinZone(15.0, 15.0));
    }
    
    /**
     * Helper method to create a test physics body with specified user data.
     * <p>
     * Creates a minimal JBox2D physics body for testing purposes. The body
     * is configured as a dynamic body type and assigned the specified userData
     * for identification during validation operations.
     * </p>
     * 
     * @param userData the string identifier to assign to the body's userData
     * @return a new JBox2D Body instance configured for testing
     */
    private Body createTestBody(String userData) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(0, 0);
        
        Body body = testWorld.createBody(bodyDef);
        body.setUserData(userData);
        
        return body;
    }
}
