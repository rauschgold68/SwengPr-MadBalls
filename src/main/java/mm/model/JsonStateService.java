package mm.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;

/**
 * Service for handling JSON serialization and deserialization of simulation state.
 * Uses Jackson ObjectMapper for consistent JSON handling across the application.
 */
public class JsonStateService {
    private final ObjectMapper mapper;
    
    public JsonStateService() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Generates JSON representation of current simulation state.
     */
    public String generateStateJson(List<GameObject> droppedObjects, List<InventoryObject> inventoryObjects) {
        try {
            SimulationState state = new SimulationState();
            state.setDroppedObjects(droppedObjects);
            state.setInventoryObjects(inventoryObjects);
            return mapper.writeValueAsString(state);
        } catch (Exception e) {
            System.err.println("Failed to generate JSON: " + e.getMessage());
            return "{}";
        }
    }
    
    /**
     * Parses JSON and returns simulation state.
     */
    public SimulationState parseStateJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, SimulationState.class);
    }
    
    /**
     * Validates if JSON string is valid simulation state format.
     */
    public boolean isValidJson(String jsonString) {
        try {
            parseStateJson(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates JSON and provides detailed error information.
     * @param jsonString The JSON string to validate
     * @return ValidationResult containing validation status and error details
     */
    public ValidationResult validateJson(String jsonString) {
        return validateJson(jsonString, 0, 0);
    }
    
    /**
     * Validates JSON and provides detailed error information including bounds checking.
     * @param jsonString The JSON string to validate
     * @param simSpaceWidth The width of the simulation space for bounds checking
     * @param simSpaceHeight The height of the simulation space for bounds checking
     * @return ValidationResult containing validation status and error details
     */
    public ValidationResult validateJson(String jsonString, double simSpaceWidth, double simSpaceHeight) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new ValidationResult(false, "Please enter some JSON content to validate");
        }
        
        try {
            SimulationState state = parseStateJson(jsonString);
            
            // Perform bounds checking if simulation space dimensions are provided
            if (simSpaceWidth > 0 && simSpaceHeight > 0) {
                ValidationResult boundsResult = validateObjectBounds(state, simSpaceWidth, simSpaceHeight);
                if (!boundsResult.isValid()) {
                    return boundsResult;
                }
            }
            
            return new ValidationResult(true, "JSON is valid and all objects are positioned correctly");
        } catch (JsonParseException e) {
            String locationInfo = "";
            if (e.getLocation() != null) {
                locationInfo = String.format(" (line %d, column %d)", 
                    e.getLocation().getLineNr(), e.getLocation().getColumnNr());
            }
            
            // Simplify common JSON syntax errors
            String originalMessage = e.getOriginalMessage().toLowerCase();
            String friendlyMessage;
            
            if (originalMessage.contains("unexpected character") || originalMessage.contains("unexpected token")) {
                friendlyMessage = "There's a syntax error in your JSON - check for missing commas, quotes, or brackets";
            } else if (originalMessage.contains("expected") && originalMessage.contains("'}'")) {
                friendlyMessage = "Missing closing bracket '}' - make sure all brackets are properly closed";
            } else if (originalMessage.contains("expected") && originalMessage.contains("':'")) {
                friendlyMessage = "Missing colon ':' after a property name";
            } else if (originalMessage.contains("unexpected end")) {
                friendlyMessage = "JSON is incomplete - it looks like it was cut off";
            } else {
                friendlyMessage = "JSON syntax error: " + e.getOriginalMessage();
            }
            
            return new ValidationResult(false, friendlyMessage + locationInfo);
        } catch (JsonMappingException e) {
            String originalMessage = e.getOriginalMessage();
            String friendlyMessage;
            
            if (originalMessage.contains("Cannot deserialize")) {
                friendlyMessage = "The JSON structure doesn't match the expected format for game objects";
            } else if (originalMessage.contains("missing property") || originalMessage.contains("required property")) {
                friendlyMessage = "Required property is missing from the JSON";
            } else {
                friendlyMessage = "JSON format error: " + originalMessage;
            }
            
            return new ValidationResult(false, friendlyMessage);
        } catch (Exception e) {
            return new ValidationResult(false, "Unable to process JSON: " + e.getMessage());
        }
    }
    
    /**
     * Validates that all objects in the simulation state are within bounds.
     * @param state The parsed simulation state
     * @param simSpaceWidth The width of the simulation space
     * @param simSpaceHeight The height of the simulation space
     * @return ValidationResult indicating if bounds are valid
     */
    private ValidationResult validateObjectBounds(SimulationState state, double simSpaceWidth, double simSpaceHeight) {
        if (state.getDroppedObjects() != null) {
            for (int i = 0; i < state.getDroppedObjects().size(); i++) {
                GameObject obj = state.getDroppedObjects().get(i);
                if (obj.getPosition() != null) {
                    float centerX = obj.getPosition().getX();
                    float centerY = obj.getPosition().getY();
                    float angle = obj.getAngle(); // rotation angle in degrees
                    
                    // Get object dimensions
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
                    
                    // Calculate bounding box considering rotation
                    BoundingBox bounds = calculateRotatedBoundingBox(centerX, centerY, objectWidth, objectHeight, angle, isCircular);
                    
                    // Check if any part of the object is outside simulation space bounds
                    if (bounds.minX < 0 || bounds.maxX > simSpaceWidth || 
                        bounds.minY < 0 || bounds.maxY > simSpaceHeight) {
                        
                        // Create human-readable error message
                        String objectName = obj.getName() != null ? obj.getName() : "object";
                        String positionDesc = String.format("%.0f, %.0f", centerX, centerY);
                        
                        // Determine which boundaries are violated
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
                        
                        // Add rotation info if object is rotated
                        String rotationInfo = "";
                        if (Math.abs(angle) > 0.1) { // Only mention rotation if significant
                            rotationInfo = String.format(" (rotated %.0f°)", angle);
                        }
                        
                        return new ValidationResult(false, 
                            String.format("The %s at position (%s)%s is placed %s and would not be visible in the game area. Please move it closer to the center.", 
                                objectName, positionDesc, rotationInfo, problem.toString()));
                    }
                }
            }
        }
        return new ValidationResult(true, "All objects are positioned correctly");
    }
    
    /**
     * Calculates the axis-aligned bounding box for a rotated object.
     * @param centerX The x-coordinate of the object's center
     * @param centerY The y-coordinate of the object's center
     * @param width The width of the object
     * @param height The height of the object
     * @param angleDegrees The rotation angle in degrees
     * @param isCircular Whether the object is circular (circles don't change bounds when rotated)
     * @return BoundingBox containing the min/max coordinates
     */
    private BoundingBox calculateRotatedBoundingBox(float centerX, float centerY, float width, float height, float angleDegrees, boolean isCircular) {
        // For circular objects, rotation doesn't change the bounding box
        if (isCircular) {
            float radius = width / 2;
            return new BoundingBox(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        }
        
        // For rectangular objects, calculate rotated bounding box
        double angleRad = Math.toRadians(angleDegrees);
        double cos = Math.abs(Math.cos(angleRad));
        double sin = Math.abs(Math.sin(angleRad));
        
        // Calculate the new width and height of the bounding box after rotation
        double newWidth = width * cos + height * sin;
        double newHeight = width * sin + height * cos;
        
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
     * Result of JSON validation containing status and error details.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
    
    /**
     * Container class for simulation state data.
     */
    public static class SimulationState {
        private List<GameObject> droppedObjects;
        private List<InventoryObject> inventoryObjects;
        
        // Getters and setters
        public List<GameObject> getDroppedObjects() { return droppedObjects; }
        public void setDroppedObjects(List<GameObject> droppedObjects) { this.droppedObjects = droppedObjects; }
        
        public List<InventoryObject> getInventoryObjects() { return inventoryObjects; }
        public void setInventoryObjects(List<InventoryObject> inventoryObjects) { this.inventoryObjects = inventoryObjects; }
    }
}