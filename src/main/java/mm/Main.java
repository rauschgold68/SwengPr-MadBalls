package mm;

import java.io.InputStream;

import mm.gui.TitleScreen;
import mm.model.objects.Level;
import mm.model.objects.LevelReader;

/**
 * The common starting point of the GUI.
 */
public class Main {
    /**
     * The external entry point of the application.
     * 
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {

        // Load JSON from resources (works on all OS)
        InputStream is = Main.class.getResourceAsStream("/mm/model/level/Standart_Level.JSON");
        if (is == null) {
            System.err.println("Level JSON not found in resources!");
        } else {
            LevelReader reader = new LevelReader(is);
            Level level = reader.readFile();
            // You can use 'level' here if needed
        }

        System.out.println("Starting...");
        TitleScreen.main(args);
        System.out.println("Exiting...");

    }
}
