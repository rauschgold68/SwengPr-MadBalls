package mm.model.objects;
/**
 * POJO
 * The InventoryObject (Item) Class
 */
public class InventoryObject {
    private String type;
    private int count;
    private Size size;
    private String sprite;
    private String colour;
    private Physics physics;
    private Float radius;

    public InventoryObject() {}

    /**
     * Constructor for InventoryObject
     * Sprite may be placed in by setting by hand 
     * Do NOT create new InventoryObject for similar Objects:
     * if the attributes are the same, just increment count
     * 
     * @param type the javafx type
     * @param count the count of this Item
     * @param size in (width, height)
     * @param colour the colour
     * @param physics the jBox2d physics Info
     * @param radius radius of circle shape placeable
     */
    public InventoryObject(String type, int count, Size size, String colour, Physics physics, Float radius) {
        this.type = type;
        this.count = count;
        this.size = size;
        this.colour = colour;
        this.physics = physics;
        this.radius = radius;
    }

    public String getType() {return this.type;}
    // no set type because if type changes, new unique object gets made

    public int getCount() {return this.count;}
    public void setCount(int newCount) {this.count = newCount;}
    
    public Size getSize() {return this.size;}
    public void setSize(Size newSize) {this.size = newSize;}

    public String getSprite() {return this.sprite;}
    public void setSprite(String newSprite) {this.sprite = newSprite;}

    public String getColour() {return this.colour;}
    public void setColor(String newColour) {this.colour = newColour;}

    public Physics getPhysics() {return this.physics;}
    public void setPhysics(Physics newPhysics) {this.physics = newPhysics;}

    public Float getRadius() {return this.radius;}
    public void setRadius(Float newRadius) {this.radius = newRadius;}

}