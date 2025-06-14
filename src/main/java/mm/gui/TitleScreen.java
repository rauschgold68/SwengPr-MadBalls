package mm.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * The TitleScreen class constructs the main menu scene for the MadBalls game.
 * It provides navigation to puzzles, sandbox mode, options, and quitting the application.
 * The screen features custom-styled buttons, background images, and overlays for options and level selection.
 */
public class TitleScreen {

    /**
     * Builds and returns the main title scene for the application.
     * <p>
     * The scene includes:
     * <ul>
     *   <li>Menu buttons for Puzzles, Sandbox, Options, and Quit, each with a decorative pin</li>
     *   <li>Logo and board background images</li>
     *   <li>Overlay windows for options and level selection</li>
     *   <li>Keyboard shortcut handling (ESC to close overlays)</li>
     * </ul>
     *
     * @param primaryStage The main application window, used for scene switching.
     * @return The fully constructed title {@link Scene}.
     */
    public Scene createTitleScene(Stage primaryStage) {

        // Create menu buttons.
        Button btnPuzzle = new Button("Puzzles");
        Button btnSandbox = new Button("Sandbox");
        Button btnOptions = new Button("Options");
        Button btnQuit = new Button("Quit");

        // Apply playful rotation to each button for a casual look.
        btnPuzzle.setRotate(-7);
        btnSandbox.setRotate(0);
        btnOptions.setRotate(5);
        btnQuit.setRotate(-3);

        // Arrange the top row of buttons (Puzzles, Sandbox, Options) with colored pins.
        HBox topRow = new HBox(60,
                btnWithPin(btnPuzzle, Color.RED),
                btnWithPin(btnSandbox, Color.BLUE),
                btnWithPin(btnOptions, Color.GREEN));
        topRow.setAlignment(Pos.CENTER);

        // Arrange the bottom row with the Quit button and its pin.
        HBox bottomRow = new HBox(btnWithPin(btnQuit, Color.YELLOW));
        bottomRow.setAlignment(Pos.BASELINE_RIGHT);

        // Stack the button rows vertically.
        VBox buttonLayer = new VBox(40, topRow, bottomRow);
        buttonLayer.setAlignment(Pos.CENTER);

        // Load background images for the logo and board.
        Image backgroundLogo = new Image(getClass().getResourceAsStream("/pictures/MadBallsLogo.jpeg"));
        Image backgroundBoard = new Image(getClass().getResourceAsStream("/pictures/MB_TitleScreenBoard.jpeg"));

        // Create a box for the logo image.
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
        overlayBackgroundOptions.setVisible(false);

        Image levelSelect = new Image(getClass().getResourceAsStream("/pictures/levelSelect.png"));
        BackgroundImage levelSelectBg = new BackgroundImage(
                levelSelect,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1920, 1080, true, true, true, true));
        overlayBackgroundOptions.setBackground(new Background(levelSelectBg));

        VBox optionsWindow = new VBox(25);
        optionsWindow.setMaxSize(720, 480);
        optionsWindow.setMinSize(720, 480);
        optionsWindow.setPadding(new Insets(30));
        optionsWindow.setAlignment(Pos.TOP_CENTER);
        optionsWindow.setBackground(new Background(new BackgroundFill(
                Color.rgb(10, 10, 20, 0.7), new CornerRadii(20), Insets.EMPTY))); // Stil vom Game

        HBox topBar = new HBox();
        topBar.setPrefWidth(720);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitleAudio = new Label("Options");
        lblTitleAudio.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnCloseOptions = new Button("X");
        btnCloseOptions.getStyleClass().add("close-btn");
        btnCloseOptions.setOnAction(e -> overlayBackgroundOptions.setVisible(false));
        topBar.getChildren().addAll(lblTitleAudio, spacer, btnCloseOptions);

        Label lblMusic = new Label("Musik-Lautstärke:");
        lblMusic.setStyle("-fx-text-fill: white;");
        Slider sliderMusic = new Slider(0, 100, 50);
        sliderMusic.setPrefWidth(300);
        HBox musicRow = new HBox(20, lblMusic, sliderMusic);
        musicRow.setAlignment(Pos.CENTER_LEFT);

        Label lblSound = new Label("Soundeffekte:");
        lblSound.setStyle("-fx-text-fill: white;");
        Slider sliderSound = new Slider(0, 100, 50);
        sliderSound.setPrefWidth(300);
        HBox soundRow = new HBox(20, lblSound, sliderSound);
        soundRow.setAlignment(Pos.CENTER_LEFT);

        Label lblTexturePack = new Label("Texture Pack:");
        lblTexturePack.setStyle("-fx-text-fill: white;");
        Button btnUploadTexture = new Button("Upload...");
        HBox textureRow = new HBox(20, lblTexturePack, btnUploadTexture);
        textureRow.setAlignment(Pos.CENTER_LEFT);

        optionsWindow.getChildren().addAll(topBar, musicRow, soundRow, textureRow);
        overlayBackgroundOptions.getChildren().add(optionsWindow);
        StackPane.setAlignment(optionsWindow, Pos.CENTER);

        // === Overlay: Puzzle / Level-Selector ===
        StackPane overlayBackgroundPuzzle = new StackPane();
        overlayBackgroundPuzzle.setVisible(false);

