package mm.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mm.model.GameObject;
import mm.model.PhysicsVisualPair;
import mm.model.SimulationModel;
import mm.view.SimulationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages all button setup and event handling for the simulation interface.
 * This class is responsible for configuring all UI buttons and their associated
 * behaviors,
 * including play/stop controls, undo/redo functionality, file operations, and
 * overlay toggles.
 * It follows the separation of concerns principle by extracting UI interaction
 * logic from
 * the main simulation controller.
 */
public class ButtonController {

    /**
     * Contains UI-related components needed by the ButtonManager.
     */
    private final UIComponents uiComponents;

    /**
     * Contains model-related components needed by the ButtonManager.
     */
    private final ModelComponents modelComponents;

    /**
     * Contains callback functions for various operations.
     */
    private final CallbackComponents callbackComponents;

    /**
     * Contains state information for the ButtonManager.
     */
    private final StateComponents stateComponents;

    /**
     * Groups UI-related components.
     */
    public static class UIComponents {
        private final SimulationView view;
        private final Stage primaryStage;
        private final double originalWidth;
        private final double originalHeight;

        /**
         * Constructs UIComponents with the specified values.
         * 
         * @param view           The simulation view
         * @param primaryStage   The primary JavaFX stage
         * @param originalWidth  The original window width
         * @param originalHeight The original window height
         */
        public UIComponents(SimulationView view, Stage primaryStage,
                double originalWidth, double originalHeight) {
            this.view = view;
            this.primaryStage = primaryStage;
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
        }
    }

    /**
     * Groups model-related components.
     */
    public static class ModelComponents {
        private final SimulationModel model;
        private final Map<GameObject, PhysicsVisualPair> gameObjectToPairMap;
        private final InventoryManager inventoryManager;

        /**
         * Constructs ModelComponents with the specified values.
         * 
         * @param model               The simulation model
         * @param gameObjectToPairMap The map of game objects to physics-visual pairs
         * @param inventoryManager    The inventory manager
         */
        public ModelComponents(SimulationModel model,
                Map<GameObject, PhysicsVisualPair> gameObjectToPairMap,
                InventoryManager inventoryManager) {
            this.model = model;
            this.gameObjectToPairMap = gameObjectToPairMap;
            this.inventoryManager = inventoryManager;
        }
    }

    /**
     * Groups callback functions.
     */
    public static class CallbackComponents {
        private final Runnable updateJsonViewerCallback;
        private final Runnable setupSimulationCallback;

        /**
         * Constructs CallbackComponents with the specified values.
         * 
         * @param updateJsonViewerCallback Callback to update the JSON viewer
         * @param setupSimulationCallback  Callback to set up the simulation
         */
        public CallbackComponents(Runnable updateJsonViewerCallback,
                Runnable setupSimulationCallback) {
            this.updateJsonViewerCallback = updateJsonViewerCallback;
            this.setupSimulationCallback = setupSimulationCallback;
        }
    }

    /**
     * Groups state information.
     */
    public static class StateComponents {
        private final String selectedSkin;
        private final boolean atPuzzlesEnd;

        /**
         * Constructs StateComponents with the specified values.
         * 
         * @param selectedSkin The selected skin name
         * @param atPuzzlesEnd Whether player is at puzzles end
         */
        public StateComponents(String selectedSkin, boolean atPuzzlesEnd) {
            this.selectedSkin = selectedSkin;
            this.atPuzzlesEnd = atPuzzlesEnd;
        }
    }

    /**
     * Parameter object for ButtonManager constructor.
     * Uses the parameter object pattern to avoid excessive parameter lists and
     * to make the constructor more readable and maintainable.
     */
    public static class Params {
        private final UIComponents uiComponents;
        private final ModelComponents modelComponents;
        private final CallbackComponents callbackComponents;
        private final StateComponents stateComponents;

        /**
         * Private constructor used by the Builder.
         * 
         * @param builder The builder containing parameter values
         */
        private Params(Builder builder) {
            this.uiComponents = builder.uiComponents;
            this.modelComponents = builder.modelComponents;
            this.callbackComponents = builder.callbackComponents;
            this.stateComponents = builder.stateComponents;
        }

        /**
         * Builder class for constructing Params objects.
         * Provides a fluent API for setting all required parameters for ButtonManager.
         */
        public static class Builder {
            private UIComponents uiComponents;
            private ModelComponents modelComponents;
            private CallbackComponents callbackComponents;
            private StateComponents stateComponents;

