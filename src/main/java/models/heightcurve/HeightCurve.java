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
 * Height contour with children, parent and cached drawing data.
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
     * Creates a new height curve with an ID, its children, coordinates and a parent.
     * @param id OSM id
     * @param height elevation in meters
     * @param coords curve coordinates
     * @param children nested curves inside this curve
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
     * Creates a height curve without children.
     * @param id OSM id
     * @param height elevation in meters
     * @param coords curve coordinates
     */
    public HeightCurve(long id, double height, List<Coordinate> coords) {
        this(id, height, coords, new ArrayList<>());
    }

    /**
     * Computes the minimum bounding rectangle (MBR) for the coordinates.
     */
    static private BoundingBox computeMbr(List<Coordinate> coords) {
        return BoundingBox.computeMbr(coords);
    }

    /**
     * Updates the cached bounding box from current coordinates.
     */
    public void updateMbr() {
        this.setMbr(computeMbr(coords));
    }

    /**
     * @return elevation of the height curve in meters
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return curve coordinates
     */
    public List<Coordinate> getCoords() {
        return coords;
    }

    /**
     * @return child curves nested inside this curve
     */
    public List<HeightCurve> getChildren() {
        return children;
    }

    /**
     * Adds a child curve and sets its parent.
     * @param child The curve to be set as child of this curve
     */
    public void addChild(HeightCurve child) {
        this.children.add(child);
        child.setParent(this);
    }

    /**
     * Sets the sea level used for color calculation.
     * @param seaLevel The sea level to be set.
     */
    public void setSeaLevel(double seaLevel) {
        this.seaLevel = seaLevel;
    }

    /**
     * @return fill color based on height and submersion
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
     * Clears submerged flags for this curve and its children.
     */
    public void resetSubmerged() {
        this.submerged = false;
        for (HeightCurve child : this.children) {
            child.resetSubmerged();
        }
    }

    /**
     * Updates submerged state based on sea level and parent state.
     */
    public void updateSubmersion(double seaLevel, boolean parentSubmerged) {
        // This curve is submerged if parent is submerged AND this curve's height is below sea level
        this.submerged = parentSubmerged && this.height < seaLevel;

        //Early return if this curve is not submerged, since none of its descendants can then be
        if (!this.submerged) return; 

        // Recursively update children
        for (HeightCurve child : this.children) {
            child.updateSubmersion(seaLevel, this.submerged);
        }
    }

    /**
     * @return true if this curve is submerged
     */
    public boolean isSubmerged() {
        return submerged;
    }

    /**
     * @return parent curve or null
     */
    public HeightCurve getParent() {
        return parent;
    }

    /**
     * Sets the parent curve reference.
     */
    public void setParent(HeightCurve parent) {
        this.parent = parent;
    }

    /**
     * Marks this curve and its children as submerged below sea level.
     */
    public void submerge(double seaLevel) {
        updateSubmersion(seaLevel, true);
    }

    /**
     * Sets the cached shape used for drawing.
     */
    public void setShape(Path2D shape){
        this.shape = shape;
    }

    /**
     * @return cached shape or null
     */
    public Path2D getShape(){
        return shape;
    }

    /**
     * @return cached adaptive path or null
     */
    public AdaptivePath getAdaptivePath() { return adaptivePath; }

    /**
     * Sets the cached adaptive path.
     */
    public void setAdaptivePath(AdaptivePath path) { this.adaptivePath = path; }

    /**
     * @return area based on bounding box
     */
    @Override
    public double getArea() {
        return getMbr().area();
    }
}
