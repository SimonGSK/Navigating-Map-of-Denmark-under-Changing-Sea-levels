package util;

import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.pathfinding.Edge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import models.osm.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Node")
class NodeTest {

    static class StubEdge extends Edge {
        public StubEdge() {
            super(new Node(1L, 0.0, 0.0), new Node(2L, 0.0, 0.0));
        }
    }

    private static final long   ID  = 42L;
    private static final double LAT = 55.6761;
    private static final double LON = 12.5683;

    private Node node;

    private final Edge edge1 = new StubEdge();
    private final Edge edge2 = new StubEdge();

    @BeforeEach
    void setUp() {
        node = new Node(ID, LAT, LON);
    }

    //Helper method for creating a submerged height curve
    private static HeightCurve submergedCurve() {
        HeightCurve hc = new HeightCurve(1L, -10.0, List.of()); // height -10
        hc.submerge(0.0);   // sea level 0  →  -10 < 0  →  submerged = true
        return hc;
    }

    private static HeightCurve notSubmergedCurve() {
        HeightCurve hc = new HeightCurve(2L, 50.0, List.of()); // height 50
        // submerge() is never called, so submerged stays false
        return hc;
    }

    @Nested
    @DisplayName("Constructor and basic getters")
    class ConstructorTests {

        @Test
        @DisplayName("getId() returns the id supplied to the constructor")
        void getId_returnsSuppliedId() {
            assertEquals(ID, node.getId());
        }

        @Test
        @DisplayName("getLat() returns the latitude supplied to the constructor")
        void getLat_returnsSuppliedLatitude() {
            assertEquals(LAT, node.getLat());
        }

        @Test
        @DisplayName("getLon() returns the longitude supplied to the constructor")
        void getLon_returnsSuppliedLongitude() {
            assertEquals(LON, node.getLon());
        }

        @Test
        @DisplayName("adjacency list is empty on construction")
        void adjacencyList_initiallyEmpty() {
            assertTrue(node.getAdjacencyList().isEmpty());
        }

        @Test
        @DisplayName("containingHeightCurve is null on construction")
        void containingHeightCurve_initiallyNull() {
            assertNull(node.getContainingHeightCurve());
        }

        @Test
        @DisplayName("node can be constructed with negative coordinates")
        void constructor_negativeCoordinates() {
            Node south = new Node(1L, -33.8688, 151.2093);
            assertEquals(-33.8688, south.getLat());
            assertEquals(151.2093, south.getLon());
        }

        @Test
        @DisplayName("node can be constructed with zero coordinates")
        void constructor_zeroCoordinates() {
            Node origin = new Node(0L, 0.0, 0.0);
            assertEquals(0.0, origin.getLat());
            assertEquals(0.0, origin.getLon());
        }
    }

    @Nested
    @DisplayName("getCoordinate()")
    class GetCoordinateTests {

        @Test
        @DisplayName("returns a Coordinate with the correct lat and lon")
        void getCoordinate_hasCorrectValues() {
            Coordinate c = node.getCoordinate();
            assertEquals(LAT, c.getLat());
            assertEquals(LON, c.getLon());
        }

        @Test
        @DisplayName("returns a defensive copy — two calls return different objects")
        void getCoordinate_isDefensiveCopy() {
            Coordinate first  = node.getCoordinate();
            Coordinate second = node.getCoordinate();

            assertNotSame(first, second);
        }

        @Test
        @DisplayName("defensive copy still holds the original values")
        void getCoordinate_copyHoldsOriginalValues() {
            Coordinate copy = node.getCoordinate();
            assertEquals(LAT, copy.getLat());
            assertEquals(LON, copy.getLon());
        }
    }

    @Nested
    @DisplayName("addNeighbour() and getAdjacencyList()")
    class AdjacencyListTests {

        @Test
        @DisplayName("addNeighbour() adds a single edge")
        void addNeighbour_singleEdge() {
            node.addNeighbour(edge1);

            List<Edge> list = node.getAdjacencyList();
            assertEquals(1, list.size());
            assertSame(edge1, list.get(0));
        }

        @Test
        @DisplayName("addNeighbour() preserves insertion order for multiple edges")
        void addNeighbour_multipleEdges_orderPreserved() {
            node.addNeighbour(edge1);
            node.addNeighbour(edge2);

            List<Edge> list = node.getAdjacencyList();
            assertEquals(2, list.size());
            assertSame(edge1, list.get(0));
            assertSame(edge2, list.get(1));
        }

        @Test
        @DisplayName("addNeighbour() allows adding the same edge reference twice")
        void addNeighbour_duplicateEdge() {
            node.addNeighbour(edge1);
            node.addNeighbour(edge1);

            assertEquals(2, node.getAdjacencyList().size());
        }

