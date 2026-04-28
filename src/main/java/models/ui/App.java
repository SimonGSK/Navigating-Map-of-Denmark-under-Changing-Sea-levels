package models.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import models.geometry.SuperAffine;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.RTree.SearchResults;
import models.RTree.Tree;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.heightcurve.HeightCurveData;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.parser.*;
import models.rendering.NodeRenderer;
import models.rendering.HeightCurveRenderer;
import models.rendering.RelationRenderer;
import models.rendering.WayRenderer;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.HashMap;

//import static com.sun.javafx.scene.CameraHelper.project;

public class App extends DrawingApp {
    private final SuperAffine superAffine = new SuperAffine();


    private double screenX = 0;
    private double screenY = 0;
    private Tree tree;

    private RelationRenderer relationRenderer;
    private WayRenderer wayRenderer;
    private HeightCurveRenderer hcRenderer;
    private HeightCurveData hcData;
    private NodeRenderer nodeRenderer;
    private double meanLat;
    private Label zoomLabel;
    private boolean showHeightCurves = false;
    private boolean showHeightLines = false;

    private double prevMouseX;
    private double prevMouseY;

    Path2D nearestNeighborPath;


    //private final ImageView imageView = new ImageView();

    @Override
    public void start(Stage stage) {
        StartupDialog dialog =  new StartupDialog();
        StartupDialog.MapChoice choice = dialog.show();
        if (choice == null) {
            Platform.exit();
            return;
        }

        stage.setTitle("Drawing App");

        stage.setResizable(false);
        stage.setWidth(getWIDTH());

        BoundingBox boundingBox;
        HashMap<Long, Node> nodeMap;
        HashMap<Long, Way> wayMap;
        HashMap<Long, Relation> relationMap;

        if (choice.binPath() != null) {
            MapData loadedData = null;
            try {
                loadedData = BinaryReader.load(choice.binPath());
                System.out.println("Loaded from binary");
            } catch (Exception e) {
                System.out.println("Binary not found, parsing instead" + e.getMessage());
            }
            if (loadedData != null) {
                hcData = loadedData.hcData;
                boundingBox = loadedData.mbr;
                nodeMap = (HashMap<Long, Node>) loadedData.nodeMap;
                wayMap = (HashMap<Long, Way>) loadedData.wayMap;
                relationMap = (HashMap<Long, Relation>) loadedData.relationMap;
            } else {
                Parser parser = new Parser(choice.osmPath());
                parser.parse();
                HCParser hcparser = new HCParser(choice.hcPath());
                hcData = hcparser.parse();
                boundingBox = parser.getBoundingBox();
                nodeMap = parser.getOsmNodeMap();
                wayMap = parser.getOsmWayMap();
                relationMap = parser.getOsmRelationMap();

                try {
                    String binWritePath = "src/main/resources/data/" + choice.osmPath().replace(".osm", ".bin");
                    BinaryWriter.write(parser, hcData, binWritePath);
                    System.out.println("Binary written for next startup");
                } catch (Exception ex) {
                    System.out.println("Could not write binary" + ex.getMessage());
                }
            }
        } else {
            Parser parser = new Parser (choice.osmPath());
            parser.parse();
            HCParser hcparser = new HCParser(choice.hcPath());
            hcData = hcparser.parse();
            boundingBox = parser.getBoundingBox();
            nodeMap = parser.getOsmNodeMap();
            wayMap = parser.getOsmWayMap();
            relationMap = parser.getOsmRelationMap();
        }



        tree = new Tree(boundingBox, nodeMap, wayMap, relationMap);

        meanLat = (tree.getMbr().maxLat() + tree.getMbr().minLat()) / 2.0; // (minLat + maxLat) / 2
        relationRenderer = new RelationRenderer(meanLat);
        wayRenderer = new WayRenderer(meanLat);
        nodeRenderer = new NodeRenderer(meanLat);
        hcRenderer = new HeightCurveRenderer(hcData, meanLat);

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

        // TODO: Make this into a separate object or function
        Button toggleButton = new Button("Show elevation map");
        toggleButton.setOnAction(e -> {
            showHeightCurves = !showHeightCurves;
            toggleButton.setText(showHeightCurves ? "Show regular map" : "Show elevation map");
            drawAndRender();
        });

        Button heightLinesButton = new Button("Show height curves");
        heightLinesButton.setOnAction(e -> {
            showHeightLines = !showHeightLines;
            heightLinesButton.setText(showHeightLines ? "Hide height curves" : "Show height curves");
            drawAndRender();
        });

        double maxH = Math.ceil(hcData.getMaxHeight());

        Slider seaSlider = new Slider(0, maxH, 0);
        seaSlider.setShowTickLabels(true);
        seaSlider.setMajorTickUnit(Math.ceil(maxH / 100) * 10);
        seaSlider.setPrefWidth(300);

        Label seaLabel = new Label("Sea level: 0m");

        seaSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double level = newVal.doubleValue();
            seaLabel.setText(String.format("Sea level: %.0fm", level));
            hcData.updateFlooding(level);
            hcRenderer.setSeaLevel(level);
            drawAndRender();
        });

        zoomLabel = new Label("Zoom: 1.00x");

        Button zoomOutButton = new Button("-");
        zoomOutButton.setOnAction(e -> applyZoom(1 / 1.5, getWIDTH() / 2.0, getHEIGHT() / 2.0));

        Button zoomInButton = new Button("+");
        zoomInButton.setOnAction(e -> applyZoom(1.5, getWIDTH() / 2.0, getHEIGHT() / 2.0));

        HBox controls = new HBox(10.0, toggleButton, heightLinesButton, seaLabel, seaSlider, zoomLabel, zoomOutButton, zoomInButton);
        controls.setPadding(new Insets(8));
        controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        BorderPane layout = new BorderPane();
        layout.setCenter(new StackPane(this.imageView, mouseEventComponent));
        layout.setBottom(controls);
        controls.setStyle("-fx-background-color: white;");

        stage.setScene(new Scene(layout, getWIDTH(), getHEIGHT() + 50));
        stage.show();

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


        if (showHeightCurves) {
            hcRenderer.drawHcMap(gc); //Kan også bruge den normale draws(), men denne simple funktion ser bedre ud
        } else {
            // nodeRenderer.draws(gc); // TODO: Implement draws() in NodeRenderer to draw trees, etc.
            relationRenderer.draws(gc);
            wayRenderer.draws(gc);
            hcRenderer.drawSubmersedCurves(gc);

            if(showHeightLines){
                hcRenderer.drawHcLines(gc);
            }
        }
    }

    private void drawAndRender() {
        draw();
        render();
    }

    private void handleMousePressed(MouseEvent event) {
        this.screenX = event.getX();
        this.screenY = event.getY();

        Node nn = tree.getNearestNode(getCursorCoordinate(screenX,screenY));
        System.out.println(nn.getCoordinate().getLat());
        System.out.println(nn.getCoordinate().getLon());

/*        if (Math.abs(this.screenX - event.getX()) < 10 && Math.abs(this.screenY - event.getY()) < 10) {
            Coordinate c = pixelToCoordinate(event.getX(),event.getY());
            Node n = tree.getNearestNode(c);
            if (n != null) {
                System.out.println("nearestNode: lat = " + n.getLat() + ", lon = " + n.getLon() + ", dist = " + Math.round(Math.sqrt(Math.pow(c.getLat() - n.getLat(),2) * Math.pow(c.getLon() - n.getLon(),2))));
            }
        }*/
    }

    private Coordinate getCursorCoordinate(double screenX, double screenY) {
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
        double factor = event.getDeltaY() > 0 ? 1.05 : 1 / 1.05;
        applyZoom(factor, event.getX(), event.getY());
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

        getZoomLevel();
    }

    private void getZoomLevel() {
        double scale = Math.log(superAffine.getScaleX()) / Math.log(2);
        if (zoomLabel != null){
            zoomLabel.setText(String.format("Zoom: %.1fx", scale));
        }
    }

    private void applyZoom(double factor, double x, double y) {
        superAffine
                .prependTranslation(-x, -y)
                .prependScale(factor, factor)
                .prependTranslation(x, y);

        getZoomLevel();
        drawAndRender();
    }
}
