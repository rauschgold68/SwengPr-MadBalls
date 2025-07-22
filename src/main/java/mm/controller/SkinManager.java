package mm.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import mm.model.GameObject;
import mm.model.InventoryObject;

/**
 * Singleton class to manage the globally selected skin/texture pack.
 * Ensures that the skin choice persists across different controllers and
 * scenes.
 */
public class SkinManager {
    private static SkinManager instance;
    private String selectedSkin = "Default";

    // Constants for string literals
    private static final String OBJECT_SKINS_PATH = "/objectSkins/";

    // Map to store the default sprite names for each object type
    private static final Map<String, String> DEFAULT_SPRITES = new HashMap<>();

    static {
        // Define default sprite names for each object type
        DEFAULT_SPRITES.put("Domino", "madDomino.png");
        DEFAULT_SPRITES.put("platform", "madPlatform.png");
        DEFAULT_SPRITES.put("log", "madLog.png");
        DEFAULT_SPRITES.put("tennisball", "madTennis.png");
        DEFAULT_SPRITES.put("bowlingball", "madBowling.png");
        DEFAULT_SPRITES.put("ball", "madBall.png");
        DEFAULT_SPRITES.put("ballon", "madBalloon.png");
        DEFAULT_SPRITES.put("box1", "madBox.png");
    }

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

    /**
     * Updates sprites for inventory objects based on the current skin selection.
     * This method will assign the correct sprite path based on the object name and
     * selected skin.
     * 
     * @param inventoryObjects the list of inventory objects to update
     */
    public void updateInventorySpritesForSkin(List<InventoryObject> inventoryObjects) {
        String skinFolder = OBJECT_SKINS_PATH + selectedSkin + "/";

        for (InventoryObject obj : inventoryObjects) {
            String objectName = obj.getName();
            String defaultSprite = DEFAULT_SPRITES.get(objectName);

            if (defaultSprite != null) {
                // Set the sprite with the current skin folder
                obj.setSprite(skinFolder + defaultSprite);
            } else {
                // Handle existing sprite logic for objects not in our default map
                String originalSprite = obj.getSprite();
                if (originalSprite != null && !originalSprite.trim().isEmpty()) {
                    String newSprite;
                    // If sprite already contains full path, replace the skin folder
                    if (originalSprite.contains(OBJECT_SKINS_PATH)) {
                        newSprite = originalSprite.replaceAll(OBJECT_SKINS_PATH + "[^/]+/", skinFolder);
                    } else {
                        // If sprite is just filename, prepend with skin folder
                        newSprite = skinFolder + originalSprite;
                    }
                    obj.setSprite(newSprite);
                }
            }
        }
    }

    /**
     * Updates sprites for game objects based on the current skin selection.
     * This method will assign the correct sprite path based on the object name and
     * selected skin.
     * 
     * @param gameObjects the list of game objects to update
     */
    public void updateGameObjectSpritesForSkin(List<GameObject> gameObjects) {
        String skinFolder = OBJECT_SKINS_PATH + selectedSkin + "/";

        for (GameObject obj : gameObjects) {
            String objectName = obj.getName();
            String defaultSprite = DEFAULT_SPRITES.get(objectName);

            if (defaultSprite != null) {
                // Set the sprite with the current skin folder
                obj.setSprite(skinFolder + defaultSprite);
            } else {
                // Handle existing sprite logic for objects not in our default map
                String originalSprite = obj.getSprite();
                if (originalSprite != null && !originalSprite.trim().isEmpty()) {
                    String newSprite;
                    // If sprite already contains full path, replace the skin folder
                    if (originalSprite.contains(OBJECT_SKINS_PATH)) {
                        newSprite = originalSprite.replaceAll(OBJECT_SKINS_PATH + "[^/]+/", skinFolder);
                    } else {
                        // If sprite is just filename, prepend with skin folder
                        newSprite = skinFolder + originalSprite;
                    }
                    obj.setSprite(newSprite);
                }
            }
        }
    }
}
