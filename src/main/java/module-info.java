/**
 * The main module of the mm application.
 */
module mm {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires transitive jbox2d.library;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    exports mm.model;
    exports mm.controller;
    exports mm.view;

    opens mm.model;
    opens mm.controller;
}
