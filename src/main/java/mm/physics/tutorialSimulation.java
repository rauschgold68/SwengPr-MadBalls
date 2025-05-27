package mm.physics;


import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import javafx.application.Application;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class tutorialSimulation extends Application{
    private GraphicsContext g;
    private static Entity e1, e2;

    private Parent createContent() {
        
        Pane root = new Pane();

        Canvas canvas = new Canvas(600, 600);
        g = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        initPhysics();

        e1 = new Entity(createBox(200,100,40,40));
        e2 = new Entity(createBox(200, 200, 50, 50));

        e1.getBody().setUserData(e1);
        e2.getBody().setUserData(e2);

        render();

        return root;

    }

    

    private void render() {
        g.clearRect(0, 0, 600, 600);
        e1.draw(g);
        e2.draw(g);
    }

    private World world = new World(new Vec2(0,0));
    
    private void initPhysics(){
        world.setContactListener(new ContactListener() {
            
            @Override
            public void beginContact(Contact contact) {


                System.out.println("Kollision erkannt");
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();
        
                if (a instanceof Entity && a == tutorialSimulation.e2) {
                    ((Entity) a).setColor(Color.RED);
                }
                if (b instanceof Entity && b == tutorialSimulation.e2) {
                    ((Entity) b).setColor(Color.RED);
                }
            }

            @Override
            public void endContact(Contact contact) {

                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();
        
                if (a instanceof Entity && a == tutorialSimulation.e2) {
                    ((Entity) a).setColor(Color.BLACK);
                }
                if (b instanceof Entity && b == tutorialSimulation.e2) {
                    ((Entity) b).setColor(Color.BLACK);
                }
            };
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {};
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {};

        });
    }

    private Body createBox(float x, float y, float w, float h){
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(x,y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/2, h/2);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1.0f;
        fd.friction = 0.3f;

        Body body = world.createBody(bd);
        body.createFixture(fd);

        return body;

    }

    public void start(Stage primaryStage) throws Exception {

        

        Scene scene = new Scene(createContent());

        scene.setOnKeyPressed(e -> {
            Vec2 pos = e1.body.getPosition(); // ⬅️ Declare and get current position here
        
            if (e.getCode() == KeyCode.W) {
                e1.body.setTransform(new Vec2(pos.x, pos.y - 5), e1.body.getAngle());
            } else if (e.getCode() == KeyCode.S) {
                e1.body.setTransform(new Vec2(pos.x, pos.y + 5), e1.body.getAngle());
            } else if (e.getCode() == KeyCode.A) {
                e1.body.setTransform(new Vec2(pos.x - 5, pos.y), e1.body.getAngle());
            } else if (e.getCode() == KeyCode.D) {
                e1.body.setTransform(new Vec2(pos.x + 5, pos.y), e1.body.getAngle());
            }
        
            world.step(1 / 60f, 6, 2); // Advance the physics simulation
            render();
        });

        primaryStage.setTitle("Collision");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {launch(args);}
}