package models.RTree;

import models.osm.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

public class TreeData implements Iterable<OsmElement>, Serializable {
    private final Map<Long, Node> nodes;
    private final Map<Long, Way> ways;
    private final Map<Long, Relation> relations;
    private Set<Way> waysInRelations;

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

    @Override
    public Iterator<OsmElement> iterator() {
        return Stream.concat(
                Stream.concat(nodes.values().stream(), ways.values().stream().filter(w -> !waysInRelations.contains(w))),
                relations.values().stream()
        ).sorted(Comparator.comparingDouble(e -> e.getMbr().minLon())).iterator();
    }

    public int size() {
        return nodes.size() + (ways.size() - waysInRelations.size()) + relations.size();
    }

    public Map<Long, Node> getNodes() {
        return nodes;
    }

    public Map<Long, Way> getWays() {
        return ways;
    }

    public Map<Long, Relation> getRelations() {
        return relations;
    }
}
