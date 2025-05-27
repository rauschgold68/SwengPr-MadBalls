package mm.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class TitleScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MadBalls©");

        // === Buttons ===
        Button btnPuzzle = new Button("Puzzles");
        Button btnSandbox = new Button("Sandbox");
        Button btnOptions = new Button("Options");
        Button btnQuit = new Button("Quit");

        btnPuzzle.setRotate(-7);
        btnSandbox.setRotate(0);
        btnOptions.setRotate(5);
        btnQuit.setRotate(-3);

        // === Buttons mit Pins kombinieren ===
        HBox topRow = new HBox(60,
                btnWithPin(btnPuzzle, Color.RED),
                btnWithPin(btnSandbox, Color.BLUE),
                btnWithPin(btnOptions, Color.GREEN));
        topRow.setAlignment(Pos.CENTER);

        HBox bottomRow = new HBox(btnWithPin(btnQuit, Color.YELLOW));
        bottomRow.setAlignment(Pos.BASELINE_RIGHT);

        VBox buttonLayer = new VBox(40, topRow, bottomRow);
        buttonLayer.setAlignment(Pos.CENTER);

        // === Bilder laden ===
        Image backgroundLogo = new Image(
                getClass().getResource("/mm/rsc/MadBallsLogo.jpeg").toExternalForm());
        Image backgroundBoard = new Image(
                getClass().getResource("/mm/rsc/MB_TitleScreenBoard.jpeg").toExternalForm());

        // === logoBox ===
        HBox logoBox = new HBox();
        logoBox.setPrefSize(800, 400);
        logoBox.setAlignment(Pos.CENTER);

        BackgroundImage logoBg = new BackgroundImage(
                backgroundLogo,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false));
        logoBox.setBackground(new Background(logoBg));

        // === boardBox ===
        HBox boardBox = new HBox(buttonLayer);
        boardBox.setPrefSize(600, 400);
        boardBox.setAlignment(Pos.CENTER);
        boardBox.setPadding(new Insets(0, 0, 20, 0));

        BackgroundImage boardBg = new BackgroundImage(
                backgroundBoard,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false));
        boardBox.setBackground(new Background(boardBg));

        VBox logoANDBoard = new VBox(logoBox, boardBox);
        logoANDBoard.setPadding(new Insets(0, 0, 30, 0));

        // === Overlay: Optionen ===
        StackPane overlayBackgroundOptions = new StackPane();
        overlayBackgroundOptions.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlayBackgroundOptions.setVisible(false);

        Pane optionsWindow = new Pane();
        optionsWindow.setMaxSize(600, 400);
        optionsWindow.setMinSize(600, 400);
        optionsWindow.setStyle("-fx-background-color: rgba(46, 36, 87, 0.94); -fx-background-radius: 12;");

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

        Button btnCloseOptions = new Button("X");
        btnCloseOptions.setLayoutX(560);
        btnCloseOptions.setLayoutY(10);
        btnCloseOptions.setOnAction(e -> overlayBackgroundOptions.setVisible(false));

        optionsWindow.getChildren().addAll(
                lblTitleAudio, lblMusic, sliderMusic, lblSound, sliderSound,
                lblTitleGraphics, lblTexturePack, btnUploadTexture,
                btnCloseOptions);

        overlayBackgroundOptions.getChildren().add(optionsWindow);
        StackPane.setAlignment(optionsWindow, Pos.CENTER);

        // === Overlay: Puzzle ===
        StackPane overlayBackgroundPuzzle = new StackPane();
        overlayBackgroundPuzzle.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlayBackgroundPuzzle.setVisible(false);

        Pane puzzleWindow = new Pane();
        puzzleWindow.setMaxSize(720, 480);
        puzzleWindow.setMinSize(720, 480);
        puzzleWindow.setStyle("-fx-background-color: rgba(46, 36, 87, 0.94); -fx-background-radius: 12;");

        Label lblTitlePuzzle = new Label("Puzzle Picker");
        lblTitlePuzzle.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        lblTitlePuzzle.setLayoutX(230);
        lblTitlePuzzle.setLayoutY(30);

        Button btnClosePuzzle = new Button("X");
        btnClosePuzzle.setLayoutX(560);
        btnClosePuzzle.setLayoutY(10);
        btnClosePuzzle.setOnAction(e -> overlayBackgroundPuzzle.setVisible(false));

        puzzleWindow.getChildren().addAll(lblTitlePuzzle, btnClosePuzzle);
        overlayBackgroundPuzzle.getChildren().add(puzzleWindow);
        StackPane.setAlignment(puzzleWindow, Pos.CENTER);

        // === Button Styles + Events ===
        btnPuzzle.getStyleClass().add("btnTS");
        btnSandbox.getStyleClass().add("btnTS");
        btnOptions.getStyleClass().add("btnTS");
        btnQuit.getStyleClass().add("btnTS");

        btnPuzzle.setOnAction(e -> overlayBackgroundPuzzle.setVisible(true));
        btnSandbox.setOnAction(e -> System.out.println("Starting Sandbox Mode"));
        btnOptions.setOnAction(e -> overlayBackgroundOptions.setVisible(true));
        btnQuit.setOnAction(e -> System.exit(0));

        // === Root-Layout ===
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0e1722;");
        root.getChildren().addAll(logoANDBoard, overlayBackgroundPuzzle,
                overlayBackgroundOptions);

        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/mm/styling/titleScreen.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setMaxWidth(1920);
        primaryStage.setMaxHeight(1080);
        primaryStage.show();
    }

    private Circle createPin(Color color) {
        Circle pin = new Circle(6);
        pin.setFill(color);
        pin.setStroke(Color.BLACK);
        pin.setStrokeWidth(1.5);
        return pin;
    }

    private StackPane btnWithPin(Button btn, Color pinColor) {
        Circle pin = createPin(pinColor);
        StackPane stack = new StackPane(btn, pin);
        StackPane.setAlignment(pin, Pos.TOP_CENTER);
        return stack;
    }

    public static void main(String[] args) {
        launch(args);
    }
}