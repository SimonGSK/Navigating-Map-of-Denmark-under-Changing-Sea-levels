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
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public WayRenderer(Collection<Way> ways, double meanLat) {
        this.ways = ways;
        this.meanLat = meanLat;
        this.cosMeanLat = Math.cos(Math.toRadians(meanLat));

        double localMinX = Double.POSITIVE_INFINITY;
        double localMaxX = Double.NEGATIVE_INFINITY;
        double localMinY = Double.POSITIVE_INFINITY;
        double localMaxY = Double.NEGATIVE_INFINITY;
        for (Way way : ways) {
            if (!isLandWay(way)) continue;
            if (way.getNodes() == null) continue;
            for (Node node : way.getNodes()) {
                if (node == null) continue;
                double x = node.getLon() * cosMeanLat;
                double y = -node.getCoordinate().getLat();
                localMinX = Math.min(localMinX, x);
                localMaxX = Math.max(localMaxX, x);
                localMinY = Math.min(localMinY, y);
                localMaxY = Math.max(localMaxY, y);
            }
        }
        this.minX = localMinX;
        this.maxX = localMaxX;
        this.minY = localMinY;
        this.maxY = localMaxY;
    }

    private boolean isLandWay(Way way) {
        var tags = way.getTags();
        if (tags == null || tags.isEmpty()) return false;

        if ("ferry".equals(tags.get("route"))
                || "ferry".equals(tags.get("disused:route"))
                || "ferry_route".equals(tags.get("seamark:type"))
                || tags.containsKey("seamark:ferry_route:category")
                || tags.containsKey("ferry")) {
            return false;
        }

        return tags.containsKey("highway")
                || tags.containsKey("building")
                || tags.containsKey("landuse")
                || tags.containsKey("natural")
                || tags.containsKey("leisure");
    }

    private boolean shouldDrawWay(Way way) {
        var tags = way.getTags();
        if (tags == null || tags.isEmpty()) return false;

        if ("ferry".equals(tags.get("route"))
                || "ferry".equals(tags.get("disused:route"))
                || "ferry_route".equals(tags.get("seamark:type"))
                || tags.containsKey("seamark:ferry_route:category")
                || tags.containsKey("ferry")
                || "power".equals(tags.get("route"))
                || tags.containsKey("boundary")
                || tags.containsKey("aerialway")) {
            return false;
        }

        return tags.containsKey("highway")
                || tags.containsKey("building")
                || tags.containsKey("waterway")
                || tags.containsKey("landuse")
                || tags.containsKey("natural")
                || tags.containsKey("leisure");
    }

    @Override
    public void drawForTest(Graphics2D gc) {
        if (Double.isFinite(minX) && Double.isFinite(maxX) && Double.isFinite(minY) && Double.isFinite(maxY)) {
            gc.setColor(Color.decode("#e7e9e8"));
            gc.fill(new Rectangle.Double(minX, minY, maxX - minX, maxY - minY));
        }

        gc.setStroke(new BasicStroke(0.0001f));

        int drawnWays = 0;
        int totalWays = 0;

        for(Way way : ways){
            if (!shouldDrawWay(way)) continue;
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
