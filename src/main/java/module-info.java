module org.example {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;

    exports Interfaces;
    exports models.geometry;
    exports models.osm;
    exports models.parser;
    exports models.ui;
}
