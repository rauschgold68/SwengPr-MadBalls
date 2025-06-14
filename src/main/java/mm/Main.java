package mm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mm.gui.TitleScreen;
import mm.model.objects.Level;

/**
 * Entry point for the MadBalls application.
 * <p>
 * This class initializes and launches the JavaFX application, setting up the primary stage
 * with the title screen and configuring the main window properties.
 * </p>
 * <p>
 * The {@link #loadedLevel} static field holds the currently loaded level, which can be accessed
 * and modified throughout the application's lifecycle.
 * </p>
 * <p>
 * The {@link #main(String[])} method starts the JavaFX runtime, and the {@link #start(Stage)} method
 * sets up the initial scene and window configuration.
 * </p>
 */
public class Main extends Application {

    /**
     * The currently loaded level in the application.
     * This can be set or accessed by other components as needed.
     */
    public static Level loadedLevel;

    /**
     * Initializes the primary stage of the application.
     * <p>
     * Sets the window title, loads the title screen, and configures the window size and properties.
     * </p>
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) {
        TitleScreen titleScreen = new TitleScreen();
        Scene titleScene = titleScreen.createTitleScene(primaryStage);
        primaryStage.setTitle("MadBalls©");
        primaryStage.setScene(titleScene);

        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    /**
     * Main entry point for the application.
     * <p>
     * Launches the JavaFX application and prints status messages to the console.
     * </p>
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        System.out.println("Starting...");
        launch(args); // Start JavaFX application
        System.out.println("Exiting...");
    }
}
