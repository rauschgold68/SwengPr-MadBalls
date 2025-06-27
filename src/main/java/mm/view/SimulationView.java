package mm.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/**
 * The {@code SimulationView} class is responsible for constructing and managing
 * the JavaFX UI components for the simulation screen in the MadBalls game.
 * <p>
 * This class follows the MVC (Model-View-Controller) pattern and contains only UI-related code.
 * It constructs the complete simulation interface including:
 * </p>
 * <ul>
 * <li>The main simulation area where physics objects are displayed and interact</li>
 * <li>An inventory sidebar for draggable objects</li>
 * <li>Control buttons for simulation management (play, stop, settings, etc.)</li>
 * <li>Overlay menus for settings and win screen functionality</li>
 * <li>Responsive layout that adapts to different screen sizes</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * The view is typically instantiated by the {@code SimulationController} and configured
 * based on the game mode (puzzle vs. sandbox) and level progression state.
 * </p>
 * 
 * <pre>{@code
 * SimulationView view = new SimulationView(primaryStage, isPuzzleMode, atPuzzlesEnd);
 * Scene scene = view.getScene();
 * primaryStage.setScene(scene);
 * }</pre>
 * 
 * @author MadBalls Development Team
 * @version 1.0
 * @since 1.0
 * @see mm.controller.SimulationController
 * @see mm.model.SimulationModel
 */
public class SimulationView {

    // Grouped component containers
    /** Main layout components */
    private final LayoutComponents layout = new LayoutComponents();
    
    /** Overlay components */
    private final OverlayComponents overlays = new OverlayComponents();
    
    /** Inventory components */
    private final InventoryComponents inventory = new InventoryComponents();

    // Control buttons - grouped by functionality to reduce field count
    /** Group containing simulation control buttons (play, stop, etc.) */
    private final SimulationButtons simulationButtons = new SimulationButtons();
    
    /** Group containing overlay menu buttons (back, quit, close) */
    private final OverlayButtons overlayButtons = new OverlayButtons();
    
    /** Group containing win screen buttons (home, next, export) */
    private final WinScreenButtons winScreenButtons = new WinScreenButtons();

    // Style constants to avoid duplicate literals
    /** CSS class name for circular buttons used in overlays */
    private static final String CIRCLE_BUTTON_CLASS = "circle-button";
    
    /** CSS class name for white labels in overlays */
    private static final String WHITE_LABEL_CLASS = "white-label";
    
    /** CSS class name for main menu buttons */
    private static final String MENU_BUTTON_CLASS = "menu-button";
    
    /** CSS class name for overlay background styling */
    private static final String OVERLAY_BACKGROUND_CLASS = "overlay-background";
    
    /** CSS class name for settings overlay background */
    private static final String SETTINGS_OVERLAY_BACKGROUND_CLASS = "settings-overlay-background";
    
    /** CSS class name for overlay close buttons */
    private static final String OVERLAY_CLOSE_BUTTON_CLASS = "overlay-close-button";
    
    /** CSS class name for large win screen titles */
    private static final String WIN_TITLE_LARGE_CLASS = "win-title-large";
    
    /**
     * Container for main layout components to reduce field count.
     */
    public static class LayoutComponents {
        /** Main border pane that holds all primary UI components */
        public BorderPane mainPane;
        
        /** The simulation space where physics objects are displayed and interact */
        public Pane simSpace;
        
        /** Bottom horizontal bar (currently unused but reserved for future features) */
        public HBox bottomBar;
        
        /** Right sidebar containing inventory and control buttons */
        public VBox sideBar;
        
        /** The main JavaFX scene containing all UI components */
        public Scene scene;
        
        /** Root stack pane that layers the main content and overlays */
        public StackPane rootStack;
    }
    
    /**
     * Container for overlay components to reduce field count.
     */
    public static class OverlayComponents {
        /** Overlay pane for the settings/pause menu */
        public StackPane overlaySettings;
        
        /** Overlay pane for the level completion/win screen */
        public StackPane winScreenOverlay;
    }
    
    /**
     * Container for inventory components to reduce field count.
     */
    public static class InventoryComponents {
        /** Container for the inventory section in the sidebar */
        public StackPane inventoryBox;
        
        /** Vertical container holding individual inventory items */
        public VBox inventoryItemBox;
    }

    /**
     * Inner class to hold simulation control buttons, reducing overall field count.
     * <p>
     * This grouping helps maintain cleaner code organization and reduces PMD violations
     * related to excessive field counts in the main class.
     * </p>
     */
    public static class SimulationButtons {
        /** Button to start the physics simulation */
        public Button playButton;
        
