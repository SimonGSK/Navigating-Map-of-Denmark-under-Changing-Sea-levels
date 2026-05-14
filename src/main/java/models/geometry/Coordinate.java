package models.geometry;

import java.io.Serializable;

public class Coordinate implements SpatialElement, Serializable {
    private final double lat;
    private final double lon;

    public Coordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public Coordinate copy() {
        return new Coordinate(lat, lon);
    }

    @Override
    public BoundingBox getMbr() {
        return new BoundingBox(lat,lon,lat,lon);
    }

    @Override
    public double getArea() {
        return 0;
    }
}
