package models.rendering;
import Interfaces.Drawable;
import models.osm.Node;
import models.osm.Way;

import java.awt.geom.Path2D;
import java.util.*;

import java.awt.*;

public class WayRenderer implements Drawable {
    private final Collection<Way> ways;
    private final double meanLat;
    private final double cosMeanLat;

    public WayRenderer(Collection<Way> ways, double meanLat){
        this.ways = ways;
        this.meanLat = meanLat;
        this.cosMeanLat = Math.cos(Math.toRadians(meanLat));
    }

    @Override
    public void drawForTest(Graphics2D gc) {

        gc.setStroke(new BasicStroke(0.0001f));

        int drawnWays = 0;
        int totalWays = 0;

        for(Way way : ways){
            totalWays++;
            Path2D path = buildPath(way);
            gc.setColor(way.getColor()); //Uses way's getColor() method to determine the color based on its tags
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
            double x = node.getLon() * cosMeanLat;
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
