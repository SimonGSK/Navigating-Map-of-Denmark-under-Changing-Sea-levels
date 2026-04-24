package models.heightcurve;

import java.util.List;
import models.geometry.Coordinate;

/**
 * Parsed height curve data.
 *
 * Intended as the output of the (optional) .hc parser as well as the hard-coded
 * examples
 */
public class HeightCurveData {

    public final double minLat;
    public final double minLon;
    public final double maxLat;
    public final double maxLon;

    /**
     * Implicit "sea" root of the containment tree. Its {@code children} are the
     * outermost coastlines.
     */
    public final HeightCurve sea;

    /**
     * Flat list of all curves (may include {@link #sea}).
     */
    public final List<HeightCurve> curves;

    public HeightCurveData(double minLat, double minLon, double maxLat, double maxLon, HeightCurve sea, List<HeightCurve> curves) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
        this.sea = sea;
        this.curves = List.copyOf(curves);
        buildTree(sea, curves);
    }

    //Sorterer alle HeightCurves fra størst til mindst så vi får dem i rækkefølge - parents er større end deres children
    //Derefter kaldes findParent() på alle HeightCurves for at finde deres parents
    private void buildTree(HeightCurve sea, List<HeightCurve> curves) {
        List<HeightCurve> sorted = curves.stream()
                .filter(c -> c.getId() != -1)
                .sorted((a, b) -> Double.compare(boundingArea(b), boundingArea(a)))
                .toList();

        for (HeightCurve hc : sorted) {
            HeightCurve parent = findParent(hc, sorted, sea);
            parent.addChild(hc);
        }
    }

    //Finder, den mindste HeightCurve der er større end den selv, altså dens Parent
    //Kalder Contains() for at tjekke om en større HeightCurve indeholder den hc vi ønsker at finde parent på
    //Returnerer havet hvis der ikke er andre HeightCurves som parent
    private HeightCurve findParent(HeightCurve hc, List<HeightCurve> sorted, HeightCurve sea) {
        HeightCurve bestParent = sea;
        double bestArea = Double.MAX_VALUE;

        for (int i = sorted.size() -1 ; i >= 0 ; i--) {
            HeightCurve potentialParent = sorted.get(i);
            if (potentialParent == hc) continue;
            if (contains(potentialParent, hc)) {
                return potentialParent;
            }
        }
        return bestParent;
    }

    private boolean contains(HeightCurve outer, HeightCurve inner) {
        if (inner.getCoords().isEmpty()) return false;
        // Tjek om første koordinat i inner ligger inde i outer
        Coordinate testPoint = inner.getCoords().get(0);
        return pointInPolygon(testPoint, outer.getCoords());
    }

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

    private boolean rayIntersects(Coordinate point, Coordinate a, Coordinate b) {
        double py = point.getLat();
        double px = point.getLon();
        double ay = a.getLat(), ax = a.getLon();
        double by = b.getLat(), bx = b.getLon();

        if ((ay > py) == (by > py)) return false; // Begge på samme side
        double intersectX = ax + (py - ay) / (by - ay) * (bx - ax);
        return px < intersectX;
    }

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

    public void updateFlooding(double seaLevel) {
        resetAll(sea); //Nulstiller alt for at genberegne oversvømmelserne

        // Start from sea's children - sea itself is always the implicit "above water" parent
        for (HeightCurve child : sea.getChildren()) {
            child.updateSubmersion(seaLevel, true);
        }
    }

    private void resetAll(HeightCurve curve) {
        curve.resetSubmerged();
        for (HeightCurve child : curve.getChildren()) {
            resetAll(child);
        }
    }

    public double getMaxHeight() {
        return curves.stream()
                .mapToDouble(HeightCurve::getHeight)
                .max()
                .orElse(100);
    }

    /**
     * Checks if a coordinate is inside a submerged height curve.
     * This is independent of the height curves' drawing and can be used
     * to determine if OSM features should be rendered as submerged.
     * 
     * @param coordinate the coordinate to check
     * @return true if the coordinate is inside a submerged height curve
     */
    public boolean isCoordinateSubmerged(Coordinate coordinate) {
        // Check each top-level height curve (children of sea)
        for (HeightCurve curve : sea.getChildren()) {
            if (isCoordinateInSubmergedCurve(coordinate, curve)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recursively checks if a coordinate is in a submerged curve or its submerged children.
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
