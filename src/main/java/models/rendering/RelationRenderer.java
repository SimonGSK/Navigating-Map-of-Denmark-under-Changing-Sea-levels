package models.rendering;

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
            if (!shouldDraw(relation, true)) continue; //Funktion til at afgøre om noget skal tegnes

            //Tilføjet for at undgå at lukkede paths, som f.eks. hiking routes, bliver til huller i multipolygons
            String relationType = relation.getTag("type");
            if (relationType == null) continue;
            if (!"multipolygon".equals(relationType) && !"boundary".equals(relationType)) {
                continue;
            }

            if (relation.getShape() == null) continue;

            Path2D path = relation.getShape();

            gc.setColor(relation.getColor());
            gc.fill(path);
        }
    }
}
