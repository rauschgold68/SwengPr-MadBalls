package mm.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import mm.model.SimulationModel;

/**
 * Controller for managing JSON viewer functionality.
 * Handles bidirectional synchronization between JSON viewer and simulation state.
 */
public class JsonViewController {
    private final SimulationModel model;
    private final TextArea jsonViewer;
    private final Runnable onSimulationUpdate;
    
    private boolean isUpdatingFromJson = false;
    private String lastJsonContent = "";
    private Timeline debounceTimeline;
    
    public JsonViewController(SimulationModel model, TextArea jsonViewer, Runnable onSimulationUpdate) {
        this.model = model;
        this.jsonViewer = jsonViewer;
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
                handleJsonTextChange(newValue);
            }
        });
        
        // Keyboard shortcuts
        jsonViewer.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
                forceUpdateFromJson();
                event.consume();
            }
        });
    }
    
    /**
     * Handles JSON text changes with debouncing and visual feedback.
     */
    private void handleJsonTextChange(String newValue) {
        // Visual feedback for editing
        setJsonViewerStyle("-fx-border-color: orange; -fx-border-width: 2px;");
        
        // Cancel previous debounce timer
        if (debounceTimeline != null) {
            debounceTimeline.stop();
        }
        
        // Create new debounce timer
        debounceTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            boolean isValid = model.isValidSimulationJson(newValue);
            
            if (isValid && isUpdateAllowed()) {
                boolean success = updateSimulationFromJson(newValue);
                setJsonViewerStyle(success ? 
                    "-fx-border-color: green; -fx-border-width: 2px;" : 
                    "-fx-border-color: red; -fx-border-width: 2px;");
            } else {
                setJsonViewerStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }
            
            // Reset border after delay
            Timeline resetTimeline = new Timeline(new KeyFrame(Duration.millis(1000), reset -> {
                setJsonViewerStyle("");
            }));
            resetTimeline.play();
        }));
        
        debounceTimeline.play();
    }
    
    /**
     * Forces immediate update from JSON (triggered by Ctrl+Enter).
     */
    private void forceUpdateFromJson() {
        String jsonContent = jsonViewer.getText();
        boolean success = updateSimulationFromJson(jsonContent);
        
        setJsonViewerStyle(success ? 
            "-fx-border-color: green; -fx-border-width: 2px;" : 
            "-fx-border-color: red; -fx-border-width: 2px;");
        
        // Reset after short delay
        Timeline resetTimeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            setJsonViewerStyle("");
        }));
        resetTimeline.play();
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
     * Cleanup method to stop timers.
     */
    public void dispose() {
        if (debounceTimeline != null) {
            debounceTimeline.stop();
        }
    }
}