            /**
             * Sets UI components.
             * 
             * @param components The UI components
             * @return This builder for method chaining
             */
            public Builder setUIComponents(UIComponents components) {
                this.uiComponents = components;
                return this;
            }

            /**
             * Sets model components.
             * 
             * @param components The model components
             * @return This builder for method chaining
             */
            public Builder setModelComponents(ModelComponents components) {
                this.modelComponents = components;
                return this;
            }

            /**
             * Sets callback components.
             * 
             * @param components The callback components
             * @return This builder for method chaining
             */
            public Builder setCallbackComponents(CallbackComponents components) {
                this.callbackComponents = components;
                return this;
            }

            /**
             * Sets state components.
             * 
             * @param components The state components
             * @return This builder for method chaining
             */
            public Builder setStateComponents(StateComponents components) {
                this.stateComponents = components;
                return this;
            }

            /**
             * Builds and returns a new Params object with the configured values.
             * 
             * @return A new Params instance
             * @throws IllegalStateException If any required parameter is null
             */
            public Params build() {
                if (uiComponents == null || modelComponents == null ||
                        callbackComponents == null || stateComponents == null) {
                    throw new IllegalStateException("Required components must not be null");
                }
                return new Params(this);
            }
        }
    }

    /**
     * Constructs a ButtonManager using the parameter object pattern.
     * Initializes all required components from the provided parameters object.
     *
     * @param params Parameter object containing all needed values
     */
    public ButtonController(Params params) {
        this.uiComponents = params.uiComponents;
        this.modelComponents = params.modelComponents;
        this.callbackComponents = params.callbackComponents;
        this.stateComponents = params.stateComponents;
    }

    /**
     * Sets up all menu buttons and their event handlers.
     * This method initializes all UI button actions by calling individual setup
     * methods.
     */
    public void setupAllButtons() {
        SimulationView.SimulationButtons simButtons = uiComponents.view.getSimulationButtons();

        setupPlayButton(simButtons);
        setupStopButton(simButtons);
        setupSettingsButton(simButtons);
        setupUndoRedoButtons(simButtons);
        setupDeleteButton(simButtons);
        setupFileButtons(simButtons);
        setupCrownButton(simButtons);
        setupOverlayToggle();
        setupWinNextLevel();
    }

