package mm.model;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import javafx.scene.layout.Pane;
import mm.controller.GameObjectController;
import mm.controller.LevelExportController;
import mm.controller.LevelImportController;
import mm.controller.PhysicsAnimationController;
import mm.controller.UndoRedoController;

/**
 * The SimulationModel class encapsulates the core simulation state and logic
 * for the MadBalls game.
 * <p>
 * This class is responsible for managing the simulation's data, including the
 * physics world,
 * game objects, inventory, and simulation state. It provides methods to
 * initialize the simulation,
 * manage inventory and dropped objects, export the current level, and handle
 * collision events.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Loading and initializing the physics world and game objects from a level
 * file</li>
 * <li>Managing inventory objects and dropped objects</li>
 * <li>Tracking no-place zones for object placement restrictions</li>
 * <li>Providing methods for creating and finding objects</li>
 * <li>Exporting the current simulation state</li>
 * <li>Setting up collision listeners for win conditions</li>
 * </ul>
 */
public class SimulationModel {

    private final PhysicsComponents physics = new PhysicsComponents();
    private final GameObjectCollections gameObjects = new GameObjectCollections();
    private final SimulationState state = new SimulationState();
    private final UndoRedoController undoRedoController = new UndoRedoController();

    private final CollisionDetection collisionService;
    private final GeometricCollisionDetection geometricCollisionService;
    private final JsonStateService jsonService;
    private final PositionValidationService positionValidationService;
    private final InventoryManagementService inventoryManagementService;
    private final WinEventService contactEventService;

    /**
     * Container for physics-related simulation components.
     */
    public static class PhysicsComponents {
        /** The Box2D physics world instance. */
        public World world;
        /** List of pairs of physics objects and their visual representations. */
        public List<PhysicsVisualPair> pairs = new ArrayList<>();
        /** List of geometry pairs (view-agnostic). */
        public List<PhysicsGeometryPair> geometryPairs = new ArrayList<>();
        /** The animation timer controlling the simulation loop. */
        public PhysicsAnimationController timer;
    }

    /**
     * Container for game object collections.
     */
    public static class GameObjectCollections {
        /** List of game objects that have been dropped into the simulation. */
        public List<GameObject> droppedObjects = new ArrayList<>();
        /** List of inventory objects available for placement. */
        public List<InventoryObject> inventoryObjects = new ArrayList<>();
        /** List of no-place zones restricting object placement. */
        public List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        /** List of dropped objects but their visual representation */
        public List<PhysicsVisualPair> droppedVisualPairs = new ArrayList<>();
    }

    /**
     * Container for simulation state and configuration.
     */
    public static class SimulationState {
        /** The path to the current level JSON file. */
        public String levelPath;
        /** Flag indicating if the win screen is currently visible. */
        public boolean winScreenVisible = false;
        /** Listener for win condition events in the simulation. */
        public WinListener winListener;
    }

    // Grouped component containers
    

    /**
     * Constructs a SimulationModel for a specific level.
     * @param levelPath the resource path to the level JSON file (e.g.,
     *                  "/level/level1.json")
     */
    public SimulationModel(String levelPath) {
        this.state.levelPath = levelPath;
        this.collisionService = new CollisionDetection(this);
        this.geometricCollisionService = new GeometricCollisionDetection(this);
        this.jsonService = new JsonStateService();
        this.positionValidationService = new PositionValidationService(physics, gameObjects);
        this.inventoryManagementService = new InventoryManagementService(gameObjects);
        this.contactEventService = new WinEventService(physics, state);
    }

    /**
     * Returns the Box2D physics world.
     * 
     * @return the physics world
     */
    public World getWorld() {
        return physics.world;
    }

    /**
     * Returns the list of physics-visual pairs in the simulation.
     * 
     * @return the list of PhysicsVisualPair objects
     */
    public List<PhysicsVisualPair> getPairs() {
        return physics.pairs;
    }

    /**
     * Returns the list of physics-geometry pairs in the simulation.
     * 
     * @return the list of PhysicsGeometryPair objects
     */
    public List<PhysicsGeometryPair> getGeometryPairs() {
        return physics.geometryPairs;
    }

    /**
     * Returns the list of dropped game objects.
     * 
     * @return the list of dropped GameObject instances
     */
    public List<GameObject> getDroppedObjects() {
        return gameObjects.droppedObjects;
    }

    /**
     * Returns the list of dropped game objects as their representation in visual
     * and body.
     * 
     * @return the list of dropped GameObjects as their PhysicalVisualPair
     *         representation.
     */
    public List<PhysicsVisualPair> getDroppedPhysicsVisualPairs() {
        return gameObjects.droppedVisualPairs;
    }

