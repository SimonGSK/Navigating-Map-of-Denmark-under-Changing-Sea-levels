package models.utils;

import models.geometry.Coordinate;
import models.osm.Node;

/**
 * Small geometry-related utility methods.
 */
public class UtilityTools {
    /**
     * Haversine distance between two coordinates in meters.
     */
    public static double haversineDistance(Coordinate a, Coordinate b) {
        double earthRadius = 6371000; // Earth radius in metres
        double aLatRadians = Math.toRadians(a.getLat());
        double bLatRadians = Math.toRadians(b.getLat());
        double deltaLat = Math.toRadians(b.getLat() - a.getLat());
        double deltaLon = Math.toRadians(b.getLon() - a.getLon());

        double haversineValue = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2),2)
                * Math.cos(aLatRadians) * Math.cos(bLatRadians);

        return earthRadius * 2 * Math.atan2(Math.sqrt(haversineValue), Math.sqrt(1 - haversineValue));
    }

    /**
     * Euclidean distance between two coordinates in degrees.
     */
    public static double euclideanDistance(Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(b.getLat() - a.getLat(),2) + Math.pow(b.getLon() - a.getLon(),2));
    }
}
