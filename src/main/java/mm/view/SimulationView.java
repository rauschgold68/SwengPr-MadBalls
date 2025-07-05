package mm.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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

    // Helper classes to reduce method count
    private final MenuGridFactory menuGridBuilder;
    private final OverlayFactory overlayFactory;
    
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

        /** Button to trigger a undo of the last action taken*/
        public Button undoButton;
        
        /** Button to trigger a redo of the last action taken */
        public Button redoButton;
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
        // Initialize helper classes
        this.menuGridBuilder = new MenuGridFactory(simulationButtons);
        this.overlayFactory = new OverlayFactory(overlayButtons, winScreenButtons);
        
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
        menuGridBuilder.buildMenuGrid(isPuzzleMode, layout.sideBar, inventory.inventoryBox);
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
        overlays.overlaySettings = overlayFactory.buildQuickMenuOverlay();
        overlays.overlaySettings.setVisible(false);

        overlays.winScreenOverlay = overlayFactory.buildWinScreenOverlay(primaryStage, isPuzzleMode, atPuzzlesEnd);
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
