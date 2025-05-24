package mm.model.level;
/**
 * POJO
 * The Physics info for Object for jBox2d 
 */
public class Physics {
    private String bodyType;
    private float density;
    private float friction;
    private float restitution;
    private String shape;

    public String getbodyType() {return this.bodyType;}
    public void setBodyType(String newBodyType) {this.bodyType = newBodyType;}

    public float getDensity() {return this.density;}
    public void setDensity(float newDensity) {this.density = newDensity;}
    
    public float getfriction() {return this.friction;}
    public void setFriction(float newFriction) {this.friction = newFriction;}
    
    public float getRestitution() {return this.restitution;}
    public void setRestitution(float newRestitution) {this.restitution = newRestitution;}
    
    public String getShape() {return this.shape;}
    public void setShape(String newShape) {this.shape = newShape;}
}