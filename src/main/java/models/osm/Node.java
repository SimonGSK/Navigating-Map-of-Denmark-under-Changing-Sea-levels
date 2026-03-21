package models.osm;

import models.geometry.Coordinate;

import java.awt.*;

public class Node extends Element {
    private final double lat;
    private final double lon;

    public Node(long id, double lat, double lon) {
        super(id);
        this.lat = lat;
        this.lon = lon;
    }

    public Coordinate getCoordinate() {
        return new Coordinate(lat, lon);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public void drawForTest(Graphics2D gc) {

    }
}