    /**
     * Sets up the play button action.
     * Configures the play button to start the physics simulation when clicked.
     * 
     * @param simButtons Container object holding all simulation buttons
     */
    private void setupPlayButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.playButton != null) {
            simButtons.playButton.setOnAction(e -> {
                PhysicsAnimationController timer = modelComponents.model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    timer.start();
                    modelComponents.inventoryManager.setInventoryItemsDisabled(true);
                }
            });
        }
    }

    /**
     * Sets up the stop button action.
     * Configures the stop button to halt the physics simulation, reset to initial
     * state,
     * and re-enable inventory interactions.
     * 
     * @param simButtons Container object holding all simulation buttons
     */
    private void setupStopButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.stopButton != null) {
            simButtons.stopButton.setOnAction(e -> {
                PhysicsAnimationController timer = modelComponents.model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                    timer.reset();

                    // Clear undo/redo history when stopping simulation to prevent inconsistent
                    // state
                    modelComponents.model.getUndoRedoManager().clear();

                    // The PhysicsAnimationController.reset() method already handles:
                    // - Restoring culled objects via Object Culling System
                    // - Clearing visual pairs
                    // - Resetting simulation state
                    modelComponents.gameObjectToPairMap.clear();
                    modelComponents.inventoryManager.setInventoryItemsDisabled(false);
                    callbackComponents.setupSimulationCallback.run();
                    modelComponents.inventoryManager.refreshInventoryDisplay();
                }
            });
        }
    }

    /**
     * Sets up the settings button action.
     * Configures the settings button to pause the simulation and display the
     * settings overlay.
     * 
     * @param simButtons Container object holding all simulation buttons
     */
    private void setupSettingsButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.settingsButton != null) {
            simButtons.settingsButton.setOnAction(e -> {
                PhysicsAnimationController timer = modelComponents.model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                uiComponents.view.getOverlaySettings().setVisible(true);
            });
        }
    }

    /**
     * Sets up the undo and redo button actions.
     * Configures undo/redo functionality when simulation is not running.
     * 
     * @param simButtons Container object holding all simulation buttons
     */
    private void setupUndoRedoButtons(SimulationView.SimulationButtons simButtons) {
        if (simButtons.undoButton != null) {
            simButtons.undoButton.setOnAction(e -> {
                if (isInteractionAllowed()) {
                    modelComponents.model.getUndoRedoManager().undo();
                    callbackComponents.updateJsonViewerCallback.run();
                }
            });
        }

        if (simButtons.redoButton != null) {
            simButtons.redoButton.setOnAction(e -> {
                if (isInteractionAllowed()) {
                    modelComponents.model.getUndoRedoManager().redo();
                    callbackComponents.updateJsonViewerCallback.run();
                }
            });
        }
    }

    /**
     * Sets up the delete button action.
     * Configures the delete button to clear all objects from the simulation,
     * restore inventory counts, and reset the simulation state.
     * 
     * @param simButtons Container object holding all simulation buttons
     */
    private void setupDeleteButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.deleteButton != null) {
            simButtons.deleteButton.setOnAction(e -> {
                // Clear undo/redo history when deleting all objects
                modelComponents.model.getUndoRedoManager().clear();

                // Restore inventory counts before clearing objects
                modelComponents.model.restoreInventoryCounts();

                modelComponents.model.setDroppedObjects(new ArrayList<>());
                modelComponents.model.setDroppedVisualPairs(new ArrayList<>());
                modelComponents.gameObjectToPairMap.clear();
                modelComponents.inventoryManager.setInventoryItemsDisabled(false);
                callbackComponents.setupSimulationCallback.run();
                modelComponents.inventoryManager.refreshInventoryDisplay();
                callbackComponents.updateJsonViewerCallback.run(); // Update JSON viewer after deleting all
            });
        }
    }

    /**
     * Sets up the import and save button actions.
     * Configures file operations for importing levels from JSON files and saving
     * the current level.
     * 
     * @param simButtons Container object holding all simulation buttons
     */
    private void setupFileButtons(SimulationView.SimulationButtons simButtons) {
        PhysicsAnimationController timer = modelComponents.model.getTimer();
        if (simButtons.importButton != null) {
            simButtons.importButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Import your level!");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                File file = fileChooser.showOpenDialog(uiComponents.primaryStage);
                if (file != null) {
                    // Clear everything before importing new level
                    clearSimulationForImport();

                    // Set new level path
                    modelComponents.model.setLevelPath("/level/" + file.getName());

                    // Setup simulation with imported level
                    callbackComponents.setupSimulationCallback.run();
                    modelComponents.inventoryManager.setupInventory(true); // Reload data when importing new level
                    callbackComponents.updateJsonViewerCallback.run(); // Update JSON viewer after import
                }
            });
        }

        if (simButtons.saveButton != null) {
            simButtons.saveButton.setOnAction(e -> {
                if (timer != null && !timer.isRunning()) {
                    modelComponents.model.exportLevel();
                }
                callbackComponents.setupSimulationCallback.run();
                modelComponents.inventoryManager.setupInventory(true);
                callbackComponents.updateJsonViewerCallback.run();
            });
        }
    }

    /**
     * Clears all simulation state before importing a new level.
     * This ensures that only objects from the imported level are present
     * by clearing undo/redo history, restoring inventory counts, and removing all
     * objects.
     */
    private void clearSimulationForImport() {
        // Clear undo/redo history when importing new level
        modelComponents.model.getUndoRedoManager().clear();

        // Restore inventory counts before clearing objects
        modelComponents.model.restoreInventoryCounts();

        // Clear all dropped objects
        modelComponents.model.setDroppedObjects(new ArrayList<>());
        modelComponents.model.setDroppedVisualPairs(new ArrayList<>());
        modelComponents.gameObjectToPairMap.clear();
    }

    /**
     * Sets up the crown button action.
     * Configures the crown button to display the win screen overlay.
     * 
     * @param simButtons Container object holding all simulation buttons
     */
    private void setupCrownButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.crownButton != null) {
            simButtons.crownButton.setOnAction(e -> {
                uiComponents.view.getWinScreenOverlay().setVisible(true);
            });
        }
    }

    /**
     * Sets up the ESC key to toggle the overlay menu.
     * Configures keyboard shortcuts and buttons for overlay visibility control,
     * returning to title screen, and exiting the application.
     */
    private void setupOverlayToggle() {
        Scene scene = uiComponents.view.getScene();
        StackPane overlaySettings = uiComponents.view.getOverlaySettings();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE && !modelComponents.model.isWinScreenVisible()) {
                overlaySettings.setVisible(!overlaySettings.isVisible());
                event.consume();
            }
        });

        // Get button groups from the refactored view
        SimulationView.OverlayButtons overlayButtons = uiComponents.view.getOverlayButtons();
        SimulationView.WinScreenButtons winButtons = uiComponents.view.getWinScreenButtons();

        overlayButtons.overlayCloseButton.setOnAction(e -> {
            uiComponents.view.getOverlaySettings().setVisible(false);
        });

        overlayButtons.overlayQuitButton.setOnAction(e -> Platform.exit());

        overlayButtons.overlayBackButton.setOnAction(e -> {
            uiComponents.view.getOverlaySettings().setVisible(false);

            // Create new title screen
            TitleScreenController titleScreenController = new TitleScreenController(uiComponents.primaryStage);
            uiComponents.primaryStage.setScene(titleScreenController.getScene());

            // Restore original window dimensions
            uiComponents.primaryStage.setWidth(uiComponents.originalWidth);
            uiComponents.primaryStage.setHeight(uiComponents.originalHeight);
        });

        if (winButtons.btnWinExport != null) {
            winButtons.btnWinExport.setOnAction(e -> {
                PhysicsAnimationController timer = modelComponents.model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    modelComponents.model.exportLevel();
                }
            });
        }

        if (winButtons.btnWinHome != null) {
            winButtons.btnWinHome.setOnAction(e -> {
                TitleScreenController titleScreenController = new TitleScreenController(uiComponents.primaryStage);
                uiComponents.primaryStage.setScene(titleScreenController.getScene());

                uiComponents.primaryStage.setWidth(uiComponents.originalWidth);
                uiComponents.primaryStage.setHeight(uiComponents.originalHeight);
            });

        }
    }

    /**
     * Sets up the next level button on the win screen.
     * Determines the next level path based on the current level number
     * and configures the next button to load that level when clicked.
     * The behavior changes depending on whether the player is at the final puzzle.
     */
    private void setupWinNextLevel() {
        int currentLevel = extractLevelNumber(modelComponents.model.getLevelPath());
        String nextLevel = "1";
        final boolean[] nextLevelAtPuzzlesEnd = { false };

        switch (currentLevel) {
            case 1:
                nextLevel = "2";
                nextLevelAtPuzzlesEnd[0] = false;
                break;
            case 2:
                nextLevel = "3";
                nextLevelAtPuzzlesEnd[0] = true;
                break;
            default:
                break;
        }

        String nextLevelPath = "/level/level" + nextLevel + ".json";
        if (uiComponents.view.getWinScreenButtons().btnWinNext != null) {
            uiComponents.view.getWinScreenButtons().btnWinNext.setOnAction(e -> {
                SimulationController simController = new SimulationController(
                        new SimulationController.SimulationControllerParams.Builder()
                                .setPrimaryStage(uiComponents.primaryStage)
                                .setLevelPath(nextLevelPath)
                                .setPuzzleMode(true)
                                .setAtPuzzlesEnd(nextLevelAtPuzzlesEnd[0])
                                .setSelectedSkin(stateComponents.selectedSkin)
                                .build());
                Scene simScene = simController.getScene();
                uiComponents.primaryStage.setScene(simScene);
            });
        }
    }

    /**
     * Extracts the level number from the level path.
     * Uses regular expressions to find the level number in the file name.
     * 
     * @param levelPath The path to the level file
     * @return The extracted level number, or -1 if not found
     */
    private int extractLevelNumber(String levelPath) {
        Matcher matcher = Pattern.compile("level(\\d+)\\.json").matcher(levelPath);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    /**
     * Determines if interaction is allowed based on simulation state.
     * Interactions are only allowed when the simulation is not running.
     * 
     * @return True if interaction is allowed, false otherwise
     */
    private boolean isInteractionAllowed() {
        PhysicsAnimationController timer = modelComponents.model.getTimer();
        return timer == null || !timer.isRunning();
    }
}