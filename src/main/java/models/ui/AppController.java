package models.ui;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import models.RTree.SearchResults;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.geometry.ExtSuperAffine;
import models.heightcurve.HeightCurve;
import models.osm.Node;
import models.pathfinding.Pathfinder;
import models.pathfinding.PathfindingObject;
import models.rendering.GraphicsRenderer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

public class AppController extends DrawingApp {
    private final ExtSuperAffine superAffine = new ExtSuperAffine();
    private final AppSettings appSettings = AppSettings.getInstance();
    private final AppData appData = new AppData();
    private final EventHandler eventHandler = new EventHandler();
    private final UserInterface userInterface = new UserInterface(this);
    private AppControllerState controllerState = AppControllerState.ready;
    private final Pathfinder pathfinder = new Pathfinder();

    private PathfindingObject pathfindingObject = PathfindingObject.getInstance();
    private final Path2D pathToNearestNode = new Path2D.Double();

    private final GraphicsRenderer graphicsRenderer = new GraphicsRenderer(this);

    private double seaLevel = 0;

    private double prevMouseX = 0;
    private double prevMouseY = 0;

    public AppSettings getAppSettings() {
        return appSettings;
    }

    private enum AppControllerState {
        ready,
        active,
        error
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    @Override
    public void start(Stage stage) {
        StartupDialog dialog = new  StartupDialog(); // TODO: Migrate StartUp dialog into the userInterface
        StartupDialog.MapChoice mapChoice = dialog.show();
        if (mapChoice == null) {
            Platform.exit();
            return;
        }
        if (mapChoice.binPath() != null) {
            appData.loadFromBinary(mapChoice.binPath());
        }
        if (appData.getState() !=  AppData.AppDataState.complete) {
            if (mapChoice.hcPath() != null) {
                appData.parse(mapChoice.osmPath(), mapChoice.hcPath());
            } else {
                appData.parse(mapChoice.osmPath());
            }
        }
        stage.setTitle("Drawing App");
        stage.setResizable(true);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds(); //Sets the startup window's size to 80% of the screen's dimensions
        double w = screenBounds.getWidth() * 0.8;
        double h = screenBounds.getHeight() * 0.8;
        stage.setWidth(w);
        stage.setHeight(h);

        switch (appData.getState()) {
            case AppData.AppDataState.complete -> {
                controllerState = AppControllerState.active;
                graphicsRenderer.init();
                userInterface.init();
            }
            case AppData.AppDataState.error -> controllerState = AppControllerState.error;
        }

        if (controllerState == AppControllerState.error) {
            System.out.println("Error parsing OSM and HC files. Shutting down.");
            return; // TODO: Implement better error handling so user can try again
        }

        Scene scene = new Scene(userInterface.getLayout(), w, h);
        userInterface.getLayout().prefWidthProperty().bind(scene.widthProperty());
        userInterface.getLayout().prefHeightProperty().bind(scene.heightProperty());

        eventHandler.initKeyboardEventComponent(scene, this::handleKeyPress);
        eventHandler.initMapMouseEventComponent(this::handleMousePress,
                                                this::handleMouseClick,
                                                this::handleMouseDrag,
                                                this::handleMouseMove,
                                                this::handleScroll);

        stage.setScene(scene);
        stage.show();

        imageView.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            int width = (int) newBounds.getWidth();
            int height = (int) newBounds.getHeight();
            if (width > 0 && height > 0) {
                resize(width, height);
                handleDraw();
            }
        });

        handleRecenter(appData.getBounds(), appData.getMeanLat());
        appSettings.setUserMode(AppSettings.UserMode.explore);

