package util;

import models.geometry.AdaptivePath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.PathIterator;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AdaptivePathTest {


    //  Helpers

    private static double[] p(double x, double y) {
        return new double[]{x, y};
    }

    private static AdaptivePath open(double[]... coords) {
        return new AdaptivePath(List.of(coords), false);
    }

    private static AdaptivePath closed(double[]... coords) {
        return new AdaptivePath(List.of(coords), true);
    }

    /** Returns how many path segments match the given PathIterator type constant. */
    private static int countSegType(AdaptivePath path, int type) {
        int count = 0;
        PathIterator it = path.getPathIterator(null);
        double[] buf = new double[6];
        while (!it.isDone()) {
            if (it.currentSegment(buf) == type) count++;
            it.next();
        }
        return count;
    }

    /** Returns total number of path segments (SEG_MOVETO + SEG_LINETO + SEG_CLOSE). */
    private static int totalSegs(AdaptivePath path) {
        int count = 0;
        PathIterator it = path.getPathIterator(null);
        double[] buf = new double[6];
        while (!it.isDone()) {
            it.currentSegment(buf);
            count++;
            it.next();
        }
        return count;
    }

    /**
     * Generates n evenly-spaced points along the straight line from (x0,y0) to (x1,y1).
     * Every point lies exactly on the line — perpendicular distance is 0.
     */
    private static List<double[]> collinearPts(int n, double x0, double y0, double x1, double y1) {
        List<double[]> pts = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double t = (n == 1) ? 0.0 : (double) i / (n - 1);
            pts.add(new double[]{x0 + t * (x1 - x0), y0 + t * (y1 - y0)});
        }
        return pts;
    }


    //  Constructor

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("getRawPointCount() matches input list size")
        void constructor_rawPointCount_matchesInput() {
            AdaptivePath path = open(p(0, 0), p(1, 0), p(2, 0), p(3, 0));
            assertEquals(4, path.getRawPointCount(),
                    "getRawPointCount() must equal the number of points passed to the constructor");
        }

        @Test
        @DisplayName("Empty list: valid path with 0 raw points")
        void constructor_emptyList_zeroPoints() {
            AdaptivePath path = new AdaptivePath(Collections.emptyList(), false);
            assertEquals(0, path.getRawPointCount(), "Empty input must produce 0 raw points");
            assertEquals(0, totalSegs(path), "Empty path must have no segments");
        }

        @Test
        @DisplayName("Single point: valid path with 1 raw point")
        void constructor_singlePoint_oneRawPoint() {
            AdaptivePath path = open(p(1.0, 2.0));
            assertEquals(1, path.getRawPointCount(), "Single-point input must produce 1 raw point");
        }

        @Test
        @DisplayName("Open path has no SEG_CLOSE segment")
        void constructor_openPath_noCloseSegment() {
            AdaptivePath path = open(p(0, 0), p(1, 0), p(2, 1));
            assertEquals(0, countSegType(path, PathIterator.SEG_CLOSE),
                    "Open path must contain no SEG_CLOSE segment");
        }

        @Test
        @DisplayName("Closed path has exactly one SEG_CLOSE segment")
        void constructor_closedPath_hasCloseSegment() {
            AdaptivePath path = closed(p(0, 0), p(1, 0), p(2, 1));
            assertEquals(1, countSegType(path, PathIterator.SEG_CLOSE),
                    "Closed path must contain exactly one SEG_CLOSE segment");
        }

        @Test
        @DisplayName("First path segment is SEG_MOVETO at the first input coordinate")
        void constructor_startPoint_matchesFirstCoordinate() {
            double startX = 3.7, startY = -1.2;
            AdaptivePath path = open(p(startX, startY), p(5.0, 5.0));

            PathIterator it = path.getPathIterator(null);
            double[] coords = new double[6];
            int type = it.currentSegment(coords);

            assertEquals(PathIterator.SEG_MOVETO, type, "First segment must be SEG_MOVETO");
            assertEquals(startX, coords[0], 1e-12, "First point x must match the first input coordinate");
            assertEquals(startY, coords[1], 1e-12, "First point y must match the first input coordinate");
        }
    }


    //  Initial path contents, full resolution built at construction time

    @Nested
    @DisplayName("Initial path contents")
    class InitialPathContentTests {

        @Test
        @DisplayName("Open path: 1 moveTo + (n-1) lineTo, no close")
        void initial_openPath_correctSegmentBreakdown() {
            int n = 6;
            AdaptivePath path = new AdaptivePath(collinearPts(n, 0, 0, 10, 0), false);
            assertEquals(1,   countSegType(path, PathIterator.SEG_MOVETO), "Should have exactly 1 moveTo");
            assertEquals(n-1, countSegType(path, PathIterator.SEG_LINETO), "Should have n-1 lineTo segments");
            assertEquals(0,   countSegType(path, PathIterator.SEG_CLOSE),  "Open path should have no close");
        }

        @Test
        @DisplayName("Closed path: 1 moveTo + (n-1) lineTo + 1 close")
        void initial_closedPath_correctSegmentBreakdown() {
            int n = 6;
            AdaptivePath path = new AdaptivePath(collinearPts(n, 0, 0, 10, 0), true);
            assertEquals(1,   countSegType(path, PathIterator.SEG_MOVETO), "Should have exactly 1 moveTo");
            assertEquals(n-1, countSegType(path, PathIterator.SEG_LINETO), "Should have n-1 lineTo segments");
            assertEquals(1,   countSegType(path, PathIterator.SEG_CLOSE),  "Closed path should have 1 close");
        }
    }


    //  updateForZoom, simplification behaviour

    @Nested
    @DisplayName("updateForZoom — simplification")
    class SimplificationTests {

        @Test
        @DisplayName("Two-point path: updateForZoom leaves segment count unchanged")
        void update_twoPoints_noSimplification() {
            AdaptivePath path = open(p(0, 0), p(10, 0));
            int before = totalSegs(path);
            path.updateForZoom(0.0);
            assertEquals(before, totalSegs(path),
                    "A two-point path has nothing to simplify; segment count must stay the same");
        }

        @Test
        @DisplayName("Collinear points: all intermediate points dropped")
        void update_collinearPoints_onlyEndpointsRemain() {
            // 10 points on a perfectly straight horizontal line — perp distance is 0 for every middle point
            AdaptivePath path = new AdaptivePath(collinearPts(10, 0, 0, 10, 0), false);
            path.updateForZoom(0.0); // epsilon = 2.0 — still drops anything at distance 0
            assertEquals(1, countSegType(path, PathIterator.SEG_MOVETO));
            assertEquals(1, countSegType(path, PathIterator.SEG_LINETO),
                    "All intermediate collinear points must be dropped (only 1 lineTo for the end)");
        }

        @Test
        @DisplayName("Low zoom produces fewer segments than high zoom")
        void update_lowZoomVsHighZoom_fewerSegmentsAtLowZoom() {
            // Zigzag with 0.001 deviation — clearly above epsilon at zoom=15 but far below at zoom=0
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < 20; i++) pts.add(new double[]{i, (i % 2 == 0) ? 0.001 : -0.001});

            AdaptivePath highZoom = new AdaptivePath(pts, false);
            AdaptivePath lowZoom  = new AdaptivePath(pts, false);
            highZoom.updateForZoom(15.0); // epsilon ≈ 6e-5: 0.001 >> epsilon → many points kept
            lowZoom.updateForZoom(0.0);   // epsilon  = 2.0:  0.001 << epsilon → nearly all dropped

            assertTrue(totalSegs(lowZoom) < totalSegs(highZoom),
                    "Low zoom (large epsilon) must retain fewer segments than high zoom (small epsilon)");
        }

        @Test
        @DisplayName("Closed path remains closed after simplification")
        void update_closedPath_remainsClosed() {
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < 10; i++) pts.add(new double[]{i, (i % 2 == 0) ? 0.5 : -0.5});
            AdaptivePath path = new AdaptivePath(pts, true);
            path.updateForZoom(5.0);
            assertEquals(1, countSegType(path, PathIterator.SEG_CLOSE),
                    "Closed path must retain exactly one SEG_CLOSE after zoom update");
        }

        @Test
        @DisplayName("Open path remains open after simplification")
        void update_openPath_remainsOpen() {
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < 10; i++) pts.add(new double[]{i, (i % 2 == 0) ? 0.5 : -0.5});
            AdaptivePath path = new AdaptivePath(pts, false);
            path.updateForZoom(5.0);
            assertEquals(0, countSegType(path, PathIterator.SEG_CLOSE),
                    "Open path must have no SEG_CLOSE after zoom update");
        }

        @Test
        @DisplayName("getRawPointCount() is unaffected by simplification")
        void update_rawPointCount_neverChanges() {
            List<double[]> pts = collinearPts(50, 0, 0, 100, 0);
            AdaptivePath path = new AdaptivePath(pts, false);
            int before = path.getRawPointCount();
            path.updateForZoom(0.0); // drops almost every point
            assertEquals(before, path.getRawPointCount(),
                    "getRawPointCount() must reflect the original input, not the simplified path");
        }

        @Test
        @DisplayName("Repeated call with same quantized zoom: segment count unchanged")
        void update_sameQuantizedZoom_pathUnchanged() {
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < 20; i++) pts.add(new double[]{i, (i % 3 == 0) ? 0.01 : 0.0});
            AdaptivePath path = new AdaptivePath(pts, false);

            path.updateForZoom(10.0);
            int countAfterFirst = totalSegs(path);

            path.updateForZoom(10.0); // same zoom, no rebuild expected
            assertEquals(countAfterFirst, totalSegs(path),
                    "Repeating the exact same zoom level must not alter the path");
        }

        @Test
        @DisplayName("Nearby zoom values that share a quantized bucket produce the same result")
        void update_nearbyZoomSameBucket_pathConsistent() {
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < 20; i++) pts.add(new double[]{i, (i % 3 == 0) ? 0.01 : 0.0});
            AdaptivePath path = new AdaptivePath(pts, false);

            path.updateForZoom(10.0);
            int countAt10 = totalSegs(path);

            // 10.4 rounds to 10 — same quantized bucket, path should be unchanged
            path.updateForZoom(10.4);
            assertEquals(countAt10, totalSegs(path),
                    "zoom=10.4 and zoom=10.0 share the same quantized bucket and must yield the same path");
        }

        @Test
        @DisplayName("Switching zoom level triggers a rebuild with different point retention")
        void update_differentZoom_segmentCountChanges() {
            // Small zigzag: deviations of 0.001
            // zoom=0  → epsilon=2.0  → 0.001 << 2.0   → all middle points dropped (2 segs)
            // zoom=15 → epsilon≈6e-5 → 0.001 >>  6e-5  → many points kept
            List<double[]> pts = new ArrayList<>();
            pts.add(new double[]{0.0, 0.0});
            for (int i = 1; i <= 18; i++) pts.add(new double[]{i, (i % 2 == 0) ? 0.001 : -0.001});
            pts.add(new double[]{19.0, 0.0});

            AdaptivePath path = new AdaptivePath(pts, false);
            path.updateForZoom(0.0);
            int atLowZoom = totalSegs(path);

            path.updateForZoom(15.0);
            int atHighZoom = totalSegs(path);

            assertTrue(atHighZoom > atLowZoom,
                    "Switching from zoom=0 to zoom=15 must increase retained segment count");
        }
    }


    //  Ramer-Douglas-Peucker algorithm

    @Nested
    @DisplayName("Ramer-Douglas-Peucker algorithm")
    class RamerDouglasPeuckerTests {

        @Test
        @DisplayName("Three collinear points: middle is always dropped")
        void rdp_threeCollinearPoints_middleDropped() {
            // (0,0)→(5,0)→(10,0): middle lies on the line, perp distance = 0
            AdaptivePath path = open(p(0, 0), p(5, 0), p(10, 0));
            path.updateForZoom(0.0); // epsilon=2.0; distance=0 < 2.0 → drop
            assertEquals(2, totalSegs(path),
                    "Collinear middle point (dist=0) must be dropped, leaving only moveTo + lineTo");
        }

        @Test
        @DisplayName("Apex above epsilon: apex is kept")
        void rdp_apexAboveEpsilon_apexKept() {
            // (0,0)→(5,5)→(10,0): perp distance from (5,5) to line = 5.0
            // zoom=0 → epsilon=4.0; 5.0 > 4.0 → apex kept
            AdaptivePath path = open(p(0, 0), p(5, 5), p(10, 0));
            path.updateForZoom(0.0);
            assertEquals(3, totalSegs(path),
                    "Apex with perp distance 5.0 must be kept when epsilon=4.0 (1 moveTo + 2 lineTo)");
        }

        @Test
        @DisplayName("Apex below epsilon: apex is dropped")
        void rdp_apexBelowEpsilon_apexDropped() {
            // (0,0)→(5,1)→(10,0): perp distance from (5,1) to line = 1.0
            // zoom=0 → epsilon=4.0; 1.0 < 4.0 → apex dropped
            AdaptivePath path = open(p(0, 0), p(5, 1), p(10, 0));
            path.updateForZoom(0.0);
            assertEquals(2, totalSegs(path),
                    "Apex with perp distance 1.0 must be dropped when epsilon=4.0");
        }

        @Test
        @DisplayName("Two significant peaks: both are retained")
        void rdp_twoSignificantPeaks_bothRetained() {
            // Peaks at y=10 ensure each point exceeds epsilon=4.0 in its recursive sub-segment.
            // (3,10) deviates 10.0 from baseline; after it is kept, both (6,0) and (9,10)
            // deviate ~4.46 from the sub-segment (3,10)→(12,0), still above epsilon=4.0.
            AdaptivePath path = open(p(0, 0), p(3, 10), p(6, 0), p(9, 10), p(12, 0));
            path.updateForZoom(0.0); // epsilon=4.0; all five points survive
            assertEquals(5, totalSegs(path),
                    "All five points must be kept when both peaks deviate above epsilon=4.0");
        }

        @Test
        @DisplayName("High zoom retains at least as many points as low zoom")
        void rdp_nestedDeviations_highZoomRetainsMore() {
            List<double[]> pts = new ArrayList<>();
            pts.add(new double[]{0,  0});
            pts.add(new double[]{5,  0.001}); // tiny — only visible at high zoom
            pts.add(new double[]{10, 0.1});   // visible at medium zoom
            pts.add(new double[]{15, 0.001});
            pts.add(new double[]{20, 0});

            AdaptivePath highZoom = new AdaptivePath(pts, false);
            AdaptivePath lowZoom  = new AdaptivePath(pts, false);
            highZoom.updateForZoom(15.0);
            lowZoom.updateForZoom(0.0);

            assertTrue(totalSegs(highZoom) >= totalSegs(lowZoom),
                    "High zoom must retain at least as many segments as low zoom");
        }

        @Test
        @DisplayName("First and last point always survive simplification")
        void rdp_endpoints_alwaysSurvive() {
            double startX = 0.0, startY = 0.0, endX = 10.0, endY = 0.0;
            List<double[]> pts = collinearPts(20, startX, startY, endX, endY);
            AdaptivePath path = new AdaptivePath(pts, false);
            path.updateForZoom(0.0); // aggressive simplification — only endpoints should remain

            PathIterator it = path.getPathIterator(null);
            double[] coords = new double[6];

            // Check start point
            it.currentSegment(coords);
            assertEquals(startX, coords[0], 1e-12, "Start x must match after simplification");
            assertEquals(startY, coords[1], 1e-12, "Start y must match after simplification");

            // Advance to last lineTo
            double lastX = 0, lastY = 0;
            while (!it.isDone()) {
                int type = it.currentSegment(coords);
                if (type == PathIterator.SEG_LINETO) { lastX = coords[0]; lastY = coords[1]; }
                it.next();
            }
            assertEquals(endX, lastX, 1e-12, "End x must match after simplification");
            assertEquals(endY, lastY, 1e-12, "End y must match after simplification");
        }

        @Test
        @DisplayName("Collinear midpoint at epsilon=tiny: still dropped (distance is exactly 0)")
        void rdp_collinearMidpoint_droppedAtAnyEpsilon() {
            // A point on the line has distance 0 — it is dropped regardless of how small epsilon is
            AdaptivePath path = open(p(0, 0), p(5, 0), p(10, 0));
            path.updateForZoom(100.0); // epsilon ≈ 2/2^100 ≈ 1.6e-30, yet distance=0 is still below
            assertEquals(2, totalSegs(path),
                    "A collinear midpoint with distance 0 must be dropped even at extreme zoom");
        }
    }


    //  Edge cases & stress

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Duplicate coordinates: intermediate identical points are dropped")
        void edgeCase_duplicateCoordinates_intermediatePointsDropped() {
            // All points share the same location — perp distance to the straight line is 0
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < 10; i++) pts.add(new double[]{5.0, 5.0});
            AdaptivePath path = new AdaptivePath(pts, false);
            path.updateForZoom(0.0);
            assertTrue(totalSegs(path) <= 2,
                    "Identical intermediate points have distance 0 and must be dropped");
        }

        @Test
        @DisplayName("Very high zoom: tiny epsilon retains small deviations")
        void edgeCase_veryHighZoom_smallDeviationsRetained() {
            // Zigzag with deviations of 0.0001 — above epsilon at zoom=20 (≈1.9e-6) but not at zoom=0
            int n = 20;
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < n; i++) pts.add(new double[]{i, (i % 2 == 0) ? 0.0001 : -0.0001});

            AdaptivePath path = new AdaptivePath(pts, false);
            path.updateForZoom(20.0);

            assertTrue(totalSegs(path) > 2,
                    "At zoom=20 (epsilon≈1.9e-6), deviations of 0.0001 must be visible and retained");
        }

        @Test
        @DisplayName("Very low zoom: large epsilon drops small deviations")
        void edgeCase_veryLowZoom_smallDeviationsDropped() {
            // Same deviations of 0.0001 — invisible at zoom=0 (epsilon=2.0)
            int n = 20;
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < n; i++) pts.add(new double[]{i, (i % 2 == 0) ? 0.0001 : -0.0001});

            AdaptivePath path = new AdaptivePath(pts, false);
            path.updateForZoom(0.0); // epsilon=2.0 >> 0.0001 → all dropped

            assertEquals(2, totalSegs(path),
                    "At zoom=0 (epsilon=2.0), deviations of 0.0001 must all be dropped");
        }

        @Test
        @DisplayName("Serialization round-trip preserves raw point count and closed flag")
        void edgeCase_serialization_roundTrip() throws Exception {
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < 10; i++) pts.add(new double[]{i, (i % 2 == 0) ? 0.5 : -0.5});
            AdaptivePath original = new AdaptivePath(pts, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject(original);

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            AdaptivePath restored = (AdaptivePath) ois.readObject();

            assertEquals(original.getRawPointCount(), restored.getRawPointCount(),
                    "Deserialized path must have the same raw point count");
            assertEquals(
                    countSegType(original, PathIterator.SEG_CLOSE),
                    countSegType(restored,  PathIterator.SEG_CLOSE),
                    "Deserialized path must preserve the closed flag");
        }

        @Test
        @DisplayName("Stress: 500-point path has fewer segments at low zoom than at high zoom")
        void stress_500PointPath_lowZoomFewerSegments() {
            int n = 500;
            List<double[]> pts = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                pts.add(new double[]{i, Math.sin(i * 0.1) * 0.001});
            }
            AdaptivePath lowZoom  = new AdaptivePath(pts, false);
            AdaptivePath highZoom = new AdaptivePath(pts, false);
            lowZoom.updateForZoom(0.0);
            highZoom.updateForZoom(15.0);

            assertTrue(totalSegs(lowZoom) < totalSegs(highZoom),
                    "500-point sinusoidal path must have fewer segments at zoom=0 than zoom=15");
        }
    }
}
