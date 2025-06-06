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

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    exports mm.gui;

    opens mm.model.objects to com.fasterxml.jackson.databind;
    opens mm to javafx.graphics;
}
