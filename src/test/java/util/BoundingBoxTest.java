package util;

import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoundingBoxTest {


    //  Helpers

    private static BoundingBox box(double minLat, double minLon, double maxLat, double maxLon) {
        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    /** Creates a point-like BoundingBox from a single Coordinate. */
    private static BoundingBox point(double lat, double lon) {
        return box(lat, lon, lat, lon);
    }


    //  Constructor & accessors

    @Nested
    @DisplayName("Constructor and accessors")
    class ConstructorTests {

        @Test
        @DisplayName("Accessors return the values passed to the constructor")
        void constructor_accessors_matchInputValues() {
            BoundingBox b = box(10.0, 20.0, 30.0, 40.0);
            assertAll(
                    () -> assertEquals(10.0, b.minLat(), "minLat"),
                    () -> assertEquals(20.0, b.minLon(), "minLon"),
                    () -> assertEquals(30.0, b.maxLat(), "maxLat"),
                    () -> assertEquals(40.0, b.maxLon(), "maxLon")
            );
        }

        @Test
        @DisplayName("Point box (minLat==maxLat, minLon==maxLon) has zero area")
        void constructor_pointBox_zeroArea() {
            assertEquals(0.0, point(55.0, 12.0).area(), "Point box must have area 0");
        }
    }

    //  area()

    @Nested
    @DisplayName("area()")
    class AreaTests {

        @Test
        @DisplayName("Area of a rectangular box is (maxLat - minLat) * (maxLon - minLon)")
        void area_rectangle_correctValue() {
            BoundingBox b = box(0.0, 0.0, 3.0, 4.0);
            assertEquals(12.0, b.area(), 1e-12, "3 x 4 box must have area 12");
        }

        @Test
        @DisplayName("Zero-height box returns area 0")
        void area_zeroHeight_returnsZero() {
            assertEquals(0.0, box(5.0, 0.0, 5.0, 10.0).area(), "Zero-height box must have area 0");
        }

        @Test
        @DisplayName("Zero-width box returns area 0")
        void area_zeroWidth_returnsZero() {
            assertEquals(0.0, box(0.0, 5.0, 10.0, 5.0).area(), "Zero-width box must have area 0");
        }

        @Test
        @DisplayName("Inverted bounds (maxLat < minLat) area is clamped to 0")
        void area_invertedBounds_clampedToZero() {
            BoundingBox b = box(10.0, 0.0, 5.0, 20.0); // maxLat < minLat
            assertEquals(0.0, b.area(), "Inverted bounds must return area 0, not negative");
        }

        @Test
        @DisplayName("area() is cached — returns identical value on repeated calls")
        void area_repeatedCall_returnsSameValue() {
            BoundingBox b = box(0.0, 0.0, 5.0, 5.0);
            assertEquals(b.area(), b.area(), "area() must return the same value on every call");
        }
    }


    //  isInside()

    @Nested
    @DisplayName("isInside()")
    class IsInsideTests {

        @Test
        @DisplayName("Strictly contained box returns true")
        void isInside_strictlyContained_returnsTrue() {
            BoundingBox inner = box(55.0, 12.0, 56.0, 13.0);
            BoundingBox outer = box(54.0, 11.0, 57.0, 14.0);
            assertTrue(inner.isInside(outer), "Inner box must be inside outer box");
        }

        @Test
        @DisplayName("Equal boxes: each is inside the other")
        void isInside_equalBoxes_returnsTrue() {
            BoundingBox a = box(54.0, 11.0, 57.0, 14.0);
            BoundingBox b = box(54.0, 11.0, 57.0, 14.0);
            assertTrue(a.isInside(b), "Equal boxes must be inside each other");
            assertTrue(b.isInside(a), "Equal boxes must be inside each other (symmetric)");
        }

        @Test
        @DisplayName("Box touching the boundary of other returns true")
        void isInside_touchingBoundary_returnsTrue() {
            BoundingBox inner = box(54.0, 11.0, 57.0, 14.0); // shares edges with outer
            BoundingBox outer = box(54.0, 11.0, 57.0, 14.0);
            assertTrue(inner.isInside(outer), "Box sharing boundary edges must be considered inside");
        }

        @Test
        @DisplayName("Partially overlapping box returns false")
        void isInside_partialOverlap_returnsFalse() {
            BoundingBox a = box(54.0, 11.0, 56.0, 13.0);
            BoundingBox b = box(55.0, 12.0, 57.0, 14.0); // overlaps but neither is inside the other
            assertFalse(a.isInside(b), "Partially overlapping box must not be considered inside");
        }

        @Test
        @DisplayName("Completely disjoint box returns false")
        void isInside_disjoint_returnsFalse() {
            BoundingBox a = box(54.0, 11.0, 55.0, 12.0);
            BoundingBox b = box(60.0, 20.0, 65.0, 25.0);
            assertFalse(a.isInside(b), "Disjoint box must not be inside other");
        }

        @Test
        @DisplayName("Larger box is not inside smaller box")
        void isInside_largerBoxInSmaller_returnsFalse() {
            BoundingBox small = box(55.0, 12.0, 56.0, 13.0);
            BoundingBox large = box(54.0, 11.0, 57.0, 14.0);
            assertFalse(large.isInside(small), "Larger box must not be inside smaller box");
        }
    }

    //  isOverlappingOther()

    @Nested
    @DisplayName("isOverlappingOther()")
    class IsOverlappingTests {

        @Test
        @DisplayName("Clearly overlapping boxes return true")
        void isOverlapping_clearOverlap_returnsTrue() {
            BoundingBox a = box(54.0, 11.0, 56.0, 13.0);
            BoundingBox b = box(55.0, 12.0, 57.0, 14.0);
            assertTrue(a.isOverlappingOther(b), "Overlapping boxes must return true");
        }

        @Test
        @DisplayName("Disjoint boxes (separated north-south) return false")
        void isOverlapping_disjointNorthSouth_returnsFalse() {
            BoundingBox south = box(50.0, 10.0, 52.0, 15.0);
            BoundingBox north = box(55.0, 10.0, 57.0, 15.0);
            assertFalse(south.isOverlappingOther(north), "North-south separated boxes must not overlap");
        }

        @Test
        @DisplayName("Disjoint boxes (separated east-west) return false")
        void isOverlapping_disjointEastWest_returnsFalse() {
            BoundingBox west = box(55.0, 10.0, 57.0, 12.0);
            BoundingBox east = box(55.0, 14.0, 57.0, 16.0);
            assertFalse(west.isOverlappingOther(east), "East-west separated boxes must not overlap");
        }

        @Test
        @DisplayName("Boxes touching on one edge return true")
        void isOverlapping_touchingEdge_returnsTrue() {
            BoundingBox a = box(54.0, 11.0, 55.0, 13.0);
            BoundingBox b = box(55.0, 11.0, 56.0, 13.0); // shares the lat=55 edge with a
            assertTrue(a.isOverlappingOther(b), "Boxes touching on an edge must be considered overlapping");
        }

        @Test
        @DisplayName("One box entirely inside another returns true")
        void isOverlapping_oneInsideOther_returnsTrue() {
            BoundingBox inner = box(55.0, 12.0, 56.0, 13.0);
            BoundingBox outer = box(54.0, 11.0, 57.0, 14.0);
            assertTrue(inner.isOverlappingOther(outer), "Box inside another must be considered overlapping");
            assertTrue(outer.isOverlappingOther(inner), "Outer box must also overlap with inner");
        }

        @Test
        @DisplayName("A box overlaps itself")
        void isOverlapping_sameBox_returnsTrue() {
            BoundingBox b = box(54.0, 11.0, 57.0, 14.0);
            assertTrue(b.isOverlappingOther(b), "A box must overlap itself");
        }
    }


    //  getExpanded()

    @Nested
    @DisplayName("getExpanded()")
    class GetExpandedTests {

        @Test
        @DisplayName("Expanded box contains both originals")
        void getExpanded_containsBothBoxes() {
            BoundingBox a = box(54.0, 11.0, 56.0, 13.0);
            BoundingBox b = box(55.0, 12.0, 57.0, 14.0);
            BoundingBox expanded = a.getExpanded(b);

            assertTrue(a.isInside(expanded), "Original box a must be inside expanded box");
            assertTrue(b.isInside(expanded), "Original box b must be inside expanded box");
        }

        @Test
        @DisplayName("Expanding with an already-contained box returns equivalent to the outer box")
        void getExpanded_innerBoxExpanded_returnsOuterBox() {
            BoundingBox outer = box(54.0, 11.0, 57.0, 14.0);
            BoundingBox inner = box(55.0, 12.0, 56.0, 13.0);
            BoundingBox expanded = outer.getExpanded(inner);

            assertEquals(outer.minLat(), expanded.minLat(), 1e-12, "minLat must match outer");
            assertEquals(outer.minLon(), expanded.minLon(), 1e-12, "minLon must match outer");
            assertEquals(outer.maxLat(), expanded.maxLat(), 1e-12, "maxLat must match outer");
            assertEquals(outer.maxLon(), expanded.maxLon(), 1e-12, "maxLon must match outer");
        }

        @Test
        @DisplayName("getExpanded is symmetric: expand(a, b) equals expand(b, a)")
        void getExpanded_symmetric() {
            BoundingBox a = box(54.0, 11.0, 56.0, 13.0);
            BoundingBox b = box(55.0, 12.0, 57.0, 14.0);

            BoundingBox ab = a.getExpanded(b);
            BoundingBox ba = b.getExpanded(a);

            assertEquals(ab.minLat(), ba.minLat(), 1e-12, "minLat must be symmetric");
            assertEquals(ab.minLon(), ba.minLon(), 1e-12, "minLon must be symmetric");
            assertEquals(ab.maxLat(), ba.maxLat(), 1e-12, "maxLat must be symmetric");
            assertEquals(ab.maxLon(), ba.maxLon(), 1e-12, "maxLon must be symmetric");
        }
    }


    //  computeMbr()

    @Nested
    @DisplayName("computeMbr()")
    class ComputeMbrTests {

        @Test
        @DisplayName("Single coordinate: MBR is a point box at that coordinate")
        void computeMbr_singleCoordinate_isPointBox() {
            BoundingBox mbr = BoundingBox.computeMbr(List.of(new Coordinate(55.0, 12.0)));

            assertEquals(55.0, mbr.minLat(), 1e-12, "minLat must match coordinate lat");
            assertEquals(55.0, mbr.maxLat(), 1e-12, "maxLat must match coordinate lat");
            assertEquals(12.0, mbr.minLon(), 1e-12, "minLon must match coordinate lon");
            assertEquals(12.0, mbr.maxLon(), 1e-12, "maxLon must match coordinate lon");
        }

        @Test
        @DisplayName("Multiple coordinates: MBR encompasses all of them")
        void computeMbr_multipleCoordinates_encompassesAll() {
            List<Coordinate> coords = List.of(
                    new Coordinate(54.0, 11.0),
                    new Coordinate(57.0, 14.0),
                    new Coordinate(55.5, 12.5)
            );
            BoundingBox mbr = BoundingBox.computeMbr(coords);

            assertEquals(54.0, mbr.minLat(), 1e-12, "minLat must be the southernmost lat");
            assertEquals(11.0, mbr.minLon(), 1e-12, "minLon must be the westernmost lon");
            assertEquals(57.0, mbr.maxLat(), 1e-12, "maxLat must be the northernmost lat");
            assertEquals(14.0, mbr.maxLon(), 1e-12, "maxLon must be the easternmost lon");
        }

        @Test
        @DisplayName("All coordinates at the same point: MBR is zero-area")
        void computeMbr_identicalCoordinates_zeroAreaMbr() {
            List<Coordinate> coords = List.of(
                    new Coordinate(55.0, 12.0),
                    new Coordinate(55.0, 12.0),
                    new Coordinate(55.0, 12.0)
            );
            BoundingBox mbr = BoundingBox.computeMbr(coords);
            assertEquals(0.0, mbr.area(), "MBR of identical coordinates must have area 0");
        }
    }


    //  areaIncreaseNeeded()

    @Nested
    @DisplayName("areaIncreaseNeeded()")
    class AreaIncreaseTests {

        @Test
        @DisplayName("MBR already inside this box: increase needed is 0")
        void areaIncreaseNeeded_mbrAlreadyInside_returnsZero() {
            BoundingBox outer = box(54.0, 11.0, 57.0, 14.0);
            BoundingBox inner = box(55.0, 12.0, 56.0, 13.0);
            assertEquals(0.0, outer.areaIncreaseNeeded(inner), 1e-12,
                    "No area increase needed when MBR is already contained");
        }

        @Test
        @DisplayName("MBR outside this box: increase needed is positive")
        void areaIncreaseNeeded_mbrOutside_returnsPositive() {
            BoundingBox base    = box(54.0, 11.0, 55.0, 12.0);
            BoundingBox outside = box(60.0, 20.0, 65.0, 25.0);
            assertTrue(base.areaIncreaseNeeded(outside) > 0,
                    "Area increase must be positive when MBR lies outside the box");
        }

        @Test
        @DisplayName("areaIncreaseNeeded is never negative")
        void areaIncreaseNeeded_neverNegative() {
            BoundingBox base = box(54.0, 11.0, 57.0, 14.0);
            BoundingBox mbr  = box(55.0, 12.0, 56.0, 13.0); // already inside
            assertTrue(base.areaIncreaseNeeded(mbr) >= 0,
                    "areaIncreaseNeeded must never return a negative value");
        }
    }

    //  equals(), hashCode(), copy()

    @Nested
    @DisplayName("equals(), hashCode(), copy()")
    class EqualityTests {

        @Test
        @DisplayName("Two boxes with identical values are equal")
        void equals_identicalValues_returnsTrue() {
            BoundingBox a = box(54.0, 11.0, 57.0, 14.0);
            BoundingBox b = box(54.0, 11.0, 57.0, 14.0);
            assertEquals(a, b, "Boxes with identical values must be equal");
        }

        @Test
        @DisplayName("Two boxes with different values are not equal")
        void equals_differentValues_returnsFalse() {
            BoundingBox a = box(54.0, 11.0, 57.0, 14.0);
            BoundingBox b = box(54.0, 11.0, 57.0, 15.0); // maxLon differs
            assertNotEquals(a, b, "Boxes with different values must not be equal");
        }

        @Test
        @DisplayName("Equal boxes have the same hashCode")
        void hashCode_equalBoxes_sameHash() {
            BoundingBox a = box(54.0, 11.0, 57.0, 14.0);
            BoundingBox b = box(54.0, 11.0, 57.0, 14.0);
            assertEquals(a.hashCode(), b.hashCode(), "Equal boxes must have the same hashCode");
        }

        @Test
        @DisplayName("copy() returns a box equal to the original")
        void copy_equalsOriginal() {
            BoundingBox original = box(54.0, 11.0, 57.0, 14.0);
            BoundingBox copy = original.copy();
            assertEquals(original, copy, "copy() must return a box equal to the original");
        }

        @Test
        @DisplayName("copy() returns a distinct object")
        void copy_isDistinctObject() {
            BoundingBox original = box(54.0, 11.0, 57.0, 14.0);
            assertNotSame(original, original.copy(), "copy() must return a new object, not the same reference");
        }
    }

    //  getCenter()

    @Nested
    @DisplayName("getCenter()")
    class GetCenterTests {

        @Test
        @DisplayName("Center of a box anchored at origin is the geometric midpoint")
        void getCenter_zeroOriginBox_correctMidpoint() {
            BoundingBox b = box(0.0, 0.0, 10.0, 20.0);
            Coordinate center = b.getCenter();
            assertEquals(5.0,  center.getLat(), 1e-9, "Center lat must be midpoint");
            assertEquals(10.0, center.getLon(), 1e-9, "Center lon must be midpoint");
        }

        @Test
        @DisplayName("Center of a non-zero-origin box is the geometric midpoint")
        void getCenter_nonZeroOriginBox_correctMidpoint() {
            BoundingBox b = box(2.0, 10.0, 8.0, 20.0);
            Coordinate center = b.getCenter();
            assertEquals(5.0,  center.getLat(), 1e-9, "Center lat must be (2+8)/2 = 5");
            assertEquals(15.0, center.getLon(), 1e-9, "Center lon must be (10+20)/2 = 15");
        }
    }
}
