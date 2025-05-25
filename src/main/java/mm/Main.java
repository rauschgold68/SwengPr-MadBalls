package mm;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import mm.gui.Gui;
import mm.model.level.GameObject;
import mm.model.level.Level;

/**
 * The common starting point of the GUI.
 */
public class Main {
    /**
     * The external entry point of the application.
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Level testLevel = mapper.readValue(new File("src/main/java/mm/model/level/Standart_Level.JSON"), Level.class);
        } catch (Exception e) {
            System.err.println(e +"occured in line:" +  22);
        }
        

        /*System.out.println("Starting...");
        Gui.main(args);
        System.out.println("Exiting...");*/
    }
}
