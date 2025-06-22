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
 * <p>
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
 * </p>
 *
 * <h3>Fields:</h3>
 * <ul>
 * <li>{@code view} - The {@link TitleScreenView} instance containing all UI
 * components for the title screen.</li>
 * <li>{@code primaryStage} - The main application window, used for scene
 * switching.</li>
 * </ul>
 *
 * <h3>Main Responsibilities:</h3>
 * <ul>
 * <li>Show overlays for puzzle/level selection and options.</li>
 * <li>Start the sandbox mode by switching to the simulation scene.</li>
 * <li>Quit the application via the Quit button.</li>
 * <li>Close overlays via close buttons or the ESC key.</li>
 * <li>Expose the main menu scene for use by the application entry point.</li>
 * </ul>
 *
 * <h3>Extensibility:</h3>
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
        this.view = new TitleScreenView(primaryStage);
        setupEventHandlers();
    }

    /**
     * Returns the main menu scene to be set on the primary stage.
     * 
     * @return the JavaFX Scene for the title screen
     */
    public Scene getScene() {
        return view.scene;
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
        view.btnPuzzle.setOnAction(e -> view.overlayBackgroundPuzzle.setVisible(true));

        // Start sandbox mode
        view.btnSandbox.setOnAction(e -> {
            SimulationController simController = new SimulationController(primaryStage, "/level/basic_sandbox.json",
                    false);
            Scene simScene = simController.getScene();
            primaryStage.setScene(simScene);
            primaryStage.sizeToScene();
        });

        // Show options overlay
        view.btnOptions.setOnAction(e -> view.overlayBackgroundOptions.setVisible(true));

        // Quit the application
        view.btnQuit.setOnAction(e -> Platform.exit());

        // Close overlays
        view.btnCloseOptions.setOnAction(e -> view.overlayBackgroundOptions.setVisible(false));
        view.btnClosePuzzle.setOnAction(e -> view.overlayBackgroundPuzzle.setVisible(false));

        // Level card click handlers
        view.levelCard1.setOnMouseClicked(e -> {
            SimulationController simController = new SimulationController(primaryStage, "/level/level1.json", true);
            Scene simScene = simController.getScene();
            primaryStage.setScene(simScene);
            primaryStage.sizeToScene();
        });
        view.levelCard2.setOnMouseClicked(e -> {
            SimulationController simController = new SimulationController(primaryStage, "/level/level2.json", true);
            Scene simScene = simController.getScene();
            primaryStage.setScene(simScene);
            primaryStage.sizeToScene();
        });
        view.levelCard3.setOnMouseClicked(e -> {
            SimulationController simController = new SimulationController(primaryStage, "/level/level3.json", true);
            Scene simScene = simController.getScene();
            primaryStage.setScene(simScene);
            primaryStage.sizeToScene();
        });

        // Keyboard shortcut: ESC closes overlays
        view.scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    if (view.overlayBackgroundOptions.isVisible()) {
                        view.overlayBackgroundOptions.setVisible(false);
                    } else if (view.overlayBackgroundPuzzle.isVisible()) {
                        view.overlayBackgroundPuzzle.setVisible(false);
                    }
                    break;
                default:
                    break;
            }
        });
    }
}
