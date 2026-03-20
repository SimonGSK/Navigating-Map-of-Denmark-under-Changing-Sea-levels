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
            if (maxLon > node.getLon()) {
                maxLon = node.getLon();
            } else if (minLon < node.getLon()) {
                minLon = node.getLon();
            }
            if (maxLat > node.getLat()) {
                maxLat = node.getLat();
            } else if (minLat < node.getLat()) {
                minLat = node.getLat();
            }
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
    public void drawForTest(Graphics2D gc,Color color, float strokeWidth) {
        /*
        if (nodes.isEmpty()) {
               return;
          }
          gc.setColor(color);
          if (strokeWidth != null) {
              gc.setStroke(new BasicStroke(strokeWidth));
          }
            Path2D.Double path = new Path2D.Double();

          if (nodes.size() == 2) {
              Node node1 = nodes.get(0);
              Node node2 = nodes.get(1);

              double x1 = node1.getLon();
              double y1 = node1.getLat();

              double x2 = node2.getLon();
              double y2 = node2.getLat();

              path.moveTo(x1, y1);
              path.lineTo(x2, y2);
              gc.draw(path);
          } else if (nodes.size() > 2) {
             Node node1 = nodes.getFirst();
              double x1 = node1.getLon();
              double y1 = node1.getLat();
              path.moveTo(x1, y1);

              for (int i = 1; i < nodes.size(); i++) {
                  Node node2 = nodes.get(i);
                    double x2 = node2.getLon();
                    double y2 = node2.getLat();
                    path.lineTo(x2, y2);
              }
              if (nodes.getFirst().getId() == nodes.getLast().getId()) {
                  gc.setStroke(new BasicStroke(0));
                  path.closePath();
                  gc.fill(path);
                  return;
              }
              gc.draw(path);
          }

         */
        
    }
}
