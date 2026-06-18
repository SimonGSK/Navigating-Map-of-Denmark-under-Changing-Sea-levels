package models.parser;

import models.RTree.Tree;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.geometry.BoundingBox;

import java.io.Serializable;
import java.util.*;

/**
 * Aggregated map data used by renderers and the tree.
 */
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

    /**
     * Creates map data from parsed ways and relations.
     * @param wayMap ways by id
     * @param relationMap relations by id
     */
    public MapData(Map<Long, Way> wayMap, Map<Long, Relation> relationMap) {
        this(wayMap, relationMap, new HashMap<>(), null, null, null);
    }

    /**
     * Creates map data for tree benchmarking.
     * @param wayMap ways by id
     * @param relationMap relations by id
     * @param nodeMap nodes by id
     * @param mbr bounding box
     */
    public MapData(Map<Long, Way> wayMap, Map<Long, Relation> relationMap, Map<Long,Node> nodeMap, BoundingBox mbr) {
        this(wayMap,relationMap,nodeMap,mbr,null,null);
    }

    /**
     * Splits OSM data into two groups: multipolygon relations and standalone ways.
     * Ways that are part of a relation are removed from the way list to avoid the same areas being drawn twice.
     * Both lists are sorted from largest to smallest area, so large background areas are drawn first and don't cover smaller details.
     * Used when loading from binary
     * @param wayMap ways by id
     * @param relationMap relations by id
     * @param nodeMap nodes by id
     * @param mbr bounding box
     * @param hcData height curve data
     * @param tree spatial index tree
     */
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
            String name = r.getTag("name");
            String type = r.getTag("type");
            if (!"multipolygon".equals(type)) continue;
            if ("Region Hovedstaden".equals(name)) continue;

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
