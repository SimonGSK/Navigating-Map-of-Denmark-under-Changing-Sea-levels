package models.rendering;
import Interfaces.Drawable;
import models.osm.Node;
import models.osm.Way;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WayRenderer implements Drawable {
    private static final double MAX_WAY_SPAN = 0.7;
    private static final double MAX_SEGMENT_LENGTH = 0.25;

    private final Collection<Way> ways;
    private final double cosMeanLat;

    public WayRenderer(Collection<Way> ways, double meanLat) {
        this.ways = ways;
        this.cosMeanLat = Math.cos(Math.toRadians(meanLat));
    }

    private boolean shouldDrawWay(Way way) {
        var tags = way.getTags();
        if (tags == null || tags.isEmpty()) return false;
        if (way.shouldNotDraw()) return false;

        //Har lige udkommenteret dette for at tjekke hvad vi mangler at farve
        return true; //hasAnyTag(tags, "highway", "building", "waterway", "landuse", "natural", "leisure", "amenity", "barrier", "aeroway");
    }

    private boolean hasAnyTag(java.util.Map<String, String> tags, String... keys) {
        for (String key : keys) {
            if (tags.containsKey(key)) return true;
        }
        return false;
    }

    private Path2D buildPathFromNodes(List<Node> nodes, boolean closePath, double cosMeanLat) {
        if (nodes == null || nodes.isEmpty()) return null;

        Path2D path = new Path2D.Double();

        boolean first = true;
        for (Node node : nodes) {
            if (node == null) continue;
            double x = node.getLon() * cosMeanLat;
            double y = -node.getCoordinate().getLat();
            if (first) {
                path.moveTo(x, y);
                first = false;
            } else {
                path.lineTo(x, y);
            }
        }
        if (closePath) path.closePath();
        return path;
    }

    @Override
    public void draws(Graphics2D gc) {

        int drawnWays = 0;
        int totalWays = 0;

        for(Way way : ways){
            if (!shouldDrawWay(way)) continue;
            totalWays++;
            Path2D path = buildPath(way);
            gc.setColor(way.getColor());
            if (path == null) continue;
            drawnWays++;

            if (way.getNodes().getFirst().getId() != way.getNodes().getLast().getId()){
                gc.setStroke(new BasicStroke(0.0001f));
                gc.draw(path);
            } else{
                gc.setStroke(new BasicStroke(0));
                gc.fill(path);
            }
        }

        System.out.println("WayRenderer: total ways=" + totalWays + ", drawn ways=" + drawnWays);
    }

    private Path2D buildPath(Way way) {
        return buildPathFromNodes(way.getNodes(), false, cosMeanLat);
    }
}
