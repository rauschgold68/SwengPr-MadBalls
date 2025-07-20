# Model-View Pattern Refactoring

## Overview

This refactoring separates JavaFX-specific UI logic from the business logic in the MadBalls project, improving adherence to the Model-View pattern.

## Key Changes

### 1. New View-Agnostic Geometry Classes

**`GeometryData`** (Abstract base class)
- Pure mathematical representation of shapes
- No dependencies on JavaFX or other UI frameworks
- Contains geometric calculations (point containment, bounds checking)

**`RectangleGeometry`** and **`CircleGeometry`**
- Concrete implementations for rectangular and circular shapes
- Handle position, size, rotation without UI dependencies

### 2. Improved Physics-Geometry Coupling

**`PhysicsGeometryPair`** (Replaces `PhysicsVisualPair` in model layer)
- Links JBox2D physics bodies with view-agnostic geometry
- Removes direct JavaFX dependencies from the model

**`GeometricCollisionDetection`** (Replaces JavaFX-dependent collision code)
- Pure geometric collision detection algorithms
- Works with `GeometryData` instead of JavaFX shapes
- More accurate and framework-independent

### 3. View Layer Enhancements

**`ShapeFactory`**
- Converts `GeometryData` to JavaFX shapes
- Bridges model geometry with view rendering
- Supports shape creation and updates

**`VisualPhysicsPair`** (For view/controller use)
- Combines `PhysicsGeometryPair` with JavaFX shapes
- Used in controllers that need both model data and visual representation

**`GeometryConverter`**
- Utility for converting between JavaFX shapes and geometry data
- Helps maintain compatibility during migration

## Benefits

### Better Separation of Concerns
- **Model**: Contains pure business logic and geometry calculations
- **View**: Handles JavaFX-specific rendering and UI components
- **Controller**: Mediates between model and view using appropriate abstractions

### Framework Independence
- Model layer can work with any UI framework (JavaFX, Swing, web, etc.)
- Easier unit testing with pure geometric calculations
- Collision detection works without JavaFX runtime

### Improved Maintainability
- Cleaner interfaces between layers
- Easier to modify visual rendering without affecting physics
- More accurate collision detection algorithms

## Migration Path

The refactoring maintains backward compatibility:

1. **Existing code continues to work** with `PhysicsVisualPair`
2. **New geometry system runs in parallel** via `PhysicsGeometryPair`
3. **`SimulationModel.addPhysicsVisualPair()`** automatically creates both pairs
4. **Gradual migration** can replace JavaFX-dependent collision detection

## Usage Examples

### Creating View-Agnostic Geometry
```java
Position pos = new Position(100f, 100f);
RectangleGeometry rect = new RectangleGeometry(pos, 50, 30, 45.0);
boolean contains = rect.containsPoint(120, 110); // true

CircleGeometry circle = new CircleGeometry(pos, 25);
double[] bounds = circle.getBounds(); // [75, 75, 125, 125]
```

### Using Geometric Collision Detection
```java
SimulationModel model = new SimulationModel("level1.json");
GeometricCollisionDetection collisionDetector = model.getGeometricCollisionService();

// Check if moving an object would cause collision
PhysicsGeometryPair movingObject = model.getGeometryPairs().get(0);
boolean wouldCollide = collisionDetector.wouldCauseOverlap(movingObject, 200, 150);
```

### Converting Between Systems
```java
// Convert JavaFX shape to geometry
Shape javaFxShape = new Rectangle(100, 50);
GeometryData geometry = GeometryConverter.fromJavaFXShape(javaFxShape);

// Create JavaFX shape from geometry
RectangleGeometry rectGeom = new RectangleGeometry(pos, 100, 50);
Shape shape = ShapeFactory.createShape(rectGeom);
```

## Files Modified

### New Files
- `mm.model.GeometryData`
- `mm.model.RectangleGeometry`
- `mm.model.CircleGeometry`
- `mm.model.PhysicsGeometryPair`
- `mm.model.GeometricCollisionDetection`
- `mm.view.ShapeFactory`
- `mm.view.VisualPhysicsPair`
- `mm.controller.GeometryConverter`

### Modified Files
- `mm.model.SimulationModel` - Added geometry pair support, removed JavaFX dependencies
- `mm.model.CollisionDetection` - Can be gradually replaced by geometric version

## Future Improvements

1. **Complete Migration**: Replace all `PhysicsVisualPair` usage with the new system
2. **Enhanced Collision**: Implement more sophisticated collision algorithms (SAT for rotated rectangles)
3. **Performance**: Add spatial partitioning for collision detection
4. **Testing**: Add comprehensive unit tests for geometric calculations
