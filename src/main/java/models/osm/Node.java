package models.osm;

import enums.ElementType;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.parser.HeightCurveData;
import models.pathfinding.Edge;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node extends OsmElement implements Comparable<Node>, Serializable {
    private final Coordinate coord;
    private transient List<Edge> adjacencyList;
    private boolean isSubmerged = false;

    public Node(long id, double lat, double lon) { // add hc
        super(id, ElementType.node, null, new BoundingBox(lat, lon, lat, lon));
        this.coord = new Coordinate(lat, lon);
        this.adjacencyList = new ArrayList<>();
    }

    public void resolveSubmersion(HeightCurveData heightCurveData) {
        this.isSubmerged = heightCurveData.isCoordinateSubmerged(this.coord); // TODO: Map nodes to height curves during parsing
    }

    public boolean isSubmerged() {
        return isSubmerged;
    }



    public List<Edge> getAdjacencyList() {

        if (adjacencyList == null) return new ArrayList<>();
        return adjacencyList;
    }

    public void addNeighbour(Edge edge) {
        if (adjacencyList == null) adjacencyList = new ArrayList<>();
        adjacencyList.add(edge);
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
    @Override
    public int compareTo(Node other) {
        return Long.compare(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        return getLat() + " " + getLon();
    }
}
