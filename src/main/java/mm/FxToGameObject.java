package mm;

import mm.model.objects.*;

import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import org.jbox2d.dynamics.*;


public class FxToGameObject {
    private static int nextname; 
    
    public static GameObject convertBack(PhysicsVisualPair pair) {
        GameObject gameObject = null;

        String name;
        Position position = new Position(0,0);
        Size size = new Size();
        String colour;
        String type;
        Physics physics = new Physics();

        Shape shape = pair.visual;

        Color tmp = (Color) shape.getFill();
        colour = (tmp != null) ? tmp.toString():"BLACK";

        if (shape instanceof Rectangle) {
            type = "Rectangle";
            name = type + Integer.toString(nextname++);
            javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) shape;
            float x = (float) rect.getTranslateX();
            float y = (float) rect.getTranslateY();
            float width = (float) rect.getWidth();
            float height = (float) rect.getHeight();

            position.setX(x);
            position.setY(y);

            size.setHeight(height);
            size.setWidth(width);

        } else if (shape instanceof Circle) {
            type = "Circle";
            name = type + Integer.toString(nextname++);
            javafx.scene.shape.Circle circle = (javafx.scene.shape.Circle) shape;
            float x = (float) circle.getTranslateX();
            float y = (float) circle.getTranslateY();
            float r = (float) circle.getRadius();

            position.setX(x);
            position.setY(y);
            size.setHeight(0);
            size.setWidth(0);
            size.setRadius(r);

        } else {
            throw new IllegalArgumentException("Shape-Typ nicht unterstützt: " + shape.getClass());
        }

        physics.setShape(pair.body.getType().toString());
        Fixture fixture = pair.body.getFixtureList();
        physics.setDensity(fixture.getDensity());
        physics.setRestitution(fixture.getRestitution());
        physics.setFriction(fixture.getFriction());
        

        gameObject = new GameObject(name, type, position, size, colour, physics);
        return gameObject;
    }
}