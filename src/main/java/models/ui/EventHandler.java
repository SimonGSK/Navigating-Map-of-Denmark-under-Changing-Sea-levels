package models.ui;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Wires input handlers to JavaFX nodes.
 */
public class EventHandler {
    private final BorderPane mapMouseEventComponent = new BorderPane();

    /**
     * Attaches mouse and scroll handlers to the map pane.
     * @param handleMousePress handler for press
     * @param handleMouseClick handler for click
     * @param handleMouseDrag handler for drag
     * @param handleMouseMove handler for move
     * @param handleScroll handler for scroll
     */
    public void initMapMouseEventComponent(
            Consumer<MouseEvent> handleMousePress,
            Consumer<MouseEvent> handleMouseClick,
            Consumer<MouseEvent> handleMouseDrag,
            Consumer<MouseEvent> handleMouseMove,
            Consumer<ScrollEvent> handleScroll
    ) {
        mapMouseEventComponent.setOnMousePressed(handleMousePress::accept);
        mapMouseEventComponent.setOnMouseClicked(handleMouseClick::accept);
        mapMouseEventComponent.setOnMouseDragged(handleMouseDrag::accept);
        mapMouseEventComponent.setOnMouseMoved(handleMouseMove::accept);
        mapMouseEventComponent.setOnScroll(handleScroll::accept);
    }

    /**
     * Attaches key handlers to the scene.
     * @param scene scene to bind
     * @param handleKeyPress key handler
     */
    public void initKeyboardEventComponent(
            Scene scene,
            Consumer<KeyEvent> handleKeyPress
    ) {
        scene.setOnKeyPressed(handleKeyPress::accept);
    }

    /**
     * @return pane that receives map mouse events
     */
    public BorderPane getMapMouseEventComponent() {
        return mapMouseEventComponent;
    }
}
