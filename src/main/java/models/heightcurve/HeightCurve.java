package models.heightcurve;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import enums.ElementType;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.osm.Element;

public class HeightCurve extends Element implements Serializable {
    private final double height;
    private final List<Coordinate> coords;
    private final List<HeightCurve> children;
    public boolean submerged;
    private transient HeightCurve parent;
    protected Path2D shape = null;
    private double seaLevel = 0;

    public HeightCurve(long id, double height, List<Coordinate> coords, List<HeightCurve> children) {
        super(id, ElementType.heightCurve, computeMbr(coords));
        this.height = height;
        this.coords = coords != null ? coords : new ArrayList<>();
        this.children = children != null ? children : new ArrayList<>();
        this.submerged = false;
        this.parent = null;
    }

    public HeightCurve(long id, double height, List<Coordinate> coords) {
        this(id, height, coords, new ArrayList<>());
    }

    static private BoundingBox computeMbr(List<Coordinate> coords) {
        return BoundingBox.computeMbr(coords);
    }

    public double getHeight() {
        return height;
    }

    public List<Coordinate> getCoords() {
        return coords;
    }

    public List<HeightCurve> getChildren() {
        return children;
    }

    public void addChild(HeightCurve child) {
        this.children.add(child);
        child.setParent(this);
    }

    public void setSeaLevel(double seaLevel) {
        this.seaLevel = seaLevel;
    }

    public Color getColor() {
        double altitude = height - seaLevel;

        if (altitude < 0 && submerged) return Color.decode("#a9d3de");   // water
        if (altitude < 0) return Color.decode("#ffffcc");  // below sea but not submerged
        if (altitude < 10) return Color.decode("#5E7F5A");
        if (altitude < 20) return Color.decode("#7FA878");
        if (altitude < 35) return Color.decode("#A3C18A");
        if (altitude < 55) return Color.decode("#C9D6A3");
        if (altitude < 75) return Color.decode("#E6D39A");
        if (altitude < 95) return Color.decode("#E5B97A");
        if (altitude < 115) return Color.decode("#D9985C");
        if (altitude < 135) return Color.decode("#C5743E");
        return Color.decode("#8F3F2B");
    }

    public void resetSubmerged() {
        this.submerged = false;
        for (HeightCurve child : this.children) {
            child.resetSubmerged();
        }
    }

    public void updateSubmersion(double seaLevel, boolean parentSubmerged) {
        // This curve is submerged if parent is submerged AND this curve's height is below sea level
        this.submerged = parentSubmerged && this.height < seaLevel;

        // Recursively update children
        for (HeightCurve child : this.children) {
            child.updateSubmersion(seaLevel, this.submerged);
        }
    }

    public boolean isSubmerged() {
        return submerged;
    }

    public HeightCurve getParent() {
        return parent;
    }

    public void setParent(HeightCurve parent) {
        this.parent = parent;
    }
    public void submerge(double seaLevel) {
        updateSubmersion(seaLevel, true);
    }

    public void setShape(Path2D shape){
        this.shape = shape;
    }

    public Path2D getShape(){
        return shape;
    }

    @Override
    public void draws(Graphics2D gc) {

    }
}
