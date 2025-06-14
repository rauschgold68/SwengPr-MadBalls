package mm.controller;

import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mm.PhysicsVisualPair;
import mm.core.physics.ResettableAnimationTimer;
import mm.model.SimulationModel;
import mm.model.objects.GameObject;
import mm.model.objects.InventoryObject;
import mm.model.objects.Position;
import mm.view.SimulationView;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code SimulationController} class coordinates the interaction between the {@link SimulationModel} and {@link SimulationView}
 * in the MadBalls game, following the MVC (Model-View-Controller) pattern.
 * <p>
 * This controller is responsible for:
 * <ul>
 *   <li>Initializing and updating the simulation area and inventory UI based on the model state.</li>
 *   <li>Handling all user input and UI events, such as drag-and-drop, keyboard shortcuts, and menu actions.</li>
 *   <li>Updating the model in response to user actions and ensuring the view reflects the current simulation state.</li>
 *   <li>Managing the wiring of event handlers for UI components, including overlay toggling and inventory interactions.</li>
 * </ul>
 * <b>Note:</b> All business logic and event handling should be managed here. The model contains only simulation state and logic,
 * and the view contains only UI construction and accessors.
 * </p>
 *
 * <h3>Main Responsibilities:</h3>
 * <ul>
 *   <li>Setup and refresh the simulation area and inventory UI.</li>
 *   <li>Enable drag-and-drop of inventory objects into the simulation area.</li>
 *   <li>Wire up menu and overlay actions (e.g., play, stop, save, settings).</li>
 *   <li>Toggle overlays and handle keyboard shortcuts.</li>
 * </ul>
 *
 * <h3>Fields:</h3>
 * <ul>
 *   <li>{@code model} - The simulation model holding all simulation state and logic.</li>
 *   <li>{@code view} - The simulation view containing all JavaFX UI components.</li>
 *   <li>{@code inventoryWrappers} - List of StackPane wrappers for inventory visuals, used for drag-and-drop.</li>
 * </ul>
 *
 * @author MadBalls
 */
public class SimulationController {

    private final SimulationModel model;
    private final SimulationView view;
    private final List<StackPane> inventoryWrappers = new ArrayList<>();

    /**
     * Constructs the SimulationController, sets up the model and view, and wires up event handlers.
     *
     * @param primaryStage the primary stage of the application
     * @param levelPath the resource path to the level JSON file
     */
    public SimulationController(Stage primaryStage, String levelPath) {
        this.model = new SimulationModel(levelPath);
        this.view = new SimulationView(primaryStage);

        setupSimulation();
        setupInventory();

        setupDragAndDrop();
        setupMenuButtons();
        setupOverlayToggle();
    }

    /**
     * Returns the simulation scene to be set on the primary stage.
     * @return the JavaFX Scene for the simulation
     */
    public Scene getScene() {
        return view.getScene();
    }

    /**
     * Initializes the simulation area by loading objects and setting up the physics world.
     * <p>
     * Clears the simulation area, loads all objects from the model, and adds their visuals to the view.
     * </p>
     */
    private void setupSimulation() {
        Pane simSpace = view.getSimSpace();
        simSpace.getChildren().clear();

        model.setupSimulation();

        // Add visuals to simSpace
        for (PhysicsVisualPair pair : model.getPairs()) {
            if (pair.visual != null) {
                simSpace.getChildren().add(pair.visual);
            }
        }
    }

