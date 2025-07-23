package mm.model;

/**
 * Service class responsible for validating positions within the simulation.
 * This class handles checking if positions are valid for object placement,
 * collision detection, and zone validation.
 */
public class PositionValidationService {
    
    private final SimulationModel.PhysicsComponents physics;
    private final SimulationModel.GameObjectCollections gameObjects;
    
    /**
     * Constructs a PositionValidationService with access to simulation components.
     * 
     * @param physics the physics components containing pairs and geometry data
     * @param gameObjects the game object collections containing zone information
     */
    public PositionValidationService(SimulationModel.PhysicsComponents physics, 
                                   SimulationModel.GameObjectCollections gameObjects) {
        this.physics = physics;
        this.gameObjects = gameObjects;
    }
    
    /**
     * Checks if a given position is inside any no-place zone.
     * Uses geometry-based collision detection for accurate results.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the position is inside a no-place zone, false otherwise
     */
    public boolean isInNoPlaceZone(double x, double y) {
        for (PhysicsVisualPair zone : gameObjects.noPlaceZones) {
            // Find corresponding geometry pair
            int index = physics.pairs.indexOf(zone);
            if (index >= 0 && index < physics.geometryPairs.size()) {
                PhysicsGeometryPair geometryPair = physics.geometryPairs.get(index);
                // Combine nested if statements
                if (geometryPair.getGeometry() != null && geometryPair.getGeometry().containsPoint(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a given position is inside any win zone.
     * Uses geometry-based collision detection for accurate results.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the position is inside a win zone, false otherwise
     */
    public boolean isInWinZone(double x, double y) {
        for (int i = 0; i < physics.pairs.size(); i++) {
            PhysicsVisualPair pair = physics.pairs.get(i);
            // Combine nested if statements and add early continue for better readability
            if (!isWinZonePair(pair) || i >= physics.geometryPairs.size()) {
                continue;
            }
            
            PhysicsGeometryPair geometryPair = physics.geometryPairs.get(i);
            if (geometryPair.getGeometry() != null && geometryPair.getGeometry().containsPoint(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a physics-visual pair represents a win zone.
     * 
     * @param pair The pair to check
     * @return true if the pair is a win zone or win platform
     */
    private boolean isWinZonePair(PhysicsVisualPair pair) {
        Object userData = pair.body.getUserData();
        return "winZone".equals(userData) || "winPlat".equals(userData);
    }
}
