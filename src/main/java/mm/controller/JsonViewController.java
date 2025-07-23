package mm.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import mm.model.JsonStateService;
import mm.model.SimulationModel;

/**
 * Controller for managing JSON viewer functionality.
 * Handles bidirectional synchronization between JSON viewer and simulation state.
 */
public class JsonViewController {
    // CSS class constants
    private static final String ERROR_BORDER_STYLE = "json-viewer-error";
    private static final String SUCCESS_BORDER_STYLE = "json-viewer-success";
    private static final String WARNING_BORDER_STYLE = "json-viewer-warning";
    private static final String INFO_BORDER_STYLE = "json-viewer-info";
    
    // Message type constants
    private static final String ERROR_TYPE = "error";
    private static final String SUCCESS_TYPE = "success";
    private static final String WARNING_TYPE = "warning";
    private static final String INFO_TYPE = "info";
    
    private final SimulationModel model;
    private final TextArea jsonViewer;
    private final Label statusLabel;
    private final Runnable onSimulationUpdate;
    
    private boolean isUpdatingFromJson = false;
    private String lastJsonContent = "";
    private Timeline debounceTimeline;
    
    /**
     * Constructs a new JsonViewController with the specified components and callback.
     * 
     * @param model the simulation model to synchronize with
     * @param jsonViewer the text area for JSON editing and display
     * @param statusLabel the label for displaying status messages
     * @param onSimulationUpdate callback to run when simulation is updated from JSON
     */
    public JsonViewController(SimulationModel model, TextArea jsonViewer, Label statusLabel, Runnable onSimulationUpdate) {
        this.model = model;
        this.jsonViewer = jsonViewer;
        this.statusLabel = statusLabel;
        this.onSimulationUpdate = onSimulationUpdate;
        
        setupJsonListener();
        updateJsonViewer(); // Initialize with current state
    }
    
    /**
     * Updates JSON viewer with current simulation state.
     */
    public void updateJsonViewer() {
        if (!isUpdatingFromJson && jsonViewer != null) {
            Platform.runLater(() -> {
                isUpdatingFromJson = true;
                String jsonContent = model.generateCurrentStateJson();
                jsonViewer.setText(jsonContent);
                lastJsonContent = jsonContent;
                isUpdatingFromJson = false;
            });
        }
    }
    
