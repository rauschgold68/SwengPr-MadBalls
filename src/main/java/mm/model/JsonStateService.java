package mm.model;

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