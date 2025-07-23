package mm.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import mm.controller.SkinManagerController;

/**
 * The {@code TitleScreenView} class constructs and exposes all JavaFX UI
 * components
 * for the main menu (title screen) of the MadBalls game.
 * <p>
 * This class is responsible for building the visual layout and structure of the
 * title screen,
 * including the main menu buttons, logo, background images, overlays for
 * options and level selection,
 * and decorative elements. It does not contain any event handling or navigation
 * logic; all such logic
 * should be managed by the controller.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Builds and arranges the main menu layout, including logo, board, and menu
 * buttons.</li>
 * <li>Creates overlays for options and puzzle/level selection, exposing them
 * for controller use.</li>
 * <li>Provides utility methods for constructing decorative UI elements (pins,
 * cards).</li>
 * <li>Exposes all relevant UI components as public fields for the controller to
 * attach event handlers.</li>
 * </ul>
 *
 * <h2>UI Structure:</h2>
 * <ul>
 * <li>{@code btnPuzzle, btnSandbox, btnOptions, btnQuit} - Main menu
 * buttons.</li>
 * <li>{@code btnCloseOptions, btnClosePuzzle} - Close buttons for
 * overlays.</li>
 * <li>{@code btnUploadTexture} - Button for uploading a texture pack in the
 * options overlay.</li>
 * <li>{@code overlayBackgroundOptions, overlayBackgroundPuzzle} - Overlays for
 * options and level selection.</li>
 * <li>{@code optionsWindow} - The options overlay window container.</li>
 * <li>{@code root} - The root StackPane containing all layers.</li>
 * <li>{@code scene} - The JavaFX Scene for the title screen.</li>
 * </ul>
 *
 * <h2>Notes:</h2>
 * <ul>
 * <li>This class should not contain any event handling or navigation
 * logic.</li>
 * <li>All UI elements that need interaction should be exposed as public fields
 * for the controller.</li>
 * <li>Level card click handlers should be set by the controller.</li>
 * </ul>
 */
public class TitleScreenView {

    /** Main menu navigation buttons. */
    public MenuButtons menuButtons = new MenuButtons();
    /** Overlay control buttons. */
    public OverlayButtons overlayButtons = new OverlayButtons();
    /** Level selection cards. */
    public LevelCards levelCards = new LevelCards();
    /** Main UI containers and overlays. */
    public UIContainers uiContainers = new UIContainers();
    /** Options screen components. */
    public OptionsComponents optionsComponents = new OptionsComponents();

    /**
     * CSS class name for title screen buttons to avoid duplicate string literals.
     */
    private static final String BUTTON_STYLE_CLASS = "btnTS";

    /**
     * Container class for main menu navigation buttons.
     */
    public static class MenuButtons {
        /** Main menu button for accessing puzzle levels. */
        public Button btnPuzzle;
        /** Main menu button for accessing sandbox mode. */
        public Button btnSandbox;
        /** Main menu button for accessing game options/settings. */
        public Button btnOptions;
        /** Main menu button for quitting the application. */
        public Button btnQuit;
    }

    /**
     * Container class for overlay control buttons.
     */
    public static class OverlayButtons {
        /** Close button for the options overlay. */
        public Button btnCloseOptions;
        /** Close button for the puzzle selection overlay. */
        public Button btnClosePuzzle;
        /** Button for uploading a texture pack in the options overlay. */
        public Button btnUploadTexture;
    }

    /**
     * Container class for level selection cards.
     */
    public static class LevelCards {
        /** Level selection card for Level 1. */
        public VBox levelCard1;
        /** Level selection card for Level 2. */
        public VBox levelCard2;
        /** Level selection card for Level 3. */
        public VBox levelCard3;
    }

