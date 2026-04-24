package org.example;

import models.pathfinding.Edge;
import models.pathfinding.GraphBuilder;
import Elements.Node;
import org.junit.jupiter.api.Test;

public class TestAdjacency {

    @Test
    void AdjacencyTest01() {
        Node a = new Node(1, 1, 1);
        Node b = new Node(2, 2, 2);
        Node c = new Node(3, 3, 3);
        Node d = new Node(4, 4, 4);

        GraphBuilder graphBuilder = new GraphBuilder();
        graphBuilder.connectTwoWay(a, b);
        graphBuilder.connectTwoWay(a, c);
        graphBuilder.connectTwoWay(c, d);

        assert(a.getAdjacencyList().size() == 2);
        assert(b.getAdjacencyList().size() == 1);
        assert(c.getAdjacencyList().size() == 2);
        assert(d.getAdjacencyList().size() == 1);

        System.out.println("All nodes have the correct number of adjacent edges.");
    }

    @Test
    void AdjacencyTest02() {
        Node a = new Node(1, 1, 1);
        GraphBuilder graphbuilder = new GraphBuilder();
        int nodes = 100;

        for (int i=0; i<nodes; i++) {
            Node node = new Node(i, i, i);
            graphbuilder.connectTwoWay(a, node);
        }

        assert(a.getAdjacencyList().size() == nodes);

        System.out.println("Node a is adjacent to all nodes.");
    }

    @Test
    void AdjacencyTest03() {
        Node a = new Node(1, 1, 1);
        Node b = new Node(2, 2, 2);

        GraphBuilder graphbuilder = new GraphBuilder();
        graphbuilder.connectTwoWay(a, b);

        Edge ab = a.getAdjacencyList().getFirst();

        assert(a.getAdjacencyList().contains(ab));

        System.out.println("Node a is adjacent to node b through edge ab.");
    }
}