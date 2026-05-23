package models.rendering;

import models.osm.Node;
import models.osm.Way;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * Draws Way elements as lines or filled polygons.
 */
public class WayRenderer extends AbstractRenderer<Way> {

        /* Cached strokes: Rebuilt only when zoom changes to a new 0.5 step.
          Creating a new BasicStroke for every road on every frame is wasteful
          since the width only changes with zoom, not per element.
         */
        private BasicStroke cachedRoadStroke = null;
        private BasicStroke cachedFillStroke = new BasicStroke(0);
        private double cachedStrokeZoom = Double.NaN;

        private static final double ZOOM_STEP = 0.5;

    private static final float MIN_ROAD_WIDTH_PX = 2.0f; // minimum screen pixels
    private static final float MAX_ROAD_WIDTH_PX = 10.0f; // maximum screen pixels

    /**
     * @param meanLat mean latitude used for projection scaling
     */
    public WayRenderer( double meanLat){
            super(meanLat);
        }

        /**
         * Returns a road stroke for the current zoom.
         * @return cached road stroke
         */
        private BasicStroke getRoadStroke() {
            /* Round zoom to the nearest 0.5 step (e.g. 11.3 → 11.5, 12.1 → 12.0)
             so we don't rebuild the stroke on every tiny scroll movement
             */
            double quantised = Math.round(currentZoomLevel/ZOOM_STEP) * ZOOM_STEP;
            if (quantised != cachedStrokeZoom || cachedRoadStroke == null) {
                cachedStrokeZoom = quantised;

                /* Roads should always appear 1.5 pixels wide on screen regardless of zoom.
                 But the renderer works in geographic degrees, not pixels, so we need to
                 convert: at zoom z, one degree = 2^z pixels, meaning one pixel = 1/2^z degrees.
                 Multiplying by 1.5 gives us the road width in degrees that equals 1.5 pixels.
                 At zoom 11 (island view): 1.5 / 2048 ≈ 0.00073° = 1.5px on screen
                 At zoom 15 (street view): 1.5 / 32768 ≈ 0.000046° = still 1.5px on screen
                 */
                float strokeWidth = (float) (1.5f / Math.pow(2, quantised));
                float minWidth    = (float) (MIN_ROAD_WIDTH_PX / Math.pow(2, quantised));
                float maxWidth    = (float) (MAX_ROAD_WIDTH_PX / Math.pow(2, quantised));
                cachedRoadStroke = new BasicStroke(strokeWidth);
            }
            return cachedRoadStroke;
        }

    /**
     * Draws a single way using stroke or fill based on closure.
     * @param gc graphics context
     * @param way way to draw
     */
    @Override
    protected void drawElement(Graphics2D gc, Way way) {
        List<Node> nodes = way.getNodes();
        if (nodes == null || nodes.size() < 2) return;

        boolean isClosed = nodes.getFirst().getId() == nodes.getLast().getId();

        Path2D path = way.getShape();
        if (path instanceof models.geometry.AdaptivePath ap) {
            ap.updateForZoom(currentZoomLevel);
        }
        gc.setColor(way.getColor());

        if (!isClosed) {
            gc.setStroke(getRoadStroke());
            gc.draw(path);
        } else {
            gc.setStroke(cachedFillStroke);
            gc.fill(path);
        }
    }
}
