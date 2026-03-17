package models.ui;

import Interfaces.Drawable;
import javafx.application.Application;
import models.geometry.SuperAffine;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class App extends DrawingApp {
    private double screenX = 0;
    private double screenY = 0;
    private final SuperAffine superAffine = new SuperAffine();
    private final List<Drawable> drawables = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Drawing App");
        stage.setResizable(false);
        stage.setWidth(getWIDTH());
        stage.setHeight(getHEIGHT());

        BorderPane mouseEventComponent = new BorderPane();
        mouseEventComponent.setOnMousePressed(this::handleMousePressed);
        mouseEventComponent.setOnMouseDragged(this::handleMouseDragged);
        mouseEventComponent.setOnScroll(this::handleScroll);

        stage.setScene(new Scene(new StackPane(this.imageView, mouseEventComponent), getWIDTH(), getHEIGHT()));
        stage.show();

        // Setup
        // reCenter(new double[]{10, 50, 15, 55}); // Centers around the bounds given

        // Initial draw and render
        draw();
        render();
    }

    /**
     * Creates a new correctly configured Graphics Context and resets the screen,
     * sets the background color to an ocean blue,
     * set the transform (which triggers the screen update)
     * and draws the contents in the drawables list.
     */
    private void draw() {
        Graphics2D gc = getNewGraphicsContext();
        DrawingUtils.applyTransformation(gc);

        // Clear background
        gc.setBackground(Color.decode("#a9d3de")); // Blue
        gc.clearRect(0, 0, getWIDTH(), getHEIGHT());

        // Transform
        gc.setTransform(superAffine);

        // Draw
        for (Drawable drawable : drawables) {
            drawable.draw(gc);
        }
    }

    /**
     * Runs both draw and render functions.
     */
    private void drawAndRender() {
        draw();
        render();
    }



    /**
     * Keeps track of the first mouse press in when panning.
     * @param event The screen mouse press event that was triggered.
     */
    private void handleMousePressed(MouseEvent event) {
        this.screenX = event.getX();
        this.screenY = event.getY();
    }

    /**
     * Pans by following the mouse.
     * @param event The screen mouse drag event that was triggered.
     */
    private void handleMouseDragged(MouseEvent event) {
        double dx = event.getX() - this.screenX, dy = event.getY() - this.screenY;
        superAffine.prependTranslation(dx, dy);
        handleMousePressed(event);

        drawAndRender();
    }

    /**
     * Zooms in-and-out around the mouse.
     * @param event The screen scroll event that was triggered.
     */
    private void handleScroll(ScrollEvent event) {
        double zoom = event.getDeltaY() > 0 ? 1.05 : 1/1.05;
        superAffine
                .prependTranslation(-event.getX(), -event.getY())
                .prependScale(zoom, zoom)
                .prependTranslation(event.getX(), event.getY());

        drawAndRender();
    }

    /**
     * Re-centers the screen against some world lat/lon bounds.
     * The function will add the projection transformation of 0.56 to the bounds.
     * @param bounds An array in the format {minLon, minLat, maxLon, maxLat}.
     */
    public void reCenter(double[] bounds) {
        double scale = getHEIGHT() / (bounds[3] - bounds[1]);
        superAffine
                .reset()
                .prependTranslation(
                        -0.56 * bounds[0],
                        bounds[3]
                )
                .prependScale(
                        scale,
                        scale
                );
    }
}
