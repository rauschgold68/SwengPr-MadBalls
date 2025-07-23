package mm.model;

import mm.model.JsonStateService.ValidationResult;

/**
 * Utility class for validating object bounds within simulation space.
 * Extracted from JsonStateService to reduce complexity and method count.
 */
class BoundsValidator {
    
    /**
     * Validates that all objects in the simulation state are within bounds.
     * @param state The parsed simulation state
     * @param simSpaceWidth The width of the simulation space
     * @param simSpaceHeight The height of the simulation space
     * @return ValidationResult indicating if bounds are valid
     */
    static ValidationResult validateObjectBounds(JsonStateService.SimulationState state, double simSpaceWidth, double simSpaceHeight) {
        if (state.getDroppedObjects() != null) {
            for (int i = 0; i < state.getDroppedObjects().size(); i++) {
                GameObject obj = state.getDroppedObjects().get(i);
                ValidationResult result = validateSingleObjectBounds(obj, simSpaceWidth, simSpaceHeight);
                if (!result.isValid()) {
                    return result;
                }
            }
        }
        return new ValidationResult(true, "All objects are positioned correctly");
    }
    
    /**
     * Validates that a single object is within bounds.
     */
    private static ValidationResult validateSingleObjectBounds(GameObject obj, double simSpaceWidth, double simSpaceHeight) {
        if (obj.getPosition() == null) {
            return new ValidationResult(true, "Object position is valid");
        }
        
        ObjectDimensions dimensions = calculateObjectDimensions(obj);
        BoundingBox bounds = calculateRotatedBoundingBox(dimensions, obj.getPosition(), obj.getAngle());
        
        return checkBoundsViolation(obj, bounds, simSpaceWidth, simSpaceHeight);
    }
    
    /**
     * Calculates the dimensions of a game object.
     */
    private static ObjectDimensions calculateObjectDimensions(GameObject obj) {
        float objectWidth = 0;
        float objectHeight = 0;
        boolean isCircular = false;
        
        if (obj.getSize() != null) {
            objectWidth = obj.getSize().getWidth();
            objectHeight = obj.getSize().getHeight();
            
            // For circular objects, use radius
            if (objectWidth == 0 && objectHeight == 0 && obj.getSize().getRadius() > 0) {
                float radius = obj.getSize().getRadius();
                objectWidth = objectHeight = radius * 2;
                isCircular = true;
            }
        }
        
        return new ObjectDimensions(objectWidth, objectHeight, isCircular);
    }
    
    /**
     * Checks if the object bounds violate the simulation space boundaries.
     */
    private static ValidationResult checkBoundsViolation(GameObject obj, BoundingBox bounds, double simSpaceWidth, double simSpaceHeight) {
        // Check if any part of the object is outside simulation space bounds
        if (bounds.minX < 0 || bounds.maxX > simSpaceWidth || 
            bounds.minY < 0 || bounds.maxY > simSpaceHeight) {
            
            return createBoundsViolationMessage(obj, bounds, simSpaceWidth, simSpaceHeight);
        }
        
        return new ValidationResult(true, "Object is within bounds");
    }
    
    /**
     * Creates a detailed error message for bounds violations.
     */
    private static ValidationResult createBoundsViolationMessage(GameObject obj, BoundingBox bounds, double simSpaceWidth, double simSpaceHeight) {
        // Create human-readable error message
        String objectName = obj.getName() != null ? obj.getName() : "object";
        String positionDesc = String.format("%.0f, %.0f", obj.getPosition().getX(), obj.getPosition().getY());
        
        StringBuilder problem = createBoundaryViolationDescription(bounds, simSpaceWidth, simSpaceHeight);
        String rotationInfo = createRotationInfo(obj.getAngle());
        
        return new ValidationResult(false, 
            String.format("The %s at position (%s)%s is placed %s and would not be visible in the game area. Please move it closer to the center.", 
                objectName, positionDesc, rotationInfo, problem.toString()));
    }
    
    /**
     * Creates a description of which boundaries are violated.
     */
    private static StringBuilder createBoundaryViolationDescription(BoundingBox bounds, double simSpaceWidth, double simSpaceHeight) {
        StringBuilder problem = new StringBuilder();
        if (bounds.minX < 0) problem.append("too far left");
        if (bounds.maxX > simSpaceWidth) {
            if (problem.length() > 0) problem.append(" and ");
            problem.append("too far right");
        }
        if (bounds.minY < 0) {
            if (problem.length() > 0) problem.append(" and ");
            problem.append("too far up");
        }
        if (bounds.maxY > simSpaceHeight) {
            if (problem.length() > 0) problem.append(" and ");
            problem.append("too far down");
        }
        return problem;
    }
    
    /**
     * Creates rotation information for error messages.
     */
    private static String createRotationInfo(float angle) {
        // Add rotation info if object is rotated
        if (Math.abs(angle) > 0.1) { // Only mention rotation if significant
            return String.format(" (rotated %.0f°)", angle);
        }
        return "";
    }
    
    /**
     * Calculates the axis-aligned bounding box for a rotated object.
     * @param dimensions The object dimensions
     * @param position The position of the object
     * @param angleDegrees The rotation angle in degrees
     * @return BoundingBox containing the min/max coordinates
     */
    private static BoundingBox calculateRotatedBoundingBox(ObjectDimensions dimensions, Position position, float angleDegrees) {
        float centerX = position.getX();
        float centerY = position.getY();
        
        // For circular objects, rotation doesn't change the bounding box
        if (dimensions.isCircular) {
            float radius = dimensions.width / 2;
            return new BoundingBox(centerX - radius, centerY - radius, 
                                 centerX + radius, centerY + radius);
        }
        
        // For rectangular objects, calculate rotated bounding box
        double angleRad = Math.toRadians(angleDegrees);
        double cos = Math.abs(Math.cos(angleRad));
        double sin = Math.abs(Math.sin(angleRad));
        
        // Calculate the new width and height of the bounding box after rotation
        double newWidth = dimensions.width * cos + dimensions.height * sin;
        double newHeight = dimensions.width * sin + dimensions.height * cos;
        
        // Calculate bounding box coordinates
        float minX = (float) (centerX - newWidth / 2);
        float maxX = (float) (centerX + newWidth / 2);
        float minY = (float) (centerY - newHeight / 2);
        float maxY = (float) (centerY + newHeight / 2);
        
        return new BoundingBox(minX, minY, maxX, maxY);
    }
    
    /**
     * Simple bounding box container class.
     */
    private static class BoundingBox {
        final float minX, minY, maxX, maxY;
        
        BoundingBox(float minX, float minY, float maxX, float maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }
    
    /**
     * Helper class to store object dimensions.
     */
    private static class ObjectDimensions {
        final float width, height;
        final boolean isCircular;
        
        ObjectDimensions(float width, float height, boolean isCircular) {
            this.width = width;
            this.height = height;
            this.isCircular = isCircular;
        }
    }
}
