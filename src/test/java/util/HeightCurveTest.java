package util;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class HeightCurveTest {

    private static final double PATHS_EQUAL_EPS = 1e-9;

    private record Point(double x, double y) {

    }

    private static boolean pathsEqual(Path2D left, Path2D right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        List<List<Point>> leftPaths = canonicalizePath(left);
        List<List<Point>> rightPaths = canonicalizePath(right);

        if (leftPaths.size() != rightPaths.size()) {
            return false;
        }

        leftPaths.sort(HeightCurveTest::comparePointLists);
        rightPaths.sort(HeightCurveTest::comparePointLists);

        for (int i = 0; i < leftPaths.size(); i++) {
            if (comparePointLists(leftPaths.get(i), rightPaths.get(i)) != 0) {
                return false;
            }
        }
        return true;
    }

    private static List<List<Point>> canonicalizePath(Path2D path) {
        List<List<Point>> raw = extractSubpaths(path);
        List<List<Point>> out = new ArrayList<>(raw.size());
        for (List<Point> points : raw) {
            if (!points.isEmpty()) {
                out.add(canonicalCycle(points));
            }
        }
        return out;
    }

    private static List<List<Point>> extractSubpaths(Path2D path) {
        PathIterator it = path.getPathIterator(null);
        double[] buf = new double[6];
        List<List<Point>> out = new ArrayList<>();
        List<Point> current = null;
        while (!it.isDone()) {
            int type = it.currentSegment(buf);
            switch (type) {
                case PathIterator.SEG_MOVETO -> {
                    if (current != null) {
                        finalizeSubpath(current, out);
                    }
                    current = new ArrayList<>();
                    current.add(new Point(buf[0], buf[1]));
                }
                case PathIterator.SEG_LINETO -> {
                    if (current == null) {
                        current = new ArrayList<>();
                    }
                    current.add(new Point(buf[0], buf[1]));
                }
                case PathIterator.SEG_CLOSE -> {
                    if (current != null) {
                        finalizeSubpath(current, out);
                        current = null;
                    }
                }
                default -> throw new AssertionError("Illegal segment type " + type);
            }
            it.next();
        }
        if (current != null) {
            finalizeSubpath(current, out);
        }
        return out;
    }

    private static void finalizeSubpath(List<Point> points, List<List<Point>> out) {
        if (points.isEmpty()) {
            return;
        }
        if (points.size() > 1 && pointsEqual(points.get(points.size() - 1), points.get(0))) {
            points.remove(points.size() - 1);
        }
        out.add(points);
    }

    private static List<Point> canonicalCycle(List<Point> points) {
        if (points.size() < 2) {
            return points;
        }
        List<Point> forward = minimalRotation(points);
        List<Point> reversed = new ArrayList<>(points);
        java.util.Collections.reverse(reversed);
        List<Point> backward = minimalRotation(reversed);
        return comparePointLists(forward, backward) <= 0 ? forward : backward;
    }

    private static List<Point> minimalRotation(List<Point> points) {
        int n = points.size();
        int best = 0;
        for (int i = 0; i < n; i++) {
            if (compareRotation(points, i, best) < 0) {
                best = i;
            }
        }
        List<Point> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            out.add(points.get((best + i) % n));
        }
        return out;
    }

    private static int compareRotation(List<Point> points, int startA, int startB) {
        int n = points.size();
        for (int i = 0; i < n; i++) {
            Point a = points.get((startA + i) % n);
            Point b = points.get((startB + i) % n);
            int cmp = comparePoints(a, b);
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    private static int comparePointLists(List<Point> left, List<Point> right) {
        int n = Math.min(left.size(), right.size());
        for (int i = 0; i < n; i++) {
            int cmp = comparePoints(left.get(i), right.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(left.size(), right.size());
    }

    private static int comparePoints(Point left, Point right) {
        double dx = left.x - right.x;
        if (Math.abs(dx) > PATHS_EQUAL_EPS) {
            return Double.compare(left.x, right.x);
        }
        double dy = left.y - right.y;
        if (Math.abs(dy) > PATHS_EQUAL_EPS) {
            return Double.compare(left.y, right.y);
        }
        return 0;
    }

    private static boolean pointsEqual(Point left, Point right) {
           return (Math.abs(left.x - right.x) <= PATHS_EQUAL_EPS) && (Math.abs(left.y - right.y) <= PATHS_EQUAL_EPS);
    }
    private static HeightCurve curve(long id, double height, Coordinate... coords) {
         List<Coordinate> list = new ArrayList<>();
         list.addAll(Arrays.asList(coords));
         return new HeightCurve(id, height, list);
    }
    private static List<Coordinate> squareCoords(double lat, double lon, double size) {
        List<Coordinate> list = new ArrayList<>();
        list.add(new Coordinate(lat, lon));
        list.add(new Coordinate(lat, lon + size));
        list.add(new Coordinate(lat + size, lon));
        list.add(new Coordinate(lat, lon));
        return list;
    }

    private static HeightCurve curve(long id, double height) {
        return new HeightCurve(id, height, squareCoords(0.0, 0.0, 1.0));
    }

    // 1) Smoke / return-type tests
    @Test
    @Order(1)
    public void boundaryPath_returnsNonNullPath2D() {
        HeightCurve c = curve(1L, 0.0,
                new Coordinate(1.0, 2.0),
                new Coordinate(2.0, 3.0),
                new Coordinate(3.0, 4.0),
                new Coordinate(1.0, 2.0)
        );
        assertNotNull(c.getBoundaryPath(1.0), "Task 'Construct the boundary path': getBoundaryPath() must return a Path2D (not null)");
    }

    @Test
    @Order(2)
    public void regionPath_returnsNonNullPath2D() {
        HeightCurve c = curve(1L, 0.0,
                new Coordinate(1.0, 2.0),
                new Coordinate(2.0, 3.0),
                new Coordinate(3.0, 4.0),
                new Coordinate(1.0, 2.0)
        );
        assertNotNull(c.getRegionPath(1.0), "Task 'Construct the region path': getRegionPath() must return a Path2D (not null)");
    }

    @Test
    @Order(3)
    public void fillColor_returnsNonNullColor() {
        HeightCurve c = curve(1L, 0.0,
                new Coordinate(1.0, 2.0),
                new Coordinate(2.0, 3.0),
                new Coordinate(3.0, 4.0),
                new Coordinate(1.0, 2.0)
        );
        assertNotNull(c.getFillColor(0.0), "Task 'Colour the region': getFillColor(seaLevel) must return a Color (not null)");
    }

    @Test
    @Order(4)
    public void submerge_doesNotThrow() {
        HeightCurve sea = new HeightCurve(0L, -1.0, squareCoords(0.0, 0.0, 2.0), new ArrayList<>());
        HeightCurve island0m = curve(1L, 0.0,
                new Coordinate(0.0, 0.0),
                new Coordinate(0.0, 1.0),
                new Coordinate(1.0, 0.0),
                new Coordinate(0.0, 0.0)
        );
        sea.getChildren().add(island0m);
        sea.resetSubmerged();
        sea.submerge(1.0);
    }

    // 2) Task 'Construct the boundary path'
    @Test
    @Order(10)
    public void boundaryPath_usesLonAsX_andLatAsY() {
        // Note: Coordinate constructor is (lat, lon), but the projection used for paths is x=lon and y=lat.
        HeightCurve c = curve(
                1L,
                0.0,
                new Coordinate(55.0, 10.0),
                new Coordinate(56.0, 11.0),
                new Coordinate(57.0, 12.0),
                new Coordinate(55.0, 10.0)
        );

        Path2D expected = new Path2D.Double();
        expected.moveTo(10.0, 55.0);
        expected.lineTo(11.0, 56.0);
        expected.lineTo(12.0, 57.0);
        expected.lineTo(10.0, 55.0);
        assertTrue(
                pathsEqual(expected, c.getBoundaryPath(1.0)),
                "Boundary path should match the expected polygon regardless of start point or direction"
        );
    }

    // 3) Task 'Construct the region path'
    @Test
    @Order(20)
    public void regionPath_usesEvenOddWindingRule() {
        HeightCurve c = curve(1L, 0.0,
                new Coordinate(0.0, 0.0),
                new Coordinate(0.0, 1.0),
                new Coordinate(1.0, 0.0),
                new Coordinate(0.0, 0.0)
        );
        Path2D expected = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        expected.moveTo(0.0, 0.0);
        expected.lineTo(1.0, 0.0);
        expected.lineTo(0.0, 1.0);
        expected.lineTo(0.0, 0.0);
        assertEquals(
                Path2D.WIND_EVEN_ODD,
                c.getRegionPath(1.0).getWindingRule(),
                "Task 'Construct the region path': use WIND_EVEN_ODD so child curves become holes"
        );
        assertTrue(
                pathsEqual(expected, c.getRegionPath(1.0)),
                "Region path geometry should match the boundary polygon"
        );
    }

    @Test
    @Order(21)
    public void regionPath_includesBoundaryAndImmediateChildBoundary() {
        HeightCurve parent = curve(1L, 0.0,
                new Coordinate(0.0, 0.0),
                new Coordinate(0.0, 4.0),
                new Coordinate(4.0, 0.0),
                new Coordinate(0.0, 0.0)
        );
        HeightCurve child = curve(2L, 2.5,
                new Coordinate(1.0, 1.0),
                new Coordinate(1.0, 2.0),
                new Coordinate(2.0, 1.0),
                new Coordinate(1.0, 1.0)
        );
        parent.getChildren().add(child);

        Path2D expected = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        expected.moveTo(0.0, 0.0);
        expected.lineTo(4.0, 0.0);
        expected.lineTo(0.0, 4.0);
        expected.lineTo(0.0, 0.0);
        expected.moveTo(1.0, 1.0);
        expected.lineTo(2.0, 1.0);
        expected.lineTo(1.0, 2.0);
        expected.lineTo(1.0, 1.0);
        assertTrue(
                pathsEqual(expected, parent.getRegionPath(1.0)),
                "Region path should include the parent boundary and immediate child as a hole"
        );
    }

    @Test
    @Order(22)
    public void regionPath_usesOnlyImmediateChildren_noRecursion() {
        HeightCurve parent = curve(1L, 0.0,
                new Coordinate(0.0, 0.0),
                new Coordinate(0.0, 4.0),
                new Coordinate(4.0, 0.0),
                new Coordinate(0.0, 0.0)
        );
        HeightCurve child = curve(2L, 2.5,
                new Coordinate(1.0, 1.0),
                new Coordinate(1.0, 2.0),
                new Coordinate(2.0, 1.0),
                new Coordinate(1.0, 1.0)
        );
        HeightCurve grandchild = curve(3L, 5.0,
                new Coordinate(1.2, 1.2),
                new Coordinate(1.2, 1.3),
                new Coordinate(1.3, 1.2),
                new Coordinate(1.2, 1.2)
        );

        parent.getChildren().add(child);
        child.getChildren().add(grandchild);

        Path2D expected = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        expected.moveTo(0.0, 0.0);
        expected.lineTo(4.0, 0.0);
        expected.lineTo(0.0, 4.0);
        expected.lineTo(0.0, 0.0);
        expected.moveTo(1.0, 1.0);
        expected.lineTo(2.0, 1.0);
        expected.lineTo(1.0, 2.0);
        expected.lineTo(1.0, 1.0);
        assertTrue(
                pathsEqual(expected, parent.getRegionPath(1.0)),
                "Region path should include only the parent and immediate child boundaries"
        );
    }

    @Test
    @Order(23)
    public void pathsEqual_handlesHoles_andCloseVsLineEnding() {
        Path2D expected = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        expected.moveTo(0.0, 0.0);
        expected.lineTo(2.0, 0.0);
        expected.lineTo(3.0, 1.0);
        expected.lineTo(2.0, 2.0);
        expected.lineTo(0.0, 2.0);
        expected.lineTo(-1.0, 1.0);
        expected.closePath();
        expected.moveTo(0.5, 0.5);
        expected.lineTo(1.5, 0.5);
        expected.lineTo(1.0, 1.5);
        expected.closePath();

        Path2D bothLineTo = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        bothLineTo.moveTo(0.0, 0.0);
        bothLineTo.lineTo(2.0, 0.0);
        bothLineTo.lineTo(3.0, 1.0);
        bothLineTo.lineTo(2.0, 2.0);
        bothLineTo.lineTo(0.0, 2.0);
        bothLineTo.lineTo(-1.0, 1.0);
        bothLineTo.lineTo(0.0, 0.0);
        bothLineTo.moveTo(0.5, 0.5);
        bothLineTo.lineTo(1.5, 0.5);
        bothLineTo.lineTo(1.0, 1.5);
        bothLineTo.lineTo(0.5, 0.5);

        Path2D outerLine_innerClose = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        outerLine_innerClose.moveTo(0.0, 0.0);
        outerLine_innerClose.lineTo(2.0, 0.0);
        outerLine_innerClose.lineTo(3.0, 1.0);
        outerLine_innerClose.lineTo(2.0, 2.0);
        outerLine_innerClose.lineTo(0.0, 2.0);
        outerLine_innerClose.lineTo(-1.0, 1.0);
        outerLine_innerClose.lineTo(0.0, 0.0);
        outerLine_innerClose.moveTo(0.5, 0.5);
        outerLine_innerClose.lineTo(1.5, 0.5);
        outerLine_innerClose.lineTo(1.0, 1.5);
        outerLine_innerClose.closePath();

        Path2D outerClose_innerLine = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        outerClose_innerLine.moveTo(0.0, 0.0);
        outerClose_innerLine.lineTo(2.0, 0.0);
        outerClose_innerLine.lineTo(3.0, 1.0);
        outerClose_innerLine.lineTo(2.0, 2.0);
        outerClose_innerLine.lineTo(0.0, 2.0);
        outerClose_innerLine.lineTo(-1.0, 1.0);
        outerClose_innerLine.closePath();
        outerClose_innerLine.moveTo(0.5, 0.5);
        outerClose_innerLine.lineTo(1.5, 0.5);
        outerClose_innerLine.lineTo(1.0, 1.5);
        outerClose_innerLine.lineTo(0.5, 0.5);

        assertTrue(pathsEqual(expected, bothLineTo), "pathsEqual should accept lineTo closure for both paths");
        assertTrue(pathsEqual(expected, outerLine_innerClose), "pathsEqual should accept lineTo closure for outer path");
        assertTrue(pathsEqual(expected, outerClose_innerLine), "pathsEqual should accept lineTo closure for inner path");
    }

    @Test
    @Order(24)
    public void pathsEqual_ignoresSubpathOrder_andDirection() {
        Path2D expected = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        expected.moveTo(0.0, 0.0);
        expected.lineTo(2.0, 0.0);
        expected.lineTo(3.0, 1.0);
        expected.lineTo(2.0, 2.0);
        expected.lineTo(0.0, 2.0);
        expected.lineTo(-1.0, 1.0);
        expected.closePath();
        expected.moveTo(0.5, 0.5);
        expected.lineTo(1.5, 0.5);
        expected.lineTo(1.0, 1.5);
        expected.closePath();

        Path2D variant = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        variant.moveTo(1.0, 1.5);
        variant.lineTo(1.5, 0.5);
        variant.lineTo(0.5, 0.5);
        variant.closePath();
        variant.moveTo(-1.0, 1.0);
        variant.lineTo(0.0, 2.0);
        variant.lineTo(2.0, 2.0);
        variant.lineTo(3.0, 1.0);
        variant.lineTo(2.0, 0.0);
        variant.lineTo(0.0, 0.0);
        variant.closePath();

        assertTrue(
                pathsEqual(expected, variant),
                "pathsEqual should ignore subpath order and direction"
        );
    }

    @Test
    @Order(25)
    public void pathsEqual_handlesMultipleDisjointPolygons() {
        Path2D expected = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        expected.moveTo(0.0, 0.0);
        expected.lineTo(1.0, 0.0);
        expected.lineTo(1.0, 1.0);
        expected.lineTo(0.0, 1.0);
        expected.closePath();
        expected.moveTo(3.0, 3.0);
        expected.lineTo(4.0, 3.0);
        expected.lineTo(4.0, 4.0);
        expected.lineTo(3.0, 4.0);
        expected.closePath();

        Path2D variant = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        variant.moveTo(4.0, 3.0);
        variant.lineTo(4.0, 4.0);
        variant.lineTo(3.0, 4.0);
        variant.lineTo(3.0, 3.0);
        variant.closePath();
        variant.moveTo(1.0, 0.0);
        variant.lineTo(1.0, 1.0);
        variant.lineTo(0.0, 1.0);
        variant.lineTo(0.0, 0.0);
        variant.closePath();

        assertTrue(
                pathsEqual(expected, variant),
                "pathsEqual should match disjoint polygons regardless of order or start point"
        );
    }

    @Test
    @Order(26)
    public void pathsEqual_detectsDifferentTopology() {
        Path2D expected = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        expected.moveTo(0.0, 0.0);
        expected.lineTo(2.0, 0.0);
        expected.lineTo(3.0, 1.0);
        expected.lineTo(2.0, 2.0);
        expected.lineTo(0.0, 2.0);
        expected.lineTo(-1.0, 1.0);
        expected.closePath();
        expected.moveTo(0.5, 0.5);
        expected.lineTo(1.5, 0.5);
        expected.lineTo(1.0, 1.5);
        expected.closePath();

        Path2D variant = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        variant.moveTo(0.0, 0.0);
        variant.lineTo(2.0, 0.0);
        variant.lineTo(3.0, 1.0);
        variant.lineTo(2.0, 2.0);
        variant.lineTo(0.0, 2.0);
        variant.lineTo(-1.0, 1.0);
        variant.closePath();
        variant.moveTo(0.5, 0.5);
        variant.lineTo(1.5, 0.5);
        variant.lineTo(1.5, 1.5);
        variant.lineTo(0.5, 1.5);
        variant.closePath();

        assertFalse(
                pathsEqual(expected, variant),
                "pathsEqual should detect a different inner polygon"
        );
    }

    // 4) Task 'Colour the region'
    @Test
    @Order(30)
    public void fillColor_elevationBelow0_submergedTrue_isWater() {
        HeightCurve c = curve(1L, 0.0);
        c.submerged = true;
        Color expected = Color.decode("#2b8cbe");
        assertEquals(
                expected,
                c.getFillColor(1.0),
                "Task 'Colour the region': if elevation (height - seaLevel) < 0 and submerged=true, use water #2b8cbe"
        );
    }

    @Test
    @Order(31)
    public void fillColor_elevationBelow0_submergedFalse_isSand() {
        HeightCurve c = curve(1L, 0.0);
        c.submerged = false;
        Color expected = Color.decode("#ffffcc");
        assertEquals(
                expected,
                c.getFillColor(1.0),
                "Task 'Colour the region': if elevation (height - seaLevel) < 0 and submerged=false, use sand #ffffcc"
        );
    }

    @Test
    @Order(32)
    public void fillColor_thresholdBands_matchPalette() {
        // Elevation is height - seaLevel (see assignment text).
        // land0: elevation >= 0 and < 2.5
        HeightCurve land0 = curve(1L, 2.5);
        assertEquals(Color.decode("#c2e699"), land0.getFillColor(2.5), "land0 #c2e699 for 0 <= elevation < 2.5");

        // land1: elevation >= 2.5 and < 5
        HeightCurve land1 = curve(2L, 5.0);
        assertEquals(Color.decode("#78c679"), land1.getFillColor(2.5), "land1 #78c679 for 2.5 <= elevation < 5");

        // land2: elevation >= 5 and < 7.5
        HeightCurve land2 = curve(3L, 7.5);
        assertEquals(Color.decode("#31a354"), land2.getFillColor(2.5), "land2 #31a354 for 5 <= elevation < 7.5");

        // land3: elevation >= 7.5 and < 10.0 (avoid enforcing colors for elevation >= 10.0)
        HeightCurve land3 = curve(4L, 12.4);
        assertEquals(Color.decode("#006837"), land3.getFillColor(2.5), "land3 #006837 for 7.5 <= elevation < 10.0");
    }

    @Test
    @Order(33)
    public void fillColor_thresholdBoundaries_exactValues() {
        HeightCurve c = curve(1L, 0.0);
        assertEquals(Color.decode("#c2e699"), c.getFillColor(0.0), "elevation == 0 uses land0 #c2e699");

        HeightCurve land1 = curve(2L, 2.5);
        assertEquals(Color.decode("#78c679"), land1.getFillColor(0.0), "elevation == 2.5 uses land1 #78c679");

        HeightCurve land2 = curve(3L, 5.0);
        assertEquals(Color.decode("#31a354"), land2.getFillColor(0.0), "elevation == 5 uses land2 #31a354");

        HeightCurve land3 = curve(4L, 7.5);
        assertEquals(Color.decode("#006837"), land3.getFillColor(0.0), "elevation == 7.5 uses land3 #006837");
    }

    @Test
    @Order(34)
    public void fillColor_submergedIgnoredForNonNegativeElevation() {
        HeightCurve c = curve(1L, 2.5);
        c.submerged = true;
        assertEquals(
                Color.decode("#c2e699"),
                c.getFillColor(1.0),
                "Task 'Colour the region': submerged should not affect colors when elevation >= 0"
        );
    }

    @Test
    @Order(35)
    public void fillColor_thresholdEpsilon_checks() {
        double eps = 1e-9;
        HeightCurve near0 = curve(1L, eps);
        assertEquals(Color.decode("#c2e699"), near0.getFillColor(0.0), "elevation just above 0 uses land0");

        HeightCurve below2_5 = curve(2L, 2.5 - eps);
        assertEquals(Color.decode("#c2e699"), below2_5.getFillColor(0.0), "elevation just below 2.5 uses land0");

        HeightCurve above2_5 = curve(3L, 2.5 + eps);
        assertEquals(Color.decode("#78c679"), above2_5.getFillColor(0.0), "elevation just above 2.5 uses land1");

        HeightCurve below5 = curve(4L, 5.0 - eps);
        assertEquals(Color.decode("#78c679"), below5.getFillColor(0.0), "elevation just below 5 uses land1");

        HeightCurve above5 = curve(5L, 5.0 + eps);
        assertEquals(Color.decode("#31a354"), above5.getFillColor(0.0), "elevation just above 5 uses land2");

        HeightCurve below7_5 = curve(6L, 7.5 - eps);
        assertEquals(Color.decode("#31a354"), below7_5.getFillColor(0.0), "elevation just below 7.5 uses land2");

        HeightCurve above7_5 = curve(7L, 7.5 + eps);
        assertEquals(Color.decode("#006837"), above7_5.getFillColor(0.0), "elevation just above 7.5 uses land3");
    }

    // 5) Task 'Resetting the submergence state'
    @Test
    @Order(40)
    public void resetSubmerged_setsFalseRecursively() {
        HeightCurve root = new HeightCurve(0L, -1.0, squareCoords(0.0, 0.0, 2.0), new ArrayList<>());
        HeightCurve a = new HeightCurve(1L, 0.0, squareCoords(0.0, 0.0, 1.0), new ArrayList<>());
        HeightCurve b = new HeightCurve(2L, 2.5, squareCoords(0.0, 0.0, 0.5), new ArrayList<>());
        root.getChildren().add(a);
        a.getChildren().add(b);

        root.submerged = true;
        a.submerged = true;
        b.submerged = true;

        root.resetSubmerged();

        assertFalse(root.submerged, "Task 'Resetting the submergence state': resetSubmerged() must reset submerged=false");
        assertFalse(a.submerged, "Task 'Resetting the submergence state': resetSubmerged() must apply recursively to children");
        assertFalse(b.submerged, "Task 'Resetting the submergence state': resetSubmerged() must apply recursively to grandchildren");
    }

    // 6) Task 'Submerge correctly'
    @Test
    @Order(50)
    public void submerge_strictlyBelowSeaLevel_only() {
        HeightCurve sea = new HeightCurve(0L, -1.0, squareCoords(0.0, 0.0, 2.0), new ArrayList<>());
        HeightCurve exactlyAtSea = new HeightCurve(1L, 1.0, squareCoords(0.0, 0.0, 1.0), new ArrayList<>());
        sea.getChildren().add(exactlyAtSea);

        sea.resetSubmerged();
        sea.submerge(1.0);

        assertFalse(
                exactlyAtSea.submerged,
                "Task 'Submerge correctly': a curve becomes submerged only if its height is strictly below sea level"
        );
    }

    @Test
    @Order(51)
    public void submerge_propagatesOnlyThroughSubmergedParents_protectedDepressionStaysDry() {
        // Matches the situation described in the assignment text:
        // sea -> island0m -> hill2_5m -> depression0m
        // At seaLevel=1.0, island0m should submerge, but hill2.5m should not,
        // so the depression0m inside the hill must stay dry.
        HeightCurve sea = new HeightCurve(0L, -1.0, squareCoords(0.0, 0.0, 3.0), new ArrayList<>());
        HeightCurve island0m = new HeightCurve(1L, 0.0, squareCoords(0.0, 0.0, 2.0), new ArrayList<>());
        HeightCurve hill2_5m = new HeightCurve(2L, 2.5, squareCoords(0.0, 0.0, 1.5), new ArrayList<>());
        HeightCurve depression0m = new HeightCurve(3L, 0.0, squareCoords(0.0, 0.0, 1.0), new ArrayList<>());

        sea.getChildren().add(island0m);
        island0m.getChildren().add(hill2_5m);
        hill2_5m.getChildren().add(depression0m);

        sea.resetSubmerged();
        sea.submerge(1.0);

        assertTrue(
                island0m.submerged,
                "Task 'Submerge correctly': with seaLevel=1.0, the outer 0m region becomes submerged if water can reach it"
        );
        assertFalse(
                hill2_5m.submerged,
                "Task 'Submerge correctly': the 2.5m hill stays dry because its height is not strictly below sea level"
        );
        assertFalse(
                depression0m.submerged,
                "Task 'Submerge correctly': water spreads inward; a protected 0m depression inside a dry 2.5m hill stays dry"
        );
    }

    @Test
    @Order(52)
    public void submerge_branchingOnlySubmergesBelowSea() {
        HeightCurve sea = new HeightCurve(0L, -1.0, squareCoords(0.0, 0.0, 3.0), new ArrayList<>());
        HeightCurve parent = new HeightCurve(1L, 0.0, squareCoords(0.0, 0.0, 2.0), new ArrayList<>());
        HeightCurve below = new HeightCurve(2L, 0.0, squareCoords(0.0, 0.0, 1.5), new ArrayList<>());
        HeightCurve above = new HeightCurve(3L, 2.5, squareCoords(0.0, 0.0, 1.5), new ArrayList<>());
        HeightCurve belowChild = new HeightCurve(4L, 0.0, squareCoords(0.0, 0.0, 1.0), new ArrayList<>());

        sea.getChildren().add(parent);
        parent.getChildren().add(below);
        parent.getChildren().add(above);
        below.getChildren().add(belowChild);

        sea.resetSubmerged();
        sea.submerge(1.0);

        assertTrue(parent.submerged, "Task 'Submerge correctly': water reaches and submerges outer 0m curve below sea level");
        assertTrue(below.submerged, "Task 'Submerge correctly': child below sea level becomes submerged");
        assertFalse(above.submerged, "Task 'Submerge correctly': child above sea level stays dry");
        assertTrue(belowChild.submerged, "Task 'Submerge correctly': submergence propagates through submerged parents");
    }

    @Test
    @Order(53)
    public void submerge_exactSeaLevelBlocksPropagation() {
        HeightCurve sea = new HeightCurve(0L, -1.0, squareCoords(0.0, 0.0, 3.0), new ArrayList<>());
        HeightCurve parent = new HeightCurve(1L, 0.0, squareCoords(0.0, 0.0, 2.0), new ArrayList<>());
        HeightCurve atSea = new HeightCurve(2L, 1.0, squareCoords(0.0, 0.0, 1.5), new ArrayList<>());
        HeightCurve deep = new HeightCurve(3L, 0.0, squareCoords(0.0, 0.0, 1.0), new ArrayList<>());

        sea.getChildren().add(parent);
        parent.getChildren().add(atSea);
        atSea.getChildren().add(deep);

        sea.resetSubmerged();
        sea.submerge(1.0);

        assertTrue(parent.submerged, "Task 'Submerge correctly': parent below sea level becomes submerged");
        assertFalse(atSea.submerged, "Task 'Submerge correctly': height equal to sea level is not submerged");
        assertFalse(deep.submerged, "Task 'Submerge correctly': no propagation through a dry parent even if child is below sea level");
    }
}


