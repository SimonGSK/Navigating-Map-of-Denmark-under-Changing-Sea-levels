package models.RTree;

import models.osm.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

/**
 * Holds all OSM elements (nodes, ways, relations) loaded for insertion into the R-Tree.
 * Ways that belong to multipolygon relations are excluded from direct iteration
 * to avoid double-inserting geometry already covered by their parent relation.
 */
public class TreeData implements Iterable<OsmElement>, Serializable {
    private final Map<Long, Node> nodes;
    private final Map<Long, Way> ways;
    private final Map<Long, Relation> relations;
    /** Ways that are members of a multipolygon relation — excluded from iteration. */
    private Set<Way> waysInRelations;

    /**
     * @param nodes map of nodes by id
     * @param ways map of ways by id
     * @param relations map of relations by id
     * @throws RuntimeException if any map argument is null
     */
    public TreeData(Map<Long, Node> nodes, Map<Long, Way> ways, Map<Long, Relation> relations) {
        if (nodes == null || ways == null || relations == null) {
            throw new RuntimeException("Inputs must not be null");
        }

        this.nodes = nodes;
        this.ways = ways;
        this.relations = relations;

        findWaysInRelations();
    }

    public TreeData() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    private void findWaysInRelations() {
        if (waysInRelations == null) {
            waysInRelations = new HashSet<>();
        }

        for (Relation r : relations.values()) {
            String relType = r.getTag("type");

            if (!"multipolygon".equals(relType)) {
                continue;
            }

            if (r.getMembers() == null) {
                continue;
            }

            for (Member m : r.getMembers()) {
                switch (m.getElement().getType()) {
                    case way -> {
                        waysInRelations.add((Way) m.getElement());
                    }
                }
            }
        }
    }

    /**
     * @return an iterator over elements sorted by minLon
     */
    @Override
    public Iterator<OsmElement> iterator() {
        return Stream.concat(
                Stream.concat(nodes.values().stream(), ways.values().stream().filter(w -> !waysInRelations.contains(w))),
                relations.values().stream()
        ).sorted(Comparator.comparingDouble(e -> e.getMbr().minLon())).iterator();
    }

    /**
     * @return total count of nodes, ways, and relations
     */
    public int size() {
        return nodes.size() + ways.size() + relations.size();
    }

    /**
     * @return node map keyed by id
     */
    public Map<Long, Node> getNodes() {
        return nodes;
    }

    /**
     * @return way map keyed by id
     */
    public Map<Long, Way> getWays() {
        return ways;
    }

    /**
     * @return relation map keyed by id
     */
    public Map<Long, Relation> getRelations() {
        return relations;
    }
}
