package org.example;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;


public class Testdijkstra {

    @Test
    void shortestPathSimpleGraph() {
        Node a = new Node(1, 0, 0);
        Node b = new Node(2, 0, 1);
        Node c = new Node(3, 0, 2);

        Map<Node, List<Path>> graph = new HashMap<>();
        graph.put(a, List.of(new Path(b, 1), new Path(c, 4)));
        graph.put(b, List.of(new Path(c, 2)));
        graph.put(c, List.of());

        double result = Dijkstra.shortestDistance(a, c, graph);

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
// Ulubulu.....
    }
}






    /* public Node(long id, double lat, double lon) {
        super(id);
        this.coord = new Coordinate(lat, lon);
    }

    public record Coordinate(double lat, double lon) {
    }

    private List<Member> members;

    public Relation(long id) {
        super(id);
    }


    1. Vertices / nodes
    2. edges / del af en way
    3. weights / afstand mellem nodes

     */



