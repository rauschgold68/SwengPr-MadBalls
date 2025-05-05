package mm.physics;

import org.jbox2d.dynamics.Body;

import javafx.scene.canvas.GraphicsContext;

public class Entity {

    Body body;
    float w, h;


    public Entity(Body body) {
        this.body = body;
        this.w = 40;
        this.h = 40;
    }


    public void draw(GraphicsContext g) {
        float x = body.getPosition().x;
        float y = body.getPosition().y;
        g.fillRect(x - w / 2, y - h / 2, w, h);
    }
}
