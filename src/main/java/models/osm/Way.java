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
    //TODO: Måske lave et felt: boolean toFill som afgør om der skal kaldes .fill() når den tegnes
    //f.eks. kan man kalde fill på buildings, landuse og leisure, men highway og waterway skal ikke fyldes
    //Feltet kan initialiseres når farven også bestemmes
    //Ved dog ikke om man kommer til at tegne nogle element oven på andre

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
    public void drawForTest(Graphics2D gc) {
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

    public Color getColor() {
        if (tags.containsKey("highway")) {
            if (tags.get("highway").equals("track") || tags.get("highway").equals("path")) {
                return Color.decode("#664627");
            }
            return Color.decode("#2b2a2a"); //Grey
        } else if (tags.containsKey("building")) {
            return Color.decode("#a34018"); //Orange
        } else if (tags.containsKey("waterway")) {
            return Color.decode("#184e85"); //Blue
        } else if (tags.containsKey("landuse")) {
            if (tags.get("landuse").equals("forest")) {
                return Color.decode("#1a3d0a");
            } else if (tags.get("landuse").equals("grass")) {
                return Color.decode("#33910a");
            } else if (tags.get("landuse").equals("industrial")) {
                return Color.decode("#4d4f4c");
            }
            return Color.decode("#362820");
        } else if (tags.containsKey("natural")){
            if (tags.get("natural").equals("water") || tags.get("natural").equals("spring")) {
                return Color.decode("#184e85");
            } else if (tags.get("natural").equals("rock") || tags.get("natural").equals("stone")) {
                return Color.decode("#2b2a2a");
            } else{
                return Color.decode("#3b421d");
            }
        } else{
            return Color.BLACK;
        }

        //TODO: Lav forskellige farver til hver key afhængigt at deres value (highway, landuse, surface, natural)
        //TODO: Tænker alle leisures skal have samme farve, uanset value, ved ikke med amenity

        //Tags:
        //Key: highway - Values: service, path, track, residential, footway, cycleway, ...
        //Key: landuse - Values: forest, grass, industrial, farmyard, recreation_ground, cemetery, allotments, residential, ...
        //Key: surface - Values: gravel, grass, asphalt, compacted, paved, paving_stones, ...
        //Key: natural - Values: scrub, water, coastline, rock, hill, peak, stone, spring, tree,
        //Key: amenity - Values: parking, grave_yard, shelter, school, ice_cream, bench, ...
        //Key: leisure - Values: park, golf_course, pitch, playground, ...
        //Key: building - Values: yes

        //More keys:
        // parking, barrier, oneway, historic, ...

    }
}
