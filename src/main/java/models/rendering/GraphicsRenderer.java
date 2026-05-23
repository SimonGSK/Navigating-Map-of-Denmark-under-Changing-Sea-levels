package models.rendering;

import models.geometry.BoundingBox;
import models.osm.Node;
import models.pathfinding.PathfindingObject;
import models.ui.AppController;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Set;

/**
 * Draws overlay graphics such as routes and debug layers.
 */
public class GraphicsRenderer {
    private final AppController appController;
    private PathfindingObject pathfindingObject;
    private double cosMeanLat;

    /**
     * @param appController controller with app state and data
     */
    public GraphicsRenderer(AppController appController) {
        this.appController = appController;
    }

    /**
     * Initializes cached references used during drawing.
     */
    public void init() {
        this.pathfindingObject = appController.getPathfindingObject();
        this.cosMeanLat = Math.cos(Math.toRadians(appController.getAppData().getMeanLat()));
    }

    /**
     * Builds a path from the current pathfinding result.
     * @param nodes ignored; uses pathfinding result
     * @return path or null
     */
    private Path2D buildPath(List<Node> nodes) {
        if (!pathfindingObject.isReady() || pathfindingObject.getPath() == null) {
            return null;
        }

        Path2D path = new Path2D.Double();

        boolean isFirst = true;
        for (Node n : pathfindingObject.getPath()) {
            if (n == null) {
                continue;
            }
            double worldX = n.getLon() * cosMeanLat;
            double worldY = -n.getCoordinate().getLat();

            if (isFirst) {
                path.moveTo(worldX, worldY);
                isFirst = false;
                continue;
            }

            path.lineTo(worldX, worldY);
        }

        return path;
    }

    /**
     * Draws overlays such as route and debug markers.
     * @param gc graphics context
     */
    public void draws(Graphics2D gc) {
        // Draw pathfinding route
        if (pathfindingObject.isReady() && pathfindingObject.getPath() != null) {
            double zoomScale = appController.getSuperAffine().getScaleX();
            float strokeWidth = (float)(3.0 / zoomScale); // 3px on screen at all zoom levels
            gc.setColor(Color.decode("#FF1DCE"));
            gc.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            gc.draw(buildPath(pathfindingObject.getPath()));
        }
        if(appController.getAppSettings().isBoundingBoxDebug()) {
            List<BoundingBox> allBoundingBoxes = appController.getAppData().getTree().getMBRList();
            for (BoundingBox bbox : allBoundingBoxes) {
                drawBoundingBoxDebug(gc, bbox, Color.RED);
            }
        }
        if(appController.getAppSettings().isPathfindingDebug()) {
            Set<Node> visited = appController.getPathfindingObject().getVisited();
            if (visited != null) {
                double zoomScale = appController.getSuperAffine().getScaleX();
                double pixelSize = Math.max(2.0, zoomScale * 0.00005); // grows with zoom, never below 2px
                double radius = pixelSize / zoomScale;
                gc.setColor(Color.ORANGE);
                for (Node node : visited) {
                    double worldX = node.getLon() * cosMeanLat;
                    double worldY = -node.getCoordinate().getLat();
                    Ellipse2D.Double circle = new Ellipse2D.Double(worldX - radius, worldY - radius, radius * 2, radius * 2);
                    gc.fill(circle);
                }
            }
        }

    }

    /**
     * Draws a bounding box for debug overlays.
     * @param gc graphics context
     * @param bbox bounding box
     * @param color stroke color
     */
    private void drawBoundingBoxDebug(Graphics2D gc, BoundingBox bbox, Color color) {
        if (bbox == null) {
            return;
        }

        gc.setColor(color);
        gc.setStroke(new BasicStroke(0.0001f));

        Path2D path = new Path2D.Double();

        // Transform coordinates: lon * cosMeanLat, -lat
        double x1 = bbox.minLon() * cosMeanLat;
        double y1 = -bbox.maxLat();
        double x2 = bbox.maxLon() * cosMeanLat;
        double y2 = -bbox.minLat();

        // Draw rectangle
        path.moveTo(x1, y1);
        path.lineTo(x2, y1);
        path.lineTo(x2, y2);
        path.lineTo(x1, y2);
        path.closePath();

        gc.draw(path);
    }
}
