package mm;

import mm.gui.titleScreen;
import mm.model.Example;

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
        Example example = new Example(0);
        System.out.println("Starting...");
        titleScreen.main(args);
        System.out.println("Exiting...");
    }
}
