package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test-Klasse für die Size-Klasse.
 * Testet die Funktionalität der Size-Klasse inklusive aller Dimensionseigenschaften
 * wie Width, Height und Radius.
 */
public class TestSize {
    
    /**
     * Testet die grundlegende Funktionalität der Size-Klasse.
     * Überprüft die Objekterstellung und stellt sicher, dass das Objekt nicht null ist.
     */
    @Test
    public void testConstructr1() {
        Size testSize = new Size();
        assertNotNull(testSize);
        assertEquals(Size.class, testSize.getClass());        
    }

    /**
     * Tests the constructor functionality for rectangle Size
     */
    @Test
    public void testConstructor2() {
        float testFloat = 0.123f;
        Size testSize = new Size(testFloat, testFloat);
        assertNotNull(testSize);
        assertEquals(Size.class, testSize.getClass());
        assertEquals(testFloat, testSize.getWidth(), 0.0001);
        assertEquals(testFloat, testSize.getHeight(), 0.0001);
    }

    /**
     * Tests the Constructor functionallity for circular Size
     */
    @Test
    public void testConstructor3() {
        float testFloat = 0.123f;
        Size testSize = new Size(testFloat);
        assertNotNull(testSize);
        assertEquals(Size.class, testSize.getClass());
        assertEquals(testFloat, testSize.getRadius(), 0.0001);
    }

    /**
     * Testet die Width-Setter- und Getter-Methoden der Size-Klasse.
     * Überprüft, ob die gesetzte Breite korrekt zurückgegeben wird.
     */
    @Test
    public void testWidthGetterAndSetter() {
        Size size = new Size();
        float expectedWidth = 123.45f;
        size.setWidth(expectedWidth);
        assertEquals(expectedWidth, size.getWidth(), 0.0001f);
    }

    /**
     * Testet die Height-Setter- und Getter-Methoden der Size-Klasse.
     * Überprüft, ob die gesetzte Höhe korrekt zurückgegeben wird.
     */
    @Test
    public void testHeightGetterAndSetter() {
        Size size = new Size();
        float expectedHeight = 67.89f;
        size.setHeight(expectedHeight);
        assertEquals(expectedHeight, size.getHeight(), 0.0001f);
    }

    /**
     * Testet die Radius-Setter- und Getter-Methoden der Size-Klasse.
     * Überprüft, ob der gesetzte Radius korrekt zurückgegeben wird.
     */
    @Test
    public void testRadiusGetterAndSetter() {
        Size size = new Size();
        float expectedRadius = 42.0f;
        size.setRadius(expectedRadius);
        assertEquals(expectedRadius, size.getRadius(), 0.0001f);
    }
}