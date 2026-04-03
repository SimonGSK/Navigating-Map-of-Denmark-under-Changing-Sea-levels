package models.ui;

import Interfaces.Drawable;
import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import models.geometry.SuperAffine;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.heightcurve.HeightCurve;
import models.heightcurve.HeightCurveData;
import models.osm.Way;
import models.parser.HCParser;
import models.parser.Parser;
import models.rendering.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import models.ui.DrawingUtils;
import models.parser.MapData;

//import static com.sun.javafx.scene.CameraHelper.project;

public class App extends DrawingApp {
    private double screenX = 0;
    private double screenY = 0;
    private final SuperAffine superAffine = new SuperAffine();
    private final List<Drawable> drawables = new ArrayList<>();

    private static final boolean USE_EXAMPLE_ISLAND = false;
    private static final double SEA_LEVEL = 0.0;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final Color WATER_COLOR = Color.decode("#2b8cbe");

    private final PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer<>(
            WIDTH, HEIGHT,
            IntBuffer.allocate(WIDTH * HEIGHT),
            PixelFormat.getIntArgbPreInstance()
    );

    private final BufferedImage bufferedImage = new BufferedImage(
            WIDTH,
            HEIGHT,
            BufferedImage.TYPE_INT_ARGB
    );

    //private final ImageView imageView = new ImageView();

    @Override
    public void start(Stage stage) {
        if  (USE_EXAMPLE_ISLAND) {
            stage.setTitle("Example ISLAND");
        } else {
            stage.setTitle("Drawing App");
        }
        stage.setResizable(false);
        stage.setWidth(getWIDTH());
        stage.setHeight(getHEIGHT());

        HCParser hcParser = new HCParser("bornholm.hc");
        HeightCurveData hcData = hcParser.parse();
        HeightCurveRenderer hcRender = new HeightCurveRenderer(hcData);

        Parser parser = new Parser("bornholm/bornholm.osm");
        parser.parse();

        List<Double> bb = parser.getBoundingBox();
        double meanLat = (bb.get(1) + bb.get(3)) / 2.0; // (minLat + maxLat) / 2

        MapData mapData = new MapData(parser.getOsmWayMap(), parser.getOsmRelationMap());

        // Alle ways sendes til WayRenderer så kystkurven har adgang til dem
        List<Way> allWays = new ArrayList<>(parser.getOsmWayMap().values());

        //drawables.add(new CoastlineRenderer(allWays, meanLat));                          // 1. Baggrund - landets baggrund
        drawables.add(new RelationRenderer(mapData.multiPolygons, meanLat));             // 2. Relations/multipolygons - skove, søer osv.
        drawables.add(new WayRenderer(mapData.standaloneWays, meanLat));                 // 3. Ways - veje, bygninger

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

        /*
        Graphics2D gc = bufferedImage.createGraphics();
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gc.setBackground(WATER_COLOR);
        gc.fillRect(0, 0, getWIDTH(), getHEIGHT());


        HeightCurveData data;
        if (USE_EXAMPLE_ISLAND) {
           data = ExampleIsland.create();
        } else {
            data = bornholm.create();
        }

        HeightCurve sea = data.sea;
        sea.resetSubmerged();
        sea.submerge(SEA_LEVEL);

        for (HeightCurve curve: data.curves) {
            if (curve == sea) {
                continue;
            }
            gc.setColor(curve.getFillColor(SEA_LEVEL));
            gc.fill(project(curve.getRegionPath(), data));
        }
        for (HeightCurve  curve: data.curves) {
            if (curve == sea) {
                continue;
            }
            gc.setColor(Color.BLACK);
            gc.draw(project(curve.getBoundaryPath(), data));
        }

        int[] pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, pixelBuffer.getBuffer().array(), 0, pixels.length);
        imageView.setImage(new WritableImage(pixelBuffer));

         */


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
            drawable.draws(gc);
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
    /*
    private static Shape project(Shape s, HeightCurveData d) {
        double p = 20, c = Math.cos(Math.toRadians((d.minLat + d.maxLat) / 2));
        double w = (d.maxLon - d.minLon) * c, h = d.maxLat - d.minLat;
        double k = Math.min((WIDTH - 2 * p) / w, (HEIGHT - 2 * p) / h);
        AffineTransform t = new AffineTransform();
        t.translate(p + (WIDTH - 2 * p - w * k) / 2, p + (HEIGHT - 2 * p - h * k) / 2 + h * k);
        t.scale(k, -k);
        t.scale(c, 1);
        t.translate(-d.minLon, -d.minLat);
        return t.createTransformedShape(s);
    }

     */
}
