package mm.model.objects;
/**
 * POJO
 * The Position of object in Level
 */
public class Position {
    private float x;
    private float y;

    public Position(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Position(){
        this.x = 0;
        this.y = 0;
    }

    public float getX() {return this.x;}
    public void setX(float newX) {this.x = newX;}

    public float getY() {return this.y;}
    public void setY(float newY) {this.y = newY;}

}