package models.osm;

import Interfaces.Drawable;
import enums.ElementType;
import models.geometry.BoundingBox;
import models.geometry.SpatialElement;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public abstract class Element implements SpatialElement, Drawable, Serializable {
    private BoundingBox mbr;
    private double area;
    final private long id;
    final private ElementType type;
    private double minZoomLevel = 0;
    protected Path2D shape = null;

    public Element(long id, ElementType type, BoundingBox mbr) {
        this.id = id;
        this.type = type;
        this.mbr = mbr;
        this.area = mbr.area();
    }

    public Element(long id, ElementType type, BoundingBox mbr, double area) {
        this.id = id;
        this.type = type;
        this.mbr = mbr;
        this.area = area;
    }

    @Override
    public BoundingBox getMbr() {
        return mbr;
    }

    public void setMbr(BoundingBox mbr) {
        this.mbr = mbr;
    }

    @Override
    public double getArea() {
        return area;
    }

    protected void setArea(double area) {
        this.area = area;
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
