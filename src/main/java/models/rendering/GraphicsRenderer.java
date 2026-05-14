package models.rendering;

import Interfaces.Drawable;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.osm.Node;
import models.pathfinding.PathfindingObject;
import models.ui.AppController;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphicsRenderer implements Drawable {
    private final AppController appController;
    private PathfindingObject pathfindingObject;
    private double cosMeanLat;

    public GraphicsRenderer(AppController appController) {
        this.appController = appController;
    }

    public void init() {
        this.pathfindingObject = appController.getPathfindingObject();
        this.cosMeanLat = Math.cos(Math.toRadians(appController.getAppData().getMeanLat()));
    }

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

    @Override
    public void draws(Graphics2D gc) {
        // Draw pathfinding route
        if (pathfindingObject.isReady() && pathfindingObject.getPath() != null) {
            gc.setColor(Color.decode("#FF1DCE"));
            gc.setStroke(new BasicStroke(0.0003f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            gc.draw(buildPath(pathfindingObject.getPath()));
        }
        if(appController.getUserInterface().isBoundingBoxDebug()) {
            List<BoundingBox> allBoundingBoxes = appController.getAppData().getTree().getMBR();
            for (BoundingBox bbox : allBoundingBoxes) {
                drawBoundingBoxDebug(gc, bbox, Color.RED);
            }
        }
        if(appController.getUserInterface().isPathfindingDebug()) {
            Set<Node> visited = appController.getPathfindingObject().getVisited();
            if (visited != null) {
                gc.setColor(Color.MAGENTA);
                for (Node node : visited) {
                    double worldX = node.getLon() * cosMeanLat;
                    double worldY = -node.getCoordinate().getLat();
                    Ellipse2D.Double circle = new Ellipse2D.Double(worldX - 0.00015f, worldY - 0.00015f, 0.0003f, 0.0003f);
                    gc.fill(circle);
                }
            }
        }

    }

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