        /** Button to stop and reset the physics simulation */
        public Button stopButton;
        
        /** Button to open the settings overlay menu */
        public Button settingsButton;
        
        /** Button to delete all dropped objects from the simulation */
        public Button deleteButton;
        
        /** Button to import a level from a JSON file (sandbox mode only) */
        public Button importButton;
        
        /** Button to save/export the current level configuration */
        public Button saveButton;
        
        /** Button to manually trigger the win screen (testing/cheat feature) */
        public Button crownButton;
    }

    /**
     * Inner class to hold overlay menu buttons, reducing overall field count.
     * <p>
     * These buttons appear in the ESC-triggered overlay menu for navigation
     * and game control options.
     * </p>
     */
    public static class OverlayButtons {
        /** Button to return to the main title screen */
        public Button overlayBackButton;
        
        /** Button to quit the entire application */
        public Button overlayQuitButton;
        
        /** Button to close the settings overlay and return to simulation */
        public Button overlayCloseButton;
    }

    /**
     * Inner class to hold win screen buttons, reducing overall field count.
     * <p>
     * These buttons appear on the level completion screen and provide
     * options for progression and level management.
     * </p>
     */
    public static class WinScreenButtons {
        /** Button to return to the main menu from the win screen */
        public Button btnWinHome;
        
        /** Button to proceed to the next level (puzzle mode only) */
        public Button btnWinNext;
        
        /** Button to export the completed level configuration */
        public Button btnWinExport;
    }

    /**
     * Constructs the SimulationView and builds the complete UI layout.
     * Creates all components including sidebar, overlays, and applies CSS styling.
     * 
     * @param primaryStage the primary JavaFX stage for binding and sizing
     * @param isPuzzleMode true if running in puzzle mode (affects button availability)
     * @param atPuzzlesEnd true if this is the final level in puzzle mode
     */
    public SimulationView(Stage primaryStage, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        initializeMainComponents();
        createSideBarWithMenuButtons(isPuzzleMode);
        setupMainLayout();
        createOverlays(primaryStage, isPuzzleMode, atPuzzlesEnd);
        setupRootStackAndScene(primaryStage);
    }

    /**
     * Initializes the main UI components for the simulation interface.
     * Creates the main pane, simulation space, and bottom bar with proper styling.
     */
    private void initializeMainComponents() {
        layout.mainPane = new BorderPane();
        layout.mainPane.setId("root-pane");

        layout.simSpace = new Pane();
        layout.simSpace.getStyleClass().add("sim-space");

        layout.bottomBar = new HBox();
        layout.bottomBar.getStyleClass().add("bottom-bar");
        layout.bottomBar.setPrefHeight(150);
    }

    /**
     * Creates the sidebar with inventory and control buttons.
     * 
     * @param isPuzzleMode determines which buttons are available (import disabled in puzzle mode)
     */
    private void createSideBarWithMenuButtons(boolean isPuzzleMode) {
        layout.sideBar = new VBox();
        layout.sideBar.getStyleClass().add("side-bar");
        layout.sideBar.setPrefWidth(200);

        createInventoryComponents();
        createMenuGrid(isPuzzleMode);
    }

    /**
     * Creates the inventory box and item container for draggable objects.
     */
    private void createInventoryComponents() {
        inventory.inventoryBox = new StackPane();
        inventory.inventoryBox.getStyleClass().add("inventory-box");
        VBox.setVgrow(inventory.inventoryBox, Priority.ALWAYS);
        inventory.inventoryItemBox = new VBox();
        inventory.inventoryBox.getChildren().add(inventory.inventoryItemBox);
        inventory.inventoryItemBox.getStyleClass().add("inventoryItemBox");
    }

    /**
     * Creates a 3x3 grid of control buttons for simulation management.
     * 
     * @param isPuzzleMode affects which buttons are displayed
     */
    private void createMenuGrid(boolean isPuzzleMode) {
        HBox squareContainer = new HBox();
        squareContainer.getStyleClass().add("square-container");
        squareContainer.setAlignment(Pos.CENTER);

        StackPane menuSquare = new StackPane();
        menuSquare.getStyleClass().add("menu-square");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("menu-grid");

        populateMenuGrid(grid, isPuzzleMode);

        menuSquare.getChildren().add(grid);
        squareContainer.getChildren().add(menuSquare);
        layout.sideBar.getChildren().addAll(inventory.inventoryBox, squareContainer);
    }

