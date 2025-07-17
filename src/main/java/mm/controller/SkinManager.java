package mm.controller;

/**
 * Singleton class to manage the globally selected skin/texture pack.
 * Ensures that the skin choice persists across different controllers and
 * scenes.
 */
public class SkinManager {
    private static SkinManager instance;
    private String selectedSkin = "Default";

    /**
     * Private constructor to prevent instantiation.
     */
    private SkinManager() {
    }

    /**
     * Gets the singleton instance of SkinManager.
     * 
     * @return the singleton instance
     */
    public static SkinManager getInstance() {
        if (instance == null) {
            instance = new SkinManager();
        }
        return instance;
    }

    /**
     * Gets the currently selected skin.
     * 
     * @return the selected skin ("Default" or "Legacy")
     */
    public String getSelectedSkin() {
        return selectedSkin;
    }

    /**
     * Sets the selected skin.
     * 
     * @param skin the skin to select ("Default" or "Legacy")
     */
    public void setSelectedSkin(String skin) {
        if (skin != null && (skin.equals("Default") || skin.equals("Legacy"))) {
            this.selectedSkin = skin;
        }
    }

    /**
     * Checks if the current skin is Legacy.
     * 
     * @return true if Legacy skin is selected, false otherwise
     */
    public boolean isLegacySkin() {
        return "Legacy".equals(selectedSkin);
    }
}
