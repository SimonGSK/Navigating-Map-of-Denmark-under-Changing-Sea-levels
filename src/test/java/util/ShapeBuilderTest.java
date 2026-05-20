package util;

import enums.ElementType;
import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.parser.ShapeBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShapeBuilderTest {

    private static final double COS_55 = Math.cos(Math.toRadians(55.0));


    //  Helpers

    private static Node node(long id, double lat, double lon) {
        return new Node(id, lat, lon);
    }

    private static Way way(long id, List<Node> nodes) {
        return new Way(id, new HashMap<>(), nodes);
    }

    /** Closed square polygon: n1 → n2 → n3 → n4 → n1 */
    private static Way closedWay(long id, double lat, double lon, double size) {
        Node n1 = node(id * 10 + 1, lat,        lon);
        Node n2 = node(id * 10 + 2, lat + size, lon);
        Node n3 = node(id * 10 + 3, lat + size, lon + size);
        Node n4 = node(id * 10 + 4, lat,        lon + size);
        return way(id, List.of(n1, n2, n3, n4, n1));
    }

    private static Member outerMember(Way way) {
        return new Member(way, ElementType.way, "outer");
    }

    private static Member innerMember(Way way) {
        return new Member(way, ElementType.way, "inner");
    }

    private static Relation relation(long id, HashMap<String, String> tags, List<Member> members) {
        return new Relation(id, tags, members);
    }

    private static HashMap<String, String> tags(String... pairs) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) map.put(pairs[i], pairs[i + 1]);
        return map;
    }

    private static HeightCurve heightCurve(long id, double height, List<Coordinate> coords) {
        return new HeightCurve(id, height, coords);
    }

    private static int countSegments(Path2D path) {
        int count = 0;
        PathIterator it = path.getPathIterator(null);
        while (!it.isDone()) {
            count++;
            it.next();
        }
        return count;
    }


    //  buildWay()

    @Nested
    @DisplayName("buildWay()")
    class BuildWayTests {

        @Test
        @DisplayName("Open way returns non-null path")
        void buildWay_openWay_returnsNonNull() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.1);
            Node n3 = node(3, 55.2, 12.2);
            ShapeBuilder builder = new ShapeBuilder(COS_55);
            assertNotNull(builder.buildWay(way(1, List.of(n1, n2, n3))));
        }

        @Test
        @DisplayName("Closed way (first node ID == last node ID) returns non-null path")
        void buildWay_closedWay_returnsNonNull() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.1);
            ShapeBuilder builder = new ShapeBuilder(COS_55);
            assertNotNull(builder.buildWay(way(1, List.of(n1, n2, n1))));
        }

        @Test
        @DisplayName("First point is projected correctly: x = lon * cosMeanLat, y = -lat")
        void buildWay_coordinateProjection_isCorrect() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 56.0, 13.0);
            ShapeBuilder builder = new ShapeBuilder(COS_55);
            Path2D path = builder.buildWay(way(1, List.of(n1, n2)));

            double[] seg = new double[6];
            path.getPathIterator(null).currentSegment(seg);
            assertEquals(12.0 * COS_55, seg[0], 1e-9, "x must be lon * cosMeanLat");
            assertEquals(-55.0,          seg[1], 1e-9, "y must be -lat");
        }

        @Test
        @DisplayName("Different cosMeanLat values produce different x projections")
        void buildWay_differentCosMeanLat_affectsProjection() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 56.0, 13.0);
            ShapeBuilder builderCos55 = new ShapeBuilder(COS_55);
            ShapeBuilder builderCos1  = new ShapeBuilder(1.0);

            double[] seg55  = new double[6];
            double[] segCos1 = new double[6];
            builderCos55.buildWay(way(1, List.of(n1, n2))).getPathIterator(null).currentSegment(seg55);
            builderCos1.buildWay(way(2, List.of(n1, n2))).getPathIterator(null).currentSegment(segCos1);

            assertNotEquals(seg55[0], segCos1[0], "Different cosMeanLat must yield different x coordinates");
        }
    }


    //  buildRelation()

    @Nested
    @DisplayName("buildRelation()")
    class BuildRelationTests {

        @Test
        @DisplayName("Null tags returns null")
        void buildRelation_nullTags_returnsNull() {
            Way outer = closedWay(1, 55.0, 12.0, 0.1);
            Relation rel = relation(1, null, List.of(outerMember(outer)));
            assertNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }

        @Test
        @DisplayName("Empty tags returns null")
        void buildRelation_emptyTags_returnsNull() {
            Way outer = closedWay(1, 55.0, 12.0, 0.1);
            Relation rel = relation(1, tags(), List.of(outerMember(outer)));
            assertNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }

        @Test
        @DisplayName("No outer ways returns null")
        void buildRelation_noOuterWays_returnsNull() {
            Way inner = closedWay(1, 55.0, 12.0, 0.05);
            Relation rel = relation(1, tags("type", "multipolygon"), List.of(innerMember(inner)));
            assertNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }

        @Test
        @DisplayName("Relation with one outer way returns non-null path")
        void buildRelation_withOuterWay_returnsNonNull() {
            Way outer = closedWay(1, 55.0, 12.0, 0.1);
            Relation rel = relation(1, tags("type", "multipolygon"), List.of(outerMember(outer)));
            assertNotNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }

        @Test
        @DisplayName("Relation with outer and inner ways returns non-null path")
        void buildRelation_withOuterAndInner_returnsNonNull() {
            Way outer = closedWay(1, 55.0,  12.0,  0.1);
            Way inner = closedWay(2, 55.02, 12.02, 0.05);
            Relation rel = relation(1, tags("type", "multipolygon"),
                    List.of(outerMember(outer), innerMember(inner)));
            assertNotNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }

        @Test
        @DisplayName("Node members are ignored — relation with only node members returns null")
        void buildRelation_onlyNodeMembers_returnsNull() {
            Node n = node(1, 55.0, 12.0);
            Member nodeMember = new Member(n, ElementType.node, "outer");
            Relation rel = relation(1, tags("type", "multipolygon"), List.of(nodeMember));
            assertNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }

        @Test
        @DisplayName("Empty-role member is treated as outer and produces non-null path")
        void buildRelation_emptyRoleTreatedAsOuter() {
            Way way = closedWay(1, 55.0, 12.0, 0.1);
            Member emptyRole = new Member(way, ElementType.way, "");
            Relation rel = relation(1, tags("type", "multipolygon"), List.of(emptyRole));
            assertNotNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }
    }


    //  getBoundaryPath()

    @Nested
    @DisplayName("getBoundaryPath()")
    class GetBoundaryPathTests {

        @Test
        @DisplayName("Returns non-null path for a valid height curve")
        void getBoundaryPath_validCurve_returnsNonNull() {
            List<Coordinate> coords = List.of(
                    new Coordinate(55.0, 12.0),
                    new Coordinate(55.1, 12.0),
                    new Coordinate(55.1, 12.1),
                    new Coordinate(55.0, 12.1)
            );
            assertNotNull(new ShapeBuilder(COS_55).getBoundaryPath(heightCurve(1, 10.0, coords)));
        }

        @Test
        @DisplayName("Segment count equals coords.size() + 1 (moveTo + lineTo… + closePath)")
        void getBoundaryPath_segmentCount_matchesCoordsSize() {
            List<Coordinate> coords = List.of(
                    new Coordinate(55.0, 12.0),
                    new Coordinate(55.1, 12.0),
                    new Coordinate(55.1, 12.1)
            );
            Path2D path = new ShapeBuilder(COS_55).getBoundaryPath(heightCurve(1, 10.0, coords));
            assertEquals(coords.size() + 1, countSegments(path),
                    "Expected moveTo + (n-1) lineTo + closePath = n+1 segments");
        }

        @Test
        @DisplayName("First point projected correctly: x = lon * cosMeanLat, y = -lat")
        void getBoundaryPath_firstPoint_projectedCorrectly() {
            List<Coordinate> coords = List.of(
                    new Coordinate(55.0, 12.0),
                    new Coordinate(55.1, 12.1)
            );
            Path2D path = new ShapeBuilder(COS_55).getBoundaryPath(heightCurve(1, 10.0, coords));

            double[] seg = new double[6];
            path.getPathIterator(null).currentSegment(seg);
            assertEquals(12.0 * COS_55, seg[0], 1e-9, "x must be lon * cosMeanLat");
            assertEquals(-55.0,          seg[1], 1e-9, "y must be -lat");
        }
    }


    //  getRegionPath()

    @Nested
    @DisplayName("getRegionPath()")
    class GetRegionPathTests {

        @Test
        @DisplayName("Height curve with no children returns non-null path")
        void getRegionPath_noChildren_returnsNonNull() {
            List<Coordinate> coords = List.of(
                    new Coordinate(55.0, 12.0),
                    new Coordinate(55.1, 12.0),
                    new Coordinate(55.1, 12.1)
            );
            assertNotNull(new ShapeBuilder(COS_55).getRegionPath(heightCurve(1, 10.0, coords)));
        }

        @Test
        @DisplayName("buildHeightCurve delegates to getRegionPath (same segment count)")
        void buildHeightCurve_delegatesToGetRegionPath() {
            List<Coordinate> coords = List.of(
                    new Coordinate(55.0, 12.0),
                    new Coordinate(55.1, 12.0),
                    new Coordinate(55.1, 12.1)
            );
            ShapeBuilder builder = new ShapeBuilder(COS_55);
            HeightCurve hc = heightCurve(1, 10.0, coords);
            assertEquals(
                    countSegments(builder.buildHeightCurve(hc)),
                    countSegments(builder.getRegionPath(hc)),
                    "buildHeightCurve and getRegionPath must produce paths with the same segment count"
            );
        }

        @Test
        @DisplayName("Height curve with one child has more segments than a childless curve")
        void getRegionPath_withChild_hasMoreSegmentsThanWithout() {
            List<Coordinate> outer = List.of(
                    new Coordinate(55.0, 12.0), new Coordinate(55.2, 12.0), new Coordinate(55.2, 12.2)
            );
            List<Coordinate> inner = List.of(
                    new Coordinate(55.05, 12.05), new Coordinate(55.1, 12.05), new Coordinate(55.1, 12.1)
            );
            HeightCurve parent = heightCurve(1, 10.0, outer);
            HeightCurve child  = heightCurve(2, 5.0, inner);
            parent.addChild(child);

            ShapeBuilder builder = new ShapeBuilder(COS_55);
            int withChild    = countSegments(builder.getRegionPath(parent));
            int withoutChild = countSegments(builder.getRegionPath(heightCurve(3, 10.0, outer)));

            assertTrue(withChild > withoutChild,
                    "Path with a child appended must have more segments than one without");
        }
    }


    //  buildRelationAdaptive()

    @Nested
    @DisplayName("buildRelationAdaptive()")
    class BuildRelationAdaptiveTests {

        @Test
        @DisplayName("One outer way produces one AdaptivePath")
        void buildRelationAdaptive_oneOuterWay_returnsOneResult() {
            Way outer = closedWay(1, 55.0, 12.0, 0.1);
            Relation rel = relation(1, tags("type", "multipolygon"), List.of(outerMember(outer)));
            assertEquals(1, new ShapeBuilder(COS_55).buildRelationAdaptive(rel).size());
        }

        @Test
        @DisplayName("Outer + inner ways produce two AdaptivePaths")
        void buildRelationAdaptive_outerAndInner_returnsTwoResults() {
            Way outer = closedWay(1, 55.0,  12.0,  0.1);
            Way inner = closedWay(2, 55.02, 12.02, 0.05);
            Relation rel = relation(1, tags("type", "multipolygon"),
                    List.of(outerMember(outer), innerMember(inner)));
            assertEquals(2, new ShapeBuilder(COS_55).buildRelationAdaptive(rel).size());
        }

        @Test
        @DisplayName("No members returns an empty list")
        void buildRelationAdaptive_noMembers_returnsEmpty() {
            Relation rel = relation(1, tags("type", "multipolygon"), List.of());
            assertTrue(new ShapeBuilder(COS_55).buildRelationAdaptive(rel).isEmpty());
        }

        @Test
        @DisplayName("Non-Way members are skipped; only Way members produce paths")
        void buildRelationAdaptive_nonWayMembersSkipped() {
            Member nodeMember = new Member(node(1, 55.0, 12.0), ElementType.node, "outer");
            Way outer = closedWay(2, 55.0, 12.0, 0.1);
            Relation rel = relation(1, tags("type", "multipolygon"),
                    List.of(nodeMember, outerMember(outer)));
            assertEquals(1, new ShapeBuilder(COS_55).buildRelationAdaptive(rel).size());
        }
    }


    //  Way stitching (via buildRelation)

    @Nested
    @DisplayName("Way stitching (via buildRelation)")
    class WayStitchingTests {

        @Test
        @DisplayName("Two adjacent ways are stitched into one ring and produce a non-null path")
        void stitching_twoAdjacentWays_producesNonNullPath() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.0);
            Node n3 = node(3, 55.1, 12.1);
            Way way1 = way(1, List.of(n1, n2));
            Way way2 = way(2, List.of(n2, n3, n1));
            Relation rel = relation(1, tags("type", "multipolygon"),
                    List.of(outerMember(way1), outerMember(way2)));
            assertNotNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }

        @Test
        @DisplayName("A reversed way is flipped automatically and still stitched correctly")
        void stitching_reversedWay_stitchedCorrectly() {
            Node n1 = node(1, 55.0, 12.0);
            Node n2 = node(2, 55.1, 12.0);
            Node n3 = node(3, 55.1, 12.1);
            Way way1 = way(1, List.of(n1, n2));
            Way way2 = way(2, List.of(n1, n3, n2)); // reversed: end connects back to n2
            Relation rel = relation(1, tags("type", "multipolygon"),
                    List.of(outerMember(way1), outerMember(way2)));
            assertNotNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }

        @Test
        @DisplayName("Single way in a relation is used as-is without stitching")
        void stitching_singleWay_usedDirectly() {
            Way outer = closedWay(1, 55.0, 12.0, 0.1);
            Relation rel = relation(1, tags("type", "multipolygon"), List.of(outerMember(outer)));
            assertNotNull(new ShapeBuilder(COS_55).buildRelation(rel));
        }
    }
}