    /**
     * Returns the list of inventory objects available for placement.
     * 
     * @return the list of InventoryObject instances
     */
    public List<InventoryObject> getInventoryObjects() {
        return gameObjects.inventoryObjects;
    }

    /**
     * Returns the list of no-place zones.
     * 
     * @return the list of PhysicsVisualPair objects representing no-place zones
     */
    public List<PhysicsVisualPair> getNoPlaceZones() {
        return gameObjects.noPlaceZones;
    }

    /**
     * Returns the animation timer for the simulation.
     * 
     * @return the ResettableAnimationTimer instance
     */
    public PhysicsAnimationController getTimer() {
        return physics.timer;
    }

    /**
     * Returns the geometric collision detection service.
     * 
     * @return the collision detection service using view-agnostic geometry
     */
    public GeometricCollisionDetection getGeometricCollisionService() {
        return geometricCollisionService;
    }

    /**
     * Returns the current level path.
     * 
     * @return the level path string
     */
    public String getLevelPath() {
        return state.levelPath;
    }

    /**
     * Checks if the win screen is currently visible.
     * 
     * @return true if the win screen is visible, false otherwise
     */
    public boolean isWinScreenVisible() {
        return state.winScreenVisible;
    }

    /**
     * Sets the Box2D physics world.
     * 
     * @param world the physics world to set
     */
    public void setWorld(World world) {
        this.physics.world = world;
    }

    /**
     * Sets the list of physics-visual pairs.
     * 
     * @param pairs the list of PhysicsVisualPair objects to set
     */
    public void setPairs(List<PhysicsVisualPair> pairs) {
        this.physics.pairs = pairs;
    }

    /**
     * Sets the list of dropped game objects.
     * 
     * @param droppedObjects the list of GameObject instances to set
     */
    public void setDroppedObjects(List<GameObject> droppedObjects) {
        this.gameObjects.droppedObjects = droppedObjects;
    }

    /**
     * Sets the list of dropped PhysicsVisualPairs
     * 
     * @param droppedPhysicsVisualPairs
     */
    public void setDroppedVisualPairs(List<PhysicsVisualPair> droppedPhysicsVisualPairs) {
        this.gameObjects.droppedVisualPairs = droppedPhysicsVisualPairs;
    }

    /**
     * Sets the list of inventory objects.
     * 
     * @param inventoryObjects the list of InventoryObject instances to set
     */
    public void setInventoryObjects(List<InventoryObject> inventoryObjects) {
        this.gameObjects.inventoryObjects = inventoryObjects;
    }

    /**
     * Sets the list of no-place zones.
     * 
     * @param noPlaceZones the list of PhysicsVisualPair objects to set as no-place
     *                     zones
     */
    public void setNoPlaceZones(List<PhysicsVisualPair> noPlaceZones) {
        this.gameObjects.noPlaceZones = noPlaceZones;
    }

    /**
     * Sets the animation timer for the simulation.
     * 
     * @param timer the PhysicsAnimationController to set
     */
    public void setTimer(PhysicsAnimationController timer) {
        this.physics.timer = timer;
    }

    /**
     * Sets the current level path.
     * 
     * @param levelPath the level path string to set
     */
    public void setLevelPath(String levelPath) {
        this.state.levelPath = levelPath;
    }

    /**
     * Loads the level and initializes the physics world and objects.
     * <p>
     * This method creates a new physics world, loads game objects from the level
     * file, converts them to physics-visual pairs, and sets up no-place zones.
     * It also adds any previously dropped objects and sets up the contact listener
     * for win conditions.
     * </p>
     */
    public void setupSimulation() {
        physics.world = new World(new Vec2(0.0f, 9.8f));
        physics.pairs = new ArrayList<>();
        physics.geometryPairs = new ArrayList<>();
        gameObjects.noPlaceZones = new ArrayList<>();

        LevelImportController importer = new LevelImportController(state.levelPath);
        List<GameObject> levelGameObjects = importer.getGameObjects();

        // Add level objects
        for (GameObject obj : levelGameObjects) {
            PhysicsVisualPair pair = GameObjectController.convert(obj, physics.world);
            addPhysicsVisualPair(pair);
            if (obj.getName().equals("noPlaceZone")) {
                gameObjects.noPlaceZones.add(pair);
            }
        }

        // Add dropped objects
        for (GameObject obj : gameObjects.droppedObjects) {
            PhysicsVisualPair pair = GameObjectController.convert(obj, physics.world);
            addPhysicsVisualPair(pair);
            if (obj.getName().equals("noPlaceZone")) {
                gameObjects.noPlaceZones.add(pair);
            }
        }

        // Initialize timer without simSpace - will be connected by controller
        physics.timer = new PhysicsAnimationController(physics.world, physics.pairs, this);

        contactEventService.setupContactListener();
    }

