package models.rendering;

import models.geometry.AdaptivePath;
import models.parser.AbstractRenderer;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.utils.UtilityTools;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;


public class RelationRenderer extends AbstractRenderer<Relation> {
    public RelationRenderer(double meanLat) {
        super(meanLat);
    }

    // Tegner alle multipolygon-relations som fyldte områder på kortet.
    // Relations tegnes i den rækkefølge de ligger i listen – det forventes at listen allerede er sorteret fra størst til mindst areal.
    @Override
    public void draws(Graphics2D gc) {
        for (Relation relation : elements) {
            if (!shouldDraw(relation)) continue; //Funktion til at afgøre om noget skal tegnes

            //Tilføjet for at undgå at lukkede paths, som f.eks. hiking routes, bliver til huller i multipolygons
            String relationType = relation.getTag("type");
            if (relationType == null) continue;
            if (!"multipolygon".equals(relationType) && !"boundary".equals(relationType)) {
                continue;
            }

            List<AdaptivePath> rings = relation.getRingShapes();
            if (rings == null || rings.isEmpty()) continue;

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
}
