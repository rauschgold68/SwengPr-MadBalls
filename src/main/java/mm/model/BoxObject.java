package mm.model;

/**
 * Represents a box that can be placed inside a level.
 * <p>
 * Each {@code Box} has a unique name
 * </p>
 * 
 * <ul>
 *   <li><b>Name:</b> Unique identifier for the object.</li>
 *   <li><b>Type:</b> JavaFX type or category of the object.</li>
 *   <li><b>Position:</b> Location of the object in the level.</li>
 *   <li><b>Angle:</b> Rotation of the object in degrees.</li>
 *   <li><b>Size:</b> Dimensions (width, height) of the object.</li>
 *   <li><b>Sprite:</b> Optional graphical representation.</li>
 *   <li><b>Colour:</b> Colour used if no sprite is set.</li>
 *   <li><b>Physics:</b> Physics properties for simulation.</li>
 * </ul>
 * 
 */
public class BoxObject extends AbstractObject {
    public BoxObject(String name/* , String color*/) {
        String type = "rectangle";
        float angle = 0.0f;
        Size size = new Size(100f, 50f, 0f);
        String colour = "BLACK";
        Physics physics = new Physics(1.0f, 0.4f, 0.0f, "dynamic");
        boolean winning = false;
        super(name, type angle, size, colour, physics, winning);
    }
}