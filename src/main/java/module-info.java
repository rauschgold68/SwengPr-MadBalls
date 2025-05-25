/**
 * The main module of the mm application.
 */
module mm {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires jbox2d.library;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    
    exports mm.gui;
    exports mm.physics;
    
    opens mm.model.objects to com.fasterxml.jackson.databind;
}
