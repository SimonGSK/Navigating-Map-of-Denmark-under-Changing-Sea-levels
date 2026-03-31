package org.example;

public record BoundingBox(double minLat, double minLon, double maxLat, double maxLon) {
    public boolean isInsideOther(BoundingBox other) {
        return this.minLat >= other.minLat && this.minLon >= other.minLon && this.maxLat <= other.maxLat && this.maxLon <= other.maxLon;
    }

    public boolean isOverlappingOther(BoundingBox other) {
        boolean separated = this.maxLat < other.minLat || this.maxLon < other.minLon || this.minLat > other.maxLat || this.minLon > other.maxLon;
        return !separated;
    }

    public Coordinate center() {
        double height = maxLat - minLat;
        double width = maxLon - minLon;
        return new Coordinate(height / 2 + minLat, width / 2 + minLon);
    }

    public double distanceFromOther(BoundingBox mbr) {
        Coordinate c = mbr.center();
        double deltaLat = c.lat() - center().lat();
        double deltaLon = c.lon() - center().lon();
        return Math.sqrt(Math.pow(deltaLat, 2) + Math.pow(deltaLon, 2));
    }

    public double distanceFromOrigo() {
        Coordinate c = center();
        return Math.sqrt(Math.pow(c.lat(), 2) + Math.pow(c.lon(), 2));
    }
}
