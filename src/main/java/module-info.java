/**
 * The main module of the mm application.
 */
module mm {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires jbox2d.library;

    exports mm.physics;
    exports mm.gui;
}
