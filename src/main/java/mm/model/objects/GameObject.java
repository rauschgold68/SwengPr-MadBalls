package mm.model.objects;
/**
 * POJO GameObjects
 * Objects placed inside of the Level
 * 
 * @author B.Schroeder
 * @version 0.5
 */
public class GameObject {
    /**
     * The name of placed Object (unique Identifier)
     */
    private String name;
    /**
     * The Type in javafx
     */
    private String type;
    /**
     * The Position of Object in Level
     */
    private Position position;
    /**
     * The size in (width, height)
     */
    private Size size;
    /**
     * The sprite used for complex Graphics
     */
    private String sprite;
    /**
     * The colour of the Object 
     * only visible, if the Object doesn't use  a sprite
     */
    private String colour;
    /**
     * The Physics Object for jBox2d 
     */
    private Physics physics;


    public GameObject() {}

    /**
     * Constructor for GameObject
     * Sprite may be placed in by setting by hand 
     * @param name unique identifyer
     * @param type the javafx type
     * @param position the position where to place object
     * @param size in (width, height)
     * @param colour the colour
     * @param physics the jBox2d physics Info
     * @param radius the radius of circle shape as in jbox2d
     */
    public GameObject(String name, String type, Position position, Size size, String colour, Physics physics) {
        this.name = name; 
        this.type = type;
        this.position = position;
        this.size = size;
        this.colour = colour;
        this.physics = physics;
        
    }

    public String getName() {return this.name;}
    //no set name because name is unique Identifyer shouldn't be changed

    public String getType() {return this.type;}
    // no set type because if type changes, new unique object gets made

    public Position getPosition() {return this.position;}
    public void setPosition(Position newPosition) {this.position = newPosition;}

    public Size getSize() {return this.size;}
    public void setSize(Size newSize) {this.size = newSize;}

    public String getSprite() {return this.sprite;}
    public void setSprite(String newSprite) {this.sprite = newSprite;}

    public String getColour() {return this.colour;}
    public void setColor(String newColour) {this.colour = newColour;}

    public Physics getPhysics() {return this.physics;}
    public void setPhysics(Physics newPhysics) {this.physics = newPhysics;}

}