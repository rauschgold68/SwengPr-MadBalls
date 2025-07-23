package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Position} class.
 * <p>
 * This test class verifies the correct behavior of Position objects, which represent
 * 2D coordinates in the simulation space. Position is a fundamental data structure
 * used throughout the model layer for object placement and geometric calculations.
 * </p>
 * <p>
 * The tests cover both constructors (default and parameterized) and verify
 * that coordinate values are properly stored and retrieved.
 * </p>
 * 
 * @see Position
 */
public class TestPosition {
    @Test
    public void testPosition() {
        Position testPosition = new Position();
        assertNotNull(testPosition);
        assertEquals(Position.class, testPosition.getClass());
        float testFloat1 = 1.2f;
        float testFloat2 = 3.4f;
        testPosition = new Position(testFloat1, testFloat2);
        assertNotNull(testPosition);
        assertEquals(testFloat1, testPosition.getX());
        assertEquals(testFloat2, testPosition.getY());
    }

    @Test
    public void testPositionSetterGetter() {
        Position testPosition = new Position();
        float testFloat = 0.123f;
        testPosition.setX(testFloat);
        testPosition.setY(testFloat);
        assertEquals(testFloat, testPosition.getX(), 0.00001f);
        assertEquals(testFloat, testPosition.getY(), 0.00001f);
    }
}
