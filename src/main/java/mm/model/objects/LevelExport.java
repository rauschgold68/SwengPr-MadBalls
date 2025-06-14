package mm.model.objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import mm.FxToGameObject;
import mm.PhysicsVisualPair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for exporting the current state of a level to a JSON file.
 * <p>
 * This class converts the current simulation's physics-visual pairs and inventory objects
 * into a {@link Level} object and serializes it as a JSON file using Jackson.
 * The exported file is saved in the <code>src/main/resources/level/</code> directory
 * with a unique name (e.g., <code>fun1.json</code>, <code>fun2.json</code>, ...).
 * </p>
 * <p>
 * Usage example:
 * <pre>
 *     LevelExport exporter = new LevelExport();
 *     exporter.export(pairs, inventoryObjects);
 * </pre>
 * </p>
 */
public class LevelExport {
    /** Counter for generating unique filenames for exported levels. */
    int nextname = 1;

    /**
     * Exports the current simulation state to a JSON file.
     * <p>
     * Converts the provided list of {@link PhysicsVisualPair} objects into {@link GameObject}s,
     * combines them with the provided inventory objects, and writes the resulting {@link Level}
     * to a JSON file in the <code>src/main/resources/level/</code> directory.
     * </p>
     *
     * @param pairs            List of {@link PhysicsVisualPair} representing objects in the level.
     * @param inventoryObjects List of {@link InventoryObject} representing the inventory for the level.
     */
    public void export(List<PhysicsVisualPair> pairs, List<InventoryObject> inventoryObjects){
        Level levelOut = new Level();
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        
        // Convert each PhysicsVisualPair to a GameObject and add to the list
        for (PhysicsVisualPair pair : pairs) {
            GameObject obj = FxToGameObject.convertBack(pair);
            gameObjects.add(obj);
        }
        
        levelOut.setLevelObjects(gameObjects);
        levelOut.setInventoryObjects(inventoryObjects);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String path = "src/main/resources/level/";
            String name = "fun" + Integer.toString(nextname++);
            File file = new File(path + name + ".json");
            
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, levelOut);
        } catch (Exception e) {
            System.err.print(e + " occured while exporting");
        }

        System.out.println("export done!");
    }
}