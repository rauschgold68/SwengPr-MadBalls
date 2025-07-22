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
    private final SimulationModel model;
    private final TextArea jsonViewer;
    private final Label statusLabel;
    private final Runnable onSimulationUpdate;
    
    private boolean isUpdatingFromJson = false;
    private String lastJsonContent = "";
    private Timeline debounceTimeline;
    
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
            if (!isUpdatingFromJson) {
                // Always clear persistent error messages when user types anything
                if (!newValue.equals(lastJsonContent)) {
                    clearPersistentMessages();
                    handleJsonTextChange(newValue);
                }
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
            if (jsonViewer.getStyle().contains("border-color")) {
                setJsonViewerStyle("");
            }
            
            // Clear status message if it's visible and appears to be an error/warning
            if (statusLabel != null && statusLabel.isVisible()) {
                String currentStyle = statusLabel.getStyle();
                boolean isPersistentMessage = currentStyle.contains("darkred") || // error
                                            currentStyle.contains("#b8860b") ||   // warning
                                            currentStyle.contains("red");         // any red styling
                
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
        setJsonViewerStyle("-fx-border-color: orange; -fx-border-width: 2px;");
        showStatusMessage("Validating JSON...", "info");
        
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
                    setJsonViewerStyle("-fx-border-color: green; -fx-border-width: 2px;");
                    showStatusMessage("JSON applied successfully!", "success");
                    // Auto-hide success messages after 2 seconds
                    Timeline successTimeline = new Timeline(new KeyFrame(Duration.millis(2000), reset -> {
                        setJsonViewerStyle("");
                        hideStatusMessage();
                    }));
                    successTimeline.play();
                } else {
                    setJsonViewerStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    showStatusMessage("Failed to apply JSON to simulation", "error");
                    // Keep error messages persistent - no auto-hide
                }
            } else if (!validation.isValid()) {
                setJsonViewerStyle("-fx-border-color: red; -fx-border-width: 2px;");
                showStatusMessage(validation.getMessage(), "error");
                // Keep error messages persistent - no auto-hide
            } else if (validation.isValid() && !isUpdateAllowed()) {
                setJsonViewerStyle("-fx-border-color: yellow; -fx-border-width: 2px;");
                showStatusMessage("Valid JSON - Stop simulation to apply changes", "warning");
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
                setJsonViewerStyle("-fx-border-color: green; -fx-border-width: 2px;");
                showStatusMessage("JSON applied successfully!", "success");
                // Auto-hide success messages after 2 seconds
                Timeline successTimeline = new Timeline(new KeyFrame(Duration.millis(2000), e -> {
                    setJsonViewerStyle("");
                    hideStatusMessage();
                }));
                successTimeline.play();
            } else {
                setJsonViewerStyle("-fx-border-color: red; -fx-border-width: 2px;");
                showStatusMessage("Failed to apply JSON to simulation", "error");
                // Keep error messages persistent - no auto-hide
            }
        } else {
            setJsonViewerStyle("-fx-border-color: red; -fx-border-width: 2px;");
            showStatusMessage(validation.getMessage(), "error");
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
     * Sets visual style on JSON viewer.
     */
    private void setJsonViewerStyle(String style) {
        Platform.runLater(() -> jsonViewer.setStyle(style));
    }
    
    /**
     * Shows status message with appropriate styling.
     */
    private void showStatusMessage(String message, String type) {
        if (statusLabel != null) {
            Platform.runLater(() -> {
                statusLabel.setText(message);
                statusLabel.setVisible(true);
                
                // Set appropriate style based on message type
                String baseStyle = "-fx-padding: 5px; -fx-font-size: 12px; ";
                switch (type) {
                    case "success":
                        statusLabel.setStyle(baseStyle + "-fx-text-fill: green; -fx-background-color: #e8f5e8; -fx-border-color: green; -fx-border-radius: 3px; -fx-background-radius: 3px;");
                        break;
                    case "error":
                        statusLabel.setStyle(baseStyle + "-fx-text-fill: darkred; -fx-background-color: #ffeaea; -fx-border-color: darkred; -fx-border-radius: 3px; -fx-background-radius: 3px;");
                        break;
                    case "warning":
                        statusLabel.setStyle(baseStyle + "-fx-text-fill: #b8860b; -fx-background-color: #fffacd; -fx-border-color: #b8860b; -fx-border-radius: 3px; -fx-background-radius: 3px;");
                        break;
                    case "info":
                    default:
                        statusLabel.setStyle(baseStyle + "-fx-text-fill: #0066cc; -fx-background-color: #e6f3ff; -fx-border-color: #0066cc; -fx-border-radius: 3px; -fx-background-radius: 3px;");
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