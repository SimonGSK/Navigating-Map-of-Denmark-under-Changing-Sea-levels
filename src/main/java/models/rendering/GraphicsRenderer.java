package models.rendering;

import Interfaces.Drawable;
import models.osm.Node;
import models.parser.AbstractRenderer;
import models.pathfinding.PathfindingObject;
import models.ui.AppController;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

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
    }
}
