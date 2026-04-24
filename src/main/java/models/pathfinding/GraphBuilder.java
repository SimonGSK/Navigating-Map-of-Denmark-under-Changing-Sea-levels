package models.pathfinding;

import models.pathfinding.Edge;

public class GraphBuilder {
    public Edge connectOneWay(Node from, Node to) {
        Edge edge = new Edge(from, to);
        from.addNeighbour(edge);
        return edge;
    }

    public Edge[] connectTwoWay(Node from, Node to) {
        Edge forward = connectOneWay(from, to);
        Edge backward = connectOneWay(to, from);
        return new Edge[]{forward, backward};
    }
}