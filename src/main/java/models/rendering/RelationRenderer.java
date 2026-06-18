package models.rendering;

import models.geometry.AdaptivePath;
import models.osm.Relation;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * Draws Relation elements onto the map.
 *
 * Only multipolygon and boundary relation types are drawn. Each relations rings are combined
 * into a single EVEN_ODD path so that inner rings automatically become holes in the outer fill.
 */
public class RelationRenderer extends AbstractRenderer<Relation> {
    /**
     * @param meanLat mean latitude used for projection scaling
     */
    public RelationRenderer(double meanLat) {
        super(meanLat);
    }

    /**
     * Draws a single relation if it is a supported area type.
     * @param gc graphics context
     * @param relation relation to draw
     */
    @Override
    protected void drawElement(Graphics2D gc, Relation relation) {
        // Skip non-area types such as hiking routes, which are closed paths
        // that would otherwise accidentally punch holes into multipolygons.
        String relationType = relation.getTag("type");
        if (relationType == null) return;
        if (!"multipolygon".equals(relationType) && !"boundary".equals(relationType)) return;

        List<AdaptivePath> rings = relation.getRingShapes();
        if (rings == null || rings.isEmpty()) return;

        // Update each ring for the current zoom, then combine into one even-odd path
        Path2D combined = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        for (AdaptivePath ring : rings) {
            ring.updateForZoom(currentZoomLevel);
            combined.append(ring, false);  // false = don't connect subpaths
        }

        Color color = relation.getColor();
        if (color == null) return;
        gc.setColor(color);
        gc.fill(combined);
    }
}
