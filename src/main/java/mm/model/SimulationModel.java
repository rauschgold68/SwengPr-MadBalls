package mm.model;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

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

    /** The path to the current level JSON file. */
    private String levelPath;
    /** The Box2D physics world instance. */
    private World world;
    /** List of pairs of physics objects and their visual representations. */
    private List<PhysicsVisualPair> pairs = new ArrayList<>();
    /** List of game objects that have been dropped into the simulation. */
    private List<GameObject> droppedObjects = new ArrayList<>();
    /** List of inventory objects available for placement. */
    private List<InventoryObject> inventoryObjects = new ArrayList<>();
    /** List of no-place zones restricting object placement. */
    private List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
    /** List of droped objects but there visual representation */
    private List<PhysicsVisualPair> droppedVisualPairs = new ArrayList<>();
    /** The animation timer controlling the simulation loop. */
    private PhysicsAnimationController timer;
    /** Flag indicating if the win screen is currently visible. */
    private boolean winScreenVisible = false;
    /** Listener for win condition events in the simulation. */
    private WinListener winListener;
    private final UndoRedoController UndoRedoController = new UndoRedoController();

    /**
     * Constructs a SimulationModel for a specific level.
     * @param levelPath the resource path to the level JSON file (e.g.,
     *                  "/level/level1.json")
     */
    public SimulationModel(String levelPath) {
        this.levelPath = levelPath;
    }

    /**
     * Returns the Box2D physics world.
     * 
     * @return the physics world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns the list of physics-visual pairs in the simulation.
     * 
     * @return the list of PhysicsVisualPair objects
     */
    public List<PhysicsVisualPair> getPairs() {
        return pairs;
    }

    /**
     * Returns the list of dropped game objects.
     * 
     * @return the list of dropped GameObject instances
     */
    public List<GameObject> getDroppedObjects() {
        return droppedObjects;
    }

    /**
     * Returns the list of dropped game objects as their representation in visual
     * and body.
     * 
     * @return the list of dropped GameObjects as their PhysicalVisualPair
     *         representation.
     */
    public List<PhysicsVisualPair> getDroppedPhysicsVisualPairs() {
        return droppedVisualPairs;
    }

    /**
     * Returns the list of inventory objects available for placement.
     * 
     * @return the list of InventoryObject instances
     */
    public List<InventoryObject> getInventoryObjects() {
        return inventoryObjects;
    }

    /**
     * Returns the list of no-place zones.
     * 
     * @return the list of PhysicsVisualPair objects representing no-place zones
     */
    public List<PhysicsVisualPair> getNoPlaceZones() {
        return noPlaceZones;
    }

    /**
     * Returns the animation timer for the simulation.
     * 
     * @return the ResettableAnimationTimer instance
     */
    public PhysicsAnimationController getTimer() {
        return timer;
    }

    /**
     * Returns the current level path.
     * 
     * @return the level path string
     */
    public String getLevelPath() {
        return levelPath;
    }

    /**
     * Checks if the win screen is currently visible.
     * 
     * @return true if the win screen is visible, false otherwise
     */
    public boolean isWinScreenVisible() {
        return winScreenVisible;
    }

    /**
     * Sets the Box2D physics world.
     * 
     * @param world the physics world to set
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Sets the list of physics-visual pairs.
     * 
     * @param pairs the list of PhysicsVisualPair objects to set
     */
    public void setPairs(List<PhysicsVisualPair> pairs) {
        this.pairs = pairs;
    }

    /**
     * Sets the list of dropped game objects.
     * 
     * @param droppedObjects the list of GameObject instances to set
     */
    public void setDroppedObjects(List<GameObject> droppedObjects) {
        this.droppedObjects = droppedObjects;
    }

    /**
     * Sets the list of dropped PhysicsVisualPairs
     * 
     * @param droppedPhysicsVisualPairs
     */
    public void setDroppedVisualPairs(List<PhysicsVisualPair> droppedPhysicsVisualPairs) {
        this.droppedVisualPairs = droppedPhysicsVisualPairs;
    }

    /**
     * Sets the list of inventory objects.
     * 
     * @param inventoryObjects the list of InventoryObject instances to set
     */
    public void setInventoryObjects(List<InventoryObject> inventoryObjects) {
        this.inventoryObjects = inventoryObjects;
    }

    /**
     * Sets the list of no-place zones.
     * 
     * @param noPlaceZones the list of PhysicsVisualPair objects to set as no-place
     *                     zones
     */
    public void setNoPlaceZones(List<PhysicsVisualPair> noPlaceZones) {
        this.noPlaceZones = noPlaceZones;
    }

    /**
     * Sets the animation timer for the simulation.
     * 
     * @param timer the PhysicsAnimationController to set
     */
    public void setTimer(PhysicsAnimationController timer) {
        this.timer = timer;
    }

    /**
     * Sets the current level path.
     * 
     * @param levelPath the level path string to set
     */
    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    /**
     * Loads the level and initializes the physics world and objects.
     * <p>
     * This method creates a new physics world, loads game objects from the level
     * file,
     * converts them to physics-visual pairs, and sets up no-place zones and the
     * animation timer.
     * It also adds any previously dropped objects and sets up the contact listener
     * for win conditions.
     * </p>
     */
    public void setupSimulation() {
        world = new World(new Vec2(0.0f, 9.8f));
        pairs = new ArrayList<>();
        noPlaceZones = new ArrayList<>();

        LevelImportController importer = new LevelImportController(levelPath);
        List<GameObject> gameObjects = importer.getGameObjects();

        // Add level objects
        for (GameObject obj : gameObjects) {
            PhysicsVisualPair pair = GameObjectController.convert(obj, world);
            pairs.add(pair);
            if (obj.getName().equals("noPlaceZone")) {
                noPlaceZones.add(pair);
            }
        }

        // Add dropped objects
        for (GameObject obj : droppedObjects) {
            PhysicsVisualPair pair = GameObjectController.convert(obj, world);
            pairs.add(pair);
            if (obj.getName().equals("noPlaceZone")) {
                noPlaceZones.add(pair);
            }
        }

        timer = new PhysicsAnimationController(world, pairs);

        listenContact();
    }

    /**
     * Loads inventory objects for the current level.
     * <p>
     * This method uses the LevelImportController to load inventory objects from the
     * level file.
     * </p>
     */
    public void setupInvetoryData() {
        LevelImportController importer = new LevelImportController(levelPath);
        inventoryObjects = importer.getInventoryObjects();
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
        droppedObjects.add(obj);
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
        LE.export(this.pairs, this.inventoryObjects);
    }

    /**
     * Sets up a contact listener for the physics world to detect collisions.
     * <p>
     * This listener checks for win conditions, such as the ball reaching the win
     * platform or zone.
     * </p>
     */
    private void listenContact() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                handleContactBegin(contact);

            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }

    /**
     * Handles the beginning of a contact between two physics bodies.
     * <p>
     * Extracts user data from both fixtures and checks for win conditions.
     * This method reduces nesting by early returns and delegates win condition
     * checking to a separate method.
     * </p>
     *
     * @param contact The contact event containing information about the colliding bodies
     */
    private void handleContactBegin(Contact contact) {
        Object userDataA = contact.getFixtureA().getBody().getUserData();
        Object userDataB = contact.getFixtureB().getBody().getUserData();

        // Early return if either body has no user data
        if (userDataA == null || userDataB == null) {
            return;
        }

        // Check for win condition and trigger if found
        if (isWinCondition(userDataA, userDataB)) {
            triggerWinCondition();
        }
    }

    /**
     * Determines if the contact between two objects represents a win condition.
     * <p>
     * A win condition occurs when a "winObject" comes into contact with either
     * a "winPlat" (win platform) or "winZone" (win zone). This method handles
     * both possible collision orders (A-B and B-A).
     * </p>
     *
     * @param userDataA The user data from the first colliding object
     * @param userDataB The user data from the second colliding object
     * @return true if this contact represents a win condition, false otherwise
     */
    private boolean isWinCondition(Object userDataA, Object userDataB) {
        return isWinObjectToTargetContact(userDataA, userDataB) || 
               isWinObjectToTargetContact(userDataB, userDataA);
    }

    /**
     * Checks if the first object is a win object and the second is a valid win target.
     * <p>
     * This helper method reduces code duplication by checking one direction of the
     * win condition (winObject touching winPlat or winZone).
     * </p>
     *
     * @param objectA The user data from the first object
     * @param objectB The user data from the second object
     * @return true if objectA is "winObject" and objectB is a win target
     */
    private boolean isWinObjectToTargetContact(Object objectA, Object objectB) {
        return "winObject".equals(objectA) && isWinTarget(objectB);
    }

    /**
     * Determines if an object is a valid win target.
     * <p>
     * Win targets are objects that, when touched by a win object, trigger
     * the win condition. Currently includes "winPlat" and "winZone".
     * </p>
     *
     * @param userData The user data from the object to check
     * @return true if the object is a valid win target
     */
    private boolean isWinTarget(Object userData) {
        return "winPlat".equals(userData) || "winZone".equals(userData);
    }

    /**
     * Triggers the win condition by stopping the simulation and notifying listeners.
     * <p>
     * This method handles all the actions that occur when a win condition is met:
     * logging the win, stopping the physics timer, setting the win screen visibility,
     * and notifying any registered win listeners.
     * </p>
     */
    private void triggerWinCondition() {
        System.out.println("WIN! ball1 reached the win condition!");
        
        // Defensive check - listener should always be set, but handle gracefully if not
        if (winListener == null) {
            System.err.println("Warning: Win condition triggered but no listener is registered");
            return;
        }

        // Stop the physics simulation
        timer.stop();
        
        // Update UI state
        winScreenVisible = true;
        
        // Notify the listener
        winListener.onWin();
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

    public void setWinListener(WinListener listener) {
        this.winListener = listener;
    }

    /**
     * Finds an inventory object template by its name.
     *
     * @param name the name of the inventory object to find
     * @return the InventoryObject with the given name, or null if not found
     */
    public InventoryObject findInventoryObjectByName(String name) {
        for (InventoryObject obj : inventoryObjects) {
            if (obj.getName().equals(name)) {
                return obj;
            }
        }
        return null;
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
        float offsetX = (float) (template.getSize().getWidth() / 2.0);
        float offsetY = (float) (template.getSize().getHeight() / 2.0);

        // Create GameObject with basic constructor
        // The position represents the top-left corner for rectangles (consistent with JavaFX positioning)
        // For drop coordinates (x,y), we want the center of the object to be at that position
        // So we calculate the top-left corner by subtracting half the dimensions
        GameObject gameObject = new GameObject(
                template.getName(), 
                template.getType(),
                new Position(x - offsetX, y - offsetY),
                template.getSize());
        
        // Set additional properties using setters
        gameObject.setPhysics(template.getPhysics());
        gameObject.setAngle(template.getAngle());
        gameObject.setColour(template.getColour());
        gameObject.setSprite(template.getSprite());
        gameObject.setWinning(template.isWinning());

        template.setCount(template.getCount() - 1);
        
        return gameObject;
    }

    /**
     * Checks if a given position is inside any no-place zone.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the position is inside a no-place zone, false otherwise
     */
    public boolean isInNoPlaceZone(double x, double y) {
        for (PhysicsVisualPair zone : noPlaceZones) {
            if (zone.visual instanceof javafx.scene.shape.Rectangle) {
                javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) zone.visual;
                double zoneX = rect.getTranslateX();
                double zoneY = rect.getTranslateY();
                double zoneW = rect.getWidth();
                double zoneH = rect.getHeight();
                if (x >= zoneX && x <= zoneX + zoneW && y >= zoneY && y <= zoneY + zoneH) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Restores inventory counts for all dropped objects.
     * <p>
     * This method should be called when clearing all dropped objects to return
     * their counts to the inventory.
     * </p>
     */
    public void restoreInventoryCounts() {
        for (GameObject droppedObj : droppedObjects) {
            InventoryObject inventoryTemplate = findInventoryObjectByName(droppedObj.getName());
            if (inventoryTemplate != null) {
                inventoryTemplate.setCount(inventoryTemplate.getCount() + 1);
            }
        }
    }

    /**
     * Gets the undo/redo manager for this simulation.
     */
    public UndoRedoController getUndoRedoManager() {
        return UndoRedoController;
    }
    
    /**
     * Increments the inventory count for a specific item.
     * Used when undoing object placement.
     */
    public void incrementInventoryCount(String itemName) {
        for (InventoryObject obj : inventoryObjects) {
            if (obj.getName().equals(itemName)) {
                obj.setCount(obj.getCount() + 1);
                break;
            }
        }
    }
}
