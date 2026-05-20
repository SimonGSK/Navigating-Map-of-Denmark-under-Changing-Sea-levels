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

    // Splits OSM data into two groups: multipolygon relations and standalone ways.
   // Ways that are part of a relation are removed from the way list to avoid the same areas being drawn twice.
   // Both lists are sorted from largest to smallest area, so large background areas are drawn first and don't cover smaller details.
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

        // Sorts relations from biggest to smallest
        multiPolygons = polys.stream()
                .sorted(Comparator.comparingDouble(r -> -r.getArea()))
                .toList();

        // Sorts ways from biggest to smallest
        standaloneWays = wayMap.values().stream()
                .filter(w -> !waysInRelations.contains(w.getId()))
                .sorted(Comparator.comparingDouble(w -> -w.getArea()))
                .toList();
    }
}
