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

        if (altitude < 0 && submerged) return Color.decode("#2b8cbe");
        if (altitude < 0 && !submerged) return Color.decode("#ffffcc");
        if (altitude < 10) return Color.decode("#edf8b1");
        if (altitude < 20) return Color.decode("#c2e699");
        if (altitude < 30) return Color.decode("#99d8c9");
        if (altitude < 40) return Color.decode("#78c679");
        if (altitude < 60) return Color.decode("#41ab5d");
        if (altitude < 80) return Color.decode("#238443");
        if (altitude < 100) return Color.decode("#006837");
        if (altitude < 130) return Color.decode("#78503a");
        return Color.decode("#5c3d2a");



        /*
        Color c = new Color(227, 28, 197, 128);

        double altitude = height - seaLevel;
        if(altitude < 0 && submerged){
            c = Color.decode("#2b8cbe");
        } else if (altitude < 0 && !submerged){
            c = Color.decode("#ffffcc");
        } else if (altitude >= 0 && altitude < 2.5){
            c = Color.decode("#c2e699");
        } else if (altitude >= 2.5 && altitude < 5){
            c = Color.decode("#78c679");
        } else if (altitude >= 5 && altitude < 7.5){
            c = Color.decode("#31a354");
        }  else if (altitude >= 7.5){
            c = Color.decode("#006837");
        }

        return c;

         */
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

    public boolean isSubmerged() {
        return submerged;
    }
}
