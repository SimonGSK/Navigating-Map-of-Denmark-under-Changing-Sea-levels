package models.geometry;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public final class BoundingBox implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final double minLat;
    private final double minLon;
    private final double maxLat;
    private final double maxLon;
    private double area = Double.NaN;

    /**
     * Constructs a rectangular bounding box from minimum and maximum coordinates.
     *
     * @param minLat Minimum latitude border.
     * @param minLon Minimum longitude border.
     * @param maxLat Maximum latitude border.
     * @param maxLon Maximum longitude border.
     */
    public BoundingBox(double minLat, double minLon, double maxLat, double maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
    }

    /**
     * Computes the minimum bounding rectangle (MBR) from the coordinates of the elements from a list.
     *
     * @param elements A list of elements that extends SpatialElement.
     * @return Generated BoundingBox fully covering logical targets accurately.
     */
    static public BoundingBox computeMbr(List<? extends SpatialElement> elements) {
        double minLat = Double.POSITIVE_INFINITY;
        double minLon = Double.POSITIVE_INFINITY;
        double maxLat = Double.NEGATIVE_INFINITY;
        double maxLon = Double.NEGATIVE_INFINITY;

        for (SpatialElement e : elements) {
            minLat = Math.min(minLat, e.getMbr().minLat());
            minLon = Math.min(minLon, e.getMbr().minLon());
            maxLat = Math.max(maxLat, e.getMbr().maxLat());
            maxLon = Math.max(maxLon, e.getMbr().maxLon());
        }
        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    /**
     * Calculates center of the BoundingBox from its minimum and maximum coordinates.
     *
     * @return A coordinate representing the center of the BoundingBox.
     */
    public Coordinate getCenter() {
        return new Coordinate((minLat + maxLat) / 2, (minLon + maxLon) / 2);
    }

    /**
     * Checks if a given BoundingBox is fully inside another one by comparing minimum and maximum coordinates.
     *
     * @param other BoundingBox to be checked if it is inside.
     * @return A boolean describing if the given BoundingBox is inside or not.
     */
    public boolean isInside(BoundingBox other) {
        return this.minLat >= other.minLat && this.minLon >= other.minLon && this.maxLat <= other.maxLat && this.maxLon <= other.maxLon;
    }

    /**
     * Checks if a given BoundingBox is overlapping another one by comparing minimum and maximum coordinates.
     *
     * @param other The BoundingBox that needs to be checked if it is overlapping.
     * @return A boolean describing if the given BoundingBox is overlapping or not.
     */
    public boolean isOverlappingOther(BoundingBox other) {
        boolean separated = this.maxLat < other.minLat || this.maxLon < other.minLon || this.minLat > other.maxLat || this.minLon > other.maxLon;
        return !separated;
    }

    /**
     *
     *
     * @param mbr
     * @return
     */
    public double areaIncreaseNeeded(BoundingBox mbr) {
        if (mbr.isInside(this)) {
            return 0;
        }

        BoundingBox container = getExpanded(mbr);

        return container.area() - this.area();
    }

    /**
     * Gets or calculates the area of the BoundingBox based on its maximum and minimum coordinates.
     *
     * @return The area as a double, or 0 if area is negative
     */
    public double area() {
        if (Double.isNaN(area)) {
            area = Math.max(0, (maxLat - minLat) * (maxLon - minLon));
        }
        return area;
    }

    /**
     * Creates a copy of the BoundingBox with its minimum and maximum coordinates
     *
     * @return The copy as a new BoundingBox object.
     */
    public BoundingBox copy() {
        return new BoundingBox(minLat, minLon, maxLat, maxLon);
    }

    /**
     * Creates a new BoundingBox object by combining the minimum and maximum coordinates of this BoundingBox and another BoundingBox.
     *
     * @param mbr A BoundingBox to be combined with this for the new combined BoundingBox.
     * @return The new combined BoundingBox object.
     */
    public BoundingBox getExpanded(BoundingBox mbr) {
        return new BoundingBox(
                Math.min(this.minLat, mbr.minLat),
                Math.min(this.minLon, mbr.minLon),
                Math.max(this.maxLat, mbr.maxLat),
                Math.max(this.maxLon, mbr.maxLon)
        );
    }

    /**
     * Gets the maximum latitude of the BoundingBox
     *
     * @return The maximum latitude as a double.
     */
    public double minLat() {
        return minLat;
    }

    /**
     * Gets the minimum longitude of the BoundingBox
     *
     * @return The minimum longitude as a double.
     */
    public double minLon() {
        return minLon;
    }

    /**
     * Gets the maximum latitude of the BoundingBox
     *
     * @return The maximum latitude as a double
     */
    public double maxLat() {
        return maxLat;
    }

    /**
     * Gets the maximum longitude of the BoundingBox
     *
     * @return The maximum longitude as a double.
     */
    public double maxLon() {
        return maxLon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BoundingBox) obj;
        return Double.doubleToLongBits(this.minLat) == Double.doubleToLongBits(that.minLat) &&
                Double.doubleToLongBits(this.minLon) == Double.doubleToLongBits(that.minLon) &&
                Double.doubleToLongBits(this.maxLat) == Double.doubleToLongBits(that.maxLat) &&
                Double.doubleToLongBits(this.maxLon) == Double.doubleToLongBits(that.maxLon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minLat, minLon, maxLat, maxLon);
    }

    @Override
    public String toString() {
        return "BoundingBox[" +
                "minLat=" + minLat + ", " +
                "minLon=" + minLon + ", " +
                "maxLat=" + maxLat + ", " +
                "maxLon=" + maxLon + ']';
    }

}
