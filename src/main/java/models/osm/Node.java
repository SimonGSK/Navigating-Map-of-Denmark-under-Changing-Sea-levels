package models.osm;

import models.geometry.Coordinate;

public class Node extends Element {
    private final double lat;
    private final double lon;

    public Node(long id, double lat, double lon) {
        super(id);
        this.lat = lat;
        this.lon = lon;
    }

    public Coordinate getCoordinate() {
        return new Coordinate(lon, lat);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
