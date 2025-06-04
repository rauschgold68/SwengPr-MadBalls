package mm;

import javafx.scene.shape.Shape;
import org.jbox2d.dynamics.Body;

public class PhysicsVisualPair {
    public final Shape visual;
    public final Body body;

    public PhysicsVisualPair(Shape visual, Body body){
        this.visual = visual;
        this.body = body;
    }
}
