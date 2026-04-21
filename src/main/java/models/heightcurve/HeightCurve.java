package models.heightcurve;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import models.geometry.Coordinate;

public class HeightCurve {

    private long id;
    private double height;
    private List<Coordinate> coords;
    private List<HeightCurve> children;
    public boolean submerged;
    private HeightCurve parent;

    public Path2D getBoundaryPath(double cosMeanLat) {
        Path2D.Double p = new Path2D.Double();

        Coordinate coordinate1 = coords.getFirst();
        double x1 = coordinate1.getLon() * cosMeanLat;
        double y1 = -coordinate1.getLat();
        p.moveTo(x1, y1);

        for (int i = 1; i < coords.size(); i++) {
            Coordinate coordinate = coords.get(i);
            double x = coordinate.getLon() * cosMeanLat;
            double y = -coordinate.getLat();
            p.lineTo(x, y);
        }

        p.closePath();
        return p;
    }

    public Path2D getRegionPath(double cosMeanLat) {
        Path2D.Double p = new Path2D.Double(Path2D.WIND_EVEN_ODD);

        p.append(getBoundaryPath(cosMeanLat), false);

        for(HeightCurve child : children){
            p.append(child.getBoundaryPath(cosMeanLat), false);
        }
        return p;
    }

    public Color getFillColor(double seaLevel) {
        double altitude = height - seaLevel;
        /*
        if (altitude < 0 && submerged) return Color.decode("#2b8cbe");   // water
        if (altitude < 0 && !submerged) return Color.decode("#ffffcc");  // below sea but not submerged
        if (altitude < 10) return Color.decode("#edf8b1");  // very light yellow-green
        if (altitude < 20) return Color.decode("#c2e699");  // light green
        if (altitude < 30) return Color.decode("#99d8c9");  // light teal
        if (altitude < 40) return Color.decode("#78c679");  // medium green
        if (altitude < 60) return Color.decode("#41ab5d");  // green
        if (altitude < 80) return Color.decode("#238443");  // darker green
        if (altitude < 100) return Color.decode("#006837"); // dark green
        if (altitude < 130) return Color.decode("#78503a"); // brown
        return Color.decode("#5c3d2a"); // dark brown (160m+)
         */

        //Hvad siger i til disse farver i stedet for?
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

    /**
     * Marks this curve and its children as submerged based on the parent-child structure.
     * A curve is submerged only if its parent is submerged AND its height is below sea level.
     * 
     * @param seaLevel the current sea level
     * @param parentSubmerged whether the parent curve is submerged
     */
    public void updateSubmersion(double seaLevel, boolean parentSubmerged) {
        // This curve is submerged if parent is submerged AND this curve's height is below sea level
        this.submerged = parentSubmerged && this.height < seaLevel;
        
        // Recursively update children
        for (HeightCurve child : this.children) {
            child.updateSubmersion(seaLevel, this.submerged);
        }
    }

    public HeightCurve(long id, double height, List<Coordinate> coords) {
        this(id, height, coords, new ArrayList<>());
    }

    public HeightCurve(long id, double height, List<Coordinate> coords, List<HeightCurve> children) {
        this.id = id;
        this.height = height;
        this.coords = coords != null ? coords : new ArrayList<>();
        this.children = children != null ? children : new ArrayList<>();
        this.submerged = false;
        this.parent = null;
    }

    public long getId() {
        return id;
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

    public boolean isSubmerged() {
        return submerged;
    }

    public HeightCurve getParent() {
        return parent;
    }

    public void setParent(HeightCurve parent) {
        this.parent = parent;
    }
}
