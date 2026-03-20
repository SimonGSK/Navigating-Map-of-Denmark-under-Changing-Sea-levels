package models.rendering;
import Interfaces.Drawable;
import models.osm.Node;
import models.osm.Way;

import java.awt.geom.Path2D;
import java.util.*;

import java.awt.*;

public class WayRenderer implements Drawable {
    private final Collection<Way> ways;

    public WayRenderer(Collection<Way> ways){
        this.ways = ways;
    }

    @Override
    public void drawForTest(Graphics2D gc, Color color, float strokeWidth) {
        gc.setColor(color);
        if (strokeWidth != 0.0f) {
            gc.setStroke(new BasicStroke(strokeWidth));
        }

        int drawnWays = 0;
        int totalWays = 0;

        for(Way way : ways){
            totalWays++;
            Path2D path = buildPath(way);
            if (path == null) continue;
            drawnWays++;
            gc.draw(path);
        }

        System.out.println("WayRenderer: total ways=" + totalWays + ", drawn ways=" + drawnWays);
    }

    private Path2D buildPath(Way way) {
        if (way.getNodes() == null || way.getNodes().isEmpty()) return null;

        Path2D path = new Path2D.Double();
        boolean first = true;

        for (Node node : way.getNodes()) {
            if (node == null) continue;

            // Lon = x, Lat = y (men inverteret fordi y-aksen peger nedad)
            double x = node.getCoordinate().getLon();
            double y = -node.getCoordinate().getLat(); // negativ fordi skærm-y er omvendt

            if (first) {
                path.moveTo(x, y);
                first = false;
            } else {
                path.lineTo(x, y);
            }
        }
        return path;
    }
}
