package models.pathfinding;

import models.osm.Node;
import models.utils.UtilityTools;

import java.io.Serializable;

/**
 * A directed edge in the pathfinding graph, connecting a source {@link Node}
 * to a target {@link Node} with a geographic weight.
 *
 * <p>The weight is computed once at construction time as the haversine distance
 * between the two nodes' coordinates, representing the real-world distance in
 * metres.
 *
 * <p>Implements {@link Serializable} to support persistence of the graph.
 */
public class Edge implements Serializable {
    private Node targetNode;
    private double weight;

    /**
     * Constructs an edge from {@code sourceNode} to {@code targetNode}, with
     * the weight set to the haversine distance between their coordinates.
     *
     * @param sourceNode the node this edge originates from
     * @param targetNode the node this edge points to
     */
    public Edge(Node sourceNode, Node targetNode) {
        this.targetNode = targetNode;
        this.weight = UtilityTools.haversineDistance(sourceNode.getCoordinate(), targetNode.getCoordinate());
    }

    /**
     * Returns the node this edge points to.
     *
     * @return the target {@link Node}
     */
    public Node getTargetNode() {
          return this.targetNode;
    }

    /**
     * Sets the target node of this edge.
     *
     * <p>Note: this does not recalculate the weight. Call {@link #setWeight}
     * afterwards if the new target has different coordinates.
     *
     * @param targetNode the new target {@link Node}
     */
    public void setTargetNode(Node targetNode) {
          this.targetNode = targetNode;
    }

    /**
     * Returns the weight of this edge, representing the haversine distance
     * between the source and target nodes' coordinates.
     *
     * @return the edge weight as a {@code double}
     */
    public double getWeight() {
          return this.weight;
    }

    /**
     * Overrides the weight of this edge for testing purposes.
     *
     * <p>Normally the weight is set automatically at construction and reflects
     * the haversine distance between the two nodes. Use this only if you need
     * to assign a custom cost (e.g. factoring in road type or speed limits).
     *
     * @param weight the new edge weight; should be non-negative
     */
    public void setWeight(double weight) {
          this.weight = weight;
    }
}