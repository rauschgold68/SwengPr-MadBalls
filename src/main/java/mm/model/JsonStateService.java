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
    
    /**
     * Constructs a new JsonStateService with configured ObjectMapper.
     * The ObjectMapper is set up with indented output for better readability.
     */
    public JsonStateService() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Generates JSON representation of current simulation state.
     * @param droppedObjects List of game objects dropped in the simulation
     * @param inventoryObjects List of objects in the inventory
     * @return JSON string representation of the simulation state
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
     * @param jsonString The JSON string to parse
     * @return SimulationState object parsed from JSON
     * @throws Exception if JSON cannot be parsed
     */
    public SimulationState parseStateJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, SimulationState.class);
    }
    
    /**
     * Validates if JSON string is valid simulation state format.
     * @param jsonString The JSON string to validate
     * @return true if JSON is valid, false otherwise
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
            return validateStateWithBounds(state, simSpaceWidth, simSpaceHeight);
        } catch (JsonParseException e) {
            return handleJsonParseException(e);
        } catch (JsonMappingException e) {
            return handleJsonMappingException(e);
        } catch (Exception e) {
            return new ValidationResult(false, "Unable to process JSON: " + e.getMessage());
        }
    }
    
    /**
     * Validates the parsed state with bounds checking if dimensions are provided.
     */
    private ValidationResult validateStateWithBounds(SimulationState state, double simSpaceWidth, double simSpaceHeight) {
        // Perform bounds checking if simulation space dimensions are provided
        if (simSpaceWidth > 0 && simSpaceHeight > 0) {
            ValidationResult boundsResult = BoundsValidator.validateObjectBounds(state, simSpaceWidth, simSpaceHeight);
            if (!boundsResult.isValid()) {
                return boundsResult;
            }
        }
        return new ValidationResult(true, "JSON is valid and all objects are positioned correctly");
    }
    
    /**
     * Handles JsonParseException and provides user-friendly error messages.
     */
    private ValidationResult handleJsonParseException(JsonParseException e) {
        String locationInfo = "";
        if (e.getLocation() != null) {
            locationInfo = String.format(" (line %d, column %d)", 
                e.getLocation().getLineNr(), e.getLocation().getColumnNr());
        }
        
        String friendlyMessage = createFriendlyParseErrorMessage(e.getOriginalMessage());
        return new ValidationResult(false, friendlyMessage + locationInfo);
    }
    
    /**
     * Creates user-friendly error messages for JSON parse exceptions.
     */
    private String createFriendlyParseErrorMessage(String originalMessage) {
        String lowercaseMessage = originalMessage.toLowerCase();
        
        if (lowercaseMessage.contains("unexpected character") || lowercaseMessage.contains("unexpected token")) {
            return "There's a syntax error in your JSON - check for missing commas, quotes, or brackets";
        } else if (lowercaseMessage.contains("expected") && lowercaseMessage.contains("'}'")) {
            return "Missing closing bracket '}' - make sure all brackets are properly closed";
        } else if (lowercaseMessage.contains("expected") && lowercaseMessage.contains("':'")) {
            return "Missing colon ':' after a property name";
        } else if (lowercaseMessage.contains("unexpected end")) {
            return "JSON is incomplete - it looks like it was cut off";
        } else {
            return "JSON syntax error: " + originalMessage;
        }
    }
    
    /**
     * Handles JsonMappingException and provides user-friendly error messages.
     */
    private ValidationResult handleJsonMappingException(JsonMappingException e) {
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
    }
    
    /**
     * Result of JSON validation containing status and error details.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        /**
         * Constructs a new ValidationResult.
         * @param valid Whether the validation passed
         * @param message Descriptive message about the validation result
         */
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