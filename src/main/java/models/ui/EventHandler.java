package models.ui;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;

import java.util.function.Consumer;
import java.util.function.Function;

public class EventHandler {
    private final BorderPane mapMouseEventComponent = new BorderPane();

    public void initMapMouseEventComponent(
            Consumer<MouseEvent> handleMousePress,
            Consumer<MouseEvent> handleMouseClick,
            Consumer<MouseEvent> handleMouseDrag,
            Consumer<ScrollEvent> handleScroll
    ) {
        mapMouseEventComponent.setOnMousePressed(handleMousePress::accept);
        mapMouseEventComponent.setOnMouseClicked(handleMouseClick::accept);
        mapMouseEventComponent.setOnMouseDragged(handleMouseDrag::accept);
        mapMouseEventComponent.setOnScroll(handleScroll::accept);
    }

    public void initKeyboardEventComponent(
            Scene scene,
            Consumer<KeyEvent> handleKeyPress
    ) {
        scene.setOnKeyPressed(handleKeyPress::accept);
    }

    public BorderPane getMapMouseEventComponent() {
        return mapMouseEventComponent;
    }
}
