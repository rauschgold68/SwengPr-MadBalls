package mm.model;

import java.util.Map;
import java.util.HashMap;

/**
 * Encapsulates the state variables for the simulation controller.
 * <p>
 * This class serves as a state container for various aspects of the simulation,
 * including window dimensions, drag operations, game object mappings, and visual settings.
 * It was extracted to reduce field count in the main controller class and improve separation
 * of concerns.
 * </p>
 * 
 * @author Your Name
 */
public class SimulationState {
    /** The original width of the simulation window */
    private final double originalWidth;
    
    /** The original height of the simulation window */
    private final double originalHeight;
    
    /** Stores the starting position of an object when a drag operation begins */
    private Position dragStartPosition;
    
    /** Stores the starting angle of an object when a rotation operation begins */
    private float dragStartAngle;
    
    /** 
     * Maps game objects to their corresponding physics-visual pairs.
     * This allows quick lookup of visual and physics components from game objects.
     */
    private final Map<GameObject, PhysicsVisualPair> gameObjectToPairMap = new HashMap<>();
    
    /** The currently selected visual skin/theme for the simulation */
    private String selectedSkin;
    
    /**
     * Constructs a new SimulationState with the specified initial values.
     * 
     * @param width the original width of the simulation window
     * @param height the original height of the simulation window
     * @param skin the initially selected visual skin/theme
     */
    public SimulationState(double width, double height, String skin) {
        this.originalWidth = width;
        this.originalHeight = height;
        this.selectedSkin = skin;
    }
    
    /**
     * Gets the original width of the simulation window.
     * 
     * @return the original width in pixels
     */
    public double getOriginalWidth() { return originalWidth; }
    
    /**
     * Gets the original height of the simulation window.
     * 
     * @return the original height in pixels
     */
    public double getOriginalHeight() { return originalHeight; }
    
    /**
     * Gets the starting position of an object when a drag operation began.
     * 
     * @return the starting position, or null if no drag operation is in progress
     */
    public Position getDragStartPosition() { return dragStartPosition; }
    
    /**
     * Sets the starting position for a drag operation.
     * 
     * @param position the starting position to store
     */
    public void setDragStartPosition(Position position) { this.dragStartPosition = position; }
    
    /**
     * Gets the starting angle of an object when a rotation operation began.
     * 
     * @return the starting angle in degrees
     */
    public float getDragStartAngle() { return dragStartAngle; }
    
    /**
     * Sets the starting angle for a rotation operation.
     * 
     * @param angle the starting angle in degrees
     */
    public void setDragStartAngle(float angle) { this.dragStartAngle = angle; }
    
    /**
     * Gets the mapping between game objects and their physics-visual pairs.
     * 
     * @return an unmodifiable view of the game object to physics-visual pair mapping
     */
    public Map<GameObject, PhysicsVisualPair> getGameObjectToPairMap() { return gameObjectToPairMap; }
    
    /**
     * Gets the currently selected visual skin/theme.
     * 
     * @return the name of the selected skin (e.g., "Default", "Legacy")
     */
    public String getSelectedSkin() { return selectedSkin; }
    
    /**
     * Sets the visual skin/theme for the simulation.
     * 
     * @param skin the name of the skin to set
     */
    public void setSelectedSkin(String skin) { this.selectedSkin = skin; }
}