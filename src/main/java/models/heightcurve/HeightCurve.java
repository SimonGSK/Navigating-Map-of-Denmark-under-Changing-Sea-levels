package models.heightcurve;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import models.geometry.Coordinate;

public class HeightCurve {

    long id;
    double height;
    List<Coordinate> coords;
    List<HeightCurve> children;
    boolean submerged;

    public Path2D getBoundaryPath() {

        Path2D.Double p = new Path2D.Double();

        Coordinate coordinate1 = coords.getFirst();
        double x1 = coordinate1.getLon();
        double y1 = coordinate1.getLat();
        p.moveTo(x1, y1);

        for (int i = 1; i < coords.size(); i++) {
            Coordinate coordinate = coords.get(i);
            double x = coordinate.getLon();
            double y = coordinate.getLat();
            p.lineTo(x, y);
        }

        return p;
    }

    public Path2D getRegionPath() {
        Path2D.Double p = new Path2D.Double(Path2D.WIND_EVEN_ODD);

        p.append(getBoundaryPath(), false);
        for(HeightCurve child : children){
            p.append(child.getBoundaryPath(), false);
        }
        return p;
    }

    public Color getFillColor(double seaLevel) {
        double altitude = height - seaLevel;

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

    }

    public void resetSubmerged() {
        this.submerged = false;
        for (HeightCurve child : this.children) {
            child.resetSubmerged();
        }
    }

    public void submerge(double seaLevel) {
        if (this.height < seaLevel){
            this.submerged = true;

            for (HeightCurve child : this.children) {
                child.submerge(seaLevel);
            }
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
    }

    public boolean isSubmerged() {
        return submerged;
    }
}