    /**
     * Connects the simulation to a JavaFX pane for visualization.
     * This method should be called by the controller after setupSimulation().
     * 
     * @param simSpace the Pane representing the simulation space in the view
     */
    public void connectToView(Pane simSpace) {
        if (physics.timer != null) {
            physics.timer.setSimSpace(simSpace);
        }
    }

    /**
     * Loads inventory objects for the current level.
     * <p>
     * This method uses the LevelImportController to load inventory objects from the
     * level file.
     * </p>
     */
    public void setupInvetoryData() {
        LevelImportController importer = new LevelImportController(state.levelPath);
        gameObjects.inventoryObjects = importer.getInventoryObjects();
    }

    /**
     * Adds a dropped object to the simulation.
     * <p>
     * The object will be included in the simulation on the next setup.
     * </p>
     * 
     * @param obj the GameObject to add as dropped
     */
    public void addDroppedObject(GameObject obj) {
        gameObjects.droppedObjects.add(obj);
        // Update the animation timer cache if it exists
        if (physics.timer != null) {
            physics.timer.updateObjectCache();
        }
    }

    /**
     * Exports the current level state to a JSON file.
     * <p>
     * Uses LevelExportController to save the current simulation state, including
     * all objects and inventory.
     * </p>
     */
    public void exportLevel() {
        LevelExportController LE = new LevelExportController();
        LE.export(this.physics.pairs, this.gameObjects.inventoryObjects);
    }

    /**
     * Interface for listening to win events in the simulation.
     * <p>
     * Implementations of this interface can receive notifications when the
     * win condition is met (e.g., when the ball reaches the win platform or zone).
     * </p>
     */
    public interface WinListener {
        /**
         * Called when the win condition is triggered.
         * <p>
         * This method is invoked when a collision is detected between the win object
         * and a win platform or win zone, indicating that the level has been completed.
         * </p>
         */
        void onWin();
    }
    /**
     * Sets the win listener for the simulation.
     * <p>
     * The win listener will be notified when a win condition is triggered,
     * such as when the win object collides with a win platform or win zone.
     * </p>
     * 
     * @param listener the WinListener to be notified of win events
     */
    public void setWinListener(WinListener listener) {
        this.state.winListener = listener;
    }

    /**
     * Finds an inventory object template by its name.
     *
     * @param name the name of the inventory object to find
     * @return the InventoryObject with the given name, or null if not found
     */
    public InventoryObject findInventoryObjectByName(String name) {
        return inventoryManagementService.findInventoryObjectByName(name);
    }

    /**
     * Creates a new GameObject from an InventoryObject template and a specified
     * position.
     *
     * @param template the InventoryObject template to use
     * @param x        the x-coordinate for the new object
     * @param y        the y-coordinate for the new object
     * @return a new GameObject instance based on the template and position
     */
    public GameObject createGameObjectFromInventory(InventoryObject template, float x, float y) {
        return inventoryManagementService.createGameObjectFromInventory(template, x, y);
    }

    /**
     * Checks if a given position is inside any no-place zone.
     * Now uses geometry-based collision detection.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the position is inside a no-place zone, false otherwise
     */
    public boolean isInNoPlaceZone(double x, double y) {
        return positionValidationService.isInNoPlaceZone(x, y);
    }

    /**
     * Checks if a given position is inside any win zone.
     * Now uses geometry-based collision detection.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the position is inside a win zone, false otherwise
     */
    public boolean isInWinZone(double x, double y) {
        return positionValidationService.isInWinZone(x, y);
    }

    /**
     * Restores inventory counts for all dropped objects.
     * <p>
     * This method should be called when clearing all dropped objects to return
     * their counts to the inventory.
     * </p>
     */
    public void restoreInventoryCounts() {
        inventoryManagementService.restoreInventoryCounts(gameObjects.droppedObjects);
    }

    /**
     * Gets the undo/redo manager for this simulation.
     */
    public UndoRedoController getUndoRedoManager() {
        return undoRedoController;
    }
    
    /**
     * Increments the inventory count for a specific item.
     * Used when undoing object placement.
     */
    public void incrementInventoryCount(String itemName) {
        inventoryManagementService.incrementInventoryCount(itemName);
    }
    
    /**
     * Decrements the inventory count for a specific item.
     * Used when redoing object placement.
     */
    public void decrementInventoryCount(String itemName) {
        inventoryManagementService.decrementInventoryCount(itemName);
    }

