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
        if(tags.containsKey("surface")){
            if(tags.get("surface").equals("grass")){
                return Color.decode("#0b4f14"); //Grøn
            } else if (tags.get("surface").equals("paved") || tags.get("surface").equals("paving_stones")){
                return Color.decode("#4e524f"); //Grå
            } else if (tags.get("surface").equals("gravel")){
                return Color.decode("#4a4437"); //Gråbrun
            }
            return Color.decode("#171716"); //Mørkegrå
        } else if (tags.containsKey("highway")) {
            if (tags.get("highway").equals("track") || tags.get("highway").equals("path")) {
                return Color.decode("#664627"); //Lysebrun
            }
            return Color.decode("#2b2a2a"); //Grå
        } else if (tags.containsKey("building")) {
            return Color.decode("#a34018"); //Orange
        } else if (tags.containsKey("amenity") || tags.containsKey("leisure")){
            return Color.decode("#471309"); //Brun-rød
        } else if (tags.containsKey("waterway")) {
            return Color.decode("#184e85"); //Blå
        } else if (tags.containsKey("landuse")) {
            if (tags.get("landuse").equals("forest")) { //TODO: De ufarvede arealer på Bornholm skulle gerne rammes af denne, men der står at det er en multipolygon, så der er måske derfor den ikke farves
                return Color.decode("#1a3d0a"); //Mørkegrøn
            } else if (tags.get("landuse").equals("grass")) {
                return Color.decode("#297209"); //Grøn
            } else if (tags.get("landuse").equals("industrial")) {
                return Color.decode("#4d4f4c"); //Grå
            }
            return Color.decode("#a7d180"); //Brun
        } else if (tags.containsKey("natural")){
            if (tags.get("natural").equals("water") || tags.get("natural").equals("spring")) {
                return Color.decode("#184e85"); //Blå
            } else if (tags.get("natural").equals("rock") || tags.get("natural").equals("stone")) {
                return Color.decode("#2b2a2a"); //Mørkegrå
            } else if (tags.get("natural").equals("coastline")){
                return Color.decode("#a19875");
            } else{
                return Color.decode("#0b4f14"); //Mørkegrøn
            }
        } else if (tags.containsKey("aeroway")){
            if(tags.get("aeroway").equals("taxiway") || tags.get("aeroway").equals("airstrip")){
                return Color.decode("#576682"); //Gråblå
            }
            return Color.decode("#a69e9d"); //Lysegrå
        } else if (tags.containsKey("barrier")){
            if(tags.get("barrier").equals("hedge")){
                return Color.decode("#0b4f14"); //Grøn
            } else{
                return Color.BLACK;
            }
        } else{
            return Color.BLACK;
        }

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
