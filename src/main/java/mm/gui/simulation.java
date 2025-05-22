package mm.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class simulation extends Application {

    // Scale: pixels per meter
    private static final float SCALE = 50.0f;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        


        /*
         * Creating the rectangle and circle in JavaFX
         */
        Rectangle rect = new Rectangle(50, 50, Color.CORNFLOWERBLUE);
        rect.setTranslateX(300);
        rect.setTranslateY(100);
        
        Circle circ = new Circle(40);
        circ.setTranslateX(400);
        circ.setTranslateY(100);

        // Display and set the shapes.
        root.getChildren().setAll(rect, circ);

        // Create the physics world with gravity
        World world = new World(new Vec2(0.0f, 9.8f));

        /*
         * Create dynamic body for the rectangle and circle.
         * That way it has a mass, velocity etc. Get measurements from JavaFX shapes.
         */
        BodyDef rectDef = new BodyDef();
        rectDef.type = BodyType.DYNAMIC;
        rectDef.position.set(
            (float)(rect.getTranslateX() + rect.getWidth() / 2) / SCALE,
            (float)(rect.getTranslateY() + rect.getHeight() / 2) / SCALE
        );

        BodyDef circDef = new BodyDef();
        circDef.type = BodyType.DYNAMIC;
        circDef.position.set((float)circ.getTranslateX() / SCALE, (float)circ.getTranslateY() / SCALE);

        Body rectangle = world.createBody(rectDef);
        Body circle = world.createBody(circDef);

        /*
         * Create the shapes for the rectangle and the circle
         * Get measurements from JavaFX shapes.
         */
        PolygonShape rectBox = new PolygonShape();
        rectBox.setAsBox((float)rect.getWidth() / 2 / SCALE, (float)rect.getHeight() / 2 / SCALE);
        CircleShape circShape = new CircleShape();
        circShape.setRadius((float)circ.getRadius() / SCALE);

        /*
         * Assign physics values to shapes.
         */
        FixtureDef rectfixtureDef = new FixtureDef();
        rectfixtureDef.shape = rectBox;
        rectfixtureDef.density = 1.0f;
        rectfixtureDef.friction = 0.3f;
        rectfixtureDef.restitution = 0.3f;
        rectangle.createFixture(rectfixtureDef);

        FixtureDef circFixtureDef = new FixtureDef();
        circFixtureDef.shape = circShape;
        circFixtureDef.density = 1.0f;
        circFixtureDef.friction = 0.1f;
        circFixtureDef.restitution = 0.4f;
        circle.createFixture(circFixtureDef);

        /*
        * Create a static ground and walls
        */        
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyType.STATIC;
        groundDef.position.set(400 / SCALE, 590 / SCALE);
        Body groundBody = world.createBody(groundDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(400 / SCALE, 10 / SCALE);

        groundBody.createFixture(groundBox, 0.0f);

        Rectangle groundRect = new Rectangle(800, 20, Color.BLACK);
        groundRect.setTranslateX(0);
        groundRect.setTranslateY(580);
        root.getChildren().add(groundRect);

        BodyDef leftWall = new BodyDef();
        leftWall.type = BodyType.STATIC;
        leftWall.position.set(0 / SCALE, 0 / SCALE);
        Body leftWallBody = world.createBody(leftWall);

        PolygonShape leftW = new PolygonShape();
        leftW.setAsBox(10 / SCALE, 600 /SCALE);
        leftWallBody.createFixture(leftW, 0.0f);

        Rectangle leftRec = new Rectangle(10, 600, Color.BLACK);
        leftRec.setTranslateY(0);
        leftRec.setTranslateX(0);
        root.getChildren().add(leftRec);


        /*
         * Create a triangle to have a collision.
        */

        BodyDef triangleDef = new BodyDef();
        triangleDef.position.set(500 / SCALE, 530 / SCALE);
        Body trinagleBody = world.createBody(triangleDef);

        PolygonShape triangleShape = new PolygonShape();
        Vec2[] triangleVertices = new Vec2[3];
        triangleVertices[1] = new Vec2(2.0f, 1.0f);
        triangleVertices[2] = new Vec2(0.0f, -1.5f); 
        triangleVertices[0] = new Vec2(-2.0f, 1.0f);  
        triangleShape.set(triangleVertices, 3);

        trinagleBody.createFixture(triangleShape, 0.0f);
        
        // Triangle visual aligned with physics triangle

        Polygon triangleVisual = new Polygon();
        triangleVisual.getPoints().setAll(
            500.0 - 2 * SCALE, 530.0 + SCALE,  // bottom left
            500.0 + 2 * SCALE, 530.0 + SCALE,  // bottom right
            500.0,         530.0 - 1.5 * SCALE // top center
        );
        triangleVisual.setFill(Color.DARKRED);

        root.getChildren().add(triangleVisual);

        // Animation loop
        AnimationTimer timer = new AnimationTimer() {
    private long lastTime = 0;

    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

            float timeStep = (now - lastTime) / 1_000_000_000.0f;
            world.step(timeStep, 8, 3);

            Vec2 pos = rectangle.getPosition();
            rect.setTranslateX(pos.x * SCALE - rect.getWidth() / 2);
            rect.setTranslateY(pos.y * SCALE - rect.getHeight() / 2);
            rect.setRotate(Math.toDegrees(rectangle.getAngle()));

            Vec2 circPos = circle.getPosition();
            circ.setTranslateX(circPos.x * SCALE);
            circ.setTranslateY(circPos.y * SCALE);
            circ.setRotate(Math.toDegrees(circle.getAngle()));

            lastTime = now;
            }
        };

        Button btnStart = new Button("Start Simulation");
        btnStart.setTranslateX(10);
        btnStart.setTranslateY(10);
        btnStart.setOnAction(e -> {
            timer.start();
        });

        root.getChildren().add(btnStart);

        primaryStage.setTitle("JavaFX + JBox2D Physics Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
