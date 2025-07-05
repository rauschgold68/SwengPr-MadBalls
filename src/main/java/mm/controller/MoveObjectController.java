package mm.controller;

import mm.model.GameObject;
import mm.model.PhysicsVisualPair;
import mm.model.Position;

/**
 * Command for moving an object in the simulation.
 * Stores the old and new positions for undo/redo.
 */
public class MoveObjectController implements Command {
    private final GameObject gameObject;
    private final PhysicsVisualPair pair;
    private final Position oldPosition;
    private final Position newPosition;
    private final float oldAngle;
    private final float newAngle;
    
    /**
     * Parameter object containing all move operation details.
     */
    public static class MoveObjectParams {
        public final GameObject gameObject;
        public final PhysicsVisualPair pair;
        public final Position oldPosition;
        public final Position newPosition;
        public final float oldAngle;
        public final float newAngle;
        
        /**
         * Private constructor used by the Builder pattern.
         * Creates a new MoveObjectParams with values from the builder.
         * 
         * @param builder The builder containing all parameter values
         */
        private MoveObjectParams(Builder builder) {
            this.gameObject = builder.gameObject;
            this.pair = builder.pair;
            this.oldPosition = builder.oldPosition;
            this.newPosition = builder.newPosition;
            this.oldAngle = builder.oldAngle;
            this.newAngle = builder.newAngle;
        }
        
        /**
         * Builder class for constructing MoveObjectParams instances.
         * Provides a fluent interface to set parameters individually.
         */
        public static class Builder {
            private GameObject gameObject;
            private PhysicsVisualPair pair;
            private Position oldPosition;
            private Position newPosition;
            private float oldAngle;
            private float newAngle;
            
            /**
             * Sets the game object to be moved.
             * 
             * @param gameObject The game object to be moved
             * @return This builder instance for method chaining
             */
            public Builder setGameObject(GameObject gameObject) {
                this.gameObject = gameObject;
                return this;
            }
            
            /**
             * Sets the physics-visual pair containing both physics body and visual representation.
             * 
             * @param pair The physics-visual pair to be moved
             * @return This builder instance for method chaining
             */
            public Builder setPair(PhysicsVisualPair pair) {
                this.pair = pair;
                return this;
            }
            
            /**
             * Sets the old and new positions for the move operation.
             * 
             * @param oldPosition The original position before the move
             * @param newPosition The target position after the move
             * @return This builder instance for method chaining
             */
            public Builder setPositions(Position oldPosition, Position newPosition) {
                this.oldPosition = oldPosition;
                this.newPosition = newPosition;
                return this;
            }
            
            /**
             * Sets the old and new rotation angles for the move operation.
             * 
             * @param oldAngle The original rotation angle in degrees before the move
             * @param newAngle The target rotation angle in degrees after the move
             * @return This builder instance for method chaining
             */
            public Builder setAngles(float oldAngle, float newAngle) {
                this.oldAngle = oldAngle;
                this.newAngle = newAngle;
                return this;
            }
            
            /**
             * Builds and returns a new MoveObjectParams instance with the configured values.
             * 
             * @return A new MoveObjectParams instance
             */
            public MoveObjectParams build() {
                return new MoveObjectParams(this);
            }
        }
    }
    
    /**
     * Constructs a new MoveObjectController command.
     * Creates defensive copies of position objects to prevent external modification.
     * 
     * @param params The parameter object containing move operation details
     */
    public MoveObjectController(MoveObjectParams params) {
        this.gameObject = params.gameObject;
        this.pair = params.pair;
        this.oldPosition = new Position(params.oldPosition.getX(), params.oldPosition.getY());
        this.newPosition = new Position(params.newPosition.getX(), params.newPosition.getY());
        this.oldAngle = params.oldAngle;
        this.newAngle = params.newAngle;
    }
    
    /**
     * Executes the move operation by applying the new position and angle.
     */
    @Override
    public void execute() {
        updateObjectTransform(newPosition, newAngle);
    }
    
    /**
     * Undoes the move operation by restoring the old position and angle.
     */
    @Override
    public void undo() {
        updateObjectTransform(oldPosition, oldAngle);
    }
    
    /**
     * Updates the object's transform (position and rotation) across all representations.
     * Updates the GameObject, visual representation, and physics body.
     * 
     * @param position The target position to apply
     * @param angle The target rotation angle in degrees to apply
     */
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
            // For rectangles, calculate the center position for physics body
            javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) pair.visual;
            float centerX = position.getX() + (float) rect.getWidth() / 2;
            float centerY = position.getY() + (float) rect.getHeight() / 2;
            pair.body.setTransform(
                new org.jbox2d.common.Vec2(centerX / 50.0f, centerY / 50.0f),
                (float) Math.toRadians(angle)
            );
        } else if (pair.visual instanceof javafx.scene.shape.Circle) {
            // For circles, use the position directly as it represents the center
            pair.body.setTransform(
                new org.jbox2d.common.Vec2(position.getX() / 50.0f, position.getY() / 50.0f),
                (float) Math.toRadians(angle)
            );
        }
    }
    
    /**
     * Returns a description of this command for display purposes.
     * 
     * @return A descriptive string for this move operation
     */
    @Override
    public String getDescription() {
        return "Move " + gameObject.getName();
    }
}