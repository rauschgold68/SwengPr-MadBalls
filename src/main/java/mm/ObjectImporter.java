package mm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mm.model.objects.GameObject;
import mm.model.objects.InventoryObject;
import mm.model.objects.Level;
import mm.model.objects.LevelReader;

public class ObjectImporter {

    private Level level;

    public ObjectImporter() {
        // Correct resource path for all OS
        InputStream is = getClass().getResourceAsStream("/level/basic_sandbox.json");
        if (is == null) {
            System.err.println("Level JSON not found in resources!");
            return;
        }
        LevelReader levelTemplate = new LevelReader(is);
        level = levelTemplate.readFile();

        if (level == null || level.getLevelObjects() == null) {
            System.err.println("No level objects found or failed to load level.");
        }
    }

    public List<GameObject> getGameObjects() {
        if (level != null && level.getLevelObjects() != null) {
            return level.getLevelObjects();
        }
        return new ArrayList<>();
    }

    public List<InventoryObject> getInventoryObjects(){
        if (level != null && level.getInventoryObjects() != null){
            return level.getInventoryObjects();
        }
        return new ArrayList<>();
    }

}
