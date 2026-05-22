package models.osm;

import enums.ElementType;
import models.geometry.BoundingBox;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Way extends OsmElement implements Iterable<Node>, Serializable {
    private final List<Node> nodes;
    private final double area;

    public Way(long id, HashMap<String, String> tags, List<Node> nodes) {
        super(id, ElementType.way, tags, computeMbr(nodes));
        this.nodes = nodes;
        this.area = getAreaShoelace(nodes);
    }

    static private BoundingBox computeMbr(List<Node> nodes) {
        return BoundingBox.computeMbr(nodes);
    }

    static private double getAreaShoelace(List<Node> nodes) {
        if (nodes == null || nodes.size() < 3) return 0; //If there are less than 3 nodes, area is 0

        //If the first node is not the same as the last, the way is not closed and doesn't have an area
        Node first = nodes.getFirst();
        Node last = nodes.getLast();
        boolean isClosed = first.getId() == last.getId();
        if (!isClosed) return 0;

        //Shoelace-formula
        double area = 0;
        int n = nodes.size();
        for (int i = 0; i < n - 1; i++) {               //For every node in the way
            double lat1 = nodes.get(i).getLat();        //we get its latitude
            double lon1 = nodes.get(i).getLon();        //and longitude,
            double lat2 = nodes.get(i + 1).getLat();    //Adds the latitude together with the next nodes' latitude,
            double lon2 = nodes.get(i + 1).getLon();    //and longitude together with the next nodes' longitude,
            area += (lon1 * lat2) - (lon2 * lat1);      //and calculates the area and adds it to the area
        }

        return Math.abs(area) / 2.0;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public Iterator<Node> iterator() {
        if (nodes == null) {
            return Collections.emptyIterator();
        }
        return nodes.iterator();
    }

    @Override
    public double getArea() {
        return area;
    }
}