    /**
     * Populates the menu grid with buttons and icons based on position and game mode.
     * 
     * @param grid the GridPane to populate
     * @param isPuzzleMode determines button icons and availability
     */
    private void populateMenuGrid(GridPane grid, boolean isPuzzleMode) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = createMenuButton();
                FontIcon icon = createIconForGridPosition(row, col, isPuzzleMode, btn);
                
                if (icon != null) {
                    setupIcon(icon);
                    btn.setGraphic(icon);
                }

                grid.add(btn, col, row);
            }
        }
    }

    /**
     * Creates a standard menu button with consistent styling.
     * 
     * @return a new Button with menu styling applied
     */
    private Button createMenuButton() {
        Button btn = new Button();
        btn.getStyleClass().add(MENU_BUTTON_CLASS);
        return btn;
    }

    /**
     * Creates the appropriate icon for a grid position and assigns the button reference.
     * Simplified version with reduced parameters and complexity.
     * 
     * @param row the grid row (0-2)
     * @param col the grid column (0-2)
     * @param isPuzzleMode determines button availability
     * @param btn the button instance to assign
     * @return the FontIcon for this position, or null if no icon
     */
    private FontIcon createIconForGridPosition(int row, int col, boolean isPuzzleMode, Button btn) {
        // Row 0: Top row controls
        if (row == 0) {
            return createTopRowIcon(col, btn);
        }
        // Row 1: Middle row controls
        if (row == 1) {
            return createMiddleRowIcon(col, isPuzzleMode, btn);
        }
        // Row 2: Bottom row controls
        if (row == 2) {
            return createBottomRowIcon(col, btn);
        }
        return null;
    }
    
    /**
     * Creates icons for the top row of the grid (play, stop, settings).
     * 
     * @param col the column position
     * @param btn the button to assign
     * @return the appropriate icon or null
     */
    private FontIcon createTopRowIcon(int col, Button btn) {
        switch (col) {
            case 0:
                simulationButtons.playButton = btn;
                return new FontIcon(FontAwesomeSolid.PLAY);
            case 1:
                simulationButtons.stopButton = btn;
                return new FontIcon(FontAwesomeSolid.STOP);
            case 2:
                simulationButtons.settingsButton = btn;
                return new FontIcon(FontAwesomeSolid.COGS);
            default:
                return null;
        }
    }
    
    /**
     * Creates icons for the middle row of the grid (delete, import, save).
     * 
     * @param col the column position
     * @param isPuzzleMode determines button availability and icons
     * @param btn the button to assign
     * @return the appropriate icon or null
     */
    private FontIcon createMiddleRowIcon(int col, boolean isPuzzleMode, Button btn) {
        switch (col) {
            case 0:
                simulationButtons.deleteButton = btn;
                String resetIcon = isPuzzleMode ? "REDO_ALT" : "TRASH_ALT";
                return new FontIcon(FontAwesomeSolid.valueOf(resetIcon));
            case 1:
                if (!isPuzzleMode) {
                    simulationButtons.importButton = btn;
                    return new FontIcon(FontAwesomeSolid.FOLDER_PLUS);
                }
                return null;
            case 2:
                simulationButtons.saveButton = btn;
                return new FontIcon(FontAwesomeSolid.SAVE);
            default:
                return null;
        }
    }
    
    /**
     * Creates icons for the bottom row of the grid (currently only crown button).
     * 
     * @param col the column position
     * @param btn the button to assign
     * @return the appropriate icon or null
     */
    private FontIcon createBottomRowIcon(int col, Button btn) {
        if (col == 0) {
            simulationButtons.crownButton = btn;
            return new FontIcon(FontAwesomeSolid.CROWN);
        }
        return null;
    }

    /**
     * Configures the standard visual properties for menu icons.
     * 
     * @param icon the FontIcon to configure
     */
    private void setupIcon(FontIcon icon) {
        icon.setIconSize(16);
        icon.setIconColor(Color.WHITE);
    }

    /**
     * Arranges the main UI components into their layout positions.
     */
    private void setupMainLayout() {
        layout.mainPane.setCenter(layout.simSpace);
        layout.mainPane.setRight(layout.sideBar);
        layout.mainPane.setBottom(layout.bottomBar);
    }

    /**
     * Creates both overlay components (settings and win screen).
     * 
     * @param primaryStage the primary stage for sizing
     * @param isPuzzleMode determines win screen button configuration
     * @param atPuzzlesEnd affects win screen button options
     */
    private void createOverlays(Stage primaryStage, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        overlays.overlaySettings = createQuickMenuOverlay();
        overlays.overlaySettings.setVisible(false);

        overlays.winScreenOverlay = createWinScreenOverlay(primaryStage, isPuzzleMode, atPuzzlesEnd);
        overlays.winScreenOverlay.setVisible(false);
    }

    /**
     * Sets up the root stack pane and creates the final scene with CSS styling.
     * 
     * @param primaryStage the primary stage for size binding
     */
    private void setupRootStackAndScene(Stage primaryStage) {
        layout.rootStack = new StackPane();
        layout.rootStack.getChildren().addAll(layout.mainPane, overlays.overlaySettings, overlays.winScreenOverlay);
        layout.rootStack.prefWidthProperty().bind(primaryStage.widthProperty());
        layout.rootStack.prefHeightProperty().bind(primaryStage.heightProperty());

        layout.scene = new Scene(layout.rootStack);
        layout.scene.getStylesheets().add(
                getClass().getResource("/styling/simulation.css").toExternalForm());
    }

    /**
     * Creates the quick menu overlay with settings and navigation options.
     * 
     * @return a StackPane representing the settings overlay
     */
    private StackPane createQuickMenuOverlay() {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add(SETTINGS_OVERLAY_BACKGROUND_CLASS);
        overlay.setPickOnBounds(true);

        VBox window = createOverlayWindow();
        setupOverlayButtons(window);

        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Creates the styled window container for overlay content.
     * 
     * @return a VBox serving as the overlay window
     */
    private VBox createOverlayWindow() {
        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(15));
        window.setMaxWidth(300);
        window.setMaxHeight(180);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(10, 10, 20, 0.9), new CornerRadii(10), Insets.EMPTY)));
        return window;
    }

    /**
     * Creates and configures the overlay menu buttons.
     * 
     * @param window the VBox container for the buttons
     */
    private void setupOverlayButtons(VBox window) {
        HBox topRow = new HBox();
        overlayButtons.overlayCloseButton = new Button("✕");
        overlayButtons.overlayCloseButton.getStyleClass().add(OVERLAY_CLOSE_BUTTON_CLASS);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(spacer, overlayButtons.overlayCloseButton);

        overlayButtons.overlayBackButton = new Button("Back to Title Screen");
        overlayButtons.overlayBackButton.getStyleClass().add(MENU_BUTTON_CLASS);
        overlayButtons.overlayBackButton.setMaxWidth(Double.MAX_VALUE);
        overlayButtons.overlayBackButton.setPrefHeight(40);

        overlayButtons.overlayQuitButton = new Button("Quit Game");
        overlayButtons.overlayQuitButton.getStyleClass().add(MENU_BUTTON_CLASS);
        overlayButtons.overlayQuitButton.setMaxWidth(Double.MAX_VALUE);
        overlayButtons.overlayQuitButton.setPrefHeight(40);

        window.getChildren().addAll(topRow, overlayButtons.overlayBackButton, overlayButtons.overlayQuitButton);
    }

    /**
     * Creates the level completion overlay with context-appropriate buttons.
     * 
     * @param ownerStage the primary stage for size binding
     * @param isPuzzleMode determines button options
     * @param atPuzzlesEnd affects button configuration
     * @return a StackPane representing the win screen overlay
     */
    private StackPane createWinScreenOverlay(Stage ownerStage, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add(OVERLAY_BACKGROUND_CLASS);
        overlay.setVisible(false);
        overlay.prefWidthProperty().bind(ownerStage.widthProperty());
        overlay.prefHeightProperty().bind(ownerStage.heightProperty());

        VBox window = createWinWindow(atPuzzlesEnd);
        HBox buttonRow = createWinButtonRow(isPuzzleMode, atPuzzlesEnd, overlay);

        window.getChildren().add(buttonRow);
        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Creates the win screen window with crown icon and title.
     * 
     * @param atPuzzlesEnd determines the title text
     * @return a VBox containing the win screen content
     */
    private VBox createWinWindow(boolean atPuzzlesEnd) {
        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(30));
        window.setMaxWidth(600);
        window.setMaxHeight(250);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(20, 20, 40, 0.9),
                new CornerRadii(16), Insets.EMPTY)));
        window.getStyleClass().add("win-window");

        FontIcon crown = new FontIcon(FontAwesomeSolid.CROWN);
        crown.setIconSize(48);
        crown.setIconColor(Color.GOLD);

        String titleText = atPuzzlesEnd ? "Puzzle Series Complete!" : "Level Complete!";
        Label title = new Label(titleText);
        title.getStyleClass().add(WIN_TITLE_LARGE_CLASS);

        window.getChildren().addAll(crown, title);
        return window;
    }

    /**
     * Creates the button row for the win screen based on game mode.
     * 
     * @param isPuzzleMode determines the button set to display
     * @param atPuzzlesEnd affects button availability
     * @param overlay reference to the overlay for close functionality
     * @return a HBox containing the appropriate buttons
     */
    private HBox createWinButtonRow(boolean isPuzzleMode, boolean atPuzzlesEnd, StackPane overlay) {
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        HBox mainMenuBox = createWinButton(FontAwesomeSolid.HOME, "Main Menu", overlay);
        HBox exportBox = createWinButton(FontAwesomeSolid.FILE_EXPORT, "Export Level", null);

        if (isPuzzleMode && !atPuzzlesEnd) {
            HBox nextBox = createWinButton(FontAwesomeSolid.ARROW_RIGHT, "Next Level", overlay);
            addButtonsWithSpacing(buttonRow, mainMenuBox, exportBox, nextBox);
        } else if (isPuzzleMode && atPuzzlesEnd) {
            addButtonsWithSpacing(buttonRow, mainMenuBox, exportBox);
        } else {
            HBox resumeBox = createWinButton(FontAwesomeSolid.PENCIL_ALT, "Resume Editing", overlay);
            addButtonsWithSpacing(buttonRow, mainMenuBox, resumeBox);
        }

        return buttonRow;
    }

    /**
     * Creates a win screen button with icon and label.
     * 
     * @param iconType the FontAwesome icon to display
     * @param labelText the button label text
     * @param overlay optional overlay reference for close functionality
     * @return an HBox containing the button and label
     */
    private HBox createWinButton(FontAwesomeSolid iconType, String labelText, StackPane overlay) {
        Button btn = new Button();
        btn.getStyleClass().add(CIRCLE_BUTTON_CLASS);
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(20);
        icon.setIconColor(Color.WHITE);
        btn.setGraphic(icon);

        if (overlay != null) {
            btn.setOnAction(e -> overlay.setVisible(false));
        }

        // Store button references
        if ("Main Menu".equals(labelText)) {
            winScreenButtons.btnWinHome = btn;
        } else if ("Next Level".equals(labelText)) {
            winScreenButtons.btnWinNext = btn;
        } else if ("Export Level".equals(labelText)) {
            winScreenButtons.btnWinExport = btn;
        }

        Label label = new Label(labelText);
        label.getStyleClass().add(WHITE_LABEL_CLASS);
        
        HBox buttonBox = new HBox(8, btn, label);
        if ("Next Level".equals(labelText)) {
            buttonBox = new HBox(8, label, btn); // Reverse order for next button
        }
        buttonBox.setAlignment(Pos.CENTER);

        return buttonBox;
    }

    /**
     * Adds buttons to a horizontal layout with evenly distributed spacing.
     * 
     * @param buttonRow the HBox container for the buttons
     * @param buttons the button containers to add with spacing
     */
    private void addButtonsWithSpacing(HBox buttonRow, HBox... buttons) {
        for (int i = 0; i < buttons.length; i++) {
            buttonRow.getChildren().add(buttons[i]);
            if (i < buttons.length - 1) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                buttonRow.getChildren().add(spacer);
            }
        }
    }

    //==================================================================================
    // COMPONENT ACCESS METHODS
    //==================================================================================
    
    /** @return the main Scene for the simulation interface */
    public Scene getScene() { return layout.scene; }
    
    /** @return the simulation space where physics objects are displayed */
    public Pane getSimSpace() { return layout.simSpace; }
    
    /** @return the inventory container for draggable objects */
    public StackPane getInventoryBox() { return inventory.inventoryBox; }
    
    /** @return the container holding individual inventory items */
    public VBox getInventoryItemBox() { return inventory.inventoryItemBox; }
    
    /** @return the right sidebar containing inventory and controls */
    public VBox getSideBar() { return layout.sideBar; }
    
    /** @return the bottom bar (reserved for future features) */
    public HBox getBottomBar() { return layout.bottomBar; }
    
    /** @return the settings/pause menu overlay */
    public StackPane getOverlaySettings() { return overlays.overlaySettings; }
    
    /** @return the level completion overlay */
    public StackPane getWinScreenOverlay() { return overlays.winScreenOverlay; }
    
    /** @return the root container that layers all components */
    public StackPane getRootStack() { return layout.rootStack; }

    //==================================================================================
    // BUTTON ACCESS METHODS
    //==================================================================================
    
    /** @return the group containing simulation control buttons */
    public SimulationButtons getSimulationButtons() { return simulationButtons; }
    
    /** @return the group containing overlay menu buttons */
    public OverlayButtons getOverlayButtons() { return overlayButtons; }
    
    /** @return the group containing win screen buttons */
    public WinScreenButtons getWinScreenButtons() { return winScreenButtons; }
}
