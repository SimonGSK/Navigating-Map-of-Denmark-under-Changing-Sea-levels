package models.parser;

import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.util.*;

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

        // Add all ways that are part of a relation to a set, to filter them out later
        for (Relation r : relationMap.values()) {
            String type = r.getTag("type");
            if (type != null && !type.equals("multipolygon") && !type.equals("boundary")) continue;
            polys.add(r);
            for (Member m : r.getMembers()) {
                if (m.getElement() instanceof Way w) {
                    waysInRelations.add(w.getId());
                }
            }
        }

        // Sorterer relations fra størst til mindst
        multiPolygons = polys.stream()
                .sorted(Comparator.comparingDouble(r -> -r.getArea()))
                .toList();

        // Sorterer ways fra størst til mindst
        standaloneWays = wayMap.values().stream()
                .filter(w -> !waysInRelations.contains(w.getId()))
                .sorted(Comparator.comparingDouble(w -> -w.getArea()))
                .toList();
    }
}