        Image levelSelectImage = new Image(getClass().getResourceAsStream("/pictures/levelSelect.png"));
        BackgroundImage puzzleBg = new BackgroundImage(
                levelSelectImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1920, 1080, true, true, true, true));
        overlayBackgroundPuzzle.setBackground(new Background(puzzleBg));

        Label puzzleTitle = new Label("Level Selection");
        puzzleTitle.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
        puzzleTitle.setPadding(new Insets(30, 0, 0, 0));

        VBox levelCard1 = createLevelCard("Level 1", "Leichtes Einsteigerlevel",
                "/pictures/levelSelectBoard.jpeg", "/level/level1.json", primaryStage);
        VBox levelCard2 = createLevelCard("Level 2", "Knifflige Mechanik", "/pictures/levelSelectBoard.jpeg", "/level/level2.json", primaryStage);
        VBox levelCard3 = createLevelCard("Level 3", "Nur für Profis", "/pictures/levelSelectBoard.jpeg", "/level/level3.json", primaryStage);

        HBox cardRow = new HBox(40, levelCard1, levelCard2, levelCard3);
        cardRow.setAlignment(Pos.CENTER);
        cardRow.setPadding(new Insets(20, 0, 0, 0));

        Button btnClosePuzzle = new Button("X");
        btnClosePuzzle.getStyleClass().add("close-btn");
        btnClosePuzzle.setOnAction(e -> overlayBackgroundPuzzle.setVisible(false));
        StackPane.setAlignment(btnClosePuzzle, Pos.TOP_RIGHT);
        StackPane.setMargin(btnClosePuzzle, new Insets(20));

        VBox cardWrapper = new VBox(puzzleTitle, cardRow);
        cardWrapper.setAlignment(Pos.TOP_CENTER);
        cardWrapper.setPadding(new Insets(60, 0, 70, 0));

        overlayBackgroundPuzzle.getChildren().addAll(cardWrapper, btnClosePuzzle);

        // === Button Styles + Events ===
        btnPuzzle.getStyleClass().add("btnTS");
        btnSandbox.getStyleClass().add("btnTS");
        btnOptions.getStyleClass().add("btnTS");
        btnQuit.getStyleClass().add("btnTS");

        btnPuzzle.setOnAction(e -> overlayBackgroundPuzzle.setVisible(true));
        btnSandbox.setOnAction(e -> {
            Simulation sim = new Simulation("/level/basic_sandbox.json");
            Scene simScene = sim.getScene(primaryStage);
            primaryStage.setScene(simScene);
            primaryStage.sizeToScene();
        });

        btnOptions.setOnAction(e -> overlayBackgroundOptions.setVisible(true));
        btnQuit.setOnAction(e -> Platform.exit());

        // === Root-Layout ===
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0e1722;");
        root.getChildren().addAll(logoANDBoard, overlayBackgroundPuzzle, overlayBackgroundOptions);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styling/titleScreen.css").toExternalForm());

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    if (overlayBackgroundOptions.isVisible()) {
                        overlayBackgroundOptions.setVisible(false);
                    } else if (overlayBackgroundPuzzle.isVisible()) {
                        overlayBackgroundPuzzle.setVisible(false);
                    }
                    break;
                default:
                    break;
            }
        });

        // force CSS and layout pass
        Platform.runLater(() -> {
            root.applyCss();
            root.layout();
        });

        return scene;
    }

    /**
     * Creates a small colored pin (circle) for button decoration.
     *
     * @param color The color of the pin.
     * @return A {@link Circle} representing the pin.
     */
    private Circle createPin(Color color) {
        Circle pin = new Circle(6);
        pin.setFill(color);
        pin.setStroke(Color.BLACK);
        pin.setStrokeWidth(1.5);
        return pin;
    }

    /**
     * Wraps a button with a colored pin at the top center for visual effect.
     *
     * @param btn      The button to decorate.
     * @param pinColor The color of the pin.
     * @return A {@link StackPane} containing the button and pin.
     */
    private StackPane btnWithPin(Button btn, Color pinColor) {
        Circle pin = createPin(pinColor);
        StackPane stack = new StackPane(btn, pin);
        StackPane.setAlignment(pin, Pos.TOP_CENTER);
        return stack;
    }

    /**
     * Creates a card for level selection, including a thumbnail, name, and description.
     * Clicking the card starts the corresponding level.
     *
     * @param levelName    The display name of the level.
     * @param description  A short description of the level.
     * @param imagePath    Path to the thumbnail image resource.
     * @param levelPath    Path to the level data file.
     * @param primaryStage The main application window for scene switching.
     * @return A {@link VBox} representing the level card.
     */
    private VBox createLevelCard(String levelName, String description, String imagePath, String levelPath, Stage primaryStage) {
        ImageView thumbnail = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        thumbnail.setFitWidth(260);
        thumbnail.setFitHeight(160);

        Label lblName = new Label(levelName);
        lblName.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblDesc = new Label(description);
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        VBox card = new VBox(14, thumbnail, lblName, lblDesc);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20));
        card.setMaxWidth(280);
        card.setMinHeight(320);
        card.setBackground(new Background(
                new BackgroundFill(Color.rgb(30, 30, 50, 0.8), new CornerRadii(16), Insets.EMPTY)));
        card.getStyleClass().add("level-card");

        card.setOnMouseClicked(e -> {
            System.out.println("Starting level: " + levelName);
            Simulation sim = new Simulation(levelPath);
            Scene simScene = sim.getScene(primaryStage);
            primaryStage.setScene(simScene);
            primaryStage.sizeToScene();
        });

        return card;
    }
}
