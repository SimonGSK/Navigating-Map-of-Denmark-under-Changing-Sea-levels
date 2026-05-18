package models.rendering;

import models.geometry.AdaptivePath;
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

    private int getHeightInterval() {
        if (currentZoomLevel < 9)  return 25;
        if (currentZoomLevel < 10) return 20;
        if (currentZoomLevel < 11) return 15;
        if (currentZoomLevel < 12) return 10;
        return 5;
    }

    private void drawHeightCurveLines(Graphics2D gc) {
        float strokeWidth = (float)(1.0 / Math.pow(2, currentZoomLevel));
        gc.setStroke(new BasicStroke(strokeWidth));
        gc.setColor(Color.darkGray);

        int interval = getHeightInterval();

        for (HeightCurve e : elements) {
            if (Math.round(e.getHeight()) % interval != 0) continue;

            AdaptivePath path = e.getAdaptivePath();
            if (path == null) {
                List<Coordinate> coords = e.getCoords();
                if (coords == null || coords.isEmpty()) continue;

                /**
                 * Build the AdaptivePath from projected coordinates on first draw and cache it.
                 * Subsequent frames reuse the cached path; updateForZoom() only rebuilds
                 * the path geometry when zoom crosses a full step, returning immediately otherwise.
                 */
                List<double[]> points = new ArrayList<>();
                for (Coordinate coord : coords) {
                    points.add(new double[]{ coord.getLon() * cosMeanLat, -coord.getLat() });
                }
                path = new AdaptivePath(points, true);
                e.setAdaptivePath(path);
            }

            path.updateForZoom(currentZoomLevel);
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

        for (HeightCurve curve : sorted) {
            /*
            Build the path once and cache it on the curve.
            Previously this rebuilt the path from raw coordinates every frame.
            This is wasteful since the shape never changes, only the color does (sea level).
             */
            Path2D path = curve.getShape();
            if (path == null) {
                path = new Path2D.Double();
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
                curve.setShape(path);
            }
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
