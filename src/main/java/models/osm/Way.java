package models.osm;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.*;

public class Way extends Element {
    private List<Node> nodes;
    private final HashMap<String, String> tags;

    public Way(long id, List<Node> nodes, HashMap<String, String> tags) {
        super(id);
        this.nodes = nodes;
        this.tags = tags;
        this.setArea(calculateArea(nodes));
    }
    public List<Node>getNodes(){
        return nodes;
    }
    public HashMap<String, String> getTags() {
        return tags;
    }

    @Override
    public void draws(Graphics2D gc) {

    }

    private double calculateArea(List<Node> nodes){
        if (nodes == null || nodes.size() < 3) return 0; //Hvis der er under 3 noder er areal 0

        //Hvis den første node ikke er den samme som den sidste, er way'en ikke lukket og har ikke noget areal
        Node first = nodes.get(0);
        Node last = nodes.get(nodes.size() - 1);
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
}
