package mm.model;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Handles collision detection between different shape types.
 * This class contains the core algorithms for shape-to-shape collision detection.
 */
class CollisionShapeHandler {
    
    private final CollisionGeometry geometry = new CollisionGeometry();
    
    /**
     * Checks collision when the moving object is a rectangle.
     */
    boolean checkRectangleCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                   double newX, double newY) {
        Rectangle movingRect = (Rectangle) movingPair.visual;
        
        if (otherPair.visual instanceof Rectangle) {
            return checkRectangleToRectangleCollision(movingRect, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Circle) {
            return checkRectangleToCircleCollision(movingRect, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Polygon) {
            return checkRectangleToPolygonCollision(movingRect, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision when the moving object is a circle.
     */
    boolean checkCircleCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                double newX, double newY) {
        Circle movingCircle = (Circle) movingPair.visual;
        
        if (otherPair.visual instanceof Circle) {
            return checkCircleToCircleCollision(movingCircle, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Rectangle) {
            return checkCircleToRectangleCollision(movingCircle, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Polygon) {
            return checkCircleToPolygonCollision(movingCircle, otherPair, newX, newY);
        }
        
        return false;
    }
    
    /**
     * Checks collision when the moving object is a polygon.
     */
    boolean checkPolygonCollision(PhysicsVisualPair movingPair, PhysicsVisualPair otherPair, 
                                 double newX, double newY) {
        Polygon movingPolygon = (Polygon) movingPair.visual;
        
        if (otherPair.visual instanceof Rectangle) {
            return checkPolygonToRectangleCollision(movingPolygon, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Circle) {
            return checkPolygonToCircleCollision(movingPolygon, otherPair, newX, newY);
        } else if (otherPair.visual instanceof Polygon) {
            return checkPolygonToPolygonCollision(movingPolygon, otherPair, newX, newY);
        }
        
        return false;
    }
    
    private boolean checkRectangleToRectangleCollision(Rectangle movingRect, 
                                                      PhysicsVisualPair otherPair, double newX, double newY) {
        Rectangle otherRect = (Rectangle) otherPair.visual;
        
        double movingCenterX = newX + movingRect.getWidth() / 2;
        double movingCenterY = newY + movingRect.getHeight() / 2;
        CollisionGeometry.CollisionRectangle moving = new CollisionGeometry.CollisionRectangle(
            new CollisionGeometry.RectangleParams(
                movingCenterX, movingCenterY, 
                movingRect.getWidth(), movingRect.getHeight()
            ).withRotation(movingRect.getRotate())
        );
        
        double otherCenterX = otherRect.getTranslateX() + otherRect.getWidth() / 2;
        double otherCenterY = otherRect.getTranslateY() + otherRect.getHeight() / 2;
        CollisionGeometry.CollisionRectangle other = new CollisionGeometry.CollisionRectangle(
            new CollisionGeometry.RectangleParams(
                otherCenterX, otherCenterY,
                otherRect.getWidth(), otherRect.getHeight()
            ).withRotation(otherRect.getRotate())
        );
        
        return geometry.checkOBBCollision(moving, other);
    }
    
    private boolean checkRectangleToCircleCollision(Rectangle movingRect, 
                                                   PhysicsVisualPair otherPair, double newX, double newY) {
        Circle otherCircle = (Circle) otherPair.visual;
        
        double movingWidth = movingRect.getWidth();
        double movingHeight = movingRect.getHeight();
        double rectCenterX = newX + movingWidth / 2;
        double rectCenterY = newY + movingHeight / 2;
        double rectRotation = movingRect.getRotate();
        
        CollisionGeometry.CollisionRectangle rect = new CollisionGeometry.CollisionRectangle(
            new CollisionGeometry.RectangleParams(
                rectCenterX, rectCenterY, movingWidth, movingHeight
            ).withRotation(rectRotation)
        );
        CollisionGeometry.CollisionCircle circle = new CollisionGeometry.CollisionCircle(
            otherCircle.getTranslateX(), 
            otherCircle.getTranslateY(), 
            otherCircle.getRadius()
        );
        
        return geometry.isRectangleCircleCollision(rect, circle);
    }
    
    private boolean checkCircleToCircleCollision(Circle movingCircle, 
                                                PhysicsVisualPair otherPair, double newX, double newY) {
        Circle otherCircle = (Circle) otherPair.visual;
        
        double movingRadius = movingCircle.getRadius();
        double otherX = otherCircle.getTranslateX();
        double otherY = otherCircle.getTranslateY();
        double otherRadius = otherCircle.getRadius();
        
        double distance = Math.sqrt(Math.pow(newX - otherX, 2) + Math.pow(newY - otherY, 2));
        return distance < movingRadius + otherRadius;
    }
    
    private boolean checkCircleToRectangleCollision(Circle movingCircle, 
                                                   PhysicsVisualPair otherPair, double newX, double newY) {
        Rectangle otherRect = (Rectangle) otherPair.visual;
        
        double otherX = otherRect.getTranslateX();
        double otherY = otherRect.getTranslateY();
        double otherWidth = otherRect.getWidth();
        double otherHeight = otherRect.getHeight();
        double otherRotation = otherRect.getRotate();
        
        CollisionGeometry.CollisionRectangle rect = new CollisionGeometry.CollisionRectangle(
            new CollisionGeometry.RectangleParams(
                otherX + otherWidth / 2, 
                otherY + otherHeight / 2, 
                otherWidth, 
                otherHeight
            ).withRotation(otherRotation)
        );
        CollisionGeometry.CollisionCircle circle = new CollisionGeometry.CollisionCircle(
            newX, newY, movingCircle.getRadius());
        
        return geometry.isRectangleCircleCollision(rect, circle);
    }
    
    private boolean checkRectangleToPolygonCollision(Rectangle movingRect, 
                                                    PhysicsVisualPair otherPair, double newX, double newY) {
        Polygon otherPolygon = (Polygon) otherPair.visual;
        
        javafx.geometry.Bounds rectBounds = geometry.getRotatedBounds(movingRect, newX, newY, movingRect.getRotate());
        javafx.geometry.Bounds polygonBounds = otherPolygon.getBoundsInParent();
        
        return rectBounds.intersects(polygonBounds);
    }
    
    private boolean checkCircleToPolygonCollision(Circle movingCircle, 
                                                 PhysicsVisualPair otherPair, double newX, double newY) {
        Polygon otherPolygon = (Polygon) otherPair.visual;
        
        javafx.geometry.Bounds polygonBounds = otherPolygon.getBoundsInParent();
        double polygonCenterX = polygonBounds.getMinX() + polygonBounds.getWidth() / 2;
        double polygonCenterY = polygonBounds.getMinY() + polygonBounds.getHeight() / 2;
        
        CollisionGeometry.CollisionRectangle rect = new CollisionGeometry.CollisionRectangle(
            new CollisionGeometry.RectangleParams(
                polygonCenterX, 
                polygonCenterY, 
                polygonBounds.getWidth(), 
                polygonBounds.getHeight()
            )
        );
        CollisionGeometry.CollisionCircle circle = new CollisionGeometry.CollisionCircle(
            newX, newY, movingCircle.getRadius());
        
        return geometry.isRectangleCircleCollision(rect, circle);
    }
    
    private boolean checkPolygonToRectangleCollision(Polygon movingPolygon, 
                                                    PhysicsVisualPair otherPair, double newX, double newY) {
        Rectangle otherRect = (Rectangle) otherPair.visual;

        Polygon tempPolygon = new Polygon();
        tempPolygon.getPoints().addAll(movingPolygon.getPoints());
        tempPolygon.setTranslateX(newX);
        tempPolygon.setTranslateY(newY);
        tempPolygon.setRotate(movingPolygon.getRotate());

        Rectangle tempRect = new Rectangle(otherRect.getWidth(), otherRect.getHeight());
        tempRect.setTranslateX(otherRect.getTranslateX());
        tempRect.setTranslateY(otherRect.getTranslateY());
        tempRect.setRotate(otherRect.getRotate());

        javafx.scene.shape.Shape intersection = javafx.scene.shape.Shape.intersect(tempPolygon, tempRect);
        return intersection.getBoundsInLocal().getWidth() > 0 && intersection.getBoundsInLocal().getHeight() > 0;
    }
    
    private boolean checkPolygonToCircleCollision(Polygon movingPolygon, 
                                                 PhysicsVisualPair otherPair, double newX, double newY) {
        Circle otherCircle = (Circle) otherPair.visual;
        
        Polygon tempPolygon = new Polygon();
        tempPolygon.getPoints().addAll(movingPolygon.getPoints());
        tempPolygon.setTranslateX(newX);
        tempPolygon.setTranslateY(newY);
        tempPolygon.setRotate(movingPolygon.getRotate());
        
        javafx.geometry.Bounds polygonBounds = tempPolygon.getBoundsInParent();
        double polygonCenterX = polygonBounds.getMinX() + polygonBounds.getWidth() / 2;
        double polygonCenterY = polygonBounds.getMinY() + polygonBounds.getHeight() / 2;
        
        CollisionGeometry.CollisionRectangle rect = new CollisionGeometry.CollisionRectangle(
            new CollisionGeometry.RectangleParams(
                polygonCenterX, 
                polygonCenterY, 
                polygonBounds.getWidth(), 
                polygonBounds.getHeight()
            )
        );
        CollisionGeometry.CollisionCircle circle = new CollisionGeometry.CollisionCircle(
            otherCircle.getTranslateX(), 
            otherCircle.getTranslateY(), 
            otherCircle.getRadius()
        );
        
        return geometry.isRectangleCircleCollision(rect, circle);
    }
    
    private boolean checkPolygonToPolygonCollision(Polygon movingPolygon, 
                                                  PhysicsVisualPair otherPair, double newX, double newY) {
        Polygon otherPolygon = (Polygon) otherPair.visual;
        
        Polygon tempPolygon = new Polygon();
        tempPolygon.getPoints().addAll(movingPolygon.getPoints());
        tempPolygon.setTranslateX(newX);
        tempPolygon.setTranslateY(newY);
        tempPolygon.setRotate(movingPolygon.getRotate());
        
        javafx.geometry.Bounds movingBounds = tempPolygon.getBoundsInParent();
        javafx.geometry.Bounds otherBounds = otherPolygon.getBoundsInParent();
        
        return movingBounds.intersects(otherBounds);
    }
}
