package mm.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
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

    private static final float SCALE = 50.0f;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        // Create visual shapes
        Rectangle rect = new Rectangle(50, 50, Color.CORNFLOWERBLUE);
        rect.setTranslateX(300);
        rect.setTranslateY(100);

        Circle circ = new Circle(20, Color.STEELBLUE);
        circ.setTranslateX(400);
        circ.setTranslateY(100);

        Rectangle collisionRec1 = new Rectangle(100, 25);
        collisionRec1.setTranslateX(250);
        collisionRec1.setTranslateY(300);
        collisionRec1.setRotate(25);

        Rectangle collisionRec2 = new Rectangle(100, 25);
        collisionRec2.setTranslateX(350);
        collisionRec2.setTranslateY(400);
        collisionRec2.setRotate(165);

        root.getChildren().setAll(rect, circ, collisionRec1, collisionRec2);

        // Physics world
        World world = new World(new Vec2(0.0f, 9.8f));

        // Dynamic rectangle
        BodyDef rectDef = new BodyDef();
        rectDef.type = BodyType.DYNAMIC;
        rectDef.position.set(
            (float)(rect.getTranslateX() + rect.getWidth() / 2) / SCALE,
            (float)(rect.getTranslateY() + rect.getHeight() / 2) / SCALE
        );
        Body rectangle = world.createBody(rectDef);

        PolygonShape rectBox = new PolygonShape();
        rectBox.setAsBox((float)rect.getWidth() / 2 / SCALE, (float)rect.getHeight() / 2 / SCALE);

        FixtureDef rectFixture = new FixtureDef();
        rectFixture.shape = rectBox;
        rectFixture.density = 1.0f;
        rectFixture.friction = 0.3f;
        rectFixture.restitution = 0.3f;
        rectangle.createFixture(rectFixture);

        // Dynamic circle
        BodyDef circDef = new BodyDef();
        circDef.type = BodyType.DYNAMIC;
        circDef.position.set(
            (float)circ.getTranslateX() / SCALE,
            (float)circ.getTranslateY() / SCALE
        );
        Body circle = world.createBody(circDef);

        CircleShape circShape = new CircleShape();
        circShape.setRadius((float)circ.getRadius() / SCALE);

        FixtureDef circFixture = new FixtureDef();
        circFixture.shape = circShape;
        circFixture.density = 1.0f;
        circFixture.friction = 0.1f;
        circFixture.restitution = 0.4f;
        circle.createFixture(circFixture);

        // Static collision box 1
        BodyDef collision1Def = new BodyDef();
        collision1Def.type = BodyType.STATIC;
        collision1Def.angle = (float) Math.toRadians(collisionRec1.getRotate());
        collision1Def.position.set(
            (float)(collisionRec1.getTranslateX() + collisionRec1.getWidth() / 2) / SCALE,
            (float)(collisionRec1.getTranslateY() + collisionRec1.getHeight() / 2) / SCALE
        );
        Body collisionBody1 = world.createBody(collision1Def);

        PolygonShape collisionShape1 = new PolygonShape();
        collisionShape1.setAsBox(
            (float)collisionRec1.getWidth() / 2 / SCALE,
            (float)collisionRec1.getHeight() / 2 / SCALE
        );
        collisionBody1.createFixture(collisionShape1, 0.0f);

        // Static collision box 2
        BodyDef collision2Def = new BodyDef();
        collision2Def.type = BodyType.STATIC;
        collision2Def.angle = (float) Math.toRadians(collisionRec2.getRotate());
        collision2Def.position.set(
            (float)(collisionRec2.getTranslateX() + collisionRec2.getWidth() / 2) / SCALE,
            (float)(collisionRec2.getTranslateY() + collisionRec2.getHeight() / 2) / SCALE
        );
        Body collisionBody2 = world.createBody(collision2Def);

        PolygonShape collisionShape2 = new PolygonShape();
        collisionShape2.setAsBox(
            (float)collisionRec2.getWidth() / 2 / SCALE,
            (float)collisionRec2.getHeight() / 2 / SCALE
        );
        collisionBody2.createFixture(collisionShape2, 0.0f);

        // Ground
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

        // Left wall
        BodyDef leftWall = new BodyDef();
        leftWall.type = BodyType.STATIC;
        leftWall.position.set(0 / SCALE, 0 / SCALE);
        Body leftWallBody = world.createBody(leftWall);

        PolygonShape leftW = new PolygonShape();
        leftW.setAsBox(10 / SCALE, 600 / SCALE);
        leftWallBody.createFixture(leftW, 0.0f);

        Rectangle leftRec = new Rectangle(10, 600, Color.BLACK);
        leftRec.setTranslateX(0);
        leftRec.setTranslateY(0);
        root.getChildren().add(leftRec);

        // Right wall
        BodyDef rightWall = new BodyDef();
        rightWall.type = BodyType.STATIC;
        rightWall.position.set(800 / SCALE, 0 / SCALE); // Position am rechten Rand des Fensters (800px breit)
        Body rightWallBody = world.createBody(rightWall);

        PolygonShape rightW = new PolygonShape();
        rightW.setAsBox(10 / SCALE, 600 / SCALE); // Gleiche Größe wie die linke Wand
        rightWallBody.createFixture(rightW, 0.0f);

        Rectangle rightRec = new Rectangle(10, 600, Color.BLACK);
        rightRec.setTranslateX(790); // 800 - 10, damit rechtsbündig
        rightRec.setTranslateY(0);
        root.getChildren().add(rightRec);

        // Triangle obstacle
        BodyDef triangleDef = new BodyDef();
        triangleDef.type = BodyType.STATIC;
        triangleDef.position.set(500 / SCALE, 530 / SCALE);
        Body triangleBody = world.createBody(triangleDef);

        PolygonShape triangleShape = new PolygonShape();
        Vec2[] triangleVertices = {
            new Vec2(-2.0f, 1.0f),
            new Vec2(2.0f, 1.0f),
            new Vec2(0.0f, -1.5f)
        };
        triangleShape.set(triangleVertices, 3);
        triangleBody.createFixture(triangleShape, 0.0f);

        Polygon triangleVisual = new Polygon(
            500.0 - 2 * SCALE, 530.0 + SCALE,
            500.0 + 2 * SCALE, 530.0 + SCALE,
            500.0, 530.0 - 1.5 * SCALE
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

                Vec2 rectPos = rectangle.getPosition();
                rect.setTranslateX(rectPos.x * SCALE - rect.getWidth() / 2);
                rect.setTranslateY(rectPos.y * SCALE - rect.getHeight() / 2);
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
        btnStart.setOnAction(e -> timer.start());
        root.getChildren().add(btnStart);

        Button btnStop = new Button("Stop Simulation");
        btnStop.setTranslateX(120);
        btnStop.setTranslateY(10);
        btnStop.setOnAction(e -> timer.stop());
        root.getChildren().add(btnStop);

        primaryStage.setTitle("JavaFX + JBox2D Physics Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
