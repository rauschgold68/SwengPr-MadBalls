package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test-Klasse für die Physics-Klasse.
 * Testet die Funktionalität der Physics-Klasse inklusive aller Setter/Getter-Methoden
 * für physikalische Eigenschaften.
 */
public class TestPhysics {
    
    /**
     * Testet die grundlegende Funktionalität der Physics-Klasse.
     * Überprüft die Objekterstellung und stellt sicher, dass das Objekt nicht null ist.
     */
    @Test
    public void testPhysics() {
        Physics testPhysics = new Physics();
        assertNotNull(testPhysics);
        assertEquals(Physics.class, testPhysics.getClass());
        float testFloat = 0.123f;
        String testString = "test";
        testPhysics = new Physics(testFloat, testFloat, testFloat, testString);
        testAssertions(testPhysics, testFloat, testFloat, testFloat, testString);
    }

    /**
     * Testet alle Setter- und Getter-Methoden der Physics-Klasse.
     * Überprüft Density, Friction, Restitution und Shape-Eigenschaften.
     */
    @Test
    public void testSetterGetter() {
        float testDensity = 0.123f;
        float testFriction = 456.7f;
        float testRestitution = 8.90f;
        String testShape = "DYNAMIC";
        Physics testPhysics = new Physics();
        testPhysics.setDensity(testDensity);
        testPhysics.setFriction(testFriction);
        testPhysics.setRestitution(testRestitution);
        testPhysics.setShape(testShape);
        testAssertions(testPhysics, testDensity, testFriction, testRestitution, testShape);
    }
    private void testAssertions(Physics testPhysics, float testDensity, float testFriction, float testRestitution, String testShape) {
        assertEquals(testDensity, testPhysics.getDensity(),0.00001);
        assertEquals(testFriction, testPhysics.getFriction(),0.00001);
        assertEquals(testRestitution, testPhysics.getRestitution(),0.00001);
        assertEquals(testShape, testPhysics.getShape());
    }
}