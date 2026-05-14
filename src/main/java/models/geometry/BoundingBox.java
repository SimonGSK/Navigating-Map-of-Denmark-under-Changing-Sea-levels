package models.geometry;

import java.io.Serializable;
import java.util.List;

public record BoundingBox(double minLat, double minLon, double maxLat, double maxLon) implements Serializable {
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
        return new Coordinate(minLat + maxLat/2, minLon + maxLon/2);
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
        return Math.max(0, (maxLat - minLat) * (maxLon - minLon));
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
}
