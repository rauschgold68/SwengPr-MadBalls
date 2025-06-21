package mm.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mm.controller.PhysicsAnimationController;
import mm.model.SimulationModel;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/**
 * The {@code SimulationView} class is responsible for constructing and managing
 * the JavaFX UI components for the simulation screen in the MadBalls game.
 * <p>
 * This class contains only UI-related code and exposes methods to access
 * and update the main panes and controls. All simulation logic and state
 * should be handled by the controller and model, following the MVC pattern.
 * </p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 * <li>Builds and arranges the main simulation layout, including the simulation
 * area, sidebar, inventory, and overlays.</li>
 * <li>Provides access to UI components for the controller to update or attach
 * event handlers.</li>
 * <li>Manages the quick menu overlay for settings, returning to the title
 * screen, and quitting the game.</li>
 * <li>Handles basic UI styling and layout, but not business logic or event
 * handling.</li>
 * </ul>
 *
 * <h3>UI Structure:</h3>
 * <ul>
 * <li>{@code BorderPane mainPane} - The root layout for the simulation
 * screen.</li>
 * <li>{@code Pane simSpace} - The central area where simulation objects are
 * displayed.</li>
 * <li>{@code VBox sideBar} - The sidebar containing the inventory and menu
 * buttons.</li>
 * <li>{@code StackPane inventoryBox} - The container for inventory items.</li>
 * <li>{@code VBox inventoryItemBox} - The box holding individual inventory item
 * nodes.</li>
 * <li>{@code HBox bottomBar} - The bottom bar for additional controls or
 * information.</li>
 * <li>{@code StackPane overlaySettings} - The overlay for quick menu actions
 * (settings, back, quit).</li>
 * <li>{@code StackPane rootStack} - The root stack pane to layer overlays above
 * the main content.</li>
 * </ul>
 *
 * <h3>Note:</h3>
 * <ul>
 * <li>This class does not contain any simulation logic or state
 * management.</li>
 * <li>Event handlers should be attached by the controller, not in this
 * class.</li>
 * </ul>
 */
public class SimulationView {

    private BorderPane mainPane;
    private Pane simSpace;
    private HBox bottomBar;
    private StackPane inventoryBox;
    private VBox inventoryItemBox;
    private VBox sideBar;
    private StackPane overlaySettings;
    private Scene scene;
    private StackPane rootStack;
    private StackPane winScreenOverlay;

    // Buttons during the simulation.
    public Button playButton;
    public Button stopButton;
    public Button settingsButton;
    public Button deleteButton;
    public Button importButton;
    public Button saveButton;
    public Button crownButton;
    public Button btnWinHome;
    public Button btnWinNext;
    public Button btnWinExport;

    // Buttons from quick menu.
    public Button overlayBackButton;
    public Button overlayQuitButton;
    public Button overlayCloseButton;

