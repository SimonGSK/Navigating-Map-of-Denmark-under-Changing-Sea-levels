package models.pathfinding;

import models.osm.Node;

/**
 * Builds edges between {@link Node} instances, abstracting the wiring of
 * adjacency lists from the rest of the graph construction logic.
 *
 * <p>Each {@code connect} method both creates the {@link Edge} and registers
 * it on the relevant node's adjacency list, so callers don't have to do this
 * manually.
 */
public class GraphBuilder {

    /**
     * Creates a directed edge from {@code source} to {@code target} and registers
     * it on {@code source}'s adjacency list.
     *
     * @param source the source node
     * @param target the target node
     * @return the newly created {@link Edge}
     */
    public Edge connectOneWay(Node source, Node target) {
        Edge edge = new Edge(source, target);
        source.addNeighbour(edge);
        return edge;
    }

    /**
     * Creates two directed edges between {@code source} and {@code target},
     * one in each direction, and registers each on its respective source
     * node's adjacency list.
     *
     * <p>Equivalent to calling {@link #connectOneWay(Node, Node) connectOneWay(source, target)}
     * and {@link #connectOneWay(Node, Node) connectOneWay(target, source)}.
     *
     * @param source one endpoint of the bidirectional connection
     * @param target   the other endpoint of the bidirectional connection
     * @return a two-element array where index {@code 0} is the forward edge
     *         ({@code source} → {@code target}) and index {@code 1} is the backward
     *         edge ({@code target} → {@code source})
     */
    public Edge[] connectTwoWay(Node source, Node target) {
        Edge forward = connectOneWay(source, target);
        Edge backward = connectOneWay(target, source);
        return new Edge[]{forward, backward};
    }
}