    /**
     * Container class for main UI containers and overlays.
     */
    public static class UIContainers {
        /** Overlay background for options/settings screen. */
        public StackPane overlayBackgroundOptions;
        /** Overlay background for puzzle/level selection screen. */
        public StackPane overlayBackgroundPuzzle;
        /** The options overlay window container with settings controls. */
        public VBox optionsWindow;
        /** The root StackPane containing all UI layers (main screen and overlays). */
        public StackPane root;
        /** The JavaFX Scene for the title screen. */
        public Scene scene;
    }

    /**
     * Container class for options screen components.
     */
    public static class OptionsComponents {
        /** Skin manager controller instance. */
        public SkinManagerController skinManager = SkinManagerController.getInstance();
        /** Choice box for selecting skin/texture pack. */
        public ChoiceBox<String> skinChoiceBox;
        /** Button for saving skin selection. */
        public Button btnSaveSkin;
    }

    /**
     * Returns the currently saved skin choice ("Default" or "Legacy").
     */
    public String getSavedSkinChoice() {
        return optionsComponents.skinManager.getSelectedSkin();
    }

    /**
     * Constructs the title screen view and builds the UI layout.
     * All event handling and navigation logic should be managed by the controller.
     */
    public TitleScreenView() {
        initializeMenuButtons();
        VBox buttonLayer = createButtonLayout();
        VBox logoANDBoard = createLogoAndBoard(buttonLayer);

        createOptionsOverlay();
        createPuzzleOverlay();

        setupButtonStyles();
        setupRootAndScene(logoANDBoard);
    }

    /**
     * Initializes the main menu buttons with their display text and rotation
     * angles.
     * Sets up the four main navigation buttons: Puzzles, Sandbox, Options, and
     * Quit.
     * Each button is given a slight rotation for visual appeal.
     */
    private void initializeMenuButtons() {
        menuButtons.btnPuzzle = new Button("Puzzles");
        menuButtons.btnSandbox = new Button("Sandbox");
        menuButtons.btnOptions = new Button("Options");
        menuButtons.btnQuit = new Button("Quit");

        menuButtons.btnPuzzle.setRotate(-7);
        menuButtons.btnSandbox.setRotate(0);
        menuButtons.btnOptions.setRotate(5);
        menuButtons.btnQuit.setRotate(-3);
    }

    /**
     * Creates the button layout with decorative pins and arranges them in rows.
     * Arranges the main menu buttons in two rows: top row contains Puzzles,
     * Sandbox,
     * and Options buttons, while the bottom row contains only the Quit button.
     * Each button is decorated with a colored pin for visual appeal.
     * 
     * @return The VBox containing the complete arranged button layout.
     */
    private VBox createButtonLayout() {
        // Top row: Puzzles, Sandbox, Options (with pins)
        HBox topRow = new HBox(60,
                btnWithPin(menuButtons.btnPuzzle, Color.RED),
                btnWithPin(menuButtons.btnSandbox, Color.BLUE),
                btnWithPin(menuButtons.btnOptions, Color.GREEN));
        topRow.setAlignment(Pos.CENTER);

        // Bottom row: Quit (with pin)
        HBox bottomRow = new HBox(btnWithPin(menuButtons.btnQuit, Color.YELLOW));
        bottomRow.setAlignment(Pos.BASELINE_RIGHT);

        // Stack button rows vertically
        VBox buttonLayer = new VBox(40, topRow, bottomRow);
        buttonLayer.setAlignment(Pos.CENTER);

        return buttonLayer;
    }

    /**
     * Creates the logo and board background layout for the main title screen.
     * Constructs the visual layout with the MadBalls logo on top and a decorative
     * board background below it. The button layer is placed on the board
     * background.
     * Both backgrounds use image resources and are sized appropriately.
     * 
     * @param buttonLayer The button layout to be placed on the board background.
     * @return The VBox containing the complete logo and board layout.
     */
    private VBox createLogoAndBoard(VBox buttonLayer) {
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

        return logoANDBoard;
    }

