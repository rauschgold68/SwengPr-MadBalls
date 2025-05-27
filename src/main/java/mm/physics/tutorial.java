package mm.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.PolygonShape;

public class tutorial {

    public static void main(String[] args) {

        float timeStep = 1.0f / 60.0f;

        int velocityIterations = 6;
        int positionIterations = 2;


        /*
         * 
         * Creating a World and Ground Box
         * World managed Speicher, Objecte und Simulation.
         * Ground Box ist quasi das selbst erstellte Objekt. 
         * 
         */

        // Schritt 1: Welt mit Schwerkraft erstellen.
        Vec2 gravity = new Vec2(0.0f, -10.0f);
        World welt = new World(gravity);

        // Schritt 2: BodyDef für den Boden erstellen.

        // -------------------
        // 1. Boden erstellen
        // -------------------
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0.0f, -10.0f);       // Position des Bodens.
        groundBodyDef.type = BodyType.STATIC;             // Verschiedene Massen, Beschleunigung und Verhalten bei Kontakt.

        // Boden erzeugen.
        Body groundBody = welt.createBody(groundBodyDef);

        // Schritt 3: Polygon erstellen und am Boden andocken.
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(50.0f, 10.0f);
        groundBody.createFixture(groundBox, 0.0f);

        /*
         * 
         * Nächstes Tutorial "Creating a Dynamic Body"
         * Eine Form die auch die Masse mit beachtet.
         * 
         */
        
        // -------------------
        // 2. Dynamischen Körper erstellen
        // -------------------
        BodyDef dybodyDef = new BodyDef();                  // Beschreibt wie sich der Körper verhalten soll.
        dybodyDef.type = BodyType.DYNAMIC;
        dybodyDef.position.set(0.0f, 4.0f);

        Body dynamicBody = welt.createBody(dybodyDef);
        
        PolygonShape dynamicBox = new PolygonShape();       // Beschreibt was für eine Form der Körper hat.
        dynamicBox.setAsBox(1.0f , 1.0f);

        FixtureDef fixuterDef = new FixtureDef();           // Beschreibt wie sich der Körper physikalisch verhält.
        fixuterDef.shape = dynamicBox;
        fixuterDef.density = 1.0f;
        fixuterDef.friction = 0.3f;

        dynamicBody.createFixture(fixuterDef);              // Weißt dem Körper die physikalischen Eigenschaften zu.

        // -------------------
        // 3. Simulation durchführen.
        // -------------------
        for (int i = 0; i < 90; ++i){

            welt.step(timeStep, velocityIterations, positionIterations);        // Welt wird fortgeführt.
            Vec2 position = dynamicBody.getPosition();
            float angle = dynamicBody.getAngle();

            System.out.printf("%4.2f %4.2f %4.2f%n", position.x, position.y, Math.toDegrees(angle));
        }

        while (dynamicBody != null) {                                           // Simulation beedndet, die Körper werden wieder entfernt.
            Body next = dynamicBody.getNext();
            welt.destroyBody(dynamicBody);
            dynamicBody = next;
        }
        welt = null;

    }
}

