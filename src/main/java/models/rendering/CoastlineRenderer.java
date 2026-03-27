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

public class CoastlineRenderer implements Drawable {
    private final List<Path2D> coastlineFillPaths;

    public CoastlineRenderer(Collection<Way> allWays, double meanLat) {
        double cosMeanLat = Math.cos(Math.toRadians(meanLat));
        this.coastlineFillPaths = buildCoastlineFillPaths(allWays, cosMeanLat);
    }

    private List<Path2D> buildCoastlineFillPaths(Collection<Way> ways, double cosMeanLat) {
        // Kopier den eksisterende buildCoastlineFillPaths-metode fra WayRenderer hertil,
        // men tilføj cosMeanLat som parameter til buildPathFromNodes-kaldet
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
                Path2D path = buildPathFromNodes(ring, true, cosMeanLat);
                if (path != null) fills.add(path);
            }
            return fills;
    }

    @Override
    public void draws(Graphics2D gc) {
        gc.setColor(Color.decode("#f5f0e1"));
        for (Path2D fillPath : coastlineFillPaths) {
            gc.fill(fillPath);
        }
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

    private Node firstNode(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;
        return nodes.get(0);
    }

    private Node lastNode(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;
        return nodes.get(nodes.size() - 1);
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
}
