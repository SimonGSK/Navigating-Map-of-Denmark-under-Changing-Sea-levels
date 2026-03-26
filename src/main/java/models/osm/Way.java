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
        double maxLon = 0;
        double maxLat = 0;
        double minLon = 0;
        double minLat = 0;
        for (Node node : nodes) {
            if (node.getLon() > maxLon) maxLon = node.getLon();
            if (node.getLon() < minLon) minLon = node.getLon();
            if (node.getLat() > maxLat) maxLat = node.getLat();
            if (node.getLat() < minLat) minLat = node.getLat();
        }
        this.setArea((maxLat - minLat) * (maxLon - minLon));
    }
    public List<Node>getNodes(){
        return nodes;
    }
    public HashMap<String, String> getTags() {
        return tags;
    }

    @Override
    public void drawForTest(Graphics2D gc) {

    }
}
