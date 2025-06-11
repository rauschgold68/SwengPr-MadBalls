package mm;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

public class PatternCreator {

    /**
     * Creates a green-white diagonal tape pattern as an ImagePattern.
     * @param width  The width of the pattern tile.
     * @param height The height of the pattern tile.
     * @return An ImagePattern to use as a fill. Here the pattern for the WinZone.
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
     * Creates a green-white diagonal tape pattern as an ImagePattern.
     * @param width  The width of the pattern tile.
     * @param height The height of the pattern tile.
     * @return An ImagePattern to use as a fill. Here the pattern for the NoPlaceZone.
     */
    public static Paint createNoPlaceZone(float width, float height){
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw diagonal stripes
        int stripeWidth = 16;
        for (float i = -height; i < width; i += stripeWidth) {
            // green part of stripes
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