    /**
     * Checks if moving an object would cause a collision.
     * Delegates to the collision detection service.
     * 
     * @param movingPair The physics-visual pair being moved
     * @param newX The proposed new X position
     * @param newY The proposed new Y position
     * @return true if collision would occur, false otherwise
     */
    public boolean wouldCauseOverlap(PhysicsVisualPair movingPair, double newX, double newY) {
        return collisionService.wouldCauseOverlap(movingPair, newX, newY);
    }

    /**
     * Checks if moving an object would cause a collision.
     * Delegates to the collision detection service.
     * 
     * @param movingPair The physics-visual pair being moved
     * @param newX The proposed new X position
     * @param newY The proposed new Y position
     * @param newAngle The proposed new angle
     * @return true if collision would occur, false otherwise
     */
    public boolean wouldCauseOverlap(PhysicsVisualPair movingPair, double newX, double newY, float newAngle) {
        return collisionService.wouldCauseOverlap(movingPair, newX, newY, newAngle);
    }

    /**
     * Adds a physics-visual pair and creates the corresponding geometry pair.
     * This method maintains synchronization between the old and new systems.
     */
    public void addPhysicsVisualPair(PhysicsVisualPair visualPair) {
        physics.pairs.add(visualPair);
        // Create corresponding geometry pair
        PhysicsGeometryPair geometryPair = mm.controller.GeometryConverter.fromVisualPair(visualPair);
        physics.geometryPairs.add(geometryPair);
    }

    /**
     * Generates JSON representation of current simulation state using Jackson.
     */
    public String generateCurrentStateJson() {
        return jsonService.generateStateJson(gameObjects.droppedObjects, gameObjects.inventoryObjects);
    }

    /**
     * Updates simulation state from JSON string using Jackson.
     */
    public boolean updateFromJson(String jsonString) {
        try {
            JsonStateService.SimulationState state = jsonService.parseStateJson(jsonString);
            
            // Clear current state
            gameObjects.droppedObjects.clear();
            gameObjects.droppedVisualPairs.clear();
            
            // Update dropped objects
            gameObjects.droppedObjects.addAll(state.getDroppedObjects());
            
            // Update inventory counts
            updateInventoryFromState(state.getInventoryObjects());
            
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update from JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates JSON format without applying changes.
     */
    public boolean isValidSimulationJson(String jsonString) {
        return jsonService.isValidJson(jsonString);
    }
    
    /**
     * Validates JSON format and provides detailed error information.
     */
    public JsonStateService.ValidationResult validateSimulationJson(String jsonString) {
        // Get simulation space bounds for validation
        double simSpaceWidth = getSimulationSpaceWidth();
        double simSpaceHeight = getSimulationSpaceHeight();
        
        return jsonService.validateJson(jsonString, simSpaceWidth, simSpaceHeight);
    }
    
    /**
     * Gets the current simulation space width.
     * @return the width of the simulation space, or 0 if not available
     */
    private double getSimulationSpaceWidth() {
        if (physics.timer != null) {
            return ((PhysicsAnimationController) physics.timer).getSimSpaceWidth();
        }
        return 0;
    }
    
    /**
     * Gets the current simulation space height.
     * @return the height of the simulation space, or 0 if not available
     */
    private double getSimulationSpaceHeight() {
        if (physics.timer != null) {
            return ((PhysicsAnimationController) physics.timer).getSimSpaceHeight();
        }
        return 0;
    }
    
    private void updateInventoryFromState(List<InventoryObject> newInventoryObjects) {
        // Create a set of names from new inventory for quick lookup
        java.util.Set<String> newInventoryNames = newInventoryObjects.stream()
                .map(InventoryObject::getName)
                .collect(java.util.stream.Collectors.toSet());
        
        // Remove items that are not in the new inventory (deletion)
        gameObjects.inventoryObjects.removeIf(existing -> !newInventoryNames.contains(existing.getName()));
        
        // Update existing items and add new ones
        for (InventoryObject newObj : newInventoryObjects) {
            InventoryObject existing = findInventoryObjectByName(newObj.getName());
            if (existing != null) {
                // Update existing item
                existing.setCount(newObj.getCount());
                existing.setAngle(newObj.getAngle());
                existing.setPhysics(newObj.getPhysics());
                existing.setSize(newObj.getSize());
                existing.setSprite(newObj.getSprite());
                existing.setWinning(newObj.isWinning());
                existing.setColour(newObj.getColour());
                
                // Remove item if count is 0 or negative
                if (existing.getCount() <= 0) {
                    gameObjects.inventoryObjects.remove(existing);
                }
            } else if (newObj.getCount() > 0) {
                // Add new item only if count is positive
                gameObjects.inventoryObjects.add(newObj);
            }
        }
    }
}