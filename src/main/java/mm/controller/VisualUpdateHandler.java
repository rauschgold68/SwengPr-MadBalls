package mm.controller;

import org.jbox2d.common.Vec2;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import mm.model.PhysicsVisualPair;

/**
 * Handles visual updates for physics simulation objects.
 * This class is responsible for updating JavaFX visual positions and rotations
 * based on physics simulation data.
 */
public class VisualUpdateHandler {
    private static final float SCALE = 50.0f;
    
    /**
     * Updates the visual position and rotation for a physics-visual pair.
     * 
     * @param pair the physics-visual pair to update
     * @param pos the position from physics simulation
     * @param angle the angle from physics simulation
     */
    public void updateVisualPosition(PhysicsVisualPair pair, Vec2 pos, double angle) {
        if (pair.visual == null) {
            return;
        }
        
        if (pair.visual instanceof javafx.scene.shape.Rectangle) {
            updateRectanglePosition((javafx.scene.shape.Rectangle) pair.visual, pos, angle);
        } else if (pair.visual instanceof javafx.scene.shape.Circle) {
            updateCirclePosition((javafx.scene.shape.Circle) pair.visual, pos, angle);
        } else if (pair.visual instanceof javafx.scene.shape.Polygon) {
            updatePolygonPosition((javafx.scene.shape.Polygon) pair.visual, pos, angle);
        }
    }
    
    /**
     * Updates rectangle position and rotation.
     */
    private void updateRectanglePosition(javafx.scene.shape.Rectangle rect, Vec2 pos, double angle) {
        rect.setTranslateX(pos.x * SCALE - rect.getWidth() / 2);
        rect.setTranslateY(pos.y * SCALE - rect.getHeight() / 2);
        rect.setRotate(angle);
    }
    
    /**
     * Updates circle position and rotation.
     */
    private void updateCirclePosition(javafx.scene.shape.Circle circ, Vec2 pos, double angle) {
        circ.setTranslateX(pos.x * SCALE);
        circ.setTranslateY(pos.y * SCALE);
        circ.setRotate(angle);
    }
    
    /**
     * Updates polygon position and rotation.
     */
    private void updatePolygonPosition(javafx.scene.shape.Polygon polygon, Vec2 pos, double angle) {
        javafx.geometry.Bounds bounds = polygon.getBoundsInLocal();
        polygon.setTranslateX(pos.x * SCALE - bounds.getWidth() / 2);
        polygon.setTranslateY(pos.y * SCALE - bounds.getHeight() / 2);
        polygon.setRotate(angle);
    }
    
    /**
     * Removes a visual node from its parent container.
     * 
     * @param visual the visual node to remove
     */
    public void removeVisualFromParent(javafx.scene.Node visual) {
        Parent parent = visual.getParent();
        if (parent instanceof Group) {
            ((Group) parent).getChildren().remove(visual);
        } else if (parent instanceof Pane) {
            ((Pane) parent).getChildren().remove(visual);
        }
    }
    
    /**
     * Batch removes multiple visuals from their parent containers.
     * 
     * @param visuals the list of visuals to remove
     */
    public void batchRemoveVisuals(java.util.List<javafx.scene.Node> visuals) {
        if (visuals.isEmpty()) {
            return;
        }
        
        Platform.runLater(() -> {
            for (javafx.scene.Node visual : visuals) {
                removeVisualFromParent(visual);
            }
        });
    }
}
