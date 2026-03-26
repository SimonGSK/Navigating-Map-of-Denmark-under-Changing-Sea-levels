package models.rendering;

import Interfaces.Drawable;
import java.util.*;
import java.awt.*;

import models.osm.Relation;
import models.osm.Node;
import models.osm.Way;
import models.osm.Member;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RelationRenderer implements Drawable{
    private final List<Relation> relations;
    private final double cosMeanLat;

    public RelationRenderer(List<Relation> relations, double cosMeanLat) {
        this.relations = relations;
        this.cosMeanLat = cosMeanLat;
    }

    // Tegner alle multipolygon-relations som fyldte områder på kortet.
    // Relations tegnes i den rækkefølge de ligger i listen – det forventes at listen allerede er sorteret fra størst til mindst areal.
    @Override
    public void drawForTest(Graphics2D gc) {
        for (Relation relation : relations) {
            drawMultiPolygon(gc, relation);
        }
    }

    // Bygger og tegner én multipolygon bestående af outer-ringe og inner-ringe.
    // Outer-ringe definerer polygonens ydre grænser, inner-ringe definerer huller (f.eks. en lysning i en skov eller en ø i en sø).
    // Bruger WIND_EVEN_ODD som fill-regel, hvilket automatisk gør at
    // overlappende inner-ringe bliver til huller i stedet for fyldte områder.
    private void drawMultiPolygon(Graphics2D gc, Relation relation) {
        List<Way> outerWays = new ArrayList<>();
        List<Way> innerWays = new ArrayList<>();

        for (Member member : relation.getMembers()) {
            if (!(member.getElement() instanceof Way way)) continue;
            if ("outer".equals(member.getRole()) || "".equals(member.getRole()) || member.getRole() == null) {
                outerWays.add(way);
            } else if ("inner".equals(member.getRole())) innerWays.add(way);
        }

        if (outerWays.isEmpty()) return;

        Path2D path = new Path2D.Double();
        path.setWindingRule(Path2D.WIND_NON_ZERO); //Ved ikke om det er bedre med WIND_EVEN_ODD, men umiddelbart farves mere med WIND_NON_ZERO

        for (Way way : outerWays) {
            appendNodes(path, stitchWays(outerWays));
            break; // stitchWays håndterer alle outer ways samlet
        }
        // Erstat løkken ovenfor med dette:
        appendNodes(path, stitchWays(outerWays));

        for (Way way : innerWays) {
            appendNodes(path, stitchWays(List.of(way)));
        }

        gc.setColor(relation.getColor());
        gc.fill(path);
    }

    // Tilføjer en sekvens af nodes til en Path2D som en lukket delsti.
    // Konverterer geografiske koordinater (lon, lat) til tegnekoordinater ved at skalere longitude med cosMeanLat for at kompensere for
    // at længdegrader er tættere på hinanden jo længere fra ækvator man er, og invertere latitude fordi skærm-y-aksen peger nedad.
    private void appendNodes(Path2D path, List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return;
        boolean first = true;
        for (Node node : nodes) {
            if (node == null) continue;
            double x = node.getLon() * cosMeanLat;
            double y = -node.getLat();
            if (first) { path.moveTo(x, y); first = false; }
            else path.lineTo(x, y);
        }
        path.closePath();
    }

    // Syr en liste af ways sammen til én sammenhængende sekvens af nodes.
    // Nødvendigt fordi en enkelt polygon i OSM ofte er splittet op i flere ways der skal sættes ende-til-ende i den rigtige rækkefølge.
    // Algoritmen starter med den første way og finder gentagne gange den næste way hvis start- eller slutnode matcher den nuværende rings slutnode.
    // Hvis en way er "baglæns" i forhold til ringen, vendes den automatisk.
    // Stopper tidligt hvis data er usammenhængende.
    private List<Node> stitchWays(List<Way> ways) {
        if (ways.isEmpty()) return List.of();
        if (ways.size() == 1) return ways.get(0).getNodes();

        LinkedList<Node> ring = new LinkedList<>(ways.get(0).getNodes());
        List<Way> remaining = new ArrayList<>(ways.subList(1, ways.size()));

        while (!remaining.isEmpty()) {
            Node last = ring.getLast();
            boolean found = false;
            for (Iterator<Way> it = remaining.iterator(); it.hasNext();) {
                List<Node> nodes = it.next().getNodes();
                if (nodes.get(0).getId() == last.getId()) {
                    nodes.subList(1, nodes.size()).forEach(ring::addLast);
                    it.remove(); found = true; break;
                } else if (nodes.get(nodes.size()-1).getId() == last.getId()) {
                    List<Node> rev = new ArrayList<>(nodes);
                    Collections.reverse(rev);
                    rev.subList(1, rev.size()).forEach(ring::addLast);
                    it.remove(); found = true; break;
                }
            }
            if (!found) break;
        }
        if (!remaining.isEmpty()) {
            System.out.println("stitchWays: kunne ikke sy "
                    + remaining.size() + " ways sammen");
        }
        return new ArrayList<>(ring);
    }
}
