package models.geometry;

import java.awt.geom.Path2D;
import java.util.*;

/**Ramer Douglas algorithm
 * A Path2D.Double that stores its raw coordinates and can rebuild itself
 * at reduced resolution for the current zoom level using the
 * Ramer–Douglas–Peucker (RDP) algorithm
 *
 * 1. Draw the straight line from P₀ to Pₙ.
 * 2. Find the point Pₖ with the greatest perpendicular distance from that line.
 * 3. If distance(Pₖ) > ε  →  Pₖ must be kept; recurse on P₀…Pₖ and Pₖ…Pₙ.
 * If distance(Pₖ) ≤ ε  →  every interior point is within ε of the line;
 *                     drop them all and keep only P₀ and Pₙ.
 *
 *
 *   At each level of recursion we scan the current
 *   sub-array once (O(n)) and split into two halves.  The depth is O(log n) on
 *   average (O(n) worst case for adversarial zigzag input), giving overall
 *   O(n log n) average time.
 */

public class AdaptivePath extends Path2D.Double {
    private static final double HALF_PIXEL = 0.5;
    private static final double ZOOM_STEP = 0.5;

    private final double[] rawX;
    private final double[] rawY;
    private final int pointCount;
    private final boolean closed;

    private double lastBuiltZoom = java.lang.Double.NaN;

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
    public void updateForZoom(double zoom) {
        double quantised = Math.round(zoom/ZOOM_STEP) * ZOOM_STEP;
        if (quantised == lastBuiltZoom) return;
        lastBuiltZoom = quantised;

        if (pointCount < 3) return;

        double epsilon = HALF_PIXEL/Math.pow(2.0, quantised);
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

    // Recursive Ramer-Douglas-Peucker implementation
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
     * Perpendicular (point-to-line) distance from point P to the infinite line
     * through A and B
     *
     * Derived from the 2-D cross-product formula:
     *          area of triangle PAB = ½ |AB × AP|
     *          height = 2·area / |AB|
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
    private void rebuildFull() {
        reset();
        if (pointCount == 0) return;
        moveTo(rawX[0], rawY[0]);
        for (int i = 1; i < pointCount; i++) lineTo(rawX[i], rawY[i]);
        if (closed) closePath();
    }
    public int getRawPointCount() {
        return pointCount;
    }

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new SerializationProxy(rawX, rawY, closed);
    }

    private static class SerializationProxy implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final double[] rawX;
        private final double[] rawY;
        private final boolean closed;

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

