package models.osm;

import enums.ElementType;
import models.geometry.BoundingBox;
import models.geometry.SpatialElement;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.Serializable;


public abstract class Element implements SpatialElement, Serializable {
    private BoundingBox mbr;
    final private long id;
    final private ElementType type;
    private double minZoomLevel = 0;
    protected Path2D shape = null;

    /**
     * Constructs an Element with a specific ID, ElementType, and minimum bounding rectangle (MBR).
     *
     * @param id The unique identifier of the element.
     * @param type The type of the element (e.g. Node, Way, Relation).
     * @param mbr The minimum bounding rectangle of the element.
     */
    public Element(long id, ElementType type, BoundingBox mbr) {
        this.id = id;
        this.type = type;
        this.mbr = mbr;
    }

    /**
     * Returns the minimum bounding rectangle enclosing this element.
     *
     * @return The spatial boundaries of the element.
     */
    @Override
    public BoundingBox getMbr() {
        return mbr;
    }

    /**
     * Sets the minimum bounding rectangle for this element.
     *
     * @param mbr The boundaries to be set.
     */
    public void setMbr(BoundingBox mbr) {
        this.mbr = mbr;
    }

    /**
     * Gets the type of the element.
     *
     * @return The ElementType representing the type of the element.
     */
    public ElementType getType() {
        return type;
    }

    /**
     * Gets the ID of the element.
     *
     * @return The element's ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the minimum zoom level at which this element is displayed.
     *
     * @return The zoom level threshold.
     */
    public double getMinZoomLevel() {
        return minZoomLevel;
    }

    /**
     * Sets the minimum zoom level required for displaying this element.
     *
     * @param minZoomLevel The zoom level threshold.
     */
    public void setMinZoomLevel(double minZoomLevel) {
        this.minZoomLevel = minZoomLevel;
    }

    /**
     * Checks whether the element should be visible at a specified zoom level.
     * Only considers the zoom level threshold.
     *
     * @param currentZoomLevel The zoom level applied to the map view.
     * @return {@code true} if the element scales to given zoom, otherwise {@code false}.
     */
    public boolean isVisibleOnZoom(double currentZoomLevel) {
        return currentZoomLevel >= minZoomLevel;
    }

    /**
     * Checks whether the element is visible at a given zoom level having an assigned color.
     *
     * @param zoomLevel The zoom level applying to the map view.
     * @return {@code true} if visible and has set color.
     */
    public boolean isVisible(double zoomLevel) {
        return zoomLevel >= minZoomLevel && getColor() != null;
    }

    /**
     * Gets the designated drawing color of the element.
     *
     * @return The Color object used to render this element.
     */
    public abstract Color getColor();

    /**
     * Assigns a 2D shape configuration to the element for rendering purposes.
     *
     * @param shape A constructed 2D path object.
     */
    public void setShape(Path2D shape){
        this.shape = shape;
    }

    /**
     * Represents the geometric properties of the element.
     *
     * @return A Path2D representing this element's drawn shape.
     */
    public Path2D getShape(){
        return shape;
    }
}