    /**
     * Creates the options overlay with game settings controls.
     * Builds a modal overlay containing volume sliders for music and sound effects,
     * as well as a texture pack upload button. The overlay has a semi-transparent
     * background and is initially hidden. The overlay uses the level select
     * background
     * image and contains a styled options window with various controls.
     */
    private void createOptionsOverlay() {
        uiContainers.overlayBackgroundOptions = new StackPane();
        uiContainers.overlayBackgroundOptions.setVisible(false);

        setupOptionsBackground();
        createOptionsWindow();

        uiContainers.overlayBackgroundOptions.getChildren().add(uiContainers.optionsWindow);
        StackPane.setAlignment(uiContainers.optionsWindow, Pos.CENTER);

        // Reset ChoiceBox auf gespeicherten Wert, wenn Overlay geschlossen wird
        uiContainers.overlayBackgroundOptions.visibleProperty().addListener((obs, wasVisible, isNowVisible) -> {
            if (!isNowVisible) {
                optionsComponents.skinChoiceBox.setValue(optionsComponents.skinManager.getSelectedSkin());
            }
        });
    }

    /**
     * Sets up the background image for the options overlay.
     */
    private void setupOptionsBackground() {
        Image levelSelect = new Image(getClass().getResourceAsStream("/pictures/levelSelect.png"));
        BackgroundImage levelSelectBg = new BackgroundImage(
                levelSelect,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1920, 1080, true, true, true, true));
        uiContainers.overlayBackgroundOptions.setBackground(new Background(levelSelectBg));
    }

    /**
     * Creates the options window with all settings controls.
     */
    private void createOptionsWindow() {
        uiContainers.optionsWindow = new VBox(25);
        uiContainers.optionsWindow.setMaxSize(720, 480);
        uiContainers.optionsWindow.setMinSize(720, 480);
        uiContainers.optionsWindow.setPadding(new Insets(30));
        uiContainers.optionsWindow.setAlignment(Pos.TOP_CENTER);
        uiContainers.optionsWindow.setBackground(new Background(new BackgroundFill(
                Color.rgb(10, 10, 20, 0.7), new CornerRadii(20), Insets.EMPTY)));

        HBox topBar = createOptionsTopBar();
        HBox musicRow = createMusicVolumeRow();
        HBox soundRow = createSoundVolumeRow();
        HBox textureRow = createTexturePackRow();

        uiContainers.optionsWindow.getChildren().addAll(topBar, musicRow, soundRow, textureRow);
    }

    /**
     * Creates the top bar with title and close button for the options window.
     * 
     * @return The HBox containing the top bar elements.
     */
    private HBox createOptionsTopBar() {
        HBox topBar = new HBox();
        topBar.setPrefWidth(720);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitleAudio = new Label("Options");
        lblTitleAudio.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        overlayButtons.btnCloseOptions = new Button("X");
        overlayButtons.btnCloseOptions.getStyleClass().add("close-btn");
        topBar.getChildren().addAll(lblTitleAudio, spacer, overlayButtons.btnCloseOptions);

        return topBar;
    }

    /**
     * Creates the music volume control row.
     * 
     * @return The HBox containing the music volume controls.
     */
    private HBox createMusicVolumeRow() {
        Label lblMusic = new Label("Musik-Lautstärke:");
        lblMusic.setStyle("-fx-text-fill: white;");
        Slider sliderMusic = new Slider(0, 100, 50);
        sliderMusic.setPrefWidth(300);
        HBox musicRow = new HBox(20, lblMusic, sliderMusic);
        musicRow.setAlignment(Pos.CENTER_LEFT);
        return musicRow;
    }

    /**
     * Creates the sound effects volume control row.
     * 
     * @return The HBox containing the sound volume controls.
     */
    private HBox createSoundVolumeRow() {
        Label lblSound = new Label("Soundeffekte:");
        lblSound.setStyle("-fx-text-fill: white;");
        Slider sliderSound = new Slider(0, 100, 50);
        sliderSound.setPrefWidth(300);
        HBox soundRow = new HBox(20, lblSound, sliderSound);
        soundRow.setAlignment(Pos.CENTER_LEFT);
        return soundRow;
    }

    /**
     * Creates the texture pack upload control row.
     * 
     * @return The HBox containing the texture pack controls.
     */
    private HBox createTexturePackRow() {
        Label lblTexturePack = new Label("Texture Pack:");
        lblTexturePack.setStyle("-fx-text-fill: white;");

        // ChoiceBox für Skin-Auswahl
        optionsComponents.skinChoiceBox = new ChoiceBox<>();
        optionsComponents.skinChoiceBox.getItems().addAll("Default", "Legacy");
        optionsComponents.skinChoiceBox.setValue(optionsComponents.skinManager.getSelectedSkin());

        // Save-Button
        optionsComponents.btnSaveSkin = new Button("Save");
        optionsComponents.btnSaveSkin.getStyleClass().add("save-btn");

        optionsComponents.btnSaveSkin.setOnAction(e -> {
            optionsComponents.skinManager.setSelectedSkin(optionsComponents.skinChoiceBox.getValue());
        });

        HBox textureRow = new HBox(20, lblTexturePack, optionsComponents.skinChoiceBox, optionsComponents.btnSaveSkin);
        textureRow.setAlignment(Pos.CENTER_LEFT);
        return textureRow;
    }

    /**
     * Creates the puzzle/level selection overlay screen.
     * Builds a modal overlay displaying available game levels as cards.
     * Each level card shows a thumbnail, name, and description. The overlay
     * includes a title, level cards arranged horizontally, and a close button.
     * The overlay is initially hidden and uses the level select background image.
     * The controller is responsible for setting up click handlers for level
     * navigation.
     */
    private void createPuzzleOverlay() {
        uiContainers.overlayBackgroundPuzzle = new StackPane();
        uiContainers.overlayBackgroundPuzzle.setVisible(false);

        Image levelSelectImage = new Image(getClass().getResourceAsStream("/pictures/levelSelect.png"));
        BackgroundImage puzzleBg = new BackgroundImage(
                levelSelectImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1920, 1080, true, true, true, true));
        uiContainers.overlayBackgroundPuzzle.setBackground(new Background(puzzleBg));

        Label puzzleTitle = new Label("Level Selection");
        puzzleTitle.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
        puzzleTitle.setPadding(new Insets(30, 0, 0, 0));

        levelCards.levelCard1 = createLevelCard("Level 1 - Double Eagle", "Difficulty: Easy",
                "/pictures/level1Preview.png");
        levelCards.levelCard2 = createLevelCard("Level 2 - Chain Reaction", "Difficulty: Medium",
                "/pictures/level2Preview.png");
        levelCards.levelCard3 = createLevelCard("Level 3 - Precise Interplay", "Difficulty: Hard",
                "/pictures/level3Preview.png");

        HBox cardRow = new HBox(40, levelCards.levelCard1, levelCards.levelCard2, levelCards.levelCard3);
        cardRow.setAlignment(Pos.CENTER);
        cardRow.setPadding(new Insets(20, 0, 0, 0));

        overlayButtons.btnClosePuzzle = new Button("X");
        overlayButtons.btnClosePuzzle.getStyleClass().add("close-btn");
        StackPane.setAlignment(overlayButtons.btnClosePuzzle, Pos.TOP_RIGHT);
        StackPane.setMargin(overlayButtons.btnClosePuzzle, new Insets(20));

        VBox cardWrapper = new VBox(puzzleTitle, cardRow);
        cardWrapper.setAlignment(Pos.TOP_CENTER);
        cardWrapper.setPadding(new Insets(60, 0, 70, 0));

        uiContainers.overlayBackgroundPuzzle.getChildren().addAll(cardWrapper, overlayButtons.btnClosePuzzle);
    }

    /**
     * Sets up the CSS style classes for all main menu buttons.
     * Applies the consistent button styling class to all four main menu buttons
     * to ensure uniform appearance across the title screen.
     */
    private void setupButtonStyles() {
        menuButtons.btnPuzzle.getStyleClass().add(BUTTON_STYLE_CLASS);
        menuButtons.btnSandbox.getStyleClass().add(BUTTON_STYLE_CLASS);
        menuButtons.btnOptions.getStyleClass().add(BUTTON_STYLE_CLASS);
        menuButtons.btnQuit.getStyleClass().add(BUTTON_STYLE_CLASS);
    }

    /**
     * Sets up the root layout and JavaFX scene for the title screen.
     * Creates the main StackPane root container, adds all UI layers (main content
     * and overlays),
     * applies the background color and CSS stylesheet, and creates the Scene
     * object.
     * Also forces a CSS and layout pass to ensure proper rendering.
     * 
     * @param logoANDBoard The main content layout containing logo, board, and
     *                     buttons.
     */
    private void setupRootAndScene(VBox logoANDBoard) {
        uiContainers.root = new StackPane();
        uiContainers.root.setStyle("-fx-background-color: #0e1722;");
        uiContainers.root.getChildren().addAll(logoANDBoard, uiContainers.overlayBackgroundPuzzle,
                uiContainers.overlayBackgroundOptions);

        uiContainers.scene = new Scene(uiContainers.root);
        uiContainers.scene.getStylesheets().add(getClass().getResource("/styling/titleScreen.css").toExternalForm());

        // force CSS and layout pass
        Platform.runLater(() -> {
            uiContainers.root.applyCss();
            uiContainers.root.layout();
        });
    }

    /**
     * Creates a small colored circular pin for button decoration.
     * The pin is a circle with a black border and specified fill color,
     * used as a decorative element positioned above buttons to simulate
     * a pinned note or poster effect.
     *
     * @param color The fill color for the pin circle.
     * @return A {@link Circle} representing the decorative pin.
     */
    private Circle createPin(Color color) {
        Circle pin = new Circle(6);
        pin.setFill(color);
        pin.setStroke(Color.BLACK);
        pin.setStrokeWidth(1.5);
        return pin;
    }

    /**
     * Wraps a button with a colored decorative pin positioned at the top center.
     * Creates a visual effect as if the button is pinned to a board with a colored
     * pin.
     * The pin is positioned above the button using StackPane alignment.
     *
     * @param btn      The button to be decorated with a pin.
     * @param pinColor The color of the decorative pin.
     * @return A {@link StackPane} containing both the button and pin with proper
     *         alignment.
     */
    private StackPane btnWithPin(Button btn, Color pinColor) {
        Circle pin = createPin(pinColor);
        StackPane stack = new StackPane(btn, pin);
        StackPane.setAlignment(pin, Pos.TOP_CENTER);
        return stack;
    }

    /**
     * Creates a visual card component for level selection display.
     * Each card contains a thumbnail image, level name, and description arranged
     * vertically.
     * The card has rounded corners, semi-transparent background, and consistent
     * styling.
     * Click event handling should be implemented by the controller that uses this
     * view.
     * 
     * <p>
     * The card includes:
     * </p>
     * <ul>
     * <li>A thumbnail image (260x160 pixels) loaded from the specified resource
     * path</li>
     * <li>A level name label with bold white text styling</li>
     * <li>A description label with word wrapping and smaller font size</li>
     * <li>Consistent padding, sizing, and background styling</li>
     * </ul>
     *
     * @param levelName   The display name of the level (e.g., "Level 1").
     * @param description A brief description of the level's difficulty or features.
     * @param imagePath   Path to the thumbnail image resource (relative to
     *                    classpath).
     * @return A {@link VBox} representing the complete level selection card.
     */
    private VBox createLevelCard(String levelName, String description, String imagePath) {
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

    /**
     * Returns the JavaFX Scene object for this title screen view.
     * This scene contains all the UI components and can be set on a Stage
     * for display. The scene includes the main title screen layout and
     * all overlay screens (hidden by default).
     * 
     * @return The {@link Scene} object containing the complete title screen UI.
     */
    public Scene getScene() {
        return uiContainers.scene;
    }
}
