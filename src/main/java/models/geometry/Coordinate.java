package models.geometry;

public class Coordinate {
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
}
