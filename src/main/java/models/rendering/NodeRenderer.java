package models.rendering;

import models.parser.AbstractRenderer;
import models.osm.Node;

import java.awt.*;

public class NodeRenderer extends AbstractRenderer<Node> {
    public NodeRenderer(double meanLat) {
        super(meanLat);
    }

    @Override
    public void draws(Graphics2D gc) {
        // TODO: Implement this function – should draw trees, etc. (read OSM documentation). Might need to remove some nodes from TreeData.nodes which are part of ways
        throw new RuntimeException("NodeRenderer.draws() not implemented");
    }
}