    /**
     * Initializes the inventory area by loading inventory objects and setting up drag-and-drop.
     * <p>
     * Clears the inventory UI, loads inventory objects from the model, and creates drag sources for each.
     * </p>
     */
    private void setupInventory() {
        VBox inventoryItemBox = view.getInventoryItemBox();
        inventoryItemBox.getChildren().clear();
        inventoryWrappers.clear();

        model.setupInventory();

        for (InventoryObject obj : model.getInventoryObjects()) {
            PhysicsVisualPair pair = mm.InventoryObjectConverter.convert(obj, model.getWorld());
            if (pair.visual != null) {
                StackPane wrapper = new StackPane(pair.visual);
                wrapper.setPrefSize(60, 60);
                inventoryWrappers.add(wrapper);

                wrapper.setOnDragDetected(event -> {
                    ResettableAnimationTimer timer = model.getTimer();
                    if (timer != null && timer.isRunning()) {
                        event.consume();
                        return;
                    }
                    Dragboard db = wrapper.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(obj.getName());
                    db.setContent(content);

                    javafx.scene.image.WritableImage snapshot = pair.visual.snapshot(null, null);
                    db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);

                    event.consume();
                });

                inventoryItemBox.getChildren().add(wrapper);
            }
        }
    }

    /**
     * Sets up drag-and-drop functionality for placing inventory objects into the simulation area.
     * <p>
     * Handles drag-over and drag-dropped events on the simulation area, checks placement restrictions,
     * and updates the model and view with new objects as needed.
     * </p>
     */
    private void setupDragAndDrop() {
        Pane simSpace = view.getSimSpace();

        simSpace.setOnDragOver(event -> {
            ResettableAnimationTimer timer = model.getTimer();
            if ((timer == null || !timer.isRunning()) && event.getGestureSource() != simSpace && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        simSpace.setOnDragDropped(event -> {
            ResettableAnimationTimer timer = model.getTimer();
            if (timer != null && timer.isRunning()) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            double x = event.getX();
            double y = event.getY();

            if (model.isInNoPlaceZone(x, y)) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String name = db.getString();
                InventoryObject template = model.findInventoryObjectByName(name);

                if (template != null) {
                    GameObject simObj = model.createGameObjectFromInventory(template, (float)x, (float)y);
                    model.addDroppedObject(simObj);

                    PhysicsVisualPair pair = mm.GameObjectConverter.convert(simObj, model.getWorld());
                    if (pair.visual != null) {
                        simSpace.getChildren().add(pair.visual);
                        model.getPairs().add(pair);
                    }
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Sets up menu button actions (play, stop, settings, delete, import, save).
     * <p>
     * The actual buttons should be retrieved from the view and wired here.
     * This is a placeholder for actual button wiring, which depends on your view implementation.
     * </p>
     */
    private void setupMenuButtons() {
        // Start simulation.
        if(view.playButton != null){
            view.playButton.setOnAction(e -> {
                ResettableAnimationTimer timer = model.getTimer();
                if (timer != null && !timer.isRunning()) {
                    timer.start();
                }
            });
        }
        //Stop and reset simulation.
        if (view.stopButton != null){
            view.stopButton.setOnAction(e -> {
                ResettableAnimationTimer timer = model.getTimer();
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
            });
        }
        //Open the settings menu.
        if (view.settingsButton != null){
            view.settingsButton.setOnAction(e -> {
                ResettableAnimationTimer timer = model.getTimer();
                timer.stop();
                view.getOverlaySettings().setVisible(true);
            });
        }
        //Import level from .json - File (to implment)
        if (view.importButton != null){
            view.importButton.setOnAction(e -> {
                System.out.println("To be implemented");
            });
        }
        if (view.saveButton != null){
            view.saveButton.setOnAction(e ->{
                ResettableAnimationTimer timer = model.getTimer();
                timer.stop();
                model.exportLevel();
            });
        }
    }

    /**
     * Sets up the ESC key to toggle the overlay menu.
     * <p>
     * Adds a key event handler to the scene to show or hide the overlay settings menu when ESC is pressed.
     * </p>
     */
    private void setupOverlayToggle() {
        Scene scene = view.getScene();
        StackPane overlaySettings = view.getOverlaySettings();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                overlaySettings.setVisible(!overlaySettings.isVisible());
                event.consume();
            }
        });
    }
}
