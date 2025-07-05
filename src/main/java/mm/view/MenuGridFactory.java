package mm.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/**
 * Helper class responsible for building the menu grid with control buttons.
 * Extracted from SimulationView to reduce method count and improve maintainability.
 * Follows the MVC pattern by handling only UI construction logic.
 */
public class MenuGridFactory {
    
    private static final String MENU_BUTTON_CLASS = "menu-button";
    
    private final SimulationView.SimulationButtons simulationButtons;
    
    /**
     * Constructs a MenuGridBuilder with references to button containers.
     * 
     * @param simulationButtons the container for simulation control buttons
     */
    public MenuGridFactory(SimulationView.SimulationButtons simulationButtons) {
        this.simulationButtons = simulationButtons;
    }
    
    /**
     * Creates and configures the complete menu grid section.
     * 
     * @param isPuzzleMode affects which buttons are displayed
     * @param sideBar the sidebar to add components to
     * @param inventoryBox the inventory box component
     */
    public void buildMenuGrid(boolean isPuzzleMode, VBox sideBar, StackPane inventoryBox) {
        HBox squareContainer = new HBox();
        squareContainer.getStyleClass().add("square-container");
        squareContainer.setAlignment(Pos.CENTER);

        StackPane menuSquare = new StackPane();
        menuSquare.getStyleClass().add("menu-square");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("menu-grid");

        populateMenuGrid(grid, isPuzzleMode);

        menuSquare.getChildren().add(grid);
        squareContainer.getChildren().add(menuSquare);
        sideBar.getChildren().addAll(inventoryBox, squareContainer);
    }

    /**
     * Populates the menu grid with buttons and icons based on position and game mode.
     * 
     * @param grid the GridPane to populate
     * @param isPuzzleMode determines button icons and availability
     */
    private void populateMenuGrid(GridPane grid, boolean isPuzzleMode) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = createMenuButton();
                FontIcon icon = createIconForGridPosition(row, col, isPuzzleMode, btn);
                
                if (icon != null) {
                    setupIcon(icon);
                    btn.setGraphic(icon);
                }

                grid.add(btn, col, row);
            }
        }
    }

    /**
     * Creates a standard menu button with consistent styling.
     * 
     * @return a new Button with menu styling applied
     */
    private Button createMenuButton() {
        Button btn = new Button();
        btn.getStyleClass().add(MENU_BUTTON_CLASS);
        return btn;
    }

    /**
     * Creates the appropriate icon for a grid position and assigns the button reference.
     * 
     * @param row the grid row (0-2)
     * @param col the grid column (0-2)
     * @param isPuzzleMode determines button availability
     * @param btn the button instance to assign
     * @return the FontIcon for this position, or null if no icon
     */
    private FontIcon createIconForGridPosition(int row, int col, boolean isPuzzleMode, Button btn) {
        if (row == 0) {
            return createTopRowIcon(col, btn);
        }
        if (row == 1) {
            return createMiddleRowIcon(col, isPuzzleMode, btn);
        }
        if (row == 2) {
            return createBottomRowIcon(col, btn);
        }
        return null;
    }
    
    /**
     * Creates icons for the top row of the grid (play, stop, settings).
     */
    private FontIcon createTopRowIcon(int col, Button btn) {
        switch (col) {
            case 0:
                simulationButtons.playButton = btn;
                return new FontIcon(FontAwesomeSolid.PLAY);
            case 1:
                simulationButtons.stopButton = btn;
                return new FontIcon(FontAwesomeSolid.STOP);
            case 2:
                simulationButtons.settingsButton = btn;
                return new FontIcon(FontAwesomeSolid.COGS);
            default:
                return null;
        }
    }
    
    /**
     * Creates icons for the middle row of the grid (delete, import, save).
     */
    private FontIcon createMiddleRowIcon(int col, boolean isPuzzleMode, Button btn) {
        switch (col) {
            case 0:
                simulationButtons.deleteButton = btn;
                String resetIcon = isPuzzleMode ? "REDO_ALT" : "TRASH_ALT";
                return new FontIcon(FontAwesomeSolid.valueOf(resetIcon));
            case 1:
                if (!isPuzzleMode) {
                    simulationButtons.importButton = btn;
                    return new FontIcon(FontAwesomeSolid.FOLDER_PLUS);
                }
                return null;
            case 2:
                simulationButtons.saveButton = btn;
                return new FontIcon(FontAwesomeSolid.SAVE);
            default:
                return null;
        }
    }
    
    /**
     * Creates icons for the bottom row of the grid (currently only crown button).
     */
    private FontIcon createBottomRowIcon(int col, Button btn) {
        switch (col) {
            case 1:
                simulationButtons.undoButton = btn;
                return new FontIcon(FontAwesomeSolid.REPLY);
            case 2:
                simulationButtons.redoButton = btn;
                return new FontIcon(FontAwesomeSolid.SHARE);
            default:
            return null;
        }
    }

    /**
     * Configures the standard visual properties for menu icons.
     */
    private void setupIcon(FontIcon icon) {
        icon.setIconSize(16);
        icon.setIconColor(Color.WHITE);
    }
}
