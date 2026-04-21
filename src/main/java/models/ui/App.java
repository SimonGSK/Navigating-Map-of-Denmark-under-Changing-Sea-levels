package models.ui;

import Interfaces.Drawable;
import javafx.scene.Scene;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.RTree.SearchResults;
import models.RTree.Tree;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.geometry.SuperAffine;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.parser.MapData;
import models.parser.Parser;
import models.rendering.NodeRenderer;
import models.rendering.RelationRenderer;
import models.rendering.WayRenderer;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//import static com.sun.javafx.scene.CameraHelper.project;

public class App extends DrawingApp {
    private static final boolean USE_EXAMPLE_ISLAND = false;
    private static final double SEA_LEVEL = 0.0;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final Color WATER_COLOR = Color.decode("#2b8cbe");
    private final SuperAffine superAffine = new SuperAffine();
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

    private double screenX = 0;
    private double screenY = 0;
    private Tree tree;

    private RelationRenderer relationRenderer;
    private WayRenderer wayRenderer;
    private NodeRenderer nodeRenderer;
    private double meanLat;

    private double prevMouseX;
    private double prevMouseY;

    Path2D nearestNeighborPath;


    //private final ImageView imageView = new ImageView();

    @Override
    public void start(Stage stage) {
        if (USE_EXAMPLE_ISLAND) {
            stage.setTitle("Example ISLAND");
        } else {
            stage.setTitle("Drawing App");
        }
        stage.setResizable(false);
        stage.setWidth(getWIDTH());
        stage.setHeight(getHEIGHT());

        /*

        HCParser hcParser = new HCParser("bornholm.hc");
        HeightCurveData hcData = hcParser.parse();
        HeightCurveRenderer hcRender = new HeightCurveRenderer(hcData);
         */

        Parser parser = new Parser("Bornholm.osm");
        parser.parse();

        tree = new Tree(
                parser.getBoundingBox(),
                parser.getOsmNodeMap(),
                parser.getOsmWayMap(),
                parser.getOsmRelationMap()
            );

        meanLat = (tree.getMbr().maxLat() + tree.getMbr().minLat()) / 2.0; // (minLat + maxLat) / 2
        relationRenderer = new RelationRenderer(meanLat);
        wayRenderer = new WayRenderer(meanLat);
        nodeRenderer = new NodeRenderer(meanLat);

/*      TODO: Remove drawables and call draws() on relationRenderer and wayRenderer manually
        // 1. Baggrund - landets baggrund
        drawables.add(relationRenderer);             // 2. Relations/multipolygons - skove, søer osv.
        drawables.add(wayRenderer);                 // 3. Ways - veje, bygninger*/

        reCenter(tree.getMbr(), meanLat);

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

        // Initial draw and render
        draw();
        render();
    }

    private BoundingBox getViewportBox() {
        int w = getWIDTH();
        int h = getHEIGHT();

        Point2D topLeft = superAffine.inverseTransform(w * 0.2, h * 0.2);
        Point2D bottomRight = superAffine.inverseTransform(w * 0.8, h * 0.8);
        double cosMeanLat = Math.cos(Math.toRadians(meanLat));

        double minLon = topLeft.getX() / cosMeanLat;
        double maxLon = bottomRight.getX() / cosMeanLat;
        double maxLat = -topLeft.getY();
        double minLat = -bottomRight.getY();

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    private void draw() {
        BoundingBox viewport = getViewportBox();

        double scaleX = superAffine.getScaleX(); // Simple LOD, setup
        double scaleY = superAffine.getScaleY(); // Simple LOD, setup
        double minGeoArea = 256.0 / (scaleX * scaleY); // Simple LOD, step 1; Elements rendering less than 16x16 px are skipped

        SearchResults searchResults = tree.search(viewport);
        wayRenderer.set(searchResults.wayList());
        relationRenderer.set(searchResults.relationList());

        Graphics2D gc = getNewGraphicsContext();

        // Clear background in device space first.
        gc.setTransform(new java.awt.geom.AffineTransform());
        gc.setBackground(Color.decode("#a9d3de")); // Blue
        gc.clearRect(0, 0, getWIDTH(), getHEIGHT());

        // Apply world transform for drawing map geometry.
        gc.setTransform(superAffine);

        // nodeRenderer.draws(gc); // TODO: Implement draws() in NodeRenderer to draw trees, etc.
        relationRenderer.draws(gc);
        wayRenderer.draws(gc);
    }

    private void drawAndRender() {
        draw();
        render();
    }

    private void handleMousePressed(MouseEvent event) {
        this.screenX = event.getX();
        this.screenY = event.getY();

/*        if (Math.abs(this.screenX - event.getX()) < 10 && Math.abs(this.screenY - event.getY()) < 10) {
            Coordinate c = pixelToCoordinate(event.getX(),event.getY());
            Node n = tree.getNearestNode(c);
            if (n != null) {
                System.out.println("nearestNode: lat = " + n.getLat() + ", lon = " + n.getLon() + ", dist = " + Math.round(Math.sqrt(Math.pow(c.getLat() - n.getLat(),2) * Math.pow(c.getLon() - n.getLon(),2))));
            }
        }*/
    }

    private Coordinate pixelToCoordinate(double screenX, double screenY) {
        Point2D world = superAffine.inverseTransform(screenX, screenY);

        double cosMeanLat = Math.cos(Math.toRadians(meanLat));
        double lon = world.getX() / cosMeanLat;
        double lat = -world.getY();

        return new Coordinate(lat,lon);
    }

    private void handleMouseDragged(MouseEvent event) {
        double dx = event.getX() - this.screenX, dy = event.getY() - this.screenY;
        superAffine.prependTranslation(dx, dy);
        handleMousePressed(event);

        drawAndRender();
    }

    private void handleScroll(ScrollEvent event) {
        double zoom = event.getDeltaY() > 0 ? 1.05 : 1 / 1.05;
        superAffine
                .prependTranslation(-event.getX(), -event.getY())
                .prependScale(zoom, zoom)
                .prependTranslation(event.getX(), event.getY());

        drawAndRender();
    }

    public void reCenter(BoundingBox mbr, double meanLat) {
        double cosMeanLat = Math.cos(Math.toRadians(meanLat));
        double dataWidth = (mbr.maxLon() - mbr.minLon()) * cosMeanLat;
        double dataHeight = mbr.maxLat() - mbr.minLat();
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
                .prependTranslation(-mbr.minLon() * cosMeanLat, mbr.maxLat())
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
