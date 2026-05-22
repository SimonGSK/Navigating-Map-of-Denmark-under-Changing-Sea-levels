package models.geometry;

import java.io.Serializable;

public class Coordinate implements SpatialElement, Serializable {
    private final double lat;
    private final double lon;

    /**
     * Constructs a geographic coordinate specifying latitude and longitude.
     *
     * @param lat The latitude of the coordinate.
     * @param lon The longitude of the coordinate.
     */
    public Coordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Gets the latitude of this coordinate.
     *
     * @return The latitude.
     */
    public double getLat() {
        return lat;
    }

    /**
     * Gets the longitude of this coordinate.
     *
     * @return The longitude.
     */
    public double getLon() {
        return lon;
    }

    /**
     * Creates a new instance of this coordinate with exactly the same values.
     *
     * @return A cloned Coordinate object.
     */
    public Coordinate copy() {
        return new Coordinate(lat, lon);
    }

    /**
     * Generates a minimum bounding rectangle (MBR) from the latitude and longitude of the coordinate.
     *
     * @return The minimal BoundingBox covering this coordinate perfectly.
     */
    @Override
    public BoundingBox getMbr() {
        return new BoundingBox(lat,lon,lat,lon);
    }

    /**
     * Computes the geometric area, which is effectively 0 for a coordinate point.
     *
     * @return Always 0.
     */
    @Override
    public double getArea() {
        return 0;
    }
}
