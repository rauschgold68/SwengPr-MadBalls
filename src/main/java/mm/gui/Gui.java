package mm.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Gui extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MadBalls©");

        // === Buttons ===
        Button btnPuzzle = new Button("Puzzles");
        Button btnSandbox = new Button("Sandbox");
        Button btnOptions = new Button("Options");
        Button btnQuit = new Button("Quit");

        // === Kreise (Reißzwecken) über Buttons ===
        Circle pinPuzzle = createPin(525, 460, Color.RED);
        Circle pinSandbox = createPin(670 + 50, 442, Color.BLUE);
        Circle pinOptions = createPin(850 + 58, 460, Color.GREEN);
        Circle pinQuit = createPin(800 + 56, 600, Color.YELLOW);

        // === Overlay und Optionsfenster ===
        StackPane overlayBackgroundOptions = new StackPane();
        overlayBackgroundOptions.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlayBackgroundOptions.setVisible(false);

        Pane optionsWindow = new Pane();
        optionsWindow.setPrefSize(600, 400);
        optionsWindow.setMaxSize(600, 400);
        optionsWindow.setStyle("-fx-background-color: rgba(46, 36, 87, 0.94); -fx-background-radius: 12;");

        // === Audio Options ===
        Label lblTitleAudio = new Label("Audio Options");
        lblTitleAudio.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        lblTitleAudio.setLayoutX(230);
        lblTitleAudio.setLayoutY(30);

        Label lblMusic = new Label("Musik-Lautstärke:");
        lblMusic.setStyle("-fx-text-fill: white;");
        lblMusic.setLayoutX(80);
        lblMusic.setLayoutY(80);

        Slider sliderMusic = new Slider(0, 100, 50);
        sliderMusic.setLayoutX(200);
        sliderMusic.setLayoutY(80);
        sliderMusic.setPrefWidth(300);

        Label lblSound = new Label("Soundeffekte:");
        lblSound.setStyle("-fx-text-fill: white;");
        lblSound.setLayoutX(80);
        lblSound.setLayoutY(130);

        Slider sliderSound = new Slider(0, 100, 50);
        sliderSound.setLayoutX(200);
        sliderSound.setLayoutY(130);
        sliderSound.setPrefWidth(300);

        // === Grafik Options ===
        Label lblTitleGraphics = new Label("Grafik");
        lblTitleGraphics.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        lblTitleGraphics.setLayoutX(260);
        lblTitleGraphics.setLayoutY(190);

        Label lblTexturePack = new Label("Texture Pack:");
        lblTexturePack.setStyle("-fx-text-fill: white;");
        lblTexturePack.setLayoutX(80);
        lblTexturePack.setLayoutY(230);

        Button btnUploadTexture = new Button("Upload...");
        btnUploadTexture.setLayoutX(200);
        btnUploadTexture.setLayoutY(225);

        // === Close-Button oben rechts ===
        Button btnCloseOptions = new Button("X");
        btnCloseOptions.setLayoutX(560);
        btnCloseOptions.setLayoutY(10);
        btnCloseOptions.setOnAction(e -> overlayBackgroundOptions.setVisible(false));

        StackPane overlayBackgroundPuzzle = new StackPane();
        overlayBackgroundPuzzle.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlayBackgroundPuzzle.setVisible(false);

        // === Puzzle Window ===
        Pane puzzleWindow = new Pane();
        puzzleWindow.setPrefSize(720, 480);
        puzzleWindow.setMaxSize(600, 400);
        puzzleWindow.setStyle("-fx-background-color: rgba(46, 36, 87, 0.94); -fx-background-radius: 12;");

        Label lblTitlePuzzle = new Label("Puzzle Picker");
        lblTitlePuzzle.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        lblTitlePuzzle.setLayoutX(230);
        lblTitlePuzzle.setLayoutY(30);

        // === Close-Button oben rechts ===
        Button btnClosePuzzle = new Button("X");
        btnClosePuzzle.setLayoutX(560);
        btnClosePuzzle.setLayoutY(10);
        btnClosePuzzle.setOnAction(e -> overlayBackgroundPuzzle.setVisible(false));

        puzzleWindow.getChildren().addAll(
                lblTitlePuzzle, btnClosePuzzle);

        optionsWindow.getChildren().addAll(
                lblTitleAudio, lblMusic, sliderMusic, lblSound, sliderSound,
                lblTitleGraphics, lblTexturePack, btnUploadTexture,
                btnCloseOptions);

        StackPane.setAlignment(optionsWindow, Pos.CENTER);
        overlayBackgroundOptions.getChildren().add(optionsWindow);

        StackPane.setAlignment(puzzleWindow, Pos.CENTER);
        overlayBackgroundPuzzle.getChildren().add(puzzleWindow);

        // === Button-Events ===
        btnPuzzle.setOnAction(e -> overlayBackgroundPuzzle.setVisible(true));
        btnSandbox.setOnAction(e -> System.out.println("Starting Sandbox Mode"));
        btnOptions.setOnAction(e -> overlayBackgroundOptions.setVisible(true));
        btnQuit.setOnAction(e -> {
            System.out.println("Quitting Game...");
            System.exit(0);
        });

        // === Hintergrundbild ===
        javafx.scene.image.Image backgroundImage = new javafx.scene.image.Image(
                getClass().getResource("MB_TitleScreen.png").toExternalForm());
        javafx.scene.image.ImageView backgroundView = new javafx.scene.image.ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);
        backgroundView.setFitWidth(1920);

        StackPane backgroundLayer = new StackPane(backgroundView);
        backgroundLayer.setPrefSize(1920, 1080);

        // === Buttons positionieren ===
        btnPuzzle.setLayoutX(480);
        btnPuzzle.setLayoutY(460);
        btnPuzzle.setRotate(-7);

        btnSandbox.setLayoutX(670);
        btnSandbox.setLayoutY(442);

        btnOptions.setLayoutX(850);
        btnOptions.setLayoutY(460);
        btnOptions.setRotate(6);

        btnQuit.setLayoutX(800);
        btnQuit.setLayoutY(600);
        btnQuit.setRotate(3);

        btnPuzzle.getStyleClass().add("btnTS");
        btnSandbox.getStyleClass().add("btnTS");
        btnOptions.getStyleClass().add("btnTS");
        btnQuit.getStyleClass().add("btnTS");

        // === Buttons & Pins in Layer ===
        Pane buttonLayer = new Pane();
        buttonLayer.getChildren().addAll(
                btnPuzzle, btnSandbox, btnOptions, btnQuit,
                pinPuzzle, pinSandbox, pinOptions, pinQuit);

        // === Root-StackPane ===
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundLayer, buttonLayer, overlayBackgroundPuzzle, overlayBackgroundOptions);

        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("titleScreen.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // === Hilfsmethode für Reißzweckenkreise ===
    private Circle createPin(double x, double y, Color color) {
        Circle pin = new Circle(6); // Radius 6px
        pin.setFill(color);
        pin.setLayoutX(x);
        pin.setLayoutY(y);
        pin.setStroke(Color.BLACK);
        pin.setStrokeWidth(1.5);
        return pin;
    }

    public static void main(String[] args) {
        launch(args);
    }
}