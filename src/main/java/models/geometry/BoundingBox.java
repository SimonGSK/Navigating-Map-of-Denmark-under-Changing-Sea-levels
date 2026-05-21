package models.geometry;

import models.utils.UtilityTools;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public final class BoundingBox implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final double minLat;
    private final double minLon;
    private final double maxLat;
    private final double maxLon;
    private double area = Double.NaN;

    public BoundingBox(double minLat, double minLon, double maxLat, double maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
    }

    static public BoundingBox computeMbr(List<? extends SpatialElement> elements) {
        double minLat = Double.POSITIVE_INFINITY;
        double minLon = Double.POSITIVE_INFINITY;
        double maxLat = Double.NEGATIVE_INFINITY;
        double maxLon = Double.NEGATIVE_INFINITY;

        for (SpatialElement e : elements) {
            minLat = Math.min(minLat, e.getMbr().minLat());
            minLon = Math.min(minLon, e.getMbr().minLon());
            maxLat = Math.max(maxLat, e.getMbr().maxLat());
            maxLon = Math.max(maxLon, e.getMbr().maxLon());
        }
        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    public Coordinate getCenter() {
        return new Coordinate(minLat + maxLat / 2, minLon + maxLon / 2);
    }

    public boolean isInside(BoundingBox other) {
        return this.minLat >= other.minLat && this.minLon >= other.minLon && this.maxLat <= other.maxLat && this.maxLon <= other.maxLon;
    }

    public boolean isOverlappingOther(BoundingBox other) {
        boolean separated = this.maxLat < other.minLat || this.maxLon < other.minLon || this.minLat > other.maxLat || this.minLon > other.maxLon;
        return !separated;
    }

    public double areaIncreaseNeeded(BoundingBox mbr) {
        if (mbr.isInside(this)) {
            return 0;
        }

        BoundingBox container = getExpanded(mbr);

        return container.area() - this.area();
    }

    public double area() {
        if (Double.isNaN(area)) {
            area = Math.max(0, (maxLat - minLat) * (maxLon - minLon));
        }
        return area;
    }

    public double getGeometricArea() {
        Coordinate lowerLeft = new Coordinate(minLat,minLon);
        Coordinate upperLeft = new Coordinate(maxLat,minLon);
        Coordinate lowerRight = new Coordinate(minLat,maxLon);
        Coordinate upperRight = new Coordinate(maxLat,maxLon);

        return UtilityTools.haversineDistance(lowerLeft,lowerRight) * UtilityTools.haversineDistance(lowerLeft, upperLeft);
    }

    public BoundingBox copy() {
        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    public BoundingBox getExpanded(BoundingBox mbr) {
        return new BoundingBox(
                Math.min(this.minLat, mbr.minLat),
                Math.min(this.minLon, mbr.minLon),
                Math.max(this.maxLat, mbr.maxLat),
                Math.max(this.maxLon, mbr.maxLon)
        );
    }

    public double minLat() {
        return minLat;
    }

    public double minLon() {
        return minLon;
    }

    public double maxLat() {
        return maxLat;
    }

    public double maxLon() {
        return maxLon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BoundingBox) obj;
        return Double.doubleToLongBits(this.minLat) == Double.doubleToLongBits(that.minLat) &&
                Double.doubleToLongBits(this.minLon) == Double.doubleToLongBits(that.minLon) &&
                Double.doubleToLongBits(this.maxLat) == Double.doubleToLongBits(that.maxLat) &&
                Double.doubleToLongBits(this.maxLon) == Double.doubleToLongBits(that.maxLon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minLat, minLon, maxLat, maxLon);
    }

    @Override
    public String toString() {
        return "BoundingBox[" +
                "minLat=" + minLat + ", " +
                "minLon=" + minLon + ", " +
                "maxLat=" + maxLat + ", " +
                "maxLon=" + maxLon + ']';
    }

}
