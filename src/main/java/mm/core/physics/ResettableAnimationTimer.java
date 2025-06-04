package mm.core.physics;

import javafx.animation.AnimationTimer;
import mm.PhysicsVisualPair;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.List;

public class ResettableAnimationTimer extends AnimationTimer {
    private long lastTime = 0;
    private World world;
    private List<PhysicsVisualPair> pairs;

    public ResettableAnimationTimer(World world, List<PhysicsVisualPair> pairs) {
        this.world = world;
        this.pairs = pairs;
    }

    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        float timeStep = (now - lastTime) / 1_000_000_000.0f;
        world.step(timeStep, 8, 3);
        lastTime = now;

        for (PhysicsVisualPair pair : pairs) {
                    if (pair.visual != null && pair.body != null) {
                        // SCALE must match the one used in GameObjectConverter
                        float SCALE = 50.0f;
                        Vec2 pos = pair.body.getPosition();
                        double angle = Math.toDegrees(pair.body.getAngle());

                        // For rectangles, set center
                        if (pair.visual instanceof javafx.scene.shape.Rectangle) {
                            javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) pair.visual;
                            rect.setTranslateX(pos.x * SCALE - rect.getWidth() / 2);
                            rect.setTranslateY(pos.y * SCALE - rect.getHeight() / 2);
                            rect.setRotate(angle);
                        }
                        // For circles, set center
                        else if (pair.visual instanceof javafx.scene.shape.Circle) {
                            javafx.scene.shape.Circle circ = (javafx.scene.shape.Circle) pair.visual;
                            circ.setTranslateX(pos.x * SCALE);
                            circ.setTranslateY(pos.y * SCALE);
                            circ.setRotate(angle);
                        }
                    }
                }
    }

    public void reset() {
        lastTime = 0;
    }
}