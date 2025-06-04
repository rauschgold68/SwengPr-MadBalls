package mm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mm.model.objects.GameObject;
import mm.model.objects.Level;
import mm.model.objects.LevelReader;

public class ObjectImporter {

    private Level level;

    public ObjectImporter() {
        // Correct resource path for all OS
        InputStream is = getClass().getResourceAsStream("/level/standard_level.json");
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

    public static void main(String[] args) {}

}
