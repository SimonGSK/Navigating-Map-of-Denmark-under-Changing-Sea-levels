package models.geometry;

public record BoundingBox(double minLat, double minLon, double maxLat, double maxLon) {
    public boolean isInside(BoundingBox other) {
        return this.minLat >= other.minLat && this.minLon >= other.minLon && this.maxLat <= other.maxLat && this.maxLon <= other.maxLon;
    }

    public boolean isOverlappingOther(BoundingBox other) {
        boolean separated = this.maxLat < other.minLat || this.maxLon < other.minLon || this.minLat > other.maxLat || this.minLon > other.maxLon;
        return !separated;
    }

    public double areaIncreaseNeeded(BoundingBox mbr) {
        if (mbr.isInside(this)) {
            return 1;
        }

        BoundingBox container = getExpanded(mbr);

        return container.area() - this.area();
    }

    public double area() {
        return (maxLat - minLat) * (maxLon - minLon);
    }

    public BoundingBox copy() {
        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    public BoundingBox getExpanded(BoundingBox mbr) {
        return new BoundingBox(
                Math.min(this.minLat, mbr.minLat),
                Math.min(this.minLon, mbr.minLon),
                Math.min(this.maxLat, mbr.maxLat),
                Math.min(this.maxLon, mbr.maxLon)
        );
    }
}