        // Initial draw and render
        handleDraw();
    }

    public ExtSuperAffine getSuperAffine() {
        return superAffine;
    }

    private BoundingBox getViewportBox() {
        int w = getWIDTH();
        int h = getHEIGHT();

        Point2D topLeft = superAffine.inverseTransform(appSettings.isViewportDebug() ? w * 0.2 : 0,
                                                       appSettings.isViewportDebug() ? h * 0.2 : 0);
        Point2D bottomRight = superAffine.inverseTransform(appSettings.isViewportDebug() ? w * 0.8 : w,
                                                           appSettings.isViewportDebug() ? h * 0.8 : h);
        double cosMeanLat = Math.cos(Math.toRadians(appData.getMeanLat()));

        double minLon = topLeft.getX() / cosMeanLat;
        double maxLon = bottomRight.getX() / cosMeanLat;
        double maxLat = -topLeft.getY();
        double minLat = -bottomRight.getY();

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    public AppData getAppData() {
        return appData;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    private void draw() {
        BoundingBox viewport = getViewportBox();

        double zoom = getZoomLevel();
        appData.getTree().setZoomLevel(zoom);

        SearchResults searchResults = appData.getTree().search(viewport);
        appData.getWayRenderer().set(searchResults.wayList());
        appData.getRelationRenderer().set(searchResults.relationList());

        appData.getWayRenderer().setCurrentZoomLevel(zoom);
        appData.getRelationRenderer().setCurrentZoomLevel(zoom);

        Graphics2D gc = getNewGraphicsContext();

        // Clear background in device space first.
        gc.setTransform(new java.awt.geom.AffineTransform());
        gc.setBackground(Color.decode("#a9d3de")); // Blue
        gc.clearRect(0, 0, getWIDTH(), getHEIGHT());

        // Apply world transform for drawing map geometry.
        gc.setTransform(superAffine);

        if (appData.getHeightCurveRenderer() == null || appSettings.getMapState() == AppSettings.MapState.osm) {
            appData.getRelationRenderer().draws(gc);
            appData.getWayRenderer().draws(gc);
        }
        if (appData.getHeightCurveRenderer() != null) {
            List<HeightCurve> list = appData.getHeightCurveData().search(viewport);

            appData.getHeightCurveRenderer().set(
                    list
            );
            appData.getHeightCurveRenderer().draws(gc);
        };

        if (appSettings.getUserMode() == (AppSettings.UserMode.select)) {
            double zoomScale = superAffine.getScaleX();
            float strokeWidth = (float)(2.0 / zoomScale); // 2px on screen at all zoom levels
            gc.setColor(Color.decode("#FF1DCE"));
            gc.setStroke(new BasicStroke(strokeWidth));
            gc.draw(pathToNearestNode);
        }
        graphicsRenderer.draws(gc);

        // Draw filled circles at start and end nodes
        if (pathfindingObject.getStartNode() != null) {
            Ellipse2D.Double startCircle = getNodeCircle(pathfindingObject.getStartNode());
            gc.setColor(Color.BLUE);
            gc.fill(startCircle);
        }

        if (pathfindingObject.getEndNode() != null) {
            Ellipse2D.Double endCircle = getNodeCircle(pathfindingObject.getEndNode());
            gc.setColor(Color.RED);
            gc.fill(endCircle);
        }

        //Viewport debug square to show the boundaries of the viewport
        if (appSettings.isViewportDebug()) {
            gc.setTransform(new java.awt.geom.AffineTransform());
            int w = getWIDTH();
            int h = getHEIGHT();
            int rectX      = (int)(w * 0.2);
            int rectY      = (int)(h * 0.2);
            int rectWidth  = (int)(w * 0.6);
            int rectHeight = (int)(h * 0.6);

            gc.setColor(Color.RED);
            gc.setStroke(new BasicStroke(2f));
            gc.drawRect(rectX, rectY, rectWidth, rectHeight);
        }
    }

    private Ellipse2D.Double getNodeCircle(Node pathfindingObject) {
        Coordinate node = pathfindingObject.getCoordinate();
        double cosMeanLat = Math.cos(Math.toRadians(appData.getMeanLat()));
        double startWorldX = node.getLon() * cosMeanLat;
        double startWorldY = -node.getLat();

        double zoomScale = superAffine.getScaleX();
        double radiusPixels = 8.0;
        double radius = radiusPixels / zoomScale;

        return new Ellipse2D.Double(
                startWorldX - radius,
                startWorldY - radius,
                radius * 2,
                radius * 2
        );
    }

    private void handleMousePress(MouseEvent event) {
        this.prevMouseX = event.getX();
        this.prevMouseY = event.getY();
    }

    private Coordinate getCursorCoordinate(double screenX, double screenY) {
        Point2D world = superAffine.inverseTransform(screenX, screenY);

        double cosMeanLat = Math.cos(Math.toRadians(appData.getMeanLat()));
        double lon = world.getX() / cosMeanLat;
        double lat = -world.getY();

        return new Coordinate(lat,lon);
    }

    private void handleMouseClick(MouseEvent event) {
        if (event.isStillSincePress()) {
            if (appSettings.getUserMode() == AppSettings.UserMode.select) {
                if (pathfindingObject == null) {
                    pathfindingObject = PathfindingObject.getInstance();
                }

                if (pathfindingObject.isReady()) {
                    pathfindingObject.clear();
                }

                Coordinate cursor = getCursorCoordinate(prevMouseX, prevMouseY);

                Node node = appData.getTree().getNearestNode(cursor);

                if (node == null) {
                    return;
                }

                if (pathfindingObject.getStartNode() == null) {
                    pathfindingObject.setStartNode(node);
                    return;
                }

                if (pathfindingObject.getStartNode().equals(node)) {
                    return;
                }

                if (pathfindingObject.getEndNode() == null) {
                    pathfindingObject.setEndNode(node);
                }

                if (pathfindingObject.isReady()) {
                    appSettings.setUserMode(AppSettings.UserMode.explore); /**/
                    pathfindingObject.updatePath();
                    handleDraw();
                }
                System.out.println(pathfindingObject.toString());
            }
        }
    }

    public PathfindingObject getPathfindingObject() {
        return pathfindingObject;
    }

    private void handleMouseMove(MouseEvent event) {
        if (!(appSettings.getUserMode() == AppSettings.UserMode.select)) {
            return;
        }

        Coordinate cursor = getCursorCoordinate(event.getX(),event.getY());
        Node nearestNode = appData.getTree().getNearestNode(cursor);

        if (nearestNode == null) {
            return;
        }

        double cosMeanLat = Math.cos(Math.toRadians(appData.getMeanLat()));

        double cursorWorldX = cursor.getLon() * cosMeanLat;
        double cursorWorldY = -cursor.getLat();

        double nodeWorldX = nearestNode.getCoordinate().getLon() * cosMeanLat;
        double nodeWorldY = -nearestNode.getCoordinate().getLat();

        pathToNearestNode.reset();
        pathToNearestNode.moveTo(cursorWorldX,cursorWorldY);
        pathToNearestNode.lineTo(nodeWorldX,nodeWorldY);

        handleDraw();
    }

    private void handleMouseDrag(MouseEvent event) {
        double dx = event.getX() - this.prevMouseX;
        double dy = event.getY() - this.prevMouseY;

        superAffine.prependTranslation(dx, dy);

        this.prevMouseX = event.getX();
        this.prevMouseY = event.getY();

        handleDraw();
    }

    private void handleScroll(ScrollEvent event) {
        double factor = event.getDeltaY() > 0 ? 1.05 : 1 / 1.05;
        handleZoom(factor, event.getX(), event.getY());
    }

    private void handleKeyPress(KeyEvent event) {
        System.out.println("Key pressed!");
        System.out.println(event.getCode());
        switch (event.getCode()) {
            case ESCAPE -> {
                appSettings.setUserMode(AppSettings.UserMode.explore);
            }
            case S -> {
                pathfindingObject.clear();
                if (appSettings.isPathfindingDebug()) appSettings.setPathfindingDebug(false);
                appSettings.setUserMode(AppSettings.UserMode.select);
            }
            case V -> {
                appSettings.setViewportDebug(!appSettings.isViewportDebug());
            }
            case B -> {
                appSettings.setBoundingBoxDebug(!appSettings.isBoundingBoxDebug());
            }
            case P -> {
                if (pathfindingObject.isReady() && pathfindingObject.getPath() != null) {
                    appSettings.setPathfindingDebug(!appSettings.isPathfindingDebug());
                }
            }
        }
        handleDraw();
    }

    public void handleDraw() {
        draw();
        render();
    }

    public double getSeaLevel() {
        return seaLevel;
    }

    public void updateSeaLevel(float seaLevel) {
        if (appData.getHeightCurveData() == null) {
            return;
        }

        this.seaLevel = seaLevel;
        appData.getHeightCurveData().updateFlooding(seaLevel);
        appData.getHeightCurveRenderer().setSeaLevel(seaLevel);
        pathfindingObject.updatePath();
        handleDraw();
    }

    public void handleRecenter(BoundingBox mbr, double meanLat) {
        double cosMeanLat = Math.cos(Math.toRadians(meanLat));
        double dataWidth = (mbr.maxLon() - mbr.minLon()) * cosMeanLat;
        double dataHeight = mbr.maxLat() - mbr.minLat();
        if (dataWidth <= 0 || dataHeight <= 0) {
            return;
        }
        int usableHeight = getHEIGHT() - 80;
        int usableWidth = getWIDTH();

        double scaleX = getWIDTH() / dataWidth;
        double scaleY = getHEIGHT() / dataHeight;
        double scale = Math.min(scaleX, scaleY);

        double mapWidth = dataWidth * scale;
        double mapHeight = dataHeight * scale;
        double offsetX = (usableWidth - mapWidth) / 2.0;
        double offsetY = ((usableHeight - mapHeight) / 2.0) + 40;

        // WayRenderer y is -latitude, so after translating by maxLat it becomes 0..dataHeight
        superAffine.reset()
                .prependTranslation(-mbr.minLon() * cosMeanLat, mbr.maxLat())
                .prependScale(scale, scale)
                .prependTranslation(offsetX, offsetY);

        getZoomLevel();
    }

    public void handleZoom(double factor, double x, double y) {
        superAffine
                .prependTranslation(-x, -y)
                .prependScale(factor, factor)
                .prependTranslation(x, y);
        handleDraw();
    }

    public double getZoomLevel() {
        return Math.log(superAffine.getScaleX()) / Math.log(2);
    };
}
