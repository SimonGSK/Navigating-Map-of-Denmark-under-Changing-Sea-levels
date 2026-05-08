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

    private Path2D buildPath(List<Node> nodes, boolean closePath, long id) {
        if (shapes.get(id) != null){
            return shapes.get(id);
        }

        Path2D path = new Path2D.Double();

        boolean isFirst = true;
        for (Node node : nodes) {
            if (node == null) continue;
            double x = node.getLon() * cosMeanLat;
            double y = -node.getCoordinate().getLat();
            if (isFirst) {
                path.moveTo(x, y);
                isFirst = false;
            } else {
                path.lineTo(x, y);
            }
        }
        if (closePath) path.closePath();

        shapes.put(id, path);

        return path;
    }

    @Override
    public void draws(Graphics2D gc) {
        for (Way way : elements) {
            if (way.getId() == 1499690249 || way.getId() == 4120948){ //Strand og coastline på Tunø
                System.out.println();
            }
            List<Node> nodes = way.getNodes();
            if (nodes == null || nodes.size() < 2) continue;

            boolean isClosed = nodes.getFirst().getId() == nodes.getLast().getId();
            if (!shouldDraw(way, isClosed)) continue; //Funktion til at afgøre om noget skal tegnes

            Path2D path = buildPath(nodes, isClosed, way.getId());
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
