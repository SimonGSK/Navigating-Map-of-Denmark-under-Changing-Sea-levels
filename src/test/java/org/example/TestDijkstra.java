package org.example;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;


public class TestDijkstra {

    @Test
    void shortestPathSimpleGraph() {
        Node a = new Node(1, 0, 0);
        Node b = new Node(2, 0, 1);
        Node c = new Node(3, 0, 2);

        Map<Node, List<Path>> graph = new HashMap<>();
        graph.put(a, List.of(new Path(b, 1), new Path(c, 4)));
        graph.put(b, List.of(new Path(c, 2)));
        graph.put(c, List.of());

        double result = Pathfinding.shortestDistance(a, c, graph);

        assertEquals(3.0, result);
    }

    @Test
    void shortestPathSimpleLonger() {
        Node a = new Node(1, 0, 0);
        Node b = new Node(2, 0, 1);
        Node c = new Node(3, 0, 2);
        Node d = new Node(4, 0, 3);
        Node e = new Node(5, 2, 4);
        Node f = new Node(6, 2, 5);
    }
}