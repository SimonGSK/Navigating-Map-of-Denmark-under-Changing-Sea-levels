module org.example {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    exports Interfaces;
    exports models.geometry;
    exports models.osm;
    exports models.parser;
    exports models.ui;
    exports models.heightcurve;
}
