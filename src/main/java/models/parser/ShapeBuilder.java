package models.parser;

import models.geometry.AdaptivePath;
import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.utils.UtilityTools;

import java.awt.geom.Path2D;
import java.util.*;

/**
 * Converts OSM elements into drawable Path2D shapes.
 *
 * Coordinates are projected by scaling longitude with cosMeanLat to correct
 * for the map projection, and negating latitude because screen Y grows downward.
 */
public class ShapeBuilder {
    double cosMeanLat;
    private static final double SNAP_THRESHOLD = 0.0001;

    /**
     * @param cosMeanLat projection scale for longitude
     */
    public ShapeBuilder(double cosMeanLat){
        this.cosMeanLat = cosMeanLat;
    }

    /**
     * Builds a path for a way.
     * @param way way to convert
     * @return path for the way
     */
    public AdaptivePath buildWay(Way way){
        List<Node> nodes = way.getNodes();
        boolean isClosed = nodes.getFirst().getId() == nodes.getLast().getId();

        List<double[]> points = new java.util.ArrayList<>(nodes.size());
        for (Node node: nodes) {
            if (node == null) continue;
            points.add(new double[] { node.getLon() * cosMeanLat, -node.getCoordinate().getLat() });
        }
        return new models.geometry.AdaptivePath(points, isClosed);
    }

    /**
     * Builds a filled path for a relation.
     * @param relation relation to convert
     * @return filled path or null
     */
    public Path2D buildRelation(Relation relation){
        if (relation.getTags() == null || relation.getTags().isEmpty()) return null;

        List<Way> outerWays = new ArrayList<>();
        List<Way> innerWays = new ArrayList<>();
        List<Way> otherWays = new ArrayList<>();

        for (Member member : relation.getMembers()) {
            if (!(member.getElement() instanceof Way way)) continue;
            if ("outer".equals(member.getRole())) {
                outerWays.add(way);
            } else if ("inner".equals(member.getRole())) {
                innerWays.add(way);
            } else {
                otherWays.add(way);
            }
        }

        if (outerWays.isEmpty()) return null;

        // EVEN_ODD winding makes inner rings punch holes in the outer fill.
        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);

        for (List<Node> ring : stitchWaysToRings(outerWays)) {
            appendNodes(path, ring);
        }

        for (List<Node> ring : stitchWaysToRings(innerWays)) {
            appendNodes(path, ring);
        }

        for (Way way : otherWays) {
            path.append(buildWay(way), false);
        }

