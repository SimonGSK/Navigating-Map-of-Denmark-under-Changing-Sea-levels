package Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A way is an ordered list of between 1 and 2,000 nodes that define a polyline. Ways are used to represent linear features such as rivers and roads.
 * <p>
 * Ways can also represent the boundaries of areas (solid polygons) such as buildings or forests. In this case, the way's first and last node will be the same. This is called a "closed way".
 * <p>
 * Note that closed ways occasionally represent loops, such as roundabouts on highways, rather than solid areas. This is usually inferred from tags on the way, for example landuse=* can never pertain to a linear feature. However, some real-life objects (such as man_made=pier) can have both a linear closed way or an areal representation area, and the tag area=yes or area=no can be used to avoid ambiguity or misinterpretation. See also: Way#Differences between linear and area representation of features.
 * <p>
 * Areas with holes, or with boundaries of more than 2,000 nodes, cannot be represented by a single way. Instead, the feature will require a more complex multipolygon relation data structure.
 * <p>
 * <a href="https://wiki.openstreetmap.org/wiki/Elements#Elements"><i>Source: OpenStreetMap Wiki; Elements</i></a>
 */
public class Way extends Element {
    private List<Node> nodes;

    public Way(long id) {
        super(id);
    }

    /**
     * Adds a node to this way's ordered list of nodes.
     * <p>
     * Note that it's possible to have the same node appear multiple times, such as a way in the shape of a figure-8.
     * <p>
     * This method lazily initializes the nodes list on first use.
     *
     * @param node the {@link Node} to be added to this way. Must not be {@code null}.
     * @return {@code true} if the node was added successfully, {@code false} otherwise.
     */
    public boolean addNote(Node node) {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }

        return nodes.add(node);
    }

    /**
     * Returns a copy of this way's ordered list of nodes.
     * <p>
     * The returned list is a defensive copy, so modifications to it will not
     * affect this way's nodes. To add nodes to this way, use {@link #addNote(Node)}.
     *
     * @return a copy of the list containing all nodes in this way, or {@code null}
     * if no nodes have been added to this way.
     */
    public List<Node> getNodes() {
        if (nodes == null) {
            return null;
        }

        return new ArrayList<>(nodes);
    }
}
