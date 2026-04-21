package models.RTree;

import models.osm.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import static models.RTree.ElementType.*;

public record TreeData (
        Map<Long, Node> nodes,
        Map<Long, Way> ways,
        Map<Long, Relation> relations
) implements Iterable<Element> {
    public TreeData(Map<Long, Node> nodes, Map<Long, Way> ways, Map<Long, Relation> relations) {
        if (nodes == null || ways == null || relations == null) {
            throw new RuntimeException("Inputs must not be null");
        }

        // TODO: Remove members from relation which also occur in nodes and ways

        this.nodes = nodes;
        this.ways = ways;
        this.relations = relations;

        removeDuplicates();
    }

    public TreeData() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    private void removeDuplicates() {
        for (Relation r : relations.values()) {
            Iterator<Member> iter = r.iterator();
            while (iter.hasNext()) {
                Member m = iter.next();
                Long key = m.getElement().getId();
                if (nodes.containsKey(key) || ways.containsKey(key) || relations.containsKey(key)) {
                    iter.remove();
                }
            }
        }
    }

    @Override
    public Iterator<Element> iterator() {
        return Stream.concat(
                Stream.concat(nodes.values().stream(), ways.values().stream()),
                relations.values().stream()
        ).iterator();
    }
}
