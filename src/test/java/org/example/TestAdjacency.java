package org.example;

import Elements.Edge;
import Elements.GraphBuilder;
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
    }
}
