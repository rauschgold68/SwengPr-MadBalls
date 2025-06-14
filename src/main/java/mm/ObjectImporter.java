package mm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mm.model.objects.GameObject;
import mm.model.objects.InventoryObject;
import mm.model.objects.Level;
import mm.model.objects.LevelReader;

/**
 * Utility class for importing level data (GameObjects and InventoryObjects) from a JSON resource file.
 * <p>
 * This class loads a level from a specified JSON file in the resources/level directory.
 * It provides methods to retrieve the loaded GameObjects and InventoryObjects for use in the simulation.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 *     ObjectImporter importer = new ObjectImporter("/level/level1.json");
 *     List&lt;GameObject&gt; objects = importer.getGameObjects();
 * </pre>
 * </p>
 */
public class ObjectImporter {

    /** The loaded Level object containing game and inventory objects. */
    private Level level;

    /**
     * Constructs an ObjectImporter and loads the level from the given resource path.
     *
     * @param resourcePath the path to the level JSON file in resources (e.g., "/level/level1.json")
     */
    public ObjectImporter(String resourcePath) {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            System.err.println("Level JSON not found in resources at: " + resourcePath);
            return;
        }
        LevelReader levelTemplate = new LevelReader(is);
        level = levelTemplate.readFile();

        if (level == null) {
            System.err.println("Failed to load level from: " + resourcePath);
        }
    }

    /**
     * Returns the list of GameObjects loaded from the level file.
     *
     * @return List of GameObjects, or an empty list if none are loaded.
     */
    public List<GameObject> getGameObjects() {
        if (level != null && level.getLevelObjects() != null) {
            return level.getLevelObjects();
        }
        return new ArrayList<>();
    }

    /**
     * Returns the list of InventoryObjects loaded from the level file.
     *
     * @return List of InventoryObjects, or an empty list if none are loaded.
     */
    public List<InventoryObject> getInventoryObjects() {
        if (level != null && level.getInventoryObjects() != null) {
            return level.getInventoryObjects();
        }
        return new ArrayList<>();
    }
}
