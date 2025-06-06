package mm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mm.gui.TitleScreen;
import mm.model.objects.Level;
import mm.model.objects.LevelReader;

import java.io.InputStream;

public class Main extends Application {

    public static Level loadedLevel;

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
        launch(args); // JavaFX starten
        System.out.println("Exiting...");
    }
}
