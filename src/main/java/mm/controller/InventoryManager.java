package mm.controller;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mm.model.InventoryObject;
import mm.model.SimulationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages inventory-related operations for the simulation.
 * Extracted from SimulationController to improve separation of concerns.
 */
public class InventoryManager {
    private final SimulationModel model;
    private final VBox inventoryItemBox;
    private final List<StackPane> inventoryWrappers = new ArrayList<>();
    private final Runnable onInventoryChangeCallback;

    /**
     * Creates a new inventory manager.
     *
     * @param model The simulation model containing inventory data
     * @param inventoryItemBox The UI container for inventory items
     * @param onInventoryChangeCallback Callback to run when inventory changes
     */
    public InventoryManager(SimulationModel model, VBox inventoryItemBox, Runnable onInventoryChangeCallback) {
        this.model = model;
        this.inventoryItemBox = inventoryItemBox;
        this.onInventoryChangeCallback = onInventoryChangeCallback;
    }

    /**
     * Initializes or refreshes the inventory area.
     *
     * @param reloadData if true, reloads inventory data from JSON file;
     *                   if false, uses existing data with current counts
     */
    public void setupInventory(boolean reloadData) {
        inventoryItemBox.getChildren().clear();
        inventoryWrappers.clear();

        // Only reload data from file if explicitly requested
        if (reloadData) {
            model.setupInvetoryData();
        }

        // Create inventory items
        for (InventoryObject obj : model.getInventoryObjects()) {
            StackPane itemWrapper = createInventoryItemWrapper(obj);
            if (itemWrapper != null) {
                inventoryWrappers.add(itemWrapper);
                setupInventoryItemHandlers(itemWrapper, obj);
                inventoryItemBox.getChildren().add(itemWrapper);
            }
        }

        // Configure inventory layout
        inventoryItemBox.setSpacing(15);
    }

    /**
     * Creates a wrapper for an inventory item with visual styling.
     *
     * @param obj the inventory object to create a wrapper for
     * @return a StackPane wrapper containing the visual representation
     */
    private StackPane createInventoryItemWrapper(InventoryObject obj) {
        Node visual = InventoryObjectController.createPreviewVisual(obj);
        if (visual == null) {
            return null;
        }

        // Dynamically adjust wrapper size based on rotated dimensions
        double rotatedWidth = visual.getBoundsInParent().getWidth();
        double rotatedHeight = visual.getBoundsInParent().getHeight();

        StackPane wrapper = new StackPane(visual);
        wrapper.setPrefSize(rotatedWidth + 20, rotatedHeight + 20);

        Label countLabel = new Label(Integer.toString(obj.getCount()));
        countLabel.setMouseTransparent(true);

        // Apply appropriate CSS class based on count
        if (obj.getCount() <= 0) {
            countLabel.getStyleClass().add("item-count-no");
            wrapper.setStyle("-fx-opacity: 0.5;");
        } else {
            countLabel.getStyleClass().add("item-count-yes");
            wrapper.setStyle("");
        }

        wrapper.getChildren().add(countLabel);
        StackPane.setAlignment(countLabel, javafx.geometry.Pos.CENTER_RIGHT);

        return wrapper;
    }

    /**
     * Sets up event handlers for an inventory item wrapper.
     *
     * @param wrapper the inventory item wrapper
     * @param obj the inventory object associated with this wrapper
     */
    private void setupInventoryItemHandlers(StackPane wrapper, InventoryObject obj) {
        wrapper.setOnDragDetected(event -> {
            // Business logic: check if drag should be allowed
            if (!isDragAllowed(obj)) {
                event.consume();
                return;
            }

            // Delegate drag setup to helper method
            startInventoryItemDrag(wrapper, obj, event);
        });
    }

    /**
     * Determines if an inventory item can be dragged.
     *
     * @param obj the inventory object to check
     * @return true if the item can be dragged, false otherwise
     */
    private boolean isDragAllowed(InventoryObject obj) {
        return isInteractionAllowed() && obj.getCount() > 0;
    }

    /**
     * Determines if interaction is allowed based on simulation state.
     *
     * @return true if interaction is allowed, false otherwise
     */
    private boolean isInteractionAllowed() {
        PhysicsAnimationController timer = model.getTimer();
        return timer == null || !timer.isRunning();
    }

    /**
     * Starts the drag operation for an inventory item.
     */
    private void startInventoryItemDrag(StackPane wrapper, InventoryObject obj, javafx.scene.input.MouseEvent event) {
        javafx.scene.input.Dragboard db = wrapper.startDragAndDrop(javafx.scene.input.TransferMode.COPY);
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(obj.getName());
        db.setContent(content);

        // Create drag image from the visual component
        Node visual = wrapper.getChildren().get(0); // First child should be the visual
        javafx.scene.SnapshotParameters snapshotParameters = new javafx.scene.SnapshotParameters();
        snapshotParameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
        javafx.scene.image.WritableImage snapshot = visual.snapshot(snapshotParameters, null);

        db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
        event.consume();
    }

    /**
     * Refreshes the inventory display without reloading data from file.
     */
    public void refreshInventoryDisplay() {
        setupInventory(false);
        if (onInventoryChangeCallback != null) {
            onInventoryChangeCallback.run();
        }
    }

    /**
     * Sets the visual state of all inventory item wrappers to indicate whether they
     * are enabled or disabled.
     *
     * @param disabled true to visually disable inventory items, false to enable them
     */
    public void setInventoryItemsDisabled(boolean disabled) {
        for (StackPane wrapper : inventoryWrappers) {
            if (disabled) {
                if (!wrapper.getStyleClass().contains("inventory-item-disabled")) {
                    wrapper.getStyleClass().add("inventory-item-disabled");
                }
            } else {
                wrapper.getStyleClass().remove("inventory-item-disabled");
            }
        }
    }

    /**
     * Updates all inventory object sprite paths to use the selected skin folder.
     */
    public void updateInventorySpritesForSkin() {
        SkinManagerController.getInstance().updateInventorySpritesForSkin(model.getInventoryObjects());
    }
}