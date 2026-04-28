package models.osm;

import models.RTree.ElementType;
import models.geometry.BoundingBox;

import java.awt.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Way extends Element implements Iterable<Node>, Serializable {
    private final List<Node> nodes;

    public Way(long id, HashMap<String, String> tags, List<Node> nodes) {
        super(id, ElementType.way, tags, computeMbr(nodes), getAreaShoelace(nodes));
        this.nodes = nodes;
    }

    static private BoundingBox computeMbr(List<Node> nodes) {
        return BoundingBox.computeMbr(nodes);
    }

    static private double getAreaShoelace(List<Node> nodes) {
        if (nodes == null || nodes.size() < 3) return 0; //Hvis der er under 3 noder er areal 0

        //Hvis den første node ikke er den samme som den sidste, er way'en ikke lukket og har ikke noget areal
        Node first = nodes.getFirst();
        Node last = nodes.getLast();
        boolean isClosed = first.getId() == last.getId();
        if (!isClosed) return 0;

        //Shoelace-formlen
        double area = 0;
        int n = nodes.size();
        for (int i = 0; i < n - 1; i++) {               //For hver node i way'en
            double lat1 = nodes.get(i).getLat();        //får vi dens latitude
            double lon1 = nodes.get(i).getLon();        //og longitude,
            double lat2 = nodes.get(i + 1).getLat();    //lægger dens latitude sammen med næste nodes latitude,
            double lon2 = nodes.get(i + 1).getLon();    //og longitude sammen med næste nodes longitude,
            area += (lon1 * lat2) - (lon2 * lat1);      //og udregner arealet og lægger det til area
        }

        return Math.abs(area) / 2.0;

        //TODO: Hække bliver i byen tegner oven på veje. Find en løsning på dette
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public void draws(Graphics2D gc) {

    }

    @Override
    public Iterator<Node> iterator() {
        if (nodes == null) {
            return Collections.emptyIterator();
        }
        return nodes.iterator();
    }
}
