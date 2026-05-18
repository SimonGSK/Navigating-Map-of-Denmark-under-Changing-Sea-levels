package models.geometry;

import java.awt.geom.Path2D;
import java.util.*;

/**Ramer Douglas algorithm:
 * A Path2D that removes unnecessary points based on the current zoom level.
 *
 * A road in OSM might have 300 coordinate points to capture every curve.
 * When zoomed far out, many of those points are less than a pixel apart —
 * drawing them wastes time for zero visual difference.
 *
 * This class stores the original points and uses the Ramer-Douglas-Peucker
 * algorithm to decide which ones are actually worth drawing at the current zoom.
 * When zooming in, more points survive and the shape gets more detailed.
 *
 * Because it extends Path2D.Double, it works everywhere a normal Path2D is used.
 * The only addition needed in the renderer is one call to updateForZoom() before drawing.
 *
 *
 * The Ramer-Douglas-Peucker algorithm is a recursive method that works like this:
 *   1. Draw a straight line from the first point to the last.
 *   2. Find whichever middle point is furthest from that straight line.
 *   3. If it's further than epsilon (half a pixel): it represents real curvature,
 *      keep it, then repeat the whole process on both halves of the line.
 *   4. If it's closer than epsilon: every point in between is sub-pixel close
 *      to the straight line, drop them all, the eye can't tell the difference.
 *
 *   Average time: O(n log n),  same class of complexity as merge sort.
 *
 *
 *   Epsilon:
 *    Epsilon = 0.5 / 2^zoom  →  half a pixel expressed in geographic degrees.
 *
 *    At zoom 11 (full island view): 1 pixel ≈ 0.00049°, so epsilon ≈ 0.00024°.
 *    A lot of points get dropped. The island outline still looks smooth.
 *
 *    At zoom 15 (street level): 1 pixel ≈ 0.000031°, so epsilon ≈ 0.000015°.
 *    Almost nothing gets dropped. Every bend in the road is preserved.
 */

public class AdaptivePath extends Path2D.Double {
    // A point must deviate more than this (in pixels) to be worth keeping.
    private static final double PIXEL_STEP = 2;

    // Only rebuild the path when zoom changes by at least this much
    private static final double ZOOM_STEP = 1;

    private final double[] rawX;
    private final double[] rawY;
    private final int pointCount;
    private final boolean closed;

    private double lastBuiltZoom = java.lang.Double.NaN;

    /**
     * Creates the path from a list of {x, y} coordinate pairs.
     * Builds at full resolution initially. Call updateForZoom() before drawing.
     * @param points projected coordinates (x = lon * cosMeanLat, y = -lat)
     * @param closed true for polygons (forests, lakes), false for lines (roads)
     */
    public AdaptivePath(List<double[]> points, boolean closed) {
        super(WIND_NON_ZERO, points.size());
        this.pointCount = points.size();
        this.closed = closed;
        this.rawX = new double[pointCount];
        this.rawY = new double[pointCount];

        for(int i = 0; i < pointCount; i++) {
            rawX[i] = points.get(i)[0];
            rawY[i] = points.get(i)[1];
        }
        rebuildFull();
    }

    /**
     *  Simplifies the path for the current zoom level and rebuilds the Path2D segments.
     *   Call this once per element per frame, just before gc.draw() or gc.fill().
     *
     *  If the zoom hasn't moved to a new 0.5-step bucket since the last call,
     *  this returns immediately — no work done.
     */
    public void updateForZoom(double zoom) {
        double quantised = Math.round(zoom/ZOOM_STEP) * ZOOM_STEP;
        if (quantised == lastBuiltZoom) return;
        lastBuiltZoom = quantised;

        if (pointCount < 3) return;

        double epsilon = PIXEL_STEP/Math.pow(2.0, quantised);
        boolean[] keep = new boolean[pointCount];
        keep[0] = true;
        keep[pointCount-1] = true;
        ramerDouglasPeucker(0, pointCount-1, epsilon, keep);

        // Rebuild the path from kept points only
        reset();
        boolean first = true;
        for (int i = 0; i < pointCount; i++) {
            if (!keep[i]) continue;
            if (first) { moveTo(rawX[i], rawY[i]); first = false; }
            else lineTo(rawX[i], rawY[i]);
        }
        if (closed) closePath();
    }

    /**
     *   Recursively marks which points between start and end are worth keeping.
     *
     *   Finds the point that deviates the most from the straight line start→end.
     *   If it's more than epsilon away: mark it, recurse on both halves.
     *   If not: drop everything in between — they're all sub-pixel close to the line.
     */
    private void ramerDouglasPeucker(int start, int end, double epsilon, boolean[] keep) {
        if (end - start < 2) return;

        double maxDist = 0.0;
        int maxIdx = start;

        for (int i = start +1; i < end; i++) {
            double d = perpendicularDistance(rawX[i], rawY[i], rawX[start], rawY[start], rawX[end], rawY[end]);
            if (d > maxDist) {
                maxDist = d;
                maxIdx = i;
            }
        }
        if (maxDist > epsilon) {
            keep[maxIdx] = true;
            ramerDouglasPeucker(start, maxIdx, epsilon, keep);
            ramerDouglasPeucker(maxIdx, end, epsilon, keep);
            // Points not marked in keep[] stay false and are dropped
        }
    }
    /**
     * Returns how far point P is from the line between A and B.
     * Falls back to direct distance from P to A if A and B are the same point.
     */
    private static double perpendicularDistance(double px, double py, double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        double lenSq = dx * dx + dy * dy;
        if (lenSq == 0.0) {
            double ex = px - ax, ey =  py - ay;
            return Math.sqrt(ex * ex + ey * ey);
        }
        double cross = Math.abs(dx * (ay-py) - (ax - px) * dy);
        return cross / Math.sqrt(lenSq);
    }
    /** Builds the Path2D at full resolution. Used at construction time. */
    private void rebuildFull() {
        reset();
        if (pointCount == 0) return;
        moveTo(rawX[0], rawY[0]);
        for (int i = 1; i < pointCount; i++) lineTo(rawX[i], rawY[i]);
        if (closed) closePath();
    }
    /** Total number of original points before any simplification. */
    public int getRawPointCount() {
        return pointCount;
    }
    /* Serialization:
     Saving AdaptivePath directly to a .bin file crashes, because Java's serialization
     tries to call a constructor in Path2D that we don't have access to from our package.

     Fix: instead of saving AdaptivePath itself, we swap it out for a simple helper
     object (SerializationProxy) that just holds the raw coordinate arrays.
     When loading the .bin back, the helper reconstructs a proper AdaptivePath.
     */
    private Object writeReplace() throws java.io.ObjectStreamException {
        return new SerializationProxy(rawX, rawY, closed);
    }

    private static class SerializationProxy implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final double[] rawX;
        private final double[] rawY;
        private final boolean closed;
        /** Written to the .bin file in place of AdaptivePath. Reconstructs it on load. */
        SerializationProxy(double[] rawX, double[] rawY, boolean closed) {
            this.rawX = rawX;
            this.rawY = rawY;
            this.closed = closed;
        }
        private Object readResolve() throws java.io.ObjectStreamException {
            List<double[]> points = new java.util.ArrayList<>(rawX.length);
            for (int i = 0; i < rawX.length; i++) {
                points.add(new double[]{rawX[i], rawY[i]});
            }
            return new AdaptivePath(points, closed);
        }
    }
}

