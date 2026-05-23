package models.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;

/**
 * Parsed height curve data.
 *
 * Intended as the output of the (optional) .hc parser as well as the hard-coded
 * examples
 */
public class HeightCurveData implements Serializable {

    public final double minLat;
    public final double minLon;
    public final double maxLat;
    public final double maxLon;

    /**
     * Implicit "sea" root of the containment tree. Its {@code children} are the
     * outermost coastlines.
     */
    public final HeightCurve root;

    /**
     * Flat list of all curves (may include {@link #root}).
     */
    public List<HeightCurve> curves;

    /**
     * Creates a height curve dataset and builds the containment tree.
     * @param minLat minimum latitude
     * @param minLon minimum longitude
     * @param maxLat maximum latitude
     * @param maxLon maximum longitude
     * @param root sea-level root curve
     * @param curves list of curves
     */
    public HeightCurveData(double minLat, double minLon, double maxLat, double maxLon, HeightCurve root, List<HeightCurve> curves) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
        this.root = root;
        this.curves = List.copyOf(curves);
        buildTree(root, curves);
    }

    /**
     * Builds the containment tree from the curve list.
     * @param root root curve
     * @param curves all curves
     */
    private void buildTree(HeightCurve root, List<HeightCurve> curves) {
        List<HeightCurve> sorted = curves.stream()
                .filter(c -> c.getId() != -1)
                .sorted((a, b) -> Double.compare(boundingArea(b), boundingArea(a)))
                .toList();

        for (HeightCurve hc : sorted) {
            HeightCurve parent = findParent(hc, sorted, root);

            if (parent != null){
                parent.addChild(hc);
            }
        }
    }

    /**
     * Searches for curves overlapping the given area.
     * @param searchArea bounding box to query
     * @return matching curves
     */
    public List<HeightCurve> search(BoundingBox searchArea) {
        List<HeightCurve> searchResults = new ArrayList<>();
        if (root != null) {
            for (HeightCurve hc : root.getChildren()) {
                searchRecursive(hc,searchArea,searchResults);
            }
        }
        return searchResults;
    }

    /**
     * Recursively collects curves intersecting the search area.
     * @param heightCurve current curve
     * @param searchArea bounding box to query
     * @param searchResults output list
     */
    public void searchRecursive(HeightCurve heightCurve, BoundingBox searchArea, List<HeightCurve> searchResults) {
        if (!searchArea.isOverlappingOther(heightCurve.getMbr())) {
            return;
        }

        searchResults.add(heightCurve);
        for (HeightCurve hc : heightCurve.getChildren()) {
            searchRecursive(hc,searchArea,searchResults);
        }
    }

    /**
     * Finds the parent curve for the given curve.
     * @param hc curve to place
     * @param sorted curves sorted by size
     * @param sea sea-level root
     * @return parent curve
     */
    private HeightCurve findParent(HeightCurve hc, List<HeightCurve> sorted, HeightCurve sea) {
        for (int i = sorted.size() -1 ; i >= 0 ; i--) {
            HeightCurve potentialParent = sorted.get(i);
            if (potentialParent == hc) continue;
            if (contains(potentialParent, hc)) {
                return potentialParent;
            }
        }

        return sea;
    }

    /**
     * Checks if one curve geometrically contains another.
     * @param outer outer curve
     * @param inner inner curve
     * @return true if inner lies within outer
     */
    private boolean contains(HeightCurve outer, HeightCurve inner) {
        if (inner.getCoords().isEmpty() || outer.getCoords().isEmpty()) return false;

        if (!inner.getMbr().isInside(outer.getMbr())) {
            return false;
        }

        // Check if the first coordinate in inner lies inside outer
        Coordinate testPoint = inner.getCoords().get(0);
        return pointInPolygon(testPoint, outer.getCoords());
    }

    /**
     * Point-in-polygon test using ray casting.
     * @param point point to test
     * @param polygon polygon ring
     * @return true if point is inside
     */
    private boolean pointInPolygon(Coordinate point, List<Coordinate> polygon) {
        int intersections = 0;
        int n = polygon.size();
        for (int i = 0; i < n - 1; i++) {
            Coordinate a = polygon.get(i);
            Coordinate b = polygon.get(i + 1);
            if (rayIntersects(point, a, b)) intersections++;
        }
        return intersections % 2 == 1;
    }

    /**
     * Tests if a horizontal ray intersects a polygon edge.
     * @param point test point
     * @param a segment start
     * @param b segment end
     * @return true if ray intersects segment
     */
    private boolean rayIntersects(Coordinate point, Coordinate a, Coordinate b) {
        double py = point.getLat();
        double px = point.getLon();
        double ay = a.getLat(), ax = a.getLon();
        double by = b.getLat(), bx = b.getLon();

        if ((ay > py) == (by > py)) return false; // Begge på samme side
        double intersectX = ax + (py - ay) / (by - ay) * (bx - ax);
        return px < intersectX;
    }

    /**
     * Computes the bounding box area for a curve.
     * @param hc curve to measure
     * @return bounding box area
     */
    private double boundingArea(HeightCurve hc) {
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
        for (Coordinate c : hc.getCoords()) {
            if (c.getLat() < minLat) minLat = c.getLat();
            if (c.getLat() > maxLat) maxLat = c.getLat();
            if (c.getLon() < minLon) minLon = c.getLon();
            if (c.getLon() > maxLon) maxLon = c.getLon();
        }
        if (minLat == Double.MAX_VALUE) return 0;
        return (maxLat - minLat) * (maxLon - minLon);
    }

    /**
     * Updates submerged flags based on sea level.
     * @param seaLevel sea level in meters
     */
    public void updateFlooding(double seaLevel) {
        resetAll(root); // Resets everything in order to recalculate the floods

        // Start from sea's children - sea itself is always the implicit "above water" parent
        for (HeightCurve child : root.getChildren()) {
            child.updateSubmersion(seaLevel, true);
        }
    }

    /**
     * Resets submerged flags for a curve subtree.
     * @param curve root curve
     */
    private void resetAll(HeightCurve curve) {
        curve.resetSubmerged();
        for (HeightCurve child : curve.getChildren()) {
            resetAll(child);
        }
    }

    /**
     * @return maximum height value in the dataset
     */
    public double getMaxHeight() {
        return curves.stream()
                .mapToDouble(HeightCurve::getHeight)
                .max()
                .orElse(100);
    }

    /**
     * Recursively checks if a coordinate is in a submerged curve or its submerged children.
     * @param coordinate coordinate to test
     * @param curve current curve
     * @return true if submerged
     */
    private boolean isCoordinateInSubmergedCurve(Coordinate coordinate, HeightCurve curve) {
        // If this curve is submerged and the coordinate is inside it, return true
        if (curve.isSubmerged() && pointInPolygon(coordinate, curve.getCoords())) {
            return true;
        }
        
        // Check children recursively
        for (HeightCurve child : curve.getChildren()) {
            if (isCoordinateInSubmergedCurve(coordinate, child)) {
                return true;
            }
        }
        
        return false;
    }
}
