package models.osm;

import Interfaces.Drawable;

import java.util.HashMap;
import java.awt.*;

public abstract class Element implements Drawable {
    final private long id;
    private HashMap<String, String> tags;
    private double area;

    public Element(long id) {
        this.id = id;
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

    protected String getTag(String key) {
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

    protected HashMap<String, String> getTags() {
        if (tags == null) {
            return null;
        }
        return new HashMap<>(tags);
    }

    public double getArea() {
        return this.area;
    }

    protected void setArea(double area) {
        this.area = area;
    }

    public Color getColor() {
        var tags = getTags();
        if (tags == null) return Color.BLACK;

        if (tags.containsKey("surface")) {
            String surface = tags.get("surface");

            if ("grass".equals(surface)) {
                return Color.decode("#0b4f14"); // Grøn
            } else if ("paved".equals(surface) || "paving_stones".equals(surface)) {
                return Color.decode("#4e524f"); // Grå
            } else if ("gravel".equals(surface)) {
                return Color.decode("#4a4437"); // Gråbrun
            }
            return Color.decode("#171716"); // Mørkegrå

        } else if (tags.containsKey("highway")) {
            String highway = tags.get("highway");

            if ("track".equals(highway) || "path".equals(highway)) {
                return Color.decode("#664627"); // Lysebrun
            }
            return Color.decode("#2b2a2a"); // Grå

        } else if (tags.containsKey("building")) {
            return Color.decode("#a34018"); // Orange

        } else if (tags.containsKey("amenity") || tags.containsKey("leisure")) {
            return Color.decode("#471309"); // Brun-rød

        } else if ("camp_site".equals(tags.get("tourism"))) {
            return Color.decode("#471309"); // Samme farve som amenity/leisure

        } else if (tags.containsKey("waterway")) {
            return Color.decode("#184e85"); // Blå

        } else if (tags.containsKey("landuse")) {
            String landuse = tags.get("landuse");

            if ("forest".equals(landuse)) {
                return Color.decode("#1a3d0a"); // Mørkegrøn
            } else if ("grass".equals(landuse)) {
                return Color.decode("#297209"); // Grøn
            } else if ("industrial".equals(landuse)) {
                return Color.decode("#4d4f4c"); // Grå
            }
            return Color.decode("#a7d180"); // Brun

        } else if (tags.containsKey("natural")) {
            String natural = tags.get("natural");

            if ("water".equals(natural) || "spring".equals(natural)) {
                return Color.decode("#184e85"); // Blå
            } else if ("rock".equals(natural) || "stone".equals(natural)) {
                return Color.decode("#2b2a2a"); // Mørkegrå
            } else if ("coastline".equals(natural)) {
                return Color.decode("#a19875");
            }
            return Color.decode("#0b4f14"); // Mørkegrøn

        } else if (tags.containsKey("aeroway")) {
            String aeroway = tags.get("aeroway");

            if ("taxiway".equals(aeroway) || "airstrip".equals(aeroway)) {
                return Color.decode("#576682"); // Gråblå
            }
            return Color.decode("#a69e9d"); // Lysegrå

        } else if (tags.containsKey("barrier")) {
            String barrier = tags.get("barrier");

            if ("hedge".equals(barrier)) {
                return Color.decode("#0b4f14"); // Grøn
            }
            return Color.BLACK;
        }

        return Color.BLACK;

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

    public boolean shouldNotDraw() {
        var tags = getTags();
        if (tags == null) return false;

        if ("power".equals(tags.get("route"))) return true;
        if (tags.containsKey("power")) return true;
        if (tags.containsKey("boundary")) return true;
        if (tags.containsValue("boundary")) return true;
        if (tags.containsKey("region")) return true;
        if (tags.containsValue("region")) return true;
        if ("ferry".equals(tags.get("route"))) return true;
        if (tags.containsValue("ferry")) return true;
        if (tags.containsValue("Belt Traffic")) return true;
        if ("underwater".equals(tags.get("location"))) return true;
        if ("strait".equals(tags.get("natural"))) return true;
        if ("bay".equals(tags.get("natural"))) return true;
        if (tags.containsKey("seamark:type")) return true;
        if (tags.containsValue("sea_area")) return true;
        if (tags.containsValue("training_area")) return true;
        if (tags.containsKey("proposed")) return true;
        if ("coastline".equals(tags.get("natural"))) return true;
        if ("sea".equals(tags.get("natural"))) return true;
        if ("ocean".equals(tags.get("natural"))) return true;
        if ("navigation".equals(tags.get("route"))) return true;
        if ("pipeline".equals(tags.get("man_made"))) return true;

        return false;
    }
}