    /**
     * Sets up real-time JSON monitoring for bidirectional updates.
     */
    private void setupJsonListener() {
        // Text change listener for real-time validation and updates
        jsonViewer.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isUpdatingFromJson && !newValue.equals(lastJsonContent)) {
                // Always clear persistent error messages when user types anything
                clearPersistentMessages();
                handleJsonTextChange(newValue);
            }
        });
        
        // Also listen for key presses to immediately clear errors on any keystroke
        jsonViewer.setOnKeyPressed(event -> {
            if (!isUpdatingFromJson && !event.isControlDown()) {
                // Clear errors immediately on any non-control key press
                clearPersistentMessages();
            }
            
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
                forceUpdateFromJson();
                event.consume();
            }
        });
    }
    
    /**
     * Clears persistent error/warning messages when user starts editing.
     */
    private void clearPersistentMessages() {
        // Always clear both visual border and status message when user starts typing
        // This ensures immediate feedback when user begins editing
        Platform.runLater(() -> {
            // Clear the JSON viewer border styling
            if (jsonViewer.getStyleClass().contains(ERROR_BORDER_STYLE) || 
                jsonViewer.getStyleClass().contains(WARNING_BORDER_STYLE)) {
                setJsonViewerStyle("");
            }
            
            // Clear status message if it's visible and appears to be an error/warning
            if (statusLabel != null && statusLabel.isVisible()) {
                boolean isPersistentMessage = statusLabel.getStyleClass().contains("status-message-error") ||
                                            statusLabel.getStyleClass().contains("status-message-warning");
                
                if (isPersistentMessage) {
                    hideStatusMessage();
                }
            }
        });
    }
    
    /**
     * Handles JSON text changes with debouncing and visual feedback.
     */
    private void handleJsonTextChange(String newValue) {
        // Visual feedback for editing
        setJsonViewerStyle(INFO_BORDER_STYLE);
        showStatusMessage("Validating JSON...", INFO_TYPE);
        
        // Cancel previous debounce timer
        if (debounceTimeline != null) {
            debounceTimeline.stop();
        }
        
        // Create new debounce timer
        debounceTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            JsonStateService.ValidationResult validation = model.validateSimulationJson(newValue);
            
            if (validation.isValid() && isUpdateAllowed()) {
                boolean success = updateSimulationFromJson(newValue);
                if (success) {
                    setJsonViewerStyle(SUCCESS_BORDER_STYLE);
                    showStatusMessage("JSON applied successfully!", SUCCESS_TYPE);
                    // Auto-hide success messages after 2 seconds
                    Timeline successTimeline = new Timeline(new KeyFrame(Duration.millis(2000), reset -> {
                        setJsonViewerStyle("");
                        hideStatusMessage();
                    }));
                    successTimeline.play();
                } else {
                    setJsonViewerStyle(ERROR_BORDER_STYLE);
                    showStatusMessage("Failed to apply JSON to simulation", ERROR_TYPE);
                    // Keep error messages persistent - no auto-hide
                }
            } else if (!validation.isValid()) {
                setJsonViewerStyle(ERROR_BORDER_STYLE);
                showStatusMessage(validation.getMessage(), ERROR_TYPE);
                // Keep error messages persistent - no auto-hide
            } else if (validation.isValid() && !isUpdateAllowed()) {
                setJsonViewerStyle(WARNING_BORDER_STYLE);
                showStatusMessage("Valid JSON - Stop simulation to apply changes", WARNING_TYPE);
                // Keep warning messages persistent - no auto-hide
            }
        }));
        
        debounceTimeline.play();
    }
    
    /**
     * Forces immediate update from JSON (triggered by Ctrl+Enter).
     */
    private void forceUpdateFromJson() {
        String jsonContent = jsonViewer.getText();
        JsonStateService.ValidationResult validation = model.validateSimulationJson(jsonContent);
        
        if (validation.isValid()) {
            boolean success = updateSimulationFromJson(jsonContent);
            if (success) {
                setJsonViewerStyle(SUCCESS_BORDER_STYLE);
                showStatusMessage("JSON applied successfully!", SUCCESS_TYPE);
                // Auto-hide success messages after 2 seconds
                Timeline successTimeline = new Timeline(new KeyFrame(Duration.millis(2000), e -> {
                    setJsonViewerStyle("");
                    hideStatusMessage();
                }));
                successTimeline.play();
            } else {
                setJsonViewerStyle(ERROR_BORDER_STYLE);
                showStatusMessage("Failed to apply JSON to simulation", ERROR_TYPE);
                // Keep error messages persistent - no auto-hide
            }
        } else {
            setJsonViewerStyle(ERROR_BORDER_STYLE);
            showStatusMessage(validation.getMessage(), ERROR_TYPE);
            // Keep error messages persistent - no auto-hide
        }
    }
    
    /**
     * Updates simulation from JSON content.
     */
    private boolean updateSimulationFromJson(String jsonContent) {
        if (!isUpdateAllowed()) {
            return false;
        }
        
        if (model.updateFromJson(jsonContent)) {
            Platform.runLater(() -> {
                isUpdatingFromJson = true;
                if (onSimulationUpdate != null) {
                    onSimulationUpdate.run();
                }
                lastJsonContent = jsonContent;
                isUpdatingFromJson = false;
            });
            return true;
        }
        return false;
    }
    
    /**
     * Checks if updates are allowed (simulation not running).
     */
    private boolean isUpdateAllowed() {
        PhysicsAnimationController timer = model.getTimer();
        return timer == null || !timer.isRunning();
    }
    
    /**
     * Sets visual style on JSON viewer using CSS classes.
     * @param cssClass the CSS class to apply, or empty string to clear styling
     */
    private void setJsonViewerStyle(String cssClass) {
        Platform.runLater(() -> {
            // Clear existing style classes
            jsonViewer.getStyleClass().removeAll(ERROR_BORDER_STYLE, SUCCESS_BORDER_STYLE, 
                                                WARNING_BORDER_STYLE, INFO_BORDER_STYLE);
            // Add new style class if not empty
            if (!cssClass.isEmpty()) {
                jsonViewer.getStyleClass().add(cssClass);
            }
        });
    }
    
    /**
     * Shows status message with appropriate styling using CSS classes.
     * @param message the message to display
     * @param type the message type (success, error, warning, info)
     */
    private void showStatusMessage(String message, String type) {
        if (statusLabel != null) {
            Platform.runLater(() -> {
                statusLabel.setText(message);
                statusLabel.setVisible(true);
                
                // Clear existing style classes
                statusLabel.getStyleClass().removeAll("status-message-base", "status-message-success", 
                                                     "status-message-error", "status-message-warning", 
                                                     "status-message-info");
                
                // Add base style class
                statusLabel.getStyleClass().add("status-message-base");
                
                // Add appropriate style class based on message type
                switch (type) {
                    case SUCCESS_TYPE:
                        statusLabel.getStyleClass().add("status-message-success");
                        break;
                    case ERROR_TYPE:
                        statusLabel.getStyleClass().add("status-message-error");
                        break;
                    case WARNING_TYPE:
                        statusLabel.getStyleClass().add("status-message-warning");
                        break;
                    case INFO_TYPE:
                    default:
                        statusLabel.getStyleClass().add("status-message-info");
                        break;
                }
            });
        }
    }
    
    /**
     * Hides the status message.
     */
    private void hideStatusMessage() {
        if (statusLabel != null) {
            Platform.runLater(() -> statusLabel.setVisible(false));
        }
    }
    
    /**
     * Cleanup method to stop timers.
     */
    public void dispose() {
        if (debounceTimeline != null) {
            debounceTimeline.stop();
        }
    }
}