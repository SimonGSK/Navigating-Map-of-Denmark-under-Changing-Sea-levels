package models.rendering;

import Interfaces.Drawable;
import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.heightcurve.HeightCurveData;
import java.util.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class HeightCurveRenderer implements Drawable {
    private final HeightCurveData data;
    private final double cosMeanLat;
    private double seaLevel;
    private boolean toFill;

    public HeightCurveRenderer(HeightCurveData data, double meanLat) {
        this.data = data;
        this.cosMeanLat = Math.cos(Math.toRadians(meanLat));
        toFill = true;
    }

    @Override
    public void draws(Graphics2D gc) {

        List<HeightCurve> sorted = new ArrayList<>(data.curves);
       sorted.remove(data.sea);

       sorted.sort((a, b) -> Double.compare(boundingArea(b), boundingArea(a)));

       for (HeightCurve curve: sorted) {
           Path2D path = curve.getRegionPath(cosMeanLat);

           if (toFill || curve.isSubmerged()) {
               gc.setColor(curve.getFillColor(seaLevel));
               gc.fill(path);
           } else {
               gc.setColor(Color.black);
               gc.draw(path);
           }
       }
       this.toFill = true;
    }

    public void drawHcLines(Graphics2D gc){
        this.toFill = false;
        draws(gc);
    }

    private double boundingArea(HeightCurve hc) {
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
        for (Coordinate coord: hc.getCoords()) {
            if (coord.getLat() < minLat) minLat = coord.getLat();
            if (coord.getLat() > maxLat) maxLat = coord.getLat();
            if (coord.getLon() < minLon) minLon = coord.getLon();
            if (coord.getLon() > maxLon) maxLon = coord.getLon();
        }
        return (maxLat - minLat) * (maxLon - minLon);
    }
    public void setSeaLevel(double level) {
        this.seaLevel = level;
    }

    //Bruges kun til OSM-kortet så hver height curve fyldes helt og ikke tager højde for children
    //Ser bedre ud
    public void drawHcMap(Graphics2D gc) {
        List<HeightCurve> sorted = new ArrayList<>(data.curves);
        sorted.remove(data.sea);
        sorted.sort((a, b) -> Double.compare(boundingArea(b), boundingArea(a)));

        for (HeightCurve curve: sorted) {
            Path2D path = new Path2D.Double();
            boolean first = true;
            for (Coordinate coord: curve.getCoords()) {
                double x = coord.getLon() * cosMeanLat;
                double y = -coord.getLat();
                if (first) {
                    path.moveTo(x, y);
                    first = false;
                } else path.lineTo(x, y);
            }
            path.closePath();
            if (toFill){
                gc.setColor(curve.getFillColor(seaLevel));
                gc.fill(path);
            } else{
                gc.setColor(Color.black);
                gc.draw(path);
            }
        }
        this.toFill = true;
    }

    public void drawSubmersedCurves(Graphics2D gc) {
        List<HeightCurve> sorted = new ArrayList<>(data.curves);
        //sorted.remove(data.sea);
        sorted.sort((a, b) -> Double.compare(boundingArea(b), boundingArea(a)));

        gc.setColor(Color.decode("#a9d3de"));

        for (HeightCurve curve : sorted) {
            if (!curve.isSubmerged()) {
                continue;
            }

            Path2D path = curve.getRegionPath(cosMeanLat);
            gc.fill(path);
        }
    }
}
