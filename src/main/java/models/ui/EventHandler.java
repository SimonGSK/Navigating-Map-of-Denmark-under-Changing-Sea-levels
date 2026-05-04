package models.ui;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;

import java.util.function.Consumer;
import java.util.function.Function;

public class EventHandler {
    private final AppController appController;
    private final BorderPane mapMouseEventComponent = new BorderPane();

    public EventHandler(
            AppController appController,
            Consumer<MouseEvent> handleMousePress,
            Consumer<MouseEvent> handleMouseDrag,
            Consumer<ScrollEvent> handleScroll
    ) {
        this.appController = appController;
        initMapMouseEventComponent(handleMousePress,handleMouseDrag,handleScroll);
    }

    public void initMapMouseEventComponent(
            Consumer<MouseEvent> handleMousePress,
            Consumer<MouseEvent> handleMouseDrag,
            Consumer<ScrollEvent> handleScroll
    ) {
        mapMouseEventComponent.setOnMousePressed(handleMousePress::accept);
        mapMouseEventComponent.setOnMouseDragged(handleMouseDrag::accept);
        mapMouseEventComponent.setOnScroll(handleScroll::accept);
    }

    public BorderPane getMapMouseEventComponent() {
        return mapMouseEventComponent;
    }
}
