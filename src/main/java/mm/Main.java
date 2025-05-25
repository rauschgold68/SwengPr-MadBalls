package mm;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import mm.gui.Gui;
import mm.model.objects.GameObject;
import mm.model.objects.Level;
import mm.model.objects.LevelReader;

/**
 * The common starting point of the GUI.
 */
public class Main {
    /**
     * The external entry point of the application.
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        String filePath = "src/main/java/mm/model/level/";
        String fileName = "Standart_Level.JSON";
        LevelReader reader = new LevelReader(filePath, fileName);

        Level level = reader.readFile();

        
        /*System.out.println("Starting...");
        Gui.main(args);
        System.out.println("Exiting...");*/
    }
}
