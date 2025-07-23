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
 * Extracted from SimulationController to improve separation of concerns.
 */
public class ButtonManager {
    private final SimulationModel model;
    private final SimulationView view;
    private final Stage primaryStage;
    private final double originalWidth;
    private final double originalHeight;
    private final Map<GameObject, PhysicsVisualPair> gameObjectToPairMap;
    private final InventoryManager inventoryManager;
    private final Runnable updateJsonViewerCallback;
    private final Runnable setupSimulationCallback;
    private String selectedSkin;

    /**
     * Constructs a ButtonManager to handle all button-related functionality.
     *
     * @param model The simulation model
     * @param view The simulation view
     * @param primaryStage The primary stage
     * @param originalWidth Original window width
     * @param originalHeight Original window height
     * @param gameObjectToPairMap Map of game objects to physics-visual pairs
     * @param inventoryManager The inventory manager
     * @param updateJsonViewerCallback Callback to update JSON viewer
     * @param setupSimulationCallback Callback to setup simulation
     * @param atPuzzlesEnd Whether at the end of puzzles
     * @param selectedSkin The selected skin theme
     */
    public ButtonManager(
            SimulationModel model,
            SimulationView view,
            Stage primaryStage,
            double originalWidth,
            double originalHeight,
            Map<GameObject, PhysicsVisualPair> gameObjectToPairMap,
            InventoryManager inventoryManager,
            Runnable updateJsonViewerCallback,
            Runnable setupSimulationCallback,
            boolean atPuzzlesEnd,
            String selectedSkin) {
        
        this.model = model;
        this.view = view;
        this.primaryStage = primaryStage;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.gameObjectToPairMap = gameObjectToPairMap;
        this.inventoryManager = inventoryManager;
        this.updateJsonViewerCallback = updateJsonViewerCallback;
        this.setupSimulationCallback = setupSimulationCallback;
        this.selectedSkin = selectedSkin;
    }

