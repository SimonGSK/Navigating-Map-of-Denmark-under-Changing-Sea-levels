package util;

import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Way;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WayTest {


    //  Helpers

    private static Node node(long id, double lat, double lon) {
        return new Node(id, lat, lon);
    }

    private static Way way(long id, List<Node> nodes) {
        return new Way(id, new HashMap<>(), nodes);
    }


    //  getArea() — shoelace formula

    @Nested
    @DisplayName("getArea() — shoelace formula")
    class AreaTests {

        @Test
        @DisplayName("Fewer than three nodes returns 0")
        void area_fewerThanThreeNodes_returnsZero() {
            Node n1 = node(1, 0.0, 0.0);
            Node n2 = node(2, 1.0, 1.0);
            assertEquals(0.0, way(1, List.of(n1, n2)).getArea(), "Two-node way must have area 0");
        }

        @Test
        @DisplayName("Open way (first ID ≠ last ID) returns 0")
        void area_openWay_returnsZero() {
            Node n1 = node(1, 0.0, 0.0);
            Node n2 = node(2, 1.0, 0.0);
            Node n3 = node(3, 1.0, 1.0);
            assertEquals(0.0, way(1, List.of(n1, n2, n3)).getArea(), "Open way must have area 0");
        }

        @Test
        @DisplayName("Collinear closed nodes return 0 (degenerate polygon)")
        void area_collinearNodes_returnsZero() {
            Node n1 = node(1, 0.0, 0.0);
            Node n2 = node(2, 0.0, 1.0);
            Node n3 = node(3, 0.0, 2.0);
            assertEquals(0.0, way(1, List.of(n1, n2, n3, n1)).getArea(), 1e-12,
                    "All-collinear nodes must yield area 0");
        }

        @Test
        @DisplayName("Unit square (1×1) has area 1")
        void area_unitSquare_returnsOne() {
            Node n1 = node(1, 0.0, 0.0);
            Node n2 = node(2, 0.0, 1.0);
            Node n3 = node(3, 1.0, 1.0);
            Node n4 = node(4, 1.0, 0.0);
            assertEquals(1.0, way(1, List.of(n1, n2, n3, n4, n1)).getArea(), 1e-12);
        }

        @Test
        @DisplayName("4×3 rectangle has area 12")
        void area_rectangle_returnsCorrectValue() {
            Node n1 = node(1, 0.0, 0.0);
            Node n2 = node(2, 0.0, 3.0);
            Node n3 = node(3, 4.0, 3.0);
            Node n4 = node(4, 4.0, 0.0);
            assertEquals(12.0, way(1, List.of(n1, n2, n3, n4, n1)).getArea(), 1e-12);
        }

        @Test
        @DisplayName("Right triangle with legs 3 and 4 has area 6")
        void area_rightTriangle_returnsCorrectValue() {
            Node n1 = node(1, 0.0, 0.0);
            Node n2 = node(2, 3.0, 0.0);
            Node n3 = node(3, 0.0, 4.0);
            assertEquals(6.0, way(1, List.of(n1, n2, n3, n1)).getArea(), 1e-12);
        }

        @Test
        @DisplayName("Counter-clockwise winding returns the same area as clockwise")
        void area_reverseWinding_returnsSameArea() {
            Node n1 = node(1, 0.0, 0.0);
            Node n2 = node(2, 0.0, 1.0);
            Node n3 = node(3, 1.0, 1.0);
            Node n4 = node(4, 1.0, 0.0);
            double cw  = way(1, List.of(n1, n2, n3, n4, n1)).getArea();
            double ccw = way(2, List.of(n1, n4, n3, n2, n1)).getArea();
            assertEquals(cw, ccw, 1e-12, "Winding direction must not affect the computed area");
        }

        @Test
        @DisplayName("getArea() is never negative")
        void area_neverNegative() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.0);
            Node n3 = node(3, 55.1, 12.1);
            assertTrue(way(1, List.of(n1, n2, n3, n1)).getArea() >= 0.0);
        }
    }


    //  getMbr()

    @Nested
    @DisplayName("getMbr()")
    class MbrTests {

        @Test
        @DisplayName("Single-node way has a point MBR (zero area)")
        void mbr_singleNode_isPointBox() {
            BoundingBox mbr = way(1, List.of(node(1, 55.0, 12.0))).getMbr();
            assertAll(
                    () -> assertEquals(55.0, mbr.minLat(), 1e-12, "minLat"),
                    () -> assertEquals(55.0, mbr.maxLat(), 1e-12, "maxLat"),
                    () -> assertEquals(12.0, mbr.minLon(), 1e-12, "minLon"),
                    () -> assertEquals(12.0, mbr.maxLon(), 1e-12, "maxLon")
            );
        }

        @Test
        @DisplayName("MBR encompasses all node coordinates")
        void mbr_multipleNodes_encompassesAll() {
            Node n1 = node(1, 54.0, 11.0);
            Node n2 = node(2, 57.0, 14.0);
            Node n3 = node(3, 55.5, 12.5);
            BoundingBox mbr = way(1, List.of(n1, n2, n3)).getMbr();
            assertAll(
                    () -> assertEquals(54.0, mbr.minLat(), 1e-12, "minLat"),
                    () -> assertEquals(57.0, mbr.maxLat(), 1e-12, "maxLat"),
                    () -> assertEquals(11.0, mbr.minLon(), 1e-12, "minLon"),
                    () -> assertEquals(14.0, mbr.maxLon(), 1e-12, "maxLon")
            );
        }

        @Test
        @DisplayName("A single outlier node widens the MBR to cover it")
        void mbr_outlierNode_widensBounds() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.1);
            Node outlier = node(3, 60.0, 20.0);
            BoundingBox mbr = way(1, List.of(n1, n2, outlier)).getMbr();
            assertEquals(60.0, mbr.maxLat(), 1e-12, "Outlier must set maxLat");
            assertEquals(20.0, mbr.maxLon(), 1e-12, "Outlier must set maxLon");
        }

        @Test
        @DisplayName("Closed rectangular way: MBR matches the node extents exactly")
        void mbr_closedRectangle_matchesNodeExtents() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 56.0, 12.0);
            Node n3 = node(3, 56.0, 13.0);
            Node n4 = node(4, 55.0, 13.0);
            BoundingBox mbr = way(1, List.of(n1, n2, n3, n4, n1)).getMbr();
            assertAll(
                    () -> assertEquals(55.0, mbr.minLat(), 1e-12, "minLat"),
                    () -> assertEquals(56.0, mbr.maxLat(), 1e-12, "maxLat"),
                    () -> assertEquals(12.0, mbr.minLon(), 1e-12, "minLon"),
                    () -> assertEquals(13.0, mbr.maxLon(), 1e-12, "maxLon")
            );
        }
    }


    //  getNodes()

    @Nested
    @DisplayName("getNodes()")
    class GetNodesTests {

        @Test
        @DisplayName("Returns the nodes that were passed to the constructor")
        void getNodes_returnsExpectedNodes() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.1);
            List<Node> input = List.of(n1, n2);
            assertEquals(input, way(1, input).getNodes());
        }

        @Test
        @DisplayName("Node count matches the input list size")
        void getNodes_countMatchesInput() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.1);
            Node n3 = node(3, 55.2, 12.2);
            assertEquals(3, way(1, List.of(n1, n2, n3)).getNodes().size());
        }

        @Test
        @DisplayName("Closed way: first and last element in getNodes() are the same object")
        void getNodes_closedWay_firstAndLastAreSameObject() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.1);
            Node n3 = node(3, 55.2, 12.0);
            List<Node> nodes = way(1, List.of(n1, n2, n3, n1)).getNodes();
            assertSame(nodes.getFirst(), nodes.getLast(),
                    "First and last node must be the same object for a closed way");
        }
    }


    //  iterator()

    @Nested
    @DisplayName("iterator()")
    class IteratorTests {

        @Test
        @DisplayName("Iterator visits every node exactly once")
        void iterator_traversesAllNodes() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.1);
            Node n3 = node(3, 55.2, 12.2);
            int count = 0;
            for (Node ignored : way(1, List.of(n1, n2, n3))) count++;
            assertEquals(3, count, "Iterator must visit every node");
        }

        @Test
        @DisplayName("Iteration order matches getNodes() order")
        void iterator_orderMatchesGetNodes() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.1);
            Node n3 = node(3, 55.2, 12.2);
            Way w = way(1, List.of(n1, n2, n3));
            List<Node> actual = new ArrayList<>();
            for (Node n : w) actual.add(n);
            assertEquals(w.getNodes(), actual, "Iteration order must match getNodes()");
        }
    }
}
