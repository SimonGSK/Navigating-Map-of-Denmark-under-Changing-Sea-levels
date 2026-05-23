package models.osm;

import enums.ElementType;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.pathfinding.Edge;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node extends OsmElement implements Comparable<Node>, Serializable {
    private final Coordinate coord;
    private transient List<Edge> adjacencyList;
    private HeightCurve containingHeightCurve;

    /**
     * Constructs a Node with a specific ID, latitude, and longitude.
     * Initializes the element type as a node, and sets its bounding box to the single coordinate.
     *
     * @param id The unique identifier of the node.
     * @param lat The latitude of the node.
     * @param lon The longitude of the node.
     */
    public Node(long id, double lat, double lon) { // add hc
        super(id, ElementType.node, null, new BoundingBox(lat, lon, lat, lon));
        this.coord = new Coordinate(lat, lon);
        this.adjacencyList = new ArrayList<>();
    }

    /**
     * Checks if this node is considered submerged based on its containing height curve.
     *
     * @return {@code true} if the associated height curve exists and is submerged, otherwise {@code false}.
     */
    public boolean isSubmerged() {
        return containingHeightCurve != null && containingHeightCurve.isSubmerged();
    }

    /**
     * Retrieves the adjacency list of edges connected to this node.
     *
     * @return A list of connected edges, or an empty list if there are none.
     */
    public List<Edge> getAdjacencyList() {

        if (adjacencyList == null) return new ArrayList<>();
        return adjacencyList;
    }

    /**
     * Adds an edge to this node's adjacency list, establishing a connection to another node.
     *
     * @param edge The edge connecting this node to its neighbor.
     */
    public void addNeighbour(Edge edge) {
        if (adjacencyList == null) adjacencyList = new ArrayList<>();
        adjacencyList.add(edge);
    }

    /**
     * Method to get a copy of the node's coordinate
     *
     * @return a copy of the node's coordinate
     */
    public Coordinate getCoordinate() {
        return coord.copy();
    }

    /**
     * Gets the latitude of the node.
     *
     * @return The latitude coordinate.
     */
    public double getLat() {
        return coord.getLat();
    }

    /**
     * Gets the longitude of the node.
     *
     * @return The longitude coordinate.
     */
    public double getLon() {
        return coord.getLon();
    }

    /**
     * Compares this node to another node based on their IDs.
     *
     * @param other The other Node to compare against.
     * @return A negative, zero, or positive integer as this node's ID is less than, equal to, or greater than the other node's ID.
     */
    @Override
    public int compareTo(Node other) {
        return Long.compare(this.getId(), other.getId());
    }

    /**
     * Returns a string representation of the node based on its latitude and longitude.
     *
     * @return A string in the format "lat lon".
     */
    @Override
    public String toString() {
        return getLat() + " " + getLon();
    }

    /**
     * Sets the height curve that contains this node.
     *
     * @param heightCurve The height curve containing this node.
     */
    public void setContainingHeightCurve(HeightCurve heightCurve) {
        this.containingHeightCurve = heightCurve;
    }

    /**
     * Retrieves the height curve containing this node.
     *
     * @return The height curve, or null if none is assigned.
     */
    public HeightCurve getContainingHeightCurve() {
        return containingHeightCurve;
    }

    /**
     * Gets the geographical area of the node.
     *
     * @return 0, as a point has no area.
     */
    @Override
    public double getArea() {
        return 0;
    }
}
