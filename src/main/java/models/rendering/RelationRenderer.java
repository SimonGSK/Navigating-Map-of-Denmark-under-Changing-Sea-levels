package models.rendering;

import models.geometry.AdaptivePath;
import models.osm.Relation;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;


public class RelationRenderer extends AbstractRenderer<Relation> {
    public RelationRenderer(double meanLat) {
        super(meanLat);
    }

    @Override
    protected void drawElement(Graphics2D gc, Relation relation) {
        //Tilføjet for at undgå at lukkede paths, som f.eks. hiking routes, bliver til huller i multipolygons
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

        gc.setColor(relation.getColor());
        gc.fill(combined);
    }
}
