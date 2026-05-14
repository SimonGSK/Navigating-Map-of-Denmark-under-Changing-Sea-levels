package models.rendering;

import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.parser.AbstractRenderer;
import models.parser.HeightCurveData;
import models.ui.AppSettings;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class HeightCurveRenderer extends AbstractRenderer<HeightCurve> { // TODO: Should extend AbstractRenderer and have HeightCurveData as the type
    private final HeightCurveData data;
    private double seaLevel;

    public HeightCurveRenderer(HeightCurveData data, double meanLat) {
        super(meanLat);
        this.data = data;
    }

    //Bruges til at tegne height curves
    @Override
    public void draws(Graphics2D gc) {
        if (appSettings.getMapState() == AppSettings.MapState.elevation) {
            drawHeightCurveMap(gc);
            return;
        }
        if (appSettings.getIsHeightCurvesVisible()) {
            drawHeightCurveLines(gc);
        }
        drawSubmergedCurves(gc);
    }

    private void drawHeightCurveLines(Graphics2D gc) {
        for (HeightCurve e : elements) {
            Path2D path = e.getShape();
            gc.setColor(Color.darkGray);
            gc.draw(path);
        }
    }

    public void setSeaLevel(double level) {
        this.seaLevel = level;
    }

    //Bruges kun til OSM-kortet så hver height curve fyldes helt og ikke tager højde for children
    //Ser bedre ud
    public void drawHeightCurveMap(Graphics2D gc) {
        List<HeightCurve> sorted = new ArrayList<>(data.curves);
        sorted.remove(data.root);
        sorted.sort((a, b) -> Double.compare(b.getArea(), a.getArea()));

        for (HeightCurve curve: sorted) {
            Path2D path = new Path2D.Double();
            boolean first = true;

            for (Coordinate coord : curve.getCoords()) {
                double x = coord.getLon() * cosMeanLat;
                double y = -coord.getLat();

                if (first) {
                    path.moveTo(x, y);
                    first = false;
                } else path.lineTo(x, y);
            }
            path.closePath();
            curve.setSeaLevel(seaLevel);
            gc.setColor(curve.getColor());
            gc.fill(path);
        }
    }

    //Bruges til at farve oversvømmede height curves på OSM-kortet
    public void drawSubmergedCurves(Graphics2D gc) {
        if (seaLevel <= 0) return;

        Composite originalComposite = gc.getComposite(); //Saves the original composite

        //Farver området mellem havet og yderste height curve
        Path2D coastArea = data.root.getShape();
        gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); // 60% uigennemsigtig (værdier mellem 0.0 og 1.0)
        gc.setColor(Color.decode("#a9d3de"));
        gc.fill(coastArea);

        List<HeightCurve> sorted = new ArrayList<>(data.curves);
        sorted.remove(data.root);
        sorted.sort((a, b) -> Double.compare(b.getArea(), a.getArea()));

        //Farver alle oversvømmede height curves
        for (HeightCurve curve : sorted) {
            if (!curve.isSubmerged()) continue;

            Path2D path = curve.getShape();
            curve.setSeaLevel(seaLevel);
            gc.setColor(curve.getColor());
            if (path == null){
                throw new Error("Path is null");
            }
            gc.draw(path);
            gc.fill(path);
        }

        gc.setComposite(originalComposite); //Sets original composite again
    }
}
