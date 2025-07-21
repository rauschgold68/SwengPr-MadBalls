package mm.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The {@code SimulationView} class is responsible for constructing and managing
 * the JavaFX UI components for the simulation screen in the MadBalls game.
 * <p>
 * This class follows the MVC (Model-View-Controller) pattern and contains only
 * UI-related code.
 * It constructs the complete simulation interface including:
 * </p>
 * <ul>
 * <li>The main simulation area where physics objects are displayed and
 * interact</li>
 * <li>An inventory sidebar for draggable objects</li>
 * <li>Control buttons for simulation management (play, stop, settings,
 * etc.)</li>
 * <li>Overlay menus for settings and win screen functionality</li>
 * <li>Responsive layout that adapts to different screen sizes</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <p>
 * The view is typically instantiated by the {@code SimulationController} and
 * configured
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
        
        /** JSON viewer area showing real-time simulation state */
        public TextArea jsonViewer;
        
        /** ScrollPane containing the JSON viewer */
        public ScrollPane jsonScrollPane;
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

        /** Scroll pane to make inventory scrollable when it gets too long */
        public ScrollPane inventoryScrollPane;

        /** Vertical container holding individual inventory items */
        public VBox inventoryItemBox;
    }

    /**
     * Inner class to hold simulation control buttons, reducing overall field count.
     * <p>
     * This grouping helps maintain cleaner code organization and reduces PMD
     * violations
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

        /** Button to trigger a undo of the last action taken */
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
     * @param isPuzzleMode true if running in puzzle mode (affects button
     *                     availability)
     * @param atPuzzlesEnd true if this is the final level in puzzle mode
     */
    public SimulationView(Stage primaryStage, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        // Initialize helper classes
        this.menuGridBuilder = new MenuGridFactory(simulationButtons);
        this.overlayFactory = new OverlayFactory(overlayButtons, winScreenButtons);

        initializeMainComponents(isPuzzleMode); // Pass isPuzzleMode parameter
        createSideBarWithMenuButtons(isPuzzleMode);
        setupMainLayout(isPuzzleMode); // Pass isPuzzleMode parameter
        createOverlays(primaryStage, isPuzzleMode, atPuzzlesEnd);
        setupRootStackAndScene(primaryStage);
    }

    /**
     * Initializes the main UI components for the simulation interface.
     * Creates the main pane, simulation space, and bottom bar with proper styling
     * and size constraints.
     * 
     * @param isPuzzleMode true if in puzzle mode (no JSON viewer), false for sandbox mode
     */
    private void initializeMainComponents(boolean isPuzzleMode) {
        layout.mainPane = new BorderPane();
        layout.mainPane.setId("root-pane");

        layout.simSpace = new Pane();
        layout.simSpace.getStyleClass().add("sim-space");

        // Add background image directly via Java code
        try {
            String backgroundImagePath = getClass().getResource("/pictures/simSpaceBg.png").toExternalForm();
            layout.simSpace.setStyle(
                    "-fx-background-image: url('" + backgroundImagePath + "'); " +
                            "-fx-background-size: cover; " +
                            "-fx-background-position: center center; " +
                            "-fx-background-repeat: no-repeat;");
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
            // Fallback to solid color if image not found
            layout.simSpace.setStyle("-fx-background-color: white;");
        }

        // Ensure simSpace doesn't grow beyond reasonable bounds
        layout.simSpace.setMaxWidth(Region.USE_COMPUTED_SIZE);
        layout.simSpace.setMaxHeight(Region.USE_COMPUTED_SIZE);

        // Always create bottom bar, but content depends on mode
        createBottomBar(isPuzzleMode);
    }

    /**
     * Creates the bottom bar with mode-specific content.
     * In sandbox mode: includes JSON viewer
     * In puzzle mode: empty bar for future features
     * 
     * @param isPuzzleMode true if in puzzle mode, false for sandbox mode
     */
    private void createBottomBar(boolean isPuzzleMode) {
    layout.bottomBar = new HBox();
    layout.bottomBar.getStyleClass().add("bottom-bar");
    
    layout.bottomBar.setPrefHeight(200);
    layout.bottomBar.setMaxHeight(200);
    layout.bottomBar.setMinHeight(200);
    // Add padding inside the bottom bar
    layout.bottomBar.setSpacing(10);
    layout.bottomBar.setStyle("-fx-padding: 10;");
    
    if (!isPuzzleMode) {
        // Sandbox mode: add JSON viewer directly (no ScrollPane wrapper)
        createJsonViewer();
        layout.bottomBar.getChildren().add(layout.jsonViewer);
        HBox.setHgrow(layout.jsonViewer, Priority.ALWAYS);
    } else {
        // Puzzle mode: set background image for bottom bar
        try {
            String bottomBarImagePath = getClass().getResource("/pictures/bottombar2.png").toExternalForm();
            layout.bottomBar.setStyle("-fx-background-color: transparent; " +
                    "-fx-background-image: url('" + bottomBarImagePath + "'); " +
                    "-fx-background-repeat: no-repeat; " +
                    "-fx-background-position: center; " +
                    "-fx-background-size: cover;");
            System.out.println("Bottom bar background image loaded successfully: " + bottomBarImagePath);
        } catch (Exception e) {
            System.err.println("Warning: Could not load bottom bar background image: " + e.getMessage());
        }
    }
}

    private void createJsonViewer() {
    layout.jsonViewer = new TextArea();
    layout.jsonViewer.setEditable(true);
    layout.jsonViewer.getStyleClass().add("json-viewer");
    layout.jsonViewer.setWrapText(false);
    // Remove fixed height constraints - let it fill the bottom bar completely
    layout.jsonViewer.setPrefHeight(Region.USE_COMPUTED_SIZE);
    layout.jsonViewer.setMinHeight(Region.USE_PREF_SIZE);
    layout.jsonViewer.setMaxHeight(Region.USE_COMPUTED_SIZE);
}

    /**
     * Creates the sidebar with inventory and control buttons.
     * 
     * @param isPuzzleMode determines which buttons are available (import disabled
     *                     in puzzle mode)
     */
    private void createSideBarWithMenuButtons(boolean isPuzzleMode) {
        layout.sideBar = new VBox();
        layout.sideBar.getStyleClass().add("side-bar");
        layout.sideBar.setPrefWidth(350);
        layout.sideBar.setMaxWidth(350);
        layout.sideBar.setMinWidth(350);

        createInventoryComponents();
        createMenuGrid(isPuzzleMode);
    }

    /**
     * Creates the inventory box and item container for draggable objects.
     * Wraps the inventory in a ScrollPane to prevent it from growing beyond the
     * window bounds.
     */
    private void createInventoryComponents() {
        inventory.inventoryBox = new StackPane();
        inventory.inventoryBox.getStyleClass().add("inventory-box");
        VBox.setVgrow(inventory.inventoryBox, Priority.ALWAYS);

        // Create the item container
        inventory.inventoryItemBox = new VBox();
        inventory.inventoryItemBox.getStyleClass().add("inventoryItemBox");

        // Wrap in ScrollPane to make it scrollable when content exceeds available space
        inventory.inventoryScrollPane = new ScrollPane(inventory.inventoryItemBox);
        inventory.inventoryScrollPane.setFitToWidth(true);
        inventory.inventoryScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        inventory.inventoryScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        inventory.inventoryScrollPane.getStyleClass().add("inventory-scroll-pane");

        // Increase scroll speed for better user experience
        inventory.inventoryScrollPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 3; // 3x faster scroll speed
            double height = inventory.inventoryScrollPane.getContent().getBoundsInLocal().getHeight();
            double vvalue = inventory.inventoryScrollPane.getVvalue();
            inventory.inventoryScrollPane.setVvalue(vvalue - deltaY / height);
            event.consume();
        });

        inventory.inventoryBox.getChildren().add(inventory.inventoryScrollPane);
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
     * 
     * @param isPuzzleMode true if in puzzle mode, false for sandbox mode
     */
    private void setupMainLayout(boolean isPuzzleMode) {
        layout.mainPane.setCenter(layout.simSpace);
        layout.mainPane.setRight(layout.sideBar);
        
        // Always add bottom bar (content varies by mode)
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
     * Ensures the layout respects the primary stage's fixed dimensions.
     * 
     * @param primaryStage the primary stage for size binding
     */
    private void setupRootStackAndScene(Stage primaryStage) {
        layout.rootStack = new StackPane();
        layout.rootStack.getChildren().addAll(layout.mainPane, overlays.overlaySettings, overlays.winScreenOverlay);

        // Bind to stage dimensions to ensure layout respects the fixed window size
        layout.rootStack.prefWidthProperty().bind(primaryStage.widthProperty());
        layout.rootStack.prefHeightProperty().bind(primaryStage.heightProperty());
        layout.rootStack.maxWidthProperty().bind(primaryStage.widthProperty());
        layout.rootStack.maxHeightProperty().bind(primaryStage.heightProperty());

        // Create scene with fixed dimensions to match the primary stage
        layout.scene = new Scene(layout.rootStack, primaryStage.getWidth(), primaryStage.getHeight());
        layout.scene.getStylesheets().add(
                getClass().getResource("/styling/simulation.css").toExternalForm());

        // Subtle resize trick to fix framebuffer issues on Linux
        // Automatically triggers a minimal window resize after a short delay
        javafx.application.Platform.runLater(() -> {
            // Wait a moment for the scene to be fully initialized
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(10), e -> {
                        double currentWidth = primaryStage.getWidth();
                        double currentHeight = primaryStage.getHeight();
                        // Minimal resize (1 pixel) - almost invisible to user
                        primaryStage.setWidth(currentWidth + 0.001);
                        primaryStage.setHeight(currentHeight + 0.001);
                        // Immediately resize back to original dimensions
                        Timeline resetTimeline = new Timeline(
                                new KeyFrame(Duration.millis(50), reset -> {
                                    primaryStage.setWidth(currentWidth);
                                    primaryStage.setHeight(currentHeight);
                                }));
                        resetTimeline.play();
                    }));
            timeline.play();
        });
    }

    // ==================================================================================
    // COMPONENT ACCESS METHODS
    // ==================================================================================

    /** @return the main Scene for the simulation interface */
    public Scene getScene() {
        return layout.scene;
    }

    /** @return the simulation space where physics objects are displayed */
    public Pane getSimSpace() {
        return layout.simSpace;
    }

    /** @return the inventory container for draggable objects */
    public StackPane getInventoryBox() {
        return inventory.inventoryBox;
    }

    /** @return the scroll pane containing the inventory items */
    public ScrollPane getInventoryScrollPane() {
        return inventory.inventoryScrollPane;
    }

    /** @return the container holding individual inventory items */
    public VBox getInventoryItemBox() {
        return inventory.inventoryItemBox;
    }

    /** @return the right sidebar containing inventory and controls */
    public VBox getSideBar() {
        return layout.sideBar;
    }
    
    /** @return the bottom bar (only available in sandbox mode) */
    public HBox getBottomBar() {
        return layout.bottomBar;
    }

    /** @return the JSON viewer text area (only available in sandbox mode) */
    public TextArea getJsonViewer() {
        return layout.jsonViewer;
    }

    /** @return the JSON viewer scroll pane (only available in sandbox mode) */
    public ScrollPane getJsonScrollPane() {
        return null; // No longer using ScrollPane wrapper
    }

    /** @return the settings/pause menu overlay */
    public StackPane getOverlaySettings() {
        return overlays.overlaySettings;
    }

    /** @return the level completion overlay */
    public StackPane getWinScreenOverlay() {
        return overlays.winScreenOverlay;
    }

    /** @return the root container that layers all components */
    public StackPane getRootStack() {
        return layout.rootStack;
    }

    // ==================================================================================
    // BUTTON ACCESS METHODS
    // ==================================================================================

    /** @return the group containing simulation control buttons */
    public SimulationButtons getSimulationButtons() {
        return simulationButtons;
    }

    /** @return the group containing overlay menu buttons */
    public OverlayButtons getOverlayButtons() {
        return overlayButtons;
    }

    /** @return the group containing win screen buttons */
    public WinScreenButtons getWinScreenButtons() {
        return winScreenButtons;
    }
}
