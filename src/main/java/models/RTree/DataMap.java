package models.RTree;

import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.util.HashMap;
import java.util.Map;

public record DataMap(
        Map<Long, Node> nodes,
        Map<Long, Way> ways,
        Map<Long, Relation> relations
) {
    public DataMap(Map<Long, Node> nodes, Map<Long, Way> ways, Map<Long, Relation> relations) {
        if (nodes == null || ways == null || relations == null) {
            throw new RuntimeException("Inputs must not be null");
        }
        this.nodes = nodes;
        this.ways = ways;
        this.relations = relations;
    }
    public DataMap() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }
}
