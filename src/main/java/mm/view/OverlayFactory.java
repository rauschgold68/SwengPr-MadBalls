package mm.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/**
 * Helper class responsible for building overlay components (settings and win screen).
 * Extracted from SimulationView to reduce method count and improve maintainability.
 * Follows the MVC pattern by handling only UI construction logic.
 */
public class OverlayFactory {
    
    // Style constants
    private static final String CIRCLE_BUTTON_CLASS = "circle-button";
    private static final String WHITE_LABEL_CLASS = "white-label";
    private static final String MENU_BUTTON_CLASS = "menu-button";
    private static final String OVERLAY_BACKGROUND_CLASS = "overlay-background";
    private static final String SETTINGS_OVERLAY_BACKGROUND_CLASS = "settings-overlay-background";
    private static final String OVERLAY_CLOSE_BUTTON_CLASS = "overlay-close-button";
    private static final String WIN_TITLE_LARGE_CLASS = "win-title-large";
    
    private final SimulationView.OverlayButtons overlayButtons;
    private final SimulationView.WinScreenButtons winScreenButtons;
    
    /**
     * Constructs an OverlayBuilder with references to button containers.
     * 
     * @param overlayButtons the container for overlay menu buttons
     * @param winScreenButtons the container for win screen buttons
     */
    public OverlayFactory(SimulationView.OverlayButtons overlayButtons, 
                         SimulationView.WinScreenButtons winScreenButtons) {
        this.overlayButtons = overlayButtons;
        this.winScreenButtons = winScreenButtons;
    }
    
    /**
     * Creates the settings/quick menu overlay with navigation options.
     * 
     * @return a StackPane representing the settings overlay
     */
    public StackPane buildQuickMenuOverlay() {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add(SETTINGS_OVERLAY_BACKGROUND_CLASS);
        overlay.setPickOnBounds(true);

        VBox window = createOverlayWindow();
        setupOverlayButtons(window);

        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Creates the level completion overlay with context-appropriate buttons.
     * 
     * @param ownerStage the primary stage for size binding
     * @param isPuzzleMode determines button options
     * @param atPuzzlesEnd affects button configuration
     * @return a StackPane representing the win screen overlay
     */
    public StackPane buildWinScreenOverlay(Stage ownerStage, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add(OVERLAY_BACKGROUND_CLASS);
        overlay.setVisible(false);
        overlay.prefWidthProperty().bind(ownerStage.widthProperty());
        overlay.prefHeightProperty().bind(ownerStage.heightProperty());

        VBox window = createWinWindow(atPuzzlesEnd);
        HBox buttonRow = createWinButtonRow(isPuzzleMode, atPuzzlesEnd, overlay);

        window.getChildren().add(buttonRow);
        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Creates the styled window container for overlay content.
     */
    private VBox createOverlayWindow() {
        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(15));
        window.setMaxWidth(300);
        window.setMaxHeight(180);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(10, 10, 20, 0.9), new CornerRadii(10), Insets.EMPTY)));
        return window;
    }

    /**
     * Creates and configures the overlay menu buttons.
     */
    private void setupOverlayButtons(VBox window) {
        HBox topRow = new HBox();
        overlayButtons.overlayCloseButton = new Button("✕");
        overlayButtons.overlayCloseButton.getStyleClass().add(OVERLAY_CLOSE_BUTTON_CLASS);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(spacer, overlayButtons.overlayCloseButton);

        overlayButtons.overlayBackButton = new Button("Back to Title Screen");
        overlayButtons.overlayBackButton.getStyleClass().add(MENU_BUTTON_CLASS);
        overlayButtons.overlayBackButton.setMaxWidth(Double.MAX_VALUE);
        overlayButtons.overlayBackButton.setPrefHeight(40);

        overlayButtons.overlayQuitButton = new Button("Quit Game");
        overlayButtons.overlayQuitButton.getStyleClass().add(MENU_BUTTON_CLASS);
        overlayButtons.overlayQuitButton.setMaxWidth(Double.MAX_VALUE);
        overlayButtons.overlayQuitButton.setPrefHeight(40);

        window.getChildren().addAll(topRow, overlayButtons.overlayBackButton, overlayButtons.overlayQuitButton);
    }

    /**
     * Creates the win screen window with crown icon and title.
     */
    private VBox createWinWindow(boolean atPuzzlesEnd) {
        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(30));
        window.setMaxWidth(600);
        window.setMaxHeight(250);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(20, 20, 40, 0.9),
                new CornerRadii(16), Insets.EMPTY)));
        window.getStyleClass().add("win-window");

        FontIcon crown = new FontIcon(FontAwesomeSolid.CROWN);
        crown.setIconSize(48);
        crown.setIconColor(Color.GOLD);

        String titleText = atPuzzlesEnd ? "Puzzle Series Complete!" : "Level Complete!";
        Label title = new Label(titleText);
        title.getStyleClass().add(WIN_TITLE_LARGE_CLASS);

        window.getChildren().addAll(crown, title);
        return window;
    }

    /**
     * Creates the button row for the win screen based on game mode.
     */
    private HBox createWinButtonRow(boolean isPuzzleMode, boolean atPuzzlesEnd, StackPane overlay) {
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        HBox mainMenuBox = createWinButton(FontAwesomeSolid.HOME, "Main Menu", overlay);
        HBox exportBox = createWinButton(FontAwesomeSolid.FILE_EXPORT, "Export Level", null);

        if (isPuzzleMode && !atPuzzlesEnd) {
            HBox nextBox = createWinButton(FontAwesomeSolid.ARROW_RIGHT, "Next Level", overlay);
            addButtonsWithSpacing(buttonRow, mainMenuBox, exportBox, nextBox);
        } else if (isPuzzleMode && atPuzzlesEnd) {
            addButtonsWithSpacing(buttonRow, mainMenuBox, exportBox);
        } else {
            HBox resumeBox = createWinButton(FontAwesomeSolid.PENCIL_ALT, "Resume Editing", overlay);
            addButtonsWithSpacing(buttonRow, mainMenuBox, resumeBox);
        }

        return buttonRow;
    }

    /**
     * Creates a win screen button with icon and label.
     */
    private HBox createWinButton(FontAwesomeSolid iconType, String labelText, StackPane overlay) {
        Button btn = new Button();
        btn.getStyleClass().add(CIRCLE_BUTTON_CLASS);
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(20);
        icon.setIconColor(Color.WHITE);
        btn.setGraphic(icon);

        if (overlay != null) {
            btn.setOnAction(e -> overlay.setVisible(false));
        }

        // Store button references
        if ("Main Menu".equals(labelText)) {
            winScreenButtons.btnWinHome = btn;
        } else if ("Next Level".equals(labelText)) {
            winScreenButtons.btnWinNext = btn;
        } else if ("Export Level".equals(labelText)) {
            winScreenButtons.btnWinExport = btn;
        }

        Label label = new Label(labelText);
        label.getStyleClass().add(WHITE_LABEL_CLASS);
        
        HBox buttonBox = new HBox(8, btn, label);
        if ("Next Level".equals(labelText)) {
            buttonBox = new HBox(8, label, btn); // Reverse order for next button
        }
        buttonBox.setAlignment(Pos.CENTER);

        return buttonBox;
    }

    /**
     * Adds buttons to a horizontal layout with evenly distributed spacing.
     */
    private void addButtonsWithSpacing(HBox buttonRow, HBox... buttons) {
        for (int i = 0; i < buttons.length; i++) {
            buttonRow.getChildren().add(buttons[i]);
            if (i < buttons.length - 1) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                buttonRow.getChildren().add(spacer);
            }
        }
    }
}