    /**
     * Sets up all menu buttons and their event handlers.
     */
    public void setupAllButtons() {
        SimulationView.SimulationButtons simButtons = view.getSimulationButtons();

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
     */
    private void setupPlayButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.playButton != null) {
            simButtons.playButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    timer.start();
                    inventoryManager.setInventoryItemsDisabled(true);
                }
            });
        }
    }

    /**
     * Sets up the stop button action.
     */
    private void setupStopButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.stopButton != null) {
            simButtons.stopButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                    timer.reset();

                    // Clear undo/redo history when stopping simulation to prevent inconsistent state
                    model.getUndoRedoManager().clear();

                    // Reset simulation to state before play was pressed
                    model.setDroppedObjects(model.getDroppedObjects());
                    model.setDroppedVisualPairs(model.getDroppedPhysicsVisualPairs());
                    gameObjectToPairMap.clear();
                    inventoryManager.setInventoryItemsDisabled(false);
                    setupSimulationCallback.run();
                    inventoryManager.refreshInventoryDisplay();
                }
            });
        }
    }

    /**
     * Sets up the settings button action.
     */
    private void setupSettingsButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.settingsButton != null) {
            simButtons.settingsButton.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                view.getOverlaySettings().setVisible(true);
            });
        }
    }

    /**
     * Sets up the undo and redo button actions.
     */
    private void setupUndoRedoButtons(SimulationView.SimulationButtons simButtons) {
        if (simButtons.undoButton != null) {
            simButtons.undoButton.setOnAction(e -> {
                if (isInteractionAllowed()) {
                    model.getUndoRedoManager().undo();
                    updateJsonViewerCallback.run();
                }
            });
        }

        if (simButtons.redoButton != null) {
            simButtons.redoButton.setOnAction(e -> {
                if (isInteractionAllowed()) {
                    model.getUndoRedoManager().redo();
                    updateJsonViewerCallback.run();
                }
            });
        }
    }

    /**
     * Sets up the delete button action.
     */
    private void setupDeleteButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.deleteButton != null) {
            simButtons.deleteButton.setOnAction(e -> {
                // Clear undo/redo history when deleting all objects
                model.getUndoRedoManager().clear();

                // Restore inventory counts before clearing objects
                model.restoreInventoryCounts();

                model.setDroppedObjects(new ArrayList<>());
                model.setDroppedVisualPairs(new ArrayList<>());
                gameObjectToPairMap.clear();
                inventoryManager.setInventoryItemsDisabled(false);
                setupSimulationCallback.run();
                inventoryManager.refreshInventoryDisplay();
                updateJsonViewerCallback.run(); // Update JSON viewer after deleting all
            });
        }
    }

    /**
     * Sets up the import and save button actions.
     */
    private void setupFileButtons(SimulationView.SimulationButtons simButtons) {
        PhysicsAnimationController timer = model.getTimer();
        if (simButtons.importButton != null) {
            simButtons.importButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Import your level!");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    // Clear everything before importing new level
                    clearSimulationForImport();
                    
                    // Set new level path
                    model.setLevelPath("/level/" + file.getName());
                    
                    // Setup simulation with imported level
                    setupSimulationCallback.run();
                    inventoryManager.setupInventory(true); // Reload data when importing new level
                    updateJsonViewerCallback.run(); // Update JSON viewer after import
                }
            });
        }

        if (simButtons.saveButton != null) {
            simButtons.saveButton.setOnAction(e -> {
                if (timer != null && !timer.isRunning()) {
                    model.exportLevel();
                }
                setupSimulationCallback.run();
                inventoryManager.setupInventory(true);
                updateJsonViewerCallback.run();
            });
        }
    }

    /**
     * Clears all simulation state before importing a new level.
     * This ensures that only objects from the imported level are present.
     */
    private void clearSimulationForImport() {
        // Clear undo/redo history when importing new level
        model.getUndoRedoManager().clear();
        
        // Restore inventory counts before clearing objects
        model.restoreInventoryCounts();
        
        // Clear all dropped objects
        model.setDroppedObjects(new ArrayList<>());
        model.setDroppedVisualPairs(new ArrayList<>());
        gameObjectToPairMap.clear();
    }

    /**
     * Sets up the crown button action.
     */
    private void setupCrownButton(SimulationView.SimulationButtons simButtons) {
        if (simButtons.crownButton != null) {
            simButtons.crownButton.setOnAction(e -> {
                view.getWinScreenOverlay().setVisible(true);
            });
        }
    }

    /**
     * Sets up the ESC key to toggle the overlay menu.
     */
    private void setupOverlayToggle() {
        Scene scene = view.getScene();
        StackPane overlaySettings = view.getOverlaySettings();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE && !model.isWinScreenVisible()) {
                overlaySettings.setVisible(!overlaySettings.isVisible());
                event.consume();
            }
        });

        // Get button groups from the refactored view
        SimulationView.OverlayButtons overlayButtons = view.getOverlayButtons();
        SimulationView.WinScreenButtons winButtons = view.getWinScreenButtons();

        overlayButtons.overlayCloseButton.setOnAction(e -> {
            view.getOverlaySettings().setVisible(false);
        });

        overlayButtons.overlayQuitButton.setOnAction(e -> Platform.exit());

        overlayButtons.overlayBackButton.setOnAction(e -> {
            view.getOverlaySettings().setVisible(false);
            
            // Create new title screen
            TitleScreenController titleScreenController = new TitleScreenController(primaryStage);
            primaryStage.setScene(titleScreenController.getScene());
            
            // Restore original window dimensions
            primaryStage.setWidth(originalWidth);
            primaryStage.setHeight(originalHeight);
        });

        if (winButtons.btnWinExport != null) {
            winButtons.btnWinExport.setOnAction(e -> {
                PhysicsAnimationController timer = model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    model.exportLevel();
                }
            });
        }
    }

    /**
     * Sets up the next level button on the win screen.
     */
    private void setupWinNextLevel() {
        int currentLevel = extractLevelNumber(model.getLevelPath());
        String nextLevel = "1";
        final boolean[] nextLevelAtPuzzlesEnd = {false};
        
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
        if (view.getWinScreenButtons().btnWinNext != null) {
            view.getWinScreenButtons().btnWinNext.setOnAction(e -> {
                SimulationController simController = new SimulationController(
                        new SimulationController.SimulationControllerParams.Builder()
                            .setPrimaryStage(primaryStage)
                            .setLevelPath(nextLevelPath)
                            .setPuzzleMode(true)
                            .setAtPuzzlesEnd(nextLevelAtPuzzlesEnd[0])
                            .setSelectedSkin(selectedSkin)
                            .build()
                );
                Scene simScene = simController.getScene();
                primaryStage.setScene(simScene);
            });
        }
    }
    
    /**
     * Extracts the level number from the level path.
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
     */
    private boolean isInteractionAllowed() {
        PhysicsAnimationController timer = model.getTimer();
        return timer == null || !timer.isRunning();
    }
}