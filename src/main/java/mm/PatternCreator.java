package mm;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

/**
 * Utility class for generating custom diagonal tape patterns as {@link ImagePattern} fills
 * for use in JavaFX shapes, such as marking special zones in the game.
 * <p>
 * Provides static methods to create visually distinctive patterns for "WinZone" and "NoPlaceZone"
 * areas, using green-white and red-white diagonal stripes, respectively.
 * </p>
 * <p>
 * These patterns are typically used as fill styles for rectangles or other shapes to visually
 * distinguish special regions in the game world.
 * </p>
 * <p>
 * <b>Usage example:</b>
 * <pre>
 *     Rectangle winZoneRect = new Rectangle(width, height);
 *     winZoneRect.setFill(PatternCreator.createWinzone(width, height));
 * </pre>
 * </p>
 */
public class PatternCreator {

    /**
     * Creates a green-white diagonal tape pattern as an {@link ImagePattern}.
     * <p>
     * This pattern is intended for marking "WinZone" areas in the game.
     * The pattern is generated dynamically using a {@link Canvas} and consists of alternating
     * green and white diagonal stripes.
     * </p>
     *
     * @param width  The width of the pattern tile.
     * @param height The height of the pattern tile.
     * @return An {@link ImagePattern} to use as a fill for JavaFX shapes.
     */
    public static Paint createWinzone(float width, float height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw diagonal stripes
        int stripeWidth = 16;
        for (float i = -height; i < width; i += stripeWidth) {
            // green part of stripes
            gc.setFill(Color.LIMEGREEN);
            gc.fillPolygon(
                new double[]{i, i + stripeWidth / 2.0, i + stripeWidth, i + stripeWidth / 2.0},
                new double[]{0, 0, height, height},
                4
            );
            // white part of stripes
            gc.setFill(Color.WHITE);
            gc.fillPolygon(
                new double[]{i + stripeWidth / 2.0, i + stripeWidth, i + 1.5 * stripeWidth, i + stripeWidth},
                new double[]{0, 0, height, height},
                4
            );
        }

        Image patternImage = canvas.snapshot(null, null);
        return new ImagePattern(patternImage, 0, 0, width, height, false);
    }

    /**
     * Creates a red-white diagonal tape pattern as an {@link ImagePattern}.
     * <p>
     * This pattern is intended for marking "NoPlaceZone" areas in the game.
     * The pattern is generated dynamically using a {@link Canvas} and consists of alternating
     * red and white diagonal stripes.
     * </p>
     *
     * @param width  The width of the pattern tile.
     * @param height The height of the pattern tile.
     * @return An {@link ImagePattern} to use as a fill for JavaFX shapes.
     */
    public static Paint createNoPlaceZone(float width, float height){
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw diagonal stripes
        int stripeWidth = 16;
        for (float i = -height; i < width; i += stripeWidth) {
            // red part of stripes
            gc.setFill(Color.RED);
            gc.fillPolygon(
                new double[]{i, i + stripeWidth / 2.0, i + stripeWidth, i + stripeWidth / 2.0},
                new double[]{0, 0, height, height},
                4
            );
            // white part of stripes
            gc.setFill(Color.WHITE);
            gc.fillPolygon(
                new double[]{i + stripeWidth / 2.0, i + stripeWidth, i + 1.5 * stripeWidth, i + stripeWidth},
                new double[]{0, 0, height, height},
                4
            );
        }

        Image patternImage = canvas.snapshot(null, null);
        return new ImagePattern(patternImage, 0, 0, width, height, false);
    }

}
