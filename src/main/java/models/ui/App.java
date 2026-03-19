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
import models.parser.Parser;
import models.rendering.WayRenderer;

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

        Parser parser = new Parser("Bornholm.osm");
        parser.parse();
        drawables.add(new WayRenderer(parser.getOsmWayMap().values()));

        List<Double> bb = parser.getBoundingBox();

        // reCenter(new double[]{10, 50, 15, 55}); // Centers around the bounds given
        reCenter(new double[]{bb.get(1), bb.get(0), bb.get(3), bb.get(2)});

        BorderPane mouseEventComponent = new BorderPane();
        mouseEventComponent.setOnMousePressed(this::handleMousePressed);
        mouseEventComponent.setOnMouseDragged(this::handleMouseDragged);
        mouseEventComponent.setOnScroll(this::handleScroll);

        stage.setScene(new Scene(new StackPane(this.imageView, mouseEventComponent), getWIDTH(), getHEIGHT()));
        stage.show();

        // Initial draw and render
        draw();
        render();
    }


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
            drawable.drawForTest(gc, Color.DARK_GRAY, 2);
        }
    }

    private void drawAndRender() {
        draw();
        render();
    }

    private void handleMousePressed(MouseEvent event) {
        this.screenX = event.getX();
        this.screenY = event.getY();
    }

    private void handleMouseDragged(MouseEvent event) {
        double dx = event.getX() - this.screenX, dy = event.getY() - this.screenY;
        superAffine.prependTranslation(dx, dy);
        handleMousePressed(event);

        drawAndRender();
    }

    private void handleScroll(ScrollEvent event) {
        double zoom = event.getDeltaY() > 0 ? 1.05 : 1/1.05;
        superAffine
                .prependTranslation(-event.getX(), -event.getY())
                .prependScale(zoom, zoom)
                .prependTranslation(event.getX(), event.getY());

        drawAndRender();
    }

    public void reCenter(double[] bounds) {
        double scale = getHEIGHT() / (bounds[3] - bounds[1]);
        superAffine.reset().prependTranslation(-0.56 * bounds[0], bounds[3]).prependScale(scale, scale);
    }
}