        @Test
        @DisplayName("getAdjacencyList() reflects edges added after the first call")
        void getAdjacencyList_reflectsSubsequentAdds() {
            node.addNeighbour(edge1);
            node.addNeighbour(edge2);

            assertEquals(2, node.getAdjacencyList().size());
            assertTrue(node.getAdjacencyList().contains(edge2));
        }
    }

    @Nested
    @DisplayName("isSubmerged()")
    class IsSubmergedTests {

        @Test
        @DisplayName("returns false when no HeightCurve is assigned")
        void isSubmerged_noHeightCurve_returnsFalse() {
            assertFalse(node.isSubmerged());
        }

        @Test
        @DisplayName("returns false when the containing HeightCurve is not submerged")
        void isSubmerged_curveNotSubmerged_returnsFalse() {
            node.setContainingHeightCurve(notSubmergedCurve());
            assertFalse(node.isSubmerged());
        }

        @Test
        @DisplayName("returns true when the containing HeightCurve is submerged")
        void isSubmerged_curveSubmerged_returnsTrue() {
            node.setContainingHeightCurve(submergedCurve());
            assertTrue(node.isSubmerged());
        }

        @Test
        @DisplayName("transitions to false after HeightCurve is reset")
        void isSubmerged_afterReset_returnsFalse() {
            HeightCurve hc = submergedCurve();
            node.setContainingHeightCurve(hc);
            assertTrue(node.isSubmerged()); // confirm precondition

            hc.resetSubmerged();
            assertFalse(node.isSubmerged());
        }

        @Test
        @DisplayName("a curve above sea level is never submerged even when submerge() is called")
        void isSubmerged_heightAboveSeaLevel_remainsFalse() {
            HeightCurve hc = new HeightCurve(3L, 100.0, List.of()); // height well above 0
            hc.submerge(0.0); // 100 < 0 is false → stays not submerged
            node.setContainingHeightCurve(hc);

            assertFalse(node.isSubmerged());
        }
    }

    @Nested
    @DisplayName("setContainingHeightCurve() and getContainingHeightCurve()")
    class HeightCurveTests {

        @Test
        @DisplayName("getContainingHeightCurve() returns the HeightCurve that was set")
        void setAndGet_heightCurve() {
            HeightCurve hc = notSubmergedCurve();
            node.setContainingHeightCurve(hc);

            assertSame(hc, node.getContainingHeightCurve());
        }

        @Test
        @DisplayName("setContainingHeightCurve(null) clears the reference")
        void set_null_clearsHeightCurve() {
            node.setContainingHeightCurve(notSubmergedCurve());
            node.setContainingHeightCurve(null);

            assertNull(node.getContainingHeightCurve());
        }

        @Test
        @DisplayName("HeightCurve can be replaced with a different instance")
        void set_replacesExistingHeightCurve() {
            HeightCurve first  = notSubmergedCurve();
            HeightCurve second = submergedCurve();

            node.setContainingHeightCurve(first);
            node.setContainingHeightCurve(second);

            assertSame(second, node.getContainingHeightCurve());
        }
    }

    @Nested
    @DisplayName("compareTo()")
    class CompareToTests {

        @Test
        @DisplayName("returns 0 when comparing a node to itself")
        void compareTo_sameNode_returnsZero() {
            assertEquals(0, node.compareTo(node));
        }

        @Test
        @DisplayName("returns 0 when comparing two nodes with the same id")
        void compareTo_equalIds_returnsZero() {
            Node other = new Node(ID, 0.0, 0.0);
            assertEquals(0, node.compareTo(other));
        }

        @Test
        @DisplayName("returns negative when this node's id is smaller")
        void compareTo_smallerIdFirst_returnsNegative() {
            Node higher = new Node(ID + 1, 0.0, 0.0);
            assertTrue(node.compareTo(higher) < 0);
        }

        @Test
        @DisplayName("returns positive when this node's id is larger")
        void compareTo_largerIdFirst_returnsPositive() {
            Node lower = new Node(ID - 1, 0.0, 0.0);
            assertTrue(node.compareTo(lower) > 0);
        }

        @Test
        @DisplayName("ordering is antisymmetric: a < b implies b > a")
        void compareTo_antisymmetric() {
            Node a = new Node(1L, 0.0, 0.0);
            Node b = new Node(2L, 0.0, 0.0);

            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToStringTests {

        @Test
        @DisplayName("contains the latitude value")
        void toString_containsLat() {
            assertTrue(node.toString().contains(String.valueOf(LAT)));
        }

        @Test
        @DisplayName("contains the longitude value")
        void toString_containsLon() {
            assertTrue(node.toString().contains(String.valueOf(LON)));
        }

        @Test
        @DisplayName("format is 'lat lon' separated by a single space")
        void toString_format() {
            assertEquals(LAT + " " + LON, node.toString());
        }
    }

    @Nested
    @DisplayName("getArea()")
    class GetAreaTests {

        @Test
        @DisplayName("always returns 0 — a point has no area")
        void getArea_returnsZero() {
            assertEquals(0.0, node.getArea());
        }
    }
}