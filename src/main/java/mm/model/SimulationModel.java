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
import mm.view.SimulationView;

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
 * <br>
 * <b>Note:</b> This class does not contain any UI or event handling code; it is
 * intended to be used
 * as the Model in an MVC architecture.
 * </p>
 *
 * <h3>Responsibilities:</h3>
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

    /**
     * Constructs a SimulationModel for a specific level.
     *
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
    public void setupInventory() {
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
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();

                if ((a != null && b != null)) {
                    if ((a.equals("ball1") && (b.equals("winPlat") || b.equals("winZone"))) ||
                            (b.equals("ball1") && (a.equals("winPlat") || a.equals("winZone")))) {

                        System.out.println("WIN! ball1 reached the win condition!");
                        if (winListener != null) {
                            winScreenVisible = true;
                            winListener.onWin();
                        }
                    }
                }
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

    // Callback-Interface
    public interface WinListener {
        void onWin();
    }

    private WinListener winListener;

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

        return new GameObject(
                template.getName(), template.getType(),
                new Position(x - offsetX, y - offsetY),
                template.getAngle(),
                template.getSize(),
                template.getColour(),
                template.getPhysics());
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
}
