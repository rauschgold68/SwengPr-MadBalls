package mm.model.objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import mm.FxToGameObject;
import mm.PhysicsVisualPair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LevelExport {
    int nextname = 1;
    public void export(List<PhysicsVisualPair> pairs, List<InventoryObject> inventoryObjects){
        Level levelOut = new Level();
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        
        //Takes Objects in Level and Creates GameObjects out of each and appends them to a List.
        for (PhysicsVisualPair pair : pairs) {
            GameObject obj = FxToGameObject.convertBack(pair);
            gameObjects.add(obj);
        }
        
        levelOut.setLevelObjects(gameObjects);
        levelOut.setInventoryObjects(inventoryObjects);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String path = "src/main/resources/level/";
            String name = "test" + Integer.toString(nextname++);
            File file = new File(path + name + ".json");
            
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, levelOut);
        } catch (Exception e) {
            System.err.print(e + "occured while exporting");
        }

        System.out.println("export done!");
    }
}