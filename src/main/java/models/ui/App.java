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
import models.osm.Way;
import models.parser.Parser;
import models.rendering.WayRenderer;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import models.ui.DrawingUtils;

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

        Parser parser = new Parser("bornholm.osm");
        parser.parse();

        List<Double> bb = parser.getBoundingBox();
        double meanLat = (bb.get(1) + bb.get(3)) / 2.0; // (minLat + maxLat) / 2

        List<Way> ways = new ArrayList<>(parser.getOsmWayMap().values());
        ways.sort(Comparator.comparingDouble(o -> -o.getArea()));
        drawables.add(new WayRenderer(ways, meanLat));

        long nonemptyWays = parser.getOsmWayMap().values().stream().filter(w -> w.getNodes() != null && !w.getNodes().isEmpty()).count();
        System.out.println("Non-empty ways in parser map=" + nonemptyWays);

        // reCenter(new double[]{10, 50, 15, 55}); // Centers around the bounds given
        if(bb.size() == 4){
            reCenter(new double[]{bb.get(0), bb.get(1), bb.get(2), bb.get(3)}, meanLat);
        }

        BorderPane mouseEventComponent = new BorderPane();
        mouseEventComponent.setOnMousePressed(this::handleMousePressed);
        mouseEventComponent.setOnMouseDragged(this::handleMouseDragged);
        mouseEventComponent.setOnScroll(this::handleScroll);

        imageView.setFitWidth(getWIDTH());
        imageView.setFitHeight(getHEIGHT());
        imageView.setPreserveRatio(false);

        StackPane root = new StackPane(this.imageView, mouseEventComponent);
        stage.setScene(new Scene(root, getWIDTH(), getHEIGHT()));
        stage.show();

        System.out.println("Nodes: " + parser.getOsmNodeMap().size());
        System.out.println("Ways: " + parser.getOsmWayMap().size());
        System.out.println("Relations: " + parser.getOsmRelationMap().size());
        System.out.println("Bounding box: " + parser.getBoundingBox());

        // Initial draw and render
        draw();
        render();
    }


    private void draw() {
        Graphics2D gc = getNewGraphicsContext();

        // Clear background in device space first.
        gc.setTransform(new java.awt.geom.AffineTransform());
        gc.setBackground(Color.decode("#a9d3de")); // Blue
        gc.clearRect(0, 0, getWIDTH(), getHEIGHT());

        // Apply world transform for drawing map geometry.
        gc.setTransform(superAffine);

        System.out.println("Drawing " + drawables.size() + " drawables with transform " + superAffine);

        for (Drawable drawable : drawables) {
            drawable.drawForTest(gc);
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

    public void reCenter(double[] bounds, double meanLat) {
        double minLon = bounds[0];
        double minLat = bounds[1];
        double maxLon = bounds[2];
        double maxLat = bounds[3];

        double cosMeanLat = Math.cos(Math.toRadians(meanLat));
        double dataWidth = (maxLon - minLon) * cosMeanLat;
        double dataHeight = maxLat - minLat;
        if (dataWidth <= 0 || dataHeight <= 0) {
            return;
        }

        double scaleX = getWIDTH() / dataWidth;
        double scaleY = getHEIGHT() / dataHeight;
        double scale = Math.min(scaleX, scaleY);

        double mapWidth = dataWidth * scale;
        double mapHeight = dataHeight * scale;
        double offsetX = (getWIDTH() - mapWidth) / 2.0;
        double offsetY = (getHEIGHT() - mapHeight) / 2.0;

        // WayRenderer y is -latitude, so after translating by maxLat it becomes 0..dataHeight
        superAffine.reset()
                .prependTranslation(-minLon * cosMeanLat, maxLat)
                .prependScale(scale, scale)
                .prependTranslation(offsetX, offsetY);
    }
}
