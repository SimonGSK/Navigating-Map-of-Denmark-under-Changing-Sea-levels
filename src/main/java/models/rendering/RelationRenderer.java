package models.rendering;

import Interfaces.AbstractRenderer;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;


public class RelationRenderer extends AbstractRenderer<Relation> {
    private static final double SNAP_THRESHOLD = 0.0001;

    public RelationRenderer(double meanLat) {
        super(meanLat);
    }

    // Tegner alle multipolygon-relations som fyldte områder på kortet.
    // Relations tegnes i den rækkefølge de ligger i listen – det forventes at listen allerede er sorteret fra størst til mindst areal.
    @Override
    public void draws(Graphics2D gc) {
        for (Relation relation : elements) {
            if (!shouldDraw(relation, true)) continue; //Funktion til at afgøre om noget skal tegnes

            //Tilføjet for at undgå at lukkede paths, som f.eks. hiking routes, bliver til huller i multipolygons
            String relationType = relation.getTag("type");
            if (relationType == null) continue;
            if (!"multipolygon".equals(relationType) && !"boundary".equals(relationType)) {
                continue;
            }

            drawMultiPolygon(gc, relation);
        }
    }

    // Bygger og tegner én multipolygon bestående af outer-ringe og inner-ringe.
    // Outer-ringe definerer polygonens ydre grænser, inner-ringe definerer huller (f.eks. en lysning i en skov eller en ø i en sø).
    // Bruger WIND_EVEN_ODD som fill-regel, hvilket automatisk gør at
    // overlappende inner-ringe bliver til huller i stedet for fyldte områder.
    private void drawMultiPolygon(Graphics2D gc, Relation relation) {
        var tags = relation.getTags(); // Todo: Don't use var -> use the returned data type for easier understanding
        if (tags == null || tags.isEmpty()) return;

        List<Way> outerWays = new ArrayList<>();
        List<Way> innerWays = new ArrayList<>();

        for (Member member : relation.getMembers()) {
            if (!(member.getElement() instanceof Way way)) continue;
            if ("outer".equals(member.getRole()) || "".equals(member.getRole()) || member.getRole() == null) {
                outerWays.add(way);
            } else if ("inner".equals(member.getRole())) innerWays.add(way);
        }

        if (outerWays.isEmpty()) return;

        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);

        for (List<Node> ring : stitchWaysToRings(outerWays)) {
            appendNodes(path, ring);
        }

        for (List<Node> ring : stitchWaysToRings(innerWays)) {
            appendNodes(path, ring);
        }

        gc.setColor(relation.getColor());
        gc.fill(path);
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
                || distance(firstNode, lastNode) < SNAP_THRESHOLD;
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

        List<List<Node>> rings = new ArrayList<>();
        List<List<Node>> candidates = new ArrayList<>();
        for (Way way : ways) {
            List<Node> nodes = way.getNodes();
            if (nodes == null || nodes.isEmpty()) continue;
            candidates.add(new ArrayList<>(nodes));
        }

        while (!candidates.isEmpty()) {
            LinkedList<Node> ring = new LinkedList<>(candidates.remove(0));
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

                    Node candidateStart = nodes.get(0);
                    Node candidateEnd = nodes.get(nodes.size() - 1);

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
        return a.getId() == b.getId() || distance(a, b) < SNAP_THRESHOLD;
    }

    private double distance(Node a, Node b) {
        double dLat = a.getLat() - b.getLat();
        double dLon = a.getLon() - b.getLon();
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }
}
