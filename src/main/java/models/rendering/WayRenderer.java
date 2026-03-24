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
    private final List<Path2D> coastlineFillPaths;

    public WayRenderer(Collection<Way> ways, double meanLat) {
        this.ways = ways;
        this.cosMeanLat = Math.cos(Math.toRadians(meanLat));
        this.coastlineFillPaths = buildCoastlineFillPaths();
    }

    private boolean shouldDrawWay(Way way) {
        var tags = way.getTags();
        if (tags == null || tags.isEmpty()) return false;
        if (isGeometryOutlier(way)) return false;
        if (isFerryLike(tags)) return false;
        if (isInfrastructureRoute(tags)) return false;

        return hasAnyTag(tags, "highway", "building", "waterway", "landuse", "natural", "leisure");
    }

    private boolean isFerryLike(java.util.Map<String, String> tags) {
        return "ferry".equals(tags.get("route"))
                || "ferry".equals(tags.get("disused:route"))
                || "ferry_route".equals(tags.get("seamark:type"))
                || tags.containsKey("seamark:ferry_route:category")
                || tags.containsKey("ferry");
    }

    private boolean isInfrastructureRoute(java.util.Map<String, String> tags) {
        return "power".equals(tags.get("route"))
                || tags.containsKey("boundary")
                || tags.containsKey("aerialway");
    }

    private boolean hasAnyTag(java.util.Map<String, String> tags, String... keys) {
        for (String key : keys) {
            if (tags.containsKey(key)) return true;
        }
        return false;
    }

    private List<Path2D> buildCoastlineFillPaths() {
        List<List<Node>> coastlineSegments = new ArrayList<>();
        for (Way way : ways) {
            var tags = way.getTags();
            if (tags == null) continue;
            if (!"coastline".equals(tags.get("natural"))) continue;
            if (way.getNodes() == null || way.getNodes().size() < 2) continue;
            coastlineSegments.add(new ArrayList<>(way.getNodes()));
        }

        List<Path2D> fills = new ArrayList<>();
        while (!coastlineSegments.isEmpty()) {
            List<Node> ring = new ArrayList<>(coastlineSegments.remove(0));
            boolean grew = true;

            while (grew && !isClosedRing(ring)) {
                grew = false;
                Node ringEnd = lastNode(ring);
                if (ringEnd == null) break;

                Iterator<List<Node>> it = coastlineSegments.iterator();
                while (it.hasNext()) {
                    List<Node> segment = it.next();
                    Node segStart = firstNode(segment);
                    Node segEnd = lastNode(segment);
                    if (segStart == null || segEnd == null) {
                        it.remove();
                        continue;
                    }

                    if (ringEnd.getId() == segStart.getId()) {
                        appendWithoutDuplicateStart(ring, segment);
                        it.remove();
                        grew = true;
                        break;
                    }
                    if (ringEnd.getId() == segEnd.getId()) {
                        Collections.reverse(segment);
                        appendWithoutDuplicateStart(ring, segment);
                        it.remove();
                        grew = true;
                        break;
                    }
                }
            }

            if (!isClosedRing(ring)) continue;
            Path2D path = buildPathFromNodes(ring, true);
            if (path != null) fills.add(path);
        }

        return fills;
    }

    private Path2D buildPathFromNodes(List<Node> nodes, boolean closePath) {
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

    private Node firstNode(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;
        return nodes.get(0);
    }

    private Node lastNode(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;
        return nodes.get(nodes.size() - 1);
    }

    private boolean isClosedRing(List<Node> nodes) {
        if (nodes == null || nodes.size() < 3) return false;
        Node first = firstNode(nodes);
        Node last = lastNode(nodes);
        return first != null && last != null && first.getId() == last.getId();
    }

    private void appendWithoutDuplicateStart(List<Node> target, List<Node> source) {
        for (int i = 1; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    private boolean isGeometryOutlier(Way way) {
        if (way.getNodes() == null || way.getNodes().size() < 2) return true;
        Node prev = null;
        double maxSegmentLengthSquared = MAX_SEGMENT_LENGTH * MAX_SEGMENT_LENGTH;
        double localMinX = Double.POSITIVE_INFINITY;
        double localMaxX = Double.NEGATIVE_INFINITY;
        double localMinY = Double.POSITIVE_INFINITY;
        double localMaxY = Double.NEGATIVE_INFINITY;

        for (Node node : way.getNodes()) {
            if (node == null) continue;
            double x = node.getLon() * cosMeanLat;
            double y = -node.getCoordinate().getLat();
            localMinX = Math.min(localMinX, x);
            localMaxX = Math.max(localMaxX, x);
            localMinY = Math.min(localMinY, y);
            localMaxY = Math.max(localMaxY, y);

            if (prev != null) {
                double x1 = prev.getLon() * cosMeanLat;
                double y1 = -prev.getCoordinate().getLat();
                double dx = x - x1;
                double dy = y - y1;
                double segmentLengthSquared = dx * dx + dy * dy;
                if (segmentLengthSquared > maxSegmentLengthSquared) {
                    return true;
                }
            }
            prev = node;
        }
        return (localMaxX - localMinX) > MAX_WAY_SPAN || (localMaxY - localMinY) > MAX_WAY_SPAN;
    }

    @Override
    public void drawForTest(Graphics2D gc) {
        if (!coastlineFillPaths.isEmpty()) {
            gc.setColor(Color.decode("#f5f0e1"));
            for (Path2D fillPath : coastlineFillPaths) {
                gc.fill(fillPath);
            }
        }

        int drawnWays = 0;
        int totalWays = 0;

        for(Way way : ways){
            if (!shouldDrawWay(way)) continue;
            totalWays++;
            Path2D path = buildPath(way);
            gc.setColor(way.getColor()); //Uses way's getColor() method to determine the color based on its tags
            if (path == null) continue;
            drawnWays++;
            //gc.draw(path);

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
        return buildPathFromNodes(way.getNodes(), false);
    }
}
