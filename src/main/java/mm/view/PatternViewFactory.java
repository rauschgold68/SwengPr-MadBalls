package mm.view;

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
 * <b>Usage example:</b>
 * <pre>
 *     Rectangle winZoneRect = new Rectangle(width, height);
 *     winZoneRect.setFill(PatternViewFactory.createWinzone(width, height));
 * </pre>
 */
public class PatternViewFactory {
    /** The width of each diagonal stripe in the pattern. */
    private static final int STRIPE_WIDTH = 16;
    /**
     * Creates a diagonal tape pattern as an {@link ImagePattern} with the specified color.
     * <p>
     * This pattern is intended for marking special zones in the game (e.g., "NoPlaceZone" with red,
     * "WinZone" with green). The pattern is generated dynamically using a {@link Canvas} and consists 
     * of alternating colored and white diagonal stripes with a matching colored border.
     * </p>
     *
     * @param width  The width of the pattern tile.
     * @param height The height of the pattern tile.
     * @param color  The primary color for the diagonal stripes and border (alternates with white).
     * @return An {@link ImagePattern} to use as a fill for JavaFX shapes.
     */
    public static Paint createPlaceZone(float width, float height, Color color) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawDiagonalStripes(gc, width, height, color);
        drawColoredBorder(gc, width, height, color);

        Image patternImage = canvas.snapshot(null, null);
        return new ImagePattern(patternImage, 0, 0, width, height, false);
    }

    /**
     * Draws diagonal stripes on the provided graphics context.
     * <p>
     * Creates alternating colored and white diagonal stripes across the canvas.
     * Uses integer-based loop indices for better performance and precision.
     * </p>
     *
     * @param gc     The GraphicsContext to draw on
     * @param width  The width of the drawing area
     * @param height The height of the drawing area
     * @param color  The primary color for the stripes
     */
    private static void drawDiagonalStripes(GraphicsContext gc, float width, float height, Color color) {
        // Calculate the range needed to cover the entire area with diagonal stripes
        int startPosition = (int) -height;
        int endPosition = (int) width;
        
        // Use integer loop to avoid float precision issues
        for (int i = startPosition; i < endPosition; i += STRIPE_WIDTH) {
            drawColoredStripe(gc, i, height, color);
            drawWhiteStripe(gc, i, height);
        }
    }

    /**
     * Draws a single colored diagonal stripe.
     * <p>
     * Creates a parallelogram shape representing one colored stripe in the pattern.
     * </p>
     *
     * @param gc       The GraphicsContext to draw on
     * @param position The horizontal starting position of the stripe
     * @param height   The height of the drawing area
     * @param color    The color to fill the stripe
     */
    private static void drawColoredStripe(GraphicsContext gc, int position, float height, Color color) {
        gc.setFill(color);
        gc.fillPolygon(
            new double[]{position, position + STRIPE_WIDTH / 2.0, position + STRIPE_WIDTH, position + STRIPE_WIDTH / 2.0},
            new double[]{0, 0, height, height},
            4
        );
    }

    /**
     * Draws a single white diagonal stripe.
     * <p>
     * Creates a parallelogram shape representing one white stripe in the pattern,
     * positioned to alternate with the colored stripes.
     * </p>
     *
     * @param gc       The GraphicsContext to draw on
     * @param position The horizontal starting position reference
     * @param height   The height of the drawing area
     */
    private static void drawWhiteStripe(GraphicsContext gc, int position, float height) {
        gc.setFill(Color.WHITE);
        gc.fillPolygon(
            new double[]{position + STRIPE_WIDTH / 2.0, position + STRIPE_WIDTH, position + 1.5 * STRIPE_WIDTH, position + STRIPE_WIDTH},
            new double[]{0, 0, height, height},
            4
        );
    }

    /**
     * Draws a colored border around the entire pattern area.
     * <p>
     * Creates a rectangular border using the specified color to frame the diagonal stripe pattern.
     * The border width is proportional to the pattern size for optimal visual appearance.
     * </p>
     *
     * @param gc     The GraphicsContext to draw on
     * @param width  The width of the drawing area
     * @param height The height of the drawing area
     * @param color  The color for the border
     */
    private static void drawColoredBorder(GraphicsContext gc, float width, float height, Color color) {
        // Calculate border width as a percentage of the smaller dimension for proportional appearance
        double borderWidth = Math.min(width, height) * 0.05; // 5% of the smaller dimension
        borderWidth = Math.max(borderWidth, 2.0); // Minimum border width of 2 pixels
        borderWidth = Math.min(borderWidth, 8.0); // Maximum border width of 8 pixels

        gc.setStroke(color);
        gc.setLineWidth(borderWidth);
        
        // Draw the border rectangle
        gc.strokeRect(borderWidth / 2, borderWidth / 2, 
                     width - borderWidth, height - borderWidth);
    }
}
