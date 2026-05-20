package util;

import models.osm.Node;
import models.pathfinding.Edge;
import models.utils.UtilityTools;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeTest {


    //  Helpers

    private static Node node(long id, double lat, double lon) {
        return new Node(id, lat, lon);
    }


    //  Constructor

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("getTargetNode() returns the target passed to the constructor")
        void constructor_setsTargetNode() {
            Node source = node(1, 55.0, 12.0);
            Node target = node(2, 56.0, 13.0);
            assertSame(target, new Edge(source, target).getTargetNode());
        }

        @Test
        @DisplayName("Weight equals the haversine distance between source and target")
        void constructor_weightMatchesHaversineDistance() {
            Node source = node(1, 55.0, 12.0);
            Node target = node(2, 56.0, 13.0);
            double expected = UtilityTools.haversineDistance(source.getCoordinate(), target.getCoordinate());
            assertEquals(expected, new Edge(source, target).getWeight(), 1e-6);
        }

        @Test
        @DisplayName("Same node as source and target gives weight 0")
        void constructor_sameNode_weightIsZero() {
            Node n = node(1, 55.0, 12.0);
            assertEquals(0.0, new Edge(n, n).getWeight(), 1e-9);
        }

        @Test
        @DisplayName("Two distinct nodes produce a positive weight")
        void constructor_distinctNodes_positiveWeight() {
            Node source = node(1, 55.0, 12.0);
            Node target = node(2, 56.0, 13.0);
            assertTrue(new Edge(source, target).getWeight() > 0.0);
        }
    }


    //  Mutators

    @Nested
    @DisplayName("setTargetNode() and setWeight()")
    class MutatorTests {

        @Test
        @DisplayName("setTargetNode() replaces the target node")
        void setTargetNode_updatesTarget() {
            Node a = node(1, 55.0, 12.0);
            Node b = node(2, 56.0, 12.0);
            Node c = node(3, 57.0, 12.0);
            Edge e = new Edge(a, b);
            e.setTargetNode(c);
            assertSame(c, e.getTargetNode());
        }

        @Test
        @DisplayName("setWeight() replaces the weight")
        void setWeight_updatesWeight() {
            Node a = node(1, 55.0, 12.0);
            Node b = node(2, 56.0, 12.0);
            Edge e = new Edge(a, b);
            e.setWeight(42.0);
            assertEquals(42.0, e.getWeight(), 1e-9);
        }

        @Test
        @DisplayName("setWeight() does not affect the target node")
        void setWeight_doesNotAffectTarget() {
            Node a = node(1, 55.0, 12.0);
            Node b = node(2, 56.0, 12.0);
            Edge e = new Edge(a, b);
            e.setWeight(99.0);
            assertSame(b, e.getTargetNode());
        }

        @Test
        @DisplayName("setTargetNode() does not affect the weight")
        void setTargetNode_doesNotAffectWeight() {
            Node a = node(1, 55.0, 12.0);
            Node b = node(2, 56.0, 12.0);
            Node c = node(3, 57.0, 12.0);
            Edge e = new Edge(a, b);
            double originalWeight = e.getWeight();
            e.setTargetNode(c);
            assertEquals(originalWeight, e.getWeight(), 1e-9);
        }
    }
}
