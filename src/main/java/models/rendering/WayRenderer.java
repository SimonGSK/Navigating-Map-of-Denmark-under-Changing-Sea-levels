package models.rendering;

import models.parser.AbstractRenderer;
import models.osm.Node;
import models.osm.Way;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

public class WayRenderer extends AbstractRenderer<Way> {

    public WayRenderer(double meanLat) {
        super(meanLat);
    }

    @Override
    public void draws(Graphics2D gc) {
        for (Way way : elements) {
            List<Node> nodes = way.getNodes();
            if (nodes == null || nodes.size() < 2) continue;

            boolean isClosed = nodes.getFirst().getId() == nodes.getLast().getId();

            if (!shouldDraw(way)) continue; //Funktion til at afgøre om noget skal tegnes

            Path2D path = way.getShape();
            if (path instanceof models.geometry.AdaptivePath ap) {
                ap.updateForZoom(currentZoomLevel);
            }
            gc.setColor(way.getColor());

            if (!isClosed) {
                float strokeWidth = (float)(1.5/Math.pow(2, currentZoomLevel));
                gc.setStroke(new BasicStroke(strokeWidth));
                gc.draw(path);
            } else {
                gc.setStroke(new BasicStroke(0));
                gc.fill(path);
            }
        }
    }
}
