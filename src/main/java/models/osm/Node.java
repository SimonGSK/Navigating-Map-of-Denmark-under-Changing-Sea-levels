package models.osm;

import models.RTree.ElementType;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;

import java.awt.*;

public class Node extends Element {
    private final Coordinate coord;

    public Node(long id, double lat, double lon) {
        super(id, ElementType.node, null, new BoundingBox(lat, lon, lat, lon));
        this.coord = new Coordinate(lat, lon);
    }

    /**
     * Method to get a defensive copy of the node's coordinate
     *
     * @return a defensive copy of the node's coordinate
     */
    public Coordinate getCoordinate() {
        return coord.copy();
    }

    public double getLat() {
        return coord.getLat();
    }

    public double getLon() {
        return coord.getLon();
    }

    @Override
    public void draws(Graphics2D gc) {

    }
}
