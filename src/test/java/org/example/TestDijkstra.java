package org.example;


import Elements.Edge;
import Elements.GraphBuilder;
import Elements.Node;
import Pathfinding.Dijkstra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;


public class TestDijkstra {
    private Node a, b, c, d, e;
    private Set<Node> allNodes;
    private Dijkstra dijkstra;

    @BeforeEach
    void setUp() {
        a = new Node(1, 55.0000, 15.0000);
        b = new Node(2, 55.0001, 15.0000); // ~11m north of A
        c = new Node(3, 55.0000, 15.0010); // ~63m east of A (long detour)
        d = new Node(4, 55.0002, 15.0000); // ~11m north of B
        e = new Node(5, 55.0003, 15.0000); // ~11m north of D

        GraphBuilder graphBuilder = new GraphBuilder();
        graphBuilder.connectTwoWay(a, b);
        graphBuilder.connectTwoWay(b, d);
        graphBuilder.connectTwoWay(a, c);
        graphBuilder.connectTwoWay(c, e);
        graphBuilder.connectTwoWay(d, e);

        allNodes = Set.of(a, b, c, d, e);
        dijkstra = new Dijkstra();

        System.out.println("A->B: " + new Edge(a, b).getWeight());
        System.out.println("B->D: " + new Edge(b, d).getWeight());
        System.out.println("D->E: " + new Edge(d, e).getWeight());
        System.out.println("A->B->D->E: " + (new Edge(a,b).getWeight() + new Edge(b,d).getWeight() + new Edge(d,e).getWeight()));
        System.out.println("A->C: " + new Edge(a, c).getWeight());
        System.out.println("C->E: " + new Edge(c, e).getWeight());
        System.out.println("A->C->E: " + (new Edge(a,c).getWeight() + new Edge(c,e).getWeight()));
    }

    @Test
    void startNodeDistanceIsZero() {
        Dijkstra.Result result = dijkstra.shortestPath(a, allNodes);
        assertEquals(0.0, result.distances().get(a), 1e-9);
    }

    @Test
    void directEdgeDistancesAreCorrect() {
        Dijkstra.Result result = dijkstra.shortestPath(a, allNodes);
        double distAB = new Edge(a, b).getWeight();
        double distAC = new Edge(a, c).getWeight();
        assertEquals(distAB, result.distances().get(b), 1e-9, "A->B should match Haversine");
        assertEquals(distAC, result.distances().get(c), 1e-9, "A->C should match Haversine");
    }

    @Test
    void shortestPathChoosesRelaxedRoute() {
        Dijkstra.Result result = dijkstra.shortestPath(a, allNodes);
        double distAB = new Edge(a, b).getWeight();
        double distBD = new Edge(b, d).getWeight();
        double distDE = new Edge(d, e).getWeight();
        assertEquals(distAB + distBD, result.distances().get(d), 1e-9, "A->D should be via B");
        assertEquals(distAB + distBD + distDE, result.distances().get(e), 1e-9, "A->E should be via B->D");
    }

    @Test
    void previousNodesReconstructCorrectPath() {
        Dijkstra.Result result = dijkstra.shortestPath(a, allNodes);
        List<Node> pathToD = dijkstra.getShortestPathTo(d, result.previousNodes());
        assertEquals(List.of(a, b, d), pathToD, "Path to D should be A->B->D");
    }

    @Test
    void previousNodesReconstructLongerPath() {
        Dijkstra.Result result = dijkstra.shortestPath(a, allNodes);
        List<Node> pathToE = dijkstra.getShortestPathTo(e, result.previousNodes());
        assertEquals(List.of(a, b, d, e), pathToE, "Path to E should be A->B->D->E");
    }

    @Test
    void allNodesAreReachable() {
        Dijkstra.Result result = dijkstra.shortestPath(a, allNodes);
        for (Node node : allNodes) {
            assertTrue(result.distances().get(node) < Double.MAX_VALUE,
                    "Node " + node.getId() + " should be reachable");
        }
    }

    @Test
    void unreachableNodeHasMaxDistance() {
        Node isolated = new Node(6, 55.0004, 15.0000);
        Set<Node> allNodesWithIsolated = new HashSet<>(allNodes);
        allNodesWithIsolated.add(isolated);

        Dijkstra.Result result = dijkstra.shortestPath(a, allNodesWithIsolated);

        assertEquals(Double.MAX_VALUE, result.distances().get(isolated),
                "Isolated node should be unreachable");
    }
}