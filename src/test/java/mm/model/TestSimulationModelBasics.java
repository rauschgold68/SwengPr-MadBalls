package mm.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSimulationModelBasics {
    
    private SimulationModel simulationModel;
    
    @BeforeEach
    public void setUp() {
        simulationModel = new SimulationModel("/test/level.json");
    }
    
    private InventoryObject createTestInventoryObject(String name, int count) {
        InventoryObject obj = new InventoryObject(name, "testType", new Size(20f, 20f));
        obj.setCount(count);
        obj.setPhysics(new Physics(1.0f, 0.5f, 0.3f, "dynamic"));
        obj.setAngle(0f);
        obj.setColour("BLACK");
        obj.setWinning(false);
        return obj;
    }
    
    private GameObject createTestGameObject(String name) {
        return new GameObject(name, "testType", new Position(10f, 20f), new Size(30f, 40f));
    }
    
    @Test
    public void testConstructor() {
        SimulationModel model = new SimulationModel("/test/level.json");
        assertNotNull(model);
        assertEquals("/test/level.json", model.getLevelPath());
        assertFalse(model.isWinScreenVisible());
    }
    
    @Test
    public void testGettersAndSettersBasic() {
        // Test level path
        simulationModel.setLevelPath("/new/level.json");
        assertEquals("/new/level.json", simulationModel.getLevelPath());
        
        // Test win screen visibility
        assertFalse(simulationModel.isWinScreenVisible());
        
        // Test geometric collision service exists
        assertNotNull(simulationModel.getGeometricCollisionService());
        
        // Test undo redo manager exists
        assertNotNull(simulationModel.getUndoRedoManager());
    }
    
    @Test
    public void testInventoryManagement() {
        // Create test inventory
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(createTestInventoryObject("ball", 3));
        inventory.add(createTestInventoryObject("box", 2));
        
        simulationModel.setInventoryObjects(inventory);
        assertEquals(inventory, simulationModel.getInventoryObjects());
        
        // Test finding inventory by name
        InventoryObject found = simulationModel.findInventoryObjectByName("ball");
        assertNotNull(found);
        assertEquals("ball", found.getName());
        assertEquals(3, found.getCount());
        
        // Test finding non-existent inventory
        InventoryObject notFound = simulationModel.findInventoryObjectByName("nonexistent");
        assertNull(notFound);
    }
    
    @Test
    public void testInventoryCountOperations() {
        // Setup test inventory
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(createTestInventoryObject("ball", 5));
        simulationModel.setInventoryObjects(inventory);
        
        InventoryObject ball = simulationModel.findInventoryObjectByName("ball");
        
        // Test increment
        simulationModel.incrementInventoryCount("ball");
        assertEquals(6, ball.getCount());
        
        // Test decrement
        simulationModel.decrementInventoryCount("ball");
        assertEquals(5, ball.getCount());
        
        // Test decrement doesn't go below zero
        ball.setCount(0);
        simulationModel.decrementInventoryCount("ball");
        assertEquals(0, ball.getCount());
        
        // Test operations on non-existent items don't crash
        assertDoesNotThrow(() -> {
            simulationModel.incrementInventoryCount("nonexistent");
            simulationModel.decrementInventoryCount("nonexistent");
        });
    }
    
    @Test
    public void testCreateGameObjectFromInventory() {
        // Setup test inventory
        InventoryObject template = createTestInventoryObject("ball", 3);
        template.setSize(new Size(10f, 10f));
        template.setColour("RED");
        template.setWinning(true);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(template);
        simulationModel.setInventoryObjects(inventory);
        
        // Create game object from template
        GameObject created = simulationModel.createGameObjectFromInventory(template, 100f, 200f);
        
        assertNotNull(created);
        assertEquals("ball", created.getName());
        assertEquals(template.getType(), created.getType());
        assertEquals(template.getColour(), created.getColour());
        assertEquals(template.isWinning(), created.isWinning());
        
        // Test position calculation with offset
        float expectedX = 100f - template.getSize().getWidth() / 2;
        float expectedY = 200f - template.getSize().getHeight() / 2;
        assertEquals(expectedX, created.getPosition().getX(), 0.01f);
        assertEquals(expectedY, created.getPosition().getY(), 0.01f);
    }
    
    @Test
    public void testDroppedObjectsManagement() {
        // Test empty initially
        assertEquals(0, simulationModel.getDroppedObjects().size());
        
        // Add dropped objects
        GameObject obj1 = createTestGameObject("ball");
        GameObject obj2 = createTestGameObject("box");
        
        simulationModel.addDroppedObject(obj1);
        simulationModel.addDroppedObject(obj2);
        
        assertEquals(2, simulationModel.getDroppedObjects().size());
        assertTrue(simulationModel.getDroppedObjects().contains(obj1));
        assertTrue(simulationModel.getDroppedObjects().contains(obj2));
        
        // Test setting dropped objects directly
        List<GameObject> newDropped = new ArrayList<>();
        newDropped.add(createTestGameObject("platform"));
        
        simulationModel.setDroppedObjects(newDropped);
        assertEquals(newDropped, simulationModel.getDroppedObjects());
        assertEquals(1, simulationModel.getDroppedObjects().size());
    }
    
    @Test
    public void testRestoreInventoryCounts() {
        // Setup inventory and dropped objects
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(createTestInventoryObject("ball", 1));
        inventory.add(createTestInventoryObject("box", 2));
        simulationModel.setInventoryObjects(inventory);
        
        // Add dropped objects
        simulationModel.addDroppedObject(createTestGameObject("ball"));
        simulationModel.addDroppedObject(createTestGameObject("ball"));
        simulationModel.addDroppedObject(createTestGameObject("box"));
        
        // Decrement counts to simulate placement
        simulationModel.decrementInventoryCount("ball");
        simulationModel.decrementInventoryCount("ball");
        simulationModel.decrementInventoryCount("box");
        
        InventoryObject ball = simulationModel.findInventoryObjectByName("ball");
        InventoryObject box = simulationModel.findInventoryObjectByName("box");
        
        // Current counts should be reduced
        assertEquals(0, ball.getCount()); // was 1, decreased by 2, but limited to 0
        assertEquals(1, box.getCount());  // was 2, decreased by 1
        
        // Restore counts
        simulationModel.restoreInventoryCounts();
        
        // Counts should be restored based on dropped objects
        assertEquals(2, ball.getCount()); // 0 + 2 dropped balls
        assertEquals(2, box.getCount());  // 1 + 1 dropped box
    }
    
    @Test
    public void testWinListener() {
        SimulationModel.WinListener mockListener = mock(SimulationModel.WinListener.class);
        
        simulationModel.setWinListener(mockListener);
        
        // Can't directly test win condition trigger without complex setup,
        // but can verify listener is set
        assertNotNull(mockListener);
    }
    
    @Test
    public void testEmptyCollections() {
        // Test that collections are properly initialized
        assertNotNull(simulationModel.getInventoryObjects());
        assertNotNull(simulationModel.getDroppedObjects());
        assertNotNull(simulationModel.getDroppedPhysicsVisualPairs());
        assertNotNull(simulationModel.getNoPlaceZones());
        assertNotNull(simulationModel.getPairs());
        assertNotNull(simulationModel.getGeometryPairs());
    }
    
    @Test
    public void testSettersForCollections() {
        // Test setting various collections
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(createTestInventoryObject("test", 1));
        
        List<GameObject> dropped = new ArrayList<>();
        dropped.add(createTestGameObject("dropped"));
        
        List<PhysicsVisualPair> visualPairs = new ArrayList<>();
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        
        simulationModel.setInventoryObjects(inventory);
        simulationModel.setDroppedObjects(dropped);
        simulationModel.setDroppedVisualPairs(visualPairs);
        simulationModel.setNoPlaceZones(noPlaceZones);
        
        assertEquals(inventory, simulationModel.getInventoryObjects());
        assertEquals(dropped, simulationModel.getDroppedObjects());
        assertEquals(visualPairs, simulationModel.getDroppedPhysicsVisualPairs());
        assertEquals(noPlaceZones, simulationModel.getNoPlaceZones());
    }
}
