package models.osm;

import enums.ElementType;
import models.geometry.BoundingBox;
import models.geometry.SpatialElement;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public abstract class Element implements SpatialElement, Serializable {
    private BoundingBox mbr;
    final private long id;
    final private ElementType type;
    private double minZoomLevel = 0;
    protected Path2D shape = null;

    public Element(long id, ElementType type, BoundingBox mbr) {
        this.id = id;
        this.type = type;
        this.mbr = mbr;
    }

    @Override
    public BoundingBox getMbr() {
        return mbr;
    }

    public void setMbr(BoundingBox mbr) {
        this.mbr = mbr;
    }

    public ElementType getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public double getMinZoomLevel() {
        return minZoomLevel;
    }

    public void setMinZoomLevel(double minZoomLevel) {
        this.minZoomLevel = minZoomLevel;
    }

    public boolean isVisibleOnZoom(double currentZoomLevel) {
        return currentZoomLevel >= minZoomLevel;
    }

    public boolean isVisible(double zoomLevel) {
        return zoomLevel >= minZoomLevel && getColor() != null;
    }

    public abstract Color getColor();

    public void setShape(Path2D shape){
        this.shape = shape;
    }

    public Path2D getShape(){
        return shape;
    }
}
