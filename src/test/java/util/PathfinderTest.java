package util;

import models.heightcurve.HeightCurve;
import models.osm.Node;
import models.pathfinding.Edge;
import models.pathfinding.Pathfinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PathfinderTest {


    //  Helpers

    private static Node node(long id, double lat, double lon) {
        return new Node(id, lat, lon);
    }

    /**
     * Adds a directed edge from→to with an explicit weight, bypassing the haversine
     * calculation in the Edge constructor so tests use predictable distances.
     */
    private static void connect(Node from, Node to, double weight) {
        Edge e = new Edge(from, to);
        e.setWeight(weight);
        from.addNeighbour(e);
    }

    private static Node submergedNode(long id, double lat, double lon) {
        Node n = new Node(id, lat, lon);
        HeightCurve hc = new HeightCurve(id, -10.0, List.of());
        hc.submerge(0.0);
        n.setContainingHeightCurve(hc);
        return n;
    }


    //  Argument validation

    @Nested
    @DisplayName("Argument validation")
    class ArgumentValidationTests {

        @Test
        @DisplayName("Null start throws IllegalArgumentException")
        void shortestPath_nullStart_throwsIAE() {
            Node target = node(2, 1.0, 0.0);
            assertThrows(IllegalArgumentException.class,
                    () -> new Pathfinder()._shortestPath(null, target, true));
        }

        @Test
        @DisplayName("Null target throws IllegalArgumentException")
        void shortestPath_nullTarget_throwsIAE() {
            Node start = node(1, 0.0, 0.0);
            assertThrows(IllegalArgumentException.class,
                    () -> new Pathfinder()._shortestPath(start, null, true));
        }

        @Test
        @DisplayName("Non-Dijkstra single-arg overload (no target) throws RuntimeException")
        void shortestPath_nonDijkstraNoTarget_throwsRuntimeException() {
            Node start = node(1, 0.0, 0.0);
            assertThrows(RuntimeException.class,
                    () -> new Pathfinder()._shortestPath(start, false));
        }
    }


    //  Start node behaviour

    @Nested
    @DisplayName("Start node")
    class StartNodeTests {

        @Test
        @DisplayName("Start node always has distance 0")
        void startNode_distanceIsZero() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            connect(a, b, 5.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, b, true);
            assertEquals(0.0, r.distances().get(a), 1e-9);
        }

        @Test
        @DisplayName("Start node is in the visited set")
        void startNode_isInVisitedSet() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            connect(a, b, 5.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, b, true);
            assertTrue(r.visitedNodes().contains(a));
        }
    }


    //  Direct connection

    @Nested
    @DisplayName("Direct connection")
    class DirectConnectionTests {

        @Test
        @DisplayName("Distance to directly connected target equals the edge weight")
        void direct_distanceEqualsEdgeWeight() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            connect(a, b, 7.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, b, true);
            assertEquals(7.0, r.distances().get(b), 1e-9);
        }

        @Test
        @DisplayName("Previous node of the target is the start for a direct connection")
        void direct_previousNodeIsStart() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            connect(a, b, 5.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, b, true);
            assertSame(a, r.previousNodes().get(b));
        }

        @Test
        @DisplayName("Target node has an entry in the distances map after being reached")
        void direct_targetInDistances() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            connect(a, b, 5.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, b, true);
            assertTrue(r.distances().containsKey(b));
        }
    }


    //  Multi-hop path

    @Nested
    @DisplayName("Multi-hop path")
    class MultiHopTests {

        @Test
        @DisplayName("Distance through two edges equals the sum of their weights")
        void multiHop_distanceIsSumOfWeights() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 3.0);
            connect(b, c, 4.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, c, true);
            assertEquals(7.0, r.distances().get(c), 1e-9);
        }

        @Test
        @DisplayName("Previous-node chain reconstructs the full path A→B→C")
        void multiHop_previousChainIsCorrect() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 3.0);
            connect(b, c, 4.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, c, true);
            assertSame(b, r.previousNodes().get(c), "prev[c] must be b");
            assertSame(a, r.previousNodes().get(b), "prev[b] must be a");
        }
    }


    //  Shortest path selection

    @Nested
    @DisplayName("Shortest path selection")
    class ShortestPathSelectionTests {

        @Test
        @DisplayName("Prefers the direct route when it is shorter than the multi-hop route")
        void selection_prefersDirectRouteWhenShorter() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 5.0);
            connect(b, c, 5.0); // via b: total 10
            connect(a, c, 3.0); // direct: 3
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, c, true);
            assertEquals(3.0, r.distances().get(c), 1e-9);
        }

        @Test
        @DisplayName("Prefers the multi-hop route when it is shorter than the direct route")
        void selection_prefersIndirectRouteWhenShorter() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 2.0);
            connect(b, c, 2.0); // via b: total 4
            connect(a, c, 9.0); // direct: 9
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, c, true);
            assertEquals(4.0, r.distances().get(c), 1e-9);
        }

        @Test
        @DisplayName("Correct previous-node is recorded when the shorter indirect route wins")
        void selection_indirectRoute_previousNodeIsCorrect() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 2.0);
            connect(b, c, 2.0);
            connect(a, c, 9.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, c, true);
            assertSame(b, r.previousNodes().get(c), "Winning route via b must set prev[c]=b");
        }
    }


    //  Submerged nodes

    @Nested
    @DisplayName("Submerged nodes")
    class SubmergedNodeTests {

        @Test
        @DisplayName("Submerged intermediate node is never visited")
        void submerged_intermediateNotVisited() {
            Node a = node(1, 0.0, 0.0);
            Node b = submergedNode(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 1.0);
            connect(b, c, 1.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, c, true);
            assertFalse(r.visitedNodes().contains(b), "Submerged node must not be visited");
        }

        @Test
        @DisplayName("Target is unreachable when the only path passes through a submerged node")
        void submerged_targetUnreachable() {
            Node a = node(1, 0.0, 0.0);
            Node b = submergedNode(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 1.0);
            connect(b, c, 1.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, c, true);
            assertFalse(r.distances().containsKey(c), "Target must not appear in distances when only route is blocked");
        }

        @Test
        @DisplayName("Non-submerged bypass is used when the direct route is blocked by a submerged node")
        void submerged_bypassRouteUsed() {
            Node a = node(1, 0.0, 0.0);
            Node b = submergedNode(2, 1.0, 0.0); // blocked
            Node c = node(3, 2.0, 0.0);
            Node bypass = node(4, 0.0, 1.0);
            connect(a, b, 1.0);
            connect(b, c, 1.0);  // blocked path
            connect(a, bypass, 5.0);
            connect(bypass, c, 5.0); // alternate route: total 10
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, c, true);
            assertEquals(10.0, r.distances().get(c), 1e-9, "Bypass route must be found");
        }
    }


    //  Unreachable target

    @Nested
    @DisplayName("Unreachable target")
    class UnreachableTargetTests {

        @Test
        @DisplayName("Completely disconnected target has no entry in the distances map")
        void unreachable_notInDistances() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node isolated = node(3, 9.0, 9.0);
            connect(a, b, 1.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, isolated, true);
            assertFalse(r.distances().containsKey(isolated));
        }

        @Test
        @DisplayName("Completely disconnected target has no previous-node entry")
        void unreachable_notInPreviousNodes() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node isolated = node(3, 9.0, 9.0);
            connect(a, b, 1.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, isolated, true);
            assertFalse(r.previousNodes().containsKey(isolated));
        }

        @Test
        @DisplayName("Other reachable nodes are still settled correctly even when target is unreachable")
        void unreachable_reachableNodesStillCorrect() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node isolated = node(3, 9.0, 9.0);
            connect(a, b, 4.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, isolated, true);
            assertEquals(4.0, r.distances().get(b), 1e-9);
        }
    }


    //  getShortestPathTo() — A* path reconstruction
    //  Only linear chains are used here: with artificial weights much smaller than
    //  real haversine distances, the heuristic is inadmissible on graphs with
    //  alternative routes, so we avoid those cases.

    @Nested
    @DisplayName("getShortestPathTo()")
    class GetShortestPathToTests {

        @Test
        @DisplayName("Direct connection: path is [start, target]")
        void getShortestPath_directConnection_twoNodePath() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            connect(a, b, 1.0);
            List<Node> path = new Pathfinder().getShortestPathTo(a, b).path();
            assertEquals(List.of(a, b), path);
        }

        @Test
        @DisplayName("Path starts with the start node")
        void getShortestPath_startsWithStart() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 1.0);
            connect(b, c, 1.0);
            List<Node> path = new Pathfinder().getShortestPathTo(a, c).path();
            assertSame(a, path.getFirst());
        }

        @Test
        @DisplayName("Path ends with the target node")
        void getShortestPath_endsWithTarget() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 1.0);
            connect(b, c, 1.0);
            List<Node> path = new Pathfinder().getShortestPathTo(a, c).path();
            assertSame(c, path.getLast());
        }

        @Test
        @DisplayName("Three-node linear chain: path is [a, b, c] in order")
        void getShortestPath_linearChain_correctOrder() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 2.0, 0.0);
            connect(a, b, 1.0);
            connect(b, c, 1.0);
            List<Node> path = new Pathfinder().getShortestPathTo(a, c).path();
            assertEquals(List.of(a, b, c), path);
        }

        @Test
        @DisplayName("Result includes a non-empty visited-nodes set")
        void getShortestPath_visitedNodesPresent() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            connect(a, b, 1.0);
            Pathfinder.Path result = new Pathfinder().getShortestPathTo(a, b);
            assertNotNull(result.visitedNodes());
            assertFalse(result.visitedNodes().isEmpty());
        }
    }


    //  Graph edge cases

    @Nested
    @DisplayName("Graph edge cases")
    class GraphEdgeCaseTests {

        @Test
        @DisplayName("Start with no outgoing edges: only start appears in distances")
        void noOutgoingEdges_onlyStartInDistances() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0); // no edge from a to b
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, b, true);
            assertTrue(r.distances().containsKey(a));
            assertFalse(r.distances().containsKey(b));
        }

        @Test
        @DisplayName("Start with no outgoing edges: visited set contains only start")
        void noOutgoingEdges_visitedContainsOnlyStart() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, b, true);
            assertEquals(1, r.visitedNodes().size());
            assertTrue(r.visitedNodes().contains(a));
        }

        @Test
        @DisplayName("Diamond graph: two equal-length routes give the correct distance")
        void diamond_equalPaths_correctDistance() {
            Node a = node(1, 0.0, 0.0);
            Node b = node(2, 1.0, 0.0);
            Node c = node(3, 0.0, 1.0);
            Node d = node(4, 1.0, 1.0);
            connect(a, b, 2.0);
            connect(a, c, 2.0);
            connect(b, d, 2.0);
            connect(c, d, 2.0); // both A→B→D and A→C→D cost 4
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, d, true);
            assertEquals(4.0, r.distances().get(d), 1e-9);
        }

        @Test
        @DisplayName("Dead-end sibling does not prevent reaching other neighbours of start")
        void deadEnd_sibling_doesNotBlockOtherNeighbours() {
            Node a       = node(1, 0.0, 0.0);
            Node deadEnd = node(2, 1.0, 0.0); // no outgoing edges
            Node target  = node(3, 0.0, 1.0);
            connect(a, deadEnd, 1.0);
            connect(a, target,  2.0);
            Pathfinder.Result r = new Pathfinder()._shortestPath(a, target, true);
            assertEquals(2.0, r.distances().get(target), 1e-9);
        }
    }
}