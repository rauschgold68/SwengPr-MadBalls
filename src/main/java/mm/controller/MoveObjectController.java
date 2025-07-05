package mm.controller;

import mm.model.GameObject;
import mm.model.PhysicsVisualPair;
import mm.model.Position;

/**
 * Command for moving an object in the simulation.
 * Stores the old and new positions for undo/redo.
 */
public class MoveObjectCommand implements Command {
    private final GameObject gameObject;
    private final PhysicsVisualPair pair;
    private final Position oldPosition;
    private final Position newPosition;
    private final float oldAngle;
    private final float newAngle;
    
    public MoveObjectCommand(GameObject gameObject, PhysicsVisualPair pair, 
                           Position oldPosition, Position newPosition,
                           float oldAngle, float newAngle) {
        this.gameObject = gameObject;
        this.pair = pair;
        this.oldPosition = new Position(oldPosition.getX(), oldPosition.getY());
        this.newPosition = new Position(newPosition.getX(), newPosition.getY());
        this.oldAngle = oldAngle;
        this.newAngle = newAngle;
    }
    
    @Override
    public void execute() {
        updateObjectTransform(newPosition, newAngle);
    }
    
    @Override
    public void undo() {
        updateObjectTransform(oldPosition, oldAngle);
    }
    
    private void updateObjectTransform(Position position, float angle) {
        // Update GameObject
        gameObject.getPosition().setX(position.getX());
        gameObject.getPosition().setY(position.getY());
        gameObject.setAngle(angle);
        
        // Update visual
        pair.visual.setTranslateX(position.getX());
        pair.visual.setTranslateY(position.getY());
        pair.visual.setRotate(angle);
        
        // Update physics body
        if (pair.visual instanceof javafx.scene.shape.Rectangle) {
            javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) pair.visual;
            float centerX = position.getX() + (float) rect.getWidth() / 2;
            float centerY = position.getY() + (float) rect.getHeight() / 2;
            pair.body.setTransform(
                new org.jbox2d.common.Vec2(centerX / 50.0f, centerY / 50.0f),
                (float) Math.toRadians(angle)
            );
        } else if (pair.visual instanceof javafx.scene.shape.Circle) {
            pair.body.setTransform(
                new org.jbox2d.common.Vec2(position.getX() / 50.0f, position.getY() / 50.0f),
                (float) Math.toRadians(angle)
            );
        }
    }
    
    @Override
    public String getDescription() {
        return "Move " + gameObject.getName();
    }
}