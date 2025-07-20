package mm.view;

import javafx.scene.shape.Shape;
import mm.model.PhysicsGeometryPair;
import org.jbox2d.dynamics.Body;

/**
 * View-specific wrapper that combines a PhysicsGeometryPair from the model
 * with its corresponding JavaFX Shape for rendering.
 * <p>
 * This class belongs in the view layer and provides the bridge between
 * the model's geometry data and the JavaFX visual representation.
 * Controllers can use this class to maintain the connection between
 * physics simulation and visual rendering.
 * </p>
 */
public class VisualPhysicsPair {
    /** The model's geometry and physics data */
    private final PhysicsGeometryPair modelPair;
    
    /** The JavaFX shape for rendering */
    private final Shape visual;
    
    /**
     * Constructs a VisualPhysicsPair from model data and a JavaFX shape.
     * 
     * @param modelPair the physics and geometry data from the model
     * @param visual the JavaFX shape for rendering
     */
    public VisualPhysicsPair(PhysicsGeometryPair modelPair, Shape visual) {
        this.modelPair = modelPair;
        this.visual = visual;
    }
    
    /**
     * Returns the JavaFX visual shape.
     */
    public Shape getVisual() {
        return visual;
    }
    
    /**
     * Returns the physics body.
     */
    public Body getBody() {
        return modelPair.getBody();
    }
    
    /**
     * Returns the underlying model pair.
     */
    public PhysicsGeometryPair getModelPair() {
        return modelPair;
    }
    
    /**
     * Factory method to create a VisualPhysicsPair from a model pair.
     */
    public static VisualPhysicsPair fromModelPair(PhysicsGeometryPair modelPair) {
        Shape visual = null;
        if (modelPair.getGeometry() != null) {
            visual = ShapeFactory.createShape(modelPair.getGeometry());
        }
        return new VisualPhysicsPair(modelPair, visual);
    }
}
