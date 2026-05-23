package models.geometry;

import java.io.Serializable;

public interface SpatialElement extends Serializable {
    /**
     * Gets the Minimum Bounding Rectangle enclosing this spatial element.
     *
     * @return The BoundingBox representing the spatial area covered by this element.
     */
    public BoundingBox getMbr();

    /**
     * Calculates or retrieves the logical geographic area covered by this spatial element.
     *
     * @return The computed area.
     */
    public double getArea();
}
