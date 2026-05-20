package models.osm;

import enums.ElementType;
import models.geometry.BoundingBox;

import java.awt.*;
import java.util.HashMap;

public abstract class OsmElement extends Element {
    protected HashMap<String, String> tags;
    private Color color = null;

    public OsmElement(long id, ElementType type, HashMap<String, String> tags, BoundingBox mbr) {
        super(id, type, mbr);
        this.tags = tags;
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

    protected boolean containsTag(String key) {
        if (tags == null) {
            return false;
        }

        return tags.containsKey(key);
    }

    public Color getColor() {
        if (color == null) {
            color = findColor();
        }
        return color;
    }

    private Color findColor() {
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
        if (tags.containsKey("barrier")) return null;
        if (tags.containsValue("nature_reserve")) return null;

        //WATER
        if (tags.containsKey("waterway") || "water".equals(tags.get("natural")) || "spring".equals(tags.get("natural"))) {
            return Color.decode("#A5BFD2"); //Dusty blue
        }

        //NATURAL
        if (tags.containsKey("natural")) {
            String natural = tags.get("natural");
            if ("wood".equals(natural) || "forest".equals(natural)) return Color.decode("#A8C19D"); // Forest green
            if ("rock".equals(natural) || "stone".equals(natural)) return Color.decode("#C8C8C8"); // Grey
            if ("wetland".equals(natural)) return Color.decode("#B9C5B2"); // Swamp
            if ("beach".equals(natural)) return Color.decode("#E5DCC6"); // Sand
            if ("shoal".equals(natural)) return Color.decode("#7c9ea6");
            return Color.decode("#D3DFC5"); // Standard natur-grøn
        }

        //HIGHWAY
        if (tags.containsKey("highway") || tags.containsKey("area:highway")) {
            String highway = tags.get("highway");
            if ("track".equals(highway) || "path".equals(highway)) return Color.decode("#C9BEB0"); // Light brown
            return Color.decode("#FFFFFF"); // White
        }

        //LANDUSE
        if (tags.containsKey("landuse")) {
            String landuse = tags.get("landuse");
            if ("forest".equals(landuse) || "wood".equals(landuse)) return Color.decode("#9DBA8E"); //Green
            if ("grass".equals(landuse) || "meadow".equals(landuse) || "farmland".equals(landuse))
                return Color.decode("#D3DFC5"); // Green
            if ("industrial".equals(landuse)) return Color.decode("#DBD7D2"); // Grey
            if ("residential".equals(landuse)) return Color.decode("#E3E1DA"); // Beige
            return Color.decode("#D1D1C4");
        }

        //AEROWAY
        if (tags.containsKey("aeroway")) {
            String aeroway = tags.get("aeroway");
            if ("taxiway".equals(aeroway) || "runway".equals(aeroway)) return Color.decode("#B0B8C1"); // Grey-blue
            return Color.decode("#D1D9E0"); // Light grey-blue
        }

        //AMENITY AND LEISURE
        if (tags.containsKey("amenity") || tags.containsKey("leisure")) {
            if (tags.containsValue("dog_park")
                    || tags.containsValue("golf_course")
                    || tags.containsKey("sport")
                    || tags.containsValue("park")) return Color.decode("#C7D9B8"); //Green
            if (tags.containsValue("hospital")) return Color.decode("#EAD7D7"); //Dusty pink
            return Color.decode("#D9D2C5"); // Beige
        }

        //BUILDING
        if (tags.containsKey("building") || tags.containsKey("building:part")) {
            return Color.decode("#D2B4A4"); // Dusty orange
        }

        //SURFACE
        if (tags.containsKey("surface")) {
            String surface = tags.get("surface");
            if ("grass".equals(surface)) return Color.decode("#D3DFC5"); // Light green
            if ("sand".equals(surface)) return Color.decode("#E5DCC6"); //Sand
            if ("paved".equals(surface) || "asphalt".equals(surface) || "concrete".equals(surface) || "paving_stones".equals(surface)) {
                return Color.decode("#DBD7D2");// Grey
            }
        }

        //OTHER TAGS
        if (tags.containsKey("man_made")) return Color.decode("#BDB9B5");
        if (tags.containsKey("tourism") || tags.containsKey("historic")) return Color.decode("#C9BFA9"); //Muted brown
        if (tags.containsKey("landcover") || tags.containsKey("grassland")) return Color.decode("#C5D9A9"); //Darker green

        //FALLBACK COLOR
        return Color.decode("#F2F0E9"); //Very light brown
    }
}
