package mm.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mm.model.GameObject;
import mm.model.InventoryObject;
import mm.model.Level;

/**
 * Utility class for importing level data (GameObjects and InventoryObjects)
 * from a JSON resource file.
 * <p>
 * This class loads a level from a specified JSON file in the resources/level
 * directory.
 * It provides methods to retrieve the loaded {@link GameObject} and
 * {@link InventoryObject} instances
 * for use in the simulation or game logic.
 * </p>
 * <b>Usage example:</b>
 * 
 * <pre>
 * ObjectImporter importer = new ObjectImporter("/level/level1.json");
 * List&lt;GameObject&gt; objects = importer.getGameObjects();
 * List&lt;InventoryObject&gt; inventory = importer.getInventoryObjects();
 * </pre>
 */
public class LevelImportController {

    /** The loaded Level object containing game and inventory objects. */
    private Level level;

    /**
     * Constructs an ObjectImporter and loads the level from the given resource
     * path.
     * <p>
     * Attempts to load the specified JSON file from the application's resources. If
     * the file
     * cannot be found or parsed, error messages are printed to the standard error
     * stream.
     * </p>
     *
     * @param resourcePath the path to the level JSON file in resources (e.g.,
     *                     "/level/level1.json")
     */
    public LevelImportController(String resourcePath) {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            System.err.println("Level JSON not found in resources at: " + resourcePath);
            return;
        }
        LevelReadController levelTemplate = new LevelReadController(is);
        level = levelTemplate.readFile();

        if (level == null) {
            System.err.println("Failed to load level from: " + resourcePath);
        } else {
            // Apply current skin to loaded objects
            applySkinToLoadedObjects();
        }
    }

    /**
     * Applies the current skin selection to all loaded objects.
     * This ensures that imported levels use the correct skin textures.
     */
    private void applySkinToLoadedObjects() {
        SkinManagerController skinManager = SkinManagerController.getInstance();

        // Apply skin to inventory objects
        if (level.getInventoryObjects() != null) {
            skinManager.updateInventorySpritesForSkin(level.getInventoryObjects());
        }

        // Apply skin to game objects
        if (level.getLevelObjects() != null) {
            skinManager.updateGameObjectSpritesForSkin(level.getLevelObjects());
        }
    }

    /**
     * Returns the list of {@link GameObject} instances loaded from the level file.
     *
     * @return List of GameObjects, or an empty list if none are loaded or if
     *         loading failed.
     */
    public List<GameObject> getGameObjects() {
        if (level != null && level.getLevelObjects() != null) {
            return level.getLevelObjects();
        }
        return new ArrayList<>();
    }

    /**
     * Returns the list of {@link InventoryObject} instances loaded from the level
     * file.
     *
     * @return List of InventoryObjects, or an empty list if none are loaded or if
     *         loading failed.
     */
    public List<InventoryObject> getInventoryObjects() {
        if (level != null && level.getInventoryObjects() != null) {
            return level.getInventoryObjects();
        }
        return new ArrayList<>();
    }
}
