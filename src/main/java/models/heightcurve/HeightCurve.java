package models.heightcurve;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import enums.ElementType;
import models.geometry.AdaptivePath;
import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.osm.Element;

/**
 *
 */
public class HeightCurve extends Element implements Serializable {
    private final double height;
    private final List<Coordinate> coords;
    private final List<HeightCurve> children;
    public boolean submerged;
    private transient HeightCurve parent;
    protected Path2D shape = null;
    private transient AdaptivePath adaptivePath = null;
    private double seaLevel = 0;

    /**
     *
     * @param id
     * @param height
     * @param coords
     * @param children
     */
    public HeightCurve(long id, double height, List<Coordinate> coords, List<HeightCurve> children) {
        super(id, ElementType.heightCurve, new BoundingBox(0,0,0,0));
        this.height = height;
        this.coords = coords != null ? coords : new ArrayList<>();
        this.children = children != null ? children : new ArrayList<>();
        this.submerged = false;
        this.parent = null;
    }

    /**
     *
     * @param id
     * @param height
     * @param coords
     */
    public HeightCurve(long id, double height, List<Coordinate> coords) {
        this(id, height, coords, new ArrayList<>());
    }

    /**
     *
     * @param coords
     * @return
     */
    static private BoundingBox computeMbr(List<Coordinate> coords) {
        return BoundingBox.computeMbr(coords);
    }

    /**
     *
     */
    public void updateMbr() {
        this.setMbr(computeMbr(coords));
    }

    /**
     *
     * @return
     */
    public double getHeight() {
        return height;
    }

    /**
     *
     * @return
     */
    public List<Coordinate> getCoords() {
        return coords;
    }

    /**
     *
     * @return
     */
    public List<HeightCurve> getChildren() {
        return children;
    }

    /**
     *
     * @param child
     */
    public void addChild(HeightCurve child) {
        this.children.add(child);
        child.setParent(this);
    }

    /**
     *
     * @param seaLevel
     */
    public void setSeaLevel(double seaLevel) {
        this.seaLevel = seaLevel;
    }

    /**
     *
     * @return
     */
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

    /**
     *
     */
    public void resetSubmerged() {
        this.submerged = false;
        for (HeightCurve child : this.children) {
            child.resetSubmerged();
        }
    }

    /**
     *
     * @param seaLevel
     * @param parentSubmerged
     */
    public void updateSubmersion(double seaLevel, boolean parentSubmerged) {
        // This curve is submerged if parent is submerged AND this curve's height is below sea level
        this.submerged = parentSubmerged && this.height < seaLevel;

        // Recursively update children
        for (HeightCurve child : this.children) {
            child.updateSubmersion(seaLevel, this.submerged);
        }
    }

    /**
     *
     * @return
     */
    public boolean isSubmerged() {
        return submerged;
    }

    /**
     *
     * @return
     */
    public HeightCurve getParent() {
        return parent;
    }

    /**
     *
     * @param parent
     */
    public void setParent(HeightCurve parent) {
        this.parent = parent;
    }

    /**
     *
     * @param seaLevel
     */
    public void submerge(double seaLevel) {
        updateSubmersion(seaLevel, true);
    }

    /**
     *
     * @param shape
     */
    public void setShape(Path2D shape){
        this.shape = shape;
    }

    /**
     *
     * @return
     */
    public Path2D getShape(){
        return shape;
    }

    /**
     *
     * @return
     */
    public AdaptivePath getAdaptivePath() { return adaptivePath; }

    /**
     *
     * @param path
     */
    public void setAdaptivePath(AdaptivePath path) { this.adaptivePath = path; }

    /**
     *
     * @return
     */
    @Override
    public double getArea() {
        return getMbr().area();
    }
}
