package mm.view;

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
 * The {@code TitleScreenView} class constructs and exposes all JavaFX UI components
 * for the main menu (title screen) of the MadBalls game.
 * <p>
 * This class is responsible for building the visual layout and structure of the title screen,
 * including the main menu buttons, logo, background images, overlays for options and level selection,
 * and decorative elements. It does not contain any event handling or navigation logic; all such logic
 * should be managed by the controller.
 * </p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Builds and arranges the main menu layout, including logo, board, and menu buttons.</li>
 *   <li>Creates overlays for options and puzzle/level selection, exposing them for controller use.</li>
 *   <li>Provides utility methods for constructing decorative UI elements (pins, cards).</li>
 *   <li>Exposes all relevant UI components as public fields for the controller to attach event handlers.</li>
 * </ul>
 *
 * <h3>UI Structure:</h3>
 * <ul>
 *   <li>{@code btnPuzzle, btnSandbox, btnOptions, btnQuit} - Main menu buttons.</li>
 *   <li>{@code btnCloseOptions, btnClosePuzzle} - Close buttons for overlays.</li>
 *   <li>{@code btnUploadTexture} - Button for uploading a texture pack in the options overlay.</li>
 *   <li>{@code overlayBackgroundOptions, overlayBackgroundPuzzle} - Overlays for options and level selection.</li>
 *   <li>{@code optionsWindow} - The options overlay window container.</li>
 *   <li>{@code root} - The root StackPane containing all layers.</li>
 *   <li>{@code scene} - The JavaFX Scene for the title screen.</li>
 * </ul>
 *
 * <h3>Notes:</h3>
 * <ul>
 *   <li>This class should not contain any event handling or navigation logic.</li>
 *   <li>All UI elements that need interaction should be exposed as public fields for the controller.</li>
 *   <li>Level card click handlers should be set by the controller.</li>
 * </ul>
 */
public class TitleScreenView {

    /** Main menu buttons for navigation. */
    public Button btnPuzzle, btnSandbox, btnOptions, btnQuit;
    /** Close buttons for overlays. */
    public Button btnCloseOptions, btnClosePuzzle;
    /** Button for uploading a texture pack in the options overlay. */
    public Button btnUploadTexture;
    /** Overlay for options/settings. */
    public StackPane overlayBackgroundOptions;
    /** Overlay for puzzle/level selection. */
    public StackPane overlayBackgroundPuzzle;
    /** The options overlay window container. */
    public VBox optionsWindow;
    /** The root StackPane containing all layers. */
    public StackPane root;
    /** The JavaFX Scene for the title screen. */
    public Scene scene;
    /** Level selection cards for the different levels. */
    public VBox levelCard1, levelCard2, levelCard3;

    /**
     * Constructs the title screen view and builds the UI layout.
     * All event handling and navigation logic should be managed by the controller.
     *
     * @param primaryStage The main application window, used for resource loading and sizing.
     */
    public TitleScreenView(Stage primaryStage) {
        // Menu buttons
        btnPuzzle = new Button("Puzzles");
        btnSandbox = new Button("Sandbox");
        btnOptions = new Button("Options");
        btnQuit = new Button("Quit");

        btnPuzzle.setRotate(-7);
        btnSandbox.setRotate(0);
        btnOptions.setRotate(5);
        btnQuit.setRotate(-3);

        // Top row: Puzzles, Sandbox, Options (with pins)
        HBox topRow = new HBox(60,
                btnWithPin(btnPuzzle, Color.RED),
                btnWithPin(btnSandbox, Color.BLUE),
                btnWithPin(btnOptions, Color.GREEN));
        topRow.setAlignment(Pos.CENTER);

        // Bottom row: Quit (with pin)
        HBox bottomRow = new HBox(btnWithPin(btnQuit, Color.YELLOW));
        bottomRow.setAlignment(Pos.BASELINE_RIGHT);

        // Stack button rows vertically
        VBox buttonLayer = new VBox(40, topRow, bottomRow);
        buttonLayer.setAlignment(Pos.CENTER);

        // Logo and board backgrounds
        Image backgroundLogo = new Image(getClass().getResourceAsStream("/pictures/MadBallsLogo.jpeg"));
        Image backgroundBoard = new Image(getClass().getResourceAsStream("/pictures/MB_TitleScreenBoard.jpeg"));

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

        // === Overlay: Options ===
        overlayBackgroundOptions = new StackPane();
        overlayBackgroundOptions.setVisible(false);

        Image levelSelect = new Image(getClass().getResourceAsStream("/pictures/levelSelect.png"));
        BackgroundImage levelSelectBg = new BackgroundImage(
                levelSelect,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1920, 1080, true, true, true, true));
        overlayBackgroundOptions.setBackground(new Background(levelSelectBg));

