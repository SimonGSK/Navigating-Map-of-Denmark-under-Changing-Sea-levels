package models.rendering;

import models.parser.AbstractRenderer;
import models.osm.Node;
import models.osm.Way;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

public class WayRenderer extends AbstractRenderer<Way> {
    private static final double MAX_WAY_SPAN = 0.7; // TODO: Remove unused variables
    private static final double MAX_SEGMENT_LENGTH = 0.25; // TODO: Remove unused variables

    public WayRenderer(double meanLat) {
        super(meanLat);
    }

    private Path2D buildPath(List<Node> nodes, boolean closePath) {

        Path2D path = new Path2D.Double();

        boolean first = true; // TODO: Use "is"-naming convention for boolean flags: first -> isFirst
        for (Node node : nodes) {
            if (node == null) continue;
            double x = node.getLon() * cosMeanLat;
            double y = -node.getCoordinate().getLat();
            if (first) {
                path.moveTo(x, y);
                first = false;
            } else {
                path.lineTo(x, y);
            }
        }
        if (closePath) path.closePath();

        return path;
    }

    @Override
    public void draws(Graphics2D gc) {
        for (Way way : elements) {
            List<Node> nodes = way.getNodes();
            if (nodes == null || nodes.size() < 2) continue;

            boolean isClosed = nodes.getFirst().getId() == nodes.getLast().getId();
            if (!shouldDraw(way, isClosed)) continue; //Funktion til at afgøre om noget skal tegnes

            Path2D path = buildPath(nodes, isClosed);
            gc.setColor(way.getColor());

            if (!isClosed) {
                gc.setStroke(new BasicStroke(0.0001f));
                gc.draw(path);
            } else {
                gc.setStroke(new BasicStroke(0));
                gc.fill(path);
            }
        }
    }
}
