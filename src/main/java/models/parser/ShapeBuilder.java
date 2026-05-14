package models.parser;

import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.utils.UtilityTools;

import java.awt.geom.Path2D;
import java.util.*;

public class ShapeBuilder {
    double cosMeanLat;
    private static final double SNAP_THRESHOLD = 0.0001;

    public ShapeBuilder(double cosMeanLat){
        this.cosMeanLat = cosMeanLat;;
    }

    public Path2D buildWay(Way way){
        Path2D path = new Path2D.Double();
        List<Node> nodes = way.getNodes();

        boolean isClosed = nodes.getFirst().getId() == nodes.getLast().getId();

        boolean isFirst = true;
        for (Node node : nodes) {
            if (node == null) continue;
            double x = node.getLon() * cosMeanLat;
            double y = -node.getCoordinate().getLat();
            if (isFirst) {
                path.moveTo(x, y);
                isFirst = false;
            } else {
                path.lineTo(x, y);
            }
        }
        if (isClosed) path.closePath();

        return path;
    }

    public Path2D buildRelation(Relation relation){
        var tags = relation.getTags();
        if (relation.getTags() == null || relation.getTags().isEmpty()) return null;

        List<Way> outerWays = new ArrayList<>();
        List<Way> innerWays = new ArrayList<>();

        for (Member member : relation.getMembers()) {
            if (!(member.getElement() instanceof Way way)) continue;
            if ("outer".equals(member.getRole()) || "".equals(member.getRole()) || member.getRole() == null) {
                outerWays.add(way);
            } else if ("inner".equals(member.getRole())) innerWays.add(way);
        }

        if (outerWays.isEmpty()) return null;

        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);

        for (List<Node> ring : stitchWaysToRings(outerWays)) {
            appendNodes(path, ring);
        }

        for (List<Node> ring : stitchWaysToRings(innerWays)) {
            appendNodes(path, ring);
        }

        return path;
    }

    // Tilføjer en sekvens af nodes til en Path2D som en lukket delsti.
    // Konverterer geografiske koordinater (lon, lat) til tegnekoordinater ved at skalere longitude med cosMeanLat for at kompensere for
    // at længdegrader er tættere på hinanden jo længere fra ækvator man er, og invertere latitude fordi skærm-y-aksen peger nedad.
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

    // Syr en liste af ways sammen til én sammenhængende sekvens af nodes.
    // Nødvendigt fordi en enkelt polygon i OSM ofte er splittet op i flere ways der skal sættes ende-til-ende i den rigtige rækkefølge.
    // Algoritmen starter med den første way og finder gentagne gange den næste way hvis start- eller slutnode matcher den nuværende rings slutnode.
    // Hvis en way er "baglæns" i forhold til ringen, vendes den automatisk.
    // Stopper tidligt hvis data er usammenhængende.
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

                    if (startToLast) {
                        for (int i = 1; i < nodes.size(); i++) {
                            ring.addLast(nodes.get(i));
                        }
                        it.remove();
                        progress = true;
                        break;
                    } else if (endToLast) {
                        for (int i = nodes.size() - 2; i >= 0; i--) {
                            ring.addLast(nodes.get(i));
                        }
                        it.remove();
                        progress = true;
                        break;
                    } else if (endToFirst) {
                        for (int i = nodes.size() - 2; i >= 0; i--) {
                            ring.addFirst(nodes.get(i));
                        }
                        it.remove();
                        progress = true;
                        break;
                    } else if (startToFirst) {
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

    private boolean isConnected(Node a, Node b) {
        return a.getId() == b.getId() || UtilityTools.euclideanDistance(a.getCoordinate(), b.getCoordinate()) < SNAP_THRESHOLD; // TODO: Test with haversineFormula
    }

    public Path2D buildHeightCurve(HeightCurve heightCurve){
        return getRegionPath(heightCurve);
    }

    //Creates a heightCurve path
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

    //Creates an area consisting of a heightcurve and its children as holes
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