        optionsWindow = new VBox(25);
        optionsWindow.setMaxSize(720, 480);
        optionsWindow.setMinSize(720, 480);
        optionsWindow.setPadding(new Insets(30));
        optionsWindow.setAlignment(Pos.TOP_CENTER);
        optionsWindow.setBackground(new Background(new BackgroundFill(
                Color.rgb(10, 10, 20, 0.7), new CornerRadii(20), Insets.EMPTY)));

        HBox topBar = new HBox();
        topBar.setPrefWidth(720);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitleAudio = new Label("Options");
        lblTitleAudio.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnCloseOptions = new Button("X");
        btnCloseOptions.getStyleClass().add("close-btn");
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
        btnUploadTexture = new Button("Upload...");
        HBox textureRow = new HBox(20, lblTexturePack, btnUploadTexture);
        textureRow.setAlignment(Pos.CENTER_LEFT);

        optionsWindow.getChildren().addAll(topBar, musicRow, soundRow, textureRow);
        overlayBackgroundOptions.getChildren().add(optionsWindow);
        StackPane.setAlignment(optionsWindow, Pos.CENTER);

        // === Overlay: Puzzle / Level-Selector ===
        overlayBackgroundPuzzle = new StackPane();
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

        levelCard1 = createLevelCard("Level 1", "Leichtes Einsteigerlevel",
                "/pictures/levelSelectBoard.jpeg", "/level/level1.json", primaryStage);
        levelCard2 = createLevelCard("Level 2", "Knifflige Mechanik",
                "/pictures/levelSelectBoard.jpeg", "/level/level2.json", primaryStage);
        levelCard3 = createLevelCard("Level 3", "Nur für Profis",
                "/pictures/levelSelectBoard.jpeg", "/level/level3.json", primaryStage);

        HBox cardRow = new HBox(40, levelCard1, levelCard2, levelCard3);
        cardRow.setAlignment(Pos.CENTER);
        cardRow.setPadding(new Insets(20, 0, 0, 0));

        btnClosePuzzle = new Button("X");
        btnClosePuzzle.getStyleClass().add("close-btn");
        StackPane.setAlignment(btnClosePuzzle, Pos.TOP_RIGHT);
        StackPane.setMargin(btnClosePuzzle, new Insets(20));

        VBox cardWrapper = new VBox(puzzleTitle, cardRow);
        cardWrapper.setAlignment(Pos.TOP_CENTER);
        cardWrapper.setPadding(new Insets(60, 0, 70, 0));

        overlayBackgroundPuzzle.getChildren().addAll(cardWrapper, btnClosePuzzle);

        // === Button Styles ===
        btnPuzzle.getStyleClass().add("btnTS");
        btnSandbox.getStyleClass().add("btnTS");
        btnOptions.getStyleClass().add("btnTS");
        btnQuit.getStyleClass().add("btnTS");

        // === Root-Layout ===
        root = new StackPane();
        root.setStyle("-fx-background-color: #0e1722;");
        root.getChildren().addAll(logoANDBoard, overlayBackgroundPuzzle, overlayBackgroundOptions);

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styling/titleScreen.css").toExternalForm());

        // force CSS and layout pass
        Platform.runLater(() -> {
            root.applyCss();
            root.layout();
        });
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
     * Clicking the card should be handled by the controller.
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

        // Controller should set the onMouseClicked handler for navigation

        return card;
    }

    public Scene getScene() {return scene;}
}
