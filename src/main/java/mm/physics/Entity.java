package mm.physics;

import org.jbox2d.dynamics.Body;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Entity {

    Body body;
    float w, h;
    Color col = Color.BLACK ;


    public Entity(Body body) {
        this.body = body;
        this.w = 40;
        this.h = 40;
        
    }

    public Body getBody() {
        return body;
    }

    public void setColor(Color col){
        this.col = col;
    }

    public void draw(GraphicsContext g) {
        float x = body.getPosition().x;
        float y = body.getPosition().y;
        g.setFill(col);
        g.fillRect(x - w / 2, y - h / 2, w, h);
    }
}
