package models.rendering;

import models.geometry.AdaptivePath;
import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.parser.HeightCurveData;
import models.ui.AppSettings;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws HeightCurve elements in three different modes depending on app state.
 *
 * In elevation map mode, each curve is drawn as a filled region with a color
 * representing its height. In OSM mode, curves are drawn as contour lines at
 * a zoom-dependent interval. In both modes, submerged curves are overlaid with
 * a semi-transparent water color when sea levels is above zero.
 */
public class HeightCurveRenderer extends AbstractRenderer<HeightCurve> {
    private final HeightCurveData data;
    private double seaLevel;
    private double pixelStep = 2;

    /**
     * @param data height curve dataset
     * @param meanLat mean latitude used for projection scaling
     */
    public HeightCurveRenderer(HeightCurveData data, double meanLat) {
        super(meanLat);
        this.data = data;
    }

    /**
     * Draws height curves based on current map state.
     * @param gc graphics context
     */
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

    /**
     * Returns contour interval for the current zoom level.
     * @return contour interval in meters
     */
    private int getHeightInterval() {
        if (currentZoomLevel < 9)  return 20;
        if (currentZoomLevel < 10) return 15;
        if (currentZoomLevel < 11) return 10;
        if (currentZoomLevel < 12) return 5;
        return 5;
    }

    /**
     * Draws contour lines for visible curves.
     * @param gc graphics context
     */
    private void drawHeightCurveLines(Graphics2D gc) {
        float strokeWidth = (float)(1.0 / Math.pow(2, currentZoomLevel));
        gc.setStroke(new BasicStroke(strokeWidth));
        gc.setColor(Color.gray);

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
                path = new AdaptivePath(points, true, pixelStep);
                e.setAdaptivePath(path);
            }

            path.updateForZoom(currentZoomLevel);
            gc.draw(path);
        }
    }

    /**
     * Sets the sea level used for submersion rendering.
     * @param level sea level
     */
    public void setSeaLevel(double level) {
        this.seaLevel = level;
    }

    /**
     * Draws filled height regions for elevation map mode.
     * @param gc graphics context
     */
    public void drawHeightCurveMap(Graphics2D gc) {
        List<HeightCurve> sorted = new ArrayList<>(data.curves);
        sorted.remove(data.root);
        sorted.sort((a, b) -> Double.compare(b.getArea(), a.getArea()));

        for (HeightCurve curve : sorted) {
            Path2D fill = buildAdaptiveFillPath(curve);
            if (fill == null) continue;
            curve.setSeaLevel(seaLevel);
            gc.setColor(curve.getColor());
            gc.fill(fill);
        }
    }

    /**
     * Draws water overlays for submerged curves.
     * @param gc graphics context
     */
    public void drawSubmergedCurves(Graphics2D gc) {
        if (seaLevel <= 0) return;

        Composite originalComposite = gc.getComposite(); //Saves the original composite

        // Flood the area between the sea and the outermost height curve.
        Path2D coastFill = buildAdaptiveFillPath(data.root);
        if (coastFill != null) {
            gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); // 60% transparent
            gc.setColor(Color.decode("#a9d3de"));
            gc.fill(coastFill);
        }

        List<HeightCurve> sorted = new ArrayList<>(data.curves);
        sorted.remove(data.root);
        sorted.sort((a, b) -> Double.compare(b.getArea(), a.getArea()));

        //Farver alle oversvømmede height curves
        for (HeightCurve curve : sorted) {
            if (!curve.isSubmerged()) continue;

            Path2D fill = buildAdaptiveFillPath(curve);
            if (fill == null) continue;
            curve.setSeaLevel(seaLevel);
            gc.setColor(curve.getColor());
            gc.draw(fill);
            gc.fill(fill);
        }

        gc.setComposite(originalComposite); //Sets original composite again
    }

    //

    /**
     * Mirrors ShapeBuilder.getRegionPath() but with adaptive simplification per ring.
     * Builds a WIND_EVEN_ODD path: outer boundary + direct children as holes,
     * so non-submerged children show the background through correctly.
     * Adaptive paths are cached on each HeightCurve, the EVEN_ODD composite is built
     * fresh each frame but is cheap because the rings are already simplified.
     * @param curve height curve
     * @return composite path or null
     */
    private Path2D buildAdaptiveFillPath(HeightCurve curve) {
        List<Coordinate> coords = curve.getCoords();
        if (coords == null || coords.isEmpty()) return null;

        AdaptivePath outer = curve.getAdaptivePath();
        if (outer == null) {
            List<double[]> points = new ArrayList<>();
            for (Coordinate coord : coords) {
                points.add(new double[]{ coord.getLon() * cosMeanLat, -coord.getLat() });
            }
            outer = new AdaptivePath(points, true, pixelStep);
            curve.setAdaptivePath(outer);
        }
        outer.updateForZoom(currentZoomLevel);

        Path2D composite = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        composite.append(outer, false);

        for (HeightCurve child : curve.getChildren()) {
            List<Coordinate> childCoords = child.getCoords();
            if (childCoords == null || childCoords.isEmpty()) continue;
            AdaptivePath childPath = child.getAdaptivePath();
            if (childPath == null) {
                List<double[]> points = new ArrayList<>();
                for (Coordinate coord : childCoords) {
                    points.add(new double[]{ coord.getLon() * cosMeanLat, -coord.getLat() });
                }
                childPath = new AdaptivePath(points, true, pixelStep);
                child.setAdaptivePath(childPath);
            }
            childPath.updateForZoom(currentZoomLevel);
            composite.append(childPath, false);
        }

        return composite;
    }
}
