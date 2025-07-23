package mm.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mm.view.TitleScreenView;

/**
 * The {@code TitleScreenController} class coordinates the interaction between
 * the {@link TitleScreenView}
 * and the rest of the application for the MadBalls game's main menu (title
 * screen).
 * This controller is responsible for:
 * <ul>
 * <li>Wiring up event handlers for all main menu buttons and overlays.</li>
 * <li>Handling navigation to sandbox mode, puzzle/level selection, options, and
 * quitting the game.</li>
 * <li>Managing overlay visibility and keyboard shortcuts (ESC to close
 * overlays).</li>
 * <li>Delegating scene switching to the primary stage.</li>
 * </ul>
 * <b>Note:</b> All UI construction is handled by {@link TitleScreenView}. This
 * class should not contain any UI layout code.
 *
 * <h2>Fields:</h2>
 * <ul>
 * <li>{@code view} - The {@link TitleScreenView} instance containing all UI
 * components for the title screen.</li>
 * <li>{@code primaryStage} - The main application window, used for scene
 * switching.</li>
 * </ul>
 *
 * <h2>Main Responsibilities:</h2>
 * <ul>
 * <li>Show overlays for puzzle/level selection and options.</li>
 * <li>Start the sandbox mode by switching to the simulation scene.</li>
 * <li>Quit the application via the Quit button.</li>
 * <li>Close overlays via close buttons or the ESC key.</li>
 * <li>Expose the main menu scene for use by the application entry point.</li>
 * </ul>
 *
 * <h2>Extensibility:</h2>
 * <ul>
 * <li>To handle level card clicks, expose the card nodes from the view and wire
 * their handlers here.</li>
 * <li>To add more overlays or menu options, extend the view and wire up new
 * handlers in this controller.</li>
 * </ul>
 *
 * @author MadBalls
 */
public class TitleScreenController {

    private final TitleScreenView view;
    private final Stage primaryStage;

    /**
     * Constructs the controller, sets up the view, and wires up all event handlers.
     *
     * @param primaryStage The main application window.
     */
    public TitleScreenController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.view = new TitleScreenView();
        setupEventHandlers();
    }

    /**
     * Returns the main menu scene to be set on the primary stage.
     * Updates the current simulation controller's skin if it exists.
     * 
     * @return the JavaFX Scene for the title screen
     */
    public Scene getScene() {
        // Update skin in current simulation if it exists
        if (ApplicationController.currentSimulationController != null) {
            ApplicationController.currentSimulationController.updateSkinChoice();
        }
        return view.uiContainers.scene;
    }

    /**
     * Wires up all button and overlay event handlers for the title screen.
     * <p>
     * Handles showing and hiding overlays, navigation to simulation scenes, and
     * quitting the application.
     * Also manages keyboard shortcuts for overlay dismissal.
     * </p>
     */
    private void setupEventHandlers() {
        // Show puzzle/level selection overlay
        view.menuButtons.btnPuzzle.setOnAction(e -> view.uiContainers.overlayBackgroundPuzzle.setVisible(true));

        // Start sandbox mode
        view.menuButtons.btnSandbox.setOnAction(e -> {
            String selectedSkin = SkinManagerController.getInstance().getSelectedSkin();
            SimulationController.SimulationControllerParams params = new SimulationController.SimulationControllerParams.Builder()
                .setPrimaryStage(primaryStage)
                .setLevelPath("/level/basic_sandbox.json")
                .setPuzzleMode(false)
                .setAtPuzzlesEnd(false)
                .setSelectedSkin(selectedSkin)
                .build();
            SimulationController simController = new SimulationController(params);
            ApplicationController.currentSimulationController = simController;
            Scene simScene = simController.getScene();
            primaryStage.setScene(simScene);
        });

        // Show options overlay
        view.menuButtons.btnOptions.setOnAction(e -> view.uiContainers.overlayBackgroundOptions.setVisible(true));

        // Quit the application
        view.menuButtons.btnQuit.setOnAction(e -> Platform.exit());

        // Close overlays
        view.overlayButtons.btnCloseOptions.setOnAction(e -> view.uiContainers.overlayBackgroundOptions.setVisible(false));
        view.overlayButtons.btnClosePuzzle.setOnAction(e -> view.uiContainers.overlayBackgroundPuzzle.setVisible(false));

        // Level card click handlers
        view.levelCards.levelCard1.setOnMouseClicked(e -> {
            String selectedSkin = SkinManagerController.getInstance().getSelectedSkin();
            SimulationController.SimulationControllerParams params = new SimulationController.SimulationControllerParams.Builder()
                .setPrimaryStage(primaryStage)
                .setLevelPath("/level/level1.json")
                .setPuzzleMode(true)
                .setAtPuzzlesEnd(false)
                .setSelectedSkin(selectedSkin)
                .build();
            SimulationController simController = new SimulationController(params);
            ApplicationController.currentSimulationController = simController;
            Scene simScene = simController.getScene();
            primaryStage.setScene(simScene);
        });
        view.levelCards.levelCard2.setOnMouseClicked(e -> {
            String selectedSkin = SkinManagerController.getInstance().getSelectedSkin();
            SimulationController.SimulationControllerParams params = new SimulationController.SimulationControllerParams.Builder()
                .setPrimaryStage(primaryStage)
                .setLevelPath("/level/level2.json")
                .setPuzzleMode(true)
                .setAtPuzzlesEnd(false)
                .setSelectedSkin(selectedSkin)
                .build();
            SimulationController simController = new SimulationController(params);
            ApplicationController.currentSimulationController = simController;
            Scene simScene = simController.getScene();
            primaryStage.setScene(simScene);
        });
        view.levelCards.levelCard3.setOnMouseClicked(e -> {
            String selectedSkin = SkinManagerController.getInstance().getSelectedSkin();
            SimulationController.SimulationControllerParams params = new SimulationController.SimulationControllerParams.Builder()
                .setPrimaryStage(primaryStage)
                .setLevelPath("/level/level3.json")
                .setPuzzleMode(true)
                .setAtPuzzlesEnd(true)
                .setSelectedSkin(selectedSkin)
                .build();
            SimulationController simController = new SimulationController(params);
            ApplicationController.currentSimulationController = simController;
            Scene simScene = simController.getScene();
            primaryStage.setScene(simScene);
        });

        // Keyboard shortcut: ESC closes overlays
        view.uiContainers.scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    if (view.uiContainers.overlayBackgroundOptions.isVisible()) {
                        view.uiContainers.overlayBackgroundOptions.setVisible(false);
                    } else if (view.uiContainers.overlayBackgroundPuzzle.isVisible()) {
                        view.uiContainers.overlayBackgroundPuzzle.setVisible(false);
                    }
                    break;
                default:
                    break;
            }
        });
    }
}
