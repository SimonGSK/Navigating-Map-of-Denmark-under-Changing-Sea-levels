package models.parser;

import java.util.*;
import models.osm.Way;
import models.osm.Relation;
import models.osm.Member;
import models.osm.Node;

public class MapData {
    public final List<Way> standaloneWays;
    public final List<Relation> multiPolygons;
    public final Set<Long> waysInRelations;

    // Opdeler OSM-data i to grupper: multipolygon-relations og standalone ways.
    // Ways der indgår i en relation fjernes fra way-listen for at undgå at de samme områder tegnes to gange.
    // Begge lister sorteres fra størst til mindst areal, så store baggrundsarealer tegnes først og ikke dækker over mindre detaljer.
    public MapData(Map<Long, Way> wayMap, Map<Long, Relation> relationMap) {
        waysInRelations = new HashSet<>();
        List<Relation> polys = new ArrayList<>();

        for (Relation r : relationMap.values()) {
            String type = r.getTag("type");
            if (!"multipolygon".equals(type) && !"boundary".equals(type) && type != null) continue;
            polys.add(r);
            for (Member m : r.getMembers()) {
                if (m.getElement() instanceof Way w) {
                    waysInRelations.add(w.getId());
                }
            }
        }

        // Sorterer relations fra størst til mindst
        multiPolygons = polys.stream()
                .sorted(Comparator.comparingDouble(r -> -estimateArea(r)))
                .toList();

        // Sorterer ways fra størst til mindst
        standaloneWays = wayMap.values().stream()
                .filter(w -> !waysInRelations.contains(w.getId()))
                .sorted(Comparator.comparingDouble(w -> -w.getArea()))
                .toList();
    }

    // Estimerer arealet af en relation ved at finde den mindste afgrænsende rektangel (bounding box) rundt om alle outer-ways.
    // Bruges udelukkende til sortering – ikke til præcise beregninger.
    // Returnerer 0 hvis relationen ingen outer-ways med nodes har.
    private double estimateArea(Relation r) {
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        for (Member m : r.getMembers()) {
            if (!(m.getElement() instanceof Way w)) continue;
            if (!"outer".equals(m.getRole())) continue;
            for (Node n : w.getNodes()) {
                if (n == null) continue;
                if (n.getLat() < minLat) minLat = n.getLat();
                if (n.getLat() > maxLat) maxLat = n.getLat();
                if (n.getLon() < minLon) minLon = n.getLon();
                if (n.getLon() > maxLon) maxLon = n.getLon();
            }
        }

        if (minLat == Double.MAX_VALUE) return 0;
        return (maxLat - minLat) * (maxLon - minLon);
    }
}