        return path;
    }

    /**
     * Builds a way path for benchmarks with a custom pixel step.
     * @param way way to convert
     * @param pixelStep minimum pixel deviation
     * @param meanLat mean latitude
     * @return adaptive path
     */
    public static Path2D _buildWayForBenchmark(Way way, double pixelStep, double meanLat){
        double cosMeanLat = Math.cos(Math.toRadians(meanLat));
        List<Node> nodes = way.getNodes();
        boolean isClosed = nodes.getFirst().getId() == nodes.getLast().getId();

        List<double[]> points = new java.util.ArrayList<>(nodes.size());
        for (Node node: nodes) {
            if (node == null) continue;
            points.add(new double[] { node.getLon() * cosMeanLat, -node.getCoordinate().getLat() });
        }
        return new models.geometry.AdaptivePath(points, isClosed, pixelStep);
    }

    /**
     * Appends a closed node ring to a path.
     * @param path target path
     * @param nodes ring nodes
     */
    private void appendNodes(Path2D path, List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return;

        List<Node> validNodes = nodes.stream()
                .filter(Objects::nonNull)
                .toList();

        if (validNodes.size() < 3) return;

        Node firstNode = validNodes.getFirst();
        Node lastNode = validNodes.getLast();
        boolean isClosed = firstNode.getId() == lastNode.getId()
                || UtilityTools.euclideanDistance(firstNode.getCoordinate(), lastNode.getCoordinate()) < SNAP_THRESHOLD; // TODO: Test with haversineFormula
        if (!isClosed) return;

        boolean isFirst = true;
        for (Node node : validNodes) {
            double x = node.getLon() * cosMeanLat;
            double y = -node.getLat();
            if (isFirst) {
                path.moveTo(x, y);
                isFirst = false;
            } else path.lineTo(x, y);
        }
        path.closePath();
    }

    /**
     * Stitches a list of ways into one or more closed rings.
     *
     * A single polygon in OSM is often split across multiple ways that need
     * to be joined end to end in the correct order. The algorithm starts a ring
     * with the first available way, then repeatedly finds the next candidate
     * whose start or end connect to the current tail or head of the ring,
     * reversing the candidate if needed. A new ring is started whenever no
     * candidate connects, which handles disconnected or malformed data.
     * @param ways way list
     * @return stitched rings
     */
    private List<List<Node>> stitchWaysToRings(List<Way> ways) {
        if (ways.isEmpty()) return List.of();
        if (ways.size() == 1) return List.of(ways.get(0).getNodes());

        List<List<Node>> rings = new ArrayList<>();
        List<List<Node>> candidates = new ArrayList<>();
        for (Way way : ways) {
            List<Node> nodes = way.getNodes();
            if (nodes == null || nodes.isEmpty()) continue;
            candidates.add(new ArrayList<>(nodes));
        }

        while (!candidates.isEmpty()) {
            LinkedList<Node> ring = new LinkedList<>(candidates.removeFirst());
            if (ring.isEmpty()) continue;

            boolean progress = true;
            while (progress && !candidates.isEmpty()) {
                progress = false;
                Node first = ring.getFirst();
                Node last = ring.getLast();

                for (Iterator<List<Node>> it = candidates.iterator(); it.hasNext(); ) {
                    List<Node> nodes = it.next();
                    if (nodes == null || nodes.isEmpty()) {
                        it.remove();
                        continue;
                    }

                    Node candidateStart = nodes.getFirst();
                    Node candidateEnd = nodes.getLast();

                    boolean startToLast = isConnected(candidateStart, last);
                    boolean endToLast = isConnected(candidateEnd, last);
                    boolean endToFirst = isConnected(candidateEnd, first);
                    boolean startToFirst = isConnected(candidateStart, first);

                    // Four cases: the candidate connects to the back or front
                    // of the ring, and may need to be reversed to do so.
                    if (startToLast) {
                        // Forward, attach to back.
                        for (int i = 1; i < nodes.size(); i++) {
                            ring.addLast(nodes.get(i));
                        }
                        it.remove();
                        progress = true;
                        break;
                    } else if (endToLast) {
                        // Reversed, attach to back.
                        for (int i = nodes.size() - 2; i >= 0; i--) {
                            ring.addLast(nodes.get(i));
                        }
                        it.remove();
                        progress = true;
                        break;
                    } else if (endToFirst) {
                        // Reversed, attach to front.
                        for (int i = nodes.size() - 2; i >= 0; i--) {
                            ring.addFirst(nodes.get(i));
                        }
                        it.remove();
                        progress = true;
                        break;
                    } else if (startToFirst) {
                        // Forward, attach to front.
                        for (int i = 1; i < nodes.size(); i++) {
                            ring.addFirst(nodes.get(i));
                        }
                        it.remove();
                        progress = true;
                        break;
                    }
                }
            }
            rings.add(new ArrayList<>(ring));
        }
        return rings;
    }

    /**
     * Same as buildRelation but returns a list of AdaptivePaths instead of a single Path2D.
     * Each ring can be simplified individually at render time.
     * @param relation relation to get an AdaptivePath built
     * @return list of adaptive paths for outer and inner rings
     */
    public List<AdaptivePath> buildRelationAdaptive(Relation relation) {
        List<AdaptivePath> result = new ArrayList<>();

        List<Way> outerWays = new ArrayList<>();
        List<Way> innerWays = new ArrayList<>();
        List<Way> otherWays = new ArrayList<>();

        for (Member member : relation.getMembers()) {
            if (!(member.getElement() instanceof Way way)) continue;
            if ("outer".equals(member.getRole())) {
                outerWays.add(way);
            } else if ("inner".equals(member.getRole())) {
                innerWays.add(way);
            } else {
                otherWays.add(way);
            }
        }

        for (List<Node> ring : stitchWaysToRings(outerWays)) {
            result.add(ringToAdaptivePath(ring));
        }
        for (List<Node> ring : stitchWaysToRings(innerWays)) {
            result.add(ringToAdaptivePath(ring));  // inner rings become holes via EVEN_ODD
        }
        for (Way way : otherWays) {
            result.add(buildWay(way));
        }
        return result;
    }

    /**
     * Converts a ring of nodes into an AdaptivePath.
     * @param ring closed ring of nodes
     * @return adaptive path for the ring
     */
    private AdaptivePath ringToAdaptivePath(List<Node> ring) {
        List<double[]> points = new ArrayList<>(ring.size());
        for (Node n : ring) {
            if (n == null) continue;
            points.add(new double[]{ n.getLon() * cosMeanLat, -n.getLat() });
        }
        return new AdaptivePath(points, true);
    }
    /**
     * Checks whether two nodes are connected by id or proximity.
     * @param a first node
     * @param b second node
     * @return true if connected by id or within snap threshold
     */
    private boolean isConnected(Node a, Node b) {
        return a.getId() == b.getId() || UtilityTools.euclideanDistance(a.getCoordinate(), b.getCoordinate()) < SNAP_THRESHOLD; // TODO: Test with haversineFormula
    }
    /**
     * Builds a filled Path2D for a height curve region, with its children appended as holes.
     * @param heightCurve height curve to convert
     * @return filled path for the height curve region
     */
    public Path2D buildHeightCurve(HeightCurve heightCurve){
        return getRegionPath(heightCurve);
    }

    /**
     * Builds the outline of a single height curve as a closed Path2D.
     * @param heightCurve height curve to convert
     * @return boundary path for the height curve
     */
    public Path2D getBoundaryPath(HeightCurve heightCurve) {
        Path2D.Double p = new Path2D.Double();
        List<Coordinate> coords = heightCurve.getCoords();

        Coordinate coordinate1 = coords.getFirst();
        double x1 = coordinate1.getLon() * cosMeanLat;
        double y1 = -coordinate1.getLat();
        p.moveTo(x1, y1);

        for (int i = 1; i < coords.size(); i++) {
            Coordinate coordinate = coords.get(i);
            double x = coordinate.getLon() * cosMeanLat;
            double y = -coordinate.getLat();
            p.lineTo(x, y);
        }

        p.closePath();
        return p;
    }

    /**
     * Builds a filled region from a height curve and its children.
     * EVEN_ODD winding makes each child curve a hole in its parent,
     * matching how elevation bands nest inside each other on a
     * topographic map.
     * @param heightCurve height curve to convert
     * @return filled path for the height curve region
     */
    public Path2D getRegionPath(HeightCurve heightCurve) {
        Path2D.Double p = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        List<HeightCurve> children = heightCurve.getChildren();

        p.append(getBoundaryPath(heightCurve), false);

        for(HeightCurve child : children){
            p.append(getBoundaryPath(child), false);
        }
        return p;
    }

}
