module org.example {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;

    exports org.example.Interfaces;
    exports org.example.models.geometry;
    exports org.example.models.osm;
    exports org.example.models.parser;
    exports org.example.models.ui;
}
