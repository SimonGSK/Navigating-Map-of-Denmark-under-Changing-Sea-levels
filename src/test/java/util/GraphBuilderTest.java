package util;

import models.osm.Node;
import models.pathfinding.Edge;
import models.pathfinding.GraphBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GraphBuilderTest {


    //  Helpers

    private static Node node(long id, double lat, double lon) {
        return new Node(id, lat, lon);
    }


    //  connectOneWay()

    @Nested
    @DisplayName("connectOneWay()")
    class ConnectOneWayTests {

        @Test
        @DisplayName("Adds exactly one edge to the source node's adjacency list")
        void connectOneWay_addsOneEdgeToSource() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            new GraphBuilder().connectOneWay(from, to);
            assertEquals(1, from.getAdjacencyList().size());
        }

        @Test
        @DisplayName("Does not add any edge to the target node's adjacency list")
        void connectOneWay_doesNotAddEdgeToTarget() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            new GraphBuilder().connectOneWay(from, to);
            assertTrue(to.getAdjacencyList().isEmpty());
        }

        @Test
        @DisplayName("Returned edge targets the destination node")
        void connectOneWay_returnedEdge_targetsDestination() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            Edge e = new GraphBuilder().connectOneWay(from, to);
            assertSame(to, e.getTargetNode());
        }

        @Test
        @DisplayName("Returned edge is the same object stored in the source adjacency list")
        void connectOneWay_returnedEdge_isSameAsStored() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            Edge returned = new GraphBuilder().connectOneWay(from, to);
            assertSame(returned, from.getAdjacencyList().get(0));
        }

        @Test
        @DisplayName("Calling twice adds two edges to the source")
        void connectOneWay_calledTwice_addsTwoEdges() {
            Node from  = node(1, 55.0, 12.0);
            Node to1   = node(2, 56.0, 12.0);
            Node to2   = node(3, 57.0, 12.0);
            GraphBuilder gb = new GraphBuilder();
            gb.connectOneWay(from, to1);
            gb.connectOneWay(from, to2);
            assertEquals(2, from.getAdjacencyList().size());
        }
    }


    //  connectTwoWay()

    @Nested
    @DisplayName("connectTwoWay()")
    class ConnectTwoWayTests {

        @Test
        @DisplayName("Adds exactly one edge to the source node")
        void connectTwoWay_addsOneEdgeToSource() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            new GraphBuilder().connectTwoWay(from, to);
            assertEquals(1, from.getAdjacencyList().size());
        }

        @Test
        @DisplayName("Adds exactly one edge to the target node")
        void connectTwoWay_addsOneEdgeToTarget() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            new GraphBuilder().connectTwoWay(from, to);
            assertEquals(1, to.getAdjacencyList().size());
        }

        @Test
        @DisplayName("Returns an array of exactly two edges")
        void connectTwoWay_returnsTwoEdges() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            assertEquals(2, new GraphBuilder().connectTwoWay(from, to).length);
        }

        @Test
        @DisplayName("Forward edge (index 0) targets the destination node")
        void connectTwoWay_forwardEdge_targetsDestination() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            Edge[] edges = new GraphBuilder().connectTwoWay(from, to);
            assertSame(to, edges[0].getTargetNode());
        }

        @Test
        @DisplayName("Backward edge (index 1) targets the source node")
        void connectTwoWay_backwardEdge_targetsSource() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            Edge[] edges = new GraphBuilder().connectTwoWay(from, to);
            assertSame(from, edges[1].getTargetNode());
        }

        @Test
        @DisplayName("Each node can reach the other through its adjacency list")
        void connectTwoWay_bothNodesReachEachOther() {
            Node from = node(1, 55.0, 12.0);
            Node to   = node(2, 56.0, 12.0);
            new GraphBuilder().connectTwoWay(from, to);
            assertSame(to,   from.getAdjacencyList().get(0).getTargetNode());
            assertSame(from, to.getAdjacencyList().get(0).getTargetNode());
        }
    }
}
