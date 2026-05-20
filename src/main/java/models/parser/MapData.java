package models.parser;

import models.RTree.Tree;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.geometry.BoundingBox;

import java.io.Serializable;
import java.util.*;

public class MapData implements Serializable {
    public final List<Way> standaloneWays;
    public final List<Relation> multiPolygons;
    public final Set<Long> waysInRelations;
    public final Map<Long, Node> nodeMap;
    public final BoundingBox mbr;
    public final HeightCurveData hcData;
    public final Map<Long, Way> wayMap;
    public final Map<Long, Relation> relationMap;
    public final Tree tree;

    // Usen when parsing normally (no binary)
    public MapData(Map<Long, Way> wayMap, Map<Long, Relation> relationMap) {
        this(wayMap, relationMap, new HashMap<>(), null, null, null);
    }

    /**
     * MapData constructor for benchmarking of Tree.java
     * @param wayMap
     * @param relationMap
     * @param nodeMap
     * @param mbr
     */
    public MapData(Map<Long, Way> wayMap, Map<Long, Relation> relationMap, Map<Long,Node> nodeMap, BoundingBox mbr) {
        this(wayMap,relationMap,nodeMap,mbr,null,null);
    }

    // Opdeler OSM-data i to grupper: multipolygon-relations og standalone ways.
    // Ways der indgår i en relation fjernes fra way-listen for at undgå at de samme områder tegnes to gange.
    // Begge lister sorteres fra størst til mindst areal, så store baggrundsarealer tegnes først og ikke dækker over mindre detaljer.
    // Used when loading from binary
    public MapData(Map<Long, Way> wayMap, Map<Long, Relation> relationMap, Map<Long,Node> nodeMap, BoundingBox mbr, HeightCurveData hcData, Tree tree) {
        this.nodeMap = nodeMap;
        this.mbr = mbr;
        this.hcData = hcData;
        this.wayMap = wayMap;
        this.relationMap = relationMap;
        this.tree = tree;

        waysInRelations = new HashSet<>();
        List<Relation> polys = new ArrayList<>();

        // Add all ways that are part of a relation to a set, to filter them out later
        for (Relation r : relationMap.values()) {
            String type = r.getTag("type");
            if (!"multipolygon".equals(type)) continue;
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
