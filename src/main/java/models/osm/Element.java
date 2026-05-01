package models.osm;

import Interfaces.Drawable;
import models.RTree.ElementType;
import models.geometry.BoundingBox;
import models.geometry.SpatialElement;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;

public abstract class Element extends SpatialElement implements Drawable, Serializable {
    final private long id;
    final private ElementType type;
    protected HashMap<String, String> tags;
    private Color color = null;

    public Element(long id, ElementType type, HashMap<String, String> tags, BoundingBox mbr) {
        this.id = id;
        this.type = type;
        this.tags = tags;
        setMbr(mbr);
        setArea(mbr.area());
    }

    public Element(long id, ElementType type, HashMap<String, String> tags, BoundingBox mbr, double area) {
        this.id = id;
        this.type = type;
        this.tags = tags;
        setMbr(mbr);
        setArea(area);
    }

    public ElementType getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    protected String addTag(String key, String value) {
        if (tags == null) {
            tags = new HashMap<>();
        }

        return tags.putIfAbsent(key, value);
    }

    protected boolean modifyTag(String key, String value) {
        if (tags == null || !tags.containsKey(key)) {
            return false;
        }

        tags.put(key, value);
        return true;
    }

    public String getTag(String key) {
        if (tags == null) {
            return null;
        }

        return tags.get(key);
    }

    protected boolean contains(String key) {
        if (tags == null) {
            return false;
        }

        return tags.containsKey(key);
    }

    public HashMap<String, String> getTags() {
        if (tags == null) {
            return null;
        }
        return new HashMap<>(tags);
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;
    }

    public Color getColor(){
        if (color == null){
            color = findColor();
        }
        return color;
    }

    public Color findColor() {
        var tags = getTags();
        if (tags == null) return Color.BLACK;

        //NOT TO DRAW
        if ("power".equals(tags.get("route"))) return null;
        if (tags.containsKey("power")) return null;
        if (tags.containsKey("boundary")) return null;
        if (tags.containsValue("boundary")) return null;
        if (tags.containsKey("region")) return null;
        if (tags.containsValue("region")) return null;
        if (tags.containsValue("ferry")) return null;
        if (tags.containsValue("Belt Traffic")) return null;
        if ("underwater".equals(tags.get("location"))) return null;
        if ("strait".equals(tags.get("natural"))) return null;
        if ("bay".equals(tags.get("natural"))) return null;
        if ("sea".equals(tags.get("natural"))) return null;
        if ("ocean".equals(tags.get("natural"))) return null;
        if ("ferry".equals(tags.get("route"))) return null;
        if ("navigation".equals(tags.get("route"))) return null;
        if (tags.containsKey("seamark:type")) return null;
        if (tags.containsValue("sea_area")) return null;
        if (tags.containsValue("training_area")) return null;
        if (tags.containsKey("proposed")) return null;
        if ("pipeline".equals(tags.get("man_made"))) return null;
        if (tags.containsKey("demolished:building")) return null;

        //NATURAL
        if (tags.containsKey("natural")) {
            String natural = tags.get("natural");
            if ("water".equals(natural) || "spring".equals(natural)) return Color.decode("#184e85"); // Blå
            if ("rock".equals(natural) || "stone".equals(natural)) return Color.decode("#2b2a2a"); // Mørkegrå
            if ("coastline".equals(natural)) return Color.decode("#a19875");
            if ("shoal".equals(natural)) return Color.decode("#7c9ea6");
            if ("wetland".equals(natural)) return Color.decode("#638040");
            if ("heath".equals(natural)) return Color.decode("#638040");
            if ("coastline".equals(natural)) return Color.decode("#c7b687");
            if ("beach".equals(natural)) return Color.decode("#c9b383");
            return Color.decode("#0b4f14"); // Mørkegrøn
        }

        //SURFACE
        if (tags.containsKey("surface")) {
            String surface = tags.get("surface");
            if ("grass".equals(surface)) return Color.decode("#0b4f14"); // Grøn
            if ("paved".equals(surface) || "paving_stones".equals(surface)) return Color.decode("#4e524f"); // Grå
            if ("gravel".equals(surface)) return Color.decode("#4a4437"); // Gråbrun
            if ("sand".equals(surface)) return Color.decode("#c9b383"); // Sandfarvet
            return Color.decode("#171716"); // Mørkegrå
        }

        //HIGHWAY
        if (tags.containsKey("highway")) {
            String highway = tags.get("highway");
            if ("track".equals(highway) || "path".equals(highway)) return Color.decode("#664627"); // Lysebrun
            return Color.decode("#2b2a2a"); // Grå
        }

        //AREA:HIGHWAY
        if (tags.containsKey("area:highway")) {
            return Color.decode("#4e524f"); //Paved grey
        }

        //LANDUSE
        if (tags.containsKey("landuse")) {
            String landuse = tags.get("landuse");
            if ("forest".equals(landuse)) return Color.decode("#1a3d0a"); // Mørkegrøn
            if ("grass".equals(landuse)) return Color.decode("#297209"); // Grøn
            if ("industrial".equals(landuse)) return Color.decode("#4d4f4c"); // Grå
            return Color.decode("#a7d180"); // Brun
        }

        //AEROWAY
        if (tags.containsKey("aeroway")) {
            String aeroway = tags.get("aeroway");
            if ("taxiway".equals(aeroway) || "airstrip".equals(aeroway)) return Color.decode("#576682"); // Gråblå
            return Color.decode("#a69e9d"); // Lysegrå
        }

        //BARRIER
        if (tags.containsKey("barrier")) {
            String barrier = tags.get("barrier");
            if ("hedge".equals(barrier)) return Color.decode("#0b4f14"); // Grøn
            return Color.decode("#825e35");
        }

        //AMENITY AND LEISURE
        if (tags.containsKey("amenity") || tags.containsKey("leisure")){
            if (tags.containsValue("dog_park")) return Color.decode("#6a8a57");
            if (tags.containsValue("golf_course")) return Color.decode("#6a8a57");
            if (tags.containsValue("hospital")) return Color.decode("#c9b09d");
            return Color.decode("#471309"); // Brun-rød
        }

        //OTHER TAGS
        if (tags.containsKey("man_made")) return Color.decode("#75716d"); //Grå
        if (tags.containsKey("tourism")) return Color.decode("#8c6239"); //Muted brown
        if (tags.containsKey("historic")) return Color.decode("#8a7355"); //Slightly lighter sandy brown
        if (tags.containsKey("building") || tags.containsKey("building:part")) return Color.decode("#a34018"); // Orange
        if (tags.containsKey("waterway")) return Color.decode("#184e85"); // Blå
        if (tags.containsKey("landcover")) return Color.decode("#1a3d0a"); //Mørkegrøn
        if (tags.containsKey("grassland")) return Color.decode("#6b8c3a"); //Grøn

        //FALLBACK COLOR
        System.out.println(tags);
        return Color.decode("#9c9083");
    }
    //TODO: Find farver til de elementer der får fallback color

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
