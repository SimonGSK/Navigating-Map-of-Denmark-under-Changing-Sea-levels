package models.rendering;

import models.osm.Node;

import java.awt.*;

public class NodeRenderer extends AbstractRenderer<Node> {
    public NodeRenderer(double meanLat) {
        super(meanLat);
    }

    @Override
    protected void drawElement(Graphics2D gc, Node node) {
        // TODO: Implement this function – should draw trees, etc. (read OSM documentation). Might need to remove some nodes from TreeData.nodes which are part of ways
        throw new RuntimeException("NodeRenderer.drawElement() not implemented");
    }
}