    /**
     * Constructs the SimulationView and builds the UI layout for the simulation
     * screen.
     * Initializes all main panes, sidebars, inventory containers, and overlays.
     *
     * @param primaryStage the primary stage of the application, used for binding
     *                     and overlay sizing
     */
    public SimulationView(Stage primaryStage) {
        // Main layout container
        mainPane = new BorderPane();
        mainPane.setId("root-pane");

        // Simulation area
        simSpace = new Pane();
        simSpace.getStyleClass().add("sim-space");
        mainPane.setCenter(simSpace);

        // Sidebar with menu buttons
        sideBar = new VBox();
        sideBar.getStyleClass().add("side-bar");
        sideBar.setPrefWidth(200);

        // Inventory box
        inventoryBox = new StackPane();
        inventoryBox.getStyleClass().add("inventory-box");
        VBox.setVgrow(inventoryBox, Priority.ALWAYS);
        inventoryItemBox = new VBox();
        inventoryBox.getChildren().add(inventoryItemBox);
        inventoryItemBox.getStyleClass().add("inventoryItemBox");

        HBox squareContainer = new HBox();
        squareContainer.getStyleClass().add("square-container");
        squareContainer.setAlignment(Pos.CENTER);

        StackPane menuSquare = new StackPane();
        menuSquare.getStyleClass().add("menu-square");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("menu-grid");

        // Populate grid with buttons/icons (no event handlers here)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = new Button();
                btn.getStyleClass().add("menu-button");
                FontIcon icon = null;

                if (row == 0 && col == 0) {
                    icon = new FontIcon(FontAwesomeSolid.PLAY);
                    playButton = btn;
                } else if (row == 0 && col == 1) {
                    icon = new FontIcon(FontAwesomeSolid.STOP);
                    stopButton = btn;
                } else if (row == 0 && col == 2) {
                    icon = new FontIcon(FontAwesomeSolid.COGS);
                    settingsButton = btn;
                } else if (row == 1 && col == 0) {
                    icon = new FontIcon(FontAwesomeSolid.TRASH_ALT);
                    deleteButton = btn;
                } else if (row == 1 && col == 1) {
                    icon = new FontIcon(FontAwesomeSolid.FOLDER_PLUS);
                    importButton = btn;
                } else if (row == 1 && col == 2) {
                    icon = new FontIcon(FontAwesomeSolid.SAVE);
                    saveButton = btn;
                } else if (row == 2 && col == 1) {
                    icon = new FontIcon(FontAwesomeSolid.CROWN);
                    crownButton = btn;
                }

                if (icon != null) {
                    icon.setIconSize(16);
                    icon.setIconColor(Color.WHITE);
                    btn.setGraphic(icon);
                }

                grid.add(btn, col, row);
            }
        }

        menuSquare.getChildren().add(grid);
        squareContainer.getChildren().add(menuSquare);
        sideBar.getChildren().addAll(inventoryBox, squareContainer);
        mainPane.setRight(sideBar);

        // Bottom bar
        bottomBar = new HBox();
        bottomBar.getStyleClass().add("bottom-bar");
        bottomBar.setPrefHeight(150);
        mainPane.setBottom(bottomBar);

        // Overlay for settings (initially hidden)
        overlaySettings = createQuickMenuOverlay(primaryStage);
        overlaySettings.setVisible(false);

        // Overlay for win screen (initially hidden)
        winScreenOverlay = createWinScreenOverlay(primaryStage, true);
        winScreenOverlay.setVisible(false);

        // Root stack to layer overlay on top of mainPane
        rootStack = new StackPane();
        rootStack.getChildren().addAll(mainPane, overlaySettings, winScreenOverlay);
        rootStack.prefWidthProperty().bind(primaryStage.widthProperty());
        rootStack.prefHeightProperty().bind(primaryStage.heightProperty());

        scene = new Scene(rootStack);
        scene.getStylesheets().add(
                getClass().getResource("/styling/simulation.css").toExternalForm());

    }

    /**
     * Creates the quick menu overlay for settings, returning to the title screen,
     * and quitting the game.
     * The overlay is initially hidden and can be toggled visible by the controller.
     *
     * @param ownerStage the owner stage for the overlay, used for sizing and
     *                   binding
     * @return the StackPane overlay containing the quick menu
     */
    private StackPane createQuickMenuOverlay(Stage ownerStage) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(30, 30, 50, 0.7);");
        overlay.setPickOnBounds(true);

        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(15));
        window.setMaxWidth(300);
        window.setMaxHeight(180);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(10, 10, 20, 0.9), new CornerRadii(10), Insets.EMPTY)));

        HBox topRow = new HBox();
        overlayCloseButton = new Button("✕");
        overlayCloseButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(spacer, overlayCloseButton);

        overlayBackButton = new Button("Back to Title Screen");
        overlayBackButton.getStyleClass().add("menu-button");
        overlayBackButton.setMaxWidth(Double.MAX_VALUE);
        overlayBackButton.setPrefHeight(40);

        overlayQuitButton = new Button("Quit Game");
        overlayQuitButton.getStyleClass().add("menu-button");
        overlayQuitButton.setMaxWidth(Double.MAX_VALUE);
        overlayQuitButton.setPrefHeight(40);

        window.getChildren().addAll(topRow, overlayBackButton, overlayQuitButton);

        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Creates a win‑screen overlay with a crown icon and optional Next Level
     * button.
     *
     * @param ownerStage     the primary Stage (for sizing/bindings)
     * @param showNextButton whether to display the Next Level button
     * @return a StackPane overlay ready to add to your scene root
     */
    private StackPane createWinScreenOverlay(Stage ownerStage, boolean isPuzzleMode) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        overlay.setVisible(false);
        overlay.prefWidthProperty().bind(ownerStage.widthProperty());
        overlay.prefHeightProperty().bind(ownerStage.heightProperty());

        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(30));
        window.setMaxWidth(600);
        window.setMaxHeight(250);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(20, 20, 40, 0.9),
                new CornerRadii(16), Insets.EMPTY)));
        window.getStyleClass().add("win-window");

        // crown icon
        FontIcon crown = new FontIcon(FontAwesomeSolid.CROWN);
        crown.setIconSize(48);
        crown.setIconColor(Color.GOLD);

        Label title = new Label("Level Complete!");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        // Main Menu icon
        btnWinHome = new Button();
        btnWinHome.getStyleClass().addAll("circle-button");
        FontIcon homeIcon = new FontIcon(FontAwesomeSolid.HOME);
        homeIcon.setIconSize(20);
        homeIcon.setIconColor(Color.WHITE);
        btnWinHome.setGraphic(homeIcon);
        btnWinHome.setOnAction(e -> overlay.setVisible(false));

        Label lblMainMenu = new Label("Main Menu");
        lblMainMenu.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        HBox mainMenuBox = new HBox(8, btnWinHome, lblMainMenu);
        mainMenuBox.setAlignment(Pos.CENTER);

        // Next Level icon
        btnWinNext = new Button();
        btnWinNext.getStyleClass().addAll("circle-button");
        FontIcon nextIcon = new FontIcon(FontAwesomeSolid.ARROW_RIGHT);
        nextIcon.setIconSize(20);
        nextIcon.setIconColor(Color.WHITE);
        btnWinNext.setGraphic(nextIcon);
        btnWinNext.setOnAction(e -> {
            overlay.setVisible(false);
            System.out.println("Next level!");
        });

        Label lblNext = new Label("Next Level");
        lblNext.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        HBox nextBox = new HBox(8, lblNext, btnWinNext);
        nextBox.setAlignment(Pos.CENTER);

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // Resume Editing Button
        Button btnResume = new Button();
        btnResume.getStyleClass().addAll("circle-button");
        FontIcon editIcon = new FontIcon(FontAwesomeSolid.PENCIL_ALT);
        editIcon.setIconSize(20);
        editIcon.setIconColor(Color.WHITE);
        btnResume.setGraphic(editIcon);
        btnResume.setOnAction(e -> overlay.setVisible(false));

        Label lblResume = new Label("Resume Editing");
        lblResume.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        HBox resumeBox = new HBox(8, lblResume, btnResume);
        resumeBox.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (isPuzzleMode) {
            btnWinExport = new Button();
            btnWinExport.getStyleClass().addAll("circle-button");
            FontIcon exportIcon = new FontIcon(FontAwesomeSolid.FILE_EXPORT);
            exportIcon.setIconSize(20);
            exportIcon.setIconColor(Color.WHITE);
            btnWinExport.setGraphic(exportIcon);
            btnWinExport.setOnAction(e -> {
                System.out.println("Export Level clicked!");
            });

            Label lblExport = new Label("Export Level");
            lblExport.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            HBox exportBox = new HBox(8, btnWinExport, lblExport);
            exportBox.setAlignment(Pos.CENTER);

            buttonRow.getChildren().addAll(mainMenuBox, spacer1, exportBox, spacer2, nextBox);
        } else {

            buttonRow.getChildren().addAll(mainMenuBox, spacer1, resumeBox);
        }

        window.getChildren().addAll(crown, title, buttonRow);
        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Returns the main simulation scene containing all UI components.
     * 
     * @return the JavaFX Scene for the simulation screen
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Returns the simulation area pane where game objects are displayed.
     * 
     * @return the Pane representing the simulation area
     */
    public Pane getSimSpace() {
        return simSpace;
    }

    /**
     * Returns the inventory box container.
     * 
     * @return the StackPane containing the inventory items
     */
    public StackPane getInventoryBox() {
        return inventoryBox;
    }

    /**
     * Returns the VBox containing inventory item nodes.
     * 
     * @return the VBox for inventory items
     */
    public VBox getInventoryItemBox() {
        return inventoryItemBox;
    }

    /**
     * Returns the sidebar VBox containing inventory and menu buttons.
     * 
     * @return the VBox representing the sidebar
     */
    public VBox getSideBar() {
        return sideBar;
    }

    /**
     * Returns the bottom bar HBox for additional controls or information.
     * 
     * @return the HBox representing the bottom bar
     */
    public HBox getBottomBar() {
        return bottomBar;
    }

    /**
     * Returns the overlay settings StackPane for quick menu actions.
     * 
     * @return the StackPane for the quick menu overlay
     */
    public StackPane getOverlaySettings() {
        return overlaySettings;
    }

    /**
     * Returns the win screen overlay StackPane.
     * 
     * @return the StackPane for the win screen overlay
     */
    public StackPane getWinScreenOverlay() {
        return winScreenOverlay;
    }

    /**
     * Returns the root stack pane that layers overlays above the main content.
     * 
     * @return the StackPane root of the simulation view
     */
    public StackPane getRootStack() {
        return rootStack;
    }
}
