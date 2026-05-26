package models.parser;

import java.util.*;

/**
 * Figures out at what zoom level each map element should start appearing.
 *
 * The minimum zoom for each element depends on two things:
 *    1. What type is it? A motorway shows earlier than a footpath.
 *    2. How big is it? A large forest shows earlier than a tiny one.
 */
public class LodAssigner {

    /**
     * An element must cover at least this many screen pixels before it gets drawn.
     * 4096 = 64x64 pixels
     * Some feature types use a lower threshold, see pixelThreshold().
     */
    private static final double PIXEL_THRESHOLD = 4096.0;

    /**
     * Returns the minimum zoom level for this element.
     * @param tags feature tags
     * @param geoArea area in square degrees
     * @param cosMeanLat projection correction
     * @return minimum zoom level
     */
    public static double compute(HashMap<String, String> tags, double geoArea, double cosMeanLat) {
        if (tags != null && (tags.containsKey("building") || tags.containsKey("building:part"))) {
            return 16.0; // Buildings are tiny, skip the size formula, use a fixed zoom instead.
        }
        double tagZoom = (tags != null) ? tagBasedMinZoom(tags): 0.0;
        double threshold = pixelThreshold(tags);
        double areaZoom = areaBasedMinZoom(geoArea, cosMeanLat, threshold);
        return Math.max(tagZoom, areaZoom);
    }
    /**
     * Returns a pixel threshold based on feature type.
     * @param tags feature tags
     * @return pixel threshold
     */
    private static double pixelThreshold(HashMap<String, String> tags) {
        if (tags == null) return PIXEL_THRESHOLD;
        String natural = tags.get("natural");
        if ("water".equals(natural) || "wetland".equals(natural)) return 512.0;
        String waterway = tags.get("waterway");
        if (waterway != null) return 512.0;
        if (tags.containsKey("landuse")) return 384.0;
        if (tags.containsKey("amenity") || tags.containsKey("leisure")) return 256.0;

        return PIXEL_THRESHOLD;
    }

    /**
     * Calculates at what zoom level this element becomes big enough to be worth drawing.
     *
     * The bigger the element, the earlier it shows up. A large forest might appear
     * at zoom 5, a tiny pond only at zoom 12. Roads have no area so this always
     * returns 0 for them, only their road type decides when they appear.
     * @param geoArea area in square degrees
     * @param cosMeanLat projection correction
     * @param threshold pixel threshold
     * @return minimum zoom level
     */
    private static double areaBasedMinZoom(double geoArea, double cosMeanLat, double threshold) {
        if (geoArea <= 0 || cosMeanLat <= 0) return 0.0;
        return Math.log(threshold/ (geoArea * cosMeanLat)) / Math.log(4.0);
    }

    /** Returns the minimum zoom based on feature type only.
     * @param tags feature tags
     * @return minimum zoom level
     */
    private static double tagBasedMinZoom(HashMap<String, String> tags) {
        if (tags == null) return 0.0;

        String highway = tags.get("highway");
        if (highway != null) {
            return switch (highway) {
                case "motorway", "motorway_link" -> 7.0;
                case "trunk", "trunk_link" -> 8.0;
                case "primary", "primary_link" -> 9.0;
                case "secondary", "secondary_link" -> 10.0;
                case "tertiary", "tertiary_link" -> 10.5;
                case "residential", "unclassified", "living_street" -> 13.5;
                case "service", "track" -> 15.0;
                case "path", "footway", "cycleway", "steps", "bridleway" -> 14.5;
                default -> 13.0;
            };
        }
        if (tags.containsKey("building") || tags.containsKey("building:part")) return 14.0;

        String natural = tags.get("natural");
        if (natural != null) {
            return switch (natural) {
                case "wood", "forest", "water", "wetland", "coastline" -> 0.0;
                case "beach", "shoal" -> 7.0;
                case "scrub", "heath", "grassland" -> 11.0;
                case "rock", "stone", "cliff", "scree" -> 13.0;
                default -> 10.0;
            };
        }
        String waterway = tags.get("waterway");
        if (waterway != null) {
            return switch (waterway) {
                case "river", "canal" -> 10.0;
                case "stream" -> 13.0;
                default -> 14.0;
            };
        }
        String landuse = tags.get("landuse");
        if (landuse != null) {
            return switch (landuse) {
                case "forest", "wood" -> 0.0;
                case "residential", "commercial",
                     "retail", "industrial" -> 8.0;
                case "farmland", "farmyard" -> 11.0;
                case "grass", "meadow" -> 12.0;
                default -> 10.0;
            };
        }
        if (tags.containsKey("aeroway")) return 8.0;
        if (tags.containsKey("amenity") || tags.containsKey("leisure")) return 11.0;
        if (tags.containsKey("man_made"))  return 13.0;
        if (tags.containsKey("tourism") || tags.containsKey("historic")) return 13.0;
        if (tags.containsKey("barrier"))  return 14.0;

        return 0.0;
    }
